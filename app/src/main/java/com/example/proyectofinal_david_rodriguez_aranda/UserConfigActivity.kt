package com.example.proyectofinal_david_rodriguez_aranda

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Gallery
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivityUserConfigBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero
import com.google.firebase.storage.FirebaseStorage

class UserConfigActivity : AppCompatActivity() {
//***************************************************VARIABLES******************************************************************************************************************************************
    lateinit var binding: ActivityUserConfigBinding
    lateinit var storage: FirebaseStorage
    var camarero: Camarero?= null
//****************************************************METODOS******************************************************************************************************************************************
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUserConfigBinding.inflate(layoutInflater)
        storage= FirebaseStorage.getInstance("gs://proyectofinal-29247.appspot.com/")
        setContentView(binding.root)
        supportActionBar?.hide()
        recogerDatos()
        setListeners()
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método se encarga de recoger los datos enviados por el intent de la activity [MenuPrincipalActivity] al pulsar
     * en la opcion del menu lateral de perfil.
     * En este caso lo que se recibe es el camarero que está usando la aplicación, el cual se alamcena en la variable [camarero];
     * después se usan sus datos para rellenar los campos del perfil correctamente
     */
    private fun recogerDatos() {
        camarero= intent.extras?.getSerializable("CAMARERO") as Camarero?

        binding.tvEmailEdit.setText(camarero?.email )
        binding.tvNameEdit.setText(camarero?.nombre)
        binding.tvPasswordEdit.setText(camarero?.password)

        val ref= storage.reference
        val imagen= ref.child("${camarero?.email}/profile.png")
        imagen.metadata.addOnSuccessListener {
            //Existe la imagen y se la ponemos
            imagen.downloadUrl.addOnSuccessListener {
                val requestOptions= RequestOptions().transform(CircleCrop())
                Glide.with(binding.ivProfileImageEdit.context)
                    .load(it)
                    .centerCrop()
                    .apply(requestOptions)
                    .into(binding.ivProfileImageEdit)
            }
        }
            .addOnFailureListener {
                //No existe la imagen, se aplica la default
                val default= ref.child("default/profile.png")
                default.downloadUrl.addOnSuccessListener {
                    val requestOptions= RequestOptions().transform(CircleCrop())
                    Glide.with(binding.ivProfileImageEdit.context)
                        .load(it)
                        .centerCrop()
                        .apply(requestOptions)
                        .into(binding.ivProfileImageEdit)
                }
            }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Método que coloca los listeners a los componentes necesarios para asignarles funcionalidad
     */
    private fun setListeners() {
        binding.ivProfileImageEdit.setOnClickListener {
            changePicture()
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Este método es llamado al pulsar en la imagen de perfil y se encarga de lanzar un intent el cuál permitirá
     * al camarero seleccionar una imagen de su galeria para utilizarla como imagen de perfil
     */
    private fun changePicture() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 123)
    }

    /**
     * Esta sobreescritura del metodo [onActivityResult] se encarga de almacenar la imagen recibida de la galeria
     * en Firebase
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val ref= storage.reference

        if (requestCode == 123 && resultCode == Activity.RESULT_OK && data != null) {
            // Get the selected image URI from the gallery
            val selectedImageUri: Uri = data.data!!

            // Upload the image to Firebase Storage
            val imageRef = ref.child("${camarero?.email}/profile.png")
            imageRef.putFile(selectedImageUri)
                .addOnSuccessListener {
                    recogerDatos()
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    // Log the error message
                    exception.printStackTrace()
                }
        }
    }
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}