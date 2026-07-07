package com.example.expensetracker.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.expensetracker.ui.theme.DangerText
import com.example.expensetracker.ui.theme.Ink
import com.example.expensetracker.ui.theme.InkMuted
import com.example.expensetracker.ui.theme.Surface as SurfaceColor

/** Confirmation shown before deleting a record; only the Delete action removes it. */
@Composable
fun ConfirmDeleteDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceColor,
        title = { Text(title, color = Ink) },
        text = { Text(text, color = InkMuted) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = DangerText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = InkMuted)
            }
        }
    )
}
