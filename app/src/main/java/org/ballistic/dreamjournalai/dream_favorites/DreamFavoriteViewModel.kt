package org.ballistic.dreamjournalai.dream_favorites

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.dream_favorites.presentation.FavoriteEvent
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.feature_dream.domain.util.OrderType
import javax.inject.Inject

@HiltViewModel
class DreamFavoriteScreenViewModel @Inject constructor(
    private val dreamUseCases: DreamUseCases,
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
        // Logging the start of the function
        Log.d("getDreams", "Starting to fetch dreams")

        getDreamJob?.cancel()
        getDreamJob = dreamUseCases.getDreams(OrderType.Date)
            .onEach { dreams ->
                // Logging when dreams are received
                Log.d("getDreams", "Received dreams: ${dreams.size} items")

                _dreamFavoriteScreenState.value = dreamFavoriteScreenState.value.copy(
                    dreams = dreams,
                    dreamFavoriteList = dreams.filter { it.isFavorite }
                )
                Log.d("getDreams", "Dreams updated in the screen state")
            }
            .catch { exception ->
                // Logging in case of an error
                Log.e("getDreams", "Error fetching dreams", exception)
            }
            .launchIn(viewModelScope)


        // Logging the event trigger
        Log.d("getDreams", "LoadStatistics event triggered")
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