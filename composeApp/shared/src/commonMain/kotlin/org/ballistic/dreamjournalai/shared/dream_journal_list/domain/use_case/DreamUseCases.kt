package org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case

data class DreamUseCases(
    val getDreams: GetDreams,
    val deleteDream: DeleteDream,
    val addDream: AddDream,
    val getDream: GetDream,
    val flagDream: FlagDream,
    val getCurrentDreamId: GetCurrentDreamID
)
