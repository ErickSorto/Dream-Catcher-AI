package org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.pages.AIPage

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.lexilabs.basic.ads.DependsOnGoogleMobileAds
import app.lexilabs.basic.ads.RewardedAd
import coil3.compose.LocalPlatformContext
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.baseline_report_24
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.dream_add_edit.domain.AIPageType
import org.ballistic.dreamjournalai.shared.dream_add_edit.domain.AITool
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components.DreamInterpretationPopUp
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components.ImageGenerationPopUp
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components.QuestionAIGenerationBottomSheet
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.pages.AIPage.AISubPages.UniversalAIPage
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.viewmodel.AddEditDreamState
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.BrighterWhite
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.Green
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LighterYellow
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.Purple
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.RedOrange
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.SkyBlue
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.Yellow
import org.ballistic.dreamjournalai.shared.core.components.ActionBottomSheet
import org.ballistic.dreamjournalai.shared.core.components.DreamTokenLayout
import org.ballistic.dreamjournalai.shared.dream_add_edit.domain.AddEditDreamEvent
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    DependsOnGoogleMobileAds::class
)
@Composable
fun SharedTransitionScope.AIPage(
    pages: List<String>,
    pagerState2: PagerState,
    addEditDreamState: AddEditDreamState,
    textFieldState: TextFieldState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit,
    onImageClick: (String) -> Unit = {},
) {

    val dreamTokens = addEditDreamState.dreamTokens
    val responseState = addEditDreamState.dreamAIExplanation
    val imageState = addEditDreamState.dreamAIImage
    val questionState = addEditDreamState.dreamAIQuestionAnswer
    val adviceState = addEditDreamState.dreamAIAdvice
    val contentState = textFieldState.text.toString()
    val storyState = addEditDreamState.dreamAIStory
    val moodState = addEditDreamState.dreamAIMoodAnalyser
    val detailState = addEditDreamState.dreamGeneratedDetails.response
    val flagContentBottomSheetState = remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = responseState) {
        if (responseState.isLoading) {
            scope.launch {
                pagerState2.animateScrollToPage(
                    1,
                )
            }
        }
    }

    LaunchedEffect(key1 = imageState) {
        if (imageState.isLoading) {
            scope.launch {
                pagerState2.animateScrollToPage(0)
            }
        }
    }

    LaunchedEffect(key1 = questionState) {
        if (questionState.isLoading) {
            scope.launch {
                pagerState2.animateScrollToPage(3)
            }
        }
    }

    LaunchedEffect(key1 = adviceState) {
        if (adviceState.isLoading) {
            scope.launch {
                pagerState2.animateScrollToPage(2)
            }
        }
    }

    LaunchedEffect(key1 = storyState) {
        if (storyState.isLoading) {
            scope.launch {
                pagerState2.animateScrollToPage(4)
            }
        }
    }

    LaunchedEffect(key1 = moodState) {
        if (moodState.isLoading) {
            scope.launch {
                pagerState2.animateScrollToPage(5)
            }
        }
    }

    if (flagContentBottomSheetState.value) {
        ActionBottomSheet(
            title = "Flag Content",
            message = "Are you sure you want to flag this content?",
            buttonText = "Flag",
            onClick = {
                onAddEditDreamEvent(AddEditDreamEvent.FlagDreamContent)
                flagContentBottomSheetState.value = false
            },
            onClickOutside = {
                flagContentBottomSheetState.value = false
            }
        )
    }

    if (addEditDreamState.dreamImageGenerationPopUpState) {
        ImageGenerationPopUp(
            addEditDreamState = addEditDreamState,
            onDreamTokenClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamImageGenerationPopUpState(false))
                if (dreamTokens < amount) {
                    scope.launch {
                        addEditDreamState.snackBarHostState.value.showSnackbar(
                            message = "Not enough dream tokens",
                            actionLabel = "Dismiss",
                            duration = SnackbarDuration.Short
                        )
                    }
                } else {
                    scope.launch {
                        onAddEditDreamEvent(
                            AddEditDreamEvent.ClickGenerateAIImage(
                                detailState,
                                amount
                            )
                        )
                    }
                }
            },
            onAdClick = {
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamImageGenerationPopUpState(false))
                scope.launch {
                    onAddEditDreamEvent(
                        AddEditDreamEvent.AdAIImageToggle(
                            true
                        )
                    )
                }
            },
            onClickOutside = {
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamImageGenerationPopUpState(false))
            },
        )
    }
    if (addEditDreamState.isAdImage) {
        RewardedAd(
            activity = LocalPlatformContext.current,
            adUnitId = "ca-app-pub-8710979310678386/8178296701",
            onRewardEarned = {
                onAddEditDreamEvent(
                    AddEditDreamEvent.ClickGenerateAIImage(
                        detailState,
                        0
                    )
                )
                onAddEditDreamEvent(AddEditDreamEvent.AdAIImageToggle(false))
            },
            onDismissed = {
                onAddEditDreamEvent(AddEditDreamEvent.AdAIImageToggle(false))
            }
        )
    }

    if (addEditDreamState.dreamInterpretationPopUpState) {
        DreamInterpretationPopUp(
            title = "Dream Interpreter",
            dreamTokens = dreamTokens,
            onAdClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamInterpretationPopUpState(false))
                scope.launch {
                    onAddEditDreamEvent(
                        AddEditDreamEvent.AdAIImageToggle(
                            true
                        )
                    )
                }
            },
            onDreamTokenClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamInterpretationPopUpState(false))
                if (dreamTokens < amount) {
                    scope.launch {
                        addEditDreamState.snackBarHostState.value.showSnackbar(
                            message = "Not enough dream tokens",
                            actionLabel = "Dismiss",
                            duration = SnackbarDuration.Short
                        )
                    }
                } else {
                    scope.launch {
                        onAddEditDreamEvent(
                            AddEditDreamEvent.ClickGenerateAIResponse(
                                contentState,
                                amount
                            )
                        )
                    }
                }

            },
            onClickOutside = {
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamInterpretationPopUpState(false))
            },
        )
    }

    if(addEditDreamState.isAdResponse){
        RewardedAd(
            activity = LocalPlatformContext.current,
            adUnitId = "ca-app-pub-8710979310678386/8178296701",
            onRewardEarned = {
                onAddEditDreamEvent(
                    AddEditDreamEvent.ClickGenerateAIResponse(
                        contentState,
                        0
                    )
                )
                onAddEditDreamEvent(AddEditDreamEvent.AdAIResponseToggle(false))
            },
            onDismissed = {
                onAddEditDreamEvent(AddEditDreamEvent.AdAIResponseToggle(false))
            }
        )
    }



    if (addEditDreamState.dreamAdvicePopUpState) {
        DreamInterpretationPopUp(
            title = "Dream Advice",
            dreamTokens = dreamTokens,
            onAdClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamAdvicePopUpState(false))
                scope.launch {
                    onAddEditDreamEvent(
                        AddEditDreamEvent.AdAIAdviceToggle(
                            true
                        )
                    )
                }
            },
            onDreamTokenClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamAdvicePopUpState(false))
                if (dreamTokens < amount) {
                    scope.launch {
                        addEditDreamState.snackBarHostState.value.showSnackbar(
                            message = "Not enough dream tokens",
                            actionLabel = "Dismiss",
                            duration = SnackbarDuration.Short
                        )
                    }
                } else {
                    scope.launch {
                        onAddEditDreamEvent(
                            AddEditDreamEvent.ClickGenerateAIAdvice(
                                contentState,
                                amount
                            )
                        )
                    }
                }

            },
            onClickOutside = {
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamAdvicePopUpState(false))
            },
        )
    }
    if(addEditDreamState.isAdAdvice){
        RewardedAd(
            activity = LocalPlatformContext.current,
            adUnitId = "ca-app-pub-8710979310678386/8178296701",
            onRewardEarned = {
                onAddEditDreamEvent(
                    AddEditDreamEvent.ClickGenerateAIAdvice(
                        contentState,
                        0
                    )
                )
                onAddEditDreamEvent(AddEditDreamEvent.AdAIAdviceToggle(false))
            },
            onDismissed = {
                onAddEditDreamEvent(AddEditDreamEvent.AdAIAdviceToggle(false))
            }
        )
    }


    if (addEditDreamState.dreamQuestionPopUpState) {
        QuestionAIGenerationBottomSheet(
            addEditDreamState = addEditDreamState,
            onAddEditDreamEvent = onAddEditDreamEvent,
            onDreamTokenClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamQuestionPopUpState(false))
                if (dreamTokens < amount) {
                    scope.launch {
                        addEditDreamState.snackBarHostState.value.showSnackbar(
                            message = "Not enough dream tokens",
                            actionLabel = "Dismiss",
                            duration = SnackbarDuration.Short
                        )
                    }
                } else {
                    scope.launch {
                        onAddEditDreamEvent(
                            AddEditDreamEvent.ClickGenerateFromQuestion(
                                contentState,
                                amount
                            )
                        )
                    }
                }
            },
            onAdClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamQuestionPopUpState(false))
                scope.launch {
                    onAddEditDreamEvent(
                        AddEditDreamEvent.AdQuestionToggle(
                            true
                        )
                    )
                }
            },
            onClickOutside = {
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamQuestionPopUpState(false))
            },
        )
    }
    if (addEditDreamState.isAdQuestion) {
        RewardedAd(
            activity = LocalPlatformContext.current,
            adUnitId = "ca-app-pub-8710979310678386/8178296701",
            onRewardEarned = {
                onAddEditDreamEvent(
                    AddEditDreamEvent.ClickGenerateFromQuestion(
                        contentState,
                        0
                    )
                )
                onAddEditDreamEvent(AddEditDreamEvent.AdQuestionToggle(false))
            },
            onDismissed = {
                onAddEditDreamEvent(AddEditDreamEvent.AdQuestionToggle(false))
            }
        )
    }

    if (addEditDreamState.dreamStoryPopupState) {
        DreamInterpretationPopUp(
            title = "Dream Story",
            dreamTokens = dreamTokens,
            onDreamTokenClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamStoryPopUpState(false))
                if (dreamTokens < amount) {
                    scope.launch {
                        addEditDreamState.snackBarHostState.value.showSnackbar(
                            message = "Not enough dream tokens",
                            actionLabel = "Dismiss",
                            duration = SnackbarDuration.Short
                        )
                    }
                } else {
                    scope.launch {
                        onAddEditDreamEvent(
                            AddEditDreamEvent.ClickGenerateStory(
                                contentState,
                                amount
                            )
                        )
                    }
                }
            },
            onAdClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamStoryPopUpState(false))
                scope.launch {
                    onAddEditDreamEvent(
                        AddEditDreamEvent.AdStoryToggle(
                            true
                        )
                    )
                }
            },
            onClickOutside = {
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamStoryPopUpState(false))
            },
        )
    }
    if (addEditDreamState.isAdStory) {
        RewardedAd(
            activity = LocalPlatformContext.current,
            adUnitId = "ca-app-pub-8710979310678386/8178296701",
            onRewardEarned = {
                onAddEditDreamEvent(
                    AddEditDreamEvent.ClickGenerateStory(
                        contentState,
                        0
                    )
                )
                onAddEditDreamEvent(AddEditDreamEvent.AdStoryToggle(false))
            },
            onDismissed = {
                onAddEditDreamEvent(AddEditDreamEvent.AdStoryToggle(false))
            }
        )
    }

    if (addEditDreamState.dreamMoodPopupState) {
        DreamInterpretationPopUp(
            title = "Dream Mood",
            dreamTokens = dreamTokens,
            onAdClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamMoodPopUpState(false))
                scope.launch {
                    onAddEditDreamEvent(
                        AddEditDreamEvent.ClickGenerateMood(
                            contentState,
                            amount
                        )
                    )
                }
            },
            onDreamTokenClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamMoodPopUpState(false))
                if (dreamTokens < amount) {
                    scope.launch {
                        addEditDreamState.snackBarHostState.value.showSnackbar(
                            message = "Not enough dream tokens",
                            actionLabel = "Dismiss",
                            duration = SnackbarDuration.Short
                        )
                    }
                } else {
                    scope.launch {
                        onAddEditDreamEvent(
                            AddEditDreamEvent.AdMoodToggle(
                                true
                            )
                        )
                    }
                }

            },
            onClickOutside = {
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamMoodPopUpState(false))
            },
        )
    }
    if (addEditDreamState.isAdMood) {
        RewardedAd(
            activity = LocalPlatformContext.current,
            adUnitId = "ca-app-pub-8710979310678386/8178296701",
            onRewardEarned = {
                onAddEditDreamEvent(
                    AddEditDreamEvent.ClickGenerateMood(
                        contentState,
                        0
                    )
                )
                onAddEditDreamEvent(AddEditDreamEvent.AdMoodToggle(false))
            },
            onDismissed = {
                onAddEditDreamEvent(AddEditDreamEvent.AdMoodToggle(false))
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(LightBlack.copy(alpha = 0.7f))
                .verticalScroll(scrollState, true)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            HorizontalPager(
                state = pagerState2,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        color = LightBlack.copy(alpha = 0.8f)
                    ),
                beyondViewportPageCount = 2,
            ) { page ->

                // Get the AI page type corresponding to the current page index
                val aiPageType =
                    AIPageType.entries[page]

                // Render the universal AI page for the current type
                UniversalAIPage(
                    contentType = aiPageType,
                    addEditDreamState = addEditDreamState,
                    textFieldState = textFieldState,
                    onAddEditEvent = onAddEditDreamEvent,
                    snackBarState = {
                        scope.launch {
                            addEditDreamState.snackBarHostState.value.showSnackbar(
                                message = "Dream is too short",
                                actionLabel = "Dismiss",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    infiniteTransition = infiniteTransition,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onImageClick = {
                        onImageClick(it)
                    }
                )
            }

            Text(
                text = "AI Tools Selection",
                style = typography.labelMedium,
                color = White,
                modifier = Modifier.padding(4.dp, 4.dp, 4.dp, 4.dp)
            )

            SecondaryTabRow(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 0.dp, bottom = 8.dp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    ),
                selectedTabIndex = pagerState2.currentPage,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        color = White,
                        modifier = Modifier.tabIndicatorOffset(pagerState2.currentPage)
                    )
                },
                divider = {},
                contentColor = White,
                containerColor = LightBlack,
            ) {
                pages.forEachIndexed { index, page ->
                    val isSelected = pagerState2.currentPage == index

                    // Setup for on-click and selection-based scaling
                    val targetScale = if (isSelected) 1.15f else .97f
                    val scale = remember { Animatable(targetScale) }

                    // Continuous floating effect setup
                    val floatingScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.15f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 2000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ), label = ""
                    )

                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            if (textFieldState.text.length >= 20) {
                                AITool.entries[index].color
                            } else White
                        } else Color.LightGray.copy(alpha = 0.6f),
                        animationSpec = tween(durationMillis = 500), label = ""
                    )

                    // Apply immediate scale change on selection
                    LaunchedEffect(isSelected) {
                        scale.animateTo(
                            targetValue = targetScale,
                            animationSpec = tween(durationMillis = 400)
                        )
                    }

                    val gradientBackground = if (isSelected) {
                        Brush.linearGradient(
                            colors = listOf(
                                BrighterWhite.copy(alpha = 0.12f),
                                BrighterWhite.copy(alpha = 0.02f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(100f, 100f)
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.1f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.1f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    }

                    Tab(
                        text = {
                            Icon(
                                painter = painterResource(AITool.entries[index].icon),
                                contentDescription = page,
                                tint = iconColor,
                                modifier = Modifier
                                    .graphicsLayer {
                                        val finalScale =
                                            if (isSelected) floatingScale * scale.value else scale.value
                                        scaleX = finalScale
                                        scaleY = finalScale
                                    }
                                    .size(26.dp)
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            onAddEditDreamEvent(AddEditDreamEvent.TriggerVibration)
                            scope.launch {
                                pagerState2.animateScrollToPage(
                                    index,
                                )
                            }
                        },
                        modifier = Modifier
                            .background(
                                brush = gradientBackground
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)

                    )
                    Text(
                        text = "Tokens",
                        style = typography.titleMedium.copy(BrighterWhite),
                        fontWeight = FontWeight.Light,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DreamTokenLayout(
                        totalDreamTokens = dreamTokens,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                IconButton(
                    onClick = {
                        flagContentBottomSheetState.value = true
                    },
                    modifier = Modifier.align(
                        Alignment.CenterEnd
                    )

                ) {
                    Icon(
                        painterResource(Res.drawable.baseline_report_24),
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
            }

            when (pagerState2.currentPage) {
                0 -> {
                    AIButton(
                        text = "Generate Painting",
                        color = SkyBlue,
                        onClick = {
                            if (textFieldState.text.length >= 20) {
                                onAddEditDreamEvent(
                                    AddEditDreamEvent.ToggleDreamImageGenerationPopUpState(
                                        true
                                    )
                                )
                            } else {
                                scope.launch {
                                    addEditDreamState.snackBarHostState.value.showSnackbar(
                                        message = "Dream is too short",
                                        actionLabel = "Dismiss",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        onAddEditDreamEvent = onAddEditDreamEvent
                    )
                }

                1 -> {
                    AIButton(
                        text = "Generate Interpretation",
                        color = Purple,
                        onClick = {
                            if (textFieldState.text.length >= 20) {
                                onAddEditDreamEvent(
                                    AddEditDreamEvent.ToggleDreamInterpretationPopUpState(
                                        true
                                    )
                                )
                            } else {
                                scope.launch {
                                    addEditDreamState.snackBarHostState.value.showSnackbar(
                                        message = "Dream is too short",
                                        actionLabel = "Dismiss",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        onAddEditDreamEvent = onAddEditDreamEvent
                    )
                }

                2 -> {
                    AIButton(
                        text = "Generate Advice",
                        color = Yellow,
                        onClick = {
                            if (textFieldState.text.length >= 20) {
                                onAddEditDreamEvent(
                                    AddEditDreamEvent.ToggleDreamAdvicePopUpState(
                                        true
                                    )
                                )
                            } else {
                                scope.launch {
                                    addEditDreamState.snackBarHostState.value.showSnackbar(
                                        message = "Dream is too short",
                                        actionLabel = "Dismiss",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        onAddEditDreamEvent = onAddEditDreamEvent
                    )
                }

                3 -> {
                    AIButton(
                        text = "Ask a Question",
                        color = RedOrange,
                        onClick = {
                            if (textFieldState.text.length >= 20) {
                                onAddEditDreamEvent(
                                    AddEditDreamEvent.ToggleDreamQuestionPopUpState(
                                        true
                                    )
                                )
                            } else {
                                scope.launch {
                                    addEditDreamState.snackBarHostState.value.showSnackbar(
                                        message = "Dream is too short",
                                        actionLabel = "Dismiss",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        onAddEditDreamEvent = onAddEditDreamEvent
                    )
                }

                4 -> {
                    AIButton(
                        text = "Generate Story",
                        color = LighterYellow,
                        onClick = {
                            if (textFieldState.text.length >= 20) {
                                onAddEditDreamEvent(
                                    AddEditDreamEvent.ToggleDreamStoryPopUpState(
                                        true
                                    )
                                )
                            } else {
                                scope.launch {
                                    addEditDreamState.snackBarHostState.value.showSnackbar(
                                        message = "Dream is too short",
                                        actionLabel = "Dismiss",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        onAddEditDreamEvent = onAddEditDreamEvent
                    )
                }

                5 -> {
                    AIButton(
                        text = "Generate Mood Analysis",
                        color = Green,
                        onClick = {
                            if (textFieldState.text.length >= 20) {
                                onAddEditDreamEvent(
                                    AddEditDreamEvent.ToggleDreamMoodPopUpState(
                                        true
                                    )
                                )
                            } else {
                                scope.launch {
                                    addEditDreamState.snackBarHostState.value.showSnackbar(
                                        message = "Dream is too short",
                                        actionLabel = "Dismiss",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        },
                        onAddEditDreamEvent = onAddEditDreamEvent
                    )
                }
            }
        }
    }
}

@Composable
fun AIButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit,
) {
    Button(
        onClick = {
            onClick()
            onAddEditDreamEvent(AddEditDreamEvent.TriggerVibration)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(
                width = 4.dp,
                color = LightBlack.copy(alpha = 0.3f),
                shape = RoundedCornerShape(9.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White,
        ),

        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 5.dp,
            pressedElevation = 10.dp,
        ),
    ) {
        Text(
            text = text,
            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}