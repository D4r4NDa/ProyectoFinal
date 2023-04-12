package com.example.proyectofinal_david_rodriguez_aranda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivityMainBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Bebida
import com.example.proyectofinal_david_rodriguez_aranda.models.Comida
import com.example.proyectofinal_david_rodriguez_aranda.prefs.Prefs
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var db: FirebaseDatabase
    lateinit var prefs: Prefs
    var email= ""
    var password= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
        prefs= Prefs(this)
        setListeners()
    }

    private fun setListeners() {
        binding.button.setOnClickListener {
            login()
        }
    }

    private fun recogerDatos(): Boolean {
        email= binding.etEmailLogin.text.toString().trim()
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailLogin.setError("El email introducido no es válido.")
            return false
        }
        password= binding.etPasswordLogin.text.toString().trim()
        if(password.length <=0) {
            binding.etPasswordLogin.setError("Porfavor rellena este campo.")
            return false
        }
        return true
    }

    private fun mostrarError(txt: String) {
        val builder= AlertDialog.Builder(this)
            .setTitle("ERROR")
            .setMessage("Error en el proceso: $txt")
            .setPositiveButton("Aceptar",null)
            .create()
            .show()
    }

    private fun login() {
        if(!recogerDatos()) return
        //Supuestamente ahora el email y password funcionan
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(this) {
            if(it.isSuccessful) {
                prefs.guardarEmail(email)
                startActivity(Intent(this, MenuPrincipalActivity::class.java))
            }else {
                val builder= AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Las credenciales no son correctas, prueba de nuevo.\nSi el error persiste contacta al administrador.")
                    .setPositiveButton("Aceptar",null)
                    .create()
                    .show()
            }
        }
    }
}