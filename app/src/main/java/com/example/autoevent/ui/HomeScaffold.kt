package com.example.autoevent.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun HomeScaffold(nav: NavHostController, onLogout: () -> Unit ) {
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route ?: "feed"

    Scaffold(
        floatingActionButton = {
            if (current == "feed") {
                FloatingActionButton(onClick = { nav.navigate("create") }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = current == "feed",
                    onClick = { nav.navigate("feed") { popUpTo("feed") { inclusive = true } } },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Feed") }
                )
                NavigationBarItem(
                    selected = current == "profile",
                    onClick = { nav.navigate("profile") { popUpTo("profile") { inclusive = true } } },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profil") }
                )
            }
        }
    ) { inner ->                          // Padding an Screens weiterreichen
        FeedNavHost(nav = nav, pad = inner, onLogout  = onLogout)
    }
}
