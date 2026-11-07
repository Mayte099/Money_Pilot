package com.example.money_pilot.controlador

import com.example.money_pilot.modelo.AnalisisModelo
import com.example.money_pilot.modelo.CategoriaTotal
import com.example.money_pilot.modelo.GastoModelo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AnalisisControlador {

    companion object {
        val instancia = AnalisisControlador()
    }

    private val gastos = mutableListOf<GastoModelo>()
    private var anioSeleccionado = Calendar.getInstance().get(Calendar.YEAR)
    private var mesSeleccionado = Calendar.getInstance().get(Calendar.MONTH)
    private var analisis = AnalisisModelo()

    fun agregarGasto(gasto: GastoModelo) {
        if (gasto.monto <= 0.0 || gasto.categoria.isBlank()) return
        gastos.add(gasto)
        recalcular()
    }

    fun seleccionarMes(anio: Int, mes: Int) {
        anioSeleccionado = anio
        mesSeleccionado = mes
        recalcular()
    }

    fun anioSeleccionado(): Int = anioSeleccionado

    fun mesSeleccionado(): Int = mesSeleccionado

    fun mesesDisponibles(): List<Pair<Int, Int>> {
        val meses = linkedSetOf<Pair<Int, Int>>()
        val cal = Calendar.getInstance()
        repeat(12) {
            meses.add(Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)))
            cal.add(Calendar.MONTH, -1)
        }
        gastos.forEach { gasto ->
            fechaDe(gasto)?.let { meses.add(Pair(it.get(Calendar.YEAR), it.get(Calendar.MONTH))) }
        }
        return meses.sortedByDescending { it.first * 12 + it.second }
    }

    fun obtenerAnalisis(): AnalisisModelo = analisis

    fun hayGastos(): Boolean = gastos.isNotEmpty()

    private fun fechaDe(gasto: GastoModelo): Calendar? {
        val formato = SimpleDateFormat(GastoControlador.FORMATO_FECHA, Locale("es", "MX"))
        formato.isLenient = false
        return try {
            val fecha = formato.parse(gasto.fecha.trim()) ?: return null
            Calendar.getInstance().apply { time = fecha }
        } catch (e: Exception) {
            null
        }
    }

    private fun recalcular() {
        val delPeriodo = gastos.filter {
            val cal = fechaDe(it)
            cal != null &&
                    cal.get(Calendar.YEAR) == anioSeleccionado &&
                    cal.get(Calendar.MONTH) == mesSeleccionado
        }

        if (delPeriodo.isEmpty()) {
            analisis = AnalisisModelo()
            return
        }

        val total = delPeriodo.sumOf { it.monto }

        val categorias = delPeriodo
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
            promedioDiario = total / diasDelPeriodo(),
            categoriaMayor = categorias.firstOrNull()?.categoria ?: ""
        )
    }

    private fun diasDelPeriodo(): Int {
        val hoy = Calendar.getInstance()
        if (hoy.get(Calendar.YEAR) == anioSeleccionado && hoy.get(Calendar.MONTH) == mesSeleccionado) {
            return hoy.get(Calendar.DAY_OF_MONTH)
        }
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(anioSeleccionado, mesSeleccionado, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}