package org.ballistic.dreamjournalai.shared.core.util

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.functions.functions

object OpenAIApiKeyUtil {

    suspend fun getOpenAISecretKey(): String {
        return try {
            val functions = Firebase.functions
            // Call the cloud function and decode the result
            val result = functions
                .httpsCallable("getOpenAISecretKey")
                .invoke()

            // Extract the key from the response and remove "apiKey=" prefix
            val response = result.data<String>().substringAfter("apiKey=").substringBefore("}")
            response
        } catch (e: Exception) {
            throw IllegalStateException("Failed to retrieve API Key: ${e.message}", e)
        }
    }
}