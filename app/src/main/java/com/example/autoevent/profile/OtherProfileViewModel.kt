package com.example.autoevent.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autoevent.event.Event
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OtherProfileViewModel(
    private val targetUid: String,                                 // ⬅ fremdes Profil
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val myUid get() = auth.currentUser!!.uid
    private val targetDoc  = db.collection("users").document(targetUid)

    /* ----- Live-Daten ----- */
    val user: StateFlow<User?> = targetDoc
        .snapshots()
        .map { it.toObject(User::class.java) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val posts: StateFlow<List<Event>> = db.collection("events")
        .whereEqualTo("creatorId", targetUid)
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .snapshots()
        .map { it.toObjects(Event::class.java) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /* ----- Folge-Status ----- */
    private val followingDoc = db.collection("follows")
        .document(myUid).collection("following").document(targetUid)

    val isFollowing: StateFlow<Boolean> = followingDoc
        .snapshots()
        .map { it.exists() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    /* ----- Aktionen ----- */
    fun follow()  = changeFollow(true)
    fun unfollow() = changeFollow(false)

    private fun changeFollow(follow: Boolean) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            db.runBatch { batch ->
                val incMy  = db.collection("users").document(myUid)
                val incHis = targetDoc
                if (follow) {
                    batch.set(followingDoc, mapOf("ts" to System.currentTimeMillis()))
                    batch.update(incMy,  "following", FieldValue.increment(1))
                    batch.update(incHis, "followers", FieldValue.increment(1))
                } else {
                    batch.delete(followingDoc)
                    batch.update(incMy,  "following", FieldValue.increment(-1))
                    batch.update(incHis, "followers", FieldValue.increment(-1))
                }
            }.let { Tasks.await(it) }
        }
    }
}
