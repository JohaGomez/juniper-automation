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
	 * ✅ Método 6: Selecciona el primer día habilitado disponible en el calendario desplegado desde el input de fecha
	 * @param inputPath: testObject del campo input de fecha
	 */
	@Keyword
	def seleccionarPrimerDiaDisponibleCalendario(String inputPath) {
		TestObject inputFecha = findTestObject(inputPath)

		// Abrir el calendario
		WebUI.waitForElementVisible(inputFecha, 10)
		WebUI.click(inputFecha)

		// Crear selector dinámico para días habilitados
		TestObject primerDiaDisponible = new TestObject("primerDiaDisponible")
		primerDiaDisponible.addProperty("xpath", ConditionType.EQUALS, "(//a[contains(@class,'ui-state-default') and not(contains(@class,'ui-state-disabled'))])[1]")

		// Esperar y hacer clic
		if (WebUI.waitForElementClickable(primerDiaDisponible, 10, FailureHandling.OPTIONAL)) {
			WebUI.click(primerDiaDisponible)
			WebUI.comment("✅ Primer día habilitado del calendario seleccionado.")
		} else {
			WebUI.comment("⚠️ No se encontró ningún día habilitado en el calendario.")
		}
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
	 * ✅ Método 12:
	 * Setea una fecha ALEATORIA dentro del mes que está a 6 meses desde hoy
	 */
	@Keyword
	def setFechaAleatoriaDesdeSeisMesesFuturo(String testObjectPath, String formato = "dd/MM/yyyy") {
		LocalDate fechaMinima = LocalDate.now().plusMonths(6).withDayOfMonth(1) // primer día del mes 6 meses adelante
		LocalDate fechaMaxima = fechaMinima.withDayOfMonth(fechaMinima.lengthOfMonth()) // último día de ese mes

		int diasRango = (int) fechaMinima.until(fechaMaxima).getDays()
		int diasAleatorios = new Random().nextInt(diasRango + 1)

		LocalDate fechaAleatoria = fechaMinima.plusDays(diasAleatorios)
		String fechaFormateada = fechaAleatoria.format(DateTimeFormatter.ofPattern(formato))

		WebUI.setText(OR.findTestObject(testObjectPath), fechaFormateada)
		WebUI.comment("📅 Fecha aleatoria en el mes que está a 6 meses: " + fechaFormateada)
		return fechaFormateada
	}
}
