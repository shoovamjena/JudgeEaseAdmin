package com.example.judgeeaseadmin.screens

// The reusable date-time picker from the previous example
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.judgeeaseadmin.viewmodel.AppViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    label: String,
    selectedDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val haptic = LocalView.current

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    val timePickerState = rememberTimePickerState(
        initialHour = selectedDateTime.hour,
        initialMinute = selectedDateTime.minute,
        is24Hour = false // Set to true for 24-hour format
    )

    // This is the clickable row in your main UI
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = selectedDateTime.format(DateTimeFormatter.ofPattern("MMM dd, hh:mm a")),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    // Date Picker Dialog (remains the same)
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    if (datePickerState.selectedDateMillis != null) {
                        showTimePicker = true
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // MODIFIED: Time Picker now uses your custom Dialog style
    if (showTimePicker) {
        Dialog(onDismissRequest = {
            showTimePicker = false
            haptic.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        }) {
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // TimePicker UI
                    TimePicker(state = timePickerState)
                    Spacer(Modifier.height(16.dp))

                    // Custom Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(onClick = {
                            showTimePicker = false
                            haptic.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        }) {
                            Text("CANCEL")
                        }
                        Button(onClick = {
                            haptic.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                val newDateTime = LocalDateTime.of(
                                    selectedDate,
                                    java.time.LocalTime.of(timePickerState.hour, timePickerState.minute)
                                )
                                onDateTimeSelected(newDateTime)
                                showTimePicker = false
                            }
                        }) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}

// The modified CompetitionDialog with TextFields and DateTimePickers
@Composable
fun CompetitionDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String, LocalDateTime, LocalDateTime) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var organizer by remember { mutableStateOf("") }
    var startDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    var endDateTime by remember { mutableStateOf(LocalDateTime.now().plusHours(2)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Competition") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = venue, onValueChange = { venue = it }, label = { Text("Venue") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = organizer, onValueChange = { organizer = it }, label = { Text("Organizer") })
                Spacer(Modifier.height(16.dp))

                // Use the DateTimePicker for start and end times
                DateTimePicker(label = "Start Time", selectedDateTime = startDateTime, onDateTimeSelected = { startDateTime = it })
                DateTimePicker(label = "End Time", selectedDateTime = endDateTime, onDateTimeSelected = { endDateTime = it })
            }
        },
        confirmButton = {
            Button(onClick = {
                onCreate(title, desc, venue, organizer, startDateTime, endDateTime)
            }) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// Your modified HomeScreen
@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavController,
    authViewModel: AppViewModel
) {
    val competitions by authViewModel.competitions.collectAsState()
    val showDialog = remember { mutableStateOf(false) }

    // This is the formatter for displaying dates in your list
    remember { DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a") }

    if (showDialog.value) {
        CompetitionDialog(
            onDismiss = { showDialog.value = false },
            onCreate = { title, desc, venue, organizer, startDateTime, endDateTime ->
                authViewModel.createCompetition(title, desc, venue, organizer, startDateTime, endDateTime)
                showDialog.value = false
            }
        )
    }

    Column(
        modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome Admin  ${authViewModel.getAdminName()}!", fontSize = 28.sp)
        Spacer(Modifier.height(12.dp))
        Button(onClick = { showDialog.value = true }) {
            Text("Create Competition")
        }
        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(competitions) { comp ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { navController.navigate("competition/${comp.id}") }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(comp.name, fontSize = 20.sp)
                        Text(comp.description, fontSize = 14.sp)
                        Text("Venue: ${comp.venue}", fontSize = 14.sp)
                        Text("By: ${comp.organizer}", fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        // MODIFIED: Use the formatter for a clean date display
                        Text("Starts: ${formatMillisToDateTime(comp.startDateTime)}", fontSize = 14.sp)
                        Text("Ends: ${formatMillisToDateTime(comp.endDateTime)}", fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = { authViewModel.signOut() }) {
            Text("SIGN OUT")
        }
    }
}

fun formatMillisToDateTime(millis: Long?): String {
    // Return a default value if the millis are null
    if (millis == null) return "N/A"

    // The formatter that defines the final output style
    // For best performance, create this formatter only once and reuse it
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm a", Locale.ENGLISH)

    // Convert the Long to a LocalDateTime object
    val localDateTime = Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    // Apply the formatter to the date-time object
    return localDateTime.format(formatter)
}