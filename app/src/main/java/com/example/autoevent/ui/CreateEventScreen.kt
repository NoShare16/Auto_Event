package com.example.autoevent.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autoevent.event.EventViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    onSaveDone: () -> Unit,
    eventVM: EventViewModel = viewModel()
) {
    /* ---------- Formular-States ---------- */
    var title       by remember { mutableStateOf("") }
    var location    by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var millis      by remember { mutableStateOf<Long?>(null) }

    val scope = rememberCoroutineScope()
    val ctx   = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    /* ---------- DatePickerDialog (Compose) ---------- */
    var showPicker by remember { mutableStateOf(false) }
    val dateState  = rememberDatePickerState()

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        millis = dateState.selectedDateMillis
                        showPicker = false
                    },
                    enabled = dateState.selectedDateMillis != null
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Abbrechen") }
            }
        ) {
            DatePicker(state = dateState)
        }
    }

    val dateText = millis?.let { dateFormatter.format(Date(it)) } ?: ""
    val allValid = title.isNotBlank() && location.isNotBlank() && millis != null

    Scaffold { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /* ----------- Pflichtfelder ----------- */
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titel*") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Ort*") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dateText,
                onValueChange = {},
                label = { Text("Datum*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPicker = true },
                readOnly = true,
                singleLine = true
            )

            /* ----------- Optional ----------- */
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Beschreibung") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        eventVM.addEvent(
                            title.trim(),
                            location.trim(),
                            Timestamp(Date(millis!!)),
                            description.trim()
                        )
                        onSaveDone()
                    }
                },
                enabled = allValid
            ) { Text("Speichern") }

            Text(
                "* Pflichtfeld – Button aktiviert sich erst, wenn alle Pflichtfelder ausgefüllt sind",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
