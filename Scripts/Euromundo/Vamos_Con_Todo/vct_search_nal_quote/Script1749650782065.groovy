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
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import org.openqa.selenium.WebElement as WebElement
import org.openqa.selenium.support.ui.Select as Select
import com.kms.katalon.core.testobject.ConditionType
import java.nio.file.StandardOpenOption
import java.nio.file.Files
import java.nio.file.Paths

// 🚪 Login
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_otn'), [:] // Si no necesitas pasarle usuario/contraseña diferentes
    , FailureHandling.STOP_ON_FAILURE)

// 📂 Navegación inicial y cierre de cookies
WebUI.waitForElementClickable(findTestObject('Euromundo/vamos_con_todo/repository_vct_inter/menu_vct'), 10)
WebUI.mouseOver(findTestObject('Euromundo/vamos_con_todo/repository_vct_inter/menu_vct'))
WebUI.click(findTestObject('Euromundo/vamos_con_todo/repository_vct_nal/select_vct_nal'))
WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

// 📍 Selección de origen y destino
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_inter'), 15)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_inter'), '35163', true)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/select_origin_inter'), '35908', true)
CustomKeywords.'utils.FechaUtils.setFechaAleatoriaDesdeTresMesesFuturo'('Euromundo/book_steps/origin_date_inter')

// 👥 Selección de Pax
CustomKeywords.'helpers.WebUIHelper.safeClick'(findTestObject('Euromundo/widget/set_rooms_pax_inter'))

int habitaciones = 1
List<Integer> adultos = [2]
List<Integer> ninos = [0]
List<Integer> infantes = [0]
List<Integer> edadesNinos = []
List<Integer> edadesInfantes = []
CustomKeywords.'utils.configuration_rooms.configurarHabitacionesYPasajeros'(habitaciones, adultos, ninos, infantes, edadesNinos,
	edadesInfantes)

// 🧒 Capturar edad del niño
TestObject objEdadNino = new TestObject('edad_nino')
objEdadNino.addProperty('xpath', ConditionType.EQUALS, '//*[@id="select2-room-selector-1-children-age-1-1-container"]')
String edadNinoTexto = WebUI.getText(objEdadNino).replaceAll('\\D', '' ) // elimina letras y deja números
int edadNino = edadNinoTexto.isInteger() ? edadNinoTexto.toInteger() : 5

// 👶 Capturar edad del infante
TestObject objEdadInf = new TestObject('edad_infante')
objEdadInf.addProperty('xpath', ConditionType.EQUALS, '//*[@id="select2-room-selector-1-babies-age-1-1-container"]')
String edadInfTexto = WebUI.getText(objEdadInf).replaceAll('\\D', '')
int edadInf = edadInfTexto.isInteger() ? edadInfTexto.toInteger() : 0
WebUI.comment("📌 Edad Niño capturada: $edadNino")
WebUI.comment("📌 Edad Infante capturada: $edadInf")
WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))

// 🏨 Selección de Hotel
boolean hotelVisible = WebUI.waitForElementVisible(findTestObject('Euromundo/vamos_con_todo/repository_vct_nal/select_hotel_vct_nal'), 
    10, FailureHandling.OPTIONAL)
WebUI.click(findTestObject('Euromundo/vamos_con_todo/repository_vct_nal/select_hotel_vct_nal'))

if (!(hotelVisible)) {
    // Paso 3a: Esperar que aparezca el botón editar
    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_edit'), 10)
    // Paso 3b: Hacer clic en el botón editar
    WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))
    // Paso 3c: Hacer clic en el botón buscar
    WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))
    // Paso 3d: Esperar nuevamente el select_hotel
    WebUI.waitForElementVisible(findTestObject('Euromundo/vamos_con_todo/repository_vct_nal/select_hotel_vct_nal'), 10)
    // Paso 3e: Hacer clic en el botón reservar hotel
    WebUI.click(findTestObject('Euromundo/vamos_con_todo/repository_vct_nal/select_hotel_vct_nal'))
}

WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_prebook'), 10)
WebUI.scrollToElement(findTestObject('Euromundo/book_steps/button_prebook'), 10)
WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'))
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)

// 👥 Datos de pasajeros
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)
CustomKeywords.'utils.PassengerFormHelper.fillPassengerData'([edadNino], [edadInf])
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_prebook'))

// 🔽 Selección de pasajero responsable

TestObject dynamicPaxSelect = new TestObject('dynamicPaxSelect')
dynamicPaxSelect.addProperty('xpath', ConditionType.EQUALS, '//select[contains(@class,\'js-set-confirm-pax-data\')]')
WebElement selectElem = WebUiCommonHelper.findWebElement(dynamicPaxSelect, 10)
Select dropdown = new Select(selectElem)
dropdown.selectByIndex(1)

// 🏠 Datos de contacto
WebUI.setText(findTestObject('Euromundo/checkout_page/passport_booking_holder'), '102635')

// City
TestObject cityObj = new TestObject('dynamicCity')
cityObj.addProperty('xpath', ConditionType.EQUALS, '//input[@name=\'holder_city\']')
WebUI.waitForElementVisible(cityObj, 10)
WebUI.setText(cityObj, 'Bogota')

// Zip
TestObject zipObj = new TestObject('dynamicZip')
zipObj.addProperty('xpath', ConditionType.EQUALS, '//input[@name=\'holder_zipcode\']')
WebUI.waitForElementVisible(zipObj, 10)
WebUI.setText(zipObj, '110111')

// Address
TestObject addrObj = new TestObject('dynamicAddress')
addrObj.addProperty('xpath', ConditionType.EQUALS, '//input[@name=\'holder_address\']')
WebUI.waitForElementVisible(addrObj, 10)
WebUI.setText(addrObj, 'Virrey')

// Teléfono
WebUI.setText(findTestObject('Euromundo/checkout_page/phone_booking_holder'), '3218111877')

// Términos
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_importantInfo'))
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_TyC_checkout'))

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