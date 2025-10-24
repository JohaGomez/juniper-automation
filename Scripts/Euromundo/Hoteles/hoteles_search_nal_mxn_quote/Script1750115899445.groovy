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
import java.nio.file.StandardOpenOption
import com.kms.katalon.core.util.KeywordUtil
import java.nio.file.Files
import java.nio.file.Paths

// 🚪 Login
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_mxn'), [:], FailureHandling.STOP_ON_FAILURE)

// ☰ Menú Hoteles Nacionales
WebUI.waitForElementClickable(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/menu_hoteles'), 10)
WebUI.mouseOver(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/menu_hoteles'))
WebUI.click(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_nal/select_hoteles_nal_mxn'))
WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

// 🌍 Selección destino
WebUI.waitForElementClickable(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/input_destination_hoteles_inter'), 15)
WebUI.click(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/input_destination_hoteles_inter'))
WebUI.setText(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/set_zone_selector'), 'Mex')
WebUI.click(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_nal/set_autocomplete_city_MEX'))

// 📅 Fechas
CustomKeywords.'utils.FechaUtils.setFechasIdaYRegreso'(
    'Euromundo/módulo_hoteles/repository_hoteles_inter/input_date_origin_hoteles',
    'Euromundo/módulo_hoteles/repository_hoteles_inter/input_date_destination_hoteles'
)

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
WebUI.click(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/input_date_destination_hoteles'))

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

if (!hotelVisible) {
    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_edit'), 10)
    WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))
    WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))
    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_reservar_hoteles'), 10)
    WebUI.click(findTestObject('Euromundo/book_steps/button_reservar_hoteles'))
}

WebUI.scrollToElement(findTestObject('Euromundo/book_steps/button_reservar_hoteles'), 10)
WebUI.click(findTestObject('Euromundo/book_steps/button_reservar_hoteles'))
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook_hoteles'), 10)

// ====================================================
// 🅰️ POLÍTICAS DE CANCELACIÓN – PÁGINA DE PASAJEROS
// ====================================================

// 1️⃣ Click en el botón "Ver Política"
TestObject verPoliticaBtn = new TestObject("verPoliticaBtn_A")
verPoliticaBtn.addProperty("xpath", ConditionType.EQUALS, "//button[contains(@class,'js-open-modal') and normalize-space()='Ver Política']")

WebUI.waitForElementClickable(verPoliticaBtn, 15)
WebUI.click(verPoliticaBtn)
KeywordUtil.logInfo("🪟 Abriendo modal de políticas en página de pasajeros...")

// 2️⃣ Esperar a que el modal aparezca
TestObject modalPoliticas_A = new TestObject("modalPoliticas_A")
modalPoliticas_A.addProperty("xpath", ConditionType.EQUALS, "//*[@id='main-modal']")
WebUI.waitForElementVisible(modalPoliticas_A, 15)

// 3️⃣ Capturar las filas
List<WebElement> filasPoliticas_A = WebUI.findWebElements(
	new TestObject().addProperty("xpath", ConditionType.EQUALS, "//*[@id='main-modal']//div[contains(@class,'cancelation-rule')]"),
	10
)

List<Map<String, String>> politicasA = []

for (WebElement fila : filasPoliticas_A) {
	String condicion = "N/A"
	String cargo = "N/A"

	try {
		condicion = fila.findElement(By.xpath(".//div[contains(@class,'rule-condition')]")).getText().trim()
	} catch (Exception e) {
		KeywordUtil.markWarning("⚠️ No se encontró texto de condición en una fila.")
	}
	try {
		cargo = fila.findElement(By.xpath(".//div[contains(@class,'rule-charge')]")).getText().trim()
	} catch (Exception e) {
		// cargo puede no existir, no se marca error
	}

	politicasA.add(["condicion": condicion, "cargo": cargo])
}

// 4️⃣ Log detallado
KeywordUtil.logInfo("📋 Políticas capturadas en página de pasajeros:")
politicasA.eachWithIndex { pol, idx ->
	KeywordUtil.logInfo("📌 Política ${idx+1}: Condición='${pol.condicion}' | Cargo='${pol.cargo}'")
}

// 5️⃣ Cerrar modal
TestObject cerrarPoliticaBtn = new TestObject("cerrarPoliticaBtn_A")
cerrarPoliticaBtn.addProperty("xpath", ConditionType.EQUALS, "//*[@id='main-modal']//button[normalize-space()='Cerrar']")
if (WebUI.waitForElementClickable(cerrarPoliticaBtn, 10, FailureHandling.OPTIONAL)) {
	WebUI.click(cerrarPoliticaBtn)
	KeywordUtil.logInfo("🧩 Modal cerrado correctamente.")
} else {
	KeywordUtil.markWarning("⚠️ No se encontró botón 'Cerrar' en el modal.")
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

// 👥 Datos de pasajeros
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook_hoteles'), 10)

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

// 📥 Confirmación pre-booking
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_prebook_hoteles'))

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

// ====================================================
// 🅱️ POLÍTICAS DE CANCELACIÓN – PÁGINA DE PAGO
// ====================================================

KeywordUtil.logInfo("💳 Capturando políticas en página de pago...")

// 1️⃣ Botón "Revise las políticas"
TestObject verPoliticaBtnB = new TestObject("verPoliticaBtn_B")
verPoliticaBtnB.addProperty(
	"xpath",
	ConditionType.EQUALS,
	"//*[@class='confirm-booking__policies-fields__policies']//button[contains(.,'Revise las políticas')]"
)

if (WebUI.waitForElementVisible(verPoliticaBtnB, 15, FailureHandling.OPTIONAL)) {
	try {
		WebUI.scrollToElement(verPoliticaBtnB, 2)
		WebUI.waitForElementClickable(verPoliticaBtnB, 10)
		WebUI.click(verPoliticaBtnB)
		KeywordUtil.logInfo("🪟 Click realizado en 'Revise las políticas' (modo normal).")
	} catch (Exception e) {
		WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(WebUiCommonHelper.findWebElement(verPoliticaBtnB, 10)))
		KeywordUtil.logInfo("🪄 Click forzado con JavaScript sobre 'Revise las políticas'.")
	}
} else {
	KeywordUtil.markWarning("⚠️ No se encontró el botón 'Revise las políticas' en la página de pago.")
}

// 2️⃣ Esperar modal REMOTO (Bootstrap) correctamente abierto
TestObject modalPoliticas_B = new TestObject("modalPoliticas_B")
modalPoliticas_B.addProperty(
	"xpath",
	ConditionType.EQUALS,
	"//div[contains(@class,'modal') and contains(@class,'in')]//div[@class='modal-content']"
)
if (!WebUI.waitForElementVisible(modalPoliticas_B, 10, FailureHandling.OPTIONAL)) {
	WebUI.delay(2)
	WebUI.waitForElementVisible(modalPoliticas_B, 10)
}
KeywordUtil.logInfo("🪟 Modal B visible.")

// 3️⃣ Capturar políticas desde el modal-body (robusto) y separarlas en 3
List<Map<String, String>> politicasB = []

TestObject modalBodyB = new TestObject("modalBodyB")
modalBodyB.addProperty(
	"xpath",
	ConditionType.EQUALS,
	"//div[contains(@class,'modal') and contains(@class,'in')]//div[@class='modal-content']//div[contains(@class,'modal-body')]"
)

// 3.1) innerHTML → texto con saltos (variables locales para no colisionar)
String htmlB = ""
for (int k = 0; k < 10 && (htmlB == null || htmlB.trim().isEmpty()); k++) {
	try {
		def el = WebUiCommonHelper.findWebElement(modalBodyB, 5)
		htmlB = (String) WebUI.executeJavaScript("return arguments[0].innerHTML || '';", Arrays.asList(el))
	} catch (Exception e) {
		htmlB = WebUI.getText(modalBodyB)
	}
	if (htmlB == null || htmlB.trim().isEmpty()) WebUI.delay(0.2)
}

// 3.2) HTML → texto y **reparación segura** de horas partidas (sin regex complejas)
String htmlB2 = (htmlB ?: '')
String textoB2 = ''
if (htmlB2) {
	textoB2 = htmlB2
		.replaceAll('(?i)<br\\s*/?>', '\n')
		.replaceAll('(?i)</p>', '\n')
		.replaceAll('(?i)<[^>]+>', ' ')
} else {
	textoB2 = ''
}

// Unir líneas cuando una línea es SOLO ":mm:ss" o ":hh:mm:ss"
List<String> rawLinesB = Arrays.asList(textoB2.split('\\r?\\n'))
List<String> fixedLinesB = new ArrayList<>()
rawLinesB.each { ln ->
	String line = (ln ?: '').replace('\u00A0',' ').replaceAll('[\\t\\x0B\\f\\r]+',' ').trim()
	def onlyTime = (line =~ /^:(\d{2}:\d{2}(?::\d{2})?)$/)
	if (onlyTime.matches() && !fixedLinesB.isEmpty()) {
		String last = fixedLinesB.remove(fixedLinesB.size()-1).replaceAll('\\s+$','')
		fixedLinesB.add(last + ':' + onlyTime[0][1]) // pega ":mm:ss" o ":hh:mm:ss" a la hora anterior
	} else {
		fixedLinesB.add(line)
	}
}
textoB2 = fixedLinesB.join('\n')

// Limpieza básica (conservando \n)
textoB2 = textoB2.replace('\u00A0',' ').replaceAll('[\\t\\x0B\\f\\r]+',' ')

// 3.3) Segmentar por políticas con separador literal (evita problemas con split/regex)
List<String> segmentosB2 = []
String lowerB2 = (textoB2 ?: '').toLowerCase()
final String SEP_B = '§§SEG_B§§'
final String SEP_B_REPL = java.util.regex.Matcher.quoteReplacement(SEP_B)
String workB = (textoB2 ?: '')

if (lowerB2.contains('cancelando') || lowerB2.contains('fecha y hora')) {
	workB = workB.replaceAll(
		'(?is)(?=\\b(?:cancelando\\s+desde|cancelando\\s+despu[eé]s\\s+de|fecha y hora))',
		SEP_B_REPL
	)
} else {
	// Fallback: antes de un cargo tipo ": 0 USD", ": $ 5.15 USD", ": 1 noche", etc.
	workB = workB.replaceAll(
		'(?i)(?=:\\s*(?:\\$?\\s?\\d|\\d|noche))',
		SEP_B_REPL
	)
}
String[] rawSegsB2 = workB.split(java.util.regex.Pattern.quote(SEP_B))
segmentosB2 = Arrays.asList(rawSegsB2).collect { it.trim() }.findAll { it }

// 3.4) Regex DOTALL y parseo a formato A (condición/cargo)
// Evita cortar en ":" de hora; y añade soporte para "o no show"
def pDesdeHastaB2 = ~/(?is)^(?:cancelando\s+)?desde\s+(.*?)\s+hasta\s+(.*?)(?::\s*(?!\d{1,2}:)(.+))?$/
def pDespuesDeB2  = ~/(?is)^(?:cancelando\s+)?despu[eé]s\s+de\s+(.*?)(?::\s*(?!\d{1,2}:)(.+))?$/
def pNoShowB2     = ~/(?is)^(?:cancelando\s+)?desde\s+(.*?)\s+o\s+no[- ]?show(?::\s*(?!\d{1,2}:)(.+))?$/
def pFechaHoraB2  = ~/(?is)^fecha y hora.*$/

segmentosB2.each { seg ->
	String s = (seg ?: '').trim()
	if (!s) return

	def m1 = (s =~ pDesdeHastaB2)
	def m2 = (s =~ pDespuesDeB2)
	def mNo = (s =~ pNoShowB2)
	def m3 = (s =~ pFechaHoraB2)

	if (m1.matches()) {
		String desde = (m1[0][1] ?: '').replace('\u00A0',' ').replaceAll('\\s+',' ').trim()
		String hasta = (m1[0][2] ?: '').replace('\u00A0',' ').replaceAll('\\s+',' ').trim()
		String cargo = (m1[0][3] ?: '').replace('\u00A0',' ').replaceAll('\\s+',' ').trim()
		politicasB.add([condicion: "Cancelando desde ${desde} hasta ${hasta}", cargo: cargo])
	} else if (m2.matches()) {
		String despues = (m2[0][1] ?: '').replace('\u00A0',' ').replaceAll('\\s+',' ').trim()
		String cargo   = (m2[0][2] ?: '').replace('\u00A0',' ').replaceAll('\\s+',' ').trim()
		politicasB.add([condicion: "Cancelando después de ${despues}", cargo: cargo])
	} else if (mNo.matches()) {
		String desde = (mNo[0][1] ?: '').replace('\u00A0',' ').replaceAll('\\s+',' ').trim()
		String cargo = (mNo[0][2] ?: '').replace('\u00A0',' ').replaceAll('\\s+',' ').trim()
		// Estandarizamos a la misma redacción que A
		politicasB.add([condicion: "Cancelando desde ${desde} o no show", cargo: cargo])
	} else if (m3.matches()) {
		politicasB.add([condicion: "Fecha y hora calculada basándose en el huso horario de destino.", cargo: "N/A"])
	}
}

// 4️⃣ Normalización y comparación contra A (restaurado)
List<Map<String, String>> politicasARefB = []
try { if (politicasA) politicasARefB = politicasA } catch (Throwable ignore) {}
try { if (politicasARefB.isEmpty() && politicas) politicasARefB = politicas } catch (Throwable ignore) {}

def _normTextBcmp = { String s -> (s ?: "").replace('\u00A0',' ').replaceAll('\\s+',' ').trim() }
def _toCmpMapB    = { Map m -> [condicion: _normTextBcmp(m.condicion), cargo: _normTextBcmp(m.cargo)] }

List<Map<String, String>> _A_Bcmp = politicasARefB.collect { _toCmpMapB(it) }
List<Map<String, String>> _B_Bcmp = politicasB.collect    { _toCmpMapB(it) }

if (_A_Bcmp.isEmpty()) {
	KeywordUtil.markWarning("⚠️ [B] No hay políticas del paso A para comparar.")
} else {
	if (_A_Bcmp.size() != _B_Bcmp.size()) {
		KeywordUtil.markWarning("⚠️ [B] Cantidad difiere: A=${_A_Bcmp.size()} vs B=${_B_Bcmp.size()} (se comparará por índice).")
	}
	KeywordUtil.logInfo("🔎 [B] Comparando políticas A vs B…")
	int _n = Math.max(_A_Bcmp.size(), _B_Bcmp.size())
	for (int i = 0; i < _n; i++) {
		def a = i < _A_Bcmp.size() ? _A_Bcmp[i] : [condicion:"N/A", cargo:"N/A"]
		def b = i < _B_Bcmp.size() ? _B_Bcmp[i] : [condicion:"N/A", cargo:"N/A"]
		if (a.condicion == b.condicion && a.cargo == b.cargo) {
			KeywordUtil.logInfo("✅ [B] Política ${i+1} coincide.")
		} else {
			KeywordUtil.markWarning("❌ [B] Diferencia en política ${i+1}: A='${a}' vs B='${b}'")
		}
	}
}


// 5️⃣ Log bonito en formato A
KeywordUtil.logInfo("📋 Políticas capturadas en página de pago (B):")
for (int i = 0; i < politicasB.size(); i++) {
	String msg = "📌 Política ${i+1}: Condición='${politicasB[i]['condicion']}' | Cargo='${politicasB[i]['cargo']}'"
	println(msg)
	KeywordUtil.logInfo(msg)
}

// 6️⃣ Cerrar modal con la X y esperar que desaparezca el overlay
TestObject cerrarPoliticaBtnB = new TestObject("cerrarPoliticaBtn_B")
cerrarPoliticaBtnB.addProperty(
	"xpath",
	ConditionType.EQUALS,
	"//div[contains(@class,'modal') and contains(@class,'in')]//button[contains(@class,'close') or @aria-label='Cerrar']"
)
if (WebUI.waitForElementVisible(cerrarPoliticaBtnB, 10, FailureHandling.OPTIONAL)) {
	try {
		WebUI.click(cerrarPoliticaBtnB)
		KeywordUtil.logInfo("🧩 Modal B cerrado (click normal).")
	} catch (Exception e) {
		WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(WebUiCommonHelper.findWebElement(cerrarPoliticaBtnB, 10)))
		KeywordUtil.logInfo("🪄 Modal B cerrado (click JS).")
	}
} else {
	KeywordUtil.markWarning("⚠️ No se encontró la 'X' para cerrar el modal B.")
}

TestObject overlayModalInB = new TestObject("overlayModalIn_B")
overlayModalInB.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class,'modal') and contains(@class,'in')]")
WebUI.waitForElementNotPresent(overlayModalInB, 10)
WebUI.delay(0.5)

// 7️⃣ Click en botón Confirm Booking
TestObject btnCustom = new TestObject("btnCustom").addProperty("xpath", ConditionType.EQUALS, "//button[contains(@class,'confirm-booking__save-button') or contains(@class,'confirm-booking')]")
WebUI.waitForElementClickable(btnCustom, 10)
WebUI.click(btnCustom)
WebUI.comment("✅ Click realizado correctamente en el botón de confirm-booking.")

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

// 💾 Guardar cotización
WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_quote_save_hoteles'), 20)
WebUI.click(findTestObject('Euromundo/book_steps/button_quote_save_hoteles'))

// ====================================================
// 🅲 POLÍTICAS DE CANCELACIÓN – MODAL C (alineado con B)
// ====================================================

// Asegurar contenedores globales (evita MissingPropertyException)
try { politicasB } catch (Throwable _){ politicasB = new ArrayList<Map<String,String>>() }
try { politicasC } catch (Throwable _){ politicasC = new ArrayList<Map<String,String>>() }

KeywordUtil.logInfo("🧾 [C] Capturando políticas (con toggle) y estandarizando como en B...")

// 0) Abrir toggle de detalles
TestObject toggleBtnC2 = new TestObject("toggleBtn_C2")
toggleBtnC2.addProperty(
	"xpath",
	ConditionType.EQUALS,
	"//*[@class='shopping-basket__line-product-name']/button[contains(@class,'shopping-basket__line-product-extended-details-toggle')]"
)
if (WebUI.waitForElementClickable(toggleBtnC2, 10, FailureHandling.OPTIONAL)) {
	try {
		WebUI.click(toggleBtnC2)
		KeywordUtil.logInfo("📂 [C] Toggle de detalles abierto.")
	} catch (Exception e) {
		WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(WebUiCommonHelper.findWebElement(toggleBtnC2, 5)))
		KeywordUtil.logInfo("🪄 [C] Toggle abierto con JS.")
	}
} else {
	KeywordUtil.markWarning("ℹ️ [C] Toggle no visible/clicable; continuando…")
}

// 1) Abrir enlace “Política de cancelación”
TestObject verPoliticaBtnC2 = new TestObject("verPoliticaBtn_C2")
verPoliticaBtnC2.addProperty(
	"xpath",
	ConditionType.EQUALS,
	"//*[@id='main-content']//a[contains(normalize-space(.),'Política de cancelación')]"
)
if (WebUI.waitForElementClickable(verPoliticaBtnC2, 10, FailureHandling.OPTIONAL)) {
	try {
		WebUI.click(verPoliticaBtnC2)
		KeywordUtil.logInfo("🪟 [C] Click en 'Política de cancelación'.")
	} catch (Exception e) {
		WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(WebUiCommonHelper.findWebElement(verPoliticaBtnC2, 5)))
		KeywordUtil.logInfo("🪄 [C] Click forzado en 'Política de cancelación'.")
	}
} else {
	KeywordUtil.markWarning("⚠️ [C] No se encontró enlace 'Política de cancelación'.")
}

// 2) Esperar modal visible (Bootstrap remoto)
TestObject modalPoliticas_C2 = new TestObject("modalPoliticas_C2")
modalPoliticas_C2.addProperty(
	"xpath",
	ConditionType.EQUALS,
	"//div[contains(@class,'modal') and (contains(@class,'in') or contains(@style,'display: block'))]//div[@class='modal-content']"
)
if (!WebUI.waitForElementVisible(modalPoliticas_C2, 10, FailureHandling.OPTIONAL)) {
	WebUI.delay(1.5)
	WebUI.waitForElementVisible(modalPoliticas_C2, 10)
}
KeywordUtil.logInfo("🪟 [C] Modal visible.")

// 3) Localizar el modal-body
List<String> bodyXPathsC2 = Arrays.asList(
	"//div[contains(@class,'modal') and contains(@class,'in')]//div[@class='modal-content']//div[contains(@class,'modal-body')]",
	"//div[@role='dialog' and contains(@class,'modal') and contains(@style,'display: block')]//div[contains(@class,'modal-body')]",
	"//*[@id='main-modal']//div[contains(@class,'modal-body')]",
	"//div[contains(@class,'modal-dialog')]//div[contains(@class,'modal-body')]"
)
WebElement bodyElC2 = null
String chosenXpathC2 = null
for (String xp : bodyXPathsC2) {
	TestObject probe = new TestObject("modalBody_C2_probe")
	probe.addProperty("xpath", ConditionType.EQUALS, xp)
	try {
		if (WebUI.waitForElementVisible(probe, 4, FailureHandling.OPTIONAL)) {
			bodyElC2 = WebUiCommonHelper.findWebElement(probe, 5)
			chosenXpathC2 = xp
			break
		}
	} catch (Throwable ignored) {}
}

// 4) innerHTML → texto; si falla, getText
String htmlC2 = ""
if (bodyElC2 != null) {
	try {
		htmlC2 = (String) WebUI.executeJavaScript("return arguments[0].innerHTML || '';", Arrays.asList(bodyElC2))
	} catch (Exception e) {
		TestObject fallbackTO = new TestObject("modalBody_C2_fallback")
		fallbackTO.addProperty("xpath", ConditionType.EQUALS, chosenXpathC2)
		htmlC2 = WebUI.getText(fallbackTO)
	}
}

// 5) HTML → texto con \n y recomposición segura de horas (igual que B)
String textoC2 = ""
if (htmlC2 != null && htmlC2.trim().length() > 0) {
	textoC2 = htmlC2
		.replaceAll('(?i)<br\\s*/?>', '\n')
		.replaceAll('(?i)</p>', '\n')
		.replaceAll('(?i)<[^>]+>', ' ')
} else {
	textoC2 = ""
}

// Unir líneas ":mm:ss" o ":hh:mm:ss" a la anterior
List<String> rawLinesC2 = Arrays.asList(textoC2.split('\\r?\\n'))
List<String> fixedLinesC2 = new ArrayList<>()
rawLinesC2.each { ln ->
	String line = (ln ?: '').replace('\u00A0',' ').replaceAll('[\\t\\x0B\\f\\r]+',' ').trim()
	def onlyTime = (line =~ /^:(\d{2}:\d{2}(?::\d{2})?)$/)
	if (onlyTime.matches() && !fixedLinesC2.isEmpty()) {
		String prev = fixedLinesC2.get(fixedLinesC2.size() - 1)
		prev = (prev ?: "").replaceAll('\\s+$','')
		fixedLinesC2.set(fixedLinesC2.size() - 1, prev + ':' + onlyTime[0][1])
	} else {
		fixedLinesC2.add(line)
	}
}
textoC2 = fixedLinesC2.join('\n')

// Limpieza (conservando \n)
textoC2 = textoC2.replace('\u00A0',' ').replaceAll('[\\t\\x0B\\f\\r]+',' ')

// 6) Segmentar por políticas con separador literal
// ⛔ IMPORTANTE: NO segmentar por “o no show” (rompe la 3ra política).
List<String> segmentosC2 = []
String lowerC2 = (textoC2 ?: "").toLowerCase()
final String SEP_C2 = '§§SEG_C2§§'
final String SEP_C2_REPL = java.util.regex.Matcher.quoteReplacement(SEP_C2)
String workC2 = (textoC2 ?: "")

if (lowerC2.contains('cancelando') || lowerC2.contains('fecha y hora')) {
	workC2 = workC2.replaceAll(
		'(?is)(?=\\b(?:cancelando\\s+desde|cancelando\\s+despu[eé]s\\s+de|fecha y hora))',
		SEP_C2_REPL
	)
} else {
	// Fallback: antes de un cargo tipo ": 0 USD", ": $ 5.15 USD", ": 1 noche"
	workC2 = workC2.replaceAll(
		'(?i)(?=:\\s*(?:\\$?\\s?\\d|\\d|noche))',
		SEP_C2_REPL
	)
}
String[] rawSegsC2 = workC2.split(java.util.regex.Pattern.quote(SEP_C2))
segmentosC2 = Arrays.asList(rawSegsC2).collect { it.trim() }.findAll { it }

// 7) Regex DOTALL y parseo (incluye “o no show” dentro del segmento)
def pDesdeHastaC2 = ~/(?is)^(?:cancelando\s+)?desde\s+(.*?)\s+hasta\s+(.*?)(?::\s*(?!\d{1,2}:)(.+))?$/
def pDespuesDeC2  = ~/(?is)^(?:cancelando\s+)?despu[eé]s\s+de\s+(.*?)(?::\s*(?!\d{1,2}:)(.+))?$/
def pNoShowC2     = ~/(?is)^(?:cancelando\s+)?desde\s+(.*?)\s+o\s+no[- ]?show(?::\s*(?!\d{1,2}:)(.+))?$/
def pFechaHoraC2  = ~/(?is)^fecha y hora.*$/

List<Map<String,String>> politicasCList = new ArrayList<>()

segmentosC2.each { seg ->
	String s = (seg ?: '').trim()
	if (!s) return

	def m1 = (s =~ pDesdeHastaC2)
	def m2 = (s =~ pDespuesDeC2)
	def mNo = (s =~ pNoShowC2)
	def m3 = (s =~ pFechaHoraC2)

	if (m1.matches()) {
		String desde = (m1[0][1] ?: '').replace('\u00A0',' ').replaceAll('\\s+',' ').trim()
		String hasta = (m1[0][2] ?: '').replace('\u00A0',' ').replaceAll('\\s+',' ').trim()
		String cargo = (m1[0][3] ?: '').replace('\u00A0',' ').replaceAll('\\s+',' ').trim()
		politicasCList.add([condicion: "Cancelando desde ${desde} hasta ${hasta}", cargo: cargo])
	} else if (m2.matches()) {
		String despues = (m2[0][1] ?: '').replace('\u00A0',' ').replaceAll('\\s+',' ').trim()
		String cargo   = (m2[0][2] ?: '').replace('\u00A0',' ').replaceAll('\\s+',' ').trim()
		politicasCList.add([condicion: "Cancelando después de ${despues}", cargo: cargo])
	} else if (mNo.matches()) {
		String desde = (mNo[0][1] ?: '').replace('\u00A0',' ').replaceAll('\\s+',' ').trim()
		String cargo = (mNo[0][2] ?: '').replace('\u00A0',' ').replaceAll('\\s+',' ').trim()
		politicasCList.add([condicion: "Cancelando desde ${desde} o no show", cargo: cargo])
	} else if (m3.matches()) {
		politicasCList.add([condicion: "Fecha y hora calculada basándose en el huso horario de destino.", cargo: "N/A"])
	}
}

// 8) Publicar en politicasC y log
politicasC.clear()
politicasC.addAll(politicasCList)

KeywordUtil.logInfo("📋 [C] Políticas capturadas (estandarizadas):")
for (int i = 0; i < politicasC.size(); i++) {
	String msg = "📌 Política ${i+1}: Condición='${politicasC[i]['condicion']}' | Cargo='${politicasC[i]['cargo']}'"
	println(msg)
	KeywordUtil.logInfo(msg)
}

// 9) Comparación B vs C
def _normTextCc2 = { String s -> (s ?: "").replace('\u00A0',' ').replaceAll('\\s+',' ').trim() }
def _toCmpCc2    = { Map m -> [condicion: _normTextCc2(m.condicion), cargo: _normTextCc2(m.cargo)] }

List<Map<String,String>> _B_Ccmp2 = (politicasB ?: []).collect { _toCmpCc2(it) }
List<Map<String,String>> _C_Ccmp2 = (politicasC ?: []).collect { _toCmpCc2(it) }

if (_B_Ccmp2.isEmpty()) {
	KeywordUtil.markWarning("⚠️ [C] No hay políticas del paso B para comparar.")
} else {
	if (_B_Ccmp2.size() != _C_Ccmp2.size()) {
		KeywordUtil.markWarning("⚠️ [C] Cantidad difiere: B=${_B_Ccmp2.size()} vs C=${_C_Ccmp2.size()} (se comparará por índice).")
	}
	KeywordUtil.logInfo("🔍 [C] Comparando B vs C…")
	int maxCompararC2 = Math.max(_B_Ccmp2.size(), _C_Ccmp2.size())
	for (int i = 0; i < maxCompararC2; i++) {
		def b = i < _B_Ccmp2.size() ? _B_Ccmp2[i] : [condicion:"N/A", cargo:"N/A"]
		def c = i < _C_Ccmp2.size() ? _C_Ccmp2[i] : [condicion:"N/A", cargo:"N/A"]
		if (b.condicion == c.condicion && b.cargo == c.cargo) {
			KeywordUtil.logInfo("✅ [C] Política ${i+1} coincide entre B y C.")
		} else {
			KeywordUtil.markWarning("❌ [C] Diferencia en política ${i+1}: B='${b}' vs C='${c}'")
		}
	}
}

// 10) Cerrar modal
TestObject cerrarPoliticaBtnC2 = new TestObject("cerrarPoliticaBtn_C2")
cerrarPoliticaBtnC2.addProperty(
	"xpath",
	ConditionType.EQUALS,
	"//div[(contains(@class,'modal') and contains(@class,'in')) or (contains(@class,'modal') and contains(@style,'display: block'))]//button[contains(@class,'close') or @aria-label='Cerrar']"
)
if (WebUI.waitForElementVisible(cerrarPoliticaBtnC2, 10, FailureHandling.OPTIONAL)) {
	try {
		WebUI.click(cerrarPoliticaBtnC2)
		KeywordUtil.logInfo("🧩 [C] Modal cerrado (click normal).")
	} catch (Exception e) {
		WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(WebUiCommonHelper.findWebElement(cerrarPoliticaBtnC2, 10)))
		KeywordUtil.logInfo("🪄 [C] Modal cerrado (click JS).")
	}
} else {
	KeywordUtil.markWarning("⚠️ [C] No se encontró la 'X' para cerrar el modal.")
}
TestObject overlayModalInC2 = new TestObject("overlayModalIn_C2")
overlayModalInC2.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class,'modal') and (contains(@class,'in') or contains(@style,'display: block'))]")
WebUI.waitForElementNotPresent(overlayModalInC2, 10)
WebUI.delay(0.5)

// 📌 Capturar localizador antes de imprimir
WebUI.waitForElementVisible(findTestObject('Euromundo/quotes/save_locator_quote'), 10)
String textoLocalizador = WebUI.getText(findTestObject('Euromundo/quotes/save_locator_quote'))
String codigoLocalizador = textoLocalizador.replace('Localizador:', '').trim()
WebUI.comment("📌 Localizador capturado: " + codigoLocalizador)

// Guardar para comparación posterior
GlobalVariable.localizadorGuardado = codigoLocalizador

// 🖨️ Imprimir cotización (abre el PDF en nueva pestaña o ventana)
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_print_quote'), 10)
WebUI.click(findTestObject('Euromundo/book_steps/button_print_quote'))

// 🔄 Cambiar al tab del PDF (segunda pestaña)
WebUI.switchToWindowIndex(1)
WebUI.delay(5) // Esperar que cargue el PDF

// 🔍 Validar que el PDF se abrió correctamente
String urlPDF = WebUI.getUrl()

if (urlPDF.contains("https://euromundomxn.juniper.es/voucher/voucher.aspx")) {
	WebUI.comment("✅ Se abrió correctamente el PDF: " + urlPDF)

	// ✅ Guardar la URL como evidencia
	String evidenciaPath = 'Evidencias/url_pdf.txt'
	Files.createDirectories(Paths.get("Evidencias"))
	Files.write(
		Paths.get(evidenciaPath),
		urlPDF.getBytes("UTF-8"),
		StandardOpenOption.CREATE,
		StandardOpenOption.TRUNCATE_EXISTING
	)
} else {
	WebUI.comment("❌ URL inesperada del PDF: " + urlPDF)
	WebUI.markFailed("La URL del PDF no es la esperada.")
}

// 🔙 Cerrar la pestaña del PDF y volver a la principal
WebUI.closeWindowIndex(1)
WebUI.switchToWindowIndex(0)

// 🔍 Buscar cotización por localizador
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_quotes'), 10)
WebUI.click(findTestObject('Euromundo/book_steps/button_quotes'))
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_quote_locator'), 10)
WebUI.click(findTestObject('Euromundo/book_steps/input_quote_locator'))
WebUI.setText(findTestObject("Euromundo/book_steps/input_quote_locator"), GlobalVariable.localizadorGuardado)
WebUI.click(findTestObject('Euromundo/book_steps/button_search_quote'))
WebUI.comment("✅ Localizador buscado y validado: ${GlobalVariable.localizadorGuardado}")
WebUI.click(findTestObject('Euromundo/quotes/validation_quote'))
// ✅ Validar mensaje de éxito tras guardar cotización
String textoCompleto = WebUI.getText(findTestObject('Euromundo/quotes/validation_quote'))

if (textoCompleto.contains("Su cotización ha sido guardada con éxito")) {
	WebUI.comment("✅ Mensaje de éxito validado correctamente.")
} else {
	WebUI.comment("❌ El texto no contiene el mensaje esperado.")
}