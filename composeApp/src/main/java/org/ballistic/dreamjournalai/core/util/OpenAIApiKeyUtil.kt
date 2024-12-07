package org.ballistic.dreamjournalai.core.util

import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

object OpenAIApiKeyUtil {
    /**
     * Asynchronously retrieves the OpenAI API key from Firebase Cloud Functions.
     *
     * @return The OpenAI API key as a String.
     * @throws IllegalStateException if there is any issue during the call or the data retrieval.
     */
    suspend fun getOpenAISecretKey(): String {
        val functions = FirebaseFunctions.getInstance()
        return try {
            val result = functions
                .getHttpsCallable("getOpenAISecretKey")
                .call()
                .await()
            val data = result.getData() as? Map<*, *> ?: throw IllegalStateException("Failed to fetch API Key")
            data["apiKey"] as? String ?: throw IllegalStateException("API Key is not available")
        } catch (e: Exception) {
            Log.e("OpenAIApiKeyUtil", "Failed to retrieve API Key: ${e.localizedMessage}", e)
            throw IllegalStateException("Failed to retrieve API Key: ${e.localizedMessage}", e)
        }
    }
}