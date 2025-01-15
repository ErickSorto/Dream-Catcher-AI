package org.ballistic.dreamjournalai.shared.dream_symbols.presentation.components

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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.DarkBlue
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components.TransparentHintTextField
import org.ballistic.dreamjournalai.shared.dream_main.presentation.viewmodel.MainScreenViewModelState
import org.ballistic.dreamjournalai.shared.dream_symbols.domain.SymbolEvent
import org.ballistic.dreamjournalai.shared.dream_symbols.presentation.viewmodel.SymbolScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolScreenTopBar(
    mainScreenViewModelState: MainScreenViewModelState,
    symbolScreenState: SymbolScreenState,
    searchedTextFieldState: TextFieldState,
    onDictionaryEvent: (SymbolEvent) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    CenterAlignedTopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!symbolScreenState.isSearching) {
                    Text(
                        text = "Symbols",
                        color = White,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
                AnimatedVisibility(
                    visible = symbolScreenState.isSearching,
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
                        textFieldState = searchedTextFieldState,
                        hint = "Search symbol...",
                        isHintVisible = searchedTextFieldState.text.toString().isEmpty(),
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
                scope.launch {
                    mainScreenViewModelState.drawerMain.open()
                }
            }) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = White
                )
            }
        },
        actions = {
            if (!symbolScreenState.isSearching) {
                IconButton(
                    onClick = {
                        onDictionaryEvent(SymbolEvent.ListenForSearchChange)
                        onDictionaryEvent(SymbolEvent.SetSearchingState(true))
                    },
                    content = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = White
                        )
                    }
                )
            } else {
                IconButton(
                    onClick = {
                        onDictionaryEvent(SymbolEvent.SetSearchingState(false))
                    },
                    content = {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = White
                        )
                    }
                )
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