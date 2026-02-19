package com.example.global_moviles_2_23310191.data.repository

import android.net.Uri
import android.util.Log
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

    // ✅ GET sin índice (NO usa orderBy en Firestore)
    suspend fun getAll(): List<ProgressEntry> {
        val userId = uid()
        Log.d("PROGRESS", "UID actual = $userId")

        val snap = db.collection("progress")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        Log.d("PROGRESS", "Docs encontrados = ${snap.size()}")

        val list = snap.documents.map { doc ->
            val photoUrlsAny = doc.get("photoUrls")
            val photoUrlsList: List<String> = when (photoUrlsAny) {
                is List<*> -> photoUrlsAny.filterIsInstance<String>()
                is Map<*, *> -> photoUrlsAny.values.filterIsInstance<String>()
                else -> emptyList()
            }

            ProgressEntry(
                id = doc.id,
                userId = doc.getString("userId") ?: userId,
                dateMillis = doc.getLong("dateMillis") ?: System.currentTimeMillis(),
                hour = doc.getLong("hour")?.toInt() ?: 0,
                minute = doc.getLong("minute")?.toInt() ?: 0,
                waistCm = doc.getDouble("waistCm") ?: 0.0,
                bicepCm = doc.getDouble("bicepCm") ?: 0.0,
                gluteCm = doc.getDouble("gluteCm") ?: 0.0,
                photoUrls = photoUrlsList,
                createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
            )
        }

        // ✅ Ordenar local (sin índice)
        return list.sortedByDescending { it.dateMillis }
    }

    suspend fun create(entry: ProgressEntry, photoUris: List<Uri>) {
        val userId = uid()

        val uploadedUrls = if (photoUris.isNotEmpty()) {
            photoUris.mapNotNull { uri ->
                try {
                    StorageUploader.uploadImage(uri, folder = "progress")
                } catch (e: Exception) {
                    Log.e("ProgressRepository", "Error al subir foto: ${e.message}", e)
                    null
                }
            }
        } else emptyList()

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
