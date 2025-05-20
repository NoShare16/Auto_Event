package com.example.autoevent.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.autoevent.R
import com.example.autoevent.event.Event

@Composable
fun EventCard(
    modifier: Modifier = Modifier,
    ev: Event,
    onUserClick: (String) -> Unit = {},      //  ← default = leeres Lambda
)
 {
    Card(modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {

            /* ---------- Avatar + Autor ---------- */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUserClick(ev.creatorId) }
            ) {
                AsyncImage(
                    model = ev.authorPhotoUrl.ifBlank { null },
                    placeholder = painterResource(R.drawable.ic_avatar_placeholder),
                    error       = painterResource(R.drawable.ic_avatar_placeholder),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(ev.authorName, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(6.dp))

            /* ---------- Titel, Ort, Datum ---------- */
            Text(ev.title, style = MaterialTheme.typography.titleMedium)

            Text(
                text = "${ev.location} • " +
                        java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
                            .format(ev.eventDate.toDate()),
                style = MaterialTheme.typography.labelMedium
            )

            /* ---------- Beschreibung (optional) ---------- */
            ev.description.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
