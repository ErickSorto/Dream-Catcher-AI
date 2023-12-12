package org.ballistic.dreamjournalai.feature_dream.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.ballistic.dreamjournalai.core.Constants.USERS
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.domain.repository.DreamRepository
import java.net.URL
import java.net.URLDecoder
import java.util.*
import javax.inject.Singleton

@Singleton
class DreamRepositoryImpl(
    private val storage: FirebaseStorage,
    private val db: FirebaseFirestore
) : DreamRepository {

    private val dreamsCollection
        get() = getCollectionReferenceForDreams()

    // Add this function to get the current user's UID
    private fun userID(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getDreams(): Flow<List<Dream>> {
        return callbackFlow {
            val registration = dreamsCollection?.addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val dreams = querySnapshot.documents.mapNotNull { document ->
                        val data = document.data
                        if (data != null) {
                            Dream(
                                title = data["title"] as String? ?: "",
                                content = data["content"] as String? ?: "",
                                timestamp = data["timestamp"] as Long? ?: 0,
                                date = data["date"] as String? ?: "",
                                sleepTime = data["sleepTime"] as String? ?: "",
                                wakeTime = data["wakeTime"] as String? ?: "",
                                AIResponse = data["airesponse"] as String? ?: "",
                                isFavorite = data["favorite"] as Boolean? ?: false,
                                isLucid = data["lucid"] as Boolean? ?: false,
                                isNightmare = data["nightmare"] as Boolean? ?: false,
                                isRecurring = data["recurring"] as Boolean? ?: false,
                                falseAwakening = data["falseAwakening"] as Boolean? ?: false,
                                lucidityRating = (data["lucidityRating"] as Long?)?.toInt() ?: 1,
                                moodRating = (data["moodRating"] as Long?)?.toInt() ?: 1,
                                vividnessRating = (data["vividnessRating"] as Long?)?.toInt() ?: 1,
                                timeOfDay = data["timeOfDay"] as String? ?: "",
                                backgroundImage = (data["backgroundImage"] as Long?)?.toInt() ?: 0,
                                generatedImage = data["generatedImage"] as String?,
                                generatedDetails = data["generatedDetails"] as String? ?: "",
                                dreamQuestion = data["dreamQuestion"] as String? ?: "",
                                dreamAIStory = data["dreamAIStory"] as String? ?: "",
                                dreamAIAdvice = data["dreamAIAdvice"] as String? ?: "",
                                dreamAIQuestionAnswer = data["dreamAIQuestionAnswer"] as String? ?: "",
                                dreamAIMood = data["dreamAIMood"] as String? ?: "",
                                id = document.id,
                                uid = data["uid"] as String? ?: ""
                            )
                        } else {
                            null
                        }
                    }
                    trySend(dreams).isSuccess
                }
            }
            awaitClose { registration?.remove() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getDream(id: String): Resource<Dream> {
        return try {
            val document = getCollectionReferenceForDreams()?.document(id)?.get()?.await()
            if (document?.exists() == true) {
                val data = document.data
                if (data != null) {
                    val dream = Dream(
                        title = data["title"] as String,
                        content = data["content"] as String,
                        timestamp = data["timestamp"] as Long,
                        date = data["date"] as String,
                        sleepTime = data["sleepTime"] as String,
                        wakeTime = data["wakeTime"] as String,
                        AIResponse = data["airesponse"] as String,
                        isFavorite = data["favorite"] as Boolean,
                        isLucid = data["lucid"] as Boolean,
                        isNightmare = data["nightmare"] as Boolean,
                        isRecurring = data["recurring"] as Boolean,
                        falseAwakening = data["falseAwakening"] as Boolean? ?: false,
                        lucidityRating = (data["lucidityRating"] as Long?)?.toInt() ?: 1,
                        moodRating = (data["moodRating"] as Long?)?.toInt() ?: 1,
                        vividnessRating = (data["vividnessRating"] as Long?)?.toInt() ?: 1,
                        timeOfDay = data["timeOfDay"] as String? ?: "",
                        backgroundImage = (data["backgroundImage"] as Long?)?.toInt() ?: 0,
                        generatedImage = data["generatedImage"] as String?,
                        generatedDetails = data["generatedDetails"] as String? ?: "",
                        dreamQuestion = data["dreamQuestion"] as String? ?: "",
                        dreamAIStory = data["dreamAIStory"] as String? ?: "",
                        dreamAIAdvice = data["dreamAIAdvice"] as String? ?: "",
                        dreamAIQuestionAnswer = data["dreamAIQuestionAnswer"] as String? ?: "",
                        dreamAIMood = data["dreamAIMood"] as String? ?: "",
                        id = document.id,
                        uid = data["uid"] as String? ?: ""
                    )
                    Resource.Success(dream)
                } else {
                    Resource.Error("Error getting dream data")
                }
            } else {
                Resource.Error("Dream not found")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching dream")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun insertDream(dream: Dream): Resource<Unit> {
        return try {
            val existingDream = getDream(dream.id ?: "")
            var newDream = dream.copy(uid = userID(), id = "")

            suspend fun uploadImageIfNeeded(dream: Dream, oldImageUrl: String? = null): Dream {
                if (dream.generatedImage != null && !dream.generatedImage.startsWith("https://firebasestorage.googleapis.com/")) {
                    return withContext(Dispatchers.IO) {
                        val storageRef = FirebaseStorage.getInstance().reference.child("${userID()}/images/${UUID.randomUUID()}.jpg")

                        val imageUrl = URL(dream.generatedImage)
                        val imageBytes = imageUrl.readBytes()
                        val uploadTask = storageRef.putBytes(imageBytes)
                        uploadTask.await()
                        val downloadUrl = storageRef.downloadUrl.await()

                        // Delete the old image if it exists
                        if (oldImageUrl != null) {
                            val oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl)
                            oldImageRef.delete().await()
                        }

                        dream.copy(generatedImage = downloadUrl.toString())
                    }
                }
                return dream
            }

            if (existingDream is Resource.Success && existingDream.data != null) {
                // Dream already exists, update it
                val updatedDream = existingDream.data.copy(
                    title = dream.title,
                    content = dream.content,
                    timestamp = dream.timestamp,
                    date = dream.date,
                    sleepTime = dream.sleepTime,
                    wakeTime = dream.wakeTime,
                    AIResponse = dream.AIResponse,
                    isFavorite = dream.isFavorite,
                    isLucid = dream.isLucid,
                    isNightmare = dream.isNightmare,
                    isRecurring = dream.isRecurring,
                    falseAwakening = dream.falseAwakening,
                    lucidityRating = dream.lucidityRating,
                    moodRating = dream.moodRating,
                    vividnessRating = dream.vividnessRating,
                    timeOfDay = dream.timeOfDay,
                    backgroundImage = dream.backgroundImage,
                    generatedImage = dream.generatedImage,
                    generatedDetails = dream.generatedDetails,
                    dreamQuestion = dream.dreamQuestion,
                    dreamAIQuestionAnswer = dream.dreamAIQuestionAnswer,
                    dreamAIStory = dream.dreamAIStory,
                    dreamAIAdvice = dream.dreamAIAdvice,
                    dreamAIMood = dream.dreamAIMood,
                )
                val updatedDreamWithImage = uploadImageIfNeeded(updatedDream, existingDream.data.generatedImage)
                dreamsCollection?.document(updatedDreamWithImage.id ?: "")?.set(updatedDreamWithImage)?.await()
            } else {
                val newDreamRef = getCollectionReferenceForDreams()?.document()
                newDream = newDream.copy(id = newDreamRef?.id)
                val newDreamWithImage = uploadImageIfNeeded(newDream)
                newDreamRef?.set(newDreamWithImage)?.await()
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error inserting dream: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun deleteDream(id: String): Resource<Unit> {
        return try {
            val dream = getDream(id)
            if (dream is Resource.Success && dream.data != null && dream.data.generatedImage != null) {
                // Dream has a generated image, delete it from Firebase Storage
                val storageRef = storage.reference

                // Parse the image URL to get the image path
                val imageUrl = URL(dream.data.generatedImage)
                val imagePath = imageUrl.path.substringAfter("/o/").substringBefore("?")

                // Decode the image path
                val decodedImagePath =
                    withContext(Dispatchers.IO) {
                        URLDecoder.decode(imagePath, "UTF-8")
                    }

                val imageRef = storageRef.child(decodedImagePath)
                imageRef.delete().await()
            }
            // Delete dream from Firebase Firestore
            dreamsCollection?.document(id)?.delete()?.await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error deleting dream: ${e.message}")
        }
    }
    private fun getCollectionReferenceForDreams(): CollectionReference? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid?.let {
            db.collection(USERS).document(it).collection("my_dreams")
        }
    }
}