package com.example.proyectofinal_david_rodriguez_aranda.adapters

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal_david_rodriguez_aranda.databinding.PedidoLayoutBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Bebida
import com.example.proyectofinal_david_rodriguez_aranda.models.BebidasPedidas

class BebidasViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val binding= PedidoLayoutBinding.bind(v)

    fun render(bPedidas: BebidasPedidas, onQuitarBebida: (Bebida) -> Unit) {
        binding.tvNombrePedido.setText(bPedidas.bebida?.nombre)
        Toast.makeText(binding.tvCantidadPedido.context, bPedidas.bebida?.nombre , Toast.LENGTH_SHORT).show()
        binding.tvCantidadPedido.setText(bPedidas.cantidad.toString())

        binding.ivDeletePedido.setOnClickListener {
            onQuitarBebida(bPedidas.bebida!!)
        }
    }
}
