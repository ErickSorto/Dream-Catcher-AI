package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage

import android.app.Activity
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.DreamTokenLayout
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.DreamInterpretationPopUp
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.ImageGenerationPopUp
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.QuestionAIGenerationBottomSheet
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.events.AITool
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AISubPages.AIDreamAdvicePage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AISubPages.AIInterpreterPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AISubPages.AIMoodPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AISubPages.AIPainterPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AISubPages.AIQuestionPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AISubPages.AIStoryPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel.AddEditDreamState


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AIPage(
    pagerState: PagerState,
    addEditDreamState: AddEditDreamState,
    textFieldState: TextFieldState,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit,
) {

    val pages = AITool.entries.map { it.title }
    val pagerSate2 = rememberPagerState(pageCount = { pages.size })
    val dreamTokens = addEditDreamState.dreamTokens.collectAsStateWithLifecycle().value
    val responseState = addEditDreamState.dreamAIExplanation
    val imageState = addEditDreamState.dreamAIImage
    val questionState = addEditDreamState.dreamQuestionAIAnswer
    val adviceState = addEditDreamState.dreamAIAdvice
    val contentState = textFieldState.text.toString()
    val storyState = addEditDreamState.dreamStoryGeneration
    val moodState = addEditDreamState.dreamMoodAIAnalyser
    val detailState = addEditDreamState.dreamGeneratedDetails.response
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as Activity
    val painter =
        rememberAsyncImagePainter(model = addEditDreamState.dreamAIImage.image.toString())


    LaunchedEffect(key1 = responseState) {
        if (responseState.isLoading) {
            scope.launch {
                pagerSate2.animateScrollToPage(1)
            }
        }
    }

    LaunchedEffect(key1 = imageState) {
        if (imageState.isLoading) {
            scope.launch {
                pagerSate2.animateScrollToPage(0)
            }
        }
    }

    LaunchedEffect(key1 = questionState) {
        if (questionState.isLoading) {
            scope.launch {
                pagerSate2.animateScrollToPage(3)
            }
        }
    }

    LaunchedEffect(key1 = adviceState) {
        if (adviceState.isLoading) {
            scope.launch {
                pagerSate2.animateScrollToPage(2)
            }
        }
    }

    LaunchedEffect(key1 = storyState) {
        if (storyState.isLoading) {
            scope.launch {
                pagerSate2.animateScrollToPage(4)
            }
        }
    }

    LaunchedEffect(key1 = moodState) {
        if (moodState.isLoading) {
            scope.launch {
                pagerSate2.animateScrollToPage(5)
            }
        }
    }

    if (addEditDreamState.dreamImageGenerationPopUpState) {
        ImageGenerationPopUp(
            addEditDreamState = addEditDreamState,
            onDreamTokenClick = {
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamImageGenerationPopUpState(false))
                if (dreamTokens < 2) {
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
                                activity,
                                false,
                                it
                            )
                        )
                    }
                }
            },
            onAdClick = {
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamImageGenerationPopUpState(false))
                scope.launch {
                    onAddEditDreamEvent(
                        AddEditDreamEvent.ClickGenerateAIImage(
                            detailState,
                            activity,
                            true,
                            it
                        )
                    )
                }
            },
            onClickOutside = {
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamImageGenerationPopUpState(false))
            },
            onAddEditDreamEvent = onAddEditDreamEvent,
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
                        AddEditDreamEvent.ClickGenerateAIResponse(
                            contentState,
                            activity,
                            true,
                            amount
                        )
                    )
                }
            },
            onDreamTokenClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamInterpretationPopUpState(false))
                if (dreamTokens <= 0) {
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
                                activity,
                                false,
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

    if (addEditDreamState.dreamAdvicePopUpState) {
        DreamInterpretationPopUp(
            title = "Dream Advice",
            dreamTokens = dreamTokens,
            onAdClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamAdvicePopUpState(false))
                scope.launch {
                    onAddEditDreamEvent(
                        AddEditDreamEvent.ClickGenerateAIAdvice(
                            contentState,
                            activity,
                            true,
                            amount
                        )
                    )
                }
            },
            onDreamTokenClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamAdvicePopUpState(false))
                if (dreamTokens <= 0) {
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
                                activity,
                                false,
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


    if (addEditDreamState.dreamQuestionPopUpState) {
        QuestionAIGenerationBottomSheet(
            addEditDreamState = addEditDreamState,
            onAddEditDreamEvent = onAddEditDreamEvent,
            onDreamTokenClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamQuestionPopUpState(false))
                if (dreamTokens <= 0) {
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
                                activity,
                                false,
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
                        AddEditDreamEvent.ClickGenerateFromQuestion(
                            contentState,
                            activity,
                            true,
                            amount
                        )
                    )
                }
            },
            onClickOutside = {
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamQuestionPopUpState(false))
            },
        )
    }

    if (addEditDreamState.dreamStoryPopupState) {
        DreamInterpretationPopUp(
            title = "Dream Story",
            dreamTokens = dreamTokens,
            onDreamTokenClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamStoryPopUpState(false))
                if (dreamTokens <= 0) {
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
                                activity,
                                false,
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
                        AddEditDreamEvent.ClickGenerateStory(
                            contentState,
                            activity,
                            true,
                            amount
                        )
                    )
                }
            },
            onClickOutside = {
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamStoryPopUpState(false))
            },
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
                            activity,
                            true,
                            amount
                        )
                    )
                }
            },
            onDreamTokenClick = { amount ->
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamMoodPopUpState(false))
                if (dreamTokens <= 0) {
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
                            AddEditDreamEvent.ClickGenerateMood(
                                contentState,
                                activity,
                                false,
                                amount
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color = colorResource(id = R.color.light_black).copy(alpha = 0.7f))
                .verticalScroll(scrollState, true)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Spacer(
                    modifier = Modifier
                        .weight(1f)

                )
                Text(
                    text = "Tokens",
                    style = typography.titleMedium.copy(color = colorResource(id = R.color.brighter_white)),
                    fontWeight = FontWeight.Light,
                )
                DreamTokenLayout(
                    totalDreamTokens = dreamTokens,
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            SecondaryTabRow(
                modifier = Modifier,
                selectedTabIndex = pagerSate2.currentPage,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        color = colorResource(id = R.color.white),
                        modifier = Modifier.tabIndicatorOffset(pagerSate2.currentPage)
                    )
                },
                contentColor = colorResource(id = R.color.white),
                containerColor = Color.Transparent,
            ) {
                pages.forEachIndexed { index, page ->
                    Tab(
                        text = {
                            val modifier = Modifier

                            modifier.rotate(if (index == 2) 45f else 0f)

                            Icon(
                                painter = painterResource(id = AITool.entries[index].icon),
                                contentDescription = page,
                                tint = colorResource(
                                    if (textFieldState.text.length >= 10) {
                                        AITool.entries[index].color
                                    } else R.color.white
                                ),
                                modifier = modifier.size(28.dp)
                            )
                        },
                        selected = pagerSate2.currentPage == index,
                        onClick = {
                            scope.launch { pagerSate2.animateScrollToPage(index) }
                        }
                    )
                }
            }
            HorizontalPager(
                state = pagerSate2,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        color = colorResource(id = R.color.light_black).copy(alpha = 0.8f)
                    )
            ) { page ->

                when (page) {
                    0 -> {
                        AIPainterPage(
                            addEditDreamState = addEditDreamState,
                            textFieldState = textFieldState,
                            painter = painter,
                            infiniteTransition = infiniteTransition,
                            pagerState = pagerState,
                            onAddEditEvent = onAddEditDreamEvent
                        )
                    }

                    1 -> {
                        AIInterpreterPage(
                            addEditDreamState = addEditDreamState,
                            textFieldState = textFieldState,
                            infiniteTransition = infiniteTransition,
                            pagerState = pagerState,
                            onAddEditEvent = onAddEditDreamEvent
                        )
                    }

                    2 -> {
                        AIDreamAdvicePage(
                            addEditDreamState = addEditDreamState,
                            textFieldState = textFieldState,
                            infiniteTransition = infiniteTransition,
                            pagerState = pagerState,
                            onAddEditEvent = onAddEditDreamEvent
                        )
                    }

                    3 -> {
                        AIQuestionPage(
                            addEditDreamState = addEditDreamState,
                            textFieldState = textFieldState,
                            infiniteTransition = infiniteTransition,
                            pagerState = pagerState,
                            onAddEditEvent = onAddEditDreamEvent
                        )
                    }

                    4 -> {
                        AIStoryPage(
                            addEditDreamState = addEditDreamState,
                            textFieldState = textFieldState,
                            infiniteTransition = infiniteTransition,
                            pagerState = pagerState,
                            onAddEditEvent = onAddEditDreamEvent
                        )
                    }

                    5 -> {
                        AIMoodPage(
                            addEditDreamState = addEditDreamState,
                            textFieldState = textFieldState,
                            infiniteTransition = infiniteTransition,
                            pagerState = pagerState,
                            onAddEditEvent = onAddEditDreamEvent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            when (pagerSate2.currentPage) {
                0 -> {
                    AIButton(
                        text = "Generate Image",
                        color = R.color.sky_blue,
                        onClick = {
                            onAddEditDreamEvent(
                                AddEditDreamEvent.ToggleDreamImageGenerationPopUpState(
                                    true
                                )
                            )
                        }
                    )
                }

                1 -> {
                    AIButton(
                        text = "Interpret Dream",
                        color = R.color.purple,
                        onClick = {
                            onAddEditDreamEvent(
                                AddEditDreamEvent.ToggleDreamInterpretationPopUpState(
                                    true
                                )
                            )
                        }
                    )
                }

                2 -> {
                    AIButton(
                        text = "Dream Advice",
                        color = R.color.Yellow,
                        onClick = {
                            onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamAdvicePopUpState(true))
                        }
                    )
                }

                3 -> {
                    AIButton(
                        text = "Ask Question",
                        color = R.color.RedOrange,
                        onClick = {
                            onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamQuestionPopUpState(true))
                        }
                    )
                }

                4 -> {
                    AIButton(
                        text = "Generate Story",
                        color = R.color.lighter_yellow,
                        onClick = {
                            onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamStoryPopUpState(true))
                        }
                    )
                }

                5 -> {
                    AIButton(
                        text = "Mood Analyser",
                        color = R.color.green,
                        onClick = {
                            onAddEditDreamEvent(AddEditDreamEvent.ToggleDreamMoodPopUpState(true))
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun AIButton(
    text: String,
    color: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(color)
        ),
        elevation = ButtonDefaults.buttonElevation(5.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(10.dp)), // Add shadow with rounded corners,
    ) {
        Text(
            text = text,
            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }

}