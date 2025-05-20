package com.example.autoevent.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.autoevent.R
import com.example.autoevent.event.Event
import com.example.autoevent.event.EventViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.collectAsState

/* ------------------------------------------------------------------ */
/* ---------------------------- FEED -------------------------------- */
/* ------------------------------------------------------------------ */

@Composable
fun FeedScreen(
    onCreateEvent: () -> Unit,
    externalPadding: PaddingValues = PaddingValues(),
    onUserClick: (String) -> Unit = {},           // Avatar-/Name-Tap
    eventVM: EventViewModel = viewModel()
) {
    val events by eventVM.events.collectAsState()
    val myUid  = FirebaseAuth.getInstance().currentUser?.uid

    // Eigene Posts im Feed ausblenden
    val feed = remember(events, myUid) {
        events.filter { it.creatorId != myUid }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateEvent) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { inner ->
        LazyColumn(
            contentPadding      = inner,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier            = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(feed) { ev -> EventCard(ev, onUserClick) }
        }
    }
}

/* ------------------------------------------------------------------ */
/* -------------------------- EVENT CARD ---------------------------- */
/* ------------------------------------------------------------------ */

@Composable
fun EventCard(
    event: Event,
    onUserClick: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {

            /* ---------- Avatar + Autor ---------- */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUserClick(event.creatorId) }
            ) {
                AsyncImage(
                    model = event.authorPhotoUrl.ifBlank { null },
                    placeholder = painterResource(R.drawable.ic_avatar_placeholder),
                    error       = painterResource(R.drawable.ic_avatar_placeholder),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(event.authorName, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(6.dp))

            /* ---------- Titel + Beschreibung ---------- */
            Text(event.title, style = MaterialTheme.typography.titleMedium)
            event.description.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
