package com.example.money_pilot.modelo.entity

import kotlin.math.roundToInt

data class Meta(
    val id: Int = 0,
    val nombre: String,
    val montoObjetivo: Double,
    val montoActual: Double = 0.0
) {
    fun porcentaje(): Int {
        if (montoObjetivo <= 0.0) return 0
        return ((montoActual / montoObjetivo) * 100).roundToInt().coerceIn(0, 100)
    }
}