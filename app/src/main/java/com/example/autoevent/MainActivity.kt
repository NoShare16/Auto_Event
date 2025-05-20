package com.example.autoevent          // ggf. an dein applicationId anpassen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.autoevent.auth.AuthFlow
import com.example.autoevent.ui.HomeScaffold

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AutoEventApp() }
    }
}

/* ---------- App-Theme + Root-Navigation ---------- */

@Composable
private fun AutoEventApp() {
    MaterialTheme {
        val rootNav = rememberNavController()
        RootNavHost(rootNav)
    }
}

/* ---------- Graph: auth  ↔  home ---------- */

@Composable
private fun RootNavHost(rootNav: NavHostController) {
    NavHost(rootNav, startDestination = "auth") {

        /* -------- Auth -------- */
        composable("auth") {
            AuthFlow(
                onAuthSuccess = {
                    rootNav.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        /* -------- Home (Feed | Profile) -------- */
        composable("home") {
            val homeNav = rememberNavController()         // eigener Controller für Tabs

            HomeScaffold(
                nav       = homeNav,                      //  ← jetzt übergeben
                onLogout  = {
                    rootNav.navigate("auth") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
