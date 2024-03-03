package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AISubPages

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.TypewriterText
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.ArcRotationAnimation
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.AskQuestionButton
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.GenerateAdviceButton
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.GenerateStoryButton
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.InterpretCustomButton
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.MoodAnalyzerButton
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.PaintCustomButton
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel.AddEditDreamState
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AIInterpreterPage(
    addEditDreamState: AddEditDreamState,
    infiniteTransition: InfiniteTransition,
    pagerState: PagerState,
) {
    val responseState = addEditDreamState.dreamAIExplanation


    if (addEditDreamState.dreamAIExplanation.response != "" && !responseState.isLoading) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Dream Interpretation",
                color = colorResource(id = R.color.brighter_white),
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 4.dp)
            )

            TypewriterText(
                text = responseState.response.trim(),
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp),
                color = colorResource(id = R.color.white),
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (responseState.response == "" && !responseState.isLoading) {
            InterpretCustomButton(
                addEditDreamState = addEditDreamState,
                pagerState = pagerState,
                size = 120.dp,
                fontSize = 24.sp
            )
        }
    }

    if (responseState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp, 0.dp, 16.dp, 16.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            ArcRotationAnimation(
                infiniteTransition = infiniteTransition,
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AIPainterPage(
    addEditDreamState: AddEditDreamState,
    painter: Painter,
    infiniteTransition: InfiniteTransition,
    pagerState: PagerState,
) {
    val imageState = addEditDreamState.dreamAIImage

    // Remember a flag to track if the first image load has been processed

    val alphaAnimatable = remember { Animatable(0f) } // Start fully transparent
    val scaleAnimatable = remember { Animatable(0.98f) } // Start slightly zoomed out

    LaunchedEffect(imageState.image) {
        // Apply delay only for the first load
        if (imageState.image != null && imageState.isLoading) {
            delay(500) // Half a second delay for the first load
           
        }

        // Ensure there's an image to load
        if (imageState.image != null) {
            // Reset animations to initial state for the new image
            alphaAnimatable.snapTo(0f)
            scaleAnimatable.snapTo(0.98f)

            // Start animations
            alphaAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing)
            )
            scaleAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (imageState.image != null) {
            Image(
                painter = painter,
                contentDescription = "AI Generated Image",
                modifier = Modifier
                    .graphicsLayer {
                        alpha = alphaAnimatable.value
                        scaleX = scaleAnimatable.value
                        scaleY = scaleAnimatable.value
                    }
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
        }
        if (imageState.image == null && !imageState.isLoading) {
            PaintCustomButton(
                addEditDreamState = addEditDreamState,
                pagerState = pagerState,
                size = 120.dp,
                fontSize = 24.sp
            )
        }
        if (imageState.isLoading) {
            ArcRotationAnimation(
                infiniteTransition = infiniteTransition,
            )
        }
    }
}








@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AIDreamAdvicePage(
    addEditDreamState: AddEditDreamState,
    infiniteTransition: InfiniteTransition,
    pagerState: PagerState,
) {
    val adviceState = addEditDreamState.dreamAIAdvice

    if (adviceState.advice != "" && !adviceState.isLoading) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Dream Advice",
                color = colorResource(id = R.color.brighter_white),
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 4.dp)
            )

            TypewriterText(
                text = adviceState.advice.trim(),
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp),
                color = colorResource(id = R.color.white),
            )
        }
    }

    // Button to generate advice
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (adviceState.advice == "" && !adviceState.isLoading) {
            GenerateAdviceButton(
                addEditDreamState = addEditDreamState,
                pagerState = pagerState,
                size = 120.dp,
                fontSize = 24.sp
            )
        }
    }

    // Loading animation while AI is generating advice
    if (adviceState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp, 0.dp, 16.dp, 16.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            ArcRotationAnimation(
                infiniteTransition = infiniteTransition,
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AIQuestionPage(
    addEditDreamState: AddEditDreamState,
    infiniteTransition: InfiniteTransition,
    pagerState: PagerState,
) {
    val questionState = addEditDreamState.dreamQuestionAIAnswer

    if (questionState.answer != "" && !questionState.isLoading) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Dream Answer",
                color = colorResource(id = R.color.brighter_white),
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 8.dp)
            )

            Text(
                text = questionState.question + if (questionState.question.endsWith("?")) "" else "?",
                color = colorResource(id = R.color.white),
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 4.dp),
                textAlign = TextAlign.Center
            )

            TypewriterText(
                text = questionState.answer.trim(),
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp),
                color = colorResource(id = R.color.white),
            )
        }
    }

    // Button to ask a new question
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (questionState.answer == "" && !questionState.isLoading) {
            AskQuestionButton(
                addEditDreamState = addEditDreamState,
                pagerState = pagerState,
                size = 120.dp,
                fontSize = 24.sp
            )
        }
    }

    // Loading animation while AI is generating response
    if (questionState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp, 0.dp, 16.dp, 16.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            ArcRotationAnimation(
                infiniteTransition = infiniteTransition,
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AIStoryPage(
    addEditDreamState: AddEditDreamState,
    infiniteTransition: InfiniteTransition,
    pagerState: PagerState,
) {
    val storyState = addEditDreamState.dreamStoryGeneration

    if (storyState.story != "" && !storyState.isLoading) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Dream Story",
                color = colorResource(id = R.color.brighter_white),
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 4.dp)
            )

            TypewriterText(
                text = storyState.story.trim(),
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp),
                color = colorResource(id = R.color.white),
            )
        }
    }

    // Button to generate a new story
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (storyState.story == "" && !storyState.isLoading) {
            GenerateStoryButton(
                addEditDreamState = addEditDreamState,
                pagerState = pagerState,
                size = 120.dp,
                fontSize = 24.sp
            )
        }
    }

    // Loading animation while AI is generating story
    if (storyState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp, 0.dp, 16.dp, 16.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            ArcRotationAnimation(
                infiniteTransition = infiniteTransition,
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AIMoodPage(
    addEditDreamState: AddEditDreamState,
    infiniteTransition: InfiniteTransition,
    pagerState: PagerState,
) {
    val moodState = addEditDreamState.dreamMoodAIAnalyser

    if (moodState.mood != "" && !moodState.isLoading) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Dream Mood",
                color = colorResource(id = R.color.brighter_white),
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 4.dp)
            )

            TypewriterText(
                text = moodState.mood.trim(),
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp),
                color = colorResource(id = R.color.white),
            )
        }
    }

    // Button to generate a new mood
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (moodState.mood == "" && !moodState.isLoading) {
            MoodAnalyzerButton(
                addEditDreamState = addEditDreamState,
                pagerState = pagerState,
                size = 120.dp,
                fontSize = 24.sp
            )
        }
    }

    if (moodState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp, 0.dp, 16.dp, 16.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            ArcRotationAnimation(
                infiniteTransition = infiniteTransition,
            )
        }
    }
}
