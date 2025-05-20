package com.example.autoevent.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autoevent.event.EventViewModel
import kotlinx.coroutines.launch

@Composable
fun CreateEventScreen(
    onSaveDone: () -> Unit,
    eventVM: EventViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Titel") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = desc, onValueChange = { desc = it },
                label = { Text("Beschreibung") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        eventVM.addEvent(title, desc)
                        onSaveDone()
                    }
                },
                enabled = title.isNotBlank()
            ) { Text("Speichern") }
        }
    }
}
