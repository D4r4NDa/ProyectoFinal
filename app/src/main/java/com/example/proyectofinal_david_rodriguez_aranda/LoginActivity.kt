package com.example.proyectofinal_david_rodriguez_aranda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivityLoginBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Bebida
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero
import com.example.proyectofinal_david_rodriguez_aranda.models.Comida
import com.example.proyectofinal_david_rodriguez_aranda.models.Mesa
import com.example.proyectofinal_david_rodriguez_aranda.prefs.Prefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var db: FirebaseDatabase
    lateinit var prefs: Prefs
    var email= ""
    var password= ""
    var camarero: Camarero?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
        prefs= Prefs(this)
        setListeners()
        recogerDatos()
    }

    private fun setListeners() {
        binding.btAccess.setOnClickListener {
            login()
        }
    }

    private fun recogerDatos() {
        val extra= intent.extras
        camarero= extra?.getSerializable("CAMARERO") as Camarero?

        binding.tvLoginUser.setText(camarero?.nombre)
        email= camarero?.email.toString()
    }

    private fun login() {
        password= binding.etPasswordLogin.text.toString().trim()
        if(password.length <=0) {
            binding.etPasswordLogin.setError("Porfavor rellena este campo.")
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(this) {
            if(it.isSuccessful) {
                prefs.guardarEmail(email)
                startActivity(Intent(this, MenuPrincipalActivity::class.java))

                binding.etPasswordLogin.setText("")

                camarero?.online=true

                db.getReference("camareros").child(camarero?.password.toString()).setValue(camarero)
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

    override fun onDestroy() {
        super.onDestroy()
        camarero?.online = false
        db.getReference("camareros").child(camarero?.password.toString()).setValue(camarero)
    }

}