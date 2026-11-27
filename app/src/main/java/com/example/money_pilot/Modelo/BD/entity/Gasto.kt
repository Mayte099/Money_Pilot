package com.example.money_pilot.modelo.entity

data class Gasto(
    val id: Int = 0,
    val monto: Double = 0.0,
    val fecha: String = "",
    val categoria: String = "",
    val metodo: String = "",
    val descripcion: String = ""
)