package com.example.money_pilot.modelo

data class UsuarioModelo(
    val nombre: String = "",
    val correo: String = "",
    val contrasenaHash: String = "",
    val sesionActiva: Boolean = true
)