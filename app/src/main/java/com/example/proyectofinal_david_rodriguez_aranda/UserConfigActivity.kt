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

    lateinit var binding: ActivityUserConfigBinding
    lateinit var storage: FirebaseStorage
    var camarero: Camarero?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUserConfigBinding.inflate(layoutInflater)
        storage= FirebaseStorage.getInstance("gs://proyectofinal-29247.appspot.com/")
        camarero= intent.extras?.getSerializable("CAMARERO") as Camarero?
        setContentView(binding.root)
        recogerDatos()
        setListeners()
    }

    private fun recogerDatos() {
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

    private fun setListeners() {
        binding.ivProfileImageEdit.setOnClickListener {
            changePicture()
        }
    }

    private fun changePicture() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 123)
    }

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
}