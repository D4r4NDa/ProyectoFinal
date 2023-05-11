package com.example.proyectofinal_david_rodriguez_aranda.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal_david_rodriguez_aranda.R
import com.example.proyectofinal_david_rodriguez_aranda.models.Comida
import com.example.proyectofinal_david_rodriguez_aranda.models.ComidasPedidas

class ComidasAdapter(var lista: MutableList<ComidasPedidas>): RecyclerView.Adapter<ComidasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComidasViewHolder {
        var v= LayoutInflater.from(parent.context).inflate(R.layout.pedido_layout, parent, false)
        return ComidasViewHolder(v)
    }

    override fun onBindViewHolder(holder: ComidasViewHolder, position: Int) {
        holder.render(lista[position])
    }

    override fun getItemCount()= lista.size
}