package utils
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Random
import com.kms.katalon.core.testobject.ObjectRepository as OR
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import internal.GlobalVariable
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.util.KeywordUtil
import java.time.temporal.ChronoUnit
import org.openqa.selenium.WebElement
import com.kms.katalon.core.testobject.ObjectRepository



public class FechaUtils {
	/**
	 * ✅ Método 1:
	 * Setea una fecha ALEATORIA entre 3 y 4 meses desde hoy
	 */
	@Keyword
	def setFechaAleatoriaDesdeTresMesesFuturo(String testObjectPath, String formato = "dd/MM/yyyy") {
		LocalDate fechaMinima = LocalDate.now().plusMonths(3)
		LocalDate fechaMaxima = fechaMinima.plusMonths(1)
		int diasRango = (int) fechaMinima.until(fechaMaxima).getDays()
		int diasAleatorios = new Random().nextInt(diasRango + 1)

		LocalDate fechaAleatoria = fechaMinima.plusDays(diasAleatorios)
		String fechaFormateada = fechaAleatoria.format(DateTimeFormatter.ofPattern(formato))

		WebUI.setText(OR.findTestObject(testObjectPath), fechaFormateada)
		WebUI.comment("📅 Fecha aleatoria entre 3 y 4 meses: " + fechaFormateada)
		return fechaFormateada
	}

	/**
	 * ✅ Método 2:
	 * Setea una fecha aleatoria que caiga en VIERNES dentro de 2 semanas después de 3 meses desde hoy
	 */
	@Keyword
	def setFechaViernesEnTresMeses(String testObjectPath, String formato = "dd/MM/yyyy") {
		LocalDate fechaInicio = LocalDate.now().plusMonths(3)
		LocalDate fechaFin = fechaInicio.plusWeeks(2)
		List<LocalDate> viernesDisponibles = []

		while (!fechaInicio.isAfter(fechaFin)) {
			if (fechaInicio.getDayOfWeek().getValue() == 5) {
				viernesDisponibles.add(fechaInicio)
			}
			fechaInicio = fechaInicio.plusDays(1)
		}

		if (viernesDisponibles.isEmpty()) {
			WebUI.comment("⚠️ No se encontró ningún viernes en el rango.")
			return null
		}

		LocalDate fechaSeleccionada = viernesDisponibles.get(new Random().nextInt(viernesDisponibles.size()))
		String fechaFormateada = fechaSeleccionada.format(DateTimeFormatter.ofPattern(formato))

		WebUI.setText(OR.findTestObject(testObjectPath), fechaFormateada)
		WebUI.comment("📅 Viernes aleatorio seteado: " + fechaFormateada)
		return fechaFormateada
	}

	/**
	 * ✅ Método 3:
	 * Setea una fecha aleatoria que caiga en DOMINGO dentro de 2 semanas después de 3 meses desde hoy
	 */
	@Keyword
	def setFechaDomingosEnTresMeses(String testObjectPath, String formato = "dd/MM/yyyy") {
		// Buscar domingos dentro de un rango de 2 semanas a partir de 3 meses desde hoy
		LocalDate fechaInicio = LocalDate.now().plusMonths(3)
		LocalDate fechaFin = fechaInicio.plusWeeks(2)

		List<LocalDate> domingosDisponibles = []

		while (!fechaInicio.isAfter(fechaFin)) {
			if (fechaInicio.getDayOfWeek().getValue() == 7) {
				// 7 = DOMINGO
				domingosDisponibles.add(fechaInicio)
			}
			fechaInicio = fechaInicio.plusDays(1)
		}

		if (domingosDisponibles.isEmpty()) {
			WebUI.comment("⚠️ No se encontró ningún domingo en el rango.")
			return null
		}

		LocalDate fechaSeleccionada = domingosDisponibles.get(new Random().nextInt(domingosDisponibles.size()))
		String fechaFormateada = fechaSeleccionada.format(DateTimeFormatter.ofPattern(formato))

		WebUI.setText(OR.findTestObject(testObjectPath), fechaFormateada)
		WebUI.comment("📅 Domingo aleatorio seteado: " + fechaFormateada)

		return fechaFormateada
	}

	/**
	 * ✅ Método 4:
	 * Setea una fecha aleatoria que caiga en SÁBADO dentro de 2 semanas después de 3 meses desde hoy
	 */
	@Keyword
	def setFechaSabadosEnTresMeses(String testObjectPath, String formato = "dd/MM/yyyy") {
		// Buscar sábados dentro de 2 semanas después de 3 meses desde hoy
		LocalDate fechaInicio = LocalDate.now().plusMonths(3)
		LocalDate fechaFin = fechaInicio.plusWeeks(2)

		List<LocalDate> sabadosDisponibles = []

		while (!fechaInicio.isAfter(fechaFin)) {
			if (fechaInicio.getDayOfWeek().getValue() == 6) {
				// 6 = SÁBADO
				sabadosDisponibles.add(fechaInicio)
			}
			fechaInicio = fechaInicio.plusDays(1)
		}

		if (sabadosDisponibles.isEmpty()) {
			WebUI.comment("⚠️ No se encontró ningún sábado en el rango.")
			return null
		}

		LocalDate fechaSeleccionada = sabadosDisponibles.get(new Random().nextInt(sabadosDisponibles.size()))
		String fechaFormateada = fechaSeleccionada.format(DateTimeFormatter.ofPattern(formato))

		WebUI.setText(OR.findTestObject(testObjectPath), fechaFormateada)
		WebUI.comment("📅 Sábado aleatorio seteado: " + fechaFormateada)

		return fechaFormateada
	}

	/**
	 * ✅ Método 5: Setea una fecha aleatoria que caiga en LUNES dentro de 2 semanas después de 3 meses desde hoy
	 */
	@Keyword
	def setFechaLunesEnTresMeses(String testObjectPath, String formato = "dd/MM/yyyy") {
		LocalDate fechaInicio = LocalDate.now().plusMonths(4)
		LocalDate fechaFin = fechaInicio.plusWeeks(2)
		List<LocalDate> lunesDisponibles = []

		while (!fechaInicio.isAfter(fechaFin)) {
			if (fechaInicio.getDayOfWeek().getValue() == 1) {
				// 1 = Lunes
				lunesDisponibles.add(fechaInicio)
			}
			fechaInicio = fechaInicio.plusDays(1)
		}

		if (lunesDisponibles.isEmpty()) {
			WebUI.comment("⚠️ No se encontró ningún lunes en el rango.")
			return null
		}

		LocalDate fechaSeleccionada = lunesDisponibles.get(new Random().nextInt(lunesDisponibles.size()))
		String fechaFormateada = fechaSeleccionada.format(DateTimeFormatter.ofPattern(formato))

		WebUI.setText(OR.findTestObject(testObjectPath), fechaFormateada)
		WebUI.comment("📅 Lunes aleatorio seteado: " + fechaFormateada)
		return fechaFormateada
	}
	
	/**
	 * ✅ Método combinado:
	 * - Si el elemento es un input, selecciona el primer día habilitado del calendario.
	 * - Si el elemento es un <select>, elige automáticamente el mes siguiente al actual.
	 * @param inputPath: testObject del campo input o select de fecha
	 */
	@Keyword
	def seleccionarPrimerDiaDisponibleCalendario(String inputPath) {
		TestObject inputFecha = ObjectRepository.findTestObject(inputPath)
		WebElement elemento = WebUiCommonHelper.findWebElement(inputFecha, 10)
		String tagName = elemento.getTagName()

		// 🧭 1️⃣ Si es un SELECT (como el combo de meses)
		if (tagName.equalsIgnoreCase("select")) {
			WebUI.comment("🔽 Detectado elemento tipo <select>: seleccionando próximo mes disponible...")

			// Calcular el siguiente mes
			LocalDate hoy = LocalDate.now()
			LocalDate mesSiguiente = hoy.plusMonths(1)
			String valorMes = mesSiguiente.format(DateTimeFormatter.ofPattern("yyyy-MM"))

			try {
				WebUI.selectOptionByValue(inputFecha, valorMes, false)
				WebUI.comment("✅ Seleccionado mes en combo: ${valorMes}")
			} catch (Exception e) {
				WebUI.comment("⚠️ No se encontró el valor ${valorMes} en el select: ${e.message}")
			}

			return
		}

		// 🧭 2️⃣ Si es un INPUT (calendario clásico)
		if (tagName.equalsIgnoreCase("input")) {
			WebUI.comment("📅 Detectado input: intentando abrir calendario y seleccionar primer día disponible...")

			// Abrir el calendario
			WebUI.waitForElementVisible(inputFecha, 10)
			WebUI.click(inputFecha)

			// Selector dinámico para días habilitados
			TestObject primerDiaDisponible = new TestObject("primerDiaDisponible")
			primerDiaDisponible.addProperty("xpath", ConditionType.EQUALS,
					"(//a[contains(@class,'ui-state-default') and not(contains(@class,'ui-state-disabled'))])[1]")

			// Esperar y hacer clic
			if (WebUI.waitForElementClickable(primerDiaDisponible, 10)) {
				WebUI.click(primerDiaDisponible)
				WebUI.comment("✅ Primer día habilitado del calendario seleccionado correctamente.")
			} else {
				WebUI.comment("⚠️ No se encontró ningún día habilitado en el calendario.")
			}

			return
		}

		// 🚫 3️⃣ Si no es ni input ni select
		WebUI.comment("⚠️ Tipo de elemento no reconocido (${tagName}), no se aplicó acción.")
	}
	

	/**
	 * ✅ Método 7 Hoteles:
	 * Setea una fecha aleatoria que caiga 2 meses hacia el futuro la ida y el regreso después de 3 dias desde hoy
	 */
	@Keyword
	def static void setFechasIdaYRegreso(String fechaIdaPath, String fechaRegresoPath) {
		def inputFechaIda = findTestObject(fechaIdaPath)
		def inputFechaRegreso = findTestObject(fechaRegresoPath)

		// Validación explícita antes de usar
		if (inputFechaIda == null) {
			KeywordUtil.markFailedAndStop("❌ El TestObject de fecha de ida no se encontró: ${fechaIdaPath}")
		}

		if (inputFechaRegreso == null) {
			KeywordUtil.markFailedAndStop("❌ El TestObject de fecha de regreso no se encontró: ${fechaRegresoPath}")
		}

		// ✅ Generar fecha de ida a 2 meses desde hoy
		Random random = new Random()
		LocalDate hoy = LocalDate.now()
		LocalDate fechaIda = hoy.plusMonths(2).plusDays(random.nextInt(5))  // un margen aleatorio de 0–4 días

		// ✅ Fecha de regreso: entre 3 y 7 días después
		LocalDate fechaRegreso = fechaIda.plusDays(3 + random.nextInt(5))

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
		String idaFormateada = fechaIda.format(formatter)
		String regresoFormateada = fechaRegreso.format(formatter)

		// Interacción con los campos
		WebUI.waitForElementVisible(inputFechaIda, 10)
		WebUI.waitForElementVisible(inputFechaRegreso, 10)
		WebUI.clearText(inputFechaIda)
		WebUI.clearText(inputFechaRegreso)
		WebUI.setText(inputFechaIda, idaFormateada)
		WebUI.setText(inputFechaRegreso, regresoFormateada)

		println "✅ Fecha ida: ${idaFormateada} | regreso: ${regresoFormateada}"
	}


	/**
	 * ✅ Método 8:
	 * Selecciona un mes aleatorio entre 3 y 4 meses desde hoy (formato yyyy-MM) en un <select>
	 */
	@Keyword
	def selectMesAleatorioDesdeTresMesesFuturo(String testObjectPath) {
		LocalDate fechaMinima = LocalDate.now().plusMonths(3)
		LocalDate fechaMaxima = fechaMinima.plusMonths(1)
		int diasRango = (int) fechaMinima.until(fechaMaxima).getDays()
		int diasAleatorios = new Random().nextInt(diasRango + 1)

		LocalDate fechaAleatoria = fechaMinima.plusDays(diasAleatorios)
		String valorSelect = fechaAleatoria.format(DateTimeFormatter.ofPattern("yyyy-MM"))

		WebUI.selectOptionByValue(OR.findTestObject(testObjectPath), valorSelect, true)
		WebUI.comment("📅 Mes seleccionado en <select>: " + valorSelect)

		return valorSelect
	}

	/**
	 * ✅ Método 9:
	 * Selecciona un mes aleatorio a futuro desde hoy (formato yyyy-MM) en un <select>
	 */
	@Keyword
	def setFechaAleatoriaDesdeUnMesFuturo(String testObjectPath, String formato = "dd/MM/yyyy") {
		LocalDate fechaMinima = LocalDate.now().plusMonths(1)
		LocalDate fechaMaxima = fechaMinima.plusMonths(1)
		int diasRango = (int) fechaMinima.until(fechaMaxima).getDays()
		int diasAleatorios = new Random().nextInt(diasRango + 1)

		LocalDate fechaAleatoria = fechaMinima.plusDays(diasAleatorios)
		String fechaFormateada = fechaAleatoria.format(DateTimeFormatter.ofPattern(formato))

		WebUI.setText(OR.findTestObject(testObjectPath), fechaFormateada)
		WebUI.comment("📅 Fecha aleatoria entre 1 y 2 meses: " + fechaFormateada)
		return fechaFormateada
	}

	/**
	 * ✅ Método 10:
	 * Setea una fecha ALEATORIA entre 4 y 5 meses desde hoy
	 */
	@Keyword
	def setFechaAleatoriaDesdeCuatroMesesFuturo(String testObjectPath, String formato = "dd/MM/yyyy") {
		LocalDate fechaMinima = LocalDate.now().plusMonths(4)  // ahora arranca en 4 meses
		LocalDate fechaMaxima = fechaMinima.plusMonths(1)      // límite en 5 meses

		int diasRango = (int) fechaMinima.until(fechaMaxima).getDays()
		int diasAleatorios = new Random().nextInt(diasRango + 1)

		LocalDate fechaAleatoria = fechaMinima.plusDays(diasAleatorios)
		String fechaFormateada = fechaAleatoria.format(DateTimeFormatter.ofPattern(formato))

		WebUI.setText(OR.findTestObject(testObjectPath), fechaFormateada)
		WebUI.comment("📅 Fecha aleatoria entre 4 y 5 meses: " + fechaFormateada)
		return fechaFormateada
	}

	/**
	 * ✅ Método 11:
	 * Setea una fecha ALEATORIA en 1 mes hacia el futuro que caiga en sábado
	 */
	@Keyword
	def setFechaSabadoEnUnMesFuturo(String testObjectPath, String formato = "dd/MM/yyyy") {
		LocalDate fechaInicio = LocalDate.now().plusMonths(1)       // Inicio: 1 mes desde hoy
		LocalDate fechaFin = fechaInicio.plusWeeks(4)               // Rango de 4 semanas después del inicio

		List<LocalDate> sabadosDisponibles = []

		while (!fechaInicio.isAfter(fechaFin)) {
			if (fechaInicio.getDayOfWeek().getValue() == 6) {
				// 6 = Sábado
				sabadosDisponibles.add(fechaInicio)
			}
			fechaInicio = fechaInicio.plusDays(1)
		}

		if (sabadosDisponibles.isEmpty()) {
			WebUI.comment("⚠️ No se encontró ningún sábado en el rango.")
			return null
		}

		// Selección aleatoria de uno de los sábados disponibles
		LocalDate fechaSeleccionada = sabadosDisponibles.get(new Random().nextInt(sabadosDisponibles.size()))
		String fechaFormateada = fechaSeleccionada.format(DateTimeFormatter.ofPattern(formato))

		WebUI.setText(OR.findTestObject(testObjectPath), fechaFormateada)
		WebUI.comment("📅 Sábado aleatorio en 1 mes: " + fechaFormateada)

		return fechaFormateada
	}

	/**
	 * ✅ Método 12 actualizado:
	 * Selecciona sábado aleatorio en 2 meses
	 */
	@Keyword
	def setFechaSabadoEnDosMeses(String testObjectPath, String formato = "dd/MM/yyyy") {
		// Buscar sábados en el mes que corresponde a 2 meses desde hoy
		LocalDate fechaInicio = LocalDate.now().plusMonths(2).withDayOfMonth(1)
		LocalDate fechaFin = fechaInicio.withDayOfMonth(fechaInicio.lengthOfMonth())

		List<LocalDate> sabadosDisponibles = []

		while (!fechaInicio.isAfter(fechaFin)) {
			if (fechaInicio.getDayOfWeek().getValue() == 6) {
				// 6 = SÁBADO
				sabadosDisponibles.add(fechaInicio)
			}
			fechaInicio = fechaInicio.plusDays(1)
		}

		if (sabadosDisponibles.isEmpty()) {
			WebUI.comment("⚠️ No se encontró ningún sábado en el mes indicado (2 meses desde hoy).")
			return null
		}

		LocalDate fechaSeleccionada = sabadosDisponibles.get(new Random().nextInt(sabadosDisponibles.size()))
		String fechaFormateada = fechaSeleccionada.format(DateTimeFormatter.ofPattern(formato))

		WebUI.setText(OR.findTestObject(testObjectPath), fechaFormateada)
		WebUI.comment("📅 Sábado aleatorio en 2 meses seteado: " + fechaFormateada)

		return fechaFormateada
	}

	/**
	 * ✅ Método 13 actualizado:
	 * Selecciona una fecha aleatorio en 2 meses a futuro
	 */
	@Keyword
	def setFechaAleatoriaEnDosMeses(String testObjectPath, String formato = "dd/MM/yyyy") {
		// 🗓️ Definir el rango del mes dentro de dos meses
		LocalDate fechaInicio = LocalDate.now().plusMonths(2).withDayOfMonth(1)
		LocalDate fechaFin = fechaInicio.withDayOfMonth(fechaInicio.lengthOfMonth())

		// 📅 Calcular la cantidad total de días del mes
		long diasEnMes = fechaInicio.lengthOfMonth()

		// 🎲 Elegir un día aleatorio entre 1 y el total de días del mes
		int diaAleatorio = new Random().nextInt((int) diasEnMes) + 1

		// 🧩 Crear la fecha final con ese día
		LocalDate fechaSeleccionada = fechaInicio.withDayOfMonth(diaAleatorio)

		// 🕓 Formatear la fecha
		String fechaFormateada = fechaSeleccionada.format(DateTimeFormatter.ofPattern(formato))

		// ✍️ Setear la fecha en el campo indicado
		WebUI.setText(OR.findTestObject(testObjectPath), fechaFormateada)
		WebUI.comment("📅 Fecha aleatoria en 2 meses seteada: " + fechaFormateada)

		return fechaFormateada
	}
	
	@Keyword
	def setFechaAleatoriaUnMesFuturo(String testObjectPath, String formato = "dd/MM/yyyy") {
		// ===============================
		// 📅 1. Calcular fecha aleatoria 1 mes a futuro
		// ===============================
		LocalDate fechaMinima = LocalDate.now().plusMonths(1)
		LocalDate fechaMaxima = fechaMinima.plusWeeks(4)
		long diasRango = fechaMinima.until(fechaMaxima).getDays()
		int diasAleatorios = new Random().nextInt((int)diasRango + 1)
		LocalDate fechaAleatoria = fechaMinima.plusDays(diasAleatorios)
		String fechaFormateada = fechaAleatoria.format(DateTimeFormatter.ofPattern(formato))

		WebUI.comment("📅 Fecha aleatoria generada: ${fechaFormateada}")

		// ===============================
		// 🔍 2. Detectar tipo de elemento
		// ===============================
		TestObject obj = OR.findTestObject(testObjectPath)
		WebElement elemento = WebUI.findWebElement(obj)
		String tagName = elemento.getTagName()

		// ===============================
		// 🧭 3. Si es un <select> (ej. mes/año)
		// ===============================
		if (tagName.equalsIgnoreCase("select")) {
			WebUI.comment("🔽 Detectado elemento tipo <select>. Seleccionando mes correspondiente...")

			// Obtener año y mes de la fecha aleatoria
			String valorOption = fechaAleatoria.format(DateTimeFormatter.ofPattern("yyyy-MM"))
			WebUI.selectOptionByValue(obj, valorOption, false)
			WebUI.comment("✅ Seleccionado valor del combo: ${valorOption}")

			return fechaFormateada
		}

		// ===============================
		// 🧩 4. Si es un <input> (editable o readonly)
		// ===============================
		if (tagName.equalsIgnoreCase("input")) {
			try {
				WebUI.comment("⌨️ Intentando ingresar fecha en input directamente...")
				WebUI.setText(obj, fechaFormateada)
			} catch (Exception e) {
				WebUI.comment("⚠️ Campo no editable. Intentando por JavaScript...")
				WebUI.executeJavaScript("arguments[0].removeAttribute('readonly'); arguments[0].setAttribute('value', arguments[1]); arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", Arrays.asList(elemento, fechaFormateada))
			}
			WebUI.comment("✅ Fecha asignada en input: ${fechaFormateada}")
			return fechaFormateada
		}

		// ===============================
		// ❔ 5. Si no es ni input ni select
		// ===============================
		WebUI.comment("⚠️ Tipo de elemento no reconocido (${tagName}). No se aplicó acción.")
		ret
	}
	
	
	
}
