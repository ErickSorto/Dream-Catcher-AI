package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.smarttoolfactory.animatedlist.AnimatedInfiniteLazyRow
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream.Companion.dreamBackgroundImages
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamEvent

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DreamImageSelectionRow(
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit = {},
    dreamBackgroundImage: MutableState<Int>
) {

    val initialSelectedItem = dreamBackgroundImages.indexOf(dreamBackgroundImage.value)

    AnimatedInfiniteLazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
        items = dreamBackgroundImages,
        initialFirstVisibleIndex = initialSelectedItem - 3,
        selectorIndex = initialSelectedItem - 1,
        visibleItemCount = 6,
        spaceBetweenItems = 8.dp,
        itemScaleRange = 1,
        showPartialItem = true,
        activeColor = Color.Cyan,
        inactiveColor = Color.Gray,
        itemContent = { animationProgress, index, image, width ->
            val scale = animationProgress.scale

           //  selectedItem = animationProgress.itemIndex
           // dreamBackgroundImage.value = selectedItem
            Box(
                modifier = Modifier
                    .width(width)
                    .height(width)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .shadow(width, CircleShape)
                    .clickable {
                        onAddEditDreamEvent(AddEditDreamEvent.ChangeDreamBackgroundImage(image))
                        dreamBackgroundImage.value = image
                    }
            ){
                Image(
                    painter = rememberAsyncImagePainter(image),
                    contentDescription = "Color",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    )
}