package com.example.proyectofinal_david_rodriguez_aranda.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal_david_rodriguez_aranda.R
import com.example.proyectofinal_david_rodriguez_aranda.models.Bebida
import com.example.proyectofinal_david_rodriguez_aranda.models.BebidasPedidas

class BebidasAdapter(var lista: ArrayList<BebidasPedidas>): RecyclerView.Adapter<BebidasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BebidasViewHolder {
        Toast.makeText(parent.context, "${lista.size}", Toast.LENGTH_SHORT).show()
        var v= LayoutInflater.from(parent.context).inflate(R.layout.pedido_layout, parent, false)
        return BebidasViewHolder(v)
    }

    override fun onBindViewHolder(holder: BebidasViewHolder, position: Int) {
        holder.render(lista[position])
    }

    override fun getItemCount()= lista.size
}