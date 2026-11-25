package com.example.money_pilot.controlador

import com.example.money_pilot.modelo.AppDatosCompartidos
import com.example.money_pilot.modelo.CategoriaModelo

data class ResultadoCategoria(
    val exito: Boolean,
    val error: String? = null
)

class CategoriasControlador {

    companion object {
        const val LONGITUD_MAXIMA = 25
    }

    fun obtenerCategorias(): List<CategoriaModelo> = AppDatosCompartidos.obtenerCategorias()

    fun agregarCategoria(nombreTexto: String): ResultadoCategoria {
        val nombre = nombreTexto.trim().replaceFirstChar { it.uppercase() }

        if (nombre.isBlank()) {
            return ResultadoCategoria(false, "El nombre es obligatorio")
        }
        if (nombre.length > LONGITUD_MAXIMA) {
            return ResultadoCategoria(false, "Máximo $LONGITUD_MAXIMA caracteres")
        }
        if (AppDatosCompartidos.nombresCategorias().any { it.equals(nombre, ignoreCase = true) }) {
            return ResultadoCategoria(false, "Ya existe una categoría con ese nombre")
        }

        AppDatosCompartidos.agregarCategoria(nombre)
        return ResultadoCategoria(true)
    }
}