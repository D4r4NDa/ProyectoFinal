package com.example.proyectofinal_david_rodriguez_aranda.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal_david_rodriguez_aranda.databinding.PedidoLayoutBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Comida
import com.example.proyectofinal_david_rodriguez_aranda.models.ComidasPedidas

class ComidasViewHolder(v: View): RecyclerView.ViewHolder(v) {

    val binding= PedidoLayoutBinding.bind(v)

    fun render(cPedida: ComidasPedidas, onQuitarComida: (Comida) -> Unit) {
        binding.tvNombrePedido.setText(cPedida.comida?.nombre)
        binding.tvCantidadPedido.setText(cPedida.cantidad.toString())

        binding.ivDeletePedido.setOnClickListener {
            onQuitarComida(cPedida.comida!!)
        }
    }

}
