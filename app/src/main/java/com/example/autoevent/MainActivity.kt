package com.example.autoevent   // ggf. an dein applicationId anpassen

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
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* ────────────────────────────────────────────────
         *  Google Places SDK einmalig initialisieren
         *  (liest denselben API-Key wie Maps aus dem Manifest)
         * ──────────────────────────────────────────────── */
        if (!Places.isInitialized()) {
            // Wenn du das Secrets-Plugin verwendest, kannst du
            // auch BuildConfig.MAPS_API_KEY nehmen.
            Places.initialize(
                applicationContext,
                getString(R.string.google_maps_key)
            )
        }

        /* ─────────────────────────────
         *  Compose-UI starten
         * ───────────────────────────── */
        setContent { AutoEventRoot() }
    }
}

/* ---------- Theme-Wrapper + Root-Nav ---------- */

@Composable
private fun AutoEventRoot() {
    MaterialTheme {
        val rootNav = rememberNavController()
        RootNavGraph(rootNav)
    }
}

/* ---------- Graph:  auth  ↔  home ---------- */

@Composable
private fun RootNavGraph(rootNav: NavHostController) {

    NavHost(
        navController   = rootNav,
        startDestination = "auth"
    ) {
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

        /* -------- Home  (Feed | Profil) -------- */
        composable("home") {
            val homeNav = rememberNavController()   // eigener Controller für Tabs

            HomeScaffold(
                nav      = homeNav,
                onLogout = {
                    rootNav.navigate("auth") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
