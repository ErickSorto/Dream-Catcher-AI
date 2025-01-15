package org.ballistic.dreamjournalai.shared.dream_journal_list.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.DarkBlue
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components.TransparentHintTextField
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.DreamListEvent
import org.ballistic.dreamjournalai.shared.dream_journal_list.presentation.viewmodel.DreamJournalListState
import org.ballistic.dreamjournalai.shared.dream_main.presentation.viewmodel.MainScreenViewModelState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamListScreenTopBar(
    dreamJournalListState: DreamJournalListState,
    mainScreenViewModelState: MainScreenViewModelState,
    searchTextFieldState: TextFieldState,
    onDreamListEvent: (DreamListEvent) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    CenterAlignedTopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
            ) {
                if (!dreamJournalListState.isSearching) {
                    Text(
                        text = "Dream Journal AI",
                        color = White,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }

                AnimatedVisibility(
                    visible = dreamJournalListState.isSearching,
                    //slide from left to right
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(500)
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { -it - 400 },
                        animationSpec = tween(500)
                    ),
                    modifier = Modifier
                        .align(Alignment.Center)

                ) {
                    TransparentHintTextField(
                        textFieldState = searchTextFieldState,
                        hint = "Search dream...",
                        isHintVisible = searchTextFieldState.text.isBlank(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.headlineSmall.copy(White),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                color = White.copy(
                                    alpha = 0.2f
                                )
                            )
                            .padding(4.dp, 2.dp, 0.dp, 2.dp)
                            .fillMaxWidth()
                            .padding(4.dp)
                            .focusable(),
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                onDreamListEvent(DreamListEvent.TriggerVibration)
                scope.launch {
                    mainScreenViewModelState.drawerMain.open()
                }
            }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = White)
            }
        },
        actions = {
            if (!dreamJournalListState.isSearching) {
                IconButton(
                    onClick = {
                        onDreamListEvent(DreamListEvent.TriggerVibration)
                        onDreamListEvent(DreamListEvent.SetSearchingState(true))
                    },
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = White
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        onDreamListEvent(DreamListEvent.TriggerVibration)
                        keyboardController?.hide()
                        searchTextFieldState.clearText()
                        onDreamListEvent(DreamListEvent.SetSearchingState(false))
                    },
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = DarkBlue.copy(alpha = 0.5f),
            navigationIconContentColor = Color.Black,
            titleContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        modifier = Modifier.dynamicBottomNavigationPadding()
    )
}
