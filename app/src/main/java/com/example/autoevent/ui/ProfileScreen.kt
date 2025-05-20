package com.example.autoevent.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autoevent.profile.ProfileViewModel
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEdit: () -> Unit,          // ← NEU: Bearbeiten-Callback
    onLogout: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    /* Live-Daten */
    val user      by vm.user.collectAsState()
    val myEvents  by vm.myEvents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mein Profil") },
                actions = {
                    TextButton(
                        onClick = {
                            vm.logout()
                            onLogout()
                        }
                    ) { Text("Logout") }
                }
            )
        }
    ) { pad ->

        LazyColumn(
            contentPadding      = pad,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier            = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            /* ---------- Header ---------- */
            user?.let {
                item {
                    ProfileHeader(
                        user      = it,
                        postCount = myEvents.size,
                        onEdit    = onEdit
                    )
                }
            }

            /* ---------- Liste der eigenen Events ---------- */
            if (myEvents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("Noch keine eigenen Events") }
                }
            } else {
                items(myEvents) { ev ->
                    EventCard(ev)
                }
            }
        }
    }
}
