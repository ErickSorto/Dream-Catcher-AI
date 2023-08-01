package org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.core.Constants.EMAIL_LABEL

@Composable
fun EmailField(
    email: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    isVisible: MutableState<Boolean> = mutableStateOf(false)
) {

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
                    text = EMAIL_LABEL
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White.copy(alpha = 0.1f),
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                disabledLabelColor = Color.Black,
                disabledBorderColor = Color.Black,
                textColor = Color.Black,
                backgroundColor = Color.White.copy(alpha = 0.3f),
                leadingIconColor = Color.Black,
                trailingIconColor = Color.Black,
                errorLabelColor = Color.Red,
                errorBorderColor = Color.Red,
                errorCursorColor = Color.Red
            )
        )
    }
}