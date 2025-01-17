package org.ballistic.dreamjournalai.shared.dream_tools.presentation.interpret_dreams_screen.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.core.components.ActionBottomSheet
import org.ballistic.dreamjournalai.shared.dream_tools.presentation.interpret_dreams_screen.viewmodel.InterpretDreamsScreenState
import org.ballistic.dreamjournalai.shared.dream_tools.domain.event.InterpretDreamsToolEvent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MassInterpretationHistoryPage(
    interpretDreamsScreenState: InterpretDreamsScreenState,
    snackbarHostState: SnackbarHostState,
    pagerState: PagerState,
    scope: CoroutineScope,
    onEvent: (InterpretDreamsToolEvent) -> Unit
) {

    if (interpretDreamsScreenState.bottomDeleteCancelSheetState) {
        ActionBottomSheet(
            modifier = Modifier.fillMaxWidth(),
            title = "Delete this Interpretation?",
            message = "Are you sure you want to delete this interpretation?",
            buttonText = "Delete",
            onClick = {
                onEvent(InterpretDreamsToolEvent.TriggerVibration)
                onEvent(InterpretDreamsToolEvent.ToggleBottomDeleteCancelSheetState(false))
                onEvent(InterpretDreamsToolEvent.DeleteMassInterpretation(interpretDreamsScreenState.chosenMassInterpretation))
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Interpretation deleted",
                        "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                }
            },
            onClickOutside = {
                onEvent(InterpretDreamsToolEvent.ToggleBottomDeleteCancelSheetState(false))
            }
        )
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            for (interpretation in interpretDreamsScreenState.massMassInterpretations) {
                MassInterpretationItem(
                    interpretDreamsScreenState = interpretDreamsScreenState,
                    pagerState = pagerState,
                    massInterpretation = interpretation,
                    onEvent = { onEvent(it) },
                    scope = scope
                )
            }
        }
    }
}