package org.ballistic.dreamjournalai.shared.dream_tools.data


import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.ballistic.dreamjournalai.shared.core.Constants
import org.ballistic.dreamjournalai.shared.core.Resource
import org.ballistic.dreamjournalai.shared.dream_tools.domain.MassInterpretationRepository
import org.ballistic.dreamjournalai.shared.dream_tools.domain.model.MassInterpretation

class MassInterpretationRepositoryImpl(
    private val db: FirebaseFirestore
) : MassInterpretationRepository {
    // 1) In GitLive, retrieve the current user from `Firebase.auth`
    private val currentUser get() = Firebase.auth.currentUser

    // 2) Build the collection reference: "USERS/{uid}/my_mass_interpretations"
    private fun getCollectionReferenceForMassInterpretations() = currentUser?.uid?.let { uid ->
        db.collection(Constants.USERS).document(uid).collection("my_mass_interpretations")
    }

    // 3) Add (or update) a MassInterpretation in Firestore
    override suspend fun addInterpretation(massInterpretation: MassInterpretation): Resource<Unit> {
        return try {
            val collectionRef = getCollectionReferenceForMassInterpretations()
                ?: return Resource.Error("User is not logged in or collection is null")

            // If massInterpretation.id is empty => create a new doc ref; otherwise use existing doc ref
            val docRef = if (massInterpretation.id.isNullOrEmpty()) {
                // doc() with no arg auto-generates an ID
                collectionRef.document
            } else {
                collectionRef.document(massInterpretation.id)
            }

            // Ensure the local object’s "id" matches docRef.id
            val updatedInterpretation = massInterpretation.copy(id = docRef.id)

            // In GitLive, set(...) is already a suspend function
            docRef.set(updatedInterpretation)

            Resource.Success(Unit)

        } catch (e: FirebaseFirestoreException) {
            Resource.Error(e.message ?: "Unknown error occurred")
        } catch (e: Exception) {
            // If you want to catch all exceptions
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override fun getInterpretations(): Flow<List<MassInterpretation>> {
        val collectionRef = getCollectionReferenceForMassInterpretations()
            ?: return flowOf(emptyList())

        return collectionRef.snapshots().map { querySnapshot ->
            querySnapshot.documents.mapNotNull { document ->
                // 'data' is a Map<String, Any> or null
                val data = document.data<Map<String, Any>>() ?: return@mapNotNull null

                MassInterpretation(
                    interpretation = data["interpretation"] as? String ?: "",
                    listOfDreamIDs = (data["listOfDreamIDs"] as? List<String?>) ?: emptyList(),
                    date = data["date"] as? Long ?: 0L,
                    model = data["model"] as? String ?: "",
                    id = document.id // Or if you want to store the Firestore doc ID here
                )
            }
        }
    }

    override suspend fun removeInterpretation(massInterpretation: MassInterpretation): Resource<Unit> {
        return try {
            val collectionReference = getCollectionReferenceForMassInterpretations()
            if (massInterpretation.id.isNullOrEmpty()) {
                return Resource.Error("Cannot remove interpretation without an ID")
            }
            collectionReference?.document(massInterpretation.id)?.delete()
            Resource.Success()
        } catch (e: FirebaseFirestoreException) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }
}

