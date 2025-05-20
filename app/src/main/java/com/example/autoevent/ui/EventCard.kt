package com.example.autoevent.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.autoevent.event.Event

@Composable
fun EventCard(
    ev: Event,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(ev.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(ev.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                ev.createdAt.toDate().toString(),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
