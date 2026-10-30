package com.example.money_pilot.controlador

import com.example.money_pilot.modelo.GastoModelo
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class CampoGasto { MONTO, FECHA, CATEGORIA, METODO, DESCRIPCION }

data class ResultadoRegistro(
    val exito: Boolean,
    val errores: Map<CampoGasto, String>
)

class GastoControlador {

    companion object {
        const val MONTO_MAXIMO = 1_000_000.0
        const val FORMATO_FECHA = "dd/MM/yyyy"
    }

    val categorias = listOf(
        "Alimentos",
        "Transporte",
        "Servicios",
        "Salud",
        "Entretenimiento",
        "Educación",
        "Ropa",
        "Hogar",
        "Otros"
    )

    val metodos = listOf(
        "Efectivo",
        "Tarjeta de débito",
        "Tarjeta de crédito",
        "Transferencia",
        "Vales",
        "Otro"
    )

    fun fechaHoy(): String {
        return SimpleDateFormat(FORMATO_FECHA, Locale("es", "MX")).format(Date())
    }

    private fun fechaValida(fecha: String): Boolean {
        val formato = SimpleDateFormat(FORMATO_FECHA, Locale("es", "MX"))
        formato.isLenient = false
        return try {
            formato.parse(fecha.trim())
            true
        } catch (e: ParseException) {
            false
        }
    }

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
        } else if (monto > MONTO_MAXIMO) {
            errores[CampoGasto.MONTO] = "El monto no puede superar $1,000,000"
        }

        if (fecha.isBlank()) {
            errores[CampoGasto.FECHA] = "La fecha es obligatoria"
        } else if (!fechaValida(fecha)) {
            errores[CampoGasto.FECHA] = "Fecha inválida, usa dd/MM/yyyy"
        }

        if (categoria.isBlank()) {
            errores[CampoGasto.CATEGORIA] = "La categoría es obligatoria"
        } else if (categoria.trim() !in categorias) {
            errores[CampoGasto.CATEGORIA] = "Selecciona una categoría de la lista"
        }

        if (metodo.isBlank()) {
            errores[CampoGasto.METODO] = "El método es obligatorio"
        } else if (metodo.trim() !in metodos) {
            errores[CampoGasto.METODO] = "Selecciona un método de la lista"
        }

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
        if (gasto.monto <= 0.0 || gasto.monto > MONTO_MAXIMO || gasto.fecha.isBlank() || gasto.categoria.isBlank()) {
            return false // Retorna falso si faltan datos
        }

        // 2. Aquí irá la conexión a tu base de datos (Room o SQLite)
        // Por ahora simularemos que se guardó correctamente imprimiéndolo en consola
        println("Gasto guardado exitosamente: $gasto")

        return true // Retorna verdadero si se guardó bien
    }
}