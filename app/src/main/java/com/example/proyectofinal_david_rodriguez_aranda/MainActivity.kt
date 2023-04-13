package com.example.proyectofinal_david_rodriguez_aranda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivityMainBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Bebida
import com.example.proyectofinal_david_rodriguez_aranda.models.Comida
import com.example.proyectofinal_david_rodriguez_aranda.models.Mesa
import com.example.proyectofinal_david_rodriguez_aranda.prefs.Prefs
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
        crearMesas()
    }

    private fun crearMesas() {
        var comidas= ArrayList<Comida>()
        var bebidas= ArrayList<Bebida>()

        var mesa1= Mesa(1, "", 0)
        var mesa2= Mesa(2, "", 1)
        var mesa3= Mesa(3, "", 2)
        var mesa4= Mesa(4, "", 2)
        var mesa5= Mesa(5, "", 0)
        var mesa6= Mesa(6, "", 1)
        var mesa7= Mesa(7, "", 2)
        var mesa8= Mesa(8, "", 1)
        var mesa9= Mesa(9, "", 1)

        var mesas= ArrayList<Mesa>()
        mesas.add(mesa1)
        mesas.add(mesa2)
        mesas.add(mesa3)
        mesas.add(mesa4)
        mesas.add(mesa5)
        mesas.add(mesa6)
        mesas.add(mesa7)
        mesas.add(mesa8)
        mesas.add(mesa9)

        for(m: Mesa in mesas) {
            db.getReference("mesas").child(m.numMesa.toString()).setValue(m)
        }
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