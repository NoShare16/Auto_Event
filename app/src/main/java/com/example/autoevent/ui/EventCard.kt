package com.example.autoevent.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.autoevent.R
import com.example.autoevent.event.Event
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EventCard(
    ev: Event,
    onUserClick: (String) -> Unit = {},      // klick auf Avatar/Name
    modifier: Modifier = Modifier             // optionales Styling
) {
    // Formatter für Datum “TT.MM.JJJJ”
    val dateFmt = remember {
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    }

    Card(
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {

            /* ---------- Avatar + Autor (klickbar) ---------- */
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
                    contentDescription = "Profilbild",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(ev.authorName, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(8.dp))

            /* ---------- Titel ---------- */
            Text(ev.title, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(4.dp))

            /* ---------- Ort • Datum ---------- */
            Text(
                text = "${ev.location}  •  ${dateFmt.format(ev.eventDate.toDate())}",
                style = androidx.compose.material3.MaterialTheme.typography.labelMedium
            )

            /* ---------- Beschreibung (optional) ---------- */
            ev.description.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
            }
        }
    }
}
