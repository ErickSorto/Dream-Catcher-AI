package org.ballistic.dreamjournalai.dream_favorites.presentation

import android.os.Vibrator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.ActionBottomSheet
import org.ballistic.dreamjournalai.core.components.TypewriterText
import org.ballistic.dreamjournalai.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.dream_favorites.domain.FavoriteEvent
import org.ballistic.dreamjournalai.dream_favorites.presentation.components.DreamFavoriteScreenTopBar
import org.ballistic.dreamjournalai.dream_favorites.presentation.viewmodel.DreamFavoriteScreenState
import org.ballistic.dreamjournalai.dream_journal_list.presentation.components.DateHeader
import org.ballistic.dreamjournalai.dream_journal_list.presentation.components.DreamItem
import org.ballistic.dreamjournalai.dream_main.presentation.viewmodel.MainScreenViewModelState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DreamFavoriteScreen(
    dreamFavoriteScreenState: DreamFavoriteScreenState,
    mainScreenViewModelState: MainScreenViewModelState,
    bottomPaddingValue: Dp,
    onEvent: (FavoriteEvent) -> Unit,
    onNavigateToDream: (dreamID: String?, backgroundID: Int) -> Unit
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        onEvent(FavoriteEvent.LoadDreams)
    }

    if (dreamFavoriteScreenState.bottomDeleteCancelSheetState) {
        ActionBottomSheet(
            title = "Delete this Dream?",
            message = "Are you sure you want to delete this dream?",
            buttonText = "Delete",
            onClick = {
                scope.launch {
                    val result =
                        mainScreenViewModelState.scaffoldState.snackBarHostState.value.showSnackbar(
                            message = "Dream deleted",
                            actionLabel = "Undo",
                            duration = SnackbarDuration.Long
                        )

                    mainScreenViewModelState.scaffoldState.snackBarHostState.value.currentSnackbarData?.dismiss()

                    if (result == SnackbarResult.ActionPerformed) {
                        onEvent(FavoriteEvent.RestoreDream)
                    } else {
                        dreamFavoriteScreenState.dreamToDelete?.let { FavoriteEvent.DeleteDream(it) }
                            ?.let {
                                onEvent(it)
                            }
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
            DreamFavoriteScreenTopBar(
                mainScreenViewModelState = mainScreenViewModelState
            )
        },
        containerColor = Color.Transparent,
    ) { paddingValues ->
        if (dreamFavoriteScreenState.dreamFavoriteList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding(), bottom = bottomPaddingValue)
                    .dynamicBottomNavigationPadding(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .background(
                            color = colorResource(id = R.color.dark_blue).copy(alpha = 0.8f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                ) {
                    TypewriterText(
                        text = "You currently have no favorites. Add some!",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding(), bottom = bottomPaddingValue)
                .dynamicBottomNavigationPadding()
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {

            val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

            dreamFavoriteScreenState.dreamFavoriteList.groupBy { it.date }
                .mapKeys { (key, _) ->
                    try {
                        LocalDate.parse(key.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        }, dateFormatter)
                    } catch (_: DateTimeParseException) {
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
                            vibrator = vibrator,
                            scope = scope,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .padding(horizontal = 12.dp),
                            onClick = {
                                onNavigateToDream(dream.id, dream.backgroundImage)
                            },
                            onDeleteClick = {
                                onEvent(
                                    FavoriteEvent.DreamToDelete(
                                        dream
                                    )
                                )
                                onEvent(FavoriteEvent.ToggleBottomDeleteCancelSheetState(true))
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
        }
    }
}
