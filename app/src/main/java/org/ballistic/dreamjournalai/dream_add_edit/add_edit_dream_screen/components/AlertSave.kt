package org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
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
            Text(text = "Save Changes?", color = colorResource(id = R.color.brighter_white))
        },
        text = {
            Text(text = "Do you want to save this dream?", color = colorResource(id = R.color.brighter_white))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Save Dream")
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