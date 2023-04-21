package org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.feature_dream.domain.util.OrderType
import org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen.DreamListEvent
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class DreamJournalListViewModel @Inject constructor(
    private val dreamUseCases: DreamUseCases
) : ViewModel() {

    private val _dreamJournalListState = MutableStateFlow(DreamJournalListState())
    val dreamJournalListState: StateFlow<DreamJournalListState> = _dreamJournalListState.asStateFlow()

    private var recentlyDeletedDream: Dream? = null

    private var getDreamJob: Job? = null

    init {
        getDreams(OrderType.Date)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: DreamListEvent) {
        when (event) {
            is DreamListEvent.DeleteDream -> {
                viewModelScope.launch {
                    dreamUseCases.deleteDream(event.dream)
                    recentlyDeletedDream = event.dream
                }
            }
            is DreamListEvent.ToggleOrderSection -> {
                _dreamJournalListState.value = _dreamJournalListState.value.copy(
                    isOrderSectionVisible = !dreamJournalListState.value.isOrderSectionVisible)
            }
            is DreamListEvent.RestoreDream -> {
                viewModelScope.launch {
                    val dreamToRestore = recentlyDeletedDream?.copy(generatedImage = null) ?: return@launch
                    dreamUseCases.addDream(dreamToRestore)
                    recentlyDeletedDream = null
                }
            }
            is DreamListEvent.Order -> {
                if (dreamJournalListState.value.orderType == event.orderType) {
                    return
                }
                getDreams(event.orderType)
            }
            is DreamListEvent.SearchDreams -> {
                _dreamJournalListState.value.searchedText.value = event.searchQuery
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDreams(orderType: OrderType) {
        getDreamJob?.cancel()
        getDreamJob = dreamUseCases.getDreams(orderType)
            .combine(dreamJournalListState.value.searchedText) { dreams, searchText ->
                if (searchText.isBlank()) {
                    dreams
                } else {
                    dreams.filter { it.doesMatchSearchQuery(searchText) }
                }
            }
            .onEach { filteredDreams ->
                _dreamJournalListState.value = dreamJournalListState.value.copy(
                    dreams = filteredDreams,
                    orderType = orderType,
                )
            }
            .launchIn(viewModelScope)
    }
}

data class DreamJournalListState(
    val dreams: List<Dream> = emptyList(),
    val orderType: OrderType = OrderType.Date,
    val isOrderSectionVisible: Boolean = false,
    val searchedText: MutableStateFlow<String> = MutableStateFlow(""),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSearching: Boolean = false,
)