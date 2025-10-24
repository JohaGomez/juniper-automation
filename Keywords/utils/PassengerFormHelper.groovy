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
import internal.GlobalVariable
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.testobject.ConditionType
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Select
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Random
import org.openqa.selenium.WebDriver
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.By
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.*
import java.time.Duration
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.temporal.ChronoUnit

public class PassengerFormHelper {

	static List<String> nombresBase = [
		"Johana",
		"Andrea",
		"Catalina",
		"Valentina",
		"Laura",
		"Sofía",
		"Camila"
	]
	static List<String> apellidosBase = [
		"Leal",
		"Martinez",
		"Rodriguez",
		"Gomez",
		"Pérez",
		"Torres"
	]

	static Random random = new Random()

	@Keyword
	static def fillPassengerData(List<Integer> edadesNinos, List<Integer> edadesInfantes) {
		if (edadesNinos == null) edadesNinos = []
		if (edadesInfantes == null) edadesInfantes = []

		WebDriver driver = DriverFactory.getWebDriver()
		List<WebElement> selects = driver.findElements(By.name("pax-treatment"))

		// Copias editables de los nombres y apellidos base
		List<String> nombresDisponibles = new ArrayList<>(nombresBase)
		List<String> apellidosDisponibles = new ArrayList<>(apellidosBase)

		int idxNino = 0
		int idxInf = 0

		for (int i = 0; i < selects.size(); i++) {
			WebElement selectElement = selects[i]
			Select select = new Select(selectElement)
			List<String> opciones = select.getOptions()*.getAttribute("value")

			String tratamiento = ""
			int edadFija = 0

			// 🧍 Adultos: MR / MRS
			if (opciones.contains("MR") || opciones.contains("MRS")) {
				tratamiento = (i % 2 == 0) ? "MR" : "MRS"
				edadFija = 30
			}
			// 👧 Niños: MISS (por compatibilidad)
			else if (opciones.contains("MISS")) {
				tratamiento = "MISS"
				edadFija = (idxNino < edadesNinos.size()) ? edadesNinos[idxNino++] : 10
			}
			// 👶 Infantes
			else if (opciones.contains("INF_M") || opciones.contains("INF_F")) {
				tratamiento = (i % 2 == 0) ? "INF_M" : "INF_F"
				edadFija = (idxInf < edadesInfantes.size()) ? edadesInfantes[idxInf++] : 1
			}

			// 👉 Asignar valor correctamente, aunque el select esté oculto (select2)
			if (tratamiento && opciones.contains(tratamiento)) {
				try {
					((JavascriptExecutor) driver).executeScript("""
					arguments[0].value = arguments[1];
					arguments[0].dispatchEvent(new Event('change', { bubbles: true }));
					arguments[0].dispatchEvent(new Event('input', { bubbles: true }));
				""", selectElement, tratamiento)

					WebUI.comment("🧒 Tratamiento ${tratamiento} asignado por JavaScript para pax ${i + 1}")
				} catch (Exception e) {
					WebUI.comment("⚠️ Error asignando tratamiento ${tratamiento} por JS: ${e.message}")
				}
			} else {
				WebUI.comment("⚠️ Ningún tratamiento válido encontrado para pax ${i + 1}")
			}

			WebUI.comment("🧾 Rellenando datos para pax ${i + 1} ➜ Tratamiento: ${tratamiento}, Edad: ${edadFija}")
			completarDatosPasajero(driver, i, edadFija, nombresDisponibles, apellidosDisponibles)
		}
	}


	private static void completarDatosPasajero(WebDriver driver, int index, int edad, List<String> nombres, List<String> apellidos) {
		String nombre = obtenerNombreUnico(nombres, index)
		String apellido = obtenerNombreUnico(apellidos, index)
		String fechaNacimiento = generarFechaNacimientoDesdeEdad(edad)
		String documento = "DOC${random.nextInt(99999999)}"
		String fechaExp = generarFechaFutura(2, 10)

		driver.findElements(By.name("pax-name"))[index]?.sendKeys(nombre)
		driver.findElements(By.name("pax-surname"))[index]?.sendKeys(apellido)

		List<WebElement> fechasNacimiento = driver.findElements(By.name("pax-birthday"))
		if (index < fechasNacimiento.size() && fechasNacimiento[index] != null) {
			WebElement fechaInput = fechasNacimiento[index]
			((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1]", fechaInput, fechaNacimiento)
			((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change'))", fechaInput)
		} else {
			WebUI.comment("⚠️ No se encontró campo de fecha de nacimiento para pax ${index + 1}, se omite asignación.")
		}

		driver.findElements(By.name("pax-document-number"))[index]?.sendKeys(documento)
		driver.findElements(By.name("pax-passport-expiration"))[index]?.sendKeys(fechaExp)

		// NUEVO: Asignar correo si el campo existe para el pasajero
		List<WebElement> camposCorreo = driver.findElements(By.xpath("//input[@type='email' and contains(@name, 'pax-email')]"))
		if (index < camposCorreo.size()) {
			WebElement campoCorreo = camposCorreo[index]
			campoCorreo.clear()
			campoCorreo.sendKeys("pasajero${index + 1}@juniper.com")
			WebUI.comment("📧 Se ingresó correo para pax ${index + 1}")
		} else {
			WebUI.comment("⚠️ No se encontró campo de correo para pax ${index + 1}, se continúa sin error.")
		}

		WebUI.comment("✅ Pax ${index + 1}: ${nombre} ${apellido}, ${edad} años, nacido el ${fechaNacimiento}")
	}


	private static String obtenerNombreUnico(List<String> lista, int fallbackIndex) {
		if (!lista.isEmpty()) {
			int idx = random.nextInt(lista.size())
			String valor = lista.remove(idx) // se elimina para no repetir
			return valor
		}
		// Si se acaban los nombres, se genera uno inventado
		return "Nombre${fallbackIndex + 1}"
	}

	private static String generarFechaNacimientoDesdeEdad(int edad) {
		LocalDate hoy = LocalDate.now()

		LocalDate fechaMin
		LocalDate fechaMax

		if (edad == 0) {
			// 👶 Menores de 1 año (infante)
			fechaMax = hoy
			fechaMin = hoy.minusMonths(11).minusDays(30)
		} else {
			// 🧒 Niños y adultos
			// Ejemplo: edad = 2 → entre hace 2 años y hace 3 años, sin pasarse
			fechaMax = hoy.minusYears(edad).plusDays(1)
			fechaMin = hoy.minusYears(edad + 1)
		}

		long diasEntre = ChronoUnit.DAYS.between(fechaMin, fechaMax)
		diasEntre = Math.max(diasEntre, 0)

		LocalDate fechaValida = fechaMin.plusDays(random.nextInt((int) diasEntre + 1))

		return fechaValida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
	}


	private static String generarFechaFutura(int anioMin, int anioMax) {
		int anios = anioMin + random.nextInt(anioMax - anioMin + 1)
		LocalDate fecha = LocalDate.now().plusYears(anios).plusDays(random.nextInt(365))
		return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
	}
}