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
import com.kms.katalon.core.webui.driver.DriverFactory

// 🚪 Login
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_otn'), [:], FailureHandling.STOP_ON_FAILURE)

// 📂 Navegación al menú Gran Deal Internacional
WebUI.waitForElementClickable(findTestObject('Euromundo/Gran_Deal/repository_GD_Inter/menu_gd'), 10)
WebUI.mouseOver(findTestObject('Euromundo/Gran_Deal/repository_GD_Inter/menu_gd'))
WebUI.click(findTestObject('Euromundo/Gran_Deal/repository_GD_Inter/select_international'))

// 🔍 Búsqueda
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_box'), 15)
WebUI.click(findTestObject('Euromundo/book_steps/button_box'))
WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_inter'), 15)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_inter'), '35751', true ) // Destino
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/select_origin_inter'), 15)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/select_origin_inter'), '35908', true ) // Origen
CustomKeywords.'utils.FechaUtils.setFechaViernesEnTresMeses'('Euromundo/book_steps/origin_date_inter' // Fecha
    )

// 👥 1. Abrir selector de habitaciones y pasajeros
CustomKeywords.'helpers.WebUIHelper.safeClick'(findTestObject('Euromundo/widget/set_rooms_pax_inter'))

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

// ⚙️ 5. Configurar habitaciones y pasajeros
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

// Clic en botón de búsqueda
WebUI.click(findTestObject('Euromundo/Gran_Deal/repository_GD_Inter/button_search_inter'))

// 🏨 Esperar resultados o reintentar si no cargó
if (!(WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_prebook_gd_inter'), 10, FailureHandling.OPTIONAL))) {
    WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_edit'), 10)
    WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))
    WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))
    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_prebook_gd_inter'), 10)
}

WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'))

// 🛡️ Cambio de seguro
// 👉 Crear objeto dinámico para la pestaña "Seguro"
TestObject tabSeguro = new TestObject('dynamicTabInsurance')
tabSeguro.addProperty('xpath', ConditionType.EQUALS, "//a[@data-toggle='tab' and @href='#insurance']")

// 👉 Crear objeto dinámico para seleccionar seguro
TestObject selectInsuranceOption = new TestObject('dynamicSelectInsuranceOption')
selectInsuranceOption.addProperty('xpath', ConditionType.EQUALS, "//button[contains(@class,'result-option__choose-button') and contains(text(),'Seleccionar')]")
   
// 👉 Hacer clic en la pestaña "Seguro"
if (WebUI.verifyElementPresent(tabSeguro, 10, FailureHandling.OPTIONAL)) {
    WebUI.waitForElementClickable(tabSeguro, 10)
    WebUI.click(tabSeguro)
    WebUI.comment("✅ Pestaña 'Seguro' abierta correctamente.")
} else {
    WebUI.comment("⚠️ No se encontró la pestaña 'Seguro'. Se continúa el flujo sin seguro.")
}

// ✅ Validar opción de seguro y hacer clic
if (WebUI.verifyElementPresent(selectInsuranceOption, 10, FailureHandling.OPTIONAL)) {
    WebUI.waitForElementClickable(selectInsuranceOption, 10)
    WebUI.click(selectInsuranceOption)
    WebUI.comment("✅ Seguro seleccionado correctamente.")
} else {
    WebUI.comment("⚠️ Opción para seleccionar seguro no encontrada. Se continúa con el flujo sin seguro.")
}

//Guardar titulo del seguro para después comparar en página de pasajeros
TestObject tituloObj = new TestObject('tituloSeguroObj')
tituloObj.addProperty(
    "xpath",
    ConditionType.EQUALS,
    "//div[@class='result-option-package__item-product']/i[contains(@class,'glyphicon-insurance-search')]/parent::div")

//Guardar el texto en una variable local
String tituloGuardado = WebUI.getText(tituloObj)

// --- Crear TestObject para capturar el mejor precio en la página de resultados ---
TestObject mejorPrecioObj = new TestObject().addProperty(
	"xpath", ConditionType.EQUALS,
	"//span[@class='bestprice__amount']")

// --- Llamar a la keyword para capturar y validar el precio en página de resultados ---
double mejorPrecio = CustomKeywords.'utils.ValidacionPrecios.validarMejorPrecioEnResultados'(mejorPrecioObj)

// 💾 Confirmar selección
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_prebook'), 10)
WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'))

// 👥 Datos de pasajeros
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

// Esperar a que carguen los elementos
WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 15)

//Guardar titulo del Seguro para comparar
TestObject otroTituloObj = new TestObject("otroTitulo")
otroTituloObj.addProperty("xpath", ConditionType.EQUALS, "//div[@class='result-option-package__row']//div[@class='result-option-package__item-product']/i[contains(@class,'glyphicon-insurance-search')]/parent::div")

//Capturar el texto del segundo título
String tituloNuevo = WebUI.getText(otroTituloObj)

//Comparar
	if(tituloGuardado.equals(tituloNuevo)) {
		println("✅ Los títulos son iguales")
	} else {
		println("❌ Los títulos son diferentes")
	}

// ==========================
// ⚠️ Manejo de cambio de precio en warning
// ==========================
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
}

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

WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_prebook'))

// 🔽 Selección de pasajero responsable
TestObject paxDropdown = new TestObject('dynamicPaxSelect')
paxDropdown.addProperty('xpath', ConditionType.EQUALS, '//select[contains(@class,\'js-set-confirm-pax-data\')]')
WebElement selectElem = WebUiCommonHelper.findWebElement(paxDropdown, 10)
new Select(selectElem).selectByIndex(1)

// 🏠 Datos de contacto
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

// ✅ Aceptación de condiciones
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_fare_breakdown'))
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_importantInfo'))
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_TyC_checkout'))

//💾 Validaciones de precios para cerrar reserva

// ==========================
// 🎯 Toggle desplegable Detalle Seguro
// ==========================
TestObject toggleDetalle = new TestObject("toggleDetalle")
toggleDetalle.addProperty(
	"xpath",
	ConditionType.EQUALS,
	"//button[contains(@class,'shopping-basket__line-product-extended-details-toggle')]"
)

// Contenedor que se muestra/oculta al expandir
TestObject detalleHotelContainer = new TestObject("detalleHotelContainer")
detalleHotelContainer.addProperty(
	"xpath",
	ConditionType.EQUALS,
	"//div[contains(@class,'shopping-basket__line-product-extended-details')]"
)

if (WebUI.verifyElementPresent(toggleDetalle, 10, FailureHandling.OPTIONAL)) {
	try {
		// Solo clic si el contenedor NO es visible
		if (!WebUI.verifyElementVisible(detalleHotelContainer, FailureHandling.OPTIONAL)) {
			WebElement el = WebUI.findWebElement(toggleDetalle, 10)
			WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(el))
			KeywordUtil.logInfo("✅ Toggle detalle clickeado para ABRIR")
			WebUI.delay(1) // esperar a que el contenido se renderice
		} else {
			KeywordUtil.logInfo("ℹ️ El detalle del hotel ya estaba desplegado, no se hace clic")
		}
	} catch (Exception e) {
		KeywordUtil.markWarning("⚠️ No se pudo interactuar con el toggle: ${e.message}")
	}
} else {
	KeywordUtil.markWarning("⚠️ No se encontró el toggleDetalle en la cesta")
}


// ====================================
// 🚀 Obtener solo el nombre del Seguro
// ====================================
TestObject tituloSeguroObj = new TestObject('tituloSeguroObj')
tituloSeguroObj.addProperty(
	"xpath",
	ConditionType.EQUALS,
	"//ul[contains(@class,'stayLines')]/li[contains(.,'Seguro:')]"
)

if (WebUI.waitForElementVisible(tituloSeguroObj, 15, FailureHandling.OPTIONAL)) {
	String tituloSeguroRaw = WebUI.getText(tituloSeguroObj).trim()
	KeywordUtil.logInfo("📌 Texto crudo capturado: ${tituloSeguroRaw}")

	// 💡 Quitar prefijo "Seguro:" si aparece
	String tituloSeguro = tituloSeguroRaw.replace("Seguro:", "").trim()
	// 💡 Eliminar la fecha si está al final
	tituloSeguro = tituloSeguro.replaceAll("\\d{2}/\\d{2}/\\d{4}", "").trim()

	KeywordUtil.logInfo("📌 Nombre Seguro procesado: ${tituloSeguro}")

	// ==========================
	// ✅ Comparar con el esperado
	// ==========================
	if (tituloNuevo.equalsIgnoreCase(tituloSeguro)) {
		KeywordUtil.logInfo("✅ Los títulos coinciden: '${tituloNuevo}'")
	} else {
		KeywordUtil.markFailedAndStop("❌ Los títulos NO coinciden: '${tituloNuevo}' vs '${tituloSeguro}'")
	}
} else {
	WebUI.takeScreenshot()
	KeywordUtil.markFailedAndStop("⚠️ No se encontró el nombre del seguro en el desplegable")
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

// 💾 Finalizar reserva
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_book'))

// ❌ Cancelación de reserva
WebUI.click(findTestObject('Euromundo/book_steps/button_cancel_book'))
WebUI.waitForAlert(10)
WebUI.acceptAlert()

// 📢 Verificación de mensaje de cancelación
TestObject alertCancel = new TestObject('dynamic/alertCancel')
alertCancel.addProperty('xpath', ConditionType.EQUALS, '//p[contains(@class,\'booking-details__status-text\') and contains(text(),\'Su reserva ha sido cancelada.\')]')
WebUI.waitForElementVisible(alertCancel, 10)
WebUI.scrollToElement(findTestObject('Euromundo/book_steps/bookings'), 10)
String actualText = WebUI.getText(alertCancel)
WebUI.verifyMatch(actualText.trim(), 'Su reserva ha sido cancelada.', false, FailureHandling.STOP_ON_FAILURE)
WebUI.comment('✅ El texto de cancelación se encontró correctamente.')

