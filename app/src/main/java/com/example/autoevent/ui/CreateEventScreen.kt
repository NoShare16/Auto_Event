package com.example.autoevent.ui

/*  ────────────────────────────────────────────────────────────────
    CreateEventScreen – neues Event anlegen
       • Titel (Pflicht)
       • Adresse via Google-Places (Pflicht)
       • Datum (Pflicht)
       • Beschreibung (optional)
       • Bild (optional, Galerie)
   ──────────────────────────────────────────────────────────────── */

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image as ImageIcon
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.autoevent.event.EventViewModel
import com.example.autoevent.util.rememberPlacesLauncher
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
    /* ---------- Formular-State ---------- */
    var title       by remember { mutableStateOf("") }
    var address     by remember { mutableStateOf("") }
    var latLng      by remember { mutableStateOf<LatLng?>(null) }
    var description by remember { mutableStateOf("") }
    var dateMillis  by remember { mutableStateOf<Long?>(null) }
    var imageUri    by remember { mutableStateOf<Uri?>(null) }   // ← NEU

    val ctx     = LocalContext.current
    val scope   = rememberCoroutineScope()
    val dateFmt = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    /* ---------- Google-Places Autocomplete ---------- */
    val launchPlaces = rememberPlacesLauncher { place ->
        address = place.address ?: place.name.orEmpty()
        latLng  = place.latLng
    }

    /* ---------- Image-Picker (Galerie) ---------- */
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    /* ---------- DatePicker ---------- */
    var showPicker by remember { mutableStateOf(false) }
    val dateState  = rememberDatePickerState()

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton    = {
                TextButton(
                    onClick = {
                        dateMillis = dateState.selectedDateMillis
                        showPicker = false
                    },
                    enabled = dateState.selectedDateMillis != null
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Abbrechen") }
            }
        ) { DatePicker(state = dateState) }
    }

    /* ---------- Validierung ---------- */
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

            /* Titel */
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titel*") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            /* Adresse (Places) */
            OutlinedTextField(
                value = address,
                onValueChange = {},
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

            /* Datum */
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

            /* Bild (optional) */
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable { imagePicker.launch("image/*") }
            ) {
                if (imageUri == null) {
                    // Placeholder
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ImageIcon,
                            contentDescription = "Bild auswählen",
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Bild hinzufügen", style = MaterialTheme.typography.labelMedium)
                    }
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(ctx)
                            .data(imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            /* Beschreibung */
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Beschreibung") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            /* Speichern */
            Button(
                enabled = allValid,
                onClick = {
                    scope.launch {
                        eventVM.addEvent(
                            title.trim(),
                            address.trim(),
                            Timestamp(Date(dateMillis!!)),
                            description.trim(),
                            imageUri                       // ← NEU
                        )
                        onSaveDone()
                    }
                }
            ) { Text("Hochladen") }


        }
    }
}
