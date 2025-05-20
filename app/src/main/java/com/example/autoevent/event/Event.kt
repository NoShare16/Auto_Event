package com.example.autoevent.event

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Event(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val creatorId: String = "",
    val authorName: String       = "",
    val authorPhotoUrl: String   = "",
)
