package org.ballistic.dreamjournalai.shared.dream_nightmares.domain

import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream

sealed class NightmareEvent {
    data object LoadDreams : NightmareEvent()
    data class DeleteDream(val dream: Dream) : NightmareEvent()
    data class DreamToDelete(val dream: Dream) : NightmareEvent()
    data class ToggleBottomDeleteCancelSheetState(val bottomDeleteCancelSheetState: Boolean) : NightmareEvent()
    data object RestoreDream : NightmareEvent()
}