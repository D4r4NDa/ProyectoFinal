package com.example.proyectofinal_david_rodriguez_aranda.models

data class Mesa(
    var numMesa: Int?= null,
    var camarero: Camarero?= null,
    var estado: Int?= null,
    var cPedidas: ArrayList<ComidasPedidas>?= null,
    var bPedidas: ArrayList<BebidasPedidas>?= null
): java.io.Serializable
