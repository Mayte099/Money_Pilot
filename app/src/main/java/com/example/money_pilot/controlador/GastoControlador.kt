package com.example.money_pilot.controlador

import com.example.money_pilot.modelo.GastoModelo

class GastoControlador {

    // Esta función recibe el gasto desde la vista y lo procesa
    fun registrarNuevoGasto(gasto: GastoModelo): Boolean {
        // 1. Validaciones básicas (que el monto no sea 0 y los campos no estén vacíos)
        if (gasto.monto <= 0.0 || gasto.fecha.isBlank() || gasto.categoria.isBlank()) {
            return false // Retorna falso si faltan datos
        }

        // 2. Aquí irá la conexión a tu base de datos (Room o SQLite)
        // Por ahora simularemos que se guardó correctamente imprimiéndolo en consola
        println("Gasto guardado exitosamente: $gasto")

        return true // Retorna verdadero si se guardó bien
    }
}