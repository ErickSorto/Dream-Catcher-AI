package org.ballistic.dreamjournalai.store_billing.presentation.anonymous_store_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.TypewriterText

@Composable
fun AnonymousStoreScreen(
    paddingValues: PaddingValues,
    navigateToAccountScreen: () -> Unit
) {
    val animationDuration = 5000
    val showButton = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .background(
                    color = colorResource(id = R.color.dark_blue).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                ),
        ) {
            TypewriterText(
                text = "In order to access store please log in! Anonymous users do not have access to the store!",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                animationDuration = animationDuration,
                onAnimationComplete = { showButton.value = true }
            )
        }
        AnimatedVisibility(
            visible = showButton.value,
            modifier = Modifier
                .fillMaxWidth(.8f)
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Button(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.RedOrange) // Replace with your desired color
                ),
                onClick = { navigateToAccountScreen() }
            ) {
                Text(
                    text = "Login",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}