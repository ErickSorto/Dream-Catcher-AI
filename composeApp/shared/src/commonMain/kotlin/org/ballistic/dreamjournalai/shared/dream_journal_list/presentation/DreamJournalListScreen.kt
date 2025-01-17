package org.ballistic.dreamjournalai.shared.dream_journal_list.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.dream_main.domain.MainScreenEvent
import org.ballistic.dreamjournalai.shared.core.components.ActionBottomSheet
import org.ballistic.dreamjournalai.shared.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.shared.core.util.formatCustomDate
import org.ballistic.dreamjournalai.shared.core.util.parseCustomDate
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.DreamListEvent
import org.ballistic.dreamjournalai.shared.dream_journal_list.presentation.components.DateHeader
import org.ballistic.dreamjournalai.shared.dream_journal_list.presentation.components.DreamItem
import org.ballistic.dreamjournalai.shared.dream_journal_list.presentation.components.DreamListScreenTopBar
import org.ballistic.dreamjournalai.shared.dream_journal_list.presentation.viewmodel.DreamJournalListState
import org.ballistic.dreamjournalai.shared.dream_main.presentation.viewmodel.MainScreenViewModelState


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DreamJournalListScreen(
    mainScreenViewModelState: MainScreenViewModelState,
    searchTextFieldState: TextFieldState,
    dreamJournalListState: DreamJournalListState,
    bottomPaddingValue: Dp,
    onMainEvent: (MainScreenEvent) -> Unit = {},
    onDreamListEvent: (DreamListEvent) -> Unit = {},
    onNavigateToDream: (dreamID: String?, backgroundID: Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    val dreamState by rememberUpdatedState(dreamJournalListState.dreams)
    val dreamCount = dreamJournalListState.dreams.size
    val isRecentlySaved = mainScreenViewModelState.isDreamRecentlySaved
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onDreamListEvent(DreamListEvent.FetchDreams)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        onMainEvent(MainScreenEvent.SetBottomBarVisibilityState(true))
        onMainEvent(MainScreenEvent.SetFloatingActionButtonState(true))
        onMainEvent(MainScreenEvent.SetDrawerState(true))
    }

//    // The key part: trigger in-app review from the VM if conditions are met
//    LaunchedEffect(isRecentlySaved) {
//        if (dreamCount >= 2 && isRecentlySaved) {
//            delay(1000)
//            // Dispatch the "TriggerReview" event to the VM
//            onDreamListEvent(DreamListEvent.TriggerReview)
//        }
//    }

    if (dreamJournalListState.bottomDeleteCancelSheetState) {
        ActionBottomSheet(
            modifier = Modifier.padding(),
            title = "Delete this Dream?",
            message = "Are you sure you want to delete this dream?",
            buttonText = "Delete",
            onClick = {
                onDreamListEvent(DreamListEvent.ToggleBottomDeleteCancelSheetState(false))
                onDreamListEvent(DreamListEvent.DeleteDream(dream = dreamJournalListState.chosenDreamToDelete!!))
                scope.launch {
                    val result =
                        mainScreenViewModelState.scaffoldState.snackBarHostState.value.showSnackbar(
                            message = "Dream deleted",
                            actionLabel = "Undo",
                            duration = SnackbarDuration.Long
                        )

                    if (result == SnackbarResult.ActionPerformed) {
                        onDreamListEvent(DreamListEvent.RestoreDream)
                    }
                }
            },
            onClickOutside = {
                onDreamListEvent(DreamListEvent.ToggleBottomDeleteCancelSheetState(false))
            }
        )
    }

    Scaffold(
        topBar = {
            DreamListScreenTopBar(
                dreamJournalListState = dreamJournalListState,
                mainScreenViewModelState = mainScreenViewModelState,
                searchTextFieldState = searchTextFieldState,
                onDreamListEvent = onDreamListEvent
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->

        val topPadding = innerPadding.calculateTopPadding()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .dynamicBottomNavigationPadding()
                .padding(top = topPadding, bottom = bottomPaddingValue),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            // Step 1: Parse and Sort Dreams
            val sortedGroupedDreams = dreamJournalListState.dreams
                .mapNotNull { dream ->
                    try {
                        val parsedDate = parseCustomDate(dream.date)
                        Pair(parsedDate, dream)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
                .sortedWith(
                    compareByDescending<Pair<LocalDate, Dream>> { it.first }
                        .thenByDescending { it.second.timestamp }
                )
                .groupBy { it.first }

            // Step 2: Iterate Through Groups
            sortedGroupedDreams.forEach { (date, dreams) ->

                // Sticky Header for the Date
                stickyHeader {
                    DateHeader(dateString = formatCustomDate(date))
                }

                // Items for Each Dream in the Date Group
                items(dreams) { (_, dream) ->
                    DreamItem(
                        dream = dream,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .padding(horizontal = 12.dp),
                        scope = scope,
                        onClick = {
                            onDreamListEvent(DreamListEvent.TriggerVibration)
                            onNavigateToDream(dream.id, dream.backgroundImage)
                        },
                        onDeleteClick = {
                            onDreamListEvent(DreamListEvent.TriggerVibration)
                            onDreamListEvent(
                                DreamListEvent.DreamToDelete(dream)
                            )
                            onDreamListEvent(
                                DreamListEvent.ToggleBottomDeleteCancelSheetState(true)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}