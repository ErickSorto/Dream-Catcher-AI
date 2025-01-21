package org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreInterceptKeyBeforeSoftKeyboard
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.dream_add_edit.domain.AddEditDreamEvent
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White

@Composable
fun TransparentHintTextField(
    hint: String,
    modifier: Modifier = Modifier,
    modifier2: Modifier = Modifier,
    onEvent: (AddEditDreamEvent) -> Unit = {},
    isHintVisible: Boolean = true,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActionHandler? = null,
    textFieldState: TextFieldState
) {
    val scope = rememberCoroutineScope()
    Logger.d("TransparentHintTextField") { "TransparentHintTextField check" }
    Box(modifier = modifier)
    {
        BasicTextField(
            state = textFieldState,
            textStyle = textStyle,
            lineLimits = if (singleLine) TextFieldLineLimits.SingleLine else TextFieldLineLimits.Default,
            scrollState = rememberScrollState(),
            keyboardOptions = keyboardOptions,
            onKeyboardAction = keyboardActions,
            modifier = modifier2
                .fillMaxWidth(),
            inputTransformation = EventTriggeringTransformation { event ->
                Logger.d("TransparentHintTextField") { "EventTriggeringTransformation is actually called" }
                scope.launch {
                    onEvent(event)
                }
            },
            cursorBrush = Brush.verticalGradient(
                colors = listOf(
                    White,
                    White
                )
            ),
        )

        if (isHintVisible) {
            Text(text = hint, style = textStyle, color = White)
        }
    }
}/**
 * Provides a callback when a text field has focus and the back button is pressed.
 *
 * This is currently useful to work around this bug: https://issuetracker.google.com/issues/312895384
 *
 * https://stackoverflow.com/a/77043957/2191796
 */
fun Modifier.onKeyboardDismiss(handleOnBackPressed: () -> Unit): Modifier =
    @OptIn(ExperimentalComposeUiApi::class)
    this.onPreInterceptKeyBeforeSoftKeyboard {
        if (it.key.keyCode == 17179869184) {
            handleOnBackPressed.invoke()
        }
        true
    }

class EventTriggeringTransformation(
    private val onEvent: (AddEditDreamEvent) -> Unit
) : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        Logger.d("EventTriggeringTransformation") { "transformInput" }
        onEvent(AddEditDreamEvent.ContentHasChanged)
    }
}