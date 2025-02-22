package org.ballistic.dreamjournalai.dream_journal_list.presentation

import android.app.Activity
import android.os.Vibrator
import android.util.Log
import androidx.activity.compose.ReportDrawn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.ballistic.dreamjournalai.core.components.ActionBottomSheet
import org.ballistic.dreamjournalai.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.core.util.formatCustomDate
import org.ballistic.dreamjournalai.core.util.parseCustomDate
import org.ballistic.dreamjournalai.dream_journal_list.domain.DreamListEvent
import org.ballistic.dreamjournalai.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.dream_journal_list.presentation.components.DateHeader
import org.ballistic.dreamjournalai.dream_journal_list.presentation.components.DreamItem
import org.ballistic.dreamjournalai.dream_journal_list.presentation.components.DreamListScreenTopBar
import org.ballistic.dreamjournalai.dream_journal_list.presentation.viewmodel.DreamJournalListState
import org.ballistic.dreamjournalai.dream_main.domain.MainScreenEvent
import org.ballistic.dreamjournalai.dream_main.presentation.viewmodel.MainScreenViewModelState


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
    val context = LocalContext.current
    val activity = context as Activity
    val vibrator = context.getSystemService(Vibrator::class.java)

    val dreamState by rememberUpdatedState(dreamJournalListState.dreams)

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

    val manager = ReviewManagerFactory.create(context)
    LaunchedEffect(dreamState, mainScreenViewModelState) {
        Log.d("DreamJournalListScreen", "Dreams: ${dreamJournalListState.dreams.size}")
        Log.d(
            "DreamJournalListScreen",
            "Recently Saved: ${mainScreenViewModelState.isDreamRecentlySaved}"
        )
        if (dreamJournalListState.dreams.size >= 3 && mainScreenViewModelState.isDreamRecentlySaved) {
            delay(1000)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                Log.d("DreamJournalListScreen", "Request: $task")
                // We got the ReviewInfo object
                val reviewInfo = task.result
                Log.d("DreamJournalListScreen", "ReviewInfo: $reviewInfo")
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    onMainEvent(MainScreenEvent.SetDreamRecentlySaved(false))
                }
            }
        }
    }

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
                vibrator = vibrator,
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
                        Log.e("DreamJournalListScreen", "Invalid date format for dream id ${dream.id}: ${dream.date}")
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
                        vibrator = vibrator,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .padding(horizontal = 12.dp),
                        scope = scope,
                        onClick = {
                            onNavigateToDream(dream.id, dream.backgroundImage)
                        },
                        onDeleteClick = {
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
        ReportDrawn()
    }
}