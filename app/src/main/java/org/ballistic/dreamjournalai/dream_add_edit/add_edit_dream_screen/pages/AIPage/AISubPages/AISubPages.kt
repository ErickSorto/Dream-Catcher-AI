package org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.pages.AIPage.AISubPages

import android.content.res.Resources
import android.os.Vibrator
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.TypewriterText
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.AIPageType
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components.ArcRotationAnimation
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components.UniversalButton
import org.ballistic.dreamjournalai.dream_add_edit.domain.AddEditDreamEvent
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.viewmodel.AIData
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.viewmodel.AddEditDreamState


@Composable
fun UniversalAIPage(
    contentType: AIPageType,
    addEditDreamState: AddEditDreamState,
    textFieldState: TextFieldState,
    vibrator: Vibrator,
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    snackBarState: () -> Unit,
    infiniteTransition: InfiniteTransition
) {
    val aiContent = contentType.getState(addEditDreamState)

    when (contentType) {
        AIPageType.QUESTION -> {
            AIQuestionPage(
                addEditDreamState = addEditDreamState,
                textFieldState = textFieldState,
                onAddEditEvent = onAddEditEvent,
                snackBarState = snackBarState,
                vibrator = vibrator,
                infiniteTransition = infiniteTransition,
            )
        }

        AIPageType.PAINTER -> {
            AIPainterPage(
                addEditDreamState = addEditDreamState,
                textFieldState = textFieldState,
                onAddEditEvent = onAddEditEvent,
                snackBarState = snackBarState,
                vibrator = vibrator,
                infiniteTransition = infiniteTransition,
            )
        }

        else -> {
            StandardAIPageLayout(
                aiContent = aiContent,
                title = contentType.title,
                contentType = contentType,
                textFieldState = textFieldState,
                onAddEditEvent = onAddEditEvent,
                snackBarState = snackBarState,
                vibrator = vibrator,
                infiniteTransition = infiniteTransition
            )
        }
    }
}

@Composable
fun StandardAIPageLayout(
    aiContent: AIData,
    title: String,
    contentType: AIPageType,
    textFieldState: TextFieldState,
    vibrator: Vibrator,
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    snackBarState: () -> Unit,
    infiniteTransition: InfiniteTransition
) {

    if (aiContent.isLoading) {
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
    if (aiContent.response.isNotEmpty() && !aiContent.isLoading) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = title,
                color = colorResource(id = R.color.brighter_white),
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp)
            )
            TypewriterText(
                text = aiContent.response.trim(),
                textAlign = TextAlign.Start,
                style = typography.bodyMedium,
                modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp),
                color = colorResource(id = R.color.white),
            )
        }
    }

    if (aiContent.response.isEmpty() && !aiContent.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            // No content and not loading, show Universal Button
            UniversalButton(
                buttonType = contentType.buttonType,
                textFieldState = textFieldState,
                onAddEditEvent = onAddEditEvent,
                snackBarState = snackBarState,
                size = 160.dp,  // Adjusted size
                fontSize = 24.sp,  // Adjusted font size
                modifier = Modifier.fillMaxSize(),
                vibrator = vibrator,
                hasText = true
            )
        }
    }
}


@Composable
fun AIPainterPage(
    addEditDreamState: AddEditDreamState,
    textFieldState: TextFieldState,
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    vibrator: Vibrator,
    snackBarState: () -> Unit,
    infiniteTransition: InfiniteTransition,
) {
    val imageState = addEditDreamState.dreamAIImage

    // Remember a flag to track if the first image load has been processed

    val alphaAnimatable = remember { Animatable(0f) } // Start fully transparent
    val scaleAnimatable = remember { Animatable(0.95f) } // Start slightly zoomed out

    LaunchedEffect(imageState.response) {
        // Apply delay only for the first load
        if (imageState.response != "" && imageState.isLoading) {
            delay(500) // Half a second delay for the first load

        }

        // Ensure there's an image to load
        if (imageState.response != "") {
            // Reset animations to initial state for the new image
            alphaAnimatable.snapTo(0f)
            scaleAnimatable.snapTo(0.97f)

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

    if (imageState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(8.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            ArcRotationAnimation(
                infiniteTransition = infiniteTransition,
            )
        }
    }

    if (imageState.response != "" && !imageState.isLoading) {
        Log.d("AIPainterPage", "Image Response: ${imageState.response}")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                rememberAsyncImagePainter(model = imageState.response),
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
    }

    if (imageState.response == "" && !imageState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            UniversalButton(
                textFieldState = textFieldState,
                buttonType = AIPageType.PAINTER.buttonType,
                size = 160.dp,
                fontSize = 24.sp,
                onAddEditEvent = onAddEditEvent,
                snackBarState = snackBarState,
                vibrator = vibrator,
                modifier = Modifier.fillMaxSize(),
                hasText = true
            )
        }
    }
}

fun getLocalizedString(res: Resources, key: String, packageName: String): String {
    val resourceId = res.getIdentifier(key, "string", packageName)
    return if (resourceId != 0) {
        res.getString(resourceId)
    } else {
        key
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AIQuestionPage(
    addEditDreamState: AddEditDreamState,
    textFieldState: TextFieldState,
    vibrator: Vibrator,
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    snackBarState: () -> Unit,
    infiniteTransition: InfiniteTransition,
) {
    val questionState = addEditDreamState.dreamAIQuestionAnswer

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

    if (questionState.response != "" && !questionState.isLoading) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Dream Answer",
                color = colorResource(id = R.color.brighter_white),
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
            )
            Text(
                text = questionState.question + if (questionState.question.endsWith("?")) "" else "?",
                color = colorResource(id = R.color.white),
                style = typography.titleSmall,
                modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 0.dp),
                textAlign = TextAlign.Center
            )

            TypewriterText(
                text = questionState.response.trim(),
                style = typography.bodyMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp),
                color = colorResource(id = R.color.white),
            )
        }
    }

    // Button to ask a new question
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (questionState.response == "" && !questionState.isLoading) {
            UniversalButton(
                textFieldState = textFieldState,
                size = 160.dp,
                fontSize = 24.sp,
                onAddEditEvent = onAddEditEvent,
                snackBarState = snackBarState,
                modifier = Modifier.fillMaxSize(),
                buttonType = AIPageType.QUESTION.buttonType,
                vibrator = vibrator,
                hasText = true
            )
        }
    }
}
