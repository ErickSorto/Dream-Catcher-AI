package org.ballistic.dreamjournalai.dream_tools.presentation.paint_dreams_screen

import android.os.Vibrator
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.dream_tools.presentation.components.DreamToolScreenWithNavigateUpTopBar
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.viewmodel.InterpretDreamsScreenState
import org.ballistic.dreamjournalai.dream_tools.domain.event.InterpretDreamsToolEvent
import org.ballistic.dreamjournalai.dream_main.domain.MainScreenEvent

@Composable
fun PaintDreamWorldScreen(
    interpretDreamsScreenState: InterpretDreamsScreenState,
    bottomPaddingValue: Dp,
    onEvent: (InterpretDreamsToolEvent) -> Unit,
    onMainScreenEvent: (MainScreenEvent) -> Unit,
    navigateUp: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            DreamToolScreenWithNavigateUpTopBar(
                title = "Interpret Dreams",
                navigateUp = navigateUp,
                vibrator = vibrator,
                enabledBack = !interpretDreamsScreenState.isLoading,
            )
        },
        bottomBar = {
            Spacer(modifier = Modifier.height(96.dp))
        },
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding(), bottom = bottomPaddingValue)
                .dynamicBottomNavigationPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }
}