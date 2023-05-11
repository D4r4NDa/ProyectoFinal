package com.example.proyectofinal_david_rodriguez_aranda.models

data class Mesa(
    var numMesa: Int?= null,
    var camarero: Camarero?= null,
    var estado: Int?= null,
    var cPedidas: MutableList<ComidasPedidas>?= null,
    var bPedidas: MutableList<BebidasPedidas>?= null
): java.io.Serializable
