package com.example.money_pilot.controlador.tareas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.money_pilot.R
import com.example.money_pilot.controlador.CategoriasControlador

class CategoriasActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SELECCION = "modo_seleccion"
        const val EXTRA_CATEGORIA = "categoria_seleccionada"
    }

    private lateinit var llCategorias: LinearLayout
    private lateinit var btnAnadir: Button

    private val controlador = CategoriasControlador()
    private var modoSeleccion = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        modoSeleccion = intent.getBooleanExtra(EXTRA_SELECCION, false)

        llCategorias = findViewById(R.id.llCategorias)
        btnAnadir = findViewById(R.id.btnAnadirCategoria)

        findViewById<ImageView>(R.id.btnBackCategorias).setOnClickListener { finish() }
        btnAnadir.setOnClickListener { mostrarDialogoNueva() }

        mostrarCategorias()
    }

    private fun mostrarCategorias() {
        llCategorias.removeAllViews()
        val inflater = LayoutInflater.from(this)

        controlador.obtenerCategorias().forEach { categoria ->
            val tarjeta = inflater.inflate(R.layout.item_categoria, llCategorias, false)
            tarjeta.findViewById<ImageView>(R.id.ivIconoCategoria).setImageResource(categoria.iconoRes)
            tarjeta.findViewById<TextView>(R.id.tvNombreCategoria).text = categoria.nombre
            tarjeta.setOnClickListener {
                if (modoSeleccion) {
                    setResult(RESULT_OK, Intent().putExtra(EXTRA_CATEGORIA, categoria.nombre))
                    finish()
                } else {
                    Toast.makeText(this, categoria.nombre, Toast.LENGTH_SHORT).show()
                }
            }
            llCategorias.addView(tarjeta)
        }
    }

    private fun mostrarDialogoNueva() {
        val contenedor = FrameLayout(this)
        val padding = (24 * resources.displayMetrics.density).toInt()
        contenedor.setPadding(padding, padding / 2, padding, 0)

        val campo = EditText(this)
        campo.hint = "Nombre de la categoría"
        campo.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        campo.maxLines = 1
        contenedor.addView(
            campo,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val dialogo = AlertDialog.Builder(this)
            .setTitle("Nueva categoría")
            .setView(contenedor)
            .setPositiveButton("Añadir", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val resultado = controlador.agregarCategoria(campo.text.toString())
                if (resultado.exito) {
                    Toast.makeText(this, "Categoría añadida", Toast.LENGTH_SHORT).show()
                    dialogo.dismiss()
                    mostrarCategorias()
                } else {
                    campo.error = resultado.error
                }
            }
        }

        dialogo.show()
    }
}