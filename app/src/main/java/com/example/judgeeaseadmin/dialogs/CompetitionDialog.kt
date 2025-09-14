package com.example.judgeeaseadmin.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun CompetitionDialog(
    onDismiss: () -> Unit,
    onCreate: ( String,String,String,String,Long,Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var organizer by remember { mutableStateOf("") }
    var startDateTime by remember { mutableLongStateOf(0L) }
    var endDateTime by remember { mutableLongStateOf(0L) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Create Competition") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") }
                )
                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue") }
                )
                OutlinedTextField(
                    value = organizer,
                    onValueChange = { organizer = it },
                    label = { Text("Organizer") }
                )
                OutlinedTextField(
                    value = startDateTime.toString(),
                    onValueChange = { startDateTime = it.toLong() },
                    label = { Text("Start Date") }
                )
                OutlinedTextField(
                    value = endDateTime.toString(),
                    onValueChange = { endDateTime = it.toLong() },
                    label = { Text("End Date") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onCreate(title, desc, venue, organizer, startDateTime, endDateTime) }) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
