package com.example.autoevent.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autoevent.event.Event
import com.example.autoevent.profile.OtherProfileViewModel
import com.example.autoevent.profile.ProfileViewModelFactory      // Factory mit uid
import androidx.compose.runtime.collectAsState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherProfileScreen(
    targetUid: String,
    onBack: () -> Unit
) {
    val vm: OtherProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(targetUid)
    )

    val user       by vm.user.collectAsState()
    val posts      by vm.posts.collectAsState()
    val following  by vm.isFollowing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(user?.displayName ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            user?.let {
                item {
                    ProfileHeader(
                        user       = it,
                        postCount  = posts.size,

                    )
                    /* Follow/Unfollow-Button */
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Button(
                            onClick = { if (following) vm.unfollow() else vm.follow() }
                        ) { Text(if (following) "Entfolgen" else "Folgen") }
                    }
                }
            }

            items(posts) { ev ->
                EventCard(ev, onUserClick = { /* bereits im Profil → kein Navigieren */ })
            }

        }
    }
}
