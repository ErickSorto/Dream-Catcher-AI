package org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.do_you_want_to_save_this_dream
import dreamjournalai.composeapp.shared.generated.resources.save_dream
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.BrighterWhite
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.RedOrange
import org.jetbrains.compose.resources.stringResource


@Composable
fun AlertSave(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onClickOutside : () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onClickOutside()
        },
        title = {
            Text(text = stringResource(Res.string.save_dream), color = BrighterWhite)
        },
        text = {
            Text(text = stringResource(Res.string.do_you_want_to_save_this_dream), color = BrighterWhite)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                //light white cloud blue
                Text(stringResource(Res.string.save_dream), color = Color(0xFFE1F5FE))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Discard Changes", color = RedOrange)
            }
        },
        shape = MaterialTheme.shapes.medium
    )
}