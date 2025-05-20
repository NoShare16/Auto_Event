package com.example.autoevent.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun FeedNavHost(
    nav: NavHostController,
    pad: PaddingValues,
    onLogout: () -> Unit
) {
    NavHost(
        navController = nav,
        startDestination = "feed"
    ) {
        /* ---------- Feed ---------- */
        composable("feed") {
            FeedScreen(
                onCreateEvent  = { nav.navigate("create") },
                externalPadding = pad
            )
        }

        /* ---------- Event erstellen ---------- */
        composable("create") {
            CreateEventScreen(onSaveDone = { nav.popBackStack() })
        }

        /* ---------- Profil ---------- */
        composable("profile") {
            ProfileScreen(
                onEdit   = { nav.navigate("editProfile") },   // ← NEU
                onLogout = onLogout
            )
        }

        /* ---------- Profil bearbeiten ---------- */
        composable("editProfile") {
            EditProfileScreen(onDone = { nav.popBackStack() })
        }
    }
}
