package org.ballistic.dreamjournalai.shared.dream_fullscreen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.baseline_report_24
import dreamjournalai.composeapp.shared.generated.resources.ic_baseline_arrow_left_alt_24
import org.ballistic.dreamjournalai.shared.dream_main.domain.MainScreenEvent
import org.ballistic.dreamjournalai.shared.core.components.ActionBottomSheet
import org.ballistic.dreamjournalai.shared.core.util.BackHandler
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FullScreenImageScreen(
    imageID: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onFullScreenEvent: (FullScreenEvent) -> Unit,
    onMainEvent: (MainScreenEvent) -> Unit,
    onBackPress: () -> Unit
) {
    BackHandler(true) {
        onBackPress()
    }
    val backButtonEnabled = remember { mutableStateOf(true) }

    val flagContentBottomSheetState = remember { mutableStateOf(false) }
    Scaffold(
        snackbarHost = {
            SnackbarHost(SnackbarHostState())
        },
    ) { innerPadding ->
        innerPadding
        if (flagContentBottomSheetState.value) {
            ActionBottomSheet (
                title = "Flag Content",
                message = "Are you sure you want to flag this content?",
                buttonText = "Flag",
                onClick = {
                    onFullScreenEvent(FullScreenEvent.Flag(imageID,
                        onSuccessEvent = {
                            onMainEvent(MainScreenEvent.ShowSnackBar(
                                "Image flagged successfully",
                            ))
                        }
                    ))
                    flagContentBottomSheetState.value = false
                },
                onClickOutside = {
                    flagContentBottomSheetState.value = false
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    onBackPress()
                    backButtonEnabled.value = false
                },
                enabled = backButtonEnabled.value,
                modifier = Modifier
                    .padding(4.dp, 8.dp, 8.dp, 8.dp)
                    .padding(top = 18.dp)
                    .align(Alignment.TopStart)
            ){
                Icon(
                    painterResource(Res.drawable.ic_baseline_arrow_left_alt_24),
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }

            //Option 3 dot icon
            IconButton(
                onClick = {
                    flagContentBottomSheetState.value = true
                },
                modifier = Modifier
                    .padding(8.dp, 8.dp, 4.dp, 8.dp)
                    .padding(top = 18.dp)
                    .align(Alignment.TopEnd)
            ){
                Icon(
                    painterResource(Res.drawable.baseline_report_24),
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }



            AsyncImage(
                model =  ImageRequest.Builder(LocalPlatformContext.current)
                    .data(imageID)
                    .crossfade(true)
                    .placeholderMemoryCacheKey("image/$imageID") //  same key as shared element key
                    .memoryCacheKey("image/$imageID") // same key as shared element key
                    .build(),
                contentDescription = "Full Screen Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .sharedElement(
                        rememberSharedContentState(key = "image/$imageID"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(500)
                        }
                    ),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}