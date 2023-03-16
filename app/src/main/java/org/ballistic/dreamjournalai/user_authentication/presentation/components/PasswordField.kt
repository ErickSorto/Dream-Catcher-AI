package org.ballistic.dreamjournalai.user_authentication.presentation.components

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.core.Constants.PASSWORD_LABEL
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.viewmodel.AuthViewModel

@Composable
fun PasswordField(
    viewModel: AuthViewModel,
    password: MutableState<String>,
    onPasswordValueChange: (newValue: TextFieldValue) -> Unit,
    forgotPassword: () -> Unit
) {

    var passwordIsVisible by remember { mutableStateOf(false) }


        OutlinedTextField(
            value = password.value,
            onValueChange = {
                password.value = it

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
                if (viewModel.isLoginLayout.value) {
                    TextButton(onClick = { forgotPassword() }) {
                        Text(text = "Forgot password?", color = Color.Black.copy(alpha = 0.5f))
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
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