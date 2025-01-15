package org.ballistic.dreamjournalai.shared.dream_favorites.presentation.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.dream_favorites.domain.FavoriteEvent
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.util.OrderType

class DreamFavoriteScreenViewModel(
    private val dreamUseCases: DreamUseCases
) : ViewModel() {

    private val _dreamFavoriteScreenState = MutableStateFlow(DreamFavoriteScreenState())
    val dreamFavoriteScreenState: StateFlow<DreamFavoriteScreenState> = _dreamFavoriteScreenState

    private var recentlyDeletedDream: Dream? = null
    private var getDreamJob: Job? = null

    fun onEvent(event: FavoriteEvent) {
        when (event) {
            is FavoriteEvent.LoadDreams -> {
                viewModelScope.launch {
                    getDreams()
                }
            }

            is FavoriteEvent.DeleteDream -> {
                viewModelScope.launch {
                    dreamUseCases.deleteDream(event.dream)
                    recentlyDeletedDream = event.dream
                }
            }

            is FavoriteEvent.RestoreDream -> {
                viewModelScope.launch {
                    val dreamToRestore =
                        recentlyDeletedDream?.copy(generatedImage = "") ?: return@launch
                    dreamUseCases.addDream(dreamToRestore)
                    recentlyDeletedDream = null
                }
            }

            is FavoriteEvent.DreamToDelete -> {
                viewModelScope.launch {
                    _dreamFavoriteScreenState.value = dreamFavoriteScreenState.value.copy(
                        dreamToDelete = event.dream
                    )
                }
            }

            is FavoriteEvent.ToggleBottomDeleteCancelSheetState ->  {
                viewModelScope.launch {
                    _dreamFavoriteScreenState.value = dreamFavoriteScreenState.value.copy(
                        bottomDeleteCancelSheetState = event.bottomDeleteCancelSheetState
                    )
                }
            }
        }
    }

    private fun getDreams() {

        getDreamJob?.cancel()
        getDreamJob = dreamUseCases.getDreams(OrderType.Date)
            .onEach { dreams ->
                _dreamFavoriteScreenState.value = dreamFavoriteScreenState.value.copy(
                    dreams = dreams,
                    dreamFavoriteList = dreams.filter { it.isFavorite }
                )
            }
            .catch { exception ->
                //TODO
            }
            .launchIn(viewModelScope)
    }
}

@Stable
data class DreamFavoriteScreenState(
    val dreams: List<Dream> = emptyList(),
    val dreamFavoriteList: List<Dream> = emptyList(),
    val bottomDeleteCancelSheetState: Boolean = false,
    val dreamToDelete: Dream? = null,
    val isDreamDeleted: Boolean = false,
    val recentlyDeletedDream: Dream? = null,
)
