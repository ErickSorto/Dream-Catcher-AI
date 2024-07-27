package org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import org.ballistic.dreamjournalai.R


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
            Text(text = stringResource(R.string.save_changes), color = colorResource(id = R.color.brighter_white))
        },
        text = {
            Text(text = stringResource(R.string.do_you_want_to_save_this_dream), color = colorResource(id = R.color.brighter_white))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text(stringResource(R.string.save_dream))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Discard Changes", color = colorResource(id = R.color.RedOrange))
            }
        },
        shape = MaterialTheme.shapes.medium
    )
}