package com.example.money_pilot.Controlador.autenticacion

import com.example.money_pilot.modelo.ModeloIngreso

class ControladorIngreso {

    companion object {
        fun obtenerTotalIngresos(correoUsuario: String): Double {
            val usuario = controladorcrearcuenta.obtenerUsuario(correoUsuario) ?: return 0.0
            val totalRegistrado = usuario.listaIngresos.sumOf { it.monto }
            return usuario.ingresoInicial + totalRegistrado
        }

        fun obtenerListaIngresos(correoUsuario: String): List<ModeloIngreso> {
            val usuario = controladorcrearcuenta.obtenerUsuario(correoUsuario)
            return usuario?.listaIngresos ?: emptyList()
        }
    }

    fun registrarIngreso(
        correoUsuario: String,
        montoStr: String,
        categoria: String,
        fecha: String,
        metodo: String,
        descripcion: String
    ): String? {
        val usuario = controladorcrearcuenta.obtenerUsuario(correoUsuario)
            ?: return "Error: Usuario no encontrado. Inicia sesión de nuevo."

        if (montoStr.isBlank()) {
            return "Por favor ingresa un monto válido."
        }

        val monto = montoStr.toDoubleOrNull()
        if (monto == null || monto <= 0.0) {
            return "El monto debe ser mayor a 0."
        }

        if (categoria.isBlank() || categoria.contains("Selecciona")) {
            return "Por favor selecciona una categoría."
        }

        if (fecha.isBlank()) {
            return "Por favor selecciona una fecha válida."
        }

        if (metodo.isBlank() || metodo.contains("Selecciona")) {
            return "Por favor selecciona un método de pago."
        }

        val nuevoIngreso = ModeloIngreso(
            id = System.currentTimeMillis().toString(),
            monto = monto,
            categoria = categoria,
            fecha = fecha,
            metodo = metodo,
            descripcion = descripcion
        )

        usuario.listaIngresos.add(nuevoIngreso)
        return null // Éxito
    }
}