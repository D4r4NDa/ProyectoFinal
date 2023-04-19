package com.example.proyectofinal_david_rodriguez_aranda

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.ProcessLifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivityLoginBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Bebida
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero
import com.example.proyectofinal_david_rodriguez_aranda.models.Comida
import com.example.proyectofinal_david_rodriguez_aranda.models.Mesa
import com.example.proyectofinal_david_rodriguez_aranda.prefs.Prefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var db: FirebaseDatabase
    lateinit var storage: FirebaseStorage
    lateinit var prefs: Prefs
    var email= ""
    var password= ""
    var camarero: Camarero?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
        storage= FirebaseStorage.getInstance("gs://proyectofinal-29247.appspot.com/")
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
        ponerImagen(camarero?.email.toString())
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
                startActivity(Intent(this, MenuPrincipalActivity::class.java).apply {
                    putExtra("CAMARERO", camarero)
                })

                binding.etPasswordLogin.setText("")

                camarero?.online=true

                db.getReference("camareros").child(camarero?.password.toString()).setValue(camarero)

                ProcessLifecycleOwner.get().lifecycle.addObserver(Observer(this, camarero?.email.toString()))
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

    private fun ponerImagen(email: String) {
        //Comprobar si el usuario tiene imagen o no
        val ref= storage.reference
        val imagen= ref.child("$email/profile.png")
        imagen.metadata.addOnSuccessListener {
            //Existe la imagen y se la ponemos
            imagen.downloadUrl.addOnSuccessListener {
                rellenarImagen(it)
            }
        }
            .addOnFailureListener {
                //No existe la imagen, se aplica la default
                val default= ref.child("default/profile.png")
                default.downloadUrl.addOnSuccessListener {
                    rellenarImagen(it)
                }
            }
    }

    private fun rellenarImagen(it: Uri?) {
        val requestOptions= RequestOptions().transform(CircleCrop())
        Glide.with(binding.ivLoginPicture.context)
            .load(it)
            .centerCrop()
            .apply(requestOptions)
            .into(binding.ivLoginPicture)
    }

    override fun onDestroy() {
        super.onDestroy()
        camarero?.online = false
        db.getReference("camareros").child(camarero?.password.toString()).setValue(camarero)
    }

}