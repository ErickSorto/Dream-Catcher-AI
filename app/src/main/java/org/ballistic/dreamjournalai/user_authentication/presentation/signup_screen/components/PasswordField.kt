package org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.core.Constants.PASSWORD_LABEL

@Composable
fun PasswordField(
    isLoginLayout: Boolean,
    password: String,
    onValueChange: (String) -> Unit,
    forgotPassword: () -> Unit,
    modifier: Modifier = Modifier,
    isVisible: MutableState<Boolean> = remember { mutableStateOf(false ) }
) {
    var passwordIsVisible by remember { mutableStateOf(false) }
    AnimatedVisibility(
        visible = isVisible.value,
        enter = slideInHorizontally(initialOffsetX = { 1000 })
    ) {
        OutlinedTextField(
            value = password,
            onValueChange = {
                onValueChange(it)
            },
            label = {
                Text(
                    text = PASSWORD_LABEL
                )
            },
            singleLine = true,
            visualTransformation = if (passwordIsVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            leadingIcon = {
                val icon = if (passwordIsVisible) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }
                IconButton(
                    onClick = {
                        passwordIsVisible = !passwordIsVisible
                    }
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            },
            trailingIcon = {
                if (isLoginLayout) {
                    TextButton(onClick = { forgotPassword() }) {
                        Text(text = "Forgot password?", color = Color.Black.copy(alpha = 0.5f))
                    }
                }
            },
            modifier = modifier.fillMaxWidth().padding(bottom = 8.dp),
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