package org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen.components

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.TransparentHintTextField
import org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen.DreamListEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.dream_list_screen.viewmodel.DreamJournalListState
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamListScreenTopBar(
    dreamJournalListState: DreamJournalListState,
    mainScreenViewModelState: MainScreenViewModelState,
    onDreamListEvent: (DreamListEvent) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val searchedText = dreamJournalListState.searchedText.collectAsStateWithLifecycle()

    CenterAlignedTopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                if (!dreamJournalListState.isSearching) {
                    Text(
                        text = "Dream Journal AI",
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(start = 16.dp)
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
                        text = searchedText.value,
                        hint = "Search dream...",
                        onValueChange = {
                            onDreamListEvent(DreamListEvent.SearchDreams(it))
                        },
                        isHintVisible = searchedText.value.isBlank(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                color = colorResource(id = R.color.white).copy(
                                    alpha = 0.2f
                                )
                            )
                            .padding(4.dp, 2.dp, 0.dp, 2.dp)
                            .fillMaxWidth()
                            .padding(4.dp)
                            .focusable()
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    mainScreenViewModelState.drawerMain.open()
                }
            }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.Black)
            }
        },
        actions = {
            if (!dreamJournalListState.isSearching) {
                IconButton(
                    onClick = {
                        onDreamListEvent(DreamListEvent.SetSearchingState(true))
                    },
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color.Black
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        onDreamListEvent(DreamListEvent.SetSearchingState(false))
                        onDreamListEvent(DreamListEvent.SearchDreams(""))
                    },
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            //animate content color
            containerColor = colorResource(id = R.color.white).copy(alpha = 0.4f),
            navigationIconContentColor = Color.Black,
            titleContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
    )
}
