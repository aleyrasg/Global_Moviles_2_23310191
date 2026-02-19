package com.example.global_moviles_2_23310191.data.repository

import android.net.Uri
import com.example.global_moviles_2_23310191.data.firebase.StorageUploader
import com.example.global_moviles_2_23310191.data.model.Place
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PlaceRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun uid(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

    suspend fun getAll(): List<Place> {
        val userId = uid()

        val snap = db.collection("places")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snap.documents.map { doc ->
            Place(
                id = doc.id,
                userId = doc.getString("userId") ?: userId,
                name = doc.getString("name") ?: "",
                address = doc.getString("address") ?: "",
                description = doc.getString("description") ?: "",
                photoUrl = doc.getString("photoUrl"),
                lat = doc.getDouble("lat"),
                lng = doc.getDouble("lng"),
                createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
            )
        }
    }

    suspend fun create(place: Place, photoUri: Uri?) {
        val userId = uid()

        val url: String? = photoUri?.let {
            try {
                StorageUploader.uploadImage(it, folder = "places")
            } catch (e: Exception) {
                android.util.Log.e("PlaceRepository", "Error al subir foto: ${e.message}")
                android.util.Log.e("PlaceRepository", "Verifica las reglas de Firebase Storage")
                e.printStackTrace()
                null
            }
        }

        val data = hashMapOf(
            "userId" to userId,
            "name" to place.name,
            "address" to place.address,
            "description" to place.description,
            "photoUrl" to url,
            "lat" to place.lat,
            "lng" to place.lng,
            "createdAt" to Timestamp.now()
        )

        db.collection("places").add(data).await()
    }

    suspend fun update(place: Place, newPhotoUri: Uri?) {
        uid()

        val newUrl: String? = newPhotoUri?.let {
            try {
                StorageUploader.uploadImage(it, folder = "places")
            } catch (e: Exception) {
                android.util.Log.e("PlaceRepository", "Error al subir foto: ${e.message}")
                android.util.Log.e("PlaceRepository", "Verifica las reglas de Firebase Storage")
                e.printStackTrace()
                null
            }
        }

        val data = hashMapOf<String, Any?>(
            "name" to place.name,
            "address" to place.address,
            "description" to place.description,
            "lat" to place.lat,
            "lng" to place.lng
        )

        if (newUrl != null) data["photoUrl"] = newUrl

        db.collection("places").document(place.id).update(data).await()
    }

    suspend fun delete(id: String) {
        uid()
        db.collection("places").document(id).delete().await()
    }
}
