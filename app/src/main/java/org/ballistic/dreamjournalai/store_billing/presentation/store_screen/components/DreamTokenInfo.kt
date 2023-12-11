package org.ballistic.dreamjournalai.store_billing.presentation.store_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.DreamTokenLayout
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState


@Composable
fun DreamTokenInfo(modifier: Modifier, mainScreenViewModelState: MainScreenViewModelState) {

    Column(
        modifier = modifier
            .padding(top = 32.dp)
            .background(
                colorResource(id = R.color.sky_blue).copy(alpha = 0.8f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        SubInfoMonthlyYearlyPrice(mainScreenViewModelState = mainScreenViewModelState)
        SubInfoFeatures()
    }
}

@Composable
fun SubInfoMonthlyYearlyPrice(
    mainScreenViewModelState: MainScreenViewModelState
) {
    Column(
        modifier = Modifier
            .padding(paddingValues = PaddingValues(8.dp, 16.dp, 8.dp, 8.dp))
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Dream Token Benefits", fontSize = 20.sp, fontWeight = Bold, color = Color.White)
            DreamTokenLayout(mainScreenViewModelState = mainScreenViewModelState)
        }
    }
}

@Composable
fun SubInfoFeatures() {
    Column(
        modifier = Modifier
            .padding(paddingValues = PaddingValues(0.dp, 8.dp, 0.dp, 0.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorResource(id = R.color.Yellow).copy(alpha = 0.8f),
                        colorResource(id = R.color.Yellow).copy(alpha = 1f)
                    )
                ),
                shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
            )
            .fillMaxWidth(),
    ) {
        CheckmarkAndText(
            text = "Paint Your Dreams",
            subText = "Unlock the power to visualize your dreams in full color with 100 monthly tokens.",
            modifier = Modifier
        )
        CheckmarkAndText(
            text = "Interpret Your Dreams",
            subText = "Unlock exclusive access to our dream interpretation feature when it's available.",
            modifier = Modifier
        )
        CheckmarkAndText(
            text = "Discover New Words",
            subText = "Expand your dream vocabulary with our upcoming dream dictionary feature.",
            modifier = Modifier
        )
        CheckmarkAndText(
            text = "Enjoy an Ad-Free Experience",
            subText = "Say goodbye to interruptions and enhance your dream journaling experience.",
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
fun CheckmarkAndText(
    text: String,
    subText: String,
    modifier: Modifier = Modifier
) {
    val showDialog = remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .padding(8.dp, 8.dp, 8.dp, 0.dp)
            .fillMaxWidth()
            .clickable { showDialog.value = true },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.drawable.baseline_check_24),
            contentDescription = "checkmark",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(48.dp)
                .padding(0.dp, 0.dp, 8.dp, 0.dp)
        )
        Column {
            Text(
                text = text,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = Bold,
            )
            if (showDialog.value) {
                SubTextDialog(subText = subText, onDismiss = { showDialog.value = false })
            }
        }
    }
}

@Composable
fun SubTextDialog(subText: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Details") },
        text = { Text(text = subText, textAlign = TextAlign.Center, color = Color.White) },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() },
                content = { Text(text = "OK") }
            )
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = colorResource(id = R.color.Yellow),
    )
}

@Composable
fun CustomButtonLayout(
    buy500IsClicked: () -> Unit,
    buy100IsClicked: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 8.dp, 8.dp, 8.dp),
    ) {
        val (monthly, annual, tag) = createRefs()

        DreamToken500ButtonBuy(
            modifier = Modifier.constrainAs(annual) {
                bottom.linkTo(monthly.top)
            },
            buy500IsClicked = {
                buy500IsClicked()
            })

        DreamToken100ButtonBuy(
            modifier = Modifier.constrainAs(monthly) {
                bottom.linkTo(parent.bottom)
            },
            buy100IsClicked = {
                buy100IsClicked()
            }
        )
        MostPopularBanner(modifier = Modifier.constrainAs(tag) {

        })
    }
}

@Composable
fun DreamToken500ButtonBuy(
    modifier: Modifier,
    buy500IsClicked: () -> Unit = {}
) {
    Button(
        onClick = { buy500IsClicked() },
        modifier = modifier
            .padding(8.dp, 0.dp, 8.dp, 8.dp)
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.Yellow).copy(alpha = 0.8f),
            contentColor = Color.White
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "500 Dream Tokens",
                fontSize = 20.sp,
                fontWeight = Bold,
                maxLines = 1,
                modifier = Modifier
                    .padding(8.dp, 8.dp, 8.dp, 8.dp)
                    .align(Alignment.CenterStart)
            )

            Column(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\$4.99",
                    fontSize = 20.sp,
                    fontWeight = Bold,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(8.dp, 8.dp, 8.dp, 0.dp)
                )
                Text(
                    text = "Save 50%",
                    fontSize = 12.sp,
                    fontWeight = Bold,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(8.dp, 0.dp, 8.dp, 8.dp)
                )
            }
        }
    }
}

@Composable
fun DreamToken100ButtonBuy(
    modifier: Modifier,
    buy100IsClicked: () -> Unit
) {
    Button(
        onClick = { buy100IsClicked() },
        modifier = modifier
            .padding(8.dp, 8.dp, 8.dp, 8.dp)
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.sky_blue).copy(alpha = 0.8f),
            contentColor = Color.White
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "100 Dream Tokens",
                fontSize = 20.sp,
                fontWeight = Bold,
                maxLines = 1,
                modifier = Modifier
                    .padding(8.dp, 8.dp, 8.dp, 8.dp)
                    .align(Alignment.CenterStart)
            )

            Column(modifier = Modifier.align(Alignment.CenterEnd)) {
                Text(
                    text = "\$1.99",
                    fontSize = 20.sp,
                    fontWeight = Bold,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(8.dp, 8.dp, 8.dp, 8.dp)
                )
            }
        }
    }
}

@Composable
fun MostPopularBanner(modifier: Modifier) {
    //offset by item height/ 2
    Box(
        modifier = modifier
            .padding(8.dp, 8.dp, 8.dp, 0.dp)
            .offset(y = (-20).dp)
            .fillMaxWidth(.5f)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        colorResource(id = R.color.RedOrange).copy(alpha = 0.9f),
                        colorResource(id = R.color.RedOrange),
                    )
                ),
            )
    ) {
        Text(
            text = "Best Value (Save 50%)",
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.Center),
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = Bold,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}
