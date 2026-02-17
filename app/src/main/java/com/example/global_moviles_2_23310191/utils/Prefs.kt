package com.example.global_moviles_2_23310191.utils

import android.content.Context

class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun saveEmail(email: String) = sp.edit().putString("email", email).apply()
    fun getEmail(): String? = sp.getString("email", null)
    fun clearEmail() = sp.edit().remove("email").apply()
}
