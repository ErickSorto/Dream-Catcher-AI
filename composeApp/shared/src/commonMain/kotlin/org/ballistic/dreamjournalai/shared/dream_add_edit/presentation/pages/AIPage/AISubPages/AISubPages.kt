package org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.pages.AIPage.AISubPages

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.ballistic.dreamjournalai.shared.dream_add_edit.domain.AIPageType
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components.ArcRotationAnimation
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.viewmodel.AIData
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.viewmodel.AddEditDreamState
import org.ballistic.dreamjournalai.dream_store.presentation.store_screen.components.singleClick
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.BrighterWhite
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.core.components.TypewriterText
import org.ballistic.dreamjournalai.shared.dream_add_edit.domain.AddEditDreamEvent
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components.UniversalButton


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.UniversalAIPage(
    contentType: AIPageType,
    addEditDreamState: AddEditDreamState,
    textFieldState: TextFieldState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    snackBarState: () -> Unit,
    onImageClick: (String) -> Unit,
    infiniteTransition: InfiniteTransition
) {
    val aiContent = contentType.getState(addEditDreamState)
    val lastClickTime = remember { mutableLongStateOf(0L) }

    when (contentType) {
        AIPageType.QUESTION -> {
            AIQuestionPage(
                addEditDreamState = addEditDreamState,
                textFieldState = textFieldState,
                onAddEditEvent = onAddEditEvent,
                snackBarState = snackBarState,
                infiniteTransition = infiniteTransition,
            )
        }

        AIPageType.PAINTER -> {
            AIPainterPage(
                addEditDreamState = addEditDreamState,
                textFieldState = textFieldState,
                onAddEditEvent = onAddEditEvent,
                snackBarState = snackBarState,
                infiniteTransition = infiniteTransition,
                animatedVisibilityScope = animatedVisibilityScope,
                onImageClick = singleClick(
                    lastClickTimeState = lastClickTime,
                    onClick = {
                        onImageClick(addEditDreamState.dreamAIImage.response)
                    }
                )
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
                color = BrighterWhite,
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp)
            )
            TypewriterText(
                text = aiContent.response.trim(),
                textAlign = TextAlign.Start,
                style = typography.bodyMedium,
                modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp),
                color = White,
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
                hasText = true
            )
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.AIPainterPage(
    addEditDreamState: AddEditDreamState,
    textFieldState: TextFieldState,
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    snackBarState: () -> Unit,
    infiniteTransition: InfiniteTransition,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onImageClick: () -> Unit
) {
    val imageState = addEditDreamState.dreamAIImage
    val painter = rememberAsyncImagePainter(
        model = imageState.response,
        filterQuality = FilterQuality.Low
    )

    val cont =  LocalPlatformContext.current
    val painterState by painter.state.collectAsState() // Collect the StateFlow into State

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (painterState is AsyncImagePainter.State.Loading) {
               ArcRotationAnimation(
                   infiniteTransition = infiniteTransition,
               )
            }
            AsyncImage(
                model =  ImageRequest.Builder(cont)
                    .data(imageState.response)
                    .crossfade(true)
                    .placeholderMemoryCacheKey("image/${imageState.response}") //  same key as shared element key
                    .memoryCacheKey("image/${imageState.response}") // same key as shared element key
                    .build(),
                contentDescription = "AI Generated Image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        onImageClick()
                    }
                    .sharedElement(
                        rememberSharedContentState(key = "image/${imageState.response}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(250)
                        }
                    ),
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
                modifier = Modifier.fillMaxSize(),
                hasText = true
            )
        }
    }
}

@Composable
fun AIQuestionPage(
    addEditDreamState: AddEditDreamState,
    textFieldState: TextFieldState,
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
                color = BrighterWhite,
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
            )
            Text(
                text = questionState.question + if (questionState.question.endsWith("?")) "" else "?",
                color = White,
                style = typography.titleSmall,
                modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 0.dp),
                textAlign = TextAlign.Center
            )

            TypewriterText(
                text = questionState.response.trim(),
                style = typography.bodyMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp),
                color = White,
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
                hasText = true
            )
        }
    }
}
