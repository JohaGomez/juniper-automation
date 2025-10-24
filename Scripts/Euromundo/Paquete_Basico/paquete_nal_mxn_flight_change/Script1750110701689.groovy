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

// 🚪 Login
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_mxn'), [:], FailureHandling.STOP_ON_FAILURE)

// ☰ Navegar al menú de paquete nacional
WebUI.waitForElementClickable(findTestObject('Euromundo/paquete_basico/repository_paquete_nal/menu_paquete_basico'), 10)
WebUI.mouseOver(findTestObject('Euromundo/paquete_basico/repository_paquete_nal/menu_paquete_basico'))
WebUI.click(findTestObject('Euromundo/paquete_basico/repository_paquete_nal/select_paquete_nal'))

// 📦 Cerrar cookies si aparecen
WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

// 📍 Origen y destino
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_inter'), 15)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_inter'), '35163', true)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/select_origin_inter'), '35908', true)
CustomKeywords.'utils.FechaUtils.setFechaAleatoriaDesdeTresMesesFuturo'('Euromundo/book_steps/origin_date_inter') // 📅 Fecha

// 👥 1. Abrir selector de habitaciones y pasajeros
WebUI.click(findTestObject('Euromundo/widget/set_rooms_pax_inter'))

// 🏨 2. Datos base de habitaciones y pasajeros
int habitaciones = 1
List<Integer> adultos = [2]
List<Integer> ninos = [0]
List<Integer> infantes = [0]

// 📥 3. Traer edades desde variables globales (si existen)
List<Integer> edadesNinos = (GlobalVariable.edadesNinos ?: []).findAll { it?.toString()?.isInteger() }.collect { it.toInteger() }
List<Integer> edadesInfantes = (GlobalVariable.edadesInfantes ?: []).findAll { it?.toString()?.isInteger() }.collect { it.toInteger() }

// 🛡️ 4. Validar rangos
boolean edadesNinosValidas = edadesNinos.every { it in 2..17 }
boolean edadesInfantesValidas = edadesInfantes.every { it in 0..1 }

if (!edadesNinosValidas || !edadesInfantesValidas) {
	KeywordUtil.markFailed("🚨 Edades fuera de rango: Niños: ${edadesNinos}, Infantes: ${edadesInfantes}")
	return
} else {
	KeywordUtil.markPassed("✅ Edades válidas: Niños ${edadesNinos}, Infantes ${edadesInfantes}")
}

// ⚙️ 5. Configurar habitaciones y pasajeros con la nueva keyword robusta
CustomKeywords.'utils.configuration_rooms.configurarHabitacionesYPasajerosV2'(
	habitaciones, adultos, ninos, infantes, edadesNinos, edadesInfantes
)

// 👁 6. Capturar visualmente edades seleccionadas del DOM (para paso posterior)
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
			WebUI.comment("🧒 Edad Niño ${i} capturada: ${edad}")
		} else {
			WebUI.comment("❗Edad no válida para niño ${i}: ${edadTexto}")
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
			WebUI.comment("👶 Edad Infante ${i} capturada: ${edad}")
		} else {
			WebUI.comment("❗Edad no válida para infante ${i}: ${edadTexto}")
		}
	}
}

// 🔍 Buscar
WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))

// 🏨 Validar si se muestra el hotel
boolean hotelVisible = WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_prebook_gd_nal'), 10, FailureHandling.OPTIONAL)

if (hotelVisible) {
    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_prebook_gd_nal'), 15)
} else {
    WebUI.comment('🔄 Resultado no cargado a la primera, reintentando búsqueda...')
    WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_edit'), 10)
    WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))
    WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))
    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_prebook_gd_nal'), 15)
    
}

// ✈️ Seleccionar vuelo
WebUI.click(findTestObject('Euromundo/flights/button_flight_change_auv'))
WebUI.click(findTestObject('Euromundo/flights/checkbox_filter_airline_aerobus'))
CustomKeywords.'utils.aerolinea_selector.seleccionarVueloAerobus'()
WebUI.waitForElementVisible(findTestObject('Euromundo/flights/select_vivaerobus_flight'), 20)
WebUI.click(findTestObject('Euromundo/flights/select_vivaerobus_flight'))

//Guardar titulo del paquete para después comparar en página de pax
TestObject tituloObj = new TestObject("tituloDinamico")
tituloObj.addProperty("xpath", ConditionType.EQUALS, '//*[@id="main-content"]/div/div[2]/div[1]/div/div/div/article/div/div[2]/div[1]')

//Guardar el texto en una variable local
String tituloGuardado = WebUI.getText(tituloObj)

// --- Crear TestObject para capturar el mejor precio en la página de resultados ---
TestObject mejorPrecioObj = new TestObject().addProperty(
	"xpath", ConditionType.EQUALS,
	"//*[@id='main-content']/div/div[2]/div[1]/div/div/div/article/section[2]/div/div/div[2]/span[1]"
)

// --- Llamar a la keyword para capturar y validar el precio en página de resultados ---
double mejorPrecio = CustomKeywords.'utils.ValidacionPrecios.validarMejorPrecioEnResultados'(mejorPrecioObj)

// --------------------------------------------------
// ✈️ Validación y comparación de información de vuelos
// --------------------------------------------------

// 1. Abrir la lista desplegable de detalles del vuelo
// --------------------------------------------------
TestObject toggleInfo = new TestObject("toggleInfo")
toggleInfo.addProperty("xpath", ConditionType.EQUALS,
	"//div[contains(@class,'result-option-package__row') and contains(@class,'flight')]//button[contains(@class,'result-option-package__moreinfo-toggle')]")

if (WebUI.verifyElementPresent(toggleInfo, 10, FailureHandling.OPTIONAL)) {
	WebUI.click(toggleInfo)
	WebUI.waitForElementVisible(
		new TestObject().addProperty("xpath", ConditionType.EQUALS,
			"//div[contains(@class,'flight-segments__segment-info')]"),
		10
	)
	println "✅ Toggle de vuelos clickeado y desplegado"
} else {
	println "⚠️ No se encontró el toggle de vuelos"
}

// --------------------------------------------------
// 2. Esperar a que se muestre la información extendida
// --------------------------------------------------
WebUI.waitForElementVisible(
	new TestObject().addProperty("xpath", ConditionType.EQUALS,
		"//div[contains(@class,'flight-segments__segment-info')]"),
	10
)

// --------------------------------------------------
// 3. Capturar información de vuelo de IDA
// --------------------------------------------------

// Número de vuelo
TestObject vueloIdaObj = new TestObject("vueloIdaObj")
vueloIdaObj.addProperty("xpath", ConditionType.EQUALS,
	"(//div[contains(@class,'flight-segments__segment-info')]/div[2])[1]")
String numeroVueloIda = WebUI.getText(vueloIdaObj).replace("Vuelo:", "").trim()

// Charter?
boolean idaEsCharter = numeroVueloIda.endsWith("N")
if (idaEsCharter) {
	KeywordUtil.logInfo("✈️ El vuelo de IDA ${numeroVueloIda} es CHARTER")
} else {
	KeywordUtil.logInfo("✈️ El vuelo de IDA ${numeroVueloIda} es REGULAR")
}

// Clase (4to div ahora)
TestObject claseIdaObj = new TestObject("claseIdaObj")
claseIdaObj.addProperty("xpath", ConditionType.EQUALS,
	"(//div[contains(@class,'flight-segments__segment-info')]/div[4])[1]")
String claseVueloIda = WebUI.getText(claseIdaObj).replace("Clase:", "").trim()

// Equipaje (5to div ahora)
TestObject equipajeIdaObj = new TestObject("equipajeIdaObj")
equipajeIdaObj.addProperty("xpath", ConditionType.EQUALS,
	"(//div[contains(@class,'flight-segments__segment-info')]/div[5])[1]")
String equipajeIda = WebUI.getText(equipajeIdaObj).replace("Equipaje:", "").trim()

// --------------------------------------------------
// 4. Capturar información de vuelo de REGRESO
// --------------------------------------------------

// Número de vuelo
TestObject vueloRegresoObj = new TestObject("vueloRegresoObj")
vueloRegresoObj.addProperty("xpath", ConditionType.EQUALS,
	"(//div[contains(@class,'flight-segments__segment-info')]/div[2])[2]")
String numeroVueloRegreso = WebUI.getText(vueloRegresoObj).replace("Vuelo:", "").trim()

// Charter?
boolean regresoEsCharter = numeroVueloRegreso.endsWith("N")
if (regresoEsCharter) {
	KeywordUtil.logInfo("✈️ El vuelo de REGRESO ${numeroVueloRegreso} es CHARTER")
} else {
	KeywordUtil.logInfo("✈️ El vuelo de REGRESO ${numeroVueloRegreso} es REGULAR")
}

// Clase (4to div)
TestObject claseRegresoObj = new TestObject("claseRegresoObj")
claseRegresoObj.addProperty("xpath", ConditionType.EQUALS,
	"(//div[contains(@class,'flight-segments__segment-info')]/div[4])[2]")
String claseVueloRegreso = WebUI.getText(claseRegresoObj).replace("Clase:", "").trim()

// Equipaje (5to div)
TestObject equipajeRegresoObj = new TestObject("equipajeRegresoObj")
equipajeRegresoObj.addProperty("xpath", ConditionType.EQUALS,
	"(//div[contains(@class,'flight-segments__segment-info')]/div[5])[2]")
String equipajeRegreso = WebUI.getText(equipajeRegresoObj).replace("Equipaje:", "").trim()

// --------------------------------------------------
// 5. Mostrar resultados en consola
// --------------------------------------------------
println "✈️ Vuelo IDA -> Número: ${numeroVueloIda}, Clase: ${claseVueloIda}, Equipaje: ${equipajeIda}"
println "✈️ Vuelo REGRESO -> Número: ${numeroVueloRegreso}, Clase: ${claseVueloRegreso}, Equipaje: ${equipajeRegreso}"

// --------------------------------------------------
// 6. Guardar en variables globales para validaciones posteriores
// --------------------------------------------------
GlobalVariable.vuelos = [
	[tipo: "IDA", numero: numeroVueloIda, clase: claseVueloIda, equipaje: equipajeIda],
	[tipo: "REGRESO", numero: numeroVueloRegreso, clase: claseVueloRegreso, equipaje: equipajeRegreso]
]


WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'))
WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'))


// 🧑 Datos pasajeros
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)

// 🛡 Validar nuevamente rangos por seguridad
boolean edadesValidas = edadesCapturadasNinos.every { it in 2..17 } && edadesCapturadasInfantes.every { it in 0..1 }
if (!edadesValidas) {
	KeywordUtil.markFailed("🚨 Edades inválidas detectadas ➜ Niños: ${edadesCapturadasNinos}, Infantes: ${edadesCapturadasInfantes}")
} else {
	// ✅ Pasar directamente a la keyword
	CustomKeywords.'utils.PassengerFormHelper.fillPassengerData'(
		edadesCapturadasNinos, edadesCapturadasInfantes
	)
}


//Guardar titulo del paquete para después comparar en página de pasajeros
TestObject otroTituloObj = new TestObject("otroTitulo")
otroTituloObj.addProperty("xpath", ConditionType.EQUALS, '//*[@id="main-content"]/div[2]/div/div[3]/div[1]/div/div/div/div[1]/div[2]/div[1]')

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

	// Regex que captura precios en USD o MXN
	def matcher = (warningText =~ /\$?\s?([\d.,]+)\s?(USD|MXN)/)
	def precios = matcher.collect { 
		[valor: it[1]?.trim(), moneda: it[2]]
	}

	if (!precios.isEmpty()) {
		def ultimoPrecio = precios.last()
		String nuevoPrecioStr = ultimoPrecio.valor
		String moneda = ultimoPrecio.moneda

		try {
			// 🚀 Solo aquí se usa parseMoney
			mejorPrecio = ValidacionesPrebook.parseMoney(nuevoPrecioStr)
			KeywordUtil.logInfo("💲 Nuevo precio detectado (warning): ${mejorPrecio} ${moneda}")
		} catch (Exception e) {
			KeywordUtil.markWarning("⚠️ No se pudo convertir el nuevo precio: ${nuevoPrecioStr} ${moneda}")
		}
	} else {
		KeywordUtil.markWarning("⚠️ No se pudo extraer ningún precio del warning")
	}
} else {
	KeywordUtil.logInfo("✅ No apareció ningún warning de cambio de precio")
}

// --------------------------------------------------
// 📋 Comparación de vuelos entre resultados y detalle expandido
// --------------------------------------------------

// 1. Abrir toggle de detalle de vuelos (si está oculto)
TestObject toggleDetalleVuelos = new TestObject("toggleDetalleVuelos")
toggleDetalleVuelos.addProperty("xpath", ConditionType.EQUALS,
	"//div[contains(@class,'result-option-package__row') and contains(@class,'flight')]//button[contains(@class,'result-option-package__moreinfo-toggle')]")

if (WebUI.verifyElementPresent(toggleDetalleVuelos, 10, FailureHandling.OPTIONAL)) {
	WebUI.click(toggleDetalleVuelos)
	WebUI.waitForElementVisible(
		new TestObject().addProperty("xpath", ConditionType.EQUALS,
			"(//div[contains(@class,'flight-segments__segment-info')])[1]"),
		10
	)
	println "✅ Toggle de vuelos desplegado para comparación"
}

// 2. Capturar datos de IDA en el detalle
TestObject vueloIdaDetalleObj = new TestObject("vueloIdaDetalleObj")
vueloIdaDetalleObj.addProperty("xpath", ConditionType.EQUALS,
	"(//div[contains(@class,'flight-segments__segment-info')]/div[2])[1]")
String numeroVueloIdaDetalle = WebUI.getText(vueloIdaDetalleObj).replace("Vuelo:", "").trim()

TestObject claseIdaDetalleObj = new TestObject("claseIdaDetalleObj")
claseIdaDetalleObj.addProperty("xpath", ConditionType.EQUALS,
	"(//div[contains(@class,'flight-segments__segment-info')]/div[3])[1]")
String claseVueloIdaDetalle = WebUI.getText(claseIdaDetalleObj).replace("Clase:", "").trim()

TestObject equipajeIdaDetalleObj = new TestObject("equipajeIdaDetalleObj")
equipajeIdaDetalleObj.addProperty("xpath", ConditionType.EQUALS,
	"(//div[contains(@class,'flight-segments__segment-info')]/div[4])[1]")
String equipajeIdaDetalle = WebUI.getText(equipajeIdaDetalleObj).replace("Equipaje:", "").trim()

// 3. Capturar datos de REGRESO en el detalle
TestObject vueloRegresoDetalleObj = new TestObject("vueloRegresoDetalleObj")
vueloRegresoDetalleObj.addProperty("xpath", ConditionType.EQUALS,
	"(//div[contains(@class,'flight-segments__segment-info')]/div[2])[2]")
String numeroVueloRegresoDetalle = WebUI.getText(vueloRegresoDetalleObj).replace("Vuelo:", "").trim()

TestObject claseRegresoDetalleObj = new TestObject("claseRegresoDetalleObj")
claseRegresoDetalleObj.addProperty("xpath", ConditionType.EQUALS,
	"(//div[contains(@class,'flight-segments__segment-info')]/div[3])[2]")
String claseVueloRegresoDetalle = WebUI.getText(claseRegresoDetalleObj).replace("Clase:", "").trim()

TestObject equipajeRegresoDetalleObj = new TestObject("equipajeRegresoDetalleObj")
equipajeRegresoDetalleObj.addProperty("xpath", ConditionType.EQUALS,
	"(//div[contains(@class,'flight-segments__segment-info')]/div[4])[2]")
String equipajeRegresoDetalle = WebUI.getText(equipajeRegresoDetalleObj).replace("Equipaje:", "").trim()

// --------------------------------------------------
// 4. Comparaciones con GlobalVariable.vuelos
// --------------------------------------------------
Map vueloIdaGuardado = GlobalVariable.vuelos.find { it.tipo == "IDA" }
Map vueloRegresoGuardado = GlobalVariable.vuelos.find { it.tipo == "REGRESO" }

// Validaciones IDA
assert vueloIdaGuardado.numero == numeroVueloIdaDetalle : "❌ Numero de vuelo IDA distinto (${vueloIdaGuardado.numero} vs ${numeroVueloIdaDetalle})"
assert vueloIdaGuardado.clase == claseVueloIdaDetalle : "❌ Clase IDA distinta (${vueloIdaGuardado.clase} vs ${claseVueloIdaDetalle})"
assert vueloIdaGuardado.equipaje == equipajeIdaDetalle : "❌ Equipaje IDA distinto (${vueloIdaGuardado.equipaje} vs ${equipajeIdaDetalle})"

// Validaciones REGRESO
assert vueloRegresoGuardado.numero == numeroVueloRegresoDetalle : "❌ Numero de vuelo REGRESO distinto (${vueloRegresoGuardado.numero} vs ${numeroVueloRegresoDetalle})"
assert vueloRegresoGuardado.clase == claseVueloRegresoDetalle : "❌ Clase REGRESO distinta (${vueloRegresoGuardado.clase} vs ${claseVueloRegresoDetalle})"
assert vueloRegresoGuardado.equipaje == equipajeRegresoDetalle : "❌ Equipaje REGRESO distinto (${vueloRegresoGuardado.equipaje} vs ${equipajeRegresoDetalle})"

println "✅ Validación de vuelos completada correctamente en página de pasajeros"
	

// TestObjects de la página de pasajeros para validación de precios
TestObject precioObj = new TestObject().addProperty("xpath", ConditionType.EQUALS,
	"//div[@class='booking-breakdown__item booking-breakdown__item--total booking-breakdown__item--is-pay-web']//span[@class='booking-breakdown__item-price']")
TestObject comisionObj = new TestObject().addProperty("xpath", ConditionType.EQUALS,
	"//div[@class='booking-breakdown__item']//span[contains(text(),'Comisiones')]/following-sibling::span")
TestObject precioFinalObj = new TestObject().addProperty("xpath", ConditionType.EQUALS,
	"//div[@class='booking-breakdown__item']//span[contains(text(),'Precio final')]/following-sibling::span")
TestObject totalAdeudadoObj = new TestObject().addProperty("xpath", ConditionType.EQUALS,
	"//span[@class='agent-markup__total-due-price']")

CustomKeywords.'utils.ValidacionesPrebook.validarPrecioPrebook'(
	precioObj,
	comisionObj,
	precioFinalObj,
	totalAdeudadoObj,
	mejorPrecio
)

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

// ➕ Finalizar datos pasajeros
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_prebook'))

// 👤 Confirmar titular reserva
TestObject paxDropdown = new TestObject('dynamicPaxSelect')
paxDropdown.addProperty('xpath', ConditionType.EQUALS, '//select[contains(@class,\'js-set-confirm-pax-data\')]')
WebElement dropdownElem = WebUiCommonHelper.findWebElement(paxDropdown, 10)
new Select(dropdownElem).selectByIndex(1)

// 🏠 Datos contacto
WebUI.setText(findTestObject('Euromundo/checkout_page/passport_booking_holder'), '102635')

// Ciudad
TestObject cityObj = new TestObject('dynamicCity')
cityObj.addProperty('xpath', ConditionType.EQUALS, '//input[@name=\'holder_city\']')
WebUI.waitForElementVisible(cityObj, 10)
WebUI.setText(cityObj, 'Bogotá')

// Código postal
TestObject zipObj = new TestObject('dynamicZip')
zipObj.addProperty('xpath', ConditionType.EQUALS, '//input[@name=\'holder_zipcode\']')
WebUI.waitForElementVisible(zipObj, 10)
WebUI.setText(zipObj, '110111')

// Dirección
TestObject addrObj = new TestObject('dynamicAddress')
addrObj.addProperty('xpath', ConditionType.EQUALS, '//input[@name=\'holder_address\']')
WebUI.waitForElementVisible(addrObj, 10)
WebUI.setText(addrObj, 'Virrey')

// Teléfono y facturación
WebUI.setText(findTestObject('Euromundo/checkout_page/phone_booking_holder'), '3218111877')
WebUI.setText(findTestObject('Euromundo/checkout_page/agent_email'), 'johana.gomez@ejuniper.com')
WebUI.setText(findTestObject('Euromundo/checkout_page/set_name_billing_info'), 'Johana Gomez')
WebUI.setText(findTestObject('Euromundo/checkout_page/set_address_billing_info'), 'Virrey')
WebUI.setText(findTestObject('Euromundo/checkout_page/set_rfc_billing_info'), '21312344')
WebUI.setText(findTestObject('Euromundo/checkout_page/set_email_billing_info'), 'johana.gomez@ejuniper.com')

// ✅ Términos y condiciones
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_fare_breakdown'))
def cb = findTestObject('Euromundo/checkout_page/checkbox_importantinfo')

if (WebUI.waitForElementPresent(cb, 3, FailureHandling.OPTIONAL) &&
	!WebUI.verifyElementChecked(cb, 1, FailureHandling.OPTIONAL)) {
  WebUI.scrollToElement(cb, 2, FailureHandling.OPTIONAL)
  try {
	WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_importantinfo'))
  } catch (Exception e) {
	WebUI.enhancedClick(cb) // fallback sin romper el flujo
  }
} else {
  WebUI.comment('➡️ Checkbox no presente o ya marcado. Continúo con el flujo.')
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


// --- TestObjects con XPaths actualizados (montos) ---
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

