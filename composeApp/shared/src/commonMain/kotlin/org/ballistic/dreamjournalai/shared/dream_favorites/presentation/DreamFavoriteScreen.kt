package org.ballistic.dreamjournalai.shared.dream_favorites.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.dream_favorites.domain.FavoriteEvent
import org.ballistic.dreamjournalai.shared.dream_journal_list.presentation.components.DateHeader
import org.ballistic.dreamjournalai.shared.dream_journal_list.presentation.components.DreamItem
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.DarkBlue
import org.ballistic.dreamjournalai.shared.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.shared.dream_favorites.presentation.components.DreamFavoriteScreenTopBar
import org.ballistic.dreamjournalai.shared.dream_main.presentation.viewmodel.MainScreenViewModelState
import org.ballistic.dreamjournalai.shared.dream_favorites.presentation.viewmodel.DreamFavoriteScreenState
import org.ballistic.dreamjournalai.shared.core.components.ActionBottomSheet
import org.ballistic.dreamjournalai.shared.core.components.TypewriterText
import org.ballistic.dreamjournalai.shared.core.util.formatCustomDate
import org.ballistic.dreamjournalai.shared.core.util.parseCustomDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DreamFavoriteScreen(
    dreamFavoriteScreenState: DreamFavoriteScreenState,
    mainScreenViewModelState: MainScreenViewModelState,
    bottomPaddingValue: Dp,
    onEvent: (FavoriteEvent) -> Unit,
    onNavigateToDream: (dreamID: String?, backgroundID: Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    // Load dreams once
    LaunchedEffect(Unit) {
        onEvent(FavoriteEvent.LoadDreams)
    }

    // Bottom sheet if deleting a dream
    if (dreamFavoriteScreenState.bottomDeleteCancelSheetState) {
        ActionBottomSheet(
            title = "Delete this Dream?",
            message = "Are you sure you want to delete this dream?",
            buttonText = "Delete",
            onClick = {
                scope.launch {
                    val snackResult =
                        mainScreenViewModelState.scaffoldState.snackBarHostState.value.showSnackbar(
                            message = "Dream deleted",
                            actionLabel = "Undo",
                            duration = SnackbarDuration.Long
                        )

                    mainScreenViewModelState.scaffoldState.snackBarHostState.value.currentSnackbarData?.dismiss()

                    if (snackResult == SnackbarResult.ActionPerformed) {
                        onEvent(FavoriteEvent.RestoreDream)
                    } else {
                        dreamFavoriteScreenState.dreamToDelete?.let { FavoriteEvent.DeleteDream(it) }
                            ?.let { onEvent(it) }
                    }
                }
            },
            onClickOutside = {
                onEvent(FavoriteEvent.ToggleBottomDeleteCancelSheetState(false))
            },
        )
    }

    Scaffold(
        topBar = {
            DreamFavoriteScreenTopBar(mainScreenViewModelState = mainScreenViewModelState)
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        if (dreamFavoriteScreenState.dreamFavoriteList.isEmpty()) {
            // Show an empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = bottomPaddingValue
                    )
                    .dynamicBottomNavigationPadding(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .background(
                            color = DarkBlue.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    TypewriterText(
                        text = "You currently have no favorites. Add some!",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        // Otherwise, show the favorites list
        LazyColumn(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = bottomPaddingValue
                )
                .dynamicBottomNavigationPadding()
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // 1) Parse and sort by LocalDate, descending, like DreamJournalListScreen
            val sortedGroupedDreams = dreamFavoriteScreenState.dreamFavoriteList
                .mapNotNull { dream ->
                    // parseCustomDate(...) might throw an exception if invalid
                    // or return null if you wrap it in a try/catch
                    try {
                        val parsedDate = parseCustomDate(dream.date)
                        // Return a pair (LocalDate, Dream)
                        parsedDate to dream
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
                // Sort descending by date, then descending by dream.timestamp
                .sortedWith(
                    compareByDescending<Pair<LocalDate, Dream>> { it.first }
                        .thenByDescending { it.second.timestamp }
                )
                // Group by the parsed LocalDate
                .groupBy { it.first }

            // 2) For each date group, display a sticky header + items
            sortedGroupedDreams.forEach { (date, dreams) ->
                stickyHeader {
                    DateHeader(dateString = formatCustomDate(date))
                }

                items(dreams) { (_, dream) ->
                    DreamItem(
                        dream = dream,
                        scope = scope,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .padding(horizontal = 12.dp),
                        onClick = {
                            onNavigateToDream(dream.id, dream.backgroundImage)
                        },
                        onDeleteClick = {
                            onEvent(FavoriteEvent.DreamToDelete(dream))
                            onEvent(FavoriteEvent.ToggleBottomDeleteCancelSheetState(true))
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}