package org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.components

import android.app.Activity
import android.os.Vibrator
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.DreamTokenLayout
import org.ballistic.dreamjournalai.core.components.TypewriterText
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components.ArcRotationAnimation
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.InterpretDreamsScreenState
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.InterpretDreamsToolEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenEvent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MassInterpretationResultPage(
    interpretDreamsScreenState: InterpretDreamsScreenState,
    onMainScreenEvent: (MainScreenEvent) -> Unit,
    snackBarHostState: SnackbarHostState,
    scope: CoroutineScope,
    pagerState: PagerState,
    onEvent: (InterpretDreamsToolEvent) -> Unit,
    vibrator: Vibrator
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading icon rotation")
    val activity = (LocalContext.current as? Activity)
    val dreamTokens = interpretDreamsScreenState.dreamTokens.collectAsStateWithLifecycle().value

    if (interpretDreamsScreenState.bottomMassInterpretationSheetState) {
        BottomModalSheetMassInterpretation(
            title = "Interpret Dreams",
            interpretDreamsScreenState = interpretDreamsScreenState,
            onDreamTokenClick = {
                onEvent(InterpretDreamsToolEvent.ToggleBottomMassInterpretationSheetState(false))
                if (interpretDreamsScreenState.dreamTokens.value < 2) {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = "Not enough dream tokens",
                            actionLabel = "Dismiss",
                            duration = SnackbarDuration.Short
                        )
                    }
                } else {
                    onMainScreenEvent(MainScreenEvent.SetDrawerState(false))
                    onMainScreenEvent(MainScreenEvent.SetBottomBarEnabledState(false))
                    scope.launch {
                        onEvent(
                            InterpretDreamsToolEvent.InterpretDreams(
                                isAd = false,
                                activity = activity!!,
                                cost = it,
                                isFinishedEvent = {
                                    onMainScreenEvent(MainScreenEvent.SetDrawerState(true))
                                    onMainScreenEvent(MainScreenEvent.SetBottomBarEnabledState(true))
                                }
                            )
                        )
                    }
                }
            },
            onAdClick = {
                onMainScreenEvent(MainScreenEvent.SetDrawerState(false))
                onMainScreenEvent(MainScreenEvent.SetBottomBarEnabledState(false))
                onEvent(InterpretDreamsToolEvent.ToggleBottomMassInterpretationSheetState(false))
                scope.launch {
                    onEvent(
                        InterpretDreamsToolEvent.InterpretDreams(
                            isAd = true,
                            activity = activity!!,
                            cost = it,
                            isFinishedEvent = {
                                onMainScreenEvent(MainScreenEvent.SetDrawerState(true))
                                onMainScreenEvent(MainScreenEvent.SetBottomBarEnabledState(true))
                            }
                        )
                    )
                }
                onMainScreenEvent(MainScreenEvent.SetDrawerState(true))
                onMainScreenEvent(MainScreenEvent.SetBottomBarEnabledState(true))
            },
            onClickOutside = {
                onEvent(InterpretDreamsToolEvent.ToggleBottomMassInterpretationSheetState(false))
            },
            onEvent = onEvent,
            dreamTokens = dreamTokens
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = colorResource(id = R.color.light_black).copy(alpha = 0.8f))
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Interpretation",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = Color.White,
            )
            Spacer(modifier = Modifier.width(8.dp))
            DreamTokenLayout(
                totalDreamTokens = dreamTokens,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .background(color = colorResource(id = R.color.light_black).copy(alpha = 0.8f))
                .fillMaxWidth()
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .animateContentSize { initialValue, targetValue -> },
            contentAlignment = Alignment.Center
        ) {
            if (interpretDreamsScreenState.isLoading) {
                ArcRotationAnimation(
                    infiniteTransition = infiniteTransition,
                )
            } else if (interpretDreamsScreenState.chosenMassInterpretation.interpretation.isNotEmpty()) {
                TypewriterText(
                    text = interpretDreamsScreenState.chosenMassInterpretation.interpretation,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    color = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart),
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Please select dreams and click on the 'Interpret Dreams' button to get an interpretation.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (interpretDreamsScreenState.chosenMassInterpretation.interpretation.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (dreamUID in interpretDreamsScreenState.chosenMassInterpretation.listOfDreamIDs) {
                    if (interpretDreamsScreenState.dreams.find { it.id == dreamUID } != null) {
                        val dream = interpretDreamsScreenState.dreams.find { it.id == dreamUID }!!
                        SmallDreamItem(
                            dream = dream,
                            imageSize = 25.dp,
                            vibrator = vibrator,
                        ) {

                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (interpretDreamsScreenState.chosenDreams.isEmpty()) {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                } else if (interpretDreamsScreenState.chosenDreams.size < 2) {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                        snackBarHostState.showSnackbar(
                            message = "Please select one more dream",
                            actionLabel = "Dismiss",
                            duration = SnackbarDuration.Short
                        )
                    }
                } else {
                    onEvent(
                        InterpretDreamsToolEvent.ToggleBottomMassInterpretationSheetState(true)
                    )
                }
            },
            modifier = Modifier
                .padding(16.dp, 0.dp, 16.dp, 0.dp)
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
                contentDescription = "Interpret Icon",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
            Spacer(modifier = Modifier.weight(1f))
            if (interpretDreamsScreenState.chosenDreams.isEmpty()) {
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "Select Dreams",
                    modifier = Modifier
                        .padding(8.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = R.drawable.mass_dream_interpretation_icon),
                    contentDescription = "Interpret ${interpretDreamsScreenState.chosenDreams.size} dreams",
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
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${interpretDreamsScreenState.chosenDreams.size}/15",
                    modifier = Modifier
                        .padding(8.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}