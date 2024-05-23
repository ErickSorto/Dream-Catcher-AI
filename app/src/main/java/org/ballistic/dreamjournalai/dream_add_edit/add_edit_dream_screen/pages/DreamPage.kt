package org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.pages

import android.os.Vibrator
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components.GenerateButtonsLayout
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components.TransparentHintTextField
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components.onKeyboardDismiss
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.events.AddEditDreamEvent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DreamPage(
    titleTextFieldState: TextFieldState,
    contentTextFieldState: TextFieldState,
    vibrator: Vibrator,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit = {},
    animateToPage: (Int) -> Unit,
    snackBarState: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .background(color = Color.Transparent)
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        //make content disappear and reappear super quickly
        TransparentHintTextField(
            hint = LocalContext.current.getString(R.string.hint_title),
            isHintVisible = titleTextFieldState.text.isBlank(),
            singleLine = true,
            textStyle = typography.headlineMedium.copy(color = colorResource(id = R.color.white)),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    colorResource(id = R.color.light_black).copy(.7f)
                )
                .padding(16.dp)
                .onKeyboardDismiss {
                    focusManager.clearFocus()
                },
            textFieldState = titleTextFieldState

        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    colorResource(id = R.color.light_black).copy(.7f)
                ),
        ) {
            TransparentHintTextField(
                hint = LocalContext.current.getString(R.string.hint_description),
                isHintVisible = contentTextFieldState.text.isBlank(),
                textStyle = typography.bodyMedium.copy(
                    color = colorResource(id = R.color.white)
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp, 8.dp))
                    .padding(12.dp, 16.dp, 12.dp, 0.dp)
                    .weight(1f)
                    .background(
                        Color.Transparent
                    )
                    .onKeyboardDismiss {
                        focusManager.clearFocus()
                    },
                modifier2 = Modifier.fillMaxSize(),
                textFieldState = contentTextFieldState
            )


            GenerateButtonsLayout(
                onAddEditEvent = onAddEditDreamEvent,
                textFieldState = contentTextFieldState,
                snackBarState = {
                    snackBarState()
                },
                animateToPage = { index ->
                    animateToPage(index)
                },
                vibrator = vibrator
            )
        }

        //animate slowly
        Spacer(modifier = Modifier.consumeWindowInsets(WindowInsets.navigationBars).imePadding())

    }
}

