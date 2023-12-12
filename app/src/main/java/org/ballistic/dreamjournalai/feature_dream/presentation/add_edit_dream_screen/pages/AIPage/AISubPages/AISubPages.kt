package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AISubPages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
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

@OptIn(ExperimentalPagerApi::class)
@RequiresApi(Build.VERSION_CODES.O)
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
                color = colorResource(id = R.color.white),
                fontSize = 24.sp,
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = colorResource(id = R.color.white))
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

@OptIn(ExperimentalPagerApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AIPainterPage(
    addEditDreamState: AddEditDreamState,
    painter: Painter,
    infiniteTransition: InfiniteTransition,
    pagerState: PagerState,
) {
    val imageState = addEditDreamState.dreamAIImage

    AnimatedVisibility(
        visible = addEditDreamState.dreamAIImage.image != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Image(
            painter = painter,
            contentDescription = "AI Generated Image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(8.dp, 8.dp, 8.dp, 8.dp)
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

@OptIn(ExperimentalPagerApi::class)
@RequiresApi(Build.VERSION_CODES.O)
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
                color = colorResource(id = R.color.white),
                fontSize = 24.sp,
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = colorResource(id = R.color.white))
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

@OptIn(ExperimentalPagerApi::class)
@RequiresApi(Build.VERSION_CODES.O)
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
                color = colorResource(id = R.color.white),
                fontSize = 24.sp,
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 8.dp)
            )
            //center question
            Text(
                text = questionState.question + if (questionState.question.endsWith("?")) "" else "?",
                color = colorResource(id = R.color.white),
                fontSize = 16.sp,
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 4.dp),
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = colorResource(id = R.color.white))
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

@OptIn(ExperimentalPagerApi::class)
@RequiresApi(Build.VERSION_CODES.O)
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
                color = colorResource(id = R.color.white),
                fontSize = 24.sp,
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = colorResource(id = R.color.white))
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

@OptIn(ExperimentalPagerApi::class)
@RequiresApi(Build.VERSION_CODES.O)
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
                color = colorResource(id = R.color.white),
                fontSize = 24.sp,
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = colorResource(id = R.color.white))
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
