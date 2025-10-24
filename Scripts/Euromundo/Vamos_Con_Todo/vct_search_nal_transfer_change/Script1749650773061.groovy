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
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.model.FailureHandling

// 🚪 Login
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_otn'), [:], FailureHandling.STOP_ON_FAILURE)

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

// 🔄 Cambio de Transfer
// Crear el objeto dinámico
TestObject btnCambiarTransfer = new TestObject('dynamicBtnTransferChange')
btnCambiarTransfer.addProperty('xpath', ConditionType.EQUALS, "//button[@data-product='transfer' and contains(@class,'result-option__change-button')]")

// Validar si existe y hacer clic
if (WebUI.verifyElementPresent(btnCambiarTransfer, 10, FailureHandling.OPTIONAL)) {
	WebUI.waitForElementClickable(btnCambiarTransfer, 10)
	WebUI.click(btnCambiarTransfer)
	WebUI.comment("✅ Botón 'Cambiar transfer' clicado correctamente.")
} else {
	WebUI.comment("⚠️ El botón 'Cambiar transfer' no está presente.")
}

// 🚌 Selección del nuevo transfer
WebUI.click(findTestObject('Euromundo/transfers/select_transfer_change'))
WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'))
WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'))

// 🔎 Validación si aparece botón "Volver"
if (WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_comeback'), 5, FailureHandling.OPTIONAL)) {
	WebUI.comment('⚠️ No se encontraron resultados. Se procede a volver y editar búsqueda...')
	WebUI.click(findTestObject('Euromundo/book_steps/button_comeback'))
	WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_edit'), 10)
	WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))
	CustomKeywords.'utils.FechaUtils.setFechaAleatoriaDesdeTresMesesFuturo'('Euromundo/book_steps/origin_date_inter')
	WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_search_inter'), 10)
	WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))
}

// 👤 Datos de pasajeros
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)
CustomKeywords.'utils.PassengerFormHelper.fillPassengerData'([edadNino], [edadInf])
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
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_importantInfo'))
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_TyC_checkout'))

// ✅ Finalizar reserva
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_book'))

// ❌ Cancelar reserva
WebUI.click(findTestObject('Euromundo/book_steps/button_cancel_book'))
WebUI.waitForAlert(10)
WebUI.acceptAlert()

// 📢 Validación mensaje de cancelación
TestObject alertCancel = new TestObject('dynamic/alertCancel')
alertCancel.addProperty('xpath', ConditionType.EQUALS, "//p[contains(@class,'booking-details__status-text') and contains(text(),'Su reserva ha sido cancelada.')]")

WebUI.waitForElementVisible(alertCancel, 10)
WebUI.scrollToElement(findTestObject('Euromundo/book_steps/bookings'), 10)
String actualText = WebUI.getText(alertCancel)

WebUI.verifyMatch(actualText.trim(), 'Su reserva ha sido cancelada.', false, FailureHandling.STOP_ON_FAILURE)
WebUI.comment('✅ El texto de cancelación se encontró correctamente.')