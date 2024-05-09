package org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

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
            Text(text = "Attention!")
        },
        text = {
            Text(text = "Do you want to save this dream?")
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
                Text("Leave Dream")
            }
        },
    )
}