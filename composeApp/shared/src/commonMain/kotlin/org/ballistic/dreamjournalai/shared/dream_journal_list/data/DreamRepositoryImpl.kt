package org.ballistic.dreamjournalai.shared.dream_journal_list.data


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

        return dream.copy(id = this.id)
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
        println("DreamInsert: Attempting to insert/update dream with ID: ${dream.id}")

        return try {
            // 1) Check if dream with this ID already exists
            val existingDream = if (!dream.id.isNullOrEmpty()) getDream(dream.id) else null

            // Helper to upload the image if needed (and delete old if present)
            suspend fun uploadImageIfNeeded(dreamToUpdate: Dream, oldImageUrl: String = ""): Dream {
                // If the new "generatedImage" is a local or raw URL, and not a direct link to Firebase
                if (dreamToUpdate.generatedImage.isNotBlank() &&
                    !dreamToUpdate.generatedImage.startsWith("https://firebasestorage.googleapis.com/")
                ) {
                    println("DreamInsert: Uploading new image for dream")

                    // We'll do all the heavy lifting in IO context
                    return withContext(Dispatchers.IO) {
                        val randomFileName = "${Uuid.random()}.jpg"

                        // 1) Build a reference: e.g. "users/{uid}/images/<random>.jpg"
                        // Using GitLive's Firebase.storage
                        val storageRef = Firebase.storage
                            .reference(location = "${userID()}/images/$randomFileName")

                        // 2) Download image bytes from the original URL
                        val imageBytes = downloadImageBytes(dreamToUpdate.generatedImage)

                        val gitLiveData: Data = imageBytes.toGitLiveData()

                        // 3) Upload the image bytes to Firebase Storage
                        storageRef.putData(gitLiveData)

                        // 4) Obtain the download URL
                        val downloadUrl = storageRef.getDownloadUrl()

                        // 5) Delete the old image if it exists
                        if (oldImageUrl.isNotBlank()) {
                            val oldRef = Firebase.storage.reference(oldImageUrl)
                            oldRef.delete()
                        }

                        println("DreamInsert: Image uploaded and old image deleted")

                        // Return a copy of dream with the new downloadUrl
                        dreamToUpdate.copy(generatedImage = downloadUrl.toString())
                    }
                }
                return dreamToUpdate
            }

            // 2) If dream already exists, we update it. Otherwise, we create a new doc.
            if (existingDream is Resource.Success && existingDream.data != null) {
                println("DreamInsert: Dream exists, updating")

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
                    dreamAIMood = dream.dreamAIMood
                )

                // 2a) Possibly upload the new image & remove the old
                val updatedWithImage = uploadImageIfNeeded(
                    updatedDream,
                    existingDream.data.generatedImage
                )

                // 2b) Update doc in Firestore
                val docRef = getCollectionReferenceForDreams()
                    ?.document(updatedWithImage.id ?: "")
                docRef?.set(updatedWithImage) // GitLive => suspend

            } else {
                println("DreamInsert: Creating new dream")

                // 3a) Possibly upload image
                val newDreamWithImage = uploadImageIfNeeded(dream)

                // 3b) Insert doc in Firestore
                val docRef = getCollectionReferenceForDreams()?.document(dream.id ?: "")
                docRef?.set(newDreamWithImage)
            }

            println("DreamInsert: Dream successfully inserted/updated")
            Resource.Success(Unit)

        } catch (e: Exception) {
            println("DreamInsert: Error inserting dream: ${e.message}")
            e.printStackTrace()
            Resource.Error("Error inserting dream: ${e.message}")
        }
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