package com.example.global_moviles_2_23310191

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // ✅ Inicializa Firebase (importante si no lo haces en otro lado)
        FirebaseApp.initializeApp(this)

        // ✅ Configurar Firestore
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.firestoreSettings = firestoreSettings {
                // Persistencia ya viene habilitada por defecto en Android
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // ✅ App Check:
        // - Debug builds: Debug provider
        // - Release builds: Play Integrity provider
        val appCheck = FirebaseAppCheck.getInstance()

        // Detecta si es debug sin BuildConfig
        val isDebug = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0

        if (isDebug) {
            appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            appCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
    }
}
