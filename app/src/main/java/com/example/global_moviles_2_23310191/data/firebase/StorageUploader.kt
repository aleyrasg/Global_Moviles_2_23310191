package com.example.global_moviles_2_23310191.data.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import java.util.UUID

object StorageUploader {

    /**
     * Sube una imagen a Firebase Storage bajo una ruta segura por usuario:
     * users/{uid}/{folder}/{filename}.jpg
     *
     * Requiere:
     * - Usuario autenticado (FirebaseAuth)
     * - Rules de Storage que permitan users/{uid}/...
     * - AppCheck configurado si está enforced
     */
    suspend fun uploadImage(
        uri: Uri,
        folder: String = "places",
        bucketUrl: String? = null // opcional: "gs://TU_BUCKET.appspot.com"
    ): String {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("Usuario no autenticado")

        // ✅ Usa el bucket correcto si lo pasas (recomendado si tienes varios proyectos/buckets)
        val storage = if (bucketUrl.isNullOrBlank()) {
            FirebaseStorage.getInstance()
        } else {
            FirebaseStorage.getInstance(bucketUrl)
        }

        // ✅ Ruta estándar y fácil de proteger con rules
        val filename = "${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child("users/$uid/$folder/$filename")

        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()

        try {
            // Subir archivo
            ref.putFile(uri, metadata).await()

            // Obtener URL
            return ref.downloadUrl.await().toString()
        } catch (ce: CancellationException) {
            // si tu coroutine se canceló, re-lanza
            throw ce
        } catch (e: Exception) {
            // Propaga el error con contexto (útil para debug)
            throw IllegalStateException("Error al subir imagen a Storage: ${e.message}", e)
        }
    }
}
