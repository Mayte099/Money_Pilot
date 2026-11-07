package com.example.money_pilot.modelo

// Modelo de datos para registrar un ingreso de dinero
data class ModeloIngreso(
    var id: String = "",
    var monto: Double = 0.0,
    var categoria: String = "",
    var fecha: String = "",
    var metodo: String = "",
    var descripcion: String = ""
)