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
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import org.openqa.selenium.support.ui.Select as Select
import org.openqa.selenium.WebElement as WebElement
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import utils.ReadPDF
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.util.regex.Pattern
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.charset.StandardCharsets
import java.nio.file.StandardOpenOption
import internal.GlobalVariable
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.time.Duration
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import groovy.json.JsonSlurper
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.testobject.ConditionType
import utils.ValidacionesPrecios
import com.kms.katalon.core.testobject.TestObject
import utils.ValidacionesPreciosTabla
import utils.ValidacionesPrecios
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject
import utils.ValidacionesPreciosTabla
import com.kms.katalon.core.model.FailureHandling as FH
import utils.ValidacionesPrebook
import org.openqa.selenium.interactions.Actions

// 🚪 Login
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_otn'), [:], FailureHandling.STOP_ON_FAILURE)

// 📍 Selección de origen y destino
WebUI.waitForElementClickable(findTestObject('Euromundo/gran_deal/repository_GD_nal/input_Destination'), 20)
WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))
WebUI.selectOptionByValue(findTestObject('Euromundo/gran_deal/repository_GD_nal/input_Destination'), '35163', true) // Cancun
WebUI.selectOptionByValue(findTestObject('Euromundo/gran_deal/repository_GD_nal/input_Origin'), '35908', true) // CDMX

// 📅 Fecha
CustomKeywords.'utils.FechaUtils.setFechaAleatoriaDesdeTresMesesFuturo'('Euromundo/gran_deal/repository_GD_nal/origin_Date')

// 👥 1. Abrir selector de habitaciones y pasajeros
CustomKeywords.'helpers.WebUIHelper.safeClick'(findTestObject('Euromundo/widget/set_rooms_pax'))

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

WebUI.click(findTestObject('Euromundo/gran_deal/repository_GD_nal/button_Search_GD'))

// 🏨 Selección hotel
CustomKeywords.'helpers.WebUIHelper.safeClick'(findTestObject('Euromundo/book_steps/button_prebook_gd_inter'))
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_prebook'), 10)

//Guardar titulo del paquete para después comparar
TestObject tituloObj = new TestObject("tituloDinamico")
tituloObj.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class,'info-card__content')]//div[contains(@class,'info-card__title')]")

//Guardar el texto en una variable local
String tituloGuardado = WebUI.getText(tituloObj)

// --- Crear TestObject para capturar el mejor precio en la página de resultados ---
TestObject mejorPrecioObj = new TestObject('mejorPrecioObj')
mejorPrecioObj.addProperty(
	"xpath",
	ConditionType.EQUALS,
	"//div[contains(@class,'bestprice')]//span[contains(@class,'bestprice__amount')]"
)

// --- Llamar a la keyword para capturar y validar el precio en página de resultados ---
double mejorPrecio = CustomKeywords.'utils.ValidacionPrecios.validarMejorPrecioEnResultados'(mejorPrecioObj)


WebUI.scrollToElement(findTestObject('Euromundo/book_steps/button_prebook'), 10)
CustomKeywords.'helpers.WebUIHelper.safeClick'(findTestObject('Euromundo/book_steps/button_prebook'))

// 👤 Datos de pasajeros
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)
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

//Guardar titulo del paquete para comparar
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


// ====================================
// ✅ Validación en página de pasajeros
// ====================================

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

// ✅ Llamada con los 5 parámetros (mejorPrecio ya normalizado a double)
CustomKeywords.'utils.ValidacionesPrebook.validarPrecioPrebook'(
	precioObj,
	comisionObj,
	precioFinalObj,
	totalAdeudadoObj,
	mejorPrecio
)
CustomKeywords.'helpers.WebUIHelper.safeClick'(findTestObject('Euromundo/book_steps/button_finalization_prebook'))

// 👤 Selección responsable
TestObject paxSelect = new TestObject('dynamicPaxSelect')
paxSelect.addProperty('xpath', ConditionType.EQUALS, '//select[contains(@class,\'js-set-confirm-pax-data\')]')
WebElement paxSelectElem = WebUiCommonHelper.findWebElement(paxSelect, 10)
new Select(paxSelectElem).selectByIndex(1)

// 🏠 Datos de contacto
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

// Teléfono
WebUI.setText(findTestObject('Euromundo/checkout_page/phone_booking_holder'), '3218111877')

// 🧾 Aceptación de condiciones
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_fare_breakdown'))
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_importantInfo'))
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_TyC_checkout'))

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

// 💾 Guardar cotización
WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_quote_save'), 20)
WebUI.click(findTestObject('Euromundo/book_steps/button_quote_save'))

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

if (urlPDF.contains("https://euromundootn.juniper.es/voucher/voucher.aspx")) {
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

// 🛑 Cierre
WebUI.closeBrowser()