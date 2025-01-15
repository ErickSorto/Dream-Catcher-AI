package org.ballistic.dreamjournalai.shared.dream_nightmares.presentation

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.DarkBlue
import org.ballistic.dreamjournalai.shared.core.components.ActionBottomSheet
import org.ballistic.dreamjournalai.shared.core.components.TypewriterText
import org.ballistic.dreamjournalai.shared.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.shared.core.util.formatCustomDate
import org.ballistic.dreamjournalai.shared.core.util.parseCustomDate
import org.ballistic.dreamjournalai.shared.dream_journal_list.presentation.components.DateHeader
import org.ballistic.dreamjournalai.shared.dream_journal_list.presentation.components.DreamItem
import org.ballistic.dreamjournalai.shared.dream_main.presentation.viewmodel.MainScreenViewModelState
import org.ballistic.dreamjournalai.shared.dream_nightmares.domain.NightmareEvent
import org.ballistic.dreamjournalai.shared.dream_nightmares.presentation.components.DreamNightmareScreenTopBar
import org.ballistic.dreamjournalai.shared.dream_nightmares.presentation.viewmodel.DreamNightmareScreenState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DreamNightmareScreen(
    dreamNightmareScreenState: DreamNightmareScreenState,
    mainScreenViewModelState: MainScreenViewModelState,
    bottomPaddingValue: Dp,
    onEvent: (NightmareEvent) -> Unit,
    onNavigateToDream: (dreamID: String?, backgroundID: Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    // Bottom sheet if deleting a dream
    if (dreamNightmareScreenState.bottomDeleteCancelSheetState) {
        ActionBottomSheet(
            title = "Delete this Dream?",
            buttonText = "Delete",
            message = "Are you sure you want to delete this dream?",
            onClick = {
                dreamNightmareScreenState.dreamToDelete?.let { NightmareEvent.DeleteDream(it) }
                    ?.let { onEvent(it) }
            },
            onClickOutside = {
                onEvent(NightmareEvent.ToggleBottomDeleteCancelSheetState(false))
            },
        )
    }

    // Load the nightmares once
    LaunchedEffect(Unit) {
        onEvent(NightmareEvent.LoadDreams)
    }

    Scaffold(
        topBar = {
            DreamNightmareScreenTopBar(
                mainScreenViewModelState = mainScreenViewModelState
            )
        },
        containerColor = Color.Transparent,
    ) { paddingValues ->

        // If we have no nightmares, show an empty-state message
        if (dreamNightmareScreenState.dreamNightmareList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
                        text = "You currently have no nightmares. Hopefully you never do!",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        // Otherwise, display them in a LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = bottomPaddingValue
                )
                .dynamicBottomNavigationPadding(),
            contentPadding = PaddingValues(bottom = 40.dp),
        ) {
            // 1) Parse and sort by LocalDate (descending), then by dream.timestamp (descending)
            val sortedGroupedNightmares = dreamNightmareScreenState.dreamNightmareList
                .mapNotNull { dream ->
                    // Try to parse the dream.date using your custom parser
                    try {
                        val parsedDate = parseCustomDate(dream.date)
                        parsedDate to dream
                    } catch (e: IllegalArgumentException) {
                        // If parse fails, skip this dream or handle differently
                        null
                    }
                }
                .sortedWith(
                    compareByDescending<Pair<LocalDate, Dream>> { it.first }
                        .thenByDescending { it.second.timestamp }
                )
                .groupBy { it.first }

            // 2) For each date group, show a sticky header, then items
            sortedGroupedNightmares.forEach { (date, dreams) ->
                stickyHeader {
                    DateHeader(dateString = formatCustomDate(date))
                }
                items(dreams) { (_, dream) ->
                    DreamItem(
                        dream = dream,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .padding(horizontal = 12.dp),
                        scope = scope,
                        onClick = {
                            onNavigateToDream(dream.id, dream.backgroundImage)
                        },
                        onDeleteClick = {
                            onEvent(NightmareEvent.DreamToDelete(dream))
                            onEvent(NightmareEvent.ToggleBottomDeleteCancelSheetState(true))
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}