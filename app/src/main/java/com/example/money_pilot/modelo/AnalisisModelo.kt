package com.example.money_pilot.modelo

data class CategoriaTotal(
    val categoria: String,
    val total: Double,
    val porcentaje: Float
)

data class AnalisisModelo(
    val totalGeneral: Double = 0.0,
    val categorias: List<CategoriaTotal> = emptyList(),
    val promedioDiario: Double = 0.0,
    val categoriaMayor: String = ""
)