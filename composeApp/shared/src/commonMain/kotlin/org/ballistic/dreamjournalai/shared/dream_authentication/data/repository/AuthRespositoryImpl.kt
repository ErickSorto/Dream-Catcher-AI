package org.ballistic.dreamjournalai.shared.dream_authentication.data.repository

import co.touchlab.kermit.Logger
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FieldValue.Companion.serverTimestamp
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.FirebaseFunctionsException
import dev.gitlive.firebase.functions.code
import dev.gitlive.firebase.functions.details
import dev.gitlive.firebase.functions.functions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.ballistic.dreamjournalai.shared.dream_authentication.domain.repository.AuthRepository
import org.ballistic.dreamjournalai.shared.dream_authentication.domain.repository.AuthStateResponse
import org.ballistic.dreamjournalai.shared.dream_authentication.domain.repository.ReloadUserResponse
import org.ballistic.dreamjournalai.shared.dream_authentication.domain.repository.SendPasswordResetEmailResponse
import org.ballistic.dreamjournalai.shared.core.Constants.CREATED_AT
import org.ballistic.dreamjournalai.shared.core.Constants.DISPLAY_NAME
import org.ballistic.dreamjournalai.shared.core.Constants.EMAIL
import org.ballistic.dreamjournalai.shared.core.Constants.USERS
import org.ballistic.dreamjournalai.shared.core.Resource

class AuthRepositoryImpl (
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
) : AuthRepository {
    private var anonymousUserId: String? = null

    private val _dreamTokens = MutableStateFlow(0)
    override val dreamTokens: StateFlow<Int> = _dreamTokens

    private val _isUserExist = MutableStateFlow(false)
    override val isUserExist: StateFlow<Boolean> = _isUserExist

    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _emailVerified = MutableStateFlow(false)
    override val isEmailVerified: StateFlow<Boolean> = _emailVerified

    private val _isUserAnonymous = MutableStateFlow(false)
    override val isUserAnonymous: StateFlow<Boolean> = _isUserAnonymous

    override suspend fun firebaseSignInWithGoogle(
        googleCredential: AuthCredential
    ): Flow<Resource<Pair<AuthResult, String?>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val anonymousUserId = if (auth.currentUser?.isAnonymous == true) {
                    auth.currentUser?.uid
                } else {
                    null
                }

                val result = auth.signInWithCredential(googleCredential)

                val isNewUser = result.additionalUserInfo?.isNewUser == true

                emit(Resource.Success(Pair(result, anonymousUserId)))
            } catch (e: FirebaseAuthUserCollisionException) {
                emit(Resource.Error("An existing user account was found with the same credentials: ${e.message}"))
            } catch (e: Exception) {
                emit(Resource.Error("Failed to sign in with Google: ${e.message}"))
            }

        }
    }

    override suspend fun consumeDreamTokens(tokensToConsume: Int): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // 1) Get the currently signed-in user from dev.gitlive.firebase.auth
                val user = Firebase.auth.currentUser
                    ?: return@withContext Resource.Error("User is not logged in.")

                // 2) Get a reference to the "USERS/{userId}" document in Firestore
                val userDocRef = Firebase.firestore.collection(USERS).document(user.uid)

                // 3) Fetch the snapshot (this is already a suspend call with dev.gitlive)
                val snapshot = userDocRef.get()
                val data: Map<String, Any> = snapshot.data<Map<String, Any>>()
                val tokens = data["dreamTokens"] as? Long ?: 0L


                // 4) Check if user has enough tokens, then update
                if (tokens >= tokensToConsume) {
                    userDocRef.update(mapOf("dreamTokens" to tokens - tokensToConsume))
                    Resource.Success(true)
                } else {
                    Resource.Error("Not enough dream tokens available.")
                }
            } catch (e: Exception) {
                Resource.Error("Failed to consume dream tokens: ${e.message}")
            }
        }
    }


    override suspend fun unlockWord(word: String, tokenCost: Int): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // 1) Get the current user from dev.gitlive.auth
                val user = Firebase.auth.currentUser
                    ?: return@withContext Resource.Error("User is not logged in.")

                // 2) Reference the user document: "USERS/{uid}"
                val userDocRef = Firebase.firestore.collection(USERS).document(user.uid)

                // 3) Fetch the snapshot (suspend call in dev.gitlive)
                val snapshot = userDocRef.get()

                // 4) Decode the snapshot's data into a Map
                val data = snapshot.data<Map<String, Any>>()

                // 5) Extract fields
                val unlockedWords = (data["unlockedWords"] as? List<*>)?.toMutableList() ?: mutableListOf()
                val userTokens = data["dreamTokens"] as? Long ?: 0L

                // 6) If the word is already unlocked, just return success
                if (unlockedWords.contains(word)) {
                    return@withContext Resource.Success(true)
                }

                // 7) Check user has enough tokens (if cost > 0)
                if (tokenCost > 0 && userTokens < tokenCost) {
                    return@withContext Resource.Error("Not enough dream tokens")
                }

                // 8) Deduct tokens if needed (presumably calls another method in your repo)
                if (tokenCost > 0) {
                    consumeDreamTokens(tokenCost) // must also be updated to dev.gitlive calls
                }

                // 9) Add new word to the unlocked list
                unlockedWords.add(word)

                // 10) Update the doc in Firestore
                userDocRef.update(mapOf("unlockedWords" to unlockedWords))

                Resource.Success(true)

            } catch (e: Exception) {
                Resource.Error("Failed to unlock word: ${e.message}")
            }
        }
    }

    override suspend fun getUnlockedWords(): Flow<Resource<List<String>>> = flow {
        emit(Resource.Loading())

        // Retrieve the current user
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            emit(Resource.Error("No user is logged in."))
            return@flow
        }

        try {
            // Reference to the user's document: USERS/{uid}
            val userDocRef = Firebase.firestore
                .collection(USERS)
                .document(currentUser.uid)

            // Fetch the user document (suspend call)
            val snapshot = userDocRef.get()

            // Check if the document exists
            if (snapshot.exists) {
                // Retrieve the 'unlockedWords' field as a List<String>
                val unlockedWords = snapshot.get("unlockedWords") as? List<String> ?: emptyList()

                Logger.d("Unlocked words: $unlockedWords")
                emit(Resource.Success(unlockedWords))
            } else {
                emit(Resource.Error("User document does not exist."))
            }
        } catch (e: Exception) {
            Logger.e("Failed to get unlocked words", e)
            emit(Resource.Error("Failed to get unlocked words: ${e.message}"))
        }
    }


    override suspend fun recordUserInteraction() {
        // If you need to reload the user, do it via dev.gitlive as well
        reloadFirebaseUser()

        val user = Firebase.auth.currentUser
        if (user != null) {
            try {
                val userDocRef = Firebase.firestore.collection(USERS).document(user.uid)

                // Set the 'lastActiveTimestamp' to server time, merging with existing fields
                userDocRef.set(
                    data = mapOf("lastActiveTimestamp" to FieldValue.serverTimestamp),
                    merge = true
                )

            } catch (e: Exception) {
                Logger.e("AuthRepositoryImpl"){ "Failed to record user interaction: $e" }
            }
        }
    }


    private suspend fun validateUser() {
        val user = Firebase.auth.currentUser

        // Update your local states or flows
        _isUserExist.value = user != null
        _emailVerified.value = user?.isEmailVerified ?: false
        _isLoggedIn.value = user != null && user.isEmailVerified == true
        _isUserAnonymous.value = user?.isAnonymous ?: false

        // If user is verified, update FireStore
        if (user != null && user.isEmailVerified) {
            try {
                val userDocRef = Firebase
                    .firestore
                    .collection(USERS)
                    .document(user.uid)

                // 'update(...)' is a suspend function in dev.gitlive
                userDocRef.update(mapOf("emailVerified" to true))

                // Optionally log success
                // Log.d("Firestore", "Email verification status updated successfully")
            } catch (e: Exception) {
                // Log or handle the error
                // Log.e("Firestore", "Error updating email verification status: ", e)
            }
        }
    }

    override suspend fun anonymousSignIn(): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val authResult = Firebase.auth.signInAnonymously()
            emit(Resource.Success(authResult))
        }.catch { e ->
            emit(Resource.Error(e.toString()))
        }
    }
    private suspend fun addUserToFirestore(registrationTimestamp: Long) {
        auth.currentUser?.apply {
            val user = toUser(registrationTimestamp)
            db.collection(USERS).document(uid).set(user)
        }
    }

    override suspend fun firebaseSignUpWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Resource<String>> {

        // Check if the current user is anonymous and store the user ID if so
        anonymousUserId = if (Firebase.auth.currentUser?.isAnonymous == true) {
            Firebase.auth.currentUser?.uid
        } else {
            null
        }

        return flow {
            emit(Resource.Loading())

            try {
                // Prepare the data to send to your Cloud Function
                val data = mapOf(
                    "email" to email,
                    "password" to password
                )

                // Call the "createAccountAndSendEmailVerification" Cloud Function
                val result = Firebase.functions
                    .httpsCallable("createAccountAndSendEmailVerification")
                    .invoke(data)

                // Decode the result into a Map
                val dataMap = result.data<Map<String, Any>>()

                // Extract the "message" field
                val message = dataMap["message"] as? String
                    ?: throw Exception("Invalid response from Firebase Function")

                // Optionally add user details to FireStore
                addUserToFirestore(registrationTimestamp = Clock.System.now().toEpochMilliseconds())

                // Emit success
                emit(Resource.Success(message))

            } catch (e: Exception) {
                // Emit any errors
                emit(Resource.Error("SignUp error: ${e.message}"))
            }
        }.catch { e ->
            // Catch any downstream exceptions
            emit(Resource.Error(e.toString()))
        }
    }



    override suspend fun sendEmailVerification(): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())
            try {
                auth.currentUser?.sendEmailVerification()
                emit(Resource.Success(true))
            } catch (e: Exception) {
                emit(Resource.Error(e.toString()))
            }
        }
    }


    override suspend fun firebaseSignInWithEmailAndPassword(
        email: String,
        password: String,
    ): Flow<Resource<AuthResult>> {
        anonymousUserId = if (auth.currentUser?.isAnonymous == true) {
            auth.currentUser?.uid
        } else {
            null
        }
        return flow {
            emit(Resource.Loading())
            try {
                val currentUser = auth.currentUser

                if (currentUser != null && anonymousUserId != null && anonymousUserId != "") {
                    val result = auth.signInWithEmailAndPassword(email, password)

                    result.user?.let {
                        transferDreamsFromAnonymousToPermanent(
                            it.uid, anonymousUserId ?: ""
                        )
                    }
                    emit(Resource.Success(result))
                } else {
                    val result = auth.signInWithEmailAndPassword(email, password)
                    emit(Resource.Success(result))
                }
            } catch (e: FirebaseAuthUserCollisionException) {
                val result = auth.signInWithEmailAndPassword(email, password)
                emit(Resource.Success(result))
            } catch (e: Exception) {
                emit(Resource.Error(e.toString()))
            }
            validateUser()
        }
    }


    override suspend fun reloadFirebaseUser(): ReloadUserResponse {
        return try {
            auth.currentUser?.reload()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): SendPasswordResetEmailResponse {
        return try {
            auth.sendPasswordResetEmail(email)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun signOut() = auth.signOut()

    override suspend fun revokeAccess(
        password: String?
    ): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())

            try {
                // Get the currently logged-in user via dev.gitlive
                val user = Firebase.auth.currentUser
                    ?: throw IllegalStateException("No authenticated user found")

                // If a password is provided, re-authenticate the user
                if (password != null && !user.email.isNullOrBlank()) {
                    val credential = EmailAuthProvider.credential(user.email!!, password)
                    // 'reauthenticate(...)' is already a suspend function in GitLive
                    user.reauthenticate(credential)
                }

                // Perform any additional cleanup or revocation as needed
                // e.g., if you have an external OAuth service, etc.

                // Finally, delete the user
                user.delete()

                emit(Resource.Success(true))

            } catch (e: Exception) {
                emit(Resource.Error("Failed to revoke access: ${e.message}", false))
            }
        }
    }


    override fun getAuthState(viewModelScope: CoroutineScope): AuthStateResponse {
        return Firebase.auth.authStateChanged
            // Map the Flow<FirebaseUser?> to Flow<Boolean>
            .map { user -> user == null }
            // Convert the flow to a StateFlow<Boolean>
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                // If currentUser is null, the initial value should be true (logged out)
                initialValue = (Firebase.auth.currentUser == null)
            )
    }

    override suspend fun transferDreamsFromAnonymousToPermanent(
        permanentUid: String,
        anonymousUid: String
    ) {
        withContext(Dispatchers.IO) {
            try {
                // Call the "handleAccountLinking" Cloud Function
                val result = Firebase.functions
                    .httpsCallable("handleAccountLinking")
                    .invoke(
                        mapOf("permanentUid" to permanentUid, "anonymousUid" to anonymousUid)
                    )

                // Decode the response into a map
                val data = result.data<Map<String, Any>>()
                val success = data["success"] == true

                if (success) {
                    // Dreams transferred successfully
                    println("Dreams transferred successfully. Proceed to clean up anonymous account if needed.")
                } else {
                    // If 'success' was false or missing
                    println("Failed to transfer dreams: ${data["message"]}")
                }
            } catch (e: Exception) {
                when (e) {
                    is FirebaseFunctionsException -> {
                        // Handle Firebase Functions–specific errors
                        println("Firebase Functions error: code=${e.code}, details=${e.details}")
                    }
                    else -> {
                        println("Error transferring dreams: $e")
                    }
                }
            }
        }
    }

    override suspend fun addDreamTokensFlowListener(): Flow<Resource<String>> {
        val user = Firebase.auth.currentUser
        if (user == null) {
            // Immediately return a flow that emits an error
            return flowOf(Resource.Error("User is not logged in"))
        }

        val docRef = Firebase.firestore.collection(USERS).document(user.uid)

        // Create one flow with `flow {}`, then collect snapshots inside it
        return flow {
            // 1) Emit a loading state up front
            emit(Resource.Loading())

            // 2) Collect snapshots in this same flow builder
            docRef.snapshots().collect { snapshot ->
                val data = snapshot.get<String>("dreamTokens")
                val dreamTokens = data.toString()
                _dreamTokens.value = dreamTokens.toInt()

                // 3) Emit a success each time we get new data
                emit(Resource.Success(dreamTokens))
            }
        }.catch { e ->
            // 4) Catch any exceptions thrown in the flow
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }
}

fun FirebaseUser.toUser(registrationTimestamp: Long) = mapOf(
    DISPLAY_NAME to displayName,
    EMAIL to email,
    CREATED_AT to serverTimestamp,
    "registrationTimestamp" to registrationTimestamp,
    "lastSignInTimestamp" to serverTimestamp,
)
