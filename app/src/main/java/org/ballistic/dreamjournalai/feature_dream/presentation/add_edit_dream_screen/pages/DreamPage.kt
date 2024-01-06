package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.GenerateButtonsLayout
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.TransparentHintTextField
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel.AddEditDreamState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DreamPage(
    pagerState: PagerState,
    addEditDreamState: AddEditDreamState,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit = {},
) {

    Column(
        modifier = Modifier
            .background(color = Color.Transparent)
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp, top = 16.dp)
    ) {

        TransparentHintTextField(
            text = addEditDreamState.dreamTitle,
            hint = LocalContext.current.getString(R.string.hint_title),
            onValueChange = {
                onAddEditDreamEvent(AddEditDreamEvent.EnteredTitle(it))
            },
            isHintVisible = addEditDreamState.dreamTitle.isBlank(),
            singleLine = true,
            textStyle = typography.headlineMedium.copy(color = colorResource(id = R.color.white)),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    colorResource(id = R.color.dark_blue).copy(.7f)
                )
                .padding(16.dp)
                .focusable()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TransparentHintTextField(
            text = addEditDreamState.dreamContent,
            hint = LocalContext.current.getString(R.string.hint_description),
            onValueChange = {
                onAddEditDreamEvent(AddEditDreamEvent.EnteredContent(it))
            },
            isHintVisible = addEditDreamState.dreamContent.isBlank(),
            textStyle = typography.bodyLarge.copy(
                color = colorResource(id = R.color.white)
            ),
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    colorResource(id = R.color.dark_blue).copy(.7f)
                )
                .padding(8.dp)
                .onFocusEvent {
                }
                .focusable()
        )

        if (addEditDreamState.dreamContent.isNotBlank() && addEditDreamState.dreamContent.length > 10) {
            GenerateButtonsLayout(
                addEditDreamState = addEditDreamState,
                pagerState = pagerState
            )
        }
    }
}