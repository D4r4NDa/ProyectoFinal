package com.example.proyectofinal_david_rodriguez_aranda.prefs

import android.content.Context

class Prefs (c: Context) {
    val store= c.getSharedPreferences("LOGIN",0)

    fun guardarEmail(email: String) {
        store.edit().putString("EMAIL", email).apply()
    }
    fun leerEmail(): String? {
        return store.getString("EMAIL",null)
    }
    fun borrarTodo() {
        store.edit().clear().apply()
    }
}