package com.example.proyectofinal_david_rodriguez_aranda.adapters

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.proyectofinal_david_rodriguez_aranda.R
import com.example.proyectofinal_david_rodriguez_aranda.databinding.UserLayoutBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero
import com.google.firebase.storage.FirebaseStorage

class UsuariosViewHolder (v: View): RecyclerView.ViewHolder(v) {

    var binding= UserLayoutBinding.bind(v)
    var storage: FirebaseStorage= FirebaseStorage.getInstance("gs://proyectofinal-29247.appspot.com/")

    fun render(c: Camarero, onUsuarioPulsado: (Camarero) -> Unit) {

        binding.tvUserEmail.setText(c.email)
        binding.tvUserName.setText(c.nombre)

        if(c.online==true) {
            binding.tvUserStatus.setText("Conectado")
            binding.ivUserConnected.isVisible= true
            binding.ivUserDisconnected.isVisible= false

        }else if(c.online==false) {
            binding.tvUserStatus.setText("Desconectado")
            binding.ivUserConnected.isVisible= false
            binding.ivUserDisconnected.isVisible= true

        }

        ponerImagen(c.email.toString())

        binding.cvUsuario.setOnClickListener {
            onUsuarioPulsado(c)
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
        Glide.with(binding.ivUserPicture.context)
            .load(it)
            .centerCrop()
            .apply(requestOptions)
            .into(binding.ivUserPicture)
    }

}
