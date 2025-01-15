package org.ballistic.dreamjournalai.shared.dream_authentication.domain.repository
import org.ballistic.dreamjournalai.shared.core.Resource

typealias SignOutResponse = Resource<Boolean>
typealias RevokeAccessResponse = Resource<Boolean>

interface ProfileRepository {
    val displayName: String
    val photoUrl: String

    suspend fun signOut(): SignOutResponse

    suspend fun revokeAccess(): RevokeAccessResponse
}