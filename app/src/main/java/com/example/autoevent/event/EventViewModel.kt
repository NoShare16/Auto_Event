package com.example.autoevent.event

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await           // ← Tasks → Coroutines

class EventViewModel(
    private val db: FirebaseFirestore    = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth       = FirebaseAuth.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : ViewModel() {

    /* ───────────── Live-Feed (Firestore → StateFlow) ───────────── */
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    init { subscribeToEvents() }

    private fun subscribeToEvents() {
        db.collection("events")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                if (snap != null) _events.value = snap.toObjects(Event::class.java)
            }
    }

    /* ───────────── Neues Event (mit optionalem Bild) ───────────── */
    /**
     *  Lädt zuerst – falls vorhanden – das Bild hoch
     *  und legt anschließend das Event-Dokument an.
     *  Wird **immer** im IO-Thread ausgeführt (viewModelScope.launch).
     */
    suspend fun addEvent(
        title: String,
        location: String,
        eventDate: Timestamp,
        description: String,
        imageUri: Uri? = null
    ) {
        val user = auth.currentUser ?: return   // sollte nicht null sein (Aufrufer = logged-in)

        viewModelScope.launch {
            /* ---------- 1) Bild hochladen (optional) ---------- */
            var imageUrl = ""
            if (imageUri != null) {
                // vorab eine neue Doc-ID erzeugen, damit Pfad eindeutig ist
                val docRef   = db.collection("events").document()
                val imgRef   = storage.reference.child("eventImages/${docRef.id}.jpg")

                // Upload & Download-URL
                imgRef.putFile(imageUri).await()
                imageUrl = imgRef.downloadUrl.await().toString()

                // ---------- 2) Event-Objekt speichern ----------
                val ev = Event(
                    id             = docRef.id,                // gleich mit ablegen
                    creatorId      = user.uid,
                    authorName     = user.displayName ?: user.email ?: "Unbekannt",
                    authorPhotoUrl = user.photoUrl?.toString() ?: "",

                    /* Pflichtfelder */
                    title       = title,
                    location    = location,
                    eventDate   = eventDate,

                    /* Optional */
                    description = description,
                    imageUrl    = imageUrl,                    // ← NEU

                    createdAt   = Timestamp.now()
                )
                docRef.set(ev).await()
            } else {
                /* ---------- ohne Bild ---------- */
                val ev = Event(
                    creatorId      = user.uid,
                    authorName     = user.displayName ?: user.email ?: "Unbekannt",
                    authorPhotoUrl = user.photoUrl?.toString() ?: "",

                    title       = title,
                    location    = location,
                    eventDate   = eventDate,

                    description = description,
                    imageUrl    = "",                          // leer

                    createdAt   = Timestamp.now()
                )
                db.collection("events").add(ev).await()
            }
        }
    }
}
