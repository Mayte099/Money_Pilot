package com.example.money_pilot.controlador

import android.util.Patterns
import com.example.money_pilot.modelo.AppDatosCompartidos
import com.example.money_pilot.modelo.UsuarioModelo
import java.security.MessageDigest

enum class CampoContrasena { ACTUAL, NUEVA, CONFIRMACION }

enum class CampoPerfil { NOMBRE, CORREO }

data class ResultadoContrasena(
    val exito: Boolean,
    val errores: Map<CampoContrasena, String>
)

data class ResultadoPerfil(
    val exito: Boolean,
    val errores: Map<CampoPerfil, String>
)

class PerfilControlador {

    companion object {
        const val LONGITUD_MINIMA = 8
    }

    fun obtenerUsuario(): UsuarioModelo = AppDatosCompartidos.usuario

    fun tieneContrasena(): Boolean = AppDatosCompartidos.usuario.contrasenaHash.isNotEmpty()

    private fun cifrar(texto: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(texto.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun guardarPerfil(nombreTexto: String, correoTexto: String): ResultadoPerfil {
        val errores = linkedMapOf<CampoPerfil, String>()
        val nombre = nombreTexto.trim()
        val correo = correoTexto.trim()

        if (nombre.isBlank()) {
            errores[CampoPerfil.NOMBRE] = "El nombre es obligatorio"
        }

        if (correo.isBlank()) {
            errores[CampoPerfil.CORREO] = "El correo es obligatorio"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            errores[CampoPerfil.CORREO] = "Ingresa un correo válido"
        }

        if (errores.isNotEmpty()) {
            return ResultadoPerfil(false, errores)
        }

        AppDatosCompartidos.actualizarPerfil(nombre, correo)
        return ResultadoPerfil(true, emptyMap())
    }

    fun cambiarContrasena(actual: String, nueva: String, confirmacion: String): ResultadoContrasena {
        val errores = linkedMapOf<CampoContrasena, String>()
        val existe = tieneContrasena()

        if (existe) {
            if (actual.isEmpty()) {
                errores[CampoContrasena.ACTUAL] = "Ingresa tu contraseña actual"
            } else if (cifrar(actual) != AppDatosCompartidos.usuario.contrasenaHash) {
                errores[CampoContrasena.ACTUAL] = "La contraseña actual es incorrecta"
            }
        }

        if (nueva.isEmpty()) {
            errores[CampoContrasena.NUEVA] = "Ingresa la nueva contraseña"
        } else if (nueva.length < LONGITUD_MINIMA) {
            errores[CampoContrasena.NUEVA] = "Mínimo $LONGITUD_MINIMA caracteres"
        } else if (existe && nueva == actual) {
            errores[CampoContrasena.NUEVA] = "Debe ser distinta a la actual"
        }

        if (confirmacion.isEmpty()) {
            errores[CampoContrasena.CONFIRMACION] = "Confirma la nueva contraseña"
        } else if (confirmacion != nueva) {
            errores[CampoContrasena.CONFIRMACION] = "Las contraseñas no coinciden"
        }

        if (errores.isNotEmpty()) {
            return ResultadoContrasena(false, errores)
        }

        AppDatosCompartidos.actualizarContrasena(cifrar(nueva))
        return ResultadoContrasena(true, emptyMap())
    }

    fun cerrarSesion() {
        AppDatosCompartidos.cerrarSesion()
    }
}