package org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.background
import org.ballistic.dreamjournalai.shared.dream_add_edit.domain.AddEditDreamEvent
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream.Companion.dreamBackgroundImages
import org.ballistic.dreamjournalai.shared.dream_journal_list.presentation.components.shimmerBrush
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DreamImageSelectionRow(
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit = {},
    dreamBackgroundImage: MutableState<Int>,
    defaultBackgroundIndex: Int = 0
) {
    // If no value is set, default to something
    if (dreamBackgroundImage.value == -1) {
        dreamBackgroundImage.value = defaultBackgroundIndex
    }

    // A local state just to highlight the selected item visually, if needed.
    val currentSelectedIndex = remember {
        mutableStateOf(dreamBackgroundImage.value)
    }

    // Define the infinite transition for animating border thickness
    val infiniteTransition = rememberInfiniteTransition()

    // Animate the border thickness between 4f and 6f
    val borderThickness by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Create the shimmer brush
    val shimmerBrush = shimmerBrush(infiniteTransition)
    val glowColor = Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 12.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dreamBackgroundImages.forEachIndexed { index, imageResId ->
            // Determine if the current item is selected
            val isSelected = index == currentSelectedIndex.value

            // Apply the animated border only if the item is selected
            val boxModifier = if (isSelected) {
                Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp)) // Rounded corners with 12.dp radius
                    .background(Color.Transparent)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(12.dp),
                        clip = false,
                        ambientColor = glowColor
                    )
                    .border(
                        width = borderThickness.dp,
                        brush = shimmerBrush,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        dreamBackgroundImage.value = index
                        currentSelectedIndex.value = index
                        onAddEditDreamEvent(AddEditDreamEvent.ChangeDreamBackgroundImage(index))
                    }
            } else {
                Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Transparent)
                    .clickable {
                        dreamBackgroundImage.value = index
                        currentSelectedIndex.value = index
                        onAddEditDreamEvent(AddEditDreamEvent.ChangeDreamBackgroundImage(index))
                    }
            }

            Box(
                modifier = boxModifier
            ) {
                Image(
                    painter = painterResource(imageResId),
                    contentDescription = stringResource(Res.string.background),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
