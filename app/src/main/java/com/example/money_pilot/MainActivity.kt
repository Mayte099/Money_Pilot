package com.example.money_pilot

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.money_pilot.Controlador.autenticacion.ControladorIngreso
import com.example.money_pilot.Controlador.autenticacion.ControladorLogin
import com.example.money_pilot.Controlador.autenticacion.controladorcrearcuenta
import com.example.money_pilot.modelo.Modelousuario
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : Activity() {

    private val controladorLogin = ControladorLogin()
    private val controladorCrearCuenta = controladorcrearcuenta()
    private val controladorIngreso = ControladorIngreso()

    private var correoUsuarioActual: String = ""
    private var nombreUsuarioActual: String = "Usuario"

    private val localeEs = Locale.forLanguageTag("es-ES")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Locale.setDefault(localeEs)
        val config = resources.configuration
        config.setLocale(localeEs)
        createConfigurationContext(config)

        cargarVistaLogin()
    }

    // 1. PANTALLA: LOGIN
    private fun cargarVistaLogin() {
        setContentView(R.layout.login)

        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        val tvCrearCuenta = findViewById<TextView>(R.id.tvCrearCuenta)

        btnIniciarSesion?.setOnClickListener {
            val usuario = etUsuario?.text?.toString() ?: ""
            val contrasena = etContrasena?.text?.toString() ?: ""

            val mensajeError = controladorLogin.validarCampos(usuario, contrasena)

            if (mensajeError == null) {
                correoUsuarioActual = usuario.lowercase().trim()
                val usuarioObj = controladorcrearcuenta.obtenerUsuario(correoUsuarioActual)
                nombreUsuarioActual = usuarioObj?.nombre ?: if (correoUsuarioActual.contains("@")) correoUsuarioActual.substringBefore("@") else correoUsuarioActual

                Toast.makeText(this, "¡Bienvenido, $nombreUsuarioActual!", Toast.LENGTH_SHORT).show()
                cargarVistaDashboard(nombreUsuarioActual)
            } else {
                Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show()
            }
        }

        tvCrearCuenta?.setOnClickListener {
            cargarVistaCrearCuenta()
        }
    }

    // 2. PANTALLA: CREAR CUENTA
    private fun cargarVistaCrearCuenta() {
        setContentView(R.layout.crearcuenta)

        val etNombre = findViewById<EditText>(R.id.etNombreCompleto)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etPassword = findViewById<EditText>(R.id.etPasswordReg)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPasswordReg)
        val cbTrabaja = findViewById<CheckBox>(R.id.cbTrabaja)
        val etIngresoInicial = findViewById<EditText>(R.id.etIngresoInicial)
        val btnCrearMiCuenta = findViewById<Button>(R.id.btnCrearMiCuenta)

        btnCrearMiCuenta?.setOnClickListener {
            val nombre = etNombre?.text?.toString() ?: ""
            val correo = etCorreo?.text?.toString() ?: ""
            val pass = etPassword?.text?.toString() ?: ""
            val confirmPass = etConfirmPassword?.text?.toString() ?: ""
            val trabaja = cbTrabaja?.isChecked == true
            val ingreso = etIngresoInicial?.text?.toString() ?: "0"

            val mensajeError = controladorCrearCuenta.registrarUsuario(
                nombre, correo, pass, confirmPass, trabaja, ingreso
            )

            if (mensajeError == null) {
                correoUsuarioActual = correo.lowercase().trim()
                nombreUsuarioActual = nombre.trim()

                Toast.makeText(this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                cargarVistaDashboard(nombreUsuarioActual)
            } else {
                Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show()
            }
        }
    }

    // 3. PANTALLA: DASHBOARD
    private fun cargarVistaDashboard(nombreUsuario: String) {
        setContentView(R.layout.dashboard)

        val btnMenuLateral = findViewById<ImageButton>(R.id.btnMenuLateral)
        val spMeses = findViewById<Spinner>(R.id.spMeses)
        val tvSubtituloMes = findViewById<TextView>(R.id.tvSubtituloMes)
        val tvTituloGrafica = findViewById<TextView>(R.id.tvTituloGrafica)

        val cardGastos = findViewById<LinearLayout>(R.id.cardGastos)
        val cardSaldo = findViewById<LinearLayout>(R.id.cardSaldo)
        val cardIngresos = findViewById<LinearLayout>(R.id.cardIngresos)

        val tvMontoIngresos = findViewById<TextView>(R.id.tvMontoIngresos)
        val tvMontoSaldo = findViewById<TextView>(R.id.tvMontoSaldo)

        val totalIngresos = ControladorIngreso.obtenerTotalIngresos(correoUsuarioActual)
        tvMontoIngresos?.text = String.format(localeEs, "$%.2f", totalIngresos)
        tvMontoSaldo?.text = String.format(localeEs, "$%.2f", totalIngresos)

        // Abrir Menú Lateral
        btnMenuLateral?.setOnClickListener {
            cargarVistaMenuLateral()
        }

        // Configurar Spinner de Meses
        val meses = arrayOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spMeses?.adapter = adapter
        spMeses?.setSelection(5) // Junio por defecto

        spMeses?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val mesSeleccionado = meses[position]
                tvSubtituloMes?.text = mesSeleccionado
                tvTituloGrafica?.text = "Gastos de ${mesSeleccionado.lowercase(localeEs)}"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Acciones en Tarjetas
        cardGastos?.setOnClickListener {
            Toast.makeText(this, "Detalle de Gastos", Toast.LENGTH_SHORT).show()
        }

        cardSaldo?.setOnClickListener {
            Toast.makeText(this, "Saldo Disponible de $nombreUsuario: $" + String.format(localeEs, "%.2f", totalIngresos), Toast.LENGTH_SHORT).show()
        }

        cardIngresos?.setOnClickListener {
            cargarVistaRegistrarIngreso()
        }
    }

    // 4. PANTALLA: MENÚ LATERAL
    private fun cargarVistaMenuLateral() {
        setContentView(R.layout.menulateral)

        val btnCerrarMenu = findViewById<ImageButton>(R.id.btnCerrarMenu)
        val optionInicio = findViewById<LinearLayout>(R.id.optionInicio)
        val optionGastos = findViewById<TextView>(R.id.optionGastos)
        val optionIngresos = findViewById<TextView>(R.id.optionIngresos)
        val optionCategoria = findViewById<TextView>(R.id.optionCategoria)
        val optionMetas = findViewById<TextView>(R.id.optionMetas)
        val optionAnalisis = findViewById<TextView>(R.id.optionAnalisis)
        val optionCuenta = findViewById<TextView>(R.id.optionCuenta)
        val optionCerrarSesion = findViewById<LinearLayout>(R.id.optionCerrarSesion)

        btnCerrarMenu?.setOnClickListener {
            cargarVistaDashboard(nombreUsuarioActual)
        }

        optionInicio?.setOnClickListener {
            cargarVistaDashboard(nombreUsuarioActual)
        }

        optionGastos?.setOnClickListener {
            Toast.makeText(this, "Sección: Gastos", Toast.LENGTH_SHORT).show()
            cargarVistaDashboard(nombreUsuarioActual)
        }

        optionIngresos?.setOnClickListener {
            cargarVistaRegistrarIngreso()
        }

        optionCategoria?.setOnClickListener {
            Toast.makeText(this, "Sección: Categoría", Toast.LENGTH_SHORT).show()
            cargarVistaDashboard(nombreUsuarioActual)
        }

        optionMetas?.setOnClickListener {
            Toast.makeText(this, "Sección: Metas", Toast.LENGTH_SHORT).show()
            cargarVistaDashboard(nombreUsuarioActual)
        }

        optionAnalisis?.setOnClickListener {
            Toast.makeText(this, "Sección: Análisis", Toast.LENGTH_SHORT).show()
            cargarVistaDashboard(nombreUsuarioActual)
        }

        optionCuenta?.setOnClickListener {
            Toast.makeText(this, "Cuenta activa: $nombreUsuarioActual ($correoUsuarioActual)", Toast.LENGTH_LONG).show()
            cargarVistaDashboard(nombreUsuarioActual)
        }

        optionCerrarSesion?.setOnClickListener {
            correoUsuarioActual = ""
            nombreUsuarioActual = "Usuario"
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            cargarVistaLogin()
        }
    }

    // 5. PANTALLA: REGISTRAR INGRESO
    private fun cargarVistaRegistrarIngreso() {
        setContentView(R.layout.ingreso)

        val btnVolver = findViewById<ImageButton>(R.id.btnVolverIngreso)
        val etMonto = findViewById<EditText>(R.id.etMontoIngreso)
        val spCategoria = findViewById<Spinner>(R.id.spCategoriaIngreso)
        val etFecha = findViewById<EditText>(R.id.etFechaIngreso)
        val spMetodo = findViewById<Spinner>(R.id.spMetodoIngreso)
        val etDescripcion = findViewById<EditText>(R.id.etDescripcionIngreso)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarIngreso)

        btnVolver?.setOnClickListener {
            cargarVistaDashboard(nombreUsuarioActual)
        }

        // Categorías
        val categorias = arrayOf("Selecciona una categoría", "Sueldo", "Ventas", "Inversiones", "Regalo", "Otros")
        val adapterCat = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategoria?.adapter = adapterCat

        // Métodos de pago
        val metodos = arrayOf("Selecciona un método", "Efectivo", "Tarjeta de Débito", "Tarjeta de Crédito", "Transferencia")
        val adapterMet = ArrayAdapter(this, android.R.layout.simple_spinner_item, metodos)
        adapterMet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spMetodo?.adapter = adapterMet

        // Fecha por defecto hoy y DatePicker configurado en español (es_ES)
        val calendar = Calendar.getInstance(localeEs)
        val sdf = SimpleDateFormat("dd/MM/yyyy", localeEs)
        etFecha?.setText(sdf.format(calendar.time))

        etFecha?.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val calendarSelected = Calendar.getInstance(localeEs)
                calendarSelected.set(selectedYear, selectedMonth, selectedDay)
                etFecha.setText(sdf.format(calendarSelected.time))
            }, year, month, day)

            dpd.show()
        }

        // Botón Guardar Ingreso
        btnGuardar?.setOnClickListener {
            val montoStr = etMonto?.text?.toString() ?: ""
            val categoriaSel = spCategoria?.selectedItem?.toString() ?: ""
            val fechaSel = etFecha?.text?.toString() ?: ""
            val metodoSel = spMetodo?.selectedItem?.toString() ?: ""
            val descripcionStr = etDescripcion?.text?.toString() ?: ""

            val mensajeError = controladorIngreso.registrarIngreso(
                correoUsuarioActual, montoStr, categoriaSel, fechaSel, metodoSel, descripcionStr
            )

            if (mensajeError == null) {
                Toast.makeText(this, "¡Ingreso registrado exitosamente!", Toast.LENGTH_SHORT).show()
                cargarVistaDashboard(nombreUsuarioActual)
            } else {
                Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show()
            }
        }
    }
}