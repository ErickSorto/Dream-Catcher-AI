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
    Row(
        modifier = Modifier
            .padding(0.dp, 16.dp, 0.dp, 0.dp)
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(10.dp),
                color = colorResource(id = R.color.dark_blue).copy(alpha = 0.7f)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PaintCustomButton(
            addEditDreamState = addEditDreamState,
            pagerState = pagerState
        )
        InterpretCustomButton(
            addEditDreamState = addEditDreamState,
            pagerState = pagerState
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PaintCustomButton(
    addEditDreamState: AddEditDreamState,
    pagerState: PagerState,
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
        Text(text = "Paint Dream", fontSize = fontSize, color = colorResource(id = R.color.white))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun InterpretCustomButton(
    addEditDreamState: AddEditDreamState,
    pagerState: PagerState,
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
        Text(text = "Interpret Dream", fontSize = fontSize, color = colorResource(id = R.color.white))
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