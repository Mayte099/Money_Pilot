package com.example.money_pilot.modelo

data class Modelousuario(
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val trabaja: Boolean = false,
    val ingresoInicial: Double = 0.0,
    val listaIngresos: MutableList<ModeloIngreso> = mutableListOf()
)