package com.example.autoevent.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Liefert eine Live-Liste aller UIDs, denen der aktuelle User folgt.
 * Nutzt die Subcollection:  /follows/{myUid}/following/{targetUid}
 */
class FollowingViewModel(
    db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    auth: FirebaseAuth    = FirebaseAuth.getInstance()
) : ViewModel() {

    private val myUid = auth.currentUser!!.uid

    val followingIds = db.collection("follows")
        .document(myUid)
        .collection("following")
        .snapshots()
        .map { snap -> snap.documents.map { it.id } }           // → List<String>
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
