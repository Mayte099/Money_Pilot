package com.example.money_pilot.controlador

import com.example.money_pilot.modelo.GastoModelo

enum class CampoGasto { MONTO, FECHA, CATEGORIA, METODO, DESCRIPCION }

data class ResultadoRegistro(
    val exito: Boolean,
    val errores: Map<CampoGasto, String>
)

class GastoControlador {

    fun validarYRegistrar(
        montoTexto: String,
        fecha: String,
        categoria: String,
        metodo: String,
        descripcion: String
    ): ResultadoRegistro {
        val errores = linkedMapOf<CampoGasto, String>()

        val monto = montoTexto.trim().replace(',', '.').toDoubleOrNull()
        if (monto == null || monto.isNaN() || monto.isInfinite() || monto <= 0.0) {
            errores[CampoGasto.MONTO] = "Ingresa un monto válido mayor a 0"
        }
        if (fecha.isBlank()) errores[CampoGasto.FECHA] = "La fecha es obligatoria"
        if (categoria.isBlank()) errores[CampoGasto.CATEGORIA] = "La categoría es obligatoria"
        if (metodo.isBlank()) errores[CampoGasto.METODO] = "El método es obligatorio"
        if (descripcion.isBlank()) errores[CampoGasto.DESCRIPCION] = "La descripción es obligatoria"

        if (errores.isNotEmpty() || monto == null) {
            return ResultadoRegistro(false, errores)
        }

        val gasto = GastoModelo(
            monto = monto,
            fecha = fecha.trim(),
            categoria = categoria.trim(),
            metodo = metodo.trim(),
            descripcion = descripcion.trim()
        )

        return ResultadoRegistro(registrarNuevoGasto(gasto), emptyMap())
    }

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