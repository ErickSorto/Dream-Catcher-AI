package org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.background
import org.ballistic.dreamjournalai.shared.dream_add_edit.domain.AddEditDreamEvent
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream.Companion.dreamBackgroundImages
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DreamImageSelectionRow(
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit = {},
    // Turn this into a mutable state so we can update it
    dreamBackgroundImage: MutableState<Int>,
    // optional: pass a default if you want
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        dreamBackgroundImages.forEachIndexed { index, imageResId ->
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .clickable {
                        dreamBackgroundImage.value = index
                        currentSelectedIndex.value = index
                        onAddEditDreamEvent(AddEditDreamEvent.ChangeDreamBackgroundImage(index))
                    }
                    // Optionally highlight if selected
                    .border(
                        width = if (index == currentSelectedIndex.value) 3.dp else 0.dp,
                        color = if (index == currentSelectedIndex.value) Color.Cyan else Color.Transparent,
                        shape = CircleShape
                    )
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