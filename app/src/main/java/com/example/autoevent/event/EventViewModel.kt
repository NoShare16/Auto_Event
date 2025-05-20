package com.example.autoevent.event

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class EventViewModel(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    init { subscribeToEvents() }

    private fun subscribeToEvents() {
        db.collection("events")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    _events.value = snap.toObjects(Event::class.java)
                }
            }
    }

    suspend fun addEvent(title: String, desc: String) {
        val event = Event(
            title = title,
            description = desc,
            creatorId = auth.currentUser?.uid.orEmpty()
        )
        db.collection("events").add(event).await()
    }
}
