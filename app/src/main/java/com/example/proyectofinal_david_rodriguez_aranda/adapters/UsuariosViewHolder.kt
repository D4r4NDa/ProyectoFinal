package com.example.proyectofinal_david_rodriguez_aranda.adapters

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal_david_rodriguez_aranda.R
import com.example.proyectofinal_david_rodriguez_aranda.databinding.UserLayoutBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero

class UsuariosViewHolder (v: View): RecyclerView.ViewHolder(v) {

    var binding= UserLayoutBinding.bind(v)

    fun render(c: Camarero, onMensajePulsado: (Camarero) -> Unit) {

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

        binding.cvUsuario.setOnClickListener {
            onMensajePulsado(c)
        }

    }

}
