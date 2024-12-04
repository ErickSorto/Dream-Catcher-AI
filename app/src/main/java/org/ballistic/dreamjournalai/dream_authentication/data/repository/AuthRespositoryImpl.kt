package org.ballistic.dreamjournalai.dream_authentication.data.repository


import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.ballistic.dreamjournalai.core.Constants.CREATED_AT
import org.ballistic.dreamjournalai.core.Constants.DISPLAY_NAME
import org.ballistic.dreamjournalai.core.Constants.EMAIL
import org.ballistic.dreamjournalai.core.Constants.USERS
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.dream_authentication.domain.repository.AuthRepository
import org.ballistic.dreamjournalai.dream_authentication.domain.repository.ReloadUserResponse
import org.ballistic.dreamjournalai.dream_authentication.domain.repository.SendPasswordResetEmailResponse

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

    init {
        validateUser()
    }

    override suspend fun firebaseSignInWithGoogle(
        googleCredential: AuthCredential
    ): Flow<Resource<Pair<AuthResult, String?>>> {
        return flow {
            Log.d("SignInWithGoogle", "Signing in with Google")
            emit(Resource.Loading())
            try {
                val anonymousUserId = if (auth.currentUser?.isAnonymous == true) {
                    Log.d("SignInWithGoogle", "Current user is anonymous with UID: ${auth.currentUser?.uid}")
                    auth.currentUser?.uid
                } else {
                    null
                }

                Log.d("SignInWithGoogle", "Attempting to sign in with Google")
                val result = auth.signInWithCredential(googleCredential).await()

                val isNewUser = result.additionalUserInfo?.isNewUser == true


                Log.d("SignInWithGoogle", "Firebase sign-in completed. Is new user: $isNewUser")

                emit(Resource.Success(Pair(result, anonymousUserId)))
            } catch (e: FirebaseAuthUserCollisionException) {
                Log.e("SignInWithGoogle", "User collision: ${e.message}")
                emit(Resource.Error("An existing user account was found with the same credentials: ${e.message}"))
            } catch (e: Exception) {
                Log.e("SignInWithGoogle", "Failed to sign in with Firebase: ${e.message}")
                emit(Resource.Error("Failed to sign in with Google: ${e.message}"))
            }

        }
    }

    override suspend fun consumeDreamTokens(tokensToConsume: Int): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            val user = FirebaseAuth.getInstance().currentUser
            run {
                val userDocRef = user?.let { db.collection(USERS).document(it.uid) }
                val snapshot = userDocRef?.get()?.await()
                val currentDreamTokens = snapshot?.getLong("dreamTokens")?.toInt() ?: 0

                if (currentDreamTokens >= tokensToConsume) {
                    userDocRef?.update("dreamTokens", currentDreamTokens - tokensToConsume)?.await()
                    Resource.Success(true)
                } else {
                    Resource.Error("Not enough dream tokens available.")
                }
            }
        }
    }


    override suspend fun unlockWord(word: String, tokenCost: Int): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            val user = FirebaseAuth.getInstance().currentUser
            val userDocRef = user?.let { db.collection(USERS).document(it.uid) }
            val snapshot = userDocRef?.get()?.await()
            val unlockedWords = snapshot?.get("unlockedWords") as? ArrayList<String> ?: arrayListOf()
            val userTokens = snapshot?.get("dreamTokens") as? Long ?: 0

            if (unlockedWords.contains(word)) {
                return@withContext Resource.Success(true)
            }

            if (tokenCost > 0 && userTokens < tokenCost) {
                return@withContext Resource.Error("Not enough dream tokens")
            }

            if (tokenCost > 0) {
                consumeDreamTokens(tokenCost)
            }

            unlockedWords.add(word)
            userDocRef?.update("unlockedWords", unlockedWords)?.await()
            Resource.Success(true)
        }
    }

    override suspend fun getUnlockedWords(): Flow<Resource<List<String>>> {
        return flow {
            Log.d("UnlockWords", "getUnlockedWords")
            emit(Resource.Loading())
            val user = FirebaseAuth.getInstance().currentUser
            run {
                val userDocRef = user?.let { db.collection(USERS).document(it.uid) }
                val snapshot = userDocRef?.get()?.await()
                val unlockedWords = snapshot?.get("unlockedWords") as? ArrayList<String>
                if (unlockedWords != null) {
                    emit(Resource.Success(unlockedWords))
                } else {
                    emit(Resource.Success(arrayListOf()))
                }
            }
        }
    }

    override suspend fun recordUserInteraction() {
        reloadFirebaseUser()
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val userDocRef = db.collection(USERS).document(it.uid)

            // Update the lastActiveTimestamp field, or create it if it doesn't exist
            userDocRef.set(mapOf("lastActiveTimestamp" to serverTimestamp()), SetOptions.merge()).await()
        }
    }


    private fun validateUser() {
        val user = auth.currentUser
        _isUserExist.value = user != null
        _emailVerified.value = user?.isEmailVerified ?: false
        _isLoggedIn.value = user != null && user.isEmailVerified
        _isUserAnonymous.value = user?.isAnonymous ?: false

        // Update the emailVerified field in Firestore
        if (user != null && user.isEmailVerified) {
            val userDocRef = db.collection(USERS).document(user.uid)
            userDocRef.update("emailVerified", true)
                .addOnSuccessListener {
                    Log.d("Firestore", "Email verification status updated successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error updating email verification status: ", exception)
                }
        }
    }

    override suspend fun anonymousSignIn(): Flow<Resource<AuthResult>> {
        return flow {
            Log.i("AnonymousSignIn", "User signed in anonymously")
            emit(Resource.Loading())
            emit(Resource.Success(auth.signInAnonymously().await()))
        }.catch { e ->
            emit(Resource.Error(e.toString()))
            Log.e("AnonymousSignIn", "Error signing in anonymously: ", e)
        }
    }

    private suspend fun addUserToFirestore(registrationTimestamp: Long) {
        auth.currentUser?.apply {
            val user = toUser(registrationTimestamp)
            db.collection(USERS).document(uid).set(user).await()
        }
    }

    override suspend fun firebaseSignUpWithEmailAndPassword(
        email: String, password: String
    ): Flow<Resource<String>> {

        anonymousUserId = if (auth.currentUser?.isAnonymous == true) {
            auth.currentUser?.uid.also {
                Log.d("SignUp", "Current user is anonymous with UID: $it")
            }
        } else {
            Log.d("SignUp", "No anonymous user found.")
            null
        }

        return flow {
            emit(Resource.Loading())
            Log.d("SignUp", "Attempting to sign up with email: $email")
            try {
                val firebaseFunctions = FirebaseFunctions.getInstance()

                // Prepare data for the Firebase Function call
                val data = hashMapOf(
                    "email" to email,
                    "password" to password
                )

                Log.d("SignUp", "Calling 'createAccountAndSendEmailVerification' with data: $data")
                val result = firebaseFunctions
                    .getHttpsCallable("createAccountAndSendEmailVerification")
                    .call(data)
                    .await()

                val dataMap = result.getData() as? HashMap<*, *>
                val message = dataMap?.get("message") as? String

                if (message != null) {
                    Log.d("SignUp", "Received message from Firebase Function: $message")
                    // Further actions such as adding user to Firestore can be logged as well
                    addUserToFirestore(registrationTimestamp = System.currentTimeMillis())
                    emit(Resource.Success(message))
                } else {
                    Log.e("SignUp", "Invalid or null response received from Firebase Function.")
                    throw Exception("Invalid response from Firebase Function")
                }
            } catch (e: Exception) {
                Log.e("SignUp", "SignUp failed with exception: $e")
                emit(Resource.Error("SignUp error: ${e.localizedMessage}"))
            }
        }
    }



    override suspend fun sendEmailVerification(): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())
            try {
                auth.currentUser?.sendEmailVerification()?.await()
                Log.d("EmailVerification", "Email verification sent")
                emit(Resource.Success(true))
            } catch (e: Exception) {
                Log.e("EmailVerification", "Error sending email verification: $e")
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
                    val result = auth.signInWithEmailAndPassword(email, password).await()

                    result.user?.let {
                        transferDreamsFromAnonymousToPermanent(
                            it.uid, anonymousUserId ?: ""
                        )
                    }
                    emit(Resource.Success(result))
                } else {
                    val result = auth.signInWithEmailAndPassword(email, password).await()
                    emit(Resource.Success(result))
                }
            } catch (e: FirebaseAuthUserCollisionException) {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                emit(Resource.Success(result))
            } catch (e: Exception) {
                emit(Resource.Error(e.toString()))
            }
            validateUser()
        }
    }


    override suspend fun reloadFirebaseUser(): ReloadUserResponse {
        return try {
            auth.currentUser?.reload()?.await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): SendPasswordResetEmailResponse {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun signOut() = auth.signOut()

    override suspend fun revokeAccess(
        password: String?
    ): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())
            try {
                auth.currentUser?.let { user ->
                    if (password != null && user.email != null) {
                        // If a password is provided, re-authenticate the user
                        val credential = EmailAuthProvider.getCredential(user.email!!, password)
                        user.reauthenticate(credential).await()
                    }

                    // Perform additional cleanup or revocation if necessary
                    // For example, revoking OAuth tokens if applicable
                    // yourOAuthService.revokeTokens(user.uid)

                    // Delete the user
                    user.delete().await()

                    emit(Resource.Success(true))
                } ?: throw IllegalStateException("No authenticated user found")
            } catch (e: Exception) {
                emit(Resource.Error("Failed to revoke access: ${e.localizedMessage}", false))
            }
        }
    }


    override fun getAuthState(viewModelScope: CoroutineScope) = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser == null)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), auth.currentUser == null)

    override suspend fun transferDreamsFromAnonymousToPermanent(
        permanentUid: String,
        anonymousUid: String,
    ) {
        try {
            val result = Firebase.functions
                .getHttpsCallable("handleAccountLinking")
                .call(mapOf("permanentUid" to permanentUid, "anonymousUid" to anonymousUid))
                .await()



            val data = result.getData() as? Map<*, *>
            if (data?.get("success") == true) {
                Log.i("TransferDreams", "Dreams transferred successfully. Proceed to clean up anonymous account if needed.")

            } else {
                Log.e("TransferDreams", "Failed to transfer dreams: ${data?.get("message")}")
                // Handle the failure accordingly, possibly retrying or asking the user to try again
            }
        } catch (e: Exception) {
            when (e) {
                is FirebaseFunctionsException -> {
                    val code = e.code
                    val details = e.details
                    // Handle Firebase Functions specific error
                    Log.e(
                        "TransferDreams",
                        "Firebase Functions error: code=$code, details=$details"
                    )
                }

                else -> {
                    Log.e("TransferDreams", "Error transferring dreams2: $e")
                }
            }
        }
    }

    override suspend fun addDreamTokensFlowListener(): Flow<Resource<Int>> {
        return callbackFlow {
            val user = FirebaseAuth.getInstance().currentUser
            val userDocRef = user?.let { db.collection(USERS).document(it.uid) }
            val listenerRegistration = userDocRef?.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Resource.Error(e.localizedMessage ?: "Unknown error"))
                    return@addSnapshotListener
                }

                val dreamTokens = snapshot?.getLong("dreamTokens")?.toInt() ?: 0
                _dreamTokens.value = dreamTokens
                trySend(Resource.Success(dreamTokens))
            }

            awaitClose {
                listenerRegistration?.remove()
            }
        }
    }
}

fun FirebaseUser.toUser(registrationTimestamp: Long) = mapOf(
    DISPLAY_NAME to displayName,
    EMAIL to email,
    CREATED_AT to serverTimestamp(),
    "registrationTimestamp" to registrationTimestamp,
    "lastSignInTimestamp" to serverTimestamp(),
)
