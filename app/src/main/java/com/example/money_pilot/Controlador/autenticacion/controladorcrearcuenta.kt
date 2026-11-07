package com.example.money_pilot.Controlador.autenticacion

import com.example.money_pilot.modelo.Modelousuario

class controladorcrearcuenta {

    companion object {
        // Guarda los usuarios registrados en memoria (Correo -> Modelousuario)
        val usuariosRegistrados = HashMap<String, Modelousuario>()

        fun obtenerUsuario(correo: String): Modelousuario? {
            return usuariosRegistrados[correo.lowercase().trim()]
        }
    }

    private val patronCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")

    fun registrarUsuario(
        nombre: String,
        correo: String,
        pass: String,
        confirmPass: String,
        trabaja: Boolean,
        ingresoStr: String
    ): String? {
        if (nombre.isBlank() || correo.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
            return "Por favor llena todos los campos obligatorios."
        }

        val correoLimpio = correo.lowercase().trim()
        if (!patronCorreo.matches(correoLimpio)) {
            return "Ingresa un correo electrónico válido (ejemplo: usuario@gmail.com)."
        }

        if (usuariosRegistrados.containsKey(correoLimpio)) {
            return "Este correo electrónico ya está registrado. Inicia sesión."
        }

        if (pass.length < 8) {
            return "La contraseña debe tener al menos 8 caracteres."
        }

        if (pass != confirmPass) {
            return "Las contraseñas no coinciden."
        }

        val ingresoInicial = ingresoStr.toDoubleOrNull() ?: 0.0

        val nuevoUsuario = Modelousuario(
            nombre = nombre.trim(),
            correo = correoLimpio,
            contrasena = pass,
            trabaja = trabaja,
            ingresoInicial = ingresoInicial
        )

        // Guarda las credenciales y datos del usuario registrado
        usuariosRegistrados[correoLimpio] = nuevoUsuario

        return null // Sin errores, registro exitoso
    }
}