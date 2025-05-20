package com.example.autoevent.event

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EventViewModel(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth     = FirebaseAuth.getInstance()
) : ViewModel() {

    /* -------------------- Live-Feed -------------------- */
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

    /* -------------------- Neues Event -------------------- */
    fun addEvent(
        title: String,
        location: String,
        eventDate: Timestamp,
        description: String
    ) {
        val user = auth.currentUser ?: return         // sollte nicht null sein

        val ev = Event(
            creatorId      = user.uid,
            authorName     = user.displayName ?: user.email ?: "Unbekannt",
            authorPhotoUrl = user.photoUrl?.toString() ?: "",

            /* Pflichtfelder */
            title       = title,
            location    = location,
            eventDate   = eventDate,

            /* Optional */
            description = description,

            createdAt   = Timestamp.now()
        )

        db.collection("events").add(ev)               // ganzes Objekt hochladen
    }
}
