package org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen

import android.os.Vibrator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.core.util.VibrationUtils.triggerVibration
import org.ballistic.dreamjournalai.dream_tools.presentation.components.DreamToolScreenWithNavigateUpTopBar
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.components.MassInterpretationHistoryPage
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.components.MassInterpretationResultPage
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.components.SelectDreamsPage
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenEvent


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MassInterpretDreamToolScreen(
    interpretDreamsScreenState: InterpretDreamsScreenState,
    bottomPaddingValue: Dp,
    onEvent: (InterpretDreamsToolEvent) -> Unit,
    onMainScreenEvent: (MainScreenEvent) -> Unit,
    navigateUp: () -> Unit
) {
    val chosenDreams = interpretDreamsScreenState.chosenDreams
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)
    val snackbarHostState = remember { SnackbarHostState() }

    if(interpretDreamsScreenState.isLoading) {
        BackHandler {
            // Do nothing
        }
    }

    LaunchedEffect(Unit) {
        onEvent(InterpretDreamsToolEvent.GetDreams)
        onEvent(InterpretDreamsToolEvent.GetMassInterpretations)
    }

    val pages = MassInterpretationTabs.entries.map { it }
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Scaffold(
        topBar = {
            DreamToolScreenWithNavigateUpTopBar(
                title = "Interpret Dreams",
                navigateUp = navigateUp,
                vibrator = vibrator,
                enabledBack = !interpretDreamsScreenState.isLoading,
                modifier = Modifier.height(72.dp)
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
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        color = colorResource(id = R.color.white),
                        modifier = Modifier.tabIndicatorOffset(pagerState.currentPage)
                    )
                },
                divider = {},
                contentColor = colorResource(id = R.color.white),
                containerColor = colorResource(id = R.color.dark_blue).copy(alpha = 0.5f),
            ) {
                pages.forEachIndexed { index, page ->
                    val isSelected = pagerState.currentPage == index
                    val transition =
                        updateTransition(targetState = isSelected, label = "TabTransition")

                    // Controlled animations for scale
                    val scale by transition.animateFloat(
                        label = "Scale",
                        transitionSpec = {
                            if (targetState) {
                                keyframes {
                                    durationMillis = 2300  // Total duration for the scale animation
                                    1.25f at 1500 using LinearOutSlowInEasing // Scale up slowly to 1.25 over 1500ms
                                    1.25f at 2000 using LinearEasing // Hold at 1.25 for 1 second (2000ms total)
                                    0f at 2300 using FastOutLinearInEasing // Quickly decrease to 0 over 300ms
                                }
                            } else {
                                tween(
                                    durationMillis = 300,
                                    easing = LinearOutSlowInEasing
                                ) // Return to original scale quickly when deselected
                            }
                        }
                    ) { state ->
                        if (state) 0f else 1f  // Scale to 0 when selected, back to 1 when not selected
                    }

                    val iconColor by transition.animateColor(
                        label = "Color",
                        transitionSpec = {
                            tween(durationMillis = 500)  // Smooth color transition
                        }
                    ) { state ->
                        if (state) Color.White else Color.Gray.copy(.6f)
                    }

                    Tab(
                        selected = isSelected,
                        onClick = {
                            triggerVibration(vibrator)
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },

                        text = {
                            // Always show icon unless it is transitioning out for the selected tab
                            if (!isSelected || scale > 0) {
                                Icon(
                                    painter = painterResource(id = page.icon),
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .graphicsLayer {
                                            scaleX = if (isSelected) scale else 1f
                                            scaleY = if (isSelected) scale else 1f
                                        }
                                )
                            }

                            if (isSelected) {
                                // Show text when selected
                                AnimatedVisibility(
                                    visible = scale == 0f,
                                    enter = fadeIn(animationSpec = tween(durationMillis = 400)) + expandVertically(
                                        animationSpec = tween(durationMillis = 300)
                                    ),
                                    exit = fadeOut(animationSpec = tween(durationMillis = 0)),
                                ) {
                                    Text(
                                        text = page.title,
                                        style = typography.titleSmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
            ) { page ->
                when (page) {
                    0 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 16.dp)
                        ) {
                            SelectDreamsPage(
                                interpretDreamsScreenState = interpretDreamsScreenState,
                                snackbarHostState = snackbarHostState,
                                scope = scope,
                                chosenDreams = chosenDreams,
                                vibrator = vibrator,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                onEvent = onEvent
                            )
                            Button(
                                onClick = {
                                    if (chosenDreams.isEmpty()) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Please select dreams to interpret",
                                                actionLabel = "Dismiss",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    } else if (chosenDreams.size < 2) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Select at least two dreams to interpret",
                                                actionLabel = "Dismiss",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    } else {
                                        scope.launch {
                                            pagerState.animateScrollToPage(1)
                                            onEvent(
                                                InterpretDreamsToolEvent.ToggleBottomMassInterpretationSheetState(
                                                    true
                                                )
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(id = R.color.RedOrange).copy(
                                        alpha = 0.8f
                                    )
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.mass_dream_interpretation_icon),
                                    contentDescription = "Interpret ${chosenDreams.size} dreams",
                                    modifier = Modifier.size(40.dp),
                                    colorFilter = ColorFilter.tint(Color.White)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                if (chosenDreams.isEmpty()) {
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Select Dreams",
                                        modifier = Modifier
                                            .padding(8.dp),
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        style = typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Image(
                                        painter = painterResource(id = R.drawable.mass_dream_interpretation_icon),
                                        contentDescription = "Interpret ${chosenDreams.size} dreams",
                                        modifier = Modifier.size(40.dp),
                                        colorFilter = ColorFilter.tint(Color.Transparent)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Interpret Dreams",
                                        modifier = Modifier
                                            .padding(8.dp),
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        style = typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.weight(1f))

                                    Text(
                                        text = "${chosenDreams.size}/15",
                                        modifier = Modifier
                                            .padding(8.dp),
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        MassInterpretationResultPage(
                            interpretDreamsScreenState = interpretDreamsScreenState,
                            onEvent = onEvent,
                            onMainScreenEvent = onMainScreenEvent,
                            snackBarHostState = snackbarHostState,
                            scope = scope,
                            pagerState = pagerState,
                            vibrator = vibrator
                        )
                    }

                    2 -> {
                        MassInterpretationHistoryPage(
                            interpretDreamsScreenState = interpretDreamsScreenState,
                            snackbarHostState = snackbarHostState,
                            onEvent = onEvent,
                            scope = scope,
                            pagerState = pagerState,
                        )
                    }
                }
            }
        }
    }
}
