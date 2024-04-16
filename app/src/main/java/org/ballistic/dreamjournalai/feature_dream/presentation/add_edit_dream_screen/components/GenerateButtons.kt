package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.smarttoolfactory.animatedlist.AnimatedInfiniteLazyRow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamEvent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GenerateButtonsLayout(
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    textFieldState: TextFieldState,
    pagerState: PagerState
) {
    val buttons = listOf(
        "Paint",
        "Interpret",
        "Advice",
        "Question",
        "Story",
        "Mood"
    )

    val initialSelectedItem = -1 // Define the initial selected button index

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = colorResource(id = R.color.light_black).copy(alpha = 0.7f))
    ) {
        AnimatedInfiniteLazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            items = buttons,
            initialFirstVisibleIndex = initialSelectedItem,
            visibleItemCount = 4,
            spaceBetweenItems = 8.dp,
            itemScaleRange = 2,
            inactiveItemPercent = 20,
            showPartialItem = true,
            activeColor = Color.Cyan,
            inactiveColor = Color.Gray,
            itemContent = { _, _, item, _ ->
                when (item) {
                    "Paint" -> PaintCustomButton(
                        textFieldState = textFieldState,
                        pagerState = pagerState,
                        subtitle = "Paint",
                        modifer = Modifier.padding(8.dp),
                        hasText = false,
                        onAddEditEvent = onAddEditEvent
                    )

                    "Interpret" -> InterpretCustomButton(
                        textFieldState = textFieldState,
                        pagerState = pagerState,
                        subtitle = "Interpret",
                        modifer = Modifier.padding(8.dp),
                        hasText = false,
                        onAddEditEvent = onAddEditEvent
                    )

                    "Advice" -> GenerateAdviceButton(
                        textFieldState = textFieldState,
                        pagerState = pagerState,
                        subtitle = "Advice",
                        modifer = Modifier.padding(8.dp),
                        hasText = false,
                        onAddEditEvent = onAddEditEvent
                    )

                    "Question" -> AskQuestionButton(
                        textFieldState = textFieldState,
                        pagerState = pagerState,
                        subtitle = "Question",
                        modifer = Modifier.padding(8.dp),
                        hasText = false,
                        onAddEditEvent = onAddEditEvent
                    )

                    "Story" -> GenerateStoryButton(
                        textFieldState = textFieldState,
                        onAddEditEvent = onAddEditEvent,
                        pagerState = pagerState,
                        subtitle = "Story",
                        modifer = Modifier.padding(8.dp),
                        hasText = false,
                    )

                    "Mood" -> MoodAnalyzerButton(
                        textFieldState = textFieldState,
                        pagerState = pagerState,
                        subtitle = "Mood",
                        modifer = Modifier.padding(8.dp),
                        hasText = false,
                        onAddEditEvent = onAddEditEvent
                    )

                    else -> {
                    }
                }
            }
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PaintCustomButton(
    textFieldState: TextFieldState,
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    pagerState: PagerState,
    subtitle: String = "Paint Dream",
    size: Dp = 32.dp,
    fontSize: TextUnit = 14.sp,
    modifer: Modifier = Modifier,
    hasText: Boolean = true
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifer.clickable {
            scope.launch {
                delay(100)
                pagerState.animateScrollToPage(1)
            }
            onAddEditEvent(AddEditDreamEvent.ToggleDreamImageGenerationPopUpState(true))
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            painter = rememberAsyncImagePainter(R.drawable.baseline_brush_24),
            contentDescription = "Paint",
            modifier = Modifier
                .padding(8.dp)
                .size(size),
            tint = if (
                textFieldState.text.toString().length >= 10
                ) colorResource(R.color.sky_blue) else colorResource(
                id = R.color.white
            )
        )

        if (hasText) {
            Text(text = subtitle, fontSize = fontSize, color = colorResource(id = R.color.white))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InterpretCustomButton(
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    textFieldState: TextFieldState,
    pagerState: PagerState,
    subtitle: String = "Interpret Dream",
    size: Dp = 32.dp,
    fontSize: TextUnit = 14.sp,
    modifer: Modifier = Modifier,
    hasText: Boolean = true
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifer.clickable {
            scope.launch {
                delay(100)
                pagerState.animateScrollToPage(1)
            }
            onAddEditEvent(AddEditDreamEvent.ToggleDreamInterpretationPopUpState(true))
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = rememberAsyncImagePainter(R.drawable.interpret_vector),
            contentDescription = "Interpret",
            modifier = Modifier
                .padding(8.dp)
                .size(size),
            tint = if (textFieldState.text.length >= 10) colorResource(R.color.purple) else colorResource(
                id = R.color.white
            )
        )
        if (hasText) {
            Text(text = subtitle, fontSize = fontSize, color = colorResource(id = R.color.white))
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GenerateStoryButton(
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    textFieldState: TextFieldState,
    pagerState: PagerState,
    subtitle: String = "Dream Story",
    size: Dp = 32.dp,
    fontSize: TextUnit = 14.sp,
    modifer: Modifier = Modifier,
    hasText: Boolean = true
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifer.clickable {
            scope.launch {
                delay(100)
                pagerState.animateScrollToPage(1) // Change to the appropriate page for story generation
            }
            onAddEditEvent(AddEditDreamEvent.ToggleDreamStoryPopUpState(true))
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = rememberAsyncImagePainter(R.drawable.baseline_auto_fix_high_24), // Replace with appropriate vector graphic
            contentDescription = "Generate Story",
            modifier = Modifier
                .padding(8.dp)
                .size(size),
            tint = if (textFieldState.text.length >= 10) colorResource(R.color.lighter_yellow) else colorResource(
                id = R.color.white
            )
        )
        if (hasText) {
            Text(text = subtitle, fontSize = fontSize, color = colorResource(id = R.color.white))
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GenerateAdviceButton(
    textFieldState: TextFieldState,
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    pagerState: PagerState,
    subtitle: String = "Dream Advice",
    size: Dp = 32.dp,
    fontSize: TextUnit = 14.sp,
    modifer: Modifier = Modifier,
    hasText: Boolean = true
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifer
            .clickable {
                scope.launch {
                    delay(100)
                    pagerState.animateScrollToPage(1)
                }
                onAddEditEvent(AddEditDreamEvent.ToggleDreamAdvicePopUpState(true))
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = rememberAsyncImagePainter(R.drawable.baseline_lightbulb_24), // Replace with your icon for advice
            contentDescription = "Advice",
            modifier = Modifier
                .padding(8.dp)
                .size(size)
                .rotate(45f),
            tint = if (textFieldState.text.length >= 10) {
                colorResource(R.color.Yellow)
            } else {
                colorResource(
                    id = R.color.white
                )
            }
        )
        if (hasText) {
            Text(text = subtitle, fontSize = fontSize, color = colorResource(id = R.color.white))
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AskQuestionButton(
    textFieldState: TextFieldState,
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    pagerState: PagerState,
    subtitle: String = "Ask a Question",
    size: Dp = 32.dp,
    fontSize: TextUnit = 14.sp,
    modifer: Modifier = Modifier,
    hasText: Boolean = true
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifer
            .clickable {
                scope.launch {
                    delay(100)
                    pagerState.animateScrollToPage(1) // Update the page index as needed
                }
                onAddEditEvent(AddEditDreamEvent.ToggleDreamQuestionPopUpState(true))
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            painter = rememberAsyncImagePainter(R.drawable.baseline_question_answer_24), // Replace with your icon for asking questions
            contentDescription = "Question",
            modifier = Modifier
                .padding(8.dp)
                .size(size),
            tint = if (textFieldState.text.length >= 10) {
                colorResource(R.color.RedOrange) // Choose an appropriate color
            } else {
                colorResource(id = R.color.white)
            }
        )
        if (hasText) {
            Text(text = subtitle, fontSize = fontSize, color = colorResource(id = R.color.white))
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoodAnalyzerButton(
    // Define a class to manage the state related to mood analysis
    textFieldState: TextFieldState,
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    pagerState: PagerState,
    subtitle: String = "Analyze Mood",
    size: Dp = 32.dp,
    fontSize: TextUnit = 14.sp,
    modifer: Modifier = Modifier,
    hasText: Boolean = true
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifer
            .clickable {
                scope.launch {
                    delay(100)
                    pagerState.animateScrollToPage(1) // Change to the appropriate page for mood analysis
                }
                onAddEditEvent(AddEditDreamEvent.ToggleDreamMoodPopUpState(true))
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            painter = rememberAsyncImagePainter(R.drawable.baseline_mood_24), // Replace with an appropriate mood analyzer icon
            contentDescription = "Analyze Mood",
            modifier = Modifier
                .padding(8.dp)
                .size(size),
            tint = if (textFieldState.text.length >= 10) colorResource(R.color.green) else colorResource(
                id = R.color.white
            )
        )
        if (hasText) {
            Text(text = subtitle, fontSize = fontSize, color = colorResource(id = R.color.white))
        }
    }
}

@Composable
fun AdTokenLayout(
    onAdClick: (amount: Int) -> Unit = {},
    onDreamTokenClick: (amount: Int) -> Unit = {},
    isAdButtonVisible: Boolean = true,
    amount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isAdButtonVisible) {
            WatchAdButton(onClick = { onAdClick(amount) })
            Spacer(modifier = Modifier.height(8.dp))
        }
        DreamTokenGenerateButton(onClick = { onDreamTokenClick(amount) }, amount = amount)
    }
}

@Composable
fun WatchAdButton(
    onClick: () -> Unit = {}
) {
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.sky_blue)),
        shape = RoundedCornerShape(10.dp)
    ) {

        Icon(
            painter = rememberAsyncImagePainter(R.drawable.baseline_smart_display_24),
            contentDescription = "Watch Ad",
            modifier = Modifier
                .padding(8.dp)
                .size(56.dp),
            tint = Color.Black
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Watch Ad",
            modifier = Modifier
                .padding(16.dp),
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DreamTokenGenerateButton(
    onClick: () -> Unit,
    amount: Int
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.Yellow)),
        shape = RoundedCornerShape(10.dp)
    ) {


        Image(
            painter = rememberAsyncImagePainter(R.drawable.dream_token),
            contentDescription = "DreamToken",
            modifier = Modifier
                .padding(4.dp)
                .size(64.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Dream Token ($amount)",
            modifier = Modifier
                .padding(0.dp, 16.dp, 16.dp, 16.dp),
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
