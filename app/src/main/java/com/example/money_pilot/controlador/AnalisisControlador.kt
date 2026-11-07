package com.example.money_pilot.controlador

import com.example.money_pilot.modelo.AnalisisModelo
import com.example.money_pilot.modelo.CategoriaTotal
import com.example.money_pilot.modelo.GastoModelo
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class AnalisisControlador {

    companion object {
        val instancia = AnalisisControlador()
    }

    private val gastos = mutableListOf<GastoModelo>()
    private var analisis = AnalisisModelo()

    fun agregarGasto(gasto: GastoModelo) {
        if (gasto.monto <= 0.0 || gasto.categoria.isBlank()) return
        gastos.add(gasto)
        recalcular()
    }

    fun obtenerAnalisis(): AnalisisModelo = analisis

    fun hayGastos(): Boolean = gastos.isNotEmpty()

    private fun recalcular() {
        if (gastos.isEmpty()) {
            analisis = AnalisisModelo()
            return
        }

        val total = gastos.sumOf { it.monto }

        val categorias = gastos
            .groupBy { it.categoria.trim() }
            .map { (nombre, lista) ->
                val subtotal = lista.sumOf { it.monto }
                CategoriaTotal(
                    categoria = nombre,
                    total = subtotal,
                    porcentaje = ((subtotal / total) * 100).toFloat()
                )
            }
            .sortedByDescending { it.total }

        analisis = AnalisisModelo(
            totalGeneral = total,
            categorias = categorias,
            promedioDiario = total / diasTranscurridos(),
            categoriaMayor = categorias.firstOrNull()?.categoria ?: ""
        )
    }

    private fun diasTranscurridos(): Long {
        val formato = SimpleDateFormat(GastoControlador.FORMATO_FECHA, Locale("es", "MX"))
        formato.isLenient = false
        val fechas = gastos.mapNotNull {
            try {
                formato.parse(it.fecha.trim())?.time
            } catch (e: Exception) {
                null
            }
        }
        if (fechas.isEmpty()) return 1L
        val dias = TimeUnit.MILLISECONDS.toDays(fechas.max() - fechas.min()) + 1
        return maxOf(dias, 1L)
    }
}