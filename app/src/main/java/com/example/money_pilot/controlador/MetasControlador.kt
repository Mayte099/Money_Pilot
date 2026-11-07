package com.example.money_pilot.controlador

import com.example.money_pilot.modelo.MetaModelo

enum class CampoMeta { NOMBRE, MONTO }

data class ResultadoMeta(
    val exito: Boolean,
    val errores: Map<CampoMeta, String>
)

class MetasControlador {

    private val metas = mutableListOf<MetaModelo>()

    fun obtenerMetas(): List<MetaModelo> = metas.toList()

    private fun parsearMonto(texto: String): Double? {
        val valor = texto.trim().replace(',', '.').toDoubleOrNull()
        if (valor == null || valor.isNaN() || valor.isInfinite() || valor <= 0.0) return null
        return valor
    }

    fun crearMeta(nombreTexto: String, montoTexto: String): ResultadoMeta {
        val errores = linkedMapOf<CampoMeta, String>()
        val nombre = nombreTexto.trim()

        if (nombre.isBlank()) {
            errores[CampoMeta.NOMBRE] = "El nombre es obligatorio"
        } else if (metas.any { it.nombre.equals(nombre, ignoreCase = true) }) {
            errores[CampoMeta.NOMBRE] = "Ya existe una meta con ese nombre"
        }

        val monto = parsearMonto(montoTexto)
        if (monto == null) {
            errores[CampoMeta.MONTO] = "Ingresa un monto válido mayor a 0"
        }

        if (errores.isNotEmpty() || monto == null) {
            return ResultadoMeta(false, errores)
        }

        metas.add(MetaModelo(nombre, monto))
        return ResultadoMeta(true, emptyMap())
    }

    fun agregarMonto(nombreTexto: String, montoTexto: String): ResultadoMeta {
        val errores = linkedMapOf<CampoMeta, String>()
        val nombre = nombreTexto.trim()
        var indice = -1

        if (nombre.isBlank()) {
            errores[CampoMeta.NOMBRE] = "El nombre es obligatorio"
        } else {
            indice = metas.indexOfFirst { it.nombre.equals(nombre, ignoreCase = true) }
            if (indice == -1) {
                errores[CampoMeta.NOMBRE] = "No existe una meta con ese nombre"
            }
        }

        val monto = parsearMonto(montoTexto)
        if (monto == null) {
            errores[CampoMeta.MONTO] = "Ingresa un monto válido mayor a 0"
        }

        if (errores.isNotEmpty() || monto == null) {
            return ResultadoMeta(false, errores)
        }

        val meta = metas[indice]
        val nuevoActual = minOf(meta.montoActual + monto, meta.montoObjetivo)
        metas[indice] = meta.copy(montoActual = nuevoActual)
        return ResultadoMeta(true, emptyMap())
    }
}