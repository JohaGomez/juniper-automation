import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration
import groovy.json.JsonSlurper

import utils.ValidacionesPrecios
import utils.ValidacionesPreciosTabla

// ======================================================
// 🚪 LOGIN
// ======================================================
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_otn'), [:], FailureHandling.STOP_ON_FAILURE)

// ======================================================
// 📂 NAVEGACIÓN AL MENÚ PCO INTERNACIONAL
// ======================================================
WebUI.waitForElementClickable(findTestObject('Euromundo/paquetes_carry_on/repository_pco_nal/menu_pco'), 10)
WebUI.mouseOver(findTestObject('Euromundo/paquetes_carry_on/repository_pco_nal/menu_pco'))
WebUI.click(findTestObject('Euromundo/paquetes_carry_on/repository_pco_inter/select_pco_inter'))
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_box'), 10)
WebUI.click(findTestObject('Euromundo/book_steps/button_box'))
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_inter'), 15)

// 🍪 Aceptar cookies (si aparece)
if (WebUI.verifyElementPresent(findTestObject('Euromundo/book_steps/button_close_cookies'), 5, FailureHandling.OPTIONAL)) {
	WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))
	KeywordUtil.logInfo("🍪 Cookies aceptadas correctamente")
}

// ======================================================
// 🔍 BÚSQUEDA DE PAQUETE
// ======================================================
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_inter'), 15)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_inter'), '36323', true)
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/select_origin_inter'), 15)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/select_origin_inter'), '35908', true)
CustomKeywords.'utils.FechaUtils.setFechaSabadosEnTresMeses'('Euromundo/book_steps/origin_date_inter')

// 👥 Selector de habitaciones y pasajeros
CustomKeywords.'helpers.WebUIHelper.safeClick'(findTestObject('Euromundo/widget/set_rooms_pax_inter'))

// ======================================================
// 🏨 CONFIGURACIÓN DE PASAJEROS
// ======================================================
int habitaciones = 1
List<Integer> adultos = [1]
List<Integer> ninos = [2]
List<Integer> infantes = [0]

// Edades globales
List<Integer> edadesNinos = (GlobalVariable.edadesNinos ?: []).findAll { it?.toString()?.isInteger() }.collect { it.toInteger() }
List<Integer> edadesInfantes = (GlobalVariable.edadesInfantes ?: []).findAll { it?.toString()?.isInteger() }.collect { it.toInteger() }

boolean edadesNinosValidas = edadesNinos.every { it in 2..17 }
boolean edadesInfantesValidas = edadesInfantes.every { it in 0..1 }

if (!edadesNinosValidas || !edadesInfantesValidas) {
	KeywordUtil.markFailed("🚨 Edades fuera de rango: Niños ${edadesNinos}, Infantes ${edadesInfantes}")
	return
} else {
	KeywordUtil.logInfo("✅ Edades válidas ➜ Niños: ${edadesNinos}, Infantes: ${edadesInfantes}")
}

CustomKeywords.'utils.configuration_rooms.configurarHabitacionesYPasajerosV2'(
	habitaciones, adultos, ninos, infantes, edadesNinos, edadesInfantes
)

// ======================================================
// 👁 CAPTURA VISUAL DE EDADES
// ======================================================
List<Integer> edadesCapturadasNinos = []
List<Integer> edadesCapturadasInfantes = []

if (ninos.sum() > 0) {
	for (int i = 1; i <= ninos[0]; i++) {
		TestObject objNino = new TestObject("edad_nino_${i}")
		objNino.addProperty('xpath', ConditionType.EQUALS, "//*[@id='select2-room-selector-1-children-age-1-${i}-container']")
		WebUI.waitForElementVisible(objNino, 5)
		String edadTexto = WebUI.getText(objNino).replaceAll('\\D', '')
		int edad = edadTexto?.isInteger() ? edadTexto.toInteger() : -1
		if (edad in 2..17) {
			edadesCapturadasNinos.add(edad)
			WebUI.comment("🧒 Edad Niño ${i}: ${edad}")
		}
	}
}

if (infantes.sum() > 0) {
	for (int i = 1; i <= infantes[0]; i++) {
		TestObject objInf = new TestObject("edad_infante_${i}")
		objInf.addProperty('xpath', ConditionType.EQUALS, "//*[@id='select2-room-selector-1-babies-age-1-${i}-container']")
		WebUI.waitForElementVisible(objInf, 5)
		String edadTexto = WebUI.getText(objInf).replaceAll('\\D', '')
		int edad = edadTexto?.isInteger() ? edadTexto.toInteger() : -1
		if (edad in 0..1) {
			edadesCapturadasInfantes.add(edad)
			WebUI.comment("👶 Edad Infante ${i}: ${edad}")
		}
	}
}


// ======================================================
// 🔎 BÚSQUEDA Y SELECCIÓN DE HOTEL
// ======================================================
WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))

boolean hotelVisible = WebUI.waitForElementVisible(findTestObject('Euromundo/vamos_con_todo/repository_vct_nal/select_hotel_vct_nal'), 10, FailureHandling.OPTIONAL)

if (!hotelVisible) {
	WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_edit'), 10)
	WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))
	WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))
	WebUI.waitForElementVisible(findTestObject('Euromundo/vamos_con_todo/repository_vct_nal/select_hotel_vct_nal'), 10)
}

// ======================================================
// 💾 GUARDAR TÍTULO DEL PAQUETE Y PRECIO
// ======================================================
TestObject tituloObj = new TestObject("tituloDinamico")
tituloObj.addProperty("xpath", ConditionType.EQUALS, "//div[@class='info-card__content']//div[@class='info-card__title']")
String tituloGuardado = WebUI.getText(tituloObj).trim()

TestObject mejorPrecioObj = new TestObject("mejorPrecio")
mejorPrecioObj.addProperty("xpath", ConditionType.EQUALS, "//div[@class='info-card__price']//span[@class='bestprice__amount']")
double mejorPrecio = CustomKeywords.'utils.ValidacionPrecios.validarMejorPrecioEnResultados'(mejorPrecioObj)

// ======================================================
// 🧾 PREBOOK Y VALIDACIONES
// ======================================================
WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'))
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)

// Validar edades
boolean edadesValidas = edadesCapturadasNinos.every { it in 2..17 } && edadesCapturadasInfantes.every { it in 0..1 }

if (edadesValidas) {
	CustomKeywords.'utils.PassengerFormHelper.fillPassengerData'(edadesCapturadasNinos, edadesCapturadasInfantes)
} else {
	KeywordUtil.markFailed("🚨 Edades inválidas detectadas ➜ Niños ${edadesCapturadasNinos}, Infantes ${edadesCapturadasInfantes}")
}

// ======================================================
// 🧩 VALIDACIÓN DE TÍTULOS
// ======================================================
TestObject otroTituloObj = new TestObject("otroTitulo")
otroTituloObj.addProperty("xpath", ConditionType.EQUALS, '//*[@id="main-content"]/div[2]/div/div[3]/div[1]/div/div/div/div[1]/div[2]/div[1]')
String tituloNuevo = WebUI.getText(otroTituloObj).trim()

if (tituloGuardado == tituloNuevo) {
	KeywordUtil.logInfo("✅ Los títulos coinciden correctamente")
} else {
	KeywordUtil.markWarning("⚠️ Los títulos difieren: '${tituloGuardado}' ≠ '${tituloNuevo}'")
}

// ======================================================
// ⚠️ MANEJO DE WARNING DE PRECIO
// ======================================================
TestObject warningPrecioObj = new TestObject("warningPrecio")
warningPrecioObj.addProperty("xpath", ConditionType.EQUALS, "//div[@class='booking-warning__content']")

if (WebUI.verifyElementPresent(warningPrecioObj, 5, FailureHandling.OPTIONAL)) {
	String warningText = WebUI.getText(warningPrecioObj)?.trim()
	KeywordUtil.logInfo("⚠️ Warning detectado: ${warningText}")

	def matcher = (warningText =~ /\$?\s?([\d.,]+)\s?USD/)
	def precios = matcher.collect { it[1]?.trim() }

	if (!precios.isEmpty()) {
		String nuevoPrecioStr = precios.last()
		try {
			mejorPrecio = ValidacionesPrecios.parseMoney(nuevoPrecioStr)
			KeywordUtil.logInfo("💲 Nuevo precio detectado (warning): ${mejorPrecio}")
		} catch (Exception e) {
			KeywordUtil.markWarning("⚠️ No se pudo convertir el nuevo precio: ${nuevoPrecioStr}")
		}
	}
} else {
	KeywordUtil.logInfo("✅ No se detectaron advertencias de precio")
}

// ======================================================
// 💵 VALIDACIÓN DE PRECIOS EN PREBOOK
// ======================================================
CustomKeywords.'utils.ValidacionesPrebook.validarPrecioPrebook'(
	findTestObject('Euromundo/checkout_page/precio'),
	findTestObject('Euromundo/checkout_page/comision'),
	findTestObject('Euromundo/checkout_page/precioFinal'),
	findTestObject('Euromundo/checkout_page/totalAdeudado'),
	mejorPrecio
)

// Continuar con la reserva
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_prebook'))

// ======================================================
// 🧍 DATOS DEL PASAJERO RESPONSABLE Y CONTACTO
// ======================================================
TestObject dynamicPaxSelect = new TestObject('dynamicPaxSelect')
dynamicPaxSelect.addProperty('xpath', ConditionType.EQUALS, '//select[contains(@class,\'js-set-confirm-pax-data\')]')
WebElement selectElem = WebUiCommonHelper.findWebElement(dynamicPaxSelect, 10)
new Select(selectElem).selectByIndex(1)

// Contacto
WebUI.setText(findTestObject('Euromundo/checkout_page/passport_booking_holder'), '102635')
WebUI.setText(findTestObject('Euromundo/checkout_page/holder_city'), 'Bogotá')
WebUI.setText(findTestObject('Euromundo/checkout_page/holder_zipcode'), '110111')
WebUI.setText(findTestObject('Euromundo/checkout_page/holder_address'), 'Virrey')
WebUI.setText(findTestObject('Euromundo/checkout_page/phone_booking_holder'), '3218111877')
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_billing'))
WebUI.setText(findTestObject('Euromundo/checkout_page/set_name_billing_info'), 'Johana Gomez')
WebUI.setText(findTestObject('Euromundo/checkout_page/set_email_billing_info'), 'Johana.gomez@ejuniper.com')

// Aceptar condiciones
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_fare_breakdown'))
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_TyC_checkout'))

// ======================================================
// 📋 VALIDACIÓN FINAL Y CANCELACIÓN
// ======================================================
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_book'))
WebUI.click(findTestObject('Euromundo/book_steps/button_cancel_prebook'))
WebUI.waitForAlert(10)
WebUI.acceptAlert()

TestObject alertCancel = new TestObject('alertCancel')
alertCancel.addProperty('xpath', ConditionType.EQUALS, "//p[contains(@class,'booking-details__status-text') and contains(text(),'Su reserva ha sido cancelada.')]")
WebUI.waitForElementVisible(alertCancel, 10)
WebUI.scrollToElement(findTestObject('Euromundo/book_steps/bookings'), 10)
String actualText = WebUI.getText(alertCancel)
WebUI.verifyMatch(actualText.trim(), 'Su reserva ha sido cancelada.', false)
WebUI.comment('✅ El texto de cancelación se encontró correctamente.')
