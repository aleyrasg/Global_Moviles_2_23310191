package com.example.global_moviles_2_23310191.data.repository

import com.example.global_moviles_2_23310191.data.model.Routine
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class RoutineRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val col = db.collection("routines")

    private fun uid(): String = auth.currentUser?.uid
        ?: throw IllegalStateException("Usuario no autenticado")

    suspend fun getAll(): List<Routine> {
        val userId = uid()
        val snap = col
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        return snap.documents.mapNotNull { doc ->
            doc.toObject(Routine::class.java)?.copy(id = doc.id)
        }
    }

    suspend fun create(r: Routine) {
        val userId = uid()
        val doc = col.document()
        val data = r.copy(id = "", userId = userId)
        doc.set(data).await()
    }

    suspend fun update(r: Routine) {
        uid() // valida sesión
        require(r.id.isNotBlank()) { "Routine.id vacío" }
        col.document(r.id).set(r).await()
    }

    suspend fun delete(id: String) {
        uid()
        col.document(id).delete().await()
    }
}
