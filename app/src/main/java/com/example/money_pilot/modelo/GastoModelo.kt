package com.example.money_pilot.modelo

// Usamos 'data class' porque es ideal para representar información en Kotlin
data class GastoModelo(
    val monto: Double,
    val fecha: String,
    val categoria: String,
    val metodo: String,
    val descripcion: String
)