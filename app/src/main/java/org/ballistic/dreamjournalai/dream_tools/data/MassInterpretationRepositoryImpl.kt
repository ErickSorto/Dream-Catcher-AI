package org.ballistic.dreamjournalai.dream_tools.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.ballistic.dreamjournalai.core.Constants
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.dream_tools.domain.MassInterpretationRepository
import org.ballistic.dreamjournalai.dream_tools.domain.model.MassInterpretation
import javax.inject.Singleton

@Singleton
class MassInterpretationRepositoryImpl(
    private val db: FirebaseFirestore
) : MassInterpretationRepository {
    private val userID: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    private fun getCollectionReferenceForMassInterpretations(): CollectionReference? {
        return userID?.let { db.collection(Constants.USERS).document(it).collection("my_mass_interpretations") }
    }

    override suspend fun addInterpretation(massInterpretation: MassInterpretation): Resource<Unit> {
        return try {
            val collectionReference = getCollectionReferenceForMassInterpretations()
            // Use the existing ID if it is provided, otherwise create a new document which will generate a new ID.
            val documentReference = if (massInterpretation.id.isNullOrEmpty()) {
                collectionReference?.document()
            } else {
                collectionReference?.document(massInterpretation.id)
            }

            // Prepare the interpretation object with the correct ID (new or existing).
            // This ensures that the ID in the object is consistent with the Firestore document ID.
            val updatedInterpretation = massInterpretation.copy(id = documentReference?.id)

            // Set the updated interpretation object in Firestore.
            // Since Firestore set method overrides the existing document, this meets the requirement.
            documentReference?.set(updatedInterpretation)?.await()
            Resource.Success()
        } catch (e: FirebaseFirestoreException) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override fun getInterpretations(): Flow<List<MassInterpretation>> {
        return callbackFlow {
            val collectionReference = getCollectionReferenceForMassInterpretations()
            val listenerRegistration = collectionReference?.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Close the flow with error
                } else if (snapshot != null && !snapshot.isEmpty) {
                    val massInterpretations = snapshot.documents.mapNotNull { document ->
                        document.toObject(MassInterpretation::class.java)?.copy(id = document.id)
                    }
                    trySend(massInterpretations).isSuccess // Emit the list of interpretations
                }
            }
            awaitClose { listenerRegistration?.remove() }
        }
    }

    override suspend fun removeInterpretation(massInterpretation: MassInterpretation): Resource<Unit> {
        return try {
            val collectionReference = getCollectionReferenceForMassInterpretations()
            if (massInterpretation.id.isNullOrEmpty()) {
                return Resource.Error("Cannot remove interpretation without an ID")
            }
            collectionReference?.document(massInterpretation.id)?.delete()?.await()
            Resource.Success()
        } catch (e: FirebaseFirestoreException) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }
}

