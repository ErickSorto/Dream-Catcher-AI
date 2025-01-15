package org.ballistic.dreamjournalai.shared.core.components

import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.BrighterWhite
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.RedOrange
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    buttonText: String,
    onClick: () -> Unit,
    onClickOutside: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        sheetState = sheetState,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        onDismissRequest = onClickOutside,
        content = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp, 0.dp, 16.dp)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = Typography().titleLarge,
                    color = BrighterWhite
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = message,
                    style = Typography().bodySmall,
                    color = BrighterWhite

                )
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedOrange
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = buttonText,
                        style = typography.headlineLarge,
                        color = White
                    )
                }
            }
        },
        containerColor = LightBlack

    )
}
