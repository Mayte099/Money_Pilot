package com.example.money_pilot.Controlador.autenticacion

class ControladorLogin {

    private val patronCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")

    fun validarCredenciales(usuario: String, contrasena: String): String? {
        return validarCampos(usuario, contrasena)
    }

    fun validarCampos(usuario: String, contrasena: String): String? {
        if (usuario.isBlank() || contrasena.isBlank()) {
            return "Por favor ingresa tu correo y contraseña."
        }

        val correoLimpio = usuario.lowercase().trim()
        if (!patronCorreo.matches(correoLimpio)) {
            return "Ingresa un correo electrónico válido (ejemplo: usuario@gmail.com)."
        }

        if (contrasena.length < 8) {
            return "La contraseña debe tener al menos 8 caracteres."
        }

        val usuarioRegistrado = controladorcrearcuenta.obtenerUsuario(correoLimpio)
        if (usuarioRegistrado == null) {
            return "El correo electrónico no está registrado. Crea una cuenta primero."
        }

        if (usuarioRegistrado.contrasena != contrasena) {
            return "Contraseña incorrecta. Inténtalo de nuevo."
        }

        return null // Validación exitosa, permite el ingreso
    }
}