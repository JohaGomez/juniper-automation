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
import com.kms.katalon.core.testobject.ConditionType
import internal.GlobalVariable
import utils.PassengerFormHelper
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import org.openqa.selenium.WebElement

public class configuration_rooms {

	// 🧠 VERSIÓN ANTIGUA (se mantiene tal cual para evitar ruptura de tests existentes)
	@Keyword
	def configurarHabitacionesYPasajeros(
			int totalHabitaciones,
			List<Integer> adultos,
			List<Integer> ninos,
			List<Integer> infantes,
			List<Integer> edadesNinos,
			List<Integer> edadesInfantes
	) {
		int totalNinos = ninos.sum()
		int totalInfantes = infantes.sum()

		if (edadesNinos.size() != totalNinos) {
			throw new IllegalArgumentException("❌ Se esperaban ${totalNinos} edades de niños, pero se recibieron ${edadesNinos.size()}")
		}
		if (edadesInfantes.size() != totalInfantes) {
			throw new IllegalArgumentException("❌ Se esperaban ${totalInfantes} edades de infantes, pero se recibieron ${edadesInfantes.size()}")
		}

		WebUI.click(crearTestObjectById('select2-room-selector-1-room-selector-nrooms-container'))
		WebUI.click(crearOpcionDropdown(totalHabitaciones.toString()))

		for (int i = 1; i <= totalHabitaciones; i++) {
			seleccionarDesdeDropdown("select2-room-selector-1-room-selector-adults-selector-${i}-container", adultos[i - 1])
			seleccionarDesdeDropdown("select2-room-selector-1-room-selector-children-selector-${i}-container", ninos[i - 1])
			seleccionarDesdeDropdown("select2-room-selector-1-room-selector-babies-selector-${i}-container", infantes[i - 1])
		}

		int contadorNino = 1
		for (int edad : edadesNinos) {
			String spanId = "select2-room-selector-1-children-age-1-${contadorNino}-container"
			seleccionarDesdeDropdown(spanId, edad)
			contadorNino++
		}

		int contadorInfante = 1
		for (int edad : edadesInfantes) {
			String spanId = "select2-room-selector-1-babies-age-1-${contadorInfante}-container"
			seleccionarDesdeDropdown(spanId, edad)
			contadorInfante++
		}

		WebUI.comment("🧒 Edades niños: $edadesNinos")
		WebUI.comment("👶 Edades infantes: $edadesInfantes")
	}

	// 🚀 NUEVA VERSIÓN CORREGIDA (distribuye edades por habitación)
	@Keyword
	def configurarHabitacionesYPasajerosV2(
			int totalHabitaciones,
			List<Integer> adultos,
			List<Integer> ninos,
			List<Integer> infantes,
			List<Integer> edadesNinos,
			List<Integer> edadesInfantes
	) {
		int totalNinos = ninos.sum()
		int totalInfantes = infantes.sum()

		// ✅ Validaciones condicionales con logs detallados
		if (totalNinos > 0 && (edadesNinos == null || edadesNinos.size() != totalNinos)) {
			WebUI.comment("⚠️ Inconsistencia detectada entre la cantidad de niños y las edades proporcionadas.")
			WebUI.comment("👶 Niños esperados: ${totalNinos}")
			WebUI.comment("📋 Edades recibidas: ${edadesNinos?.size() ?: 0} → ${edadesNinos}")
			WebUI.comment("💡 Sugerencia: asegúrate de que el parámetro 'edadesNinos' contenga la misma cantidad de elementos que el número de niños.")
			WebUI.comment("   Ejemplo correcto: [5, 8]  o  '5,8' si lo pasas como texto.")
			throw new IllegalArgumentException("❌ Se esperaban ${totalNinos} edades de niños, pero se recibió ${edadesNinos?.size() ?: 0}")
		}
		
		if (totalInfantes > 0 && (edadesInfantes == null || edadesInfantes.size() != totalInfantes)) {
			WebUI.comment("⚠️ Inconsistencia detectada entre la cantidad de infantes y las edades proporcionadas.")
			WebUI.comment("👶 Infantes esperados: ${totalInfantes}")
			WebUI.comment("📋 Edades recibidas: ${edadesInfantes?.size() ?: 0} → ${edadesInfantes}")
			WebUI.comment("💡 Sugerencia: revisa el parámetro 'edadesInfantes'. Ejemplo correcto: [1] o '1'.")
			throw new IllegalArgumentException("❌ Se esperaban ${totalInfantes} edades de infantes, pero se recibió ${edadesInfantes?.size() ?: 0}")
		}
		

		// 📦 Guardamos solo si hay datos
		GlobalVariable.edadesNinos = totalNinos > 0 ? edadesNinos : []
		GlobalVariable.edadesInfantes = totalInfantes > 0 ? edadesInfantes : []

		// 🏨 Selección de número de habitaciones
		WebUI.click(crearTestObjectById('select2-room-selector-1-room-selector-nrooms-container'))
		WebUI.click(crearOpcionDropdown(totalHabitaciones.toString()))

		for (int i = 1; i <= totalHabitaciones; i++) {
			seleccionarDesdeDropdown("select2-room-selector-1-room-selector-adults-selector-${i}-container", adultos[i - 1])
			seleccionarDesdeDropdown("select2-room-selector-1-room-selector-children-selector-${i}-container", ninos[i - 1])
			seleccionarDesdeDropdown("select2-room-selector-1-room-selector-babies-selector-${i}-container", infantes[i - 1])
		}

		// 🧒 Edad de niños (si hay)
		if (totalNinos > 0) {
			int indexNino = 0
			for (int i = 1; i <= totalHabitaciones; i++) {
				for (int j = 1; j <= ninos[i - 1]; j++) {
					String spanId = "select2-room-selector-1-children-age-${i}-${j}-container"
					seleccionarDesdeDropdown(spanId, edadesNinos[indexNino])
					indexNino++
				}
			}
		}

		// 👶 Edad de infantes (si hay)
		if (totalInfantes > 0) {
			int indexInfante = 0
			for (int i = 1; i <= totalHabitaciones; i++) {
				for (int j = 1; j <= infantes[i - 1]; j++) {
					String spanId = "select2-room-selector-1-babies-age-${i}-${j}-container"
					seleccionarDesdeDropdown(spanId, edadesInfantes[indexInfante])
					indexInfante++
				}
			}
		}

		WebUI.comment("🧒 Edades niños por habitación: $edadesNinos")
		WebUI.comment("👶 Edades infantes por habitación: $edadesInfantes")
	}

	// 🛠 MÉTODOS UTILITARIOS
	private TestObject crearTestObjectById(String id) {
		TestObject to = new TestObject(id)
		to.addProperty('xpath', ConditionType.EQUALS, "//span[@id='${id}']")
		return to
	}

	private TestObject crearOpcionDropdown(String valor) {
		TestObject to = new TestObject("opcion_${valor}")
		to.addProperty('xpath', ConditionType.EQUALS, "//li[text()='${valor}']")
		return to
	}

	private void seleccionarDesdeDropdown(String spanId, int valorSeleccionar) {
		TestObject campo = crearTestObjectById(spanId)
		WebUI.waitForElementClickable(campo, 5)
		WebUI.click(campo)

		TestObject opcion = crearOpcionDropdown(valorSeleccionar.toString())
		WebUI.waitForElementVisible(opcion, 5)
		WebUI.click(opcion)
	}

	@Keyword
	def static void configurarHabitacionesYPasajerosV3(int habitaciones, List<Integer> adultos, List<Integer> ninos, List<Integer> edadesNinos) {

		WebUI.comment("⚙️ Iniciando configuración de pasajeros...")

		// === 1. DETECTAR SI EXISTE SELECTOR DE HABITACIONES ===
		TestObject selectorHabitaciones = new TestObject('selectorHabitaciones')
		selectorHabitaciones.addProperty('xpath', ConditionType.EQUALS, "//*[@id='room-selector-1-room-selector-nrooms']")

		boolean tieneHabitaciones = WebUI.waitForElementVisible(selectorHabitaciones, 3, FailureHandling.OPTIONAL)

		if (tieneHabitaciones) {
			WebUI.comment("🏨 Flujo con habitaciones detectado — seleccionando ${habitaciones} habitación(es)...")

			WebUI.executeJavaScript("""
				var sel = document.getElementById('room-selector-1-room-selector-nrooms');
				if (sel) {
					sel.value = arguments[0].toString();
					sel.dispatchEvent(new Event('change', { bubbles: true }));
					if (window.jQuery && jQuery(sel).data('select2')) {
						jQuery(sel).val(arguments[0].toString()).trigger('change.select2');
					}
				}
			""", Arrays.asList(habitaciones))

			WebUI.delay(1)
		} else {
			WebUI.comment("ℹ️ Flujo sin habitaciones detectado (ej. Insurances) — saltando esta sección.")
		}

		// === 2. SELECCIONAR ADULTOS ===
		TestObject selectorAdultos = new TestObject('selectorAdultos')
		selectorAdultos.addProperty('xpath', ConditionType.EQUALS, "//*[@id='room-selector-1-room-selector-adults-selector-1']")

		if (WebUI.waitForElementVisible(selectorAdultos, 10, FailureHandling.OPTIONAL)) {
			int valorAdultos = adultos[0]
			WebUI.comment("👤 Asignando ${valorAdultos} adultos...")

			WebUI.executeJavaScript("""
				var sel = document.getElementById('room-selector-1-room-selector-adults-selector-1');
				var val = arguments[0].toString();
				if (!sel) return;
				sel.value = val;
				sel.dispatchEvent(new Event('change', { bubbles: true }));
				if (window.jQuery && jQuery(sel).data('select2')) {
					jQuery(sel).val(val).trigger('change.select2');
				}
			""", Arrays.asList(valorAdultos))

			WebUI.delay(1)

			TestObject contenedorAdultos = new TestObject('contenedorAdultos')
			contenedorAdultos.addProperty('xpath', ConditionType.EQUALS,
					"//*[@id='select2-room-selector-1-room-selector-adults-selector-1-container']")

			if (WebUI.waitForElementVisible(contenedorAdultos, 5, FailureHandling.OPTIONAL)) {
				WebUI.comment("✅ Adultos seleccionados → ${WebUI.getText(contenedorAdultos)}")
			}
		} else {
			WebUI.comment("⚠️ No se encontró selector de adultos.")
		}

		// === 3. SELECCIONAR NIÑOS ===
		TestObject selectorNinos = new TestObject('selectorNinos')
		selectorNinos.addProperty('xpath', ConditionType.EQUALS, "//*[@id='room-selector-1-room-selector-children-selector-1']")

		if (WebUI.waitForElementVisible(selectorNinos, 5, FailureHandling.OPTIONAL)) {
			int valorNinos = ninos[0]
			WebUI.comment("🧒 Asignando ${valorNinos} niño(s)...")

			WebUI.executeJavaScript("""
				var sel = document.getElementById('room-selector-1-room-selector-children-selector-1');
				var val = arguments[0].toString();
				if (!sel) return;
				sel.value = val;
				sel.dispatchEvent(new Event('change', { bubbles: true }));
				if (window.jQuery && jQuery(sel).data('select2')) {
					jQuery(sel).val(val).trigger('change.select2');
				}
			""", Arrays.asList(valorNinos))

			WebUI.delay(1)

			TestObject contenedorNinos = new TestObject('contenedorNinos')
			contenedorNinos.addProperty('xpath', ConditionType.EQUALS,
					"//*[@id='select2-room-selector-1-room-selector-children-selector-1-container']")

			if (WebUI.waitForElementVisible(contenedorNinos, 5, FailureHandling.OPTIONAL)) {
				WebUI.comment("✅ Niños seleccionados → ${WebUI.getText(contenedorNinos)}")
			}
		} else {
			WebUI.comment("ℹ️ No hay selector de niños visible.")
		}

		// === 4. ASIGNAR EDADES DE NIÑOS (si aplica) ===
		if (ninos.sum() > 0 && edadesNinos) {
			for (int i = 1; i <= edadesNinos.size(); i++) {
				int edad = edadesNinos[i - 1]
				TestObject edadSelect = new TestObject("edadNino_${i}")
				edadSelect.addProperty('xpath', ConditionType.EQUALS, "//*[@id='room-selector-1-children-age-1-${i}']")

				if (WebUI.waitForElementVisible(edadSelect, 5, FailureHandling.OPTIONAL)) {
					WebUI.executeJavaScript("""
						var sel = document.getElementById('room-selector-1-children-age-1-${i}');
						if (!sel) return;
						var val = arguments[0].toString();
						sel.value = val;
						sel.dispatchEvent(new Event('change', { bubbles: true }));
						if (window.jQuery && jQuery(sel).data('select2')) {
							jQuery(sel).val(val).trigger('change.select2');
						}
					""", Arrays.asList(edad))
					WebUI.comment("🎯 Edad Niño ${i}: ${edad}")
				}
			}
		}

		WebUI.comment("🏁 Configuración completada correctamente.")
	}
}