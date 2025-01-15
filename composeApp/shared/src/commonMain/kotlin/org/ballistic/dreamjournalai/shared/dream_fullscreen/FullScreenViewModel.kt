package org.ballistic.dreamjournalai.shared.dream_fullscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.core.Resource
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.repository.DreamRepository

class FullScreenViewModel(
    private val dreamRepository: DreamRepository,
) : ViewModel() {
    val _fullScreenViewModelState = MutableStateFlow(FullScreenViewModelState())
    val fullScreenViewModelState: StateFlow<FullScreenViewModelState> = _fullScreenViewModelState.asStateFlow()

    fun onEvent(event: FullScreenEvent) {
        when (event) {
            is FullScreenEvent.Flag -> {
                viewModelScope.launch {
                    val result = dreamRepository.flagDream(null, event.imagePath)
                    if (result is Resource.Success<*>) {
                        _fullScreenViewModelState.update {
                            it.copy(flagSuccess = true)
                        }
                        event.onSuccessEvent()
                    }
                }
            }
        }
    }
}

data class FullScreenViewModelState(
    val flagSuccess: Boolean = false,
)