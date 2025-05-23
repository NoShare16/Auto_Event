package com.example.autoevent.util

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.Locale

private fun initPlacesOnce(ctx: android.content.Context) {
    if (!Places.isInitialized()) {
        val resId = ctx.resources.getIdentifier("google_maps_key", "string", ctx.packageName)
        Places.initialize(ctx, ctx.getString(resId), Locale.getDefault())
    }
}

@Composable
fun rememberPlacesLauncher(
    onPlaceSelected: (Place) -> Unit
): () -> Unit {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) { initPlacesOnce(ctx) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res: ActivityResult ->
        when (res.resultCode) {
            Activity.RESULT_OK -> {
                val place = Autocomplete.getPlaceFromIntent(res.data!!)
                onPlaceSelected(place)
            }
            AutocompleteActivity.RESULT_ERROR -> {
                val status = Autocomplete.getStatusFromIntent(res.data!!)
                Log.e("Places", "Status: ${status.statusCode} • ${status.statusMessage}")
                Toast.makeText(
                    ctx,
                    "Places-Fehler: ${status.statusMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> Unit   // User canceled
        }
    }

    return remember(launcher) {
        {
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG)
            ).build(ctx)
            launcher.launch(intent)
        }
    }
}
