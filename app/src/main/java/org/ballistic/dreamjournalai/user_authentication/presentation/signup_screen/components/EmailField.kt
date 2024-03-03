package org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.Constants.EMAIL_LABEL
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.onKeyboardDismiss

@Composable
fun EmailField(
    email: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    isVisible: MutableState<Boolean> = mutableStateOf(false)
) {
    val focusManager = LocalFocusManager.current
    AnimatedVisibility(
        visible = isVisible.value,
        enter = slideInHorizontally(initialOffsetX = { 1000 }),
        exit = slideOutHorizontally(targetOffsetX = { -1000 })
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { newValue ->
                onValueChange(newValue)
            },
            label = {
                Text(
                    text = EMAIL_LABEL,
                    color = Color.White
                )
            },
            textStyle = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 16.sp,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .onKeyboardDismiss {
                    focusManager.clearFocus()
                },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colorResource(id = R.color.light_black).copy(alpha = 0.7f),
                unfocusedContainerColor = colorResource(id = R.color.light_black).copy(alpha = 0.7f),
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                cursorColor = Color.White,
                errorCursorColor = Color.Red,
                focusedBorderColor = Color.White.copy(alpha = 0.4f),
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Black,
                errorBorderColor = Color.Red,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                disabledLabelColor = Color.Black,
                errorLabelColor = Color.Red,
            )
        )
    }
}