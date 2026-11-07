package com.example.money_pilot

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.money_pilot.controlador.CampoMeta
import com.example.money_pilot.controlador.MetasControlador
import com.example.money_pilot.controlador.ResultadoMeta
import java.util.Locale

class MetasActivity : ComponentActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etMonto: EditText
    private lateinit var btnCrear: Button
    private lateinit var btnAgregarMonto: Button
    private lateinit var btnBack: View
    private lateinit var llMetas: LinearLayout
    private lateinit var tvActivas: TextView

    private val controlador = MetasControlador()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metas)

        etNombre = findViewById(R.id.etNombreMeta)
        etMonto = findViewById(R.id.etMontoMeta)
        btnCrear = findViewById(R.id.btnCrearMeta)
        btnAgregarMonto = findViewById(R.id.btnAgregarMonto)
        btnBack = findViewById(R.id.btnBackMetas)
        llMetas = findViewById(R.id.llMetas)
        tvActivas = findViewById(R.id.tvActivas)

        listOf(etNombre, etMonto).forEach { campo ->
            campo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    campo.error = null
                }
            })
        }

        btnBack.setOnClickListener { finish() }

        btnCrear.setOnClickListener {
            procesar(
                controlador.crearMeta(etNombre.text.toString(), etMonto.text.toString()),
                "Meta creada correctamente"
            )
        }

        btnAgregarMonto.setOnClickListener {
            procesar(
                controlador.agregarMonto(etNombre.text.toString(), etMonto.text.toString()),
                "Monto agregado correctamente"
            )
        }

        mostrarMetas()
    }

    private fun procesar(resultado: ResultadoMeta, mensajeExito: String) {
        if (!resultado.exito) {
            resultado.errores.forEach { (campo, mensaje) ->
                campoDe(campo).error = mensaje
            }
            resultado.errores.keys.firstOrNull()?.let { campoDe(it).requestFocus() }
            return
        }

        Toast.makeText(this, mensajeExito, Toast.LENGTH_SHORT).show()
        limpiarCampos()
        ocultarTeclado()
        mostrarMetas()
    }

    private fun campoDe(campo: CampoMeta): EditText = when (campo) {
        CampoMeta.NOMBRE -> etNombre
        CampoMeta.MONTO -> etMonto
    }

    private fun mostrarMetas() {
        llMetas.removeAllViews()
        val metas = controlador.obtenerMetas()
        val inflater = LayoutInflater.from(this)

        if (metas.isEmpty()) {
            val vacio = TextView(this)
            vacio.text = "Aún no tienes metas. Crea la primera abajo."
            vacio.setTextColor(getColor(R.color.meta_texto_suave))
            vacio.textSize = 14f
            vacio.gravity = Gravity.CENTER
            vacio.setPadding(0, 24, 0, 24)
            llMetas.addView(
                vacio,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }

        metas.forEach { meta ->
            val tarjeta = inflater.inflate(R.layout.item_meta, llMetas, false)
            tarjeta.findViewById<TextView>(R.id.tvNombreMeta).text = meta.nombre
            tarjeta.findViewById<TextView>(R.id.tvMontosMeta).text =
                "${dinero(meta.montoActual)} de ${dinero(meta.montoObjetivo)}"
            tarjeta.findViewById<TextView>(R.id.tvPorcentajeMeta).text = "${meta.porcentaje()}%"
            tarjeta.findViewById<ProgressBar>(R.id.pbMeta).progress = meta.porcentaje()
            llMetas.addView(tarjeta)
        }

        val cantidad = metas.size
        tvActivas.text = "$cantidad ${if (cantidad == 1) "activa" else "activas"}"
    }

    private fun dinero(valor: Double): String {
        val formato = if (valor % 1.0 == 0.0) "\$%,.0f" else "\$%,.2f"
        return String.format(Locale.US, formato, valor)
    }

    private fun limpiarCampos() {
        etNombre.text.clear()
        etMonto.text.clear()
        etNombre.error = null
        etMonto.error = null
    }

    private fun ocultarTeclado() {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val vista = currentFocus ?: etNombre
        manager.hideSoftInputFromWindow(vista.windowToken, 0)
        vista.clearFocus()
    }
}