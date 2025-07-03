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
// 🔽 Aquí insertas tu bloque personalizado con WebElements
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import org.openqa.selenium.WebElement as WebElement
import org.openqa.selenium.By as By
import org.openqa.selenium.support.ui.Select as Select
import com.kms.katalon.core.testobject.ConditionType as ConditionType

// 🚪 Login
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_mxn'), [:], FailureHandling.STOP_ON_FAILURE)

// ☰ Menú Circuitos Internacionales
WebUI.waitForElementClickable(findTestObject('Euromundo/circuitos/repositpory_circuitos_inter/menu_circuitos'), 10)

WebUI.mouseOver(findTestObject('Euromundo/circuitos/repositpory_circuitos_inter/menu_circuitos'))

WebUI.click(findTestObject('Euromundo/circuitos/repositpory_circuitos_inter/select_circuitos_inter'))

// 🧳 Ingreso a buscador
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_box'), 10)

WebUI.click(findTestObject('Euromundo/book_steps/button_box'))

// 🌍 Destino y origen
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_inter'), 15)

WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_inter'), '35751', true)

WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/select_origin_inter'), '35908', true)

// 📅 Fecha
CustomKeywords.'utils.FechaUtils.setFechaAleatoriaDesdeTresMesesFuturo'('Euromundo/book_steps/origin_date_inter')

// 🔍 Buscar
WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))


// 🏨 Validar hotel disponible
boolean hotelVisible = WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_select_hotel'), 10, FailureHandling.OPTIONAL)

if (!(hotelVisible)) {
    WebUI.comment('🔁 Reintento de búsqueda por timeout...')

    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_edit'), 10)

    WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))

    WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))

    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_select_hotel'), 10)

    WebUI.click(findTestObject('Euromundo/book_steps/button_select_hotel'))
}

// ✅ Prebook
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_prebook'), 10)

WebUI.scrollToElement(findTestObject('Euromundo/book_steps/button_prebook'), 10)

WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'), FailureHandling.STOP_ON_FAILURE)

WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'))

// 🔎 Validación si aparece botón "Volver"
if (WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_comeback'), 5, FailureHandling.OPTIONAL)) {
	WebUI.comment('⚠️ No se encontraron resultados. Se procede a volver y editar búsqueda...')

	// Clic en el botón "volver"
	WebUI.click(findTestObject('Euromundo/book_steps/button_comeback'))

	// Esperar y dar clic en el botón "Editar"
	WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_edit'), 10)

	WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))

	// Setear nueva fecha aleatoria desde tres meses en el futuro
	CustomKeywords.'utils.FechaUtils.setFechaAleatoriaDesdeTresMesesFuturo'('Euromundo/book_steps/origin_date_inter')

	// Clic en botón buscar nuevamente
	WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_search_inter'), 10)

	WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))
}

// 👥 Pasajeros
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)

// PAX 1
WebUI.selectOptionByValue(findTestObject('Euromundo/pax_page/select_title_pax1_extended'), 'MR', true)

WebUI.setText(findTestObject('Euromundo/pax_page/input_name_pax1_extended'), 'Juan Daniel')

WebUI.setText(findTestObject('Euromundo/pax_page/input_surname_pax1_extended'), 'Gomez')

WebUI.setText(findTestObject('Euromundo/pax_page/input_birthday_pax1_extended'), '25/10/1990')

WebUI.setText(findTestObject('Euromundo/pax_page/set_document_extended2_pax1'), '1232434')

WebUI.setText(findTestObject('Euromundo/pax_page/set_expiration_document_pax1'), '21/09/2031')

// PAX 2
WebUI.selectOptionByValue(findTestObject('Euromundo/pax_page/select_title_pax2_extended'), 'MRS', true)

WebUI.setText(findTestObject('Euromundo/pax_page/input_name_pax2_extended'), 'Johana')

WebUI.setText(findTestObject('Euromundo/pax_page/input_surname_pax2_extended'), 'Gomez')

WebUI.setText(findTestObject('Euromundo/pax_page/input_birthday_pax2_extended'), '18/09/1995')

WebUI.setText(findTestObject('Euromundo/pax_page/set_document_extended2_pax2'), '43534234')

WebUI.setText(findTestObject('Euromundo/pax_page/set_expiration_document_pax2'), '21/09/2031')

WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_prebook'))

// 🎫 Confirmar titular reserva
TestObject paxDropdown = new TestObject('dynamicPaxSelect')

paxDropdown.addProperty('xpath', ConditionType.EQUALS, '//select[contains(@class,\'js-set-confirm-pax-data\')]')

WebElement dropdownElem = WebUiCommonHelper.findWebElement(paxDropdown, 10)

new Select(dropdownElem).selectByIndex(1)

// 🏠 Datos contacto
WebUI.setText(findTestObject('Euromundo/checkout_page/passport_booking_holder'), '102635')

// CITY
TestObject cityObj = new TestObject('dynamicCity')

cityObj.addProperty('xpath', ConditionType.EQUALS, '//input[@name=\'holder_city\']')

WebUI.waitForElementVisible(cityObj, 10)

WebUI.setText(cityObj, 'Bogotá')

// ZIPCODE
TestObject zipObj = new TestObject('dynamicZip')

zipObj.addProperty('xpath', ConditionType.EQUALS, '//input[@name=\'holder_zipcode\']')

WebUI.waitForElementVisible(zipObj, 10)

WebUI.setText(zipObj, '110111')

// ADDRESS
TestObject addrObj = new TestObject('dynamicAddress')

addrObj.addProperty('xpath', ConditionType.EQUALS, '//input[@name=\'holder_address\']')

WebUI.waitForElementVisible(addrObj, 10)

WebUI.setText(addrObj, 'Virrey')

// Teléfono
WebUI.setText(findTestObject('Euromundo/checkout_page/phone_booking_holder'), '3218111877')

// ✅ Confirmaciones
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_importantInfo'))

WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_TyC_checkout'))

// 🧾 Finalizar y cancelar
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_book'))

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

