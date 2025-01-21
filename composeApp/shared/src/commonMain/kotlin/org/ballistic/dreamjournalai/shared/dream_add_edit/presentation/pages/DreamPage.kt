package org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.pages

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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.hint_description
import dreamjournalai.composeapp.shared.generated.resources.hint_title
import org.ballistic.dreamjournalai.shared.dream_add_edit.domain.AddEditDreamEvent
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components.GenerateButtonsLayout
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components.TransparentHintTextField
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components.onKeyboardDismiss
import org.jetbrains.compose.resources.stringResource

@Composable
fun DreamPage(
    titleTextFieldState: TextFieldState,
    contentTextFieldState: TextFieldState,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit,
    animateToPage: (Int) -> Unit,
    snackBarState: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .background(color = Color.Transparent)
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        Logger.d("DreamPage") { "focusManager: $focusManager" }
        //make content disappear and reappear super quickly
        TransparentHintTextField(
            hint = stringResource(Res.string.hint_title),
            isHintVisible = titleTextFieldState.text.isBlank(),
            singleLine = true,
            textStyle = typography.headlineMedium.copy(color = White),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    LightBlack.copy(.7f)
                )
                .padding(16.dp)
                .onKeyboardDismiss {
                    focusManager.clearFocus()
                },
            textFieldState = titleTextFieldState,
            onEvent = {
                Logger.d("DreamPage") { "onAddEditDreamEvent: $it" }
                onAddEditDreamEvent(it)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    LightBlack.copy(.7f)
                ),
        ) {
            TransparentHintTextField(
                hint = stringResource(Res.string.hint_description),
                isHintVisible = contentTextFieldState.text.isBlank(),
                textStyle = typography.bodyMedium.copy(
                    color = White
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
                textFieldState = contentTextFieldState,
                onEvent = {
                    onAddEditDreamEvent(it)
                }
            )

            GenerateButtonsLayout(
                onAddEditEvent = onAddEditDreamEvent,
                textFieldState = contentTextFieldState,
                snackBarState = {
                    snackBarState()
                },
                animateToPage = { index ->
                    animateToPage(index)
                }
            )
        }

        //animate slowly
        Spacer(modifier = Modifier.consumeWindowInsets(WindowInsets.navigationBars).imePadding())

    }
}

