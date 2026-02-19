package com.example.global_moviles_2_23310191.data.repository

import android.net.Uri
import com.example.global_moviles_2_23310191.data.firebase.StorageUploader
import com.example.global_moviles_2_23310191.data.model.ProgressEntry
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProgressRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun uid(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

    suspend fun getAll(): List<ProgressEntry> {
        val userId = uid()

        val snap = db.collection("progress")
            .whereEqualTo("userId", userId)
            .orderBy("dateMillis", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()

        return snap.documents.map { doc ->
            @Suppress("UNCHECKED_CAST")
            ProgressEntry(
                id = doc.id,
                userId = doc.getString("userId") ?: userId,
                dateMillis = doc.getLong("dateMillis") ?: System.currentTimeMillis(),
                hour = doc.getLong("hour")?.toInt() ?: 0,
                minute = doc.getLong("minute")?.toInt() ?: 0,
                waistCm = doc.getDouble("waistCm") ?: 0.0,
                bicepCm = doc.getDouble("bicepCm") ?: 0.0,
                gluteCm = doc.getDouble("gluteCm") ?: 0.0,
                photoUrls = (doc.get("photoUrls") as? List<String>) ?: emptyList(),
                createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
            )
        }
    }

    suspend fun create(entry: ProgressEntry, photoUris: List<Uri>) {
        val userId = uid()

        // Upload all photos only if there are any
        // Handle each photo upload individually to avoid failing all if one fails
        val uploadedUrls = if (photoUris.isNotEmpty()) {
            photoUris.mapNotNull { uri ->
                try {
                    StorageUploader.uploadImage(uri, folder = "progress")
                } catch (e: Exception) {
                    // Log error but continue with other photos
                    android.util.Log.e("ProgressRepository", "Error al subir foto: ${e.message}")
                    android.util.Log.e("ProgressRepository", "Verifica las reglas de Firebase Storage")
                    e.printStackTrace()
                    null
                }
            }
        } else {
            emptyList()
        }

        val data = hashMapOf(
            "userId" to userId,
            "dateMillis" to entry.dateMillis,
            "hour" to entry.hour,
            "minute" to entry.minute,
            "waistCm" to entry.waistCm,
            "bicepCm" to entry.bicepCm,
            "gluteCm" to entry.gluteCm,
            "photoUrls" to uploadedUrls,
            "createdAt" to Timestamp.now()
        )

        db.collection("progress").add(data).await()
    }

    suspend fun delete(id: String) {
        uid()
        db.collection("progress").document(id).delete().await()
    }
}

