package com.example.proyectofinal_david_rodriguez_aranda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.example.proyectofinal_david_rodriguez_aranda.databinding.ActivityEstadoMesaBinding
import com.example.proyectofinal_david_rodriguez_aranda.models.Camarero
import com.example.proyectofinal_david_rodriguez_aranda.models.Mesa

class EstadoMesaActivity : AppCompatActivity() {

    lateinit var binding: ActivityEstadoMesaBinding
    var mesa: Mesa?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEstadoMesaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recogerDatos()
    }

    private fun recogerDatos() {
        val extra= intent.extras
        mesa= extra?.getSerializable("MESA") as Mesa?

        pintar()
    }

    private fun pintar() {
        if(mesa?.estado==0) {
            binding.btAtenderMesa.isVisible= true
            binding.tvMensajeEstadoMesa.text= "MESA LIBRE"

        }else if(mesa?.estado==1) {
            binding.btAtenderMesa.isVisible= false
            binding.tvMensajeEstadoMesa.text= "LA MESA ESTA SIENDO\nLIMPIADA"

        }else if(mesa?.estado==2) {
            binding.btAtenderMesa.isVisible= false
            binding.tvMensajeEstadoMesa.text= "LA MESA ESTA SIENDO ATENDIDA POR\nWIP"

        }
    }
}