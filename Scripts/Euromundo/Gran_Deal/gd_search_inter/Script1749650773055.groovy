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
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Select
import com.kms.katalon.core.webui.common.WebUiCommonHelper

// 🚪 Login
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_otn'), [:], FailureHandling.STOP_ON_FAILURE)

// 📂 Navegación al menú Gran Deal Internacional
WebUI.waitForElementClickable(findTestObject('Euromundo/Gran_Deal/repository_GD_Inter/menu_gd'), 10)
WebUI.mouseOver(findTestObject('Euromundo/Gran_Deal/repository_GD_Inter/menu_gd'))
WebUI.click(findTestObject('Euromundo/Gran_Deal/repository_GD_Inter/select_international'))

// 🔍 Búsqueda de hoteles
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_box'), 10)
WebUI.click(findTestObject('Euromundo/book_steps/button_box'))
WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_inter'), 15)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_inter'), '184911', true)  // Destino
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/select_origin_inter'), '35908', true)       // Origen

CustomKeywords.'utils.FechaUtils.setFechaViernesEnTresMeses'('Euromundo/book_steps/origin_date_inter')

// Clic en botón de búsqueda
WebUI.click(findTestObject('Euromundo/Gran_Deal/repository_GD_Inter/button_search_inter'))

// 🏨 Esperar resultados o reintentar si no cargó
if (!WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_prebook_gd'), 10, FailureHandling.OPTIONAL)) {
    WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_edit'), 10)
    WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))
    WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))
    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_prebook_gd'), 10)
}
WebUI.click(findTestObject('Euromundo/book_steps/button_prebook_gd'))

// 👥 Datos de pasajeros
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)

// Pax 1
WebUI.selectOptionByValue(findTestObject('Euromundo/pax_page/select_title_pax1'), 'MR', false)
WebUI.setText(findTestObject('Euromundo/pax_page/input_name_pax1'), 'Juan Daniel')
WebUI.setText(findTestObject('Euromundo/pax_page/input_surname_pax1'), 'Gomez')
WebUI.setText(findTestObject('Euromundo/pax_page/input_birthday_pax1'), '25/10/1990')

// Pax 2
WebUI.selectOptionByValue(findTestObject('Euromundo/pax_page/select_title_pax2'), 'MRS', true)
WebUI.setText(findTestObject('Euromundo/pax_page/input_name_pax2'), 'Johana')
WebUI.setText(findTestObject('Euromundo/pax_page/input_surname_pax2'), 'Gomez')
WebUI.setText(findTestObject('Euromundo/pax_page/input_birthday_pax2'), '18/09/1995')

WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_prebook'))

// 🔽 Selección de pasajero responsable
TestObject paxDropdown = new TestObject('dynamicPaxSelect')
paxDropdown.addProperty('xpath', ConditionType.EQUALS, "//select[contains(@class,'js-set-confirm-pax-data')]")
WebElement selectElem = WebUiCommonHelper.findWebElement(paxDropdown, 10)
new Select(selectElem).selectByIndex(1)

// 🏠 Datos de contacto
WebUI.setText(findTestObject('Euromundo/checkout_page/passport_booking_holder'), '102635')

TestObject cityObj = new TestObject('dynamicCity')
cityObj.addProperty('xpath', ConditionType.EQUALS, "//input[@name='holder_city']")
WebUI.waitForElementVisible(cityObj, 10)
WebUI.setText(cityObj, 'Bogota')

TestObject zipObj = new TestObject('dynamicZip')
zipObj.addProperty('xpath', ConditionType.EQUALS, "//input[@name='holder_zipcode']")
WebUI.waitForElementVisible(zipObj, 10)
WebUI.setText(zipObj, '110111')

TestObject addrObj = new TestObject('dynamicAddress')
addrObj.addProperty('xpath', ConditionType.EQUALS, "//input[@name='holder_address']")
WebUI.waitForElementVisible(addrObj, 10)
WebUI.setText(addrObj, 'Virrey')

WebUI.setText(findTestObject('Euromundo/checkout_page/phone_booking_holder'), '3218111877')

// ✅ Aceptación de condiciones
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_importantInfo'))
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_TyC_checkout'))

// 💾 Finalizar reserva
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_book'))

// ❌ Cancelación de reserva
WebUI.click(findTestObject('Euromundo/book_steps/button_cancel_book'))
WebUI.waitForAlert(10)
WebUI.acceptAlert()

// 📢 Verificación de mensaje de cancelación
TestObject alertCancel = new TestObject('dynamic/alertCancel')
alertCancel.addProperty('xpath', ConditionType.EQUALS, "//p[contains(@class,'booking-details__status-text') and contains(text(),'Su reserva ha sido cancelada.')]")

WebUI.waitForElementVisible(alertCancel, 10)
WebUI.scrollToElement(findTestObject('Euromundo/book_steps/bookings'), 10)

String actualText = WebUI.getText(alertCancel)
WebUI.verifyMatch(actualText.trim(), 'Su reserva ha sido cancelada.', false, FailureHandling.STOP_ON_FAILURE)

WebUI.comment('✅ El texto de cancelación se encontró correctamente.')

