package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.smarttoolfactory.animatedlist.AnimatedInfiniteLazyRow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel.AddEditDreamState

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun GenerateButtonsLayout(
    addEditDreamState: AddEditDreamState,
    pagerState: PagerState
) {
    val buttons = listOf(
        "Paint",
        "Interpret",
        "Advice",
        "Question"
        // Add any other button labels here
    )

    val initialSelectedItem = -1 // Define the initial selected button index

    Box(
        modifier = Modifier
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = colorResource(id = R.color.dark_blue).copy(alpha = 0.7f))
    ) {
        AnimatedInfiniteLazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
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
                    "Paint" -> PaintCustomButton(addEditDreamState, pagerState, "Paint")
                    "Interpret" -> InterpretCustomButton(addEditDreamState, pagerState, "Interpret")
                    "Advice" -> GenerateAdviceButton(addEditDreamState, pagerState, "Advice")
                    "Question" -> AskQuestionButton(addEditDreamState, pagerState, "Question")
                }
            }
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PaintCustomButton(
    addEditDreamState: AddEditDreamState,
    pagerState: PagerState,
    subtitle : String = "Paint Dream",
    size: Dp = 40.dp,
    fontSize: TextUnit = 16.sp
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.IconButton(
            onClick = {
                scope.launch {
                    delay(100)
                    pagerState.animateScrollToPage(1)
                }
                addEditDreamState.imageGenerationPopUpState.value = true
            },
            modifier = Modifier.size(size)
        ) {
            Icon(
                painter = rememberAsyncImagePainter(R.drawable.paint_vector),
                contentDescription = "Paint",
                modifier = Modifier
                    .size(size)
                    .rotate(45f),
                tint = if (addEditDreamState.dreamInfo.dreamIsLucid) colorResource(R.color.sky_blue) else colorResource(
                    id = R.color.white
                )
            )

        }
        Text(text = subtitle, fontSize = fontSize, color = colorResource(id = R.color.white))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun InterpretCustomButton(
    addEditDreamState: AddEditDreamState,
    pagerState: PagerState,
    subtitle: String = "Interpret Dream",
    size: Dp = 40.dp,
    fontSize: TextUnit = 16.sp
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.IconButton(
            onClick = {
                scope.launch {
                    delay(100)
                    pagerState.animateScrollToPage(1)
                }
                addEditDreamState.dreamInterpretationPopUpState.value = true
            },
            modifier = Modifier.size(size),
        ) {
            Icon(
                painter = rememberAsyncImagePainter(R.drawable.interpret_vector),
                contentDescription = "Interpret",
                modifier = Modifier
                    .size(size)
                    .rotate(45f),
                tint = if (addEditDreamState.dreamInfo.dreamIsFavorite) colorResource(R.color.Yellow) else colorResource(
                    id = R.color.white
                )
            )
        }
        Text(text = subtitle, fontSize = fontSize, color = colorResource(id = R.color.white))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun GenerateAdviceButton(
    addEditDreamState: AddEditDreamState,
    pagerState: PagerState,
    subtitle: String = "Dream Advice",
    size: Dp = 40.dp,
    fontSize: TextUnit = 16.sp
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.IconButton(
            onClick = {
                scope.launch {
                    delay(100)
                    pagerState.animateScrollToPage(3)
                }
                addEditDreamState.dreamAdvicePopUpState.value = true
            },
            modifier = Modifier.size(size)
        ) {
            Icon(
                painter = rememberAsyncImagePainter(R.drawable.baseline_lightbulb_24), // Replace with your icon for advice
                contentDescription = "Advice",
                modifier = Modifier
                    .size(size)
                    .rotate(45f),
                tint = if (addEditDreamState.dreamContent.length >= 10){
                    colorResource(R.color.Yellow)
                } else{
                    colorResource(
                        id = R.color.white
                    )
                }
            )
        }
        Text(text = subtitle, fontSize = fontSize, color = colorResource(id = R.color.white))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun AskQuestionButton(
    addEditDreamState: AddEditDreamState,
    pagerState: PagerState,
    subtitle: String = "Ask Question",
    size: Dp = 40.dp,
    fontSize: TextUnit = 16.sp
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.IconButton(
            onClick = {
                scope.launch {
                    delay(100)
                    pagerState.animateScrollToPage(4) // Update the page index as needed
                }
                addEditDreamState.questionPopUpState.value = true // Handle the state for asking a question
            },
            modifier = Modifier.size(size)
        ) {
            Icon(
                painter = rememberAsyncImagePainter(R.drawable.baseline_question_mark_24), // Replace with your icon for asking questions
                contentDescription = "Question",
                modifier = Modifier
                    .size(size),
                tint = if (addEditDreamState.dreamContent.length >= 10){
                    colorResource(R.color.sky_blue) // Choose an appropriate color
                } else{
                    colorResource(id = R.color.white)
                }
            )
        }
        Text(text = subtitle, fontSize = fontSize, color = colorResource(id = R.color.white))
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdTokenLayout(
    onAdClick: () -> Unit = {},
    onDreamTokenClick: () -> Unit = {},
    amount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        WatchAdButton(onClick = { onAdClick() })
        Spacer(modifier = Modifier.height(8.dp))
        DreamTokenGenerateButton(onClick = { onDreamTokenClick() }, amount = amount)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
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
        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.sky_blue)),
        shape = RoundedCornerShape(10.dp)
    ) {

        Icon(
            painter = rememberAsyncImagePainter(R.drawable.baseline_smart_display_24),
            contentDescription = "Watch Ad",
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp),
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
        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.Yellow)),
        shape = RoundedCornerShape(10.dp)
    ) {


        Image(
            painter = rememberAsyncImagePainter(R.drawable.dream_token),
            contentDescription = "DreamToken",
            modifier = Modifier
                .padding(16.dp, 16.dp, 0.dp, 16.dp)
                .size(40.dp)
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