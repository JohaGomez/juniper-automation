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
import org.openqa.selenium.WebElement as WebElement
import org.openqa.selenium.support.ui.Select as Select
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper


// 🚪 Login
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_otn'), [:] // Si no necesitas pasarle usuario/contraseña diferentes
    , FailureHandling.STOP_ON_FAILURE)

// 📂 Navegación al menú VCT Internacional
WebUI.waitForElementClickable(findTestObject('Euromundo/paquetes_carry_on/repository_pco_nal/menu_pco'), 10)
WebUI.mouseOver(findTestObject('Euromundo/paquetes_carry_on/repository_pco_nal/menu_pco'))
WebUI.click(findTestObject('Euromundo/paquetes_carry_on/repository_pco_inter/select_pco_inter'))
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_box'), 10)
WebUI.click(findTestObject('Euromundo/book_steps/button_box'))
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_inter'), 15)

// 🍪 Aceptar Cookies
WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))


// 🔍 Búsqueda
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_inter'), '29476', true)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/select_origin_inter'), '35908', true)
CustomKeywords.'utils.FechaUtils.setFechaSabadosEnTresMeses'('Euromundo/book_steps/origin_date_inter') //Fecha
WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))

// 🏨 Selección de Hotel
boolean hotelVisible = WebUI.waitForElementVisible(findTestObject('Euromundo/vamos_con_todo/repository_vct_nal/select_hotel_vct_nal'), 
    10, FailureHandling.OPTIONAL)

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
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_billing'))
WebUI.setText(findTestObject('Euromundo/checkout_page/set_name_billing_info'), 'Johana Gomez')
WebUI.setText(findTestObject('Euromundo/checkout_page/set_email_billing_info'), 'Johana.gomez@ejuniper.com')
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_fare_breakdown'))
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_TyC_checkout'))

// 📦 Reservar y Cancelar
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_book'))
WebUI.click(findTestObject('Euromundo/book_steps/button_cancel_prebook'))

//Espera a que aparezca el alert (hasta 10 segundos)
WebUI.waitForAlert(10)

// Acepta el alert (equivale a hacer clic en “Aceptar”)
WebUI.acceptAlert()

// 📢 Verificación de mensaje de cancelación
TestObject alertCancel = new TestObject('dynamic/alertCancel')
alertCancel.addProperty('xpath', ConditionType.EQUALS, '//p[contains(@class,\'booking-details__status-text\') and contains(text(),\'Su reserva ha sido cancelada.\')]')
// 2. Esperar que el mensaje sea visible
WebUI.waitForElementVisible(alertCancel, 10)
// 3. Scroll hacia la sección de reservas (si es necesario)
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/bookings'), 10)
WebUI.scrollToElement(findTestObject('Euromundo/book_steps/bookings'), 10)
// 4. Obtener texto real y validarlo
String actualText = WebUI.getText(alertCancel)
WebUI.verifyMatch(actualText.trim(), 'Su reserva ha sido cancelada.', false, FailureHandling.STOP_ON_FAILURE)
WebUI.comment('✅ El texto de cancelación se encontró correctamente.')

