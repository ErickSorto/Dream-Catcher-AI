package org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.components

import android.os.Vibrator
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.dream_journal_list.dream_list_screen.components.DateHeader
import org.ballistic.dreamjournalai.dream_journal_list.dream_list_screen.components.DreamItem
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.InterpretDreamsScreenState
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.InterpretDreamsToolEvent
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectDreamsPage(
    interpretDreamsScreenState: InterpretDreamsScreenState,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    chosenDreams: List<Dream>,
    vibrator: Vibrator,
    modifier: Modifier = Modifier,
    onEvent: (InterpretDreamsToolEvent) -> Unit
) {

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

        interpretDreamsScreenState.dreams.groupBy { it.date }
            .mapKeys { (key, _) ->
                try {
                    LocalDate.parse(key.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }, dateFormatter)
                } catch (e: DateTimeParseException) {
                    null
                }
            }
            .filterKeys { it != null }
            .toSortedMap(compareByDescending { it })
            .mapKeys { (key, _) -> key?.format(dateFormatter) }
            .forEach { (dateString, dreamsForDate) ->

                stickyHeader {
                    dateString?.let { DateHeader(dateString = it) }
                }

                items(dreamsForDate) { dream ->
                    val isDreamChosen = chosenDreams.contains(dream)
                    DreamItem(
                        dream = dream,
                        vibrator = vibrator,
                        hasBorder = isDreamChosen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .padding(horizontal = 12.dp),
                        scope = scope,
                        onClick = {
                            val chosenDreamSizeLimit = 15
                            if (chosenDreams.size >= chosenDreamSizeLimit && !chosenDreams.contains(dream)) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Only 15 dreams can be selected",
                                        "Dismiss",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } else {
                                onEvent(
                                    InterpretDreamsToolEvent.ToggleDreamToInterpretationList(
                                        dream
                                    )
                                )
                            }
                        },
                        )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
    }
}