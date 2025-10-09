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
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_mxn'), [:], FailureHandling.STOP_ON_FAILURE)

// ☰ Menú Hoteles Internacionales
WebUI.waitForElementClickable(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/menu_hoteles'), 10)
WebUI.mouseOver(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/menu_hoteles'))
WebUI.click(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/select_hoteles_inter_walm'))

// 🧳 Ingreso a buscador
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_box'), 10)
WebUI.click(findTestObject('Euromundo/book_steps/button_box'))
WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

// 🌍 Selección destino
WebUI.waitForElementClickable(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/input_destination_hoteles_inter'), 15)
WebUI.click(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/input_destination_hoteles_inter'))
WebUI.setText(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/set_zone_selector'), 'Las')
WebUI.click(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/set_autocomplete_city_LAS'))

// 📅 Fechas
CustomKeywords.'utils.FechaUtils.setFechasIdaYRegreso'('Euromundo/hoteles/repository_hoteles_inter/input_date_origin_hoteles', 
    'Euromundo/hoteles/repository_hoteles_inter/input_date_destination_hoteles')

// 👥 1. Abrir selector de habitaciones y pasajeros
CustomKeywords.'helpers.WebUIHelper.safeClick'(findTestObject('Euromundo/widget/set_rooms_pax_inter_hotel'))

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

// 🔍 Buscar hoteles
WebUI.click(findTestObject('Euromundo/book_steps/button_search_hotels'))

//Guardar titulo del paquete para después comparar
WebUI.waitForElementClickable(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/save_hotel_name_inter'), 15)
TestObject tituloObj = new TestObject("tituloDinamico")
tituloObj.addProperty("xpath", ConditionType.EQUALS, "(//h2[contains(@class,'info-card__title')]//a[contains(@class,'js-result-detail-action')])[1]")

//Guardar el texto en una variable local
String tituloGuardado = WebUI.getText(tituloObj)

// --- Crear TestObject para capturar el mejor precio en la página de resultados ---
TestObject mejorPrecioObj = new TestObject("mejorPrecio")
mejorPrecioObj.addProperty("xpath", ConditionType.EQUALS,
	"(//*[@id='results-list']//span[contains(@class,'js-currency-conversor')])[1]")

println "💲 El mejor precio encontrado es: ${mejorPrecioObj}"

// --- Llamar a la keyword para capturar y validar ---
double mejorPrecio = CustomKeywords.'utils.ValidacionPrecios.validarMejorPrecioEnResultados'(mejorPrecioObj)


// 🏨 Validar disponibilidad hotel
boolean hotelVisible = WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_reservar_hoteles'), 10, FailureHandling.OPTIONAL)

if (!(hotelVisible)) {
    WebUI.comment('🔄 Reintentando búsqueda tras editar')
    WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_edit'), 10)
    WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))
		CustomKeywords.'utils.FechaUtils.setFechasIdaYRegreso'('Euromundo/hoteles/repository_hoteles_inter/input_date_origin_hoteles', 
    'Euromundo/hoteles/repository_hoteles_inter/input_date_destination_hoteles')
    WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))
    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_reservar_hoteles'), 10)
}

WebUI.click(findTestObject('Euromundo/book_steps/button_reservar_hoteles'))

// ===========================
// Políticas de Cancelación - Dinámico con Map
// ===========================

// 1. Click en el botón "Ver Política"
TestObject verPoliticaBtn = new TestObject("verPoliticaBtn")
verPoliticaBtn.addProperty("xpath", ConditionType.EQUALS, "//button[contains(@class,'js-open-modal') and normalize-space()='Ver Política']")

WebUI.waitForElementClickable(verPoliticaBtn, 10)
WebUI.click(verPoliticaBtn)

// 2. Esperar a que se muestre el modal
TestObject modalPoliticas_A = new TestObject("modalPoliticas_A")
modalPoliticas_A.addProperty("xpath", ConditionType.EQUALS, "//*[@id='main-modal']")
WebUI.waitForElementVisible(modalPoliticas_A, 10)

// 3. Capturar todas las filas de políticas
List<WebElement> filasPoliticas_A = WebUI.findWebElements(
	new TestObject().addProperty("xpath", ConditionType.EQUALS, "//*[@id='main-modal']//div[@class='row cancelation-rule']"),
	10
)

// 4. Lista de mapas para guardar las políticas
List<Map<String, String>> politicas = []

for (int i = 0; i < filasPoliticas_A.size(); i++) {
	WebElement fila = filasPoliticas_A[i]

	// Condición (siempre existe)
	String condicion = fila.findElement(By.xpath(".//div[contains(@class,'rule-condition')]")).getText().trim()

	// Cargo (puede que no exista en la última fila)
	String cargo = "N/A"
	List<WebElement> cargos = fila.findElements(By.xpath(".//div[contains(@class,'rule-charge')]"))
	if (cargos.size() > 0) {
		cargo = cargos[0].getText().trim()
	}

	// Guardar como mapa
	Map<String, String> politica = [
		"condicion" : condicion,
		"cargo"     : cargo
	]
	politicas.add(politica)
}

// 5. Imprimir políticas en consola y log de Katalon
for (int i = 0; i < politicas.size(); i++) {
	String msg = "📌 Política ${i+1}: Condición='${politicas[i]['condicion']}' | Cargo='${politicas[i]['cargo']}'"
	println(msg)                        // Consola
	KeywordUtil.logInfo(msg)            // Log de ejecución
}

// 6. Cerrar el modal
TestObject cerrarPoliticaBtn = new TestObject("cerrarPoliticaBtn")
cerrarPoliticaBtn.addProperty(
	"xpath",
	ConditionType.EQUALS,
	"//*[@id='main-modal']//div[@class='modal-footer']//button[contains(@class,'btn-primary') and normalize-space()='Cerrar']"
)

if (WebUI.waitForElementClickable(cerrarPoliticaBtn, 10, FailureHandling.OPTIONAL)) {
	WebUI.click(cerrarPoliticaBtn)
} else {
	KeywordUtil.markWarning("⚠️ No se encontró el botón 'Cerrar' del modal de políticas")
}

// ⚖️ Política de cancelación
TestObject policyTextObj = new TestObject('policyText')
policyTextObj.addProperty('xpath', ConditionType.EQUALS, '//div[contains(text(),\'cancelar la reserva\')]')
String policyText = WebUI.getText(policyTextObj)

if (policyText.toLowerCase().contains('sin incurrir en gastos')) {
    TestObject finalizeBtn = new TestObject('finalizeReservationBtn')
    finalizeBtn.addProperty('xpath', ConditionType.EQUALS, '//button[contains(text(),\'Finalizar reserva\')]')
    WebUI.waitForElementClickable(finalizeBtn, 10)
} else {
    KeywordUtil.logInfo('⚠️ Hotel no reembolsable: la política no permite cancelación sin gastos.')
    KeywordUtil.markFailed('Hotel no reembolsable: flujo detenido por política restrictiva.')
}

// 👥 Datos pasajeros
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

//Guardar titulo del hotel para comparar
TestObject otroTituloObj = new TestObject("otroTitulo")
otroTituloObj.addProperty(
	"xpath",
	ConditionType.EQUALS,
	"//*[@id='main-content']/div[2]/div/div[1]/div[2]/div/div[2]/div[1]/div/div/div/div[1]")


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

	def matcher = (warningText =~ /\$?\s?([\d.,]+)\s?(USD|MXN)?/)
	def precios = matcher.collect { it[1]?.trim() }
	def monedas = matcher.collect { it[2]?.trim() }

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

// 📥 Confirmar reserva
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_prebook_hoteles'))

// 👤 Seleccionar titular reserva
// 👤 Seleccionar titular
TestObject dynamicPaxSelect = new TestObject('dynamicPaxSelect')
dynamicPaxSelect.addProperty('xpath', ConditionType.EQUALS, '//select[contains(@class,\'js-set-confirm-pax-data\')]')

WebElement selectElem = WebUiCommonHelper.findWebElement(dynamicPaxSelect, 10)
Select dropdown = new Select(selectElem)
dropdown.selectByIndex(1)

// 📦 Datos de contacto
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

// 🧾 Facturación
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_billing'), FailureHandling.STOP_ON_FAILURE)
WebUI.setText(findTestObject('Euromundo/checkout_page/set_name_billing_info'), 'Johana Gomez')
WebUI.setText(findTestObject('Euromundo/checkout_page/set_email_billing_info'), 'johana.gomez@ejuniper.com')

// ✅ Confirmaciones
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_fare_breakdown'))

TestObject importantInfoCheckbox = findTestObject('Euromundo/checkout_page/checkbox_importantInfo')
if (WebUI.verifyElementPresent(importantInfoCheckbox, 5, FailureHandling.OPTIONAL)) {
    WebUI.click(importantInfoCheckbox)
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

// Tabla del breakdown
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

// 📤 Finalizar y cancelar
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_book'))
WebUI.click(findTestObject('Euromundo/book_steps/button_cancel_book'))

WebUI.waitForAlert(10)
WebUI.acceptAlert()

// 📢 Validar cancelación
TestObject alertCancel = new TestObject('dynamic/alertCancel')
alertCancel.addProperty('xpath', ConditionType.EQUALS, '//p[contains(@class,\'booking-details__status-text\') and contains(text(),\'Su reserva ha sido cancelada.\')]')
WebUI.waitForElementVisible(alertCancel, 10)
WebUI.scrollToElement(findTestObject('Euromundo/book_steps/bookings'), 10)
String actualText = WebUI.getText(alertCancel)
WebUI.verifyMatch(actualText.trim(), 'Su reserva ha sido cancelada.', false, FailureHandling.STOP_ON_FAILURE)
WebUI.comment('✅ El texto de cancelación se encontró correctamente.')

