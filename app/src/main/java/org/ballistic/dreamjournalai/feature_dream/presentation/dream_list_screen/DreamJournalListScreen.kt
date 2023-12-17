package org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen.components.DateHeader
import org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen.components.DreamItem
import org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen.viewmodel.DreamJournalListState
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState
import org.ballistic.dreamjournalai.navigation.Screens
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DreamJournalListScreen(
    navController: NavController,
    mainScreenViewModelState: MainScreenViewModelState,
    dreamJournalListState: DreamJournalListState,
    innerPadding: PaddingValues = PaddingValues(),
    onMainEvent: (MainScreenEvent) -> Unit = {},
    onDreamListEvent: (DreamListEvent) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val groupedDreams = remember { mutableStateOf(emptyMap<String, List<Dream>>()) }
    val searchedText = mainScreenViewModelState.searchedText.collectAsStateWithLifecycle().value

    LaunchedEffect(dreamJournalListState) {
        groupedDreams.value = dreamJournalListState.dreams.groupBy { it.date }
    }

    LaunchedEffect(searchedText) {
        dreamJournalListState.searchedText.value = searchedText
    }

    onMainEvent(MainScreenEvent.SetBottomBarState(true))
    onMainEvent(MainScreenEvent.SetFloatingActionButtonState(true))
    onMainEvent(MainScreenEvent.SetTopBarState(true))
    onMainEvent(MainScreenEvent.SetDrawerState(true))


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {

        val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

        groupedDreams.value
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
                    DreamItem(
                        dream = dream,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .padding(horizontal = 12.dp)
                            .clickable {
                                navController.navigate(
                                    Screens.AddEditDreamScreen.route +
                                            "?dreamId=${dream.id}&dreamImageBackground=${dream.backgroundImage}"
                                )
                            },
                        onDeleteClick = {
                            onDreamListEvent(
                                DreamListEvent.DeleteDream(
                                    dream = dream,
                                    context = context
                                )
                            )
                            scope.launch {
                                val result =
                                    mainScreenViewModelState.scaffoldState.snackBarHostState.value.showSnackbar(
                                        message = "Dream deleted",
                                        actionLabel = "Undo",
                                        duration = SnackbarDuration.Long
                                    )

                                mainScreenViewModelState.scaffoldState.snackBarHostState.value.currentSnackbarData?.dismiss()

                                if (result == SnackbarResult.ActionPerformed) {
                                    onDreamListEvent(
                                        DreamListEvent.RestoreDream
                                    )
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
    }
}