package org.ballistic.dreamjournalai.user_authentication.domain.repository

import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.ballistic.dreamjournalai.core.Resource


typealias OneTapSignInResponse = Resource<BeginSignInResult>
typealias SignInWithGoogleResponse = Resource<Unit>
typealias SignUpResponse = Resource<Boolean>
typealias SendEmailVerificationResponse = Resource<Boolean>
typealias SignInResponse = Boolean
typealias ReloadUserResponse = Resource<Boolean>
typealias SendPasswordResetEmailResponse = Resource<Boolean>
typealias AuthStateResponse = StateFlow<Boolean>

interface AuthRepository {
    val currentUser: StateFlow<FirebaseUser?>
    val isUserExist: StateFlow<Boolean>
    val isEmailVerified: StateFlow<Boolean>
    val isLoggedIn: StateFlow<Boolean>
    val isUserAnonymous: StateFlow<Boolean>

    suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse

    suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): Flow<Resource<Pair<AuthResult, String?>>>

    suspend fun firebaseSignUpWithEmailAndPassword(email: String, password: String): Flow<Resource<String>>

    suspend fun sendEmailVerification(): Flow <Resource<Boolean>>

    suspend fun firebaseSignInWithEmailAndPassword(email: String, password: String): Flow<Resource<AuthResult>>

    suspend fun anonymousSignIn(): Flow<Resource<AuthResult>>

    suspend fun reloadFirebaseUser(): ReloadUserResponse

    suspend fun sendPasswordResetEmail(email: String): SendEmailVerificationResponse

    fun signOut()

    suspend fun revokeAccess(password: String?): Flow<RevokeAccessResponse>

    fun getAuthState(viewModelScope: CoroutineScope): AuthStateResponse

    suspend fun transferDreamsFromAnonymousToPermanent(permanentUid: String, anonymousUid: String)

    val dreamTokens: StateFlow<Int>

    suspend fun consumeDreamTokens(tokensToConsume: Int): Resource<Boolean>

    suspend fun unlockWord(word: String, tokenCost: Int): Resource<Boolean>

    suspend fun getUnlockedWords(): Flow<Resource<List<String>>>

    suspend fun recordUserInteraction()
}