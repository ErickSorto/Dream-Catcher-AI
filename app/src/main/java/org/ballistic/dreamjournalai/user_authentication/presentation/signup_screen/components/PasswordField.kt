package org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.Constants.PASSWORD_LABEL
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.onKeyboardDismiss

@Composable
fun PasswordField(
    isLoginLayout: Boolean,
    password: String,
    onValueChange: (String) -> Unit,
    forgotPassword: () -> Unit,
    modifier: Modifier = Modifier,
    isVisible: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    var passwordIsVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
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
                    text = PASSWORD_LABEL,
                    color = Color.White
                )
            },
            textStyle = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 16.sp,
            ),
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
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            },
            trailingIcon = {
                if (isLoginLayout) {
                    TextButton(onClick = { forgotPassword() }) {
                        Text(text = "Forgot password?",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            },
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
                focusedBorderColor =  Color.White.copy(alpha = 0.4f),
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
