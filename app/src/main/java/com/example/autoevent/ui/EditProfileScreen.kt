package com.example.autoevent.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import com.example.autoevent.profile.ProfileViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.background   


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onDone: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    val user by vm.user.collectAsState()
    if (user == null) return                       // noch nichts geladen

    /* -------- States -------- */
    var name   by remember { mutableStateOf(user!!.displayName) }
    var bio    by remember { mutableStateOf(user!!.bio) }
    var imgUri by remember { mutableStateOf<Uri?>(null) }        // ⚡ gewähltes Bild
    val scope  = rememberCoroutineScope()

    /* -------- Bild-Picker -------- */
    val pickImage = rememberLauncherForActivityResult(GetContent()) { uri ->
        imgUri = uri                               // ⚡ Preview aktualisieren
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil bearbeiten") },
                actions = {
                    TextButton(onClick = {
                        scope.launch {
                            vm.updateProfile(name.trim(), bio.trim(), imgUri)   // ⚡
                            onDone()
                        }
                    }) { Text("Speichern") }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            /* -------- Avatar mit Klick ---------- */
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = imgUri ?: user!!.photoUrl.ifBlank { null },
                    placeholder = painterResource(R.drawable.ic_avatar_placeholder),
                    error       = painterResource(R.drawable.ic_avatar_placeholder),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { pickImage.launch("image/*") }      // ⚡
                )
                /* Kleines Edit-Icon als Overlay */
                Icon(
                    Icons.Default.Edit, contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(4.dp)
                )
            }

            /* -------- Eingabefelder ---------- */
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { if (it.length <= 50) bio = it },      // ⚡ 50 Zeichen
                label = { Text("Bio (${bio.length}/50)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
