package com.example.autoevent.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autoevent.event.Event
import com.example.autoevent.event.EventViewModel
import androidx.compose.runtime.collectAsState


@Composable
fun FeedScreen(
    onCreateEvent: () -> Unit,
    externalPadding: PaddingValues = PaddingValues(),
    eventVM: EventViewModel = viewModel()
) {
    val events by eventVM.events.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateEvent) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { inner ->                                // Scaffold-eigenes Padding
        LazyColumn(
            contentPadding = inner,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)                 // Rahmen um die Liste
        ) {
            items(events) { ev -> EventCard(ev) }
        }
    }
}


