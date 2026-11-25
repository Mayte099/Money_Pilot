package com.example.money_pilot.modelo

import android.content.Context
import android.content.SharedPreferences
import com.example.money_pilot.R

object AppDatosCompartidos {

    val ICONO_PREDETERMINADO = R.drawable.ic_cp_ticket

    private const val PREFS = "moneypilot_usuario"

    private var prefs: SharedPreferences? = null

    private val categorias = mutableListOf(
        CategoriaModelo("Comida", R.drawable.ic_cp_comida),
        CategoriaModelo("Hogar", R.drawable.ic_cp_hogar),
        CategoriaModelo("Transporte", R.drawable.ic_cp_transporte),
        CategoriaModelo("Salud", R.drawable.ic_cp_salud),
        CategoriaModelo("Ocio", R.drawable.ic_cp_ocio),
        CategoriaModelo("Eventos", R.drawable.ic_cp_ticket)
    )

    var usuario = UsuarioModelo()
        private set

    fun iniciar(context: Context) {
        if (prefs != null) return
        val almacen = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs = almacen
        usuario = UsuarioModelo(
            nombre = almacen.getString("nombre", "") ?: "",
            correo = almacen.getString("correo", "") ?: "",
            contrasenaHash = almacen.getString("contrasenaHash", "") ?: "",
            sesionActiva = almacen.getBoolean("sesionActiva", true)
        )
    }

    private fun guardarUsuario() {
        prefs?.edit()
            ?.putString("nombre", usuario.nombre)
            ?.putString("correo", usuario.correo)
            ?.putString("contrasenaHash", usuario.contrasenaHash)
            ?.putBoolean("sesionActiva", usuario.sesionActiva)
            ?.apply()
    }

    fun obtenerCategorias(): List<CategoriaModelo> = categorias.toList()

    fun nombresCategorias(): List<String> = categorias.map { it.nombre }

    fun agregarCategoria(nombre: String) {
        categorias.add(CategoriaModelo(nombre, ICONO_PREDETERMINADO))
    }

    fun actualizarPerfil(nombre: String, correo: String) {
        usuario = usuario.copy(nombre = nombre, correo = correo)
        guardarUsuario()
    }

    fun actualizarContrasena(hash: String) {
        usuario = usuario.copy(contrasenaHash = hash)
        guardarUsuario()
    }

    fun cerrarSesion() {
        usuario = usuario.copy(sesionActiva = false)
        guardarUsuario()
    }
}