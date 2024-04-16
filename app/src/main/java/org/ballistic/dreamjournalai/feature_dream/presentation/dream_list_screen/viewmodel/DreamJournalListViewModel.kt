package org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen.viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.feature_dream.domain.util.OrderType
import org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen.DreamListEvent
import javax.inject.Inject


@HiltViewModel
class DreamJournalListViewModel @Inject constructor(
    private val dreamUseCases: DreamUseCases
) : ViewModel() {

    private val _dreamJournalListState = MutableStateFlow(DreamJournalListState())
    val dreamJournalListState: StateFlow<DreamJournalListState> = _dreamJournalListState.asStateFlow()

    @OptIn(ExperimentalFoundationApi::class)
    private val _searchTextFieldState = MutableStateFlow(TextFieldState())

    @OptIn(ExperimentalFoundationApi::class)
    val searchTextFieldState: StateFlow<TextFieldState> = _searchTextFieldState.asStateFlow()

    private var recentlyDeletedDream: Dream? = null

    private var getDreamJob: Job? = null

    init {
        getDreams(OrderType.Date)
    }

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
            is DreamListEvent.SetSearchingState -> {
                _dreamJournalListState.value = _dreamJournalListState.value.copy(
                    isSearching = event.state
                )
            }
        }
    }


    @OptIn(ExperimentalFoundationApi::class)
    private fun getDreams(orderType: OrderType) {
        getDreamJob?.cancel()
        getDreamJob = dreamUseCases.getDreams(orderType)
            .combine(searchTextFieldState.value.textAsFlow()) { dreams, searchText ->
                if (searchText.isBlank()) {
                    dreams
                } else {
                    dreams.filter { it.doesMatchSearchQuery(searchText.toString()) }
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
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSearching: Boolean = false,
)