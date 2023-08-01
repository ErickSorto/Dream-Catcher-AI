package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import org.ballistic.dreamjournalai.R
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.core.components.DreamTokenLayout
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.*
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel.AddEditDreamState
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun AIPage(
    pagerState: PagerState,
    addEditDreamState: AddEditDreamState,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit,
    mainScreenViewModelState: MainScreenViewModelState
) {

    val pages = listOf("Painting", "Interpretation")
    val pagerSate2 = rememberPagerState()

    val responseState = addEditDreamState.dreamAIExplanation
    val imageState = addEditDreamState.dreamAIImage
    val contentState = addEditDreamState.dreamContent
    val detailState = addEditDreamState.dreamGeneratedDetails.response
    val infiniteTransition = rememberInfiniteTransition()
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


    if (addEditDreamState.imageGenerationPopUpState.value) {
        ImageGenerationPopUp(
            addEditDreamState = addEditDreamState,
            onDreamTokenClick = {
                addEditDreamState.imageGenerationPopUpState.value = false
                if (mainScreenViewModelState.dreamTokens.value <= 1) {
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
                                false
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
                            true
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
            onAdClick = {
                addEditDreamState.dreamInterpretationPopUpState.value = false
                scope.launch {
                    onAddEditDreamEvent(
                        AddEditDreamEvent.ClickGenerateAIResponse(
                            contentState,
                            activity,
                            true
                        )
                    )
                }
            },
            onDreamTokenClick = {
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
                                false
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
                    text = "AI Results",
                    style = typography.titleMedium.copy(color = colorResource(id = R.color.white)),
                    fontWeight = FontWeight.Bold,
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(color = colorResource(id = R.color.white))
                )
                DreamTokenLayout(mainScreenViewModelState = mainScreenViewModelState)
            }

            HorizontalPager(
                count = pages.size,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(brush = Brush.linearGradient(
                        colors = listOf(
                            colorResource(id = R.color.dark_blue).copy(alpha = 0.1f),
                            colorResource(id = R.color.white).copy(alpha = 0.1f),
                            colorResource(id = R.color.dark_blue).copy(alpha = 0.1f),
                        ),
                    ))
                ,
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