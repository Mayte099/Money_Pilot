package com.example.money_pilot.modelo.entity

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val contrasena: String = "",
    val contrasenaHash: String = "",
    val trabaja: Boolean = false,
    val ingresoInicial: Double = 0.0,
    val sesionActiva: Boolean = true
)