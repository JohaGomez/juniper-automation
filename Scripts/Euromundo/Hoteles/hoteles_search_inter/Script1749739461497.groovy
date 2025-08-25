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
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil

// 🚪 Login
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_otn'), [:], FailureHandling.STOP_ON_FAILURE)


// ☰ Menú Hoteles Internacionales
WebUI.waitForElementClickable(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/menu_hoteles'), 10)
WebUI.mouseOver(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/menu_hoteles'))
WebUI.click(findTestObject('Euromundo/módulo_hoteles/repository_hoteles_inter/select_hoteles_inter'))


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
CustomKeywords.'utils.FechaUtils.setFechasIdaYRegreso'(
    'Euromundo/hoteles/repository_hoteles_inter/input_date_origin_hoteles',
    'Euromundo/hoteles/repository_hoteles_inter/input_date_destination_hoteles'
)


// 🔍 Buscar hoteles
WebUI.click(findTestObject('Euromundo/book_steps/button_search_hotels'))


// 🏨 Validar disponibilidad hotel
boolean hotelVisible = WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_reservar_hoteles'), 10, FailureHandling.OPTIONAL)

if (!hotelVisible) {
    WebUI.comment('🔄 Reintentando búsqueda tras editar')

    WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_edit'), 10)
    WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))
    WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))
    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_reservar_hoteles'), 10)
}

WebUI.scrollToElement(findTestObject('Euromundo/book_steps/button_reservar_hoteles'), 10)
WebUI.click(findTestObject('Euromundo/book_steps/button_reservar_hoteles'))


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
WebUI.selectOptionByValue(findTestObject('Euromundo/pax_page/select_title_hotel_pax1'), 'MR', true)
WebUI.setText(findTestObject('Euromundo/pax_page/input_name_hotel_pax1'), 'Juan Daniel')
WebUI.setText(findTestObject('Euromundo/pax_page/input_surname_hotel_pax1'), 'Gomez')

WebUI.selectOptionByValue(findTestObject('Euromundo/pax_page/select_title_hotel_pax2'), 'MRS', true)
WebUI.setText(findTestObject('Euromundo/pax_page/input_name_hotel_pax2'), 'Johana')
WebUI.setText(findTestObject('Euromundo/pax_page/input_surname_hotel_pax2'), 'Gomez')


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


// 🧾 Facturación
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_billing'), FailureHandling.STOP_ON_FAILURE)
WebUI.setText(findTestObject('Euromundo/checkout_page/set_name_billing_info'), 'Johana Gomez')
WebUI.setText(findTestObject('Euromundo/checkout_page/set_email_billing_info'), 'johana.gomez@ejuniper.com')


// ✅ Confirmaciones
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_fare_breakdown'))
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_importantInfo'))
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

