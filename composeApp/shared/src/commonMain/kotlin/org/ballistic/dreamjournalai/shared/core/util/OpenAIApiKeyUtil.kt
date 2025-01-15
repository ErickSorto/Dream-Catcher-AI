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

            // Convert HttpsCallableResult into a Map
            val data = result.data<Map<String, Any>>()
                ?: throw IllegalStateException("Failed to fetch API Key")

            // Extract the key from the map
            data["apiKey"] as? String
                ?: throw IllegalStateException("API Key is not available in the response")
        } catch (e: Exception) {
            throw IllegalStateException("Failed to retrieve API Key: ${e.message}", e)
        }
    }
}