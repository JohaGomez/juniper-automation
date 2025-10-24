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
import org.openqa.selenium.support.ui.Select as Select
import com.kms.katalon.core.testobject.ConditionType
import org.openqa.selenium.WebElement as WebElement
import com.kms.katalon.core.webui.common.WebUiCommonHelper as WebUiCommonHelper
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.time.Duration
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import groovy.json.JsonSlurper
import com.kms.katalon.core.util.KeywordUtil

// 🚪 Login
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_otn'), [:] // Si no necesitas pasarle usuario/contraseña diferentes
    , FailureHandling.STOP_ON_FAILURE)

// 📂 Navegación al menú Gran VCT Nacional
WebUI.waitForElementClickable(findTestObject('Euromundo/vamos_con_todo/repository_vct_inter/menu_vct'), 10)
WebUI.mouseOver(findTestObject('Euromundo/vamos_con_todo/repository_vct_inter/menu_vct'))
WebUI.click(findTestObject('Euromundo/vamos_con_todo/repository_vct_nal/select_vct_nal'))

// 🍪 Aceptar Cookies
WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

// 🔍 Búsqueda
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_inter'), 15)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_inter'), '38688', true)
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/select_origin_inter'), 25)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/select_origin_inter'), '35908', true)
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/origin_date_inter'), 25)
CustomKeywords.'utils.FechaUtils.setFechaAleatoriaDesdeTresMesesFuturo'('Euromundo/book_steps/origin_date_inter') //Fecha

// 👥 1. Abrir selector de habitaciones y pasajeros
CustomKeywords.'helpers.WebUIHelper.safeClick'(findTestObject('Euromundo/widget/set_rooms_pax_inter'))

// 🏨 2. Datos base de habitaciones y pasajeros
int habitaciones = 1
List<Integer> adultos = [1]
List<Integer> ninos = [2]
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

// Clic en botón de búsqueda
WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))

// 🏨 Esperar resultados o reintentar si no cargó
if (!(WebUI.waitForElementVisible(findTestObject('Euromundo/vamos_con_todo/repository_vct_nal/select_hotel_vct_nal'), 10, FailureHandling.OPTIONAL))) {
	WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_edit'), 10)
	WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))
	WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))
	WebUI.waitForElementVisible(findTestObject('Euromundo/vamos_con_todo/repository_vct_nal/select_hotel_vct_nal'), 10)
}

// ✈️ Seleccionar vuelo
WebUI.click(findTestObject('Euromundo/flights/button_flight_change_gd'))
WebUI.click(findTestObject('Euromundo/flights/checkbox_filter_airline_aerobus'))
CustomKeywords.'utils.aerolinea_selector.seleccionarVueloAerobus'()
WebUI.waitForElementVisible(findTestObject('Euromundo/flights/select_vivaerobus_flight'), 20)
WebUI.click(findTestObject('Euromundo/flights/select_vivaerobus_flight'))
WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'))
WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'))

// 👥 Datos de pasajeros
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

// Continuar con la reserva
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_prebook'))

// 🔽 Selección de pasajero responsable
TestObject paxDropdown = new TestObject('dynamicPaxSelect')
paxDropdown.addProperty('xpath', ConditionType.EQUALS, '//select[contains(@class,\'js-set-confirm-pax-data\')]')
WebElement dropdownElem = WebUiCommonHelper.findWebElement(paxDropdown, 10)
new Select(dropdownElem).selectByIndex(1)

// 🏠 Datos de contacto
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

// ✅ Aceptación de condiciones
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_importantInfo'))
WebUI.click(findTestObject('Euromundo/checkout_page/checkbox_TyC_checkout'))

// 📦 Reservar y Cancelar
WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_book'))
WebUI.click(findTestObject('Euromundo/book_steps/button_cancel_book'))

// Espera a que aparezca el alert (hasta 10 segundos)
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

