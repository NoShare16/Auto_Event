package com.example.autoevent.ui

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
import com.example.autoevent.R               // ← für R.drawable.*
import com.example.autoevent.profile.User

@Composable
fun ProfileHeader(
    user: User,
    postCount: Int,
    onEdit: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        /* ---------- Avatar ---------- */
        AsyncImage(
            model = user.photoUrl.ifBlank { null },       // leere URL ⇒ null
            placeholder = painterResource(R.drawable.ic_avatar_placeholder),
            error       = painterResource(R.drawable.ic_avatar_placeholder),
            contentDescription = null,
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.height(8.dp))

        /* ---------- Name + Bio ---------- */
        Text(user.displayName, style = MaterialTheme.typography.titleMedium)

        user.bio.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(4.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(8.dp))

        /* ---------- Statistiken ---------- */
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Stat("Beiträge", postCount)
            Stat("Follower", user.followers)
            Stat("Folgt",    user.following)
        }

        Spacer(Modifier.height(8.dp))

        /* ---------- Bearbeiten-Button ---------- */
        OutlinedButton(onClick = onEdit) { Text("Bearbeiten") }
    }
}

@Composable
private fun Stat(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value.toString(), style = MaterialTheme.typography.bodyLarge)
        Text(label,           style = MaterialTheme.typography.labelSmall)
    }
}
