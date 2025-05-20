package com.example.autoevent.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autoevent.event.EventViewModel
import com.example.autoevent.follow.FollowingViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FeedScreen(
    onCreateEvent: () -> Unit,
    onUserClick: (String) -> Unit = {},
    eventVM: EventViewModel      = viewModel(),
    followVM: FollowingViewModel = viewModel()
) {
    // Alle Events
    val allEvents by eventVM.events.collectAsState()
    // IDs der gefolgten Accounts
    val following by followVM.followingIds.collectAsState()
    val myUid    = FirebaseAuth.getInstance().currentUser?.uid

    // Zwei Listen: Alle (ohne eigene) und "Folge ich"
    val feedAll = remember(allEvents, myUid) {
        allEvents.filter { it.creatorId != myUid }
    }
    val feedFollowing = remember(allEvents, following) {
        allEvents.filter { it.creatorId in following }
    }

    // Tab-Auswahl
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Alle", "Folge ich")

    Scaffold(
        topBar = {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick  = { selectedTab = index },
                        text     = { Text(label) }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateEvent) {
                Icon(Icons.Default.Add, contentDescription = "Neues Event")
            }
        }
    ) { paddingValues ->
        val currentFeed = if (selectedTab == 0) feedAll else feedFollowing

        LazyColumn(
            contentPadding      = paddingValues,
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
                            .padding(top = 32.dp)
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (selectedTab == 0)
                                "Noch keine Posts"
                            else
                                "Noch keine Beiträge von Accounts, denen du folgst"
                        )
                    }
                }
            } else {
                items(currentFeed) { event ->
                    EventCard(
                        ev          = event,
                        onUserClick = onUserClick
                    )
                }
            }
        }
    }
}
