package org.ballistic.dreamjournalai.dream_authentication.domain.repository

import org.ballistic.dreamjournalai.core.Resource

typealias SignOutResponse = Resource<Boolean>
typealias RevokeAccessResponse = Resource<Boolean>

interface ProfileRepository {
    val displayName: String
    val photoUrl: String

    suspend fun signOut(): SignOutResponse

    suspend fun revokeAccess(): RevokeAccessResponse
}