package com.example.autoevent.ui

import com.example.autoevent.util.rememberPlacesLauncher
import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autoevent.event.EventViewModel
import com.google.android.gms.maps.model.LatLng
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
    /* ---------- UI-State ---------- */
    var title       by remember { mutableStateOf("") }
    var address     by remember { mutableStateOf("") }
    var latLng      by remember { mutableStateOf<LatLng?>(null) }
    var description by remember { mutableStateOf("") }
    var dateMillis  by remember { mutableStateOf<Long?>(null) }

    val ctx     = LocalContext.current
    val scope   = rememberCoroutineScope()
    val dateFmt = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    /* ---------- Places-Launcher aus Helper ---------- */
    val launchPlaces = rememberPlacesLauncher { place ->
        address = place.address ?: place.name.orEmpty()
        latLng  = place.latLng
    }

    /* ---------- DatePicker ---------- */
    var showPicker by remember { mutableStateOf(false) }
    val dateState  = rememberDatePickerState()

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateMillis = dateState.selectedDateMillis
                        showPicker = false
                    },
                    enabled = dateState.selectedDateMillis != null
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Abbrechen") } }
        ) { DatePicker(state = dateState) }
    }

    /* ---------- Ableitungen & Validierung ---------- */
    val dateText = dateMillis?.let { dateFmt.format(Date(it)) } ?: ""
    val allValid = title.isNotBlank() && address.isNotBlank() && dateMillis != null

    /* ---------- UI ---------- */
    Scaffold { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /* ------ Titel ------ */
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titel*") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            /* ------ Adresse (Places) ------ */
            OutlinedTextField(
                value = address,
                onValueChange = {},      // read-only
                label = { Text("Adresse*") },
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = launchPlaces) {
                        Icon(Icons.Default.Place, contentDescription = "Adresse wählen")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { launchPlaces() }
            )

            /* ------ Datum ------ */
            OutlinedTextField(
                value = dateText,
                onValueChange = {},
                label = { Text("Datum*") },
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showPicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Datum wählen")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPicker = true }
            )

            /* ------ Beschreibung (optional) ------ */
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Beschreibung") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            /* ------ Speichern ------ */
            Button(
                enabled = allValid,
                onClick = {
                    scope.launch {
                        eventVM.addEvent(
                            title.trim(),
                            address.trim(),               // fürs MVP nur die Adresse
                            Timestamp(Date(dateMillis!!)),
                            description.trim()
                        )
                        onSaveDone()
                    }
                }
            ) { Text("Speichern") }

            Text(
                "* Pflichtfeld – Button wird erst aktiv, wenn alle Pflichtfelder ausgefüllt sind",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
