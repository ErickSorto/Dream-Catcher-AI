package org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.coil3.CoilImage
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.ai_tools_selection
import dreamjournalai.composeapp.shared.generated.resources.baseline_smart_display_24
import dreamjournalai.composeapp.shared.generated.resources.tap
import dreamjournalai.composeapp.shared.generated.resources.watch_ad
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.dream_add_edit.domain.AddEditDreamEvent
import org.ballistic.dreamjournalai.shared.dream_add_edit.domain.ButtonType
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.RedOrange
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.SkyBlue
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import dreamjournalai.composeapp.shared.generated.resources.dream_token
import org.ballistic.dreamjournalai.shared.dream_store.presentation.store_screen.components.singleClick

@Composable
fun GenerateButtonsLayout(
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    animateToPage: (Int) -> Unit,
    snackBarState: () -> Unit,
    textFieldState: TextFieldState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.ai_tools_selection),
            style = MaterialTheme.typography.labelMedium,
            color = White,
            modifier = Modifier.padding(4.dp, 4.dp, 4.dp, 4.dp)
        )
        Box(
            modifier = Modifier
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp, top = 0.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color = White.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ButtonType.entries.forEach { item ->
                    UniversalButton(
                        buttonType = item,
                        textFieldState = textFieldState,
                        animateToPage = { index ->
                            animateToPage(index)
                        },
                        onAddEditEvent = onAddEditEvent,
                        snackBarState = {
                            snackBarState()
                        },
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .size(44.dp),
                        hasText = false
                    )
                }
            }
        }
    }
}


@Composable
fun UniversalButton(
    modifier: Modifier = Modifier,
    buttonType: ButtonType,
    textFieldState: TextFieldState,
    animateToPage: (Int) -> Unit = {},
    onAddEditEvent: (AddEditDreamEvent) -> Unit,
    snackBarState: () -> Unit = {},
    size: Dp = 32.dp,
    fontSize: TextUnit = 14.sp,
    hasText: Boolean = true
) {
    val keyBoardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val textColor = if (textFieldState.text.length >= 20) {
        buttonType.longTextColorId
    } else {
        buttonType.baseColorId
    }

    val alpha = if (textFieldState.text.length >= 20) {
        1f
    } else {
        0.7f
    }

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false),
            ) {
                onAddEditEvent(AddEditDreamEvent.TriggerVibration)
                if (textFieldState.text.isNotBlank() && textFieldState.text.length >= 20) {
                    keyBoardController?.hide()
                    focusManager.clearFocus()
                    scope.launch {
                        animateToPage(buttonType.pageIndex)
                    }
                    onAddEditEvent(buttonType.eventCreator(true))
                } else {
                    snackBarState()
                }

            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (hasText) {
            Text(
                text = stringResource(Res.string.tap),
                fontSize = fontSize,
                color = Color.Transparent
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Icon(
            painter = painterResource(buttonType.drawableId),
            contentDescription = stringResource(buttonType.title),
            modifier = Modifier
                .padding(8.dp)
                .size(size),
            tint = textColor.copy(alpha = alpha)
        )
        if (hasText) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tap to ${stringResource(buttonType.title)}",
                fontSize = fontSize,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}


@Composable
fun AdTokenLayout(
    onAdClick: (amount: Int) -> Unit = {},
    onDreamTokenClick: (amount: Int) -> Unit = {},
    isAdButtonVisible: Boolean = true,
    amount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        DreamTokenGenerateButton(onClick = { onDreamTokenClick(amount) }, amount = amount)
        if (isAdButtonVisible) {
            Spacer(modifier = Modifier.height(16.dp))
            WatchAdButton(onClick = { onAdClick(amount) })
        }
    }
}

@Composable
fun WatchAdButton(
    onClick: () -> Unit = {}
) {
    val lastClickTime = remember { mutableLongStateOf(0L) }
    Button(
        onClick = singleClick(lastClickTime) {onClick()},
        modifier = Modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = RedOrange),
    ) {

        Icon(
            painter = painterResource(Res.drawable.baseline_smart_display_24),
            contentDescription = stringResource(Res.string.watch_ad),
            modifier = Modifier
                .size(36.dp),
            tint = White,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(Res.string.watch_ad),
            modifier = Modifier
                .padding(4.dp),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.size(36.dp))
    }
}

@Composable
fun DreamTokenGenerateButton(
    onClick: () -> Unit,
    amount: Int
) {
    val amountText = if (amount == 0) "" else amount.toString()
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
    ) {
        Image(
            painter = painterResource(Res.drawable.dream_token),
            contentDescription = "DreamToken",
            modifier = Modifier
                .size(40.dp)
        )

        Text(
            text = amountText,
            modifier = Modifier
                .padding(4.dp),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = if (amount == 0) "Free" else "DreamToken",
            modifier = Modifier
                .padding(4.dp),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(Res.drawable.dream_token),
            contentDescription = "DreamToken",
            modifier = Modifier
                .size(40.dp),
            tint = Color.Transparent
        )
        Text(
            text = amountText,
            modifier = Modifier
                .padding(4.dp),
            color = Color.Transparent,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
