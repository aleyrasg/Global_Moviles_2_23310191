package com.example.global_moviles_2_23310191.data.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

object StorageUploader {

    suspend fun uploadImage(
        uri: Uri,
        folder: String = "places"
    ): String {

        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("Usuario no autenticado")

        val storage = FirebaseStorage.getInstance()

        // Ruta segura por usuario
        val filename = "${UUID.randomUUID()}.jpg"
        val ref = storage.reference
            .child("$folder/$uid/$filename")

        // Subir archivo
        ref.putFile(uri).await()

        // Obtener URL pública
        val downloadUrl = ref.downloadUrl.await()

        return downloadUrl.toString()
    }
}
