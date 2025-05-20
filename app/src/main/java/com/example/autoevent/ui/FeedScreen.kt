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
import com.example.autoevent.follow.FollowingViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.collectAsState

/* ------------------------------------------------------------------ */
/* ---------------------------- FEED TABS --------------------------- */
/* ------------------------------------------------------------------ */

@Composable
fun FeedScreen(
    onCreateEvent: () -> Unit,
    externalPadding: PaddingValues = PaddingValues(),
    onUserClick: (String) -> Unit = {},
    eventVM: EventViewModel = viewModel(),
    followVM: FollowingViewModel = viewModel()          //  ←  NEU
) {
    val allEvents   by eventVM.events.collectAsState()
    val following   by followVM.followingIds.collectAsState()

    val myUid = FirebaseAuth.getInstance().currentUser?.uid

    /* ---------- Zwei Feed-Varianten ---------- */
    val feedAll = remember(allEvents, myUid) {
        allEvents.filter { it.creatorId != myUid }          // eigene raus
    }
    val feedFollowing = remember(allEvents, following) {
        allEvents.filter { it.creatorId in following }
    }

    /* ---------- Tab-State ---------- */
    var selTab by remember { mutableIntStateOf(0) }
    val tabLabels = listOf("Alle", "Folge ich")

    Scaffold(
        topBar = {
            TabRow(selectedTabIndex = selTab) {
                tabLabels.forEachIndexed { idx, txt ->
                    Tab(
                        selected  = selTab == idx,
                        onClick   = { selTab = idx },
                        text      = { Text(txt) }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateEvent) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { inner ->

        val currentFeed = if (selTab == 0) feedAll else feedFollowing

        LazyColumn(
            contentPadding      = inner,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier            = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (currentFeed.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (selTab == 0) "Noch keine Posts"
                            else "Noch keine Posts von Accounts, denen du folgst"
                        )
                    }
                }
            } else {
                items(currentFeed) { ev -> EventCard(ev, onUserClick) }
            }
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

            /* Avatar + Autor */
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

            /* Inhalt */
            Text(event.title, style = MaterialTheme.typography.titleMedium)
            event.description.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
