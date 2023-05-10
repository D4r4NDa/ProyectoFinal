package com.example.proyectofinal_david_rodriguez_aranda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivityMainBinding

/**
 * La activity principal, actua como una pantalla de inicio de la aplicación.
 * La unica funcionalidad que contiene es un boton que lleva a la selección de usuario
 */
class MainActivity : AppCompatActivity() {
//***************************************************VARIABLES******************************************************************************************************************************************
    lateinit var binding: ActivityMainBinding

//****************************************************METODOS******************************************************************************************************************************************
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Método que coloca los listeners a los componentes necesarios para asignarles funcionalidad
     */
    private fun setListeners() {
        binding.btSelectUser.setOnClickListener {
            selectUser()
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Método que declara un intent para iniciar la activity que permite seleccionar el usuario
     */
    private fun selectUser() {
        val i = Intent(this, SelectUserActivity::class.java)
        startActivity(i)
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}