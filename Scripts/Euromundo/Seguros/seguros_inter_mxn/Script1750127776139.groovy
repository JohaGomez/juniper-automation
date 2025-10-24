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
import org.openqa.selenium.By as By
import org.openqa.selenium.support.ui.Select as Select
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.testobject.ObjectRepository as OR

WebUI.callTestCase(findTestCase('Euromundo/Login/Login_mxn'), [:] // Si no necesitas pasarle usuario/contraseña diferentes
    , FailureHandling.STOP_ON_FAILURE)

WebUI.waitForElementClickable(findTestObject('Euromundo/seguros/repository_seguros_nal/menu_seguros'), 10)

WebUI.mouseOver(findTestObject('Euromundo/seguros/repository_seguros_nal/menu_seguros'))

WebUI.click(findTestObject('Euromundo/seguros/repository_seguros_inter/select_seguros_inter'))

WebUI.click(findTestObject('Euromundo/book_steps/button_box'))

WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_insurance'), 15)

WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_insurance'), '29476', true)

CustomKeywords.'utils.FechaUtils.setFechasIdaYRegreso'('Euromundo/seguros/repository_seguros_nal/input_date_start_insurance', 
    'Euromundo/seguros/repository_seguros_nal/input_date_end_insurance')

WebUI.click(findTestObject('Euromundo/seguros/repository_seguros_nal/button_insurances'))

// Paso 2: Esperar hasta 10 segundos por el elemento select_hotel
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

WebUI.selectOptionByValue(findTestObject('Euromundo/pax_page/select_title_seguros_pax1'), 'MR', true)

WebUI.setText(findTestObject('Euromundo/pax_page/input_name_basic_pax1'), 'Juan Daniel')

WebUI.setText(findTestObject('Euromundo/pax_page/input_surname_basic_pax1'), 'Gomez')

// 👩 PAX 2
WebUI.selectOptionByValue(findTestObject('Euromundo/pax_page/select_title_seguros_pax2'), 'MRS', true)

WebUI.setText(findTestObject('Euromundo/pax_page/input_name_basic_pax2'), 'Johana')

WebUI.setText(findTestObject('Euromundo/pax_page/input_surname_basic_pax2'), 'Gomez')

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

WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_importantInfo'))

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

