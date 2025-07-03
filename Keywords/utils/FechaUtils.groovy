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

import internal.GlobalVariable

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
	 * ✅ Método 6 Hoteles:
	 * Setea una fecha aleatoria que caiga en cualquier dia dentro de 2 semanas después de 3 meses desde hoy
	 */
	@Keyword
	def setFechasIdaYRegreso(String campoIda, String campoRegreso, String formato = "dd/MM/yyyy") {
		LocalDate fechaMin = LocalDate.now().plusMonths(3)
		LocalDate fechaMax = fechaMin.plusMonths(1)
		int diasRango = (int) fechaMin.until(fechaMax).getDays()
		int diasAleatorios = new Random().nextInt(diasRango + 1)
		LocalDate fechaIda = fechaMin.plusDays(diasAleatorios)
		LocalDate fechaRegreso = fechaIda.plusDays(3)

		String fechaIdaTexto = fechaIda.format(DateTimeFormatter.ofPattern(formato))
		String fechaRegresoTexto = fechaRegreso.format(DateTimeFormatter.ofPattern(formato))

		WebUI.setText(OR.findTestObject(campoIda), fechaIdaTexto)
		WebUI.click(OR.findTestObject(campoRegreso)) // Para activar el input si es necesario
		WebUI.setText(OR.findTestObject(campoRegreso), fechaRegresoTexto)

		WebUI.comment("📅 Fecha ida: " + fechaIdaTexto + " | Fecha regreso: " + fechaRegresoTexto)

		return [ida: fechaIdaTexto, regreso: fechaRegresoTexto]
	}

	/**
	 * ✅ Método 7:
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
}
