package com.example.proyectofinal_david_rodriguez_aranda.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal_david_rodriguez_aranda.R
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero
import com.example.proyectofinal_david_rodriguez_aranda.models.Mesa

class MesasAdapter(var lista: ArrayList<Mesa>, var c: Camarero, private val onMensajePulsado: (Mesa) -> Unit): RecyclerView.Adapter<MesasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesasViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.selector_mesa_layout, parent,false)
        return MesasViewHolder(v)
    }

    override fun onBindViewHolder(holder: MesasViewHolder, position: Int) {
        holder.render(lista[position], c , onMensajePulsado)
    }

    override fun getItemCount(): Int= lista.size
}