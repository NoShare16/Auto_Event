package com.example.autoevent.event

import com.google.firebase.Timestamp

data class Event(
    val id: String          = "",            // Firestore-Doc-ID
    val creatorId: String   = "",            // für Filter / Profile
    val authorName: String  = "",
    val authorPhotoUrl: String = "",

    /* -------- Pflichtfelder -------- */
    val title: String       = "",
    val eventDate: Timestamp = Timestamp.now(),
    val location: String    = "",

    /* -------- Optional -------- */
    val description: String = "",
    val imageUrl: String = "",

    /* Admin / Sortierung */
    val createdAt: Timestamp = Timestamp.now()
)
