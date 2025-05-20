package com.example.autoevent.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

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
                onCreateEvent   = { nav.navigate("create") },
                externalPadding = pad,
                onUserClick     = { uid -> nav.navigate("profile/$uid") }   // Avatar-Tap
            )
        }

        /* ---------- Event erstellen ---------- */
        composable("create") {
            CreateEventScreen(onSaveDone = { nav.popBackStack() })
        }

        /* ---------- Eigenes Profil ---------- */
        composable("profile") {
            ProfileScreen(
                onEdit   = { nav.navigate("editProfile") },
                onLogout = onLogout
            )
        }

        /* ---------- Profil bearbeiten ---------- */
        composable("editProfile") {
            EditProfileScreen(onDone = { nav.popBackStack() })
        }

        /* ---------- Fremdes Profil ---------- */
        composable(
            route = "profile/{uid}",
            arguments = listOf(navArgument("uid") { defaultValue = "" })
        ) { back ->
            val uid = back.arguments?.getString("uid") ?: return@composable
            OtherProfileScreen(
                targetUid = uid,
                onBack    = { nav.popBackStack() }
            )
        }
    }
}
