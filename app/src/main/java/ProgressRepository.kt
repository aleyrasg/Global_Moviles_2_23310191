package com.example.global_moviles_2_23310191.ui.progress

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ProgressRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getProgress(): List<Map<String, Any>> {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

        val snap = db.collection("progress")
            .whereEqualTo("userId", uid)
            .orderBy("dateMillis", Query.Direction.DESCENDING)
            .get()
            .await()

        return snap.documents.mapNotNull { it.data }
    }

    suspend fun addProgress(data: Map<String, Any>) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

        val payload = data.toMutableMap()
        payload["userId"] = uid
        if (!payload.containsKey("dateMillis")) {
            payload["dateMillis"] = System.currentTimeMillis()
        }

        db.collection("progress").add(payload).await()
    }
}
