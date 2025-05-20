package com.example.autoevent.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autoevent.event.Event
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/* ---------- Datenklasse für das Firestore-User-Dokument ---------- */
data class User(
    val displayName: String = "",
    val bio: String = "",
    val photoUrl: String = "",
    val followers: Int = 0,
    val following: Int = 0,
    val updatedAt: Timestamp? = null
)

/* ---------- ViewModel ---------- */
class ProfileViewModel(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : ViewModel() {

    /* -------- User-Flow -------- */
    private val uid get() = auth.currentUser!!.uid
    private val userDocRef get() = db.collection("users").document(uid)

    val user: StateFlow<User?> = userDocRef
        .snapshots()                                     // Flow über Firestore-Listener
        .map { snap -> snap.toObject(User::class.java) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /* -------- Eigene Events -------- */
    val myEvents: StateFlow<List<Event>> = db.collection("events")
        .whereEqualTo("creatorId", uid)
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .snapshots()
        .map { snap -> snap.toObjects(Event::class.java) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /* -------- Profil aktualisieren -------- */
    fun updateProfile(name: String, bio: String, newImageUri: Uri?) = viewModelScope.launch {
        val updates = mutableMapOf<String, Any>(
            "displayName" to name,
            "bio"         to bio,
            "updatedAt"   to Timestamp.now()
        )

        newImageUri?.let { uri ->
            val url = uploadAvatar(uri)          // Storage-Upload + URL-Abruf
            updates["photoUrl"] = url
        }

        withContext(Dispatchers.IO) {
            Tasks.await(userDocRef.update(updates))   // Firestore-Update, blockiert im IO-Thread
        }
    }

    /* -------- Helper: Bild hochladen und URL zurückgeben -------- */
    private suspend fun uploadAvatar(uri: Uri): String = withContext(Dispatchers.IO) {
        val ref = storage.reference.child("avatars/$uid.jpg")

        // 1) Bild hochladen
        Tasks.await(ref.putFile(uri))

        // 2) Download-URL holen
        val url = Tasks.await(ref.downloadUrl)
        url.toString()
    }

    /* -------- Logout -------- */
    fun logout() = auth.signOut()
}
