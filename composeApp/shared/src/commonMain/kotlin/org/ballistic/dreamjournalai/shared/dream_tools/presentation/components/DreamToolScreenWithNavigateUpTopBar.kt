package org.ballistic.dreamjournalai.shared.dream_tools.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.DarkBlue
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.core.components.dynamicBottomNavigationPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamToolScreenWithNavigateUpTopBar(
    title: String,
    onEvent: () -> Unit,
    enabledBack: Boolean = true,
    navigateUp: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val topBarModifier = Modifier.height(72.dp)

    CenterAlignedTopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = White,
                    style = typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.Center),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        onEvent() // Vibrate
                        navigateUp()
                    }
                },
                enabled = enabledBack
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
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
        modifier = topBarModifier.dynamicBottomNavigationPadding()
    )
}