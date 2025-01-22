package org.ballistic.dreamjournalai.shared.dream_journal_list.data


import co.touchlab.kermit.Logger
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.storage.Data
import dev.gitlive.firebase.storage.storage
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.ballistic.dreamjournalai.shared.core.Constants.USERS
import org.ballistic.dreamjournalai.shared.core.Resource
import org.ballistic.dreamjournalai.shared.core.domain.Flag
import org.ballistic.dreamjournalai.shared.core.util.decodeUrlPart
import org.ballistic.dreamjournalai.shared.core.util.downloadImageBytes
import org.ballistic.dreamjournalai.shared.core.util.toGitLiveData
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.repository.DreamRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private val logger = Logger.withTag("DreamRepositoryImpl")

class DreamRepositoryImpl(
    private val db: FirebaseFirestore
) :
    DreamRepository {

    private val dreamsCollection
        get() = getCollectionReferenceForDreams()

    private val flagsCollection: CollectionReference
        get() = db.collection("flags")

    private var currentDreamId: String = ""

    // Add this function to get the current user's UID
    private fun userID(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override fun getDreams(): Flow<List<Dream>> {
        val collection = getCollectionReferenceForDreams()
            ?: return flowOf(emptyList())

        return collection.snapshots().map { querySnapshot ->
            querySnapshot.documents.mapNotNull { doc ->
                doc.toDream()
            }
        }
    }

    private fun DocumentSnapshot.toDream(): Dream? {
        val dream = data<Dream>()

        return dream.copy(
            id = this.id,
            uid = this.reference.parent.parent?.id ?: ""
        )
    }

    override suspend fun getDream(id: String): Resource<Dream> {
        return try {
            // 1) Get the doc reference from your "my_dreams" subcollection
            val docRef = getCollectionReferenceForDreams()?.document(id)
                ?: return Resource.Error("User not logged in or reference is null")

            // 2) Fetch the snapshot (GitLive’s .get() is already a suspend function)
            val snapshot = docRef.get()

            // 3) Check if the document exists, parse the data
            if (snapshot.exists) {

                val dream = snapshot.getDream()  // see extension below

                Resource.Success(dream)
            } else {
                Resource.Error("Dream not found")
            }

        } catch (e: Exception) {
            Resource.Error("Error fetching dream: ${e.message}")
        }
    }

    private fun DocumentSnapshot.getDream(): Dream? {
        val dream = data<Dream>()

        return dream
    }



    override suspend fun getCurrentDreamId(): Resource<String> {
        return Resource.Success(currentDreamId)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun insertDream(dream: Dream): Resource<Unit> {
        logger.d { "insertDream: Attempting to insert/update dream with ID: ${dream.id}" }
        return try {
            val existingDream = if (!dream.id.isNullOrEmpty()) getDream(dream.id) else null
            logger.d { "insertDream: Existing dream: $existingDream" }

            suspend fun uploadImageIfNeeded(dreamToUpdate: Dream, oldImageUrl: String = ""): Dream {
                if (dreamToUpdate.generatedImage.isNotBlank() &&
                    !dreamToUpdate.generatedImage.startsWith("https://firebasestorage.googleapis.com/")
                ) {
                    logger.d { "uploadImageIfNeeded: Uploading new image for dream" }

                    return withContext(Dispatchers.IO) {
                        val randomFileName = "${Uuid.random()}.jpg"
                        logger.d { "uploadImageIfNeeded: Generated random filename: $randomFileName" }

                        val storageRef = Firebase.storage
                            .reference(location = "${userID()}/images/$randomFileName")

                        val imageBytes = downloadImageBytes(dreamToUpdate.generatedImage)
                        logger.d { "uploadImageIfNeeded: Downloaded image bytes (${imageBytes.size} bytes)" }

                        val gitLiveData: Data = imageBytes.toGitLiveData()
                        storageRef.putData(gitLiveData)
                        logger.d { "uploadImageIfNeeded: Uploaded new image to Firebase Storage" }

                        val downloadUrl = storageRef.getDownloadUrl()
                        logger.d { "uploadImageIfNeeded: New image download URL: $downloadUrl" }

                        // Parse the relative path from the oldImageUrl
                        if (oldImageUrl.isNotBlank()) {
                            val relativePath = parseFirebaseStoragePath(oldImageUrl)
                            logger.d { "uploadImageIfNeeded: Deleting old image at relative path: $relativePath" }

                            val oldRef = Firebase.storage.reference(relativePath)
                            oldRef.delete()
                        }

                        dreamToUpdate.copy(generatedImage = downloadUrl.toString())
                    }
                }
                return dreamToUpdate
            }

            if (existingDream is Resource.Success && existingDream.data != null) {
                logger.d { "insertDream: Dream exists, updating" }

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
                    id = existingDream.data.id,
                    uid = existingDream.data.uid
                )
                logger.d { "insertDream: Updated dream details: $updatedDream" }

                val updatedWithImage = uploadImageIfNeeded(updatedDream, existingDream.data.generatedImage)
                logger.d { "insertDream: Updated dream with new image details: $updatedWithImage" }

                val docRef = getCollectionReferenceForDreams()?.document(updatedWithImage.id ?: "")
                logger.d { "insertDream: Firestore reference for update: ${docRef?.path}" }
                docRef?.set(updatedWithImage)
            } else {
                logger.d { "insertDream: Creating new dream" }

                val newDreamWithImage = uploadImageIfNeeded(dream)
                logger.d { "insertDream: New dream with image: $newDreamWithImage" }

                val docRef = getCollectionReferenceForDreams()?.document(dream.id ?: "")
                logger.d { "insertDream: Firestore reference for new dream: ${docRef?.path}" }
                docRef?.set(newDreamWithImage)
            }

            logger.d { "insertDream: Dream successfully inserted/updated" }
            Resource.Success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "insertDream: Error inserting dream" }
            Resource.Error("Error inserting dream: ${e.message}")
        }
    }

    fun parseFirebaseStoragePath(fullUrl: String): String {
        val regex = """https://firebasestorage.googleapis.com/v0/b/[^/]+/o/(.*)\?""".toRegex()
        val match = regex.find(fullUrl)
        return match?.groupValues?.get(1)?.replace("%2F", "/") // Decode URL-encoded path
            ?: throw IllegalArgumentException("Invalid Firebase Storage URL: $fullUrl")
    }


    override suspend fun deleteDream(id: String): Resource<Unit> {
        return try {
            // 1) Fetch the dream first
            val dreamResult = getDream(id)
            if (dreamResult is Resource.Success && dreamResult.data != null) {
                val dreamData = dreamResult.data

                // 2) If dream has a generated image, remove it from Firebase Storage
                if (dreamData.generatedImage.isNotBlank()) {
                    // Example URL:
                    // "https://firebasestorage.googleapis.com/v0/b/<bucket>/o/users%2Fuid%2Fimages%2Ffilename.jpg?alt=media..."
                    val imageUrl = dreamData.generatedImage

                    // 2a) Parse with Ktor's Url (works in KMM)
                    val ktorUrl = Url(imageUrl)
                    // e.g. ktorUrl.encodedPath might be: "/v0/b/<bucket>/o/users%2Fuid%2Fimages%2Ffilename.jpg"
                    val pathPortion = ktorUrl.encodedPath.substringAfter("/o/").substringBefore("?")
                    // e.g. "users%2Fuid%2Fimages%2Ffilename.jpg"

                    // 2b) Decode the path. Ktor has decodeURLPart(...) for URL-encoded strings
                    val decodedPath = decodeUrlPart(pathPortion)
                    // e.g. "users/uid/images/filename.jpg"

                    // 3) Build a reference in GitLive
                    val imageRef = Firebase.storage.reference(decodedPath)

                    // 4) Delete the file (suspend call in GitLive)
                    imageRef.delete()
                }
            }

            // 5) Remove the dream doc from Firestore
            dreamsCollection?.document(id)?.delete()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error deleting dream: ${e.message}")
        }
    }

    override suspend fun flagDream(
        dreamId: String?,
        imageAddress: String?
    ): Resource<Unit> {
        if (dreamId.isNullOrBlank() && imageAddress.isNullOrBlank()) {
            return Resource.Error("Either dreamId or imageAddress must be provided to flag content.")
        }

        return try {
            val flag = Flag(
                dreamId = dreamId?.takeIf { it.isNotBlank() } ?: "",
                imageAddress = imageAddress?.takeIf { it.isNotBlank() } ?: ""
            )

            flagsCollection.add(flag)

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error flagging content: ${e.message}")
        }
    }

    private fun getCollectionReferenceForDreams(): CollectionReference? {
        val user = Firebase.auth.currentUser

        return user?.uid?.let { uid ->
            db.collection(USERS) // "USERS" is your top-level collection
                .document(uid)   // user's document
                .collection("my_dreams")  // the "my_dreams" subcollection
        }
    }
}