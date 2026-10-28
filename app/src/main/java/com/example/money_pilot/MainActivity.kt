package com.example.money_pilot

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.money_pilot.controlador.GastoControlador
import com.example.money_pilot.modelo.GastoModelo

class MainActivity : ComponentActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etCategory: EditText
    private lateinit var etMethod: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnClear: Button
    private lateinit var btnSave: Button

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

        btnClear.setOnClickListener {
            limpiarCampos()
        }

        btnSave.setOnClickListener {
            guardarDatos()
        }
    }

    private fun guardarDatos() {
        val montoTexto = etAmount.text.toString()
        val montoValor = if (montoTexto.isNotEmpty()) montoTexto.toDouble() else 0.0

        val nuevoGasto = GastoModelo(
            monto = montoValor,
            fecha = etDate.text.toString(),
            categoria = etCategory.text.toString(),
            metodo = etMethod.text.toString(),
            descripcion = etDescription.text.toString()
        )

        val exito = controlador.registrarNuevoGasto(nuevoGasto)

        if (exito) {
            Toast.makeText(this, "Gasto guardado correctamente", Toast.LENGTH_SHORT).show()
            limpiarCampos()
        } else {
            Toast.makeText(this, "Por favor, llena los campos obligatorios", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limpiarCampos() {
        etAmount.text.clear()
        etDate.text.clear()
        etCategory.text.clear()
        etMethod.text.clear()
        etDescription.text.clear()
    }
}