package com.example.money_pilot.controlador.tareas

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.money_pilot.R
import com.example.money_pilot.controlador.CampoGasto
import com.example.money_pilot.controlador.GastoControlador
import com.example.money_pilot.modelo.AppDatosCompartidos
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GastoActivity : Activity() {

    private val controlador = GastoControlador()
    private val localeMx = Locale("es", "MX")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gasto)

        AppDatosCompartidos.iniciar(this)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnPerfil = findViewById<ImageView>(R.id.btnPerfil)
        val btnCategorias = findViewById<ImageView>(R.id.btnCategorias)
        val btnAnalisis = findViewById<ImageView>(R.id.btnAnalisis)
        val btnMetas = findViewById<ImageView>(R.id.btnMetas)

        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etCategory = findViewById<EditText>(R.id.etCategory)
        val etMethod = findViewById<EditText>(R.id.etMethod)
        val etDescription = findViewById<EditText>(R.id.etDescription)

        val btnClear = findViewById<Button>(R.id.btnClear)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Navegación entre actividades
        btnBack?.setOnClickListener { finish() }
        btnPerfil?.setOnClickListener { startActivity(Intent(this, PerfilActivity::class.java)) }
        btnCategorias?.setOnClickListener { startActivity(Intent(this, CategoriasActivity::class.java)) }
        btnAnalisis?.setOnClickListener { startActivity(Intent(this, AnalisisActivity::class.java)) }
        btnMetas?.setOnClickListener { startActivity(Intent(this, MetasActivity::class.java)) }

        // Fecha por defecto hoy + DatePicker en formato dd/MM/yyyy
        val sdf = SimpleDateFormat(GastoControlador.FORMATO_FECHA, localeMx)
        etDate?.setText(controlador.fechaHoy())
        etDate?.isFocusable = false
        etDate?.isClickable = true

        etDate?.setOnClickListener {
            val cal = Calendar.getInstance(localeMx)
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val calSel = Calendar.getInstance(localeMx)
                    calSel.set(year, month, day)
                    etDate.setText(sdf.format(calSel.time))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Categorías sincronizadas con AppDatosCompartidos
        etCategory?.isFocusable = false
        etCategory?.isClickable = true
        etCategory?.setOnClickListener {
            val categorias = AppDatosCompartidos.nombresCategorias().toTypedArray()
            AlertDialog.Builder(this)
                .setTitle("Selecciona una categoría")
                .setItems(categorias) { _, which ->
                    etCategory.setText(categorias[which])
                }
                .show()
        }

        // Métodos de pago desde GastoControlador
        etMethod?.isFocusable = false
        etMethod?.isClickable = true
        etMethod?.setOnClickListener {
            val metodos = controlador.metodos.toTypedArray()
            AlertDialog.Builder(this)
                .setTitle("Selecciona un método")
                .setItems(metodos) { _, which ->
                    etMethod.setText(metodos[which])
                }
                .show()
        }

        // Botón Limpiar
        btnClear?.setOnClickListener {
            etAmount?.text?.clear()
            etCategory?.text?.clear()
            etMethod?.text?.clear()
            etDescription?.text?.clear()
            etDate?.setText(controlador.fechaHoy())
        }

        // Botón Guardar conectado a GastoControlador -> AnalisisControlador
        btnSave?.setOnClickListener {
            val resultado = controlador.validarYRegistrar(
                montoTexto = etAmount?.text?.toString() ?: "",
                fecha = etDate?.text?.toString() ?: "",
                categoria = etCategory?.text?.toString() ?: "",
                metodo = etMethod?.text?.toString() ?: "",
                descripcion = etDescription?.text?.toString() ?: ""
            )

            if (resultado.exito) {
                Toast.makeText(this, "Gasto registrado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                val primerError = resultado.errores.values.firstOrNull()
                    ?: resultado.errores[CampoGasto.MONTO]
                    ?: "Revisa los datos del gasto"
                Toast.makeText(this, primerError, Toast.LENGTH_LONG).show()
            }
        }
    }
}