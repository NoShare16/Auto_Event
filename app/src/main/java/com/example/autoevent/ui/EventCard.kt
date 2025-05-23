package com.example.autoevent.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.autoevent.R
import com.example.autoevent.event.Event
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventCard(
    ev: Event,
    modifier: Modifier = Modifier,
    onUserClick: (String) -> Unit = {}
) {
    val dateFmt = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
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
                Text(
                    ev.authorName.ifBlank { "Unbekannt" },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(8.dp))

            /* ---------- Titel ---------- */
            Text(ev.title, style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(4.dp))

            /* ---------- Ort & Datum ---------- */
            Text("Location: ${ev.location}", style = MaterialTheme.typography.labelMedium)
            Text("Date: ${dateFmt.format(ev.eventDate.toDate())}", style = MaterialTheme.typography.labelMedium)

            /* ---------- Beschreibung ---------- */
            ev.description.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }

            /* ---------- Optionales Bild ---------- */
            ev.imageUrl.takeIf { it.isNotBlank() }?.let { url ->
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = url,
                    contentDescription = "Event-Bild",
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_image_placeholder),
                    error       = painterResource(R.drawable.ic_image_placeholder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}
