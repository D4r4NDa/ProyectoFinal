package com.example.proyectofinal_david_rodriguez_aranda.models

import java.io.Serializable

data class ComidasPedidas (
    var comida: Comida?= null,
    var cantidad: Int?= null
): Serializable