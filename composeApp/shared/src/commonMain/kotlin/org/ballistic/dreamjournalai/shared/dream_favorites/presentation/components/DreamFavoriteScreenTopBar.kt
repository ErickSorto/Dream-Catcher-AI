package org.ballistic.dreamjournalai.shared.dream_favorites.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.DarkBlue
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.shared.dream_main.presentation.viewmodel.MainScreenViewModelState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamFavoriteScreenTopBar(
    mainScreenViewModelState: MainScreenViewModelState,
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
                Text(
                    text = "Favorites",
                    color = White,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
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
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = Color.Transparent
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