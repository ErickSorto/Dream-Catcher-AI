package org.ballistic.dreamjournalai.dream_add_edit.presentation.components

import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.PrimaryIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.util.VibrationUtil.triggerVibration
import org.ballistic.dreamjournalai.dream_add_edit.domain.AddEditPages
import org.ballistic.dreamjournalai.dream_add_edit.domain.AITool
import org.ballistic.dreamjournalai.dream_add_edit.domain.AddEditDreamEvent
import org.ballistic.dreamjournalai.dream_add_edit.presentation.pages.AIPage.AIPage
import org.ballistic.dreamjournalai.dream_add_edit.presentation.pages.DreamPage
import org.ballistic.dreamjournalai.dream_add_edit.presentation.pages.InfoPage
import org.ballistic.dreamjournalai.dream_add_edit.presentation.pages.dictionary_page.WordPage
import org.ballistic.dreamjournalai.dream_add_edit.presentation.viewmodel.AddEditDreamState


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
@UiComposable
fun SharedTransitionScope.TabLayout(
    dreamBackgroundImage: MutableState<Int>,
    dreamTitleState: TextFieldState,
    dreamContentState: TextFieldState,
    addEditDreamState: AddEditDreamState,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit,
    keyboardController: SoftwareKeyboardController?,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onImageClick: (String) -> Unit
) {
    val pages = AddEditPages.entries.map { it }
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val pages2 = AITool.entries.map { it.title }
    val pagerState2 = rememberPagerState(pageCount = { pages2.size })
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)

    PrimaryTabRow(
        modifier = Modifier,
        selectedTabIndex = pagerState.currentPage,
        indicator = {
            PrimaryIndicator(
                color = colorResource(id = R.color.white),
                modifier = Modifier.tabIndicatorOffset(pagerState.currentPage)
            )
        },
        divider = {},
        contentColor = colorResource(id = R.color.white),
        containerColor = colorResource(id = R.color.light_black).copy(alpha = 0.7f),
    ) {
        pages.forEachIndexed { index, page ->
            val isSelected = pagerState.currentPage == index
            val transition = updateTransition(targetState = isSelected, label = "TabTransition")

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
                        tween(durationMillis = 300, easing = LinearOutSlowInEasing) // Return to original scale quickly when deselected
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
                    scope.launch {
                        triggerVibration(vibrator)
                        pagerState.animateScrollToPage(
                            index,
                        )
                        keyboardController?.hide()
                        focusManager.clearFocus(true)
                    }
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
                DreamPage(
                    titleTextFieldState = dreamTitleState,
                    contentTextFieldState = dreamContentState,
                    vibrator = vibrator,
                    snackBarState = {
                        scope.launch {
                            addEditDreamState.snackBarHostState.value.showSnackbar(
                                message = "Dream is too short",
                                actionLabel = "Dismiss",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    animateToPage = { index ->
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                            pagerState2.animateScrollToPage(index)
                        }
                    },
                )
            }

            1 -> {
                keyboardController?.hide()
                focusManager.clearFocus(true)
                AIPage(
                    pages = pages2,
                    pagerState2 = pagerState2,
                    addEditDreamState = addEditDreamState,
                    onAddEditDreamEvent = onAddEditDreamEvent,
                    textFieldState = dreamContentState,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onImageClick = onImageClick
                )
            }

            2 -> {
                WordPage(
                    addEditDreamState = addEditDreamState,
                    onAddEditDreamEvent = onAddEditDreamEvent
                )
            }

            3 -> {
                InfoPage(
                    dreamBackgroundImage,
                    addEditDreamState = addEditDreamState,
                    onAddEditDreamEvent = onAddEditDreamEvent
                )
            }
        }
    }
}