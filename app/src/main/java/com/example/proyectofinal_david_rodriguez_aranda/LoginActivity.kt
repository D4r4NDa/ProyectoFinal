package com.example.proyectofinal_david_rodriguez_aranda

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ProcessLifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivityLoginBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero
import com.example.proyectofinal_david_rodriguez_aranda.prefs.Prefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

/**
 * En esta activity se completa el inicio de sesión, aquí se introduce la contraseña necesaria para acceder a la aplicación
 * según el camarero seleccionado en la activity [SelectUserActivity]
 */
class LoginActivity : AppCompatActivity() {
//***************************************************VARIABLES******************************************************************************************************************************************
    lateinit var binding: ActivityLoginBinding
    lateinit var db: FirebaseDatabase
    lateinit var storage: FirebaseStorage
    lateinit var prefs: Prefs
    var email= ""
    var password= ""
    var camarero: Camarero?= null

//****************************************************METODOS******************************************************************************************************************************************
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
        storage= FirebaseStorage.getInstance("gs://proyectofinal-29247.appspot.com/")
        prefs= Prefs(this)
        supportActionBar?.hide()
        setListeners()
        recogerDatos()
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Método que coloca los listeners a los componentes necesarios para asignarles funcionalidad
     */
    private fun setListeners() {
        binding.btAccess.setOnClickListener {
            login()
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se encarga de recoger los datos enviados por el intent de la activity [SelectUserActivity];
     * en este caso es el camarero seleccionado, el cual se carga en la variable [camarero].
     * Además también se asigna el email de dicho camarero a la variable [email], la cual se usará internamente para el inicio de sesión;
     * después se muestra el nombre del camarero en un TextView y se llama al método [ponerImagen]
     */
    private fun recogerDatos() {
        val extra= intent.extras
        camarero= extra?.getSerializable("CAMARERO") as Camarero?

        binding.tvLoginUser.setText(camarero?.nombre)
        email= camarero?.email.toString()
        ponerImagen(camarero?.email.toString())
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se encarga de hacer la comprobación de las credenciales para el inicio de sesión.
     * Primero toma la contraseña introducida en el campo de texto y la introduce en la variable [password];
     * después comprueba que la contraseña no sea una cadena vacia, si lo es marca el error en el campo de texto.
     * Si la contraseña no está vacia se conecta con el servicio de autentificación de Firebase para comprobar que el email y la contraseña
     * son correctos; si lo son, se lanza la activity [MenuPrincipalActivity] con un intent que contiene el camarero con el que
     * se ha iniciado sesión, después se limpia el campo de contraseña y se establece el estado del camarero a conectado.
     *
     * En caso de que las credenciales no sean correctas se muestra un dialogo indicándolo y no se continua con el inicio de sesión
     */
    private fun login() {
        val typeface= Typeface.createFromAsset(assets, "fonts/caveat.ttf")
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

                db.getReference("camareros").child(camarero?.email.toString().replace(".","-")).setValue(camarero)

                ProcessLifecycleOwner.get().lifecycle.addObserver(Observer(this, camarero?.email.toString()))
            }else {
                val builder= AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Las credenciales no son correctas, prueba de nuevo.\nSi el error persiste contacta al administrador.")
                    .setPositiveButton("Aceptar") {_, _ ->
                        binding.etPasswordLogin.setText(null)
                    }

                val dialog= builder.create()
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_round_corners)
                dialog.setOnShowListener {
                    val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    val message = dialog.findViewById<TextView>(android.R.id.message)
                    val title = dialog.findViewById<TextView>(androidx.appcompat.R.id.alertTitle)

                    positiveButton?.let {
                        it.setTypeface(typeface)
                        it.setTextColor(Color.BLACK)
                    }
                    message?.let {
                        it.setTypeface(typeface)
                        it.setTextColor(Color.BLACK)
                        it.setTextSize(25F)
                    }
                    title?.let {
                        it.setTypeface(typeface)
                        it.setTextSize(45F)
                        it.setTextColor(Color.BLACK)
                    }
                }
                dialog.show()
            }
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se encarga de comprobar si el camarero tiene una foto de perfil subida en Firebase.
     * Tanto si la tiene como si no tras la comprobación se llamará al método [rellenarImagen] al cual se le pasará la imagen
     * del camarero si la tiene, y si no se le pasará una imagen default
     *
     * @param email
     */
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
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se encarga de renderizar la imagen enviada desde el método [ponerImagen] en el ImageView correspondiente
     * haciendo uso de la libreria Glide
     *
     * @param it
     */
    private fun rellenarImagen(it: Uri?) {
        val requestOptions= RequestOptions().transform(CircleCrop())
        Glide.with(binding.ivLoginPicture.context)
            .load(it)
            .centerCrop()
            .apply(requestOptions)
            .into(binding.ivLoginPicture)
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Esta sobreescritura del método [onDestroy] se encarga de establecer el estado el camarero a desconectado
     * cuando esta activity se destruya
     */
    override fun onDestroy() {
        super.onDestroy()
        camarero?.online = false
        db.getReference("camareros").child(camarero?.email.toString().replace(".","-")).setValue(camarero)
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}