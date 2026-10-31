package com.example.money_pilot

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.money_pilot.controlador.CampoGasto
import com.example.money_pilot.controlador.GastoControlador
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etCategory: EditText
    private lateinit var etMethod: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnClear: Button
    private lateinit var btnSave: Button
    private lateinit var btnBack: View
    private lateinit var btnMetas: View

    private val controlador = GastoControlador()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gasto)

        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        etCategory = findViewById(R.id.etCategory)
        etMethod = findViewById(R.id.etMethod)
        etDescription = findViewById(R.id.etDescription)
        btnClear = findViewById(R.id.btnClear)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)
        btnMetas = findViewById(R.id.btnMetas)

        configurarSelector(etDate) { mostrarCalendario() }
        configurarSelector(etCategory) { mostrarLista("Categoría", controlador.categorias, etCategory) }
        configurarSelector(etMethod) { mostrarLista("Método", controlador.metodos, etMethod) }

        etDate.setText(controlador.fechaHoy())

        listOf(etDate, etCategory, etMethod, etDescription).forEach { campo ->
            campo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    quitarError(campo)
                }
            })
        }

        etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                etAmount.error = null
            }
        })

        btnBack.setOnClickListener {
            finish()
        }

        btnMetas.isClickable = true
        btnMetas.isFocusable = true
        btnMetas.bringToFront()
        btnMetas.setOnClickListener {
            Toast.makeText(this, "Click en Metas", Toast.LENGTH_SHORT).show()
            try {
                startActivity(Intent(this, MetasActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        btnClear.setOnClickListener {
            limpiarCampos()
        }

        btnSave.setOnClickListener {
            guardarDatos()
        }
    }

    private fun configurarSelector(campo: EditText, accion: () -> Unit) {
        campo.isFocusable = false
        campo.isFocusableInTouchMode = false
        campo.isCursorVisible = false
        campo.isLongClickable = false
        campo.setOnClickListener { accion() }
    }

    private fun mostrarCalendario() {
        val formato = SimpleDateFormat(GastoControlador.FORMATO_FECHA, Locale("es", "MX"))
        formato.isLenient = false
        val calendario = Calendar.getInstance()
        try {
            formato.parse(etDate.text.toString())?.let { calendario.time = it }
        } catch (e: Exception) {
        }

        DatePickerDialog(
            this,
            { _, anio, mes, dia ->
                etDate.setText(String.format(Locale.US, "%02d/%02d/%04d", dia, mes + 1, anio))
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun mostrarLista(titulo: String, opciones: List<String>, destino: EditText) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setItems(opciones.toTypedArray()) { _, indice ->
                destino.setText(opciones[indice])
            }
            .show()
    }

    private fun guardarDatos() {
        val resultado = controlador.validarYRegistrar(
            etAmount.text.toString(),
            etDate.text.toString(),
            etCategory.text.toString(),
            etMethod.text.toString(),
            etDescription.text.toString()
        )

        if (resultado.errores.isNotEmpty()) {
            var primerError: EditText? = null
            resultado.errores.forEach { (campo, mensaje) ->
                val vista = campoDe(campo)
                if (vista === etAmount) etAmount.error = mensaje else marcarError(vista, mensaje)
                if (primerError == null) primerError = vista
            }
            primerError?.requestFocus()
            return
        }

        if (resultado.exito) {
            Toast.makeText(this, "Gasto guardado correctamente", Toast.LENGTH_SHORT).show()
            limpiarCampos()
        } else {
            Toast.makeText(this, "No se pudo guardar el gasto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun campoDe(campo: CampoGasto): EditText = when (campo) {
        CampoGasto.MONTO -> etAmount
        CampoGasto.FECHA -> etDate
        CampoGasto.CATEGORIA -> etCategory
        CampoGasto.METODO -> etMethod
        CampoGasto.DESCRIPCION -> etDescription
    }

    private fun marcarError(campo: EditText, mensaje: String) {
        campo.error = mensaje
        campo.setBackgroundResource(R.drawable.bg_campo_error)
    }

    private fun quitarError(campo: EditText) {
        campo.error = null
        campo.setBackgroundResource(R.drawable.bg_campo)
    }

    private fun limpiarCampos() {
        etAmount.text.clear()
        etDate.setText(controlador.fechaHoy())
        etCategory.text.clear()
        etMethod.text.clear()
        etDescription.text.clear()
        etAmount.error = null
        listOf(etDate, etCategory, etMethod, etDescription).forEach { quitarError(it) }
        etAmount.requestFocus()
    }
}