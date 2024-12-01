package org.ballistic.dreamjournalai.dream_add_edit.presentation.components

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FullScreenImageScreen(
    imageID: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackPress: () -> Unit
) {
    BackHandler {
        onBackPress()
    }

    val decodedUrl = Uri.decode(imageID)

    Log.d("FullScreenImageScreen", "imageID: $imageID")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        //Back icon top left
        Icon(
            painter = painterResource(id = R),
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier
                .padding(16.dp)
                .size(24.dp)
                .clickable {
                    onBackPress()
                }
        )

        Image(
            painter = rememberAsyncImagePainter(model = decodedUrl),
            contentDescription = "Full Screen Image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .sharedElement(
                    rememberSharedContentState(key = "image/$decodedUrl"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ ->
                        tween(500)
                    }
                ),
            contentScale = ContentScale.FillBounds
        )
    }
}