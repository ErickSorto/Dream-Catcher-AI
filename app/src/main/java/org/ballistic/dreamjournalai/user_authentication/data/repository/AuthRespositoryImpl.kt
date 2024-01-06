package org.ballistic.dreamjournalai.user_authentication.data.repository


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.*
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import org.ballistic.dreamjournalai.core.Constants.CREATED_AT
import org.ballistic.dreamjournalai.core.Constants.DISPLAY_NAME
import org.ballistic.dreamjournalai.core.Constants.EMAIL
import org.ballistic.dreamjournalai.core.Constants.SIGN_IN_REQUEST
import org.ballistic.dreamjournalai.core.Constants.SIGN_UP_REQUEST
import org.ballistic.dreamjournalai.core.Constants.USERS
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.user_authentication.domain.repository.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient,
    @Named(SIGN_IN_REQUEST)
    private var signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST)
    private var signUpRequest: BeginSignInRequest,
    private var signInClient: GoogleSignInClient,
    private val db: FirebaseFirestore,
) : AuthRepository {
    override val currentUser = MutableStateFlow(auth.currentUser)

    private var anonymousUserId: String? = null

    private val _isUserExist = MutableStateFlow(false)
    override val isUserExist: StateFlow<Boolean> = _isUserExist

    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _emailVerified = MutableStateFlow(false)
    override val isEmailVerified: StateFlow<Boolean> = _emailVerified

    private val _isUserAnonymous = MutableStateFlow(false)
    override val isUserAnonymous: StateFlow<Boolean> = _isUserAnonymous

    private val _dreamTokens = MutableStateFlow(0)
    override val dreamTokens: StateFlow<Int> = _dreamTokens

    init {
        setupUserDataListener()
    }

    init {
        validateUser()
    }

    override suspend fun consumeDreamTokens(tokensToConsume: Int): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            val user = currentUser
            run {
                val userDocRef = user.value?.let { db.collection(USERS).document(it.uid) }
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
            val user = currentUser
            val userDocRef = user.value?.let { db.collection(USERS).document(it.uid) }
            val snapshot = userDocRef?.get()?.await()
            val unlockedWords = snapshot?.get("unlockedWords") as? ArrayList<String> ?: arrayListOf()
            val userTokens = snapshot?.get("tokens") as? Int ?: 0

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
            val user = currentUser
            run {
                val userDocRef = user.value?.let { db.collection(USERS).document(it.uid) }
                val snapshot = userDocRef?.get()?.await()
                val unlockedWords = snapshot?.get("unlockedWords") as? ArrayList<String>
                Log.d("UnlockWords", "unlockedWords: $unlockedWords")
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
        val user = currentUser.value
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

    private fun setupUserDataListener() {
        auth.currentUser?.let { user ->
            val userDocRef = db.collection(USERS).document(user.uid)
            userDocRef.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle the error case
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val dreamTokensValue = snapshot.getLong("dreamTokens")?.toInt() ?: 0
                    _dreamTokens.value = dreamTokensValue
                }
            }
        }
    }


    override suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse {
        anonymousUserId = if (auth.currentUser?.isAnonymous == true) {
            auth.currentUser?.uid
        } else {
            null
        }

        return try {
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            Resource.Success(signInResult)
        } catch (e: Exception) {
            try {
                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                Resource.Success(signUpResult)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    override suspend fun firebaseSignInWithGoogle(
        googleCredential: AuthCredential
    ): Flow<Resource<Pair<AuthResult, String?>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val result = auth.signInWithCredential(googleCredential).await()
                if (anonymousUserId != null) {
                    Log.d("SignIn", "result ${result.user?.uid} ${result.user?.isAnonymous} anonymousUserId $anonymousUserId")
                    emit(Resource.Success(Pair(result, anonymousUserId)))
                } else{
                    emit(Resource.Success(Pair(result, null)))
                }
            } catch (e: FirebaseAuthUserCollisionException) {
                val result = auth.signInWithCredential(googleCredential).await()

                if (anonymousUserId != null) {
                    Log.d("SignIn", "result ${result.user?.uid} ${result.user?.isAnonymous} anonymousUserId $anonymousUserId")
                    emit(Resource.Success(Pair(result, anonymousUserId)))
                } else {
                    emit(Resource.Success(Pair(result, null)))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.toString()))
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
            auth.currentUser?.uid
        } else {
            null
        }

        return flow {
            emit(Resource.Loading())
            try {
                val firebaseFunctions = FirebaseFunctions.getInstance()

                // Call the createAccount function
                val data = hashMapOf(
                    "email" to email,
                    "password" to password
                )

                Log.d("SignUp", "data: $data")
                val result = firebaseFunctions
                    .getHttpsCallable("createAccountAndSendEmailVerification")
                    .call(data)
                    .await()

                val dataMap = result.data as? HashMap<*, *>
                val message = dataMap?.get("message") as? String

                if (message != null) {
                    // If you still want to add the user to Firestore after receiving the message, you can keep the next lines
                    addUserToFirestore(
                        registrationTimestamp = System.currentTimeMillis()
                    )
                    Log.d("SignUp", "Response: $message")
                    emit(Resource.Success(message))
                } else {
                    throw Exception("Invalid response from Firebase Function")
                }
            } catch (e: Exception) {
                Log.e("SignUp", "Error: $e")
                emit(Resource.Error(e.toString()))
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

                if (currentUser != null && anonymousUserId != null) {
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
    ): Flow<RevokeAccessResponse> {
        return flow {
            emit(Resource.Loading())
            auth.currentUser?.apply {
                if (password != null) {
                    val credential = EmailAuthProvider.getCredential(email!!, password)
                    reauthenticate(credential).await()
                }
                signInClient.revokeAccess().await()
                oneTapClient.signOut().await()
                delete().await()
            }

            emit(Resource.Success(true))
        }.catch { e ->
            emit(Resource.Error(e.toString()))
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

            val data = result.data as? Map<*, *>
            if (data?.get("success") == true) {
                // The dreams were transferred successfully
                (data["message"] as? String)?.let { Log.i("TransferDreams", it) }
            } else {
                // There was a problem
                Log.e("TransferDreams", "Error transferring dreams: ${data?.get("message")}")
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
                    Log.e("TransferDreams", "Error transferring dreams: $e")
                }
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
    "dreamTokens" to 50,
)
