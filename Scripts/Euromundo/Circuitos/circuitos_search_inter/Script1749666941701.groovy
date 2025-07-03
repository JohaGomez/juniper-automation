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
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_otn'), [:], FailureHandling.STOP_ON_FAILURE)

// ☰ Menú Circuitos
WebUI.waitForElementClickable(findTestObject('Euromundo/circuitos/repositpory_circuitos_inter/menu_circuitos'), 10)

WebUI.mouseOver(findTestObject('Euromundo/circuitos/repositpory_circuitos_inter/menu_circuitos'))

WebUI.click(findTestObject('Euromundo/circuitos/repositpory_circuitos_inter/select_circuitos_inter'))

// 📦 Criterios de búsqueda
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_box'), 10)

WebUI.click(findTestObject('Euromundo/book_steps/button_box'))

WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_inter'), 15)

WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_inter'), '35751', true)

WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/select_origin_inter'), '35908', true)

CustomKeywords.'utils.FechaUtils.setFechaAleatoriaDesdeTresMesesFuturo'('Euromundo/book_steps/origin_date_inter')

// 🔍 Buscar resultados
WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))

// 🏨 Verificar disponibilidad
boolean hotelVisible = WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_select_hotel'), 10, FailureHandling.OPTIONAL)

if (!(hotelVisible)) {
    WebUI.comment('🔄 Reintentando búsqueda tras editar')

    WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_edit'), 10)

    WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))

    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/origin_date_inter'), 10)

    CustomKeywords.'utils.FechaUtils.setFechaAleatoriaDesdeTresMesesFuturo'('Euromundo/book_steps/origin_date_inter')

    WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))

    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_select_hotel'), 10)
}

WebUI.click(findTestObject('Euromundo/book_steps/button_select_hotel'))

// 🧾 Buscar fila refundable y agregar carrito
try {
    List<WebElement> resultados = WebUiCommonHelper.findWebElements(findTestObject('divresultados'), 30)

    WebElement fila = resultados.find({ 
            it.getAttribute('data-refundable').equalsIgnoreCase('true')
        })

    if (fila != null) {
        fila.findElement(By.xpath('.//button[contains(@class,\'miBoton\')]')).click()

        WebUI.comment('✅ Hotel refundable agregado al carrito')
    } else {
        WebUI.comment('⚠️ No se encontró hotel refundable')
    }
}
catch (Exception e) {
    WebUI.comment("❌ Error al buscar fila refundable: $e.message")
} 

// ✅ Pre-booking
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_prebook'), 10)

WebUI.scrollToElement(findTestObject('Euromundo/book_steps/button_prebook'), 10)

WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'))

// 🔄 Si no finaliza, ajustar fecha regreso
boolean prebookVisible = WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 
    10, FailureHandling.OPTIONAL)

if (!(prebookVisible)) {
    WebUI.comment('🔁 Ajustando fecha regreso y reintentando prebook')

    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_comeback'), 10)

    WebUI.click(findTestObject('Euromundo/book_steps/button_comeback'))

    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_edit'), 10)

    WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))

    CustomKeywords.'utils.FechaUtils.setFechaAleatoriaDesdeTresMesesFuturo'('Euromundo/book_steps/origin_date_inter')

    WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))

    WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'))
}

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

// 👥 Datos pasajeros
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)

WebUI.selectOptionByValue(findTestObject('Euromundo/pax_page/select_title_pax1_extended'), 'MR', true)

WebUI.setText(findTestObject('Euromundo/pax_page/input_name_pax1_extended'), 'Juan Daniel')

WebUI.setText(findTestObject('Euromundo/pax_page/input_surname_pax1_extended'), 'Gomez')

WebUI.setText(findTestObject('Euromundo/pax_page/input_birthday_pax1_extended'), '25/10/1990')

WebUI.setText(findTestObject('Euromundo/pax_page/set_document_new_pax1'), '1232434')

WebUI.setText(findTestObject('Euromundo/pax_page/set_expiration_document_new_pax1'), '21/09/2031')

WebUI.selectOptionByValue(findTestObject('Euromundo/pax_page/select_title_pax2_extended'), 'MRS', true)

WebUI.setText(findTestObject('Euromundo/pax_page/input_name_pax2_extended'), 'Johana')

WebUI.setText(findTestObject('Euromundo/pax_page/input_surname_pax2_extended'), 'Gomez')

WebUI.setText(findTestObject('Euromundo/pax_page/input_birthday_pax2_extended'), '18/09/1995')

WebUI.setText(findTestObject('Euromundo/pax_page/set_document_new_pax2'), '43534234')

WebUI.setText(findTestObject('Euromundo/pax_page/set_expiration_document_new_pax2'), '21/09/2031')

WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_prebook'))

// 🧍 Datos de contacto - Titular
TestObject paxDropdown = new TestObject('paxDropdown')

paxDropdown.addProperty('xpath', ConditionType.EQUALS, '//select[contains(@class,\'js-set-confirm-pax-data\')]')

Select dropdown = new Select(WebUiCommonHelper.findWebElement(paxDropdown, 10))

dropdown.selectByIndex(1)

WebUI.setText(findTestObject('Euromundo/checkout_page/passport_booking_holder'), '102635')

TestObject city = new TestObject().addProperty('xpath', ConditionType.EQUALS, '//input[@name="holder_city"]')

TestObject zip = new TestObject().addProperty('xpath', ConditionType.EQUALS, '//input[@name="holder_zipcode"]')

TestObject address = new TestObject().addProperty('xpath', ConditionType.EQUALS, '//input[@name="holder_address"]')

WebUI.waitForElementVisible(city, 10)

WebUI.setText(city, 'Bogota')

WebUI.setText(zip, '110111')

WebUI.setText(address, 'Virrey')

WebUI.setText(findTestObject('Euromundo/checkout_page/phone_booking_holder'), '3218111877')

// ✅ Checkboxes y confirmación
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_importantInfo'))

WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_TyC_checkout'))

WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_book'))

WebUI.click(findTestObject('Euromundo/book_steps/button_cancel_book'))

// ❗ Confirmar cancelación
WebUI.waitForAlert(10)

WebUI.acceptAlert()

TestObject alertCancel = new TestObject('alertCancel')

alertCancel.addProperty('xpath', ConditionType.EQUALS, '//p[contains(@class,"booking-details__status-text") and contains(text(),"Su reserva ha sido cancelada.")]')

WebUI.waitForElementVisible(alertCancel, 10)

WebUI.scrollToElement(findTestObject('Euromundo/book_steps/bookings'), 10)

String actualText = WebUI.getText(alertCancel)

WebUI.verifyMatch(actualText.trim(), 'Su reserva ha sido cancelada.', false)

WebUI.comment('✅ La reserva fue cancelada exitosamente.')

