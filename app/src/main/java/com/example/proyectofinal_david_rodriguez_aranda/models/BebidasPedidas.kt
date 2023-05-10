package com.example.proyectofinal_david_rodriguez_aranda.models

import java.io.Serializable

data class BebidasPedidas(
    var bebida: Bebida?= null,
    var cantidad: Int?= null
):Serializable
