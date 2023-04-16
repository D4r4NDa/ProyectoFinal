package com.example.proyectofinal_david_rodriguez_aranda.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal_david_rodriguez_aranda.R
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero
import com.example.proyectofinal_david_rodriguez_aranda.models.Mesa

class UsuariosAdapter (var lista: ArrayList<Camarero>, private val onMensajePulsado: (Camarero) -> Unit): RecyclerView.Adapter<UsuariosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuariosViewHolder {
        var v= LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
        return UsuariosViewHolder(v)

    }

    override fun onBindViewHolder(holder: UsuariosViewHolder, position: Int) {
        holder.render(lista[position], onMensajePulsado)
    }

    override fun getItemCount()= lista.size
}