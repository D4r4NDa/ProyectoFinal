package com.example.proyectofinal_david_rodriguez_aranda.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal_david_rodriguez_aranda.R
import com.example.proyectofinal_david_rodriguez_aranda.models.Bebida
import com.example.proyectofinal_david_rodriguez_aranda.models.BebidasPedidas

class BebidasAdapter(var lista: MutableList<BebidasPedidas>, private val onQuitarBebida: (Bebida) -> Unit): RecyclerView.Adapter<BebidasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BebidasViewHolder {
        var v= LayoutInflater.from(parent.context).inflate(R.layout.pedido_layout, parent, false)
        return BebidasViewHolder(v)
    }

    override fun onBindViewHolder(holder: BebidasViewHolder, position: Int) {
        holder.render(lista[position], onQuitarBebida)
    }

    override fun getItemCount()= lista.size
}