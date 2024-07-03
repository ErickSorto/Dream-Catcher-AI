package org.ballistic.dreamjournalai.feature_dream.domain.use_case

data class DreamUseCases(
    val getDreams: GetDreams,
    val deleteDream: DeleteDream,
    val addDream: AddDream,
    val getDream: GetDream,
    val getCurrentDreamId: GetCurrentDreamID
)
