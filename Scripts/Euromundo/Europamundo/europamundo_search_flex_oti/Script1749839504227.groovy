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

WebUI.callTestCase(findTestCase('Euromundo/Login/Login_oti'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.waitForElementClickable(findTestObject('Euromundo/europamundo/repository_europamundo/menu_europamundo'), 10)

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/menu_europamundo'))

WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

WebUI.waitForElementClickable(findTestObject('Euromundo/europamundo/repository_europamundo/input_city'), 15)

WebUI.selectOptionByValue(findTestObject('Euromundo/europamundo/repository_europamundo/input_city'), '29476', true)

CustomKeywords.'utils.FechaUtils.selectMesAleatorioDesdeTresMesesFuturo'('Euromundo/europamundo/repository_europamundo/input_date_package')

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/button_search_package'))

// Paso 2: Esperar hasta 10 segundos por el elemento select_hotel
boolean hotelVisible = WebUI.waitForElementVisible(findTestObject('Euromundo/europamundo/repository_europamundo/select_package'), 
    10, FailureHandling.OPTIONAL)

if (!(hotelVisible)) {
    // Paso 3a: Esperar que aparezca el botón editar
    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_edit'), 10)

    // Paso 3b: Hacer clic en el botón editar
    WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))

    // Paso 3c: Hacer clic en el botón buscar
    WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))

    // Paso 3d: Esperar nuevamente el select_hotel
    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_prebook_gd'), 10)

    // Paso 3e: Hacer clic en el botón reservar hotel
    WebUI.click(findTestObject('Euromundo/book_steps/button_prebook_gd'))
}

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/select_package'))

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/button_quote1'))

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/button_book'))

WebUI.selectOptionByValue(findTestObject('Euromundo/europamundo/repository_europamundo/input_arrival_transfers_oti'), 'C2EE3F4E7262FE61CD0C3D39B8CA8A5A', 
    true)

WebUI.selectOptionByValue(findTestObject('Euromundo/europamundo/repository_europamundo/input_departure_transfers_oti'), 
    '96635C11AA7CBC69DC03854224814561', true)

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/button_quote2_walm'))

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/button_continue'))

WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)

WebUI.selectOptionByValue(findTestObject('Euromundo/pax_page/select_title_pax1'), 'MR', false)

WebUI.setText(findTestObject('Euromundo/pax_page/input_name_pax1'), 'Juan Daniel')

WebUI.setText(findTestObject('Euromundo/pax_page/input_surname_pax1'), 'Gomez')

WebUI.setText(findTestObject('Euromundo/pax_page/input_birthday_pax1_extended'), '25/10/1990')

WebUI.setText(findTestObject('Euromundo/pax_page/set_document_extended_pax1'), '1232434')

WebUI.setText(findTestObject('Euromundo/pax_page/set_expiration_document_extended_pax1'), '21/09/2031')

// Pax 2
WebUI.selectOptionByValue(findTestObject('Euromundo/pax_page/select_title_pax2'), 'MRS', true)

WebUI.setText(findTestObject('Euromundo/pax_page/input_name_pax2'), 'Johana')

WebUI.setText(findTestObject('Euromundo/pax_page/input_surname_pax2'), 'Gomez')

WebUI.setText(findTestObject('Euromundo/pax_page/input_birthday_pax2_extended'), '18/09/1995')

WebUI.setText(findTestObject('Euromundo/pax_page/set_document_extended_pax2'), '43534234')

WebUI.setText(findTestObject('Euromundo/pax_page/set_expiration_document_extended_pax2'), '21/09/2031')

WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_prebook'))

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

alertCancel.addProperty('xpath', ConditionType.EQUALS, '//p[contains(@class,\'booking-details__status-text\') and contains(text(),\'Su reserva ha sido cancelada.\')]')

WebUI.waitForElementVisible(alertCancel, 10)

WebUI.scrollToElement(findTestObject('Euromundo/book_steps/bookings'), 10)

String actualText = WebUI.getText(alertCancel)

WebUI.verifyMatch(actualText.trim(), 'Su reserva ha sido cancelada.', false, FailureHandling.STOP_ON_FAILURE)

WebUI.comment('✅ El texto de cancelación se encontró correctamente.')

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/button_quote2'))

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/button_continue'))

WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)

