package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.DreamTokenLayout
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.DreamInterpretationPopUp
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.GenerateButtonsLayout
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.ImageGenerationPopUp
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.QuestionAIGenerationBottomSheet
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AISubPages.AIDreamAdvicePage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AISubPages.AIInterpreterPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AISubPages.AIMoodPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AISubPages.AIPainterPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AISubPages.AIQuestionPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AISubPages.AIStoryPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel.AddEditDreamState
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState


@OptIn(ExperimentalPagerApi::class)
@Composable
fun AIPage(
    pagerState: PagerState,
    addEditDreamState: AddEditDreamState,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit,
    mainScreenViewModelState: MainScreenViewModelState
) {

    val pages = listOf("Painting", "Interpretation", "Advice", "Question", "Story", "Mood")
    val pagerSate2 = rememberPagerState()

    val responseState = addEditDreamState.dreamAIExplanation
    val imageState = addEditDreamState.dreamAIImage
    val questionState = addEditDreamState.dreamQuestionAIAnswer
    val adviceState = addEditDreamState.dreamAIAdvice
    val contentState = addEditDreamState.dreamContent
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

    if (addEditDreamState.imageGenerationPopUpState.value) {
        ImageGenerationPopUp(
            mainScreenViewModelState = mainScreenViewModelState,
            addEditDreamState = addEditDreamState,
            onDreamTokenClick = {
                addEditDreamState.imageGenerationPopUpState.value = false
                if (mainScreenViewModelState.dreamTokens.value < 2) {
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
                addEditDreamState.imageGenerationPopUpState.value = false
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
                addEditDreamState.imageGenerationPopUpState.value = false
            },
            onAddEditDreamEvent = onAddEditDreamEvent,
        )
    }

    if (addEditDreamState.dreamInterpretationPopUpState.value) {
        DreamInterpretationPopUp(
            title = "Dream Interpreter",
            mainScreenViewModelState = mainScreenViewModelState,
            onAdClick = { amount ->
                addEditDreamState.dreamInterpretationPopUpState.value = false
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
                addEditDreamState.dreamInterpretationPopUpState.value = false
                if (mainScreenViewModelState.dreamTokens.value <= 0) {
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
                addEditDreamState.dreamInterpretationPopUpState.value = false
            },
        )
    }

    if(addEditDreamState.dreamAdvicePopUpState.value){
        DreamInterpretationPopUp(
            title = "Dream Advice",
            mainScreenViewModelState = mainScreenViewModelState,
            onAdClick = { amount ->
                addEditDreamState.dreamAdvicePopUpState.value = false
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
                addEditDreamState.dreamAdvicePopUpState.value = false
                if (mainScreenViewModelState.dreamTokens.value <= 0) {
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
                addEditDreamState.dreamAdvicePopUpState.value = false
            },
        )
    }


    if (addEditDreamState.questionPopUpState.value) {
        QuestionAIGenerationBottomSheet(
            mainScreenViewModelState = mainScreenViewModelState,
            addEditDreamState = addEditDreamState,
            onAddEditDreamEvent = onAddEditDreamEvent,
            onDreamTokenClick = { amount ->
                addEditDreamState.questionPopUpState.value = false
                if (mainScreenViewModelState.dreamTokens.value <= 0) {
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
                addEditDreamState.questionPopUpState.value = false
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
                addEditDreamState.questionPopUpState.value = false
            },
        )
    }

    if (addEditDreamState.storyPopupState.value) {
        DreamInterpretationPopUp(
            title = "Dream Story",
            mainScreenViewModelState = mainScreenViewModelState,
            onDreamTokenClick = { amount ->
                addEditDreamState.storyPopupState.value = false
                if (mainScreenViewModelState.dreamTokens.value <= 0) {
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
                addEditDreamState.storyPopupState.value = false
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
                addEditDreamState.storyPopupState.value = false
            },
        )
    }

    if (addEditDreamState.moodPopupState.value) {
        DreamInterpretationPopUp(
            title = "Dream Mood",
            mainScreenViewModelState = mainScreenViewModelState,
            onAdClick = { amount ->
                addEditDreamState.moodPopupState.value = false
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
                addEditDreamState.moodPopupState.value = false
                if (mainScreenViewModelState.dreamTokens.value <= 0) {
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
                addEditDreamState.moodPopupState.value = false
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
                .background(color = colorResource(id = R.color.dark_blue).copy(alpha = 0.7f))
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
                        .height(1.dp)
                        .background(color = colorResource(id = R.color.white))
                )
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Results",
                    style = typography.titleMedium.copy(color = colorResource(id = R.color.brighter_white)),
                    fontWeight = FontWeight.Light,
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(color = colorResource(id = R.color.white))
                )
                DreamTokenLayout(
                    totalDreamTokens = mainScreenViewModelState.dreamTokens.value,
                )
            }

            HorizontalPager(
                count = pages.size,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                colorResource(id = R.color.dark_blue).copy(alpha = 0.1f),
                                colorResource(id = R.color.white).copy(alpha = 0.1f),
                                colorResource(id = R.color.dark_blue).copy(alpha = 0.1f),
                            ),
                        )
                    ),
                pagerSate2
            ) { page ->

                when (page) {
                    0 -> {
                        AIPainterPage(
                            addEditDreamState = addEditDreamState,
                            painter = painter,
                            infiniteTransition = infiniteTransition,
                            pagerState = pagerState,
                        )
                    }

                    1 -> {
                        AIInterpreterPage(
                            addEditDreamState = addEditDreamState,
                            infiniteTransition = infiniteTransition,
                            pagerState = pagerState,
                        )
                    }

                    2 -> {
                        AIDreamAdvicePage(
                            addEditDreamState = addEditDreamState,
                            infiniteTransition = infiniteTransition,
                            pagerState = pagerState,
                        )
                    }
                    3 -> {
                        AIQuestionPage(
                            addEditDreamState = addEditDreamState,
                            infiniteTransition = infiniteTransition,
                            pagerState = pagerState,
                        )
                    }
                    4 -> {
                        AIStoryPage(
                            addEditDreamState = addEditDreamState,
                            infiniteTransition = infiniteTransition,
                            pagerState = pagerState,
                        )
                    }
                    5 -> {
                        AIMoodPage(
                            addEditDreamState = addEditDreamState,
                            infiniteTransition = infiniteTransition,
                            pagerState = pagerState,
                        )
                    }
                }
            }
            Row(
                Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { iteration ->
                    val color =
                        if (pagerSate2.currentPage == iteration) colorResource(id = R.color.white) else Color.White.copy(
                            alpha = 0.5f
                        )

                    val size = if (pagerSate2.currentPage == iteration) 12.dp else 10.dp
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(size)
                    )
                }
            }
        }

        GenerateButtonsLayout(
            addEditDreamState = addEditDreamState,
            pagerState = pagerState
        )
    }
}
