package com.example.money_pilot

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.money_pilot.controlador.AnalisisControlador
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AnalisisActivity : ComponentActivity() {

    private lateinit var chartDona: PieChart
    private lateinit var tvTotal: TextView
    private lateinit var tvPeriodo: TextView
    private lateinit var tvPromedio: TextView
    private lateinit var tvMayorCategoria: TextView
    private lateinit var llLeyenda: LinearLayout
    private lateinit var btnBack: View
    private lateinit var btnHistorial: View
    private lateinit var btnPeriodo: View

    private val controlador = AnalisisControlador.instancia

    private val coloresGrafica by lazy {
        listOf(
            getColor(R.color.an_vino),
            getColor(R.color.an_cereza),
            getColor(R.color.an_rosa_medio),
            getColor(R.color.an_rosa_claro)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analisis)

        chartDona = findViewById(R.id.chartDona)
        tvTotal = findViewById(R.id.tvTotal)
        tvPeriodo = findViewById(R.id.tvPeriodo)
        tvPromedio = findViewById(R.id.tvPromedio)
        tvMayorCategoria = findViewById(R.id.tvMayorCategoria)
        llLeyenda = findViewById(R.id.llLeyenda)
        btnBack = findViewById(R.id.btnBackAnalisis)
        btnHistorial = findViewById(R.id.btnHistorial)
        btnPeriodo = findViewById(R.id.btnPeriodo)

        configurarGrafica()

        btnBack.setOnClickListener { finish() }
        btnHistorial.setOnClickListener {
            Toast.makeText(this, "Historial detallado próximamente", Toast.LENGTH_SHORT).show()
        }
        btnPeriodo.setOnClickListener { mostrarSelectorMes() }

        actualizarPantalla()
    }

    override fun onResume() {
        super.onResume()
        actualizarPantalla()
    }

    private fun nombreMes(anio: Int, mes: Int): String {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(anio, mes, 1)
        val texto = SimpleDateFormat("MMMM yyyy", Locale("es", "MX")).format(cal.time)
        return texto.replaceFirstChar { it.uppercase() }
    }

    private fun mostrarSelectorMes() {
        val meses = controlador.mesesDisponibles()
        val nombres = meses.map { nombreMes(it.first, it.second) }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Selecciona un mes")
            .setItems(nombres) { _, indice ->
                controlador.seleccionarMes(meses[indice].first, meses[indice].second)
                actualizarPantalla()
            }
            .show()
    }

    private fun configurarGrafica() {
        chartDona.description.isEnabled = false
        chartDona.legend.isEnabled = false
        chartDona.setDrawEntryLabels(false)
        chartDona.isDrawHoleEnabled = true
        chartDona.holeRadius = 70f
        chartDona.transparentCircleRadius = 0f
        chartDona.setHoleColor(Color.TRANSPARENT)
        chartDona.setTouchEnabled(false)
        chartDona.setNoDataText("")
        chartDona.setExtraOffsets(8f, 8f, 8f, 8f)
    }

    private fun actualizarPantalla() {
        val analisis = controlador.obtenerAnalisis()

        tvPeriodo.text = nombreMes(controlador.anioSeleccionado(), controlador.mesSeleccionado())
        tvTotal.text = dinero(analisis.totalGeneral)
        tvPromedio.text = dinero(analisis.promedioDiario)
        tvMayorCategoria.text = if (analisis.categoriaMayor.isBlank()) "Vacío" else analisis.categoriaMayor

        val entradas = mutableListOf<PieEntry>()
        val colores = mutableListOf<Int>()

        if (analisis.categorias.isEmpty()) {
            entradas.add(PieEntry(1f))
            colores.add(getColor(R.color.an_gris_vacio))
        } else {
            analisis.categorias.forEachIndexed { indice, item ->
                entradas.add(PieEntry(item.total.toFloat(), item.categoria))
                colores.add(coloresGrafica[indice % coloresGrafica.size])
            }
        }

        val conjunto = PieDataSet(entradas, "")
        conjunto.colors = colores
        conjunto.setDrawValues(false)
        conjunto.sliceSpace = if (analisis.categorias.size > 1) 2f else 0f

        chartDona.data = PieData(conjunto)
        chartDona.highlightValues(null)
        chartDona.invalidate()

        llLeyenda.removeAllViews()
        analisis.categorias.forEachIndexed { indice, item ->
            llLeyenda.addView(
                crearFilaLeyenda(
                    coloresGrafica[indice % coloresGrafica.size],
                    "${item.categoria} (${Math.round(item.porcentaje)}%)"
                )
            )
        }
    }

    private fun crearFilaLeyenda(color: Int, texto: String): View {
        val fila = LinearLayout(this)
        fila.orientation = LinearLayout.HORIZONTAL
        fila.gravity = Gravity.CENTER_VERTICAL
        fila.setPadding(0, dp(4), 0, dp(4))

        val punto = View(this)
        val fondo = GradientDrawable()
        fondo.shape = GradientDrawable.OVAL
        fondo.setColor(color)
        punto.background = fondo
        fila.addView(punto, LinearLayout.LayoutParams(dp(12), dp(12)))

        val etiqueta = TextView(this)
        etiqueta.text = texto
        etiqueta.textSize = 13f
        etiqueta.setTextColor(getColor(R.color.an_vino))
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.marginStart = dp(8)
        fila.addView(etiqueta, params)

        return fila
    }

    private fun dinero(valor: Double): String {
        return String.format(Locale.US, "\$%,.2f", valor)
    }

    private fun dp(valor: Int): Int {
        return (valor * resources.displayMetrics.density).toInt()
    }
}