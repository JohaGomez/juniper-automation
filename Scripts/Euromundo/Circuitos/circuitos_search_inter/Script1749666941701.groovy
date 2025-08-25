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
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.time.Duration
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import groovy.json.JsonSlurper
import com.kms.katalon.core.util.KeywordUtil

// 🚪 Login
WebUI.callTestCase(findTestCase('Euromundo/Login/Login_otn'), [:], FailureHandling.STOP_ON_FAILURE)

// ☰ Menú Circuitos
WebUI.waitForElementClickable(findTestObject('Euromundo/circuitos/repository_circuitos_inter/menu_circuitos'), 10)
WebUI.mouseOver(findTestObject('Euromundo/circuitos/repository_circuitos_inter/menu_circuitos'))
WebUI.click(findTestObject('Euromundo/circuitos/repository_circuitos_inter/select_circuitos_inter'))

// 📦 Criterios de búsqueda
TestObject buttonBox = findTestObject('Euromundo/book_steps/button_box')

if (WebUI.waitForElementPresent(buttonBox, 10)) {
    WebUI.waitForElementVisible(buttonBox, 5)
    WebUI.waitForElementClickable(buttonBox, 5)
    WebUI.click(buttonBox)
} else {
    WebUI.comment("❌ No se encontró el botón 'button_box' dentro del tiempo de espera.")
    WebUI.takeScreenshot()
    assert false : "Elemento 'button_box' no encontrado en DOM"
}

// 🍪 Aceptar Cookies
WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

// 🔍 búsqueda
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_inter'), 15)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_inter'), '35751', true)
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/select_origin_inter'), 15)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/select_origin_inter'), '35908', true)
CustomKeywords.'utils.FechaUtils.setFechaAleatoriaDesdeTresMesesFuturo'('Euromundo/book_steps/origin_date_inter') //Fecha

// 👥 1. Abrir selector de habitaciones y pasajeros
CustomKeywords.'helpers.WebUIHelper.safeClick'(findTestObject('Euromundo/widget/set_rooms_pax_inter'))

// 🏨 2. Datos base de habitaciones y pasajeros
int habitaciones = 1
List<Integer> adultos = [2]
List<Integer> ninos = [0]
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

WebUI.click(findTestObject('Euromundo/book_steps/button_finalization_prebook'))

// 🔽 Selección de pasajero responsable
TestObject paxDropdown = new TestObject('dynamicPaxSelect')
paxDropdown.addProperty('xpath', ConditionType.EQUALS, '//select[contains(@class,\'js-set-confirm-pax-data\')]')
WebElement dropdownElem = WebUiCommonHelper.findWebElement(paxDropdown, 10)
new Select(dropdownElem).selectByIndex(1)

// 🏠 Datos de contacto
WebUI.setText(findTestObject('Euromundo/checkout_page/passport_booking_holder'), '102635')

Map<String, String> contactFields = [('holder_city') : 'Bogota', ('holder_zipcode') : '110111', ('holder_address') : 'Virrey']

contactFields.each({ def name, def value ->
        TestObject input = new TestObject(name)
        input.addProperty('xpath', ConditionType.EQUALS, "//input[@name='$name']")
        WebUI.waitForElementVisible(input, 10)
        WebUI.setText(input, value)
    })

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

