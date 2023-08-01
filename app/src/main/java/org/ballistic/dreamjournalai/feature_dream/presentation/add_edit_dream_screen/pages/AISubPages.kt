package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.ArcRotationAnimation
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.InterpretCustomButton
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.PaintCustomButton
import org.ballistic.dreamjournalai.core.components.TypewriterText
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


    if (addEditDreamState.dreamAIExplanation.response != "") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding()
                .verticalScroll(rememberScrollState())
        ) {
            TypewriterText(
                text = responseState.response.trim(),
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp),
                color = colorResource(id = org.ballistic.dreamjournalai.R.color.white),
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
                .padding(16.dp, 16.dp, 16.dp, 16.dp)
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