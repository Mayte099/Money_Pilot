package com.example.money_pilot.modelo.entity

data class Ingreso(
    val id: String = "",
    val correoUsuario: String = "",
    val monto: Double = 0.0,
    val categoria: String = "",
    val fecha: String = "",
    val metodo: String = "",
    val descripcion: String = ""
)