package com.example.money_pilot.controlador.tareas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.money_pilot.R
import com.example.money_pilot.controlador.CampoContrasena
import com.example.money_pilot.controlador.CampoPerfil
import com.example.money_pilot.controlador.PerfilControlador
import com.example.money_pilot.modelo.AppDatosCompartidos

class PerfilActivity : ComponentActivity() {

    private lateinit var tvNombre: TextView
    private lateinit var tvCorreo: TextView

    private val controlador = PerfilControlador()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppDatosCompartidos.iniciar(this)
        setContentView(R.layout.activity_perfil)

        tvNombre = findViewById(R.id.tvNombreUsuario)
        tvCorreo = findViewById(R.id.tvCorreoUsuario)

        tvNombre.setOnClickListener { mostrarDialogoPerfil() }
        tvCorreo.setOnClickListener { mostrarDialogoPerfil() }
        findViewById<View>(R.id.ivAvatar).setOnClickListener { mostrarDialogoPerfil() }
        findViewById<View>(R.id.cardPassword).setOnClickListener { mostrarDialogoContrasena() }
        findViewById<View>(R.id.cardLogout).setOnClickListener { confirmarCierreSesion() }

        mostrarUsuario()
    }

    private fun mostrarUsuario() {
        val usuario = controlador.obtenerUsuario()
        tvNombre.text = if (usuario.nombre.isBlank()) "Toca para agregar tu nombre" else usuario.nombre
        tvCorreo.text = if (usuario.correo.isBlank()) "Toca para agregar tu correo" else usuario.correo
    }

    private fun dp(valor: Int): Int {
        return (valor * resources.displayMetrics.density).toInt()
    }

    private fun crearCampo(hint: String, contrasena: Boolean, tipo: Int = InputType.TYPE_CLASS_TEXT): EditText {
        val campo = EditText(this)
        campo.hint = hint
        campo.inputType = if (contrasena) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            tipo
        }
        campo.maxLines = 1
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = dp(8)
        campo.layoutParams = params
        return campo
    }

    private fun mostrarDialogoPerfil() {
        val usuario = controlador.obtenerUsuario()
        val contenedor = LinearLayout(this)
        contenedor.orientation = LinearLayout.VERTICAL
        contenedor.setPadding(dp(24), dp(16), dp(24), 0)

        val etNombre = crearCampo(
            "Nombre",
            false,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        )
        val etCorreo = crearCampo(
            "Correo electrónico",
            false,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        )
        etNombre.setText(usuario.nombre)
        etCorreo.setText(usuario.correo)
        contenedor.addView(etNombre)
        contenedor.addView(etCorreo)

        val dialogo = AlertDialog.Builder(this)
            .setTitle("Editar perfil")
            .setView(contenedor)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val resultado = controlador.guardarPerfil(
                    etNombre.text.toString(),
                    etCorreo.text.toString()
                )

                if (resultado.exito) {
                    Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    dialogo.dismiss()
                    mostrarUsuario()
                } else {
                    resultado.errores.forEach { (campo, mensaje) ->
                        when (campo) {
                            CampoPerfil.NOMBRE -> etNombre.error = mensaje
                            CampoPerfil.CORREO -> etCorreo.error = mensaje
                        }
                    }
                }
            }
        }

        dialogo.show()
    }

    private fun mostrarDialogoContrasena() {
        val existe = controlador.tieneContrasena()
        val contenedor = LinearLayout(this)
        contenedor.orientation = LinearLayout.VERTICAL
        contenedor.setPadding(dp(24), dp(16), dp(24), 0)

        val etActual = crearCampo("Contraseña actual", true)
        val etNueva = crearCampo("Nueva contraseña", true)
        val etConfirmacion = crearCampo("Confirmar nueva contraseña", true)
        if (existe) contenedor.addView(etActual)
        contenedor.addView(etNueva)
        contenedor.addView(etConfirmacion)

        val dialogo = AlertDialog.Builder(this)
            .setTitle(if (existe) "Cambiar contraseña" else "Crear contraseña")
            .setView(contenedor)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val resultado = controlador.cambiarContrasena(
                    etActual.text.toString(),
                    etNueva.text.toString(),
                    etConfirmacion.text.toString()
                )

                if (resultado.exito) {
                    Toast.makeText(this, "Contraseña guardada", Toast.LENGTH_SHORT).show()
                    dialogo.dismiss()
                } else {
                    resultado.errores.forEach { (campo, mensaje) ->
                        when (campo) {
                            CampoContrasena.ACTUAL -> etActual.error = mensaje
                            CampoContrasena.NUEVA -> etNueva.error = mensaje
                            CampoContrasena.CONFIRMACION -> etConfirmacion.error = mensaje
                        }
                    }
                }
            }
        }

        dialogo.show()
    }

    private fun confirmarCierreSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Seguro que quieres cerrar sesión?")
            .setPositiveButton("Cerrar sesión") { _, _ ->
                controlador.cerrarSesion()
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}