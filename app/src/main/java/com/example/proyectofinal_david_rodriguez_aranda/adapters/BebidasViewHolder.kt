package com.example.proyectofinal_david_rodriguez_aranda.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal_david_rodriguez_aranda.databinding.PedidoLayoutBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Bebida
import com.example.proyectofinal_david_rodriguez_aranda.models.BebidasPedidas
import com.example.proyectofinal_david_rodriguez_aranda.models.Comida

class BebidasViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val binding= PedidoLayoutBinding.bind(v)

    fun render(bPedidas: BebidasPedidas) {
        binding.tvNombrePedido.setText(bPedidas.bebida?.nombre)
        binding.tvCantidadPedido.setText(bPedidas.cantidad.toString())
    }
}
