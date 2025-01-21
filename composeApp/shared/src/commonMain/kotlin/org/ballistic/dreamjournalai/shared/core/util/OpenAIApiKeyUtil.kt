package org.ballistic.dreamjournalai.shared.core.util

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.functions.functions

object OpenAIApiKeyUtil {

    suspend fun getOpenAISecretKey(): String {
        return try {
            val functions = Firebase.functions
            // `invoke()` returns an HttpsCallableResult, then
            // `.data<T>()` decodes it to the type you specify.
            val result = functions
                .httpsCallable("getOpenAISecretKey")
                .invoke()

            // Extract the key from the map
            result.data<String>()
        } catch (e: Exception) {
            throw IllegalStateException("Failed to retrieve API Key: ${e.message}", e)
        }
    }
}