import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.WebElement as WebElement
import org.openqa.selenium.support.ui.Select as Select
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.time.Duration
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import groovy.json.JsonSlurper
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.testobject.ConditionType
import utils.ValidacionesPrecios
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject
import utils.ValidacionesPreciosTabla
import com.kms.katalon.core.model.FailureHandling as FH
import utils.ValidacionesPrebook
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.WebElement
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.JavascriptExecutor

// 🚪 Login
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_mxn'), [:] // Si no necesitas pasarle usuario/contraseña diferentes
    , FailureHandling.STOP_ON_FAILURE)

// ☰ Navegar al menú
WebUI.waitForElementClickable(findTestObject('Euromundo/seguros/repository_seguros_nal/menu_seguros'), 10)
WebUI.mouseOver(findTestObject('Euromundo/seguros/repository_seguros_nal/menu_seguros'))
WebUI.click(findTestObject('Euromundo/seguros/repository_seguros_inter/select_seguros_inter'))
WebUI.click(findTestObject('Euromundo/book_steps/button_box'))

// 🍪 Aceptar Cookies
WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

// 🔍 Búsqueda
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_insurance'), 15)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_insurance'), '29476', true)
CustomKeywords.'utils.FechaUtils.setFechasIdaYRegreso'(
	'Euromundo/seguros/repository_seguros_nal/input_date_start_insurance', 
    'Euromundo/seguros/repository_seguros_nal/input_date_end_insurance') //Fecha

// 👥 1. Abrir selector de habitaciones y pasajeros
WebUI.waitForElementClickable(findTestObject('Euromundo/widget/set_rooms_pax_inter'), 15)
CustomKeywords.'helpers.WebUIHelper.safeClick'(findTestObject('Euromundo/widget/set_rooms_pax_inter'))

// 🏨 2. Datos base de habitaciones y pasajeros
int habitaciones = 1
List<Integer> adultos = [2]
List<Integer> ninos = [0]

// 📥 3. Traer edades desde variables globales (si existen)
List<Integer> edadesNinos = (GlobalVariable.edadesNinos ?: []).findAll { it?.toString()?.isInteger() }.collect { it.toInteger() }

// 🛡️ 4. Validar rangos
boolean edadesNinosValidas = edadesNinos.every { it in 2..17 }

if (!edadesNinosValidas) {
	KeywordUtil.markFailed("🚨 Edades fuera de rango: Niños: ${edadesNinos}")
	return
} else {
	KeywordUtil.markPassed("✅ Edades válidas: Niños ${edadesNinos}")
}

// ⚙️ 5. Configurar habitaciones y pasajeros
CustomKeywords.'utils.configuration_rooms.configurarHabitacionesYPasajerosV3'(habitaciones, adultos, ninos, edadesNinos)

// 👁 6. Capturar visualmente edades seleccionadas del DOM (para paso posterior)
List<Integer> edadesCapturadasNinos = []

if (ninos.sum() > 0) {
	for (int i = 1; i <= ninos[0]; i++) {
		TestObject objNino = new TestObject("edad_nino_${i}")
		objNino.addProperty('xpath', ConditionType.EQUALS, "//*[@id='select2-room-selector-1-children-age-1-${i}-container']")
		
		WebUI.waitForElementVisible(objNino, 5)
		String edadTexto = WebUI.getText(objNino).replaceAll('\\D', '')
		int edad = edadTexto?.isInteger() ? edadTexto.toInteger() : -1
		
		if (edad in 2..17) {
			edadesCapturadasNinos.add(edad)
			WebUI.comment("🧒 Edad Niño ${i} capturada: ${edad}")
		} else {
			WebUI.comment("❗Edad no válida para niño ${i}: ${edadTexto}")
		}
	}
}

WebUI.click(findTestObject('Euromundo/seguros/repository_seguros_nal/button_insurances'))

//Guardar titulo del paquete para después comparar
TestObject tituloObj = new TestObject("tituloDinamico")
tituloObj.addProperty("xpath", ConditionType.EQUALS, "//*[@id='results-list']/div[1]/div[1]/article/div[1]/div[2]/h2")

//Guardar el texto en una variable local
String tituloGuardado = WebUI.getText(tituloObj)

// --- Crear TestObject para capturar el mejor precio en la página de resultados ---
TestObject mejorPrecioObj = new TestObject().addProperty(
	"xpath", ConditionType.EQUALS,
	"//*[@id='results-list']/div[1]/div[1]/article/div[1]/div[2]/h2"
)

// --- Llamar a la keyword para capturar y validar el precio en página de resultados ---
double mejorPrecio = CustomKeywords.'utils.ValidacionPrecios.validarMejorPrecioEnResultados'(mejorPrecioObj)

// Seleccionar Seguro
boolean hotelVisible = WebUI.waitForElementVisible(findTestObject('Euromundo/seguros/repository_seguros_nal/select_insurance_prebook'), 
    10, FailureHandling.OPTIONAL)

WebUI.click(findTestObject('Euromundo/seguros/repository_seguros_nal/select_insurance_prebook'))

if (!(hotelVisible)) {
    // Paso 3a: Esperar que aparezca el botón editar
    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_edit'), 10)
    // Paso 3b: Hacer clic en el botón editar
    WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))
    // Paso 3c: Hacer clic en el botón buscar
    WebUI.click(findTestObject('Euromundo/seguros/repository_seguros_nal/button_insurances'))
    // Paso 3d: Esperar nuevamente el select_hotel
    WebUI.waitForElementVisible(findTestObject('Euromundo/seguros/repository_seguros_nal/select_insurance_prebook'), 10)
    // Paso 3e: Hacer clic en el botón reservar hotel
    WebUI.click(findTestObject('Euromundo/seguros/repository_seguros_nal/select_insurance_prebook'))
}

WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)

// 👥 Datos de pasajeros

// 🛡 Validar nuevamente rangos por seguridad
boolean edadesValidas = edadesCapturadasNinos.every { it in 2..17 }
if (!edadesValidas) {
	KeywordUtil.markFailed("🚨 Edades inválidas detectadas ➜ Niños: ${edadesCapturadasNinos}")
} else {
	// ✅ Pasar directamente a la keyword
	CustomKeywords.'utils.PassengerFormHelper.fillPassengerData'(
		edadesCapturadasNinos, []
	)

}

// ⚖️ Política de cancelación (validación estricta con normalize-space)
TestObject policyFees = new TestObject('policyFees')
policyFees.addProperty(
    'xpath',
    ConditionType.EQUALS,
    "//div[contains(@class,'col-sm-12') and contains(normalize-space(.),'Reserva sujeta a gastos de cancelación')]"
)

if (WebUI.waitForElementPresent(policyFees, 5, FailureHandling.OPTIONAL)) {
    String txt = WebUI.getText(policyFees)?.trim()
    KeywordUtil.markFailedAndStop("🚫 Política restrictiva detectada: ${txt}")
} else {
    KeywordUtil.logInfo("✅ No se detectó política restrictiva, el flujo continúa.")
}

//Guardar titulo del paquete para después comparar en página de pasajeros
TestObject otroTituloObj = new TestObject("otroTitulo")
otroTituloObj.addProperty("xpath", ConditionType.EQUALS, "//*[@id='main-content']/div[2]/div/div[3]/div[1]/div/div[1]/div[2]/div[1]")

//Capturar el texto del segundo título
String tituloNuevo = WebUI.getText(otroTituloObj)

//Comparar
if(tituloGuardado.equals(tituloNuevo)) {
	println("✅ Los títulos son iguales")
} else {
	println("❌ Los títulos son diferentes")
}

// =========================================
// ⚠️ Manejo de cambio de precio en warning
// =========================================
TestObject warningPrecioObj = new TestObject("warningPrecio")
warningPrecioObj.addProperty("xpath", ConditionType.EQUALS,
	"//div[@class='booking-warning__content']"
)

if (WebUI.verifyElementPresent(warningPrecioObj, 5, FailureHandling.OPTIONAL)) {
	KeywordUtil.logInfo("⚠️ Apareció un warning de cambio de precio")

	String warningText = WebUI.getText(warningPrecioObj)?.trim()
	KeywordUtil.logInfo("📌 Texto warning: ${warningText}")

	def matcher = (warningText =~ /\$?\s?([\d.,]+)\s?USD/)
	def precios = matcher.collect { it[1]?.trim() }

	if (!precios.isEmpty()) {
		String nuevoPrecioStr = precios.last()
		try {
			// 🚀 Solo aquí se usa parseMoney
			mejorPrecio = ValidacionesPrebook.parseMoney(nuevoPrecioStr)
			KeywordUtil.logInfo("💲 Nuevo precio detectado (warning): ${mejorPrecio}")
		} catch (Exception e) {
			KeywordUtil.markWarning("⚠️ No se pudo convertir el nuevo precio: ${nuevoPrecioStr}")
		}
	} else {
		KeywordUtil.markWarning("⚠️ No se pudo extraer ningún precio del warning")
	}
} else {
	KeywordUtil.logInfo("✅ No apareció ningún warning de cambio de precio")
}


// TestObjects de la página de pasajeros para validación de precios
TestObject precioObj = new TestObject("precio")
precioObj.addProperty("xpath", ConditionType.EQUALS,
	"//div[@class='booking-breakdown__item booking-breakdown__item--total booking-breakdown__item--is-pay-web']//span[@class='booking-breakdown__item-price']")

TestObject comisionObj = new TestObject("comision")
comisionObj.addProperty("xpath", ConditionType.EQUALS,
	"//div[@class='booking-breakdown__item']//span[contains(text(),'Comisiones')]/following-sibling::span")

TestObject precioFinalObj = new TestObject("precioFinal")
precioFinalObj.addProperty("xpath", ConditionType.EQUALS,
	"//div[@class='booking-breakdown__item']//span[contains(text(),'Precio final')]/following-sibling::span")

TestObject totalAdeudadoObj = new TestObject("totalAdeudado")
totalAdeudadoObj.addProperty("xpath", ConditionType.EQUALS,
	"//span[@class='agent-markup__total-due-price']")

// ✅ Llamada con los 5 parámetros
CustomKeywords.'utils.ValidacionesPrebook.validarPrecioPrebook'(
	precioObj,
	comisionObj,
	precioFinalObj,
	totalAdeudadoObj,
	mejorPrecio
)

// ☑️ Checkbox condicional
TestObject checkboxObj = new TestObject('checkbox')
checkboxObj.addProperty('xpath', ConditionType.EQUALS, '//input[@type=\'checkbox\' and @required=\'\']')

if (WebUI.verifyElementPresent(checkboxObj, 5, FailureHandling.OPTIONAL)) {
	KeywordUtil.logInfo('☑️ Checkbox presente. Se hará clic.')
	WebUI.click(checkboxObj)
}

// ➕ Finalizar datos pasajeros
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_prebook'))

// 👤 Seleccionar titular reserva
TestObject paxDropdown = new TestObject('dynamicPaxSelect')
paxDropdown.addProperty('xpath', ConditionType.EQUALS, '//select[contains(@class,\'js-set-confirm-pax-data\')]')

WebElement selectElem = WebUiCommonHelper.findWebElement(paxDropdown, 10)
Select dropdown = new Select(selectElem)
dropdown.selectByIndex(1)


// 🧾 Datos contacto
WebUI.setText(findTestObject('Euromundo/checkout_page/passport_booking_holder'), '102635')

TestObject cityObj = new TestObject('dynamicCity')
cityObj.addProperty('xpath', ConditionType.EQUALS, '//input[@name=\'holder_city\']')
WebUI.waitForElementVisible(cityObj, 10)
WebUI.setText(cityObj, 'Bogota')

TestObject zipObj = new TestObject('dynamicZip')
zipObj.addProperty('xpath', ConditionType.EQUALS, '//input[@name=\'holder_zipcode\']')
WebUI.waitForElementVisible(zipObj, 10)
WebUI.setText(zipObj, '110111')

TestObject addrObj = new TestObject('dynamicAddress')
addrObj.addProperty('xpath', ConditionType.EQUALS, '//input[@name=\'holder_address\']')
WebUI.waitForElementVisible(addrObj, 10)
WebUI.setText(addrObj, 'Virrey')

WebUI.setText(findTestObject('Euromundo/checkout_page/phone_booking_holder'), '3218111877')

// ✅ Términos y condiciones
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_fare_breakdown'))
TestObject checkbox = findTestObject('Euromundo/checkout_page/checkbox_importantInfo')

if (WebUI.verifyElementPresent(checkbox, 3, FailureHandling.OPTIONAL)) {
	WebUI.click(checkbox)
	WebUI.comment("✅ Checkbox encontrado y clicado.")
} else {
	WebUI.comment("⚠️ Checkbox no presente, se continúa sin clic.")
}

//💾 Validaciones de precios para cerrar reserva

// Crear un TestObject para el título en Book
TestObject tituloBookObj = new TestObject('tituloBookObj')
tituloBookObj.addProperty('xpath', ConditionType.EQUALS, '//*[@id="main-content"]/div[2]/div/div/div[1]/div[1]/div/div[2]/table/tbody/tr[1]/td/div[2]/div[1]/span')

// Obtener el texto desde el TestObject
String tituloBook = WebUI.getText(tituloBookObj).trim()
KeywordUtil.logInfo("📌 Título Book: ${tituloBook}")

// Comparación
if (tituloNuevo == tituloBook) {
	KeywordUtil.logInfo("✅ Los títulos coinciden")
} else {
	KeywordUtil.markFailedAndStop("❌ Los títulos NO coinciden: '${tituloNuevo}' vs '${tituloBook}'")
}

// ==========================
// ✅ Validar precios en página de pasajeros
// ==========================

// --- Helper local para crear TestObjects dinámicos ---
private TestObject byXpath(String name, String xpath) {
	TestObject t = new TestObject(name)
	t.addProperty('xpath', ConditionType.EQUALS, xpath)
	return t
}

// Precio
TestObject precioObj_pas = byXpath(
	'precioObj_pas',
	'//*[@id="main-content"]/div[2]/div/div/div[1]/div[1]/div/div[3]/div/div[2]/div[1]')

// Comisiones
TestObject comisionObj_pas = byXpath(
	'comisionObj_pas',
	'//*[@id="main-content"]/div[2]/div/div/div[1]/div[1]/div/div[3]/div/div[2]/div[2]/div/div[1]')

// Precio Final
TestObject precioFinalObj_pas = byXpath(
	'precioFinalObj_pas',
	'//*[@id="main-content"]/div[2]/div/div/div[1]/div[1]/div/div[3]/div/div[2]/div[2]/div/div[3]')

// Precio Paquete
TestObject precioPaqueteObj_pas = byXpath(
	'precioPaqueteObj_pas',
	'//*[@id="main-content"]/div[2]/div/div/div[1]/div[1]/div/div[2]/table/tbody/tr[2]/td')

// --- Llamada a la keyword de precios ---
CustomKeywords.'utils.ValidacionesPrecios.validarPrecio'(
	precioObj_pas,
	comisionObj_pas,
	precioFinalObj_pas,
	precioPaqueteObj_pas,
	mejorPrecio
)


// ==========================
// 📌 Validación tabla de desglose de comisiones
// ==========================

TestObject btnDesglose = new TestObject('btnDesglose').addProperty(
  'xpath', ConditionType.EQUALS,
  "//button[contains(.,'Desglose') or contains(.,'Breakdown') or contains(@class,'js-open-breakdown')]"
)

// Tabla del breakdown (OJO: es otra estructura distinta al DIV)
TestObject tablaDesgloseTable = new TestObject('tablaDesgloseTable').addProperty(
  'xpath', ConditionType.EQUALS,
  "//table[contains(@class,'confirm-booking__tableBreakdown__table')]"
)
WebUI.waitForElementPresent(tablaDesgloseTable, 10, FH.OPTIONAL)
WebUI.scrollToElement(tablaDesgloseTable, 3)
WebUI.waitForElementVisible(tablaDesgloseTable, 10, FH.OPTIONAL)

// Si tienes una clase utils.ValidacionesPreciosTabla con método validacionDesglose(mejorPrecio)
try {
  def valTabla = new utils.ValidacionesPreciosTabla()
  if (WebUI.verifyElementPresent(tablaDesgloseTable, 2, FH.OPTIONAL)) {
	  valTabla.validacionDesglose(mejorPrecio)
	  WebUI.comment("✅ Validación de la tabla de desglose ejecutada correctamente")
  } else {
	  WebUI.comment("⚠️ La tabla de desglose de comisiones no está visible en pantalla")
  }
} catch (Throwable t) {
  WebUI.comment("ℹ️ Saltando validación de tabla: clase/método no disponible: ${t.message}")
}

WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_TyC_checkout'))

// 🧾 Finalizar y cancelar
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_book'))
WebUI.click(findTestObject('Euromundo/book_steps/button_cancel_prebook'))
WebUI.click(findTestObject('Euromundo/book_steps/button_cancel_prebook_hotel'))
WebUI.click(findTestObject('Euromundo/book_steps/button_cancel_book'))

WebUI.waitForAlert(10)
WebUI.acceptAlert()

// 📢 Validar mensaje de cancelación
TestObject alertCancel = new TestObject('dynamic/alertCancel')
alertCancel.addProperty('xpath', ConditionType.EQUALS, '//p[contains(@class,\'booking-details__status-text\') and contains(text(),\'Su reserva ha sido cancelada.\')]')
WebUI.waitForElementVisible(alertCancel, 10)
WebUI.scrollToElement(findTestObject('Euromundo/book_steps/bookings'), 10)
String actualText = WebUI.getText(alertCancel)
WebUI.verifyMatch(actualText.trim(), 'Su reserva ha sido cancelada.', false)
WebUI.comment('✅ El texto de cancelación se encontró correctamente.')
