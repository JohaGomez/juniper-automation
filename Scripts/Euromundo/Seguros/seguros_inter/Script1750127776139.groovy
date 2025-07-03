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

WebUI.callTestCase(findTestCase('Euromundo/Login/Login_mxn'), [:] // Si no necesitas pasarle usuario/contraseña diferentes
    , FailureHandling.STOP_ON_FAILURE)

WebUI.waitForElementClickable(findTestObject('Euromundo/seguros/repository_seguros_nal/menu_seguros'), 10)

WebUI.mouseOver(findTestObject('Euromundo/seguros/repository_seguros_nal/menu_seguros'))

WebUI.click(findTestObject('Euromundo/seguros/repository_seguros_inter/select_seguros_inter'))

WebUI.click(findTestObject('Euromundo/book_steps/button_box'))

WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_insurance'), 15)

WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_insurance'), '29476', true)

CustomKeywords.'utils.FechaUtils.setFechasIdaYRegreso'(
	'Euromundo/seguros/repository_seguros_nal/input_date_start_insurance',
	'Euromundo/seguros/repository_seguros_nal/input_date_end_insurance'
)

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

WebUI.closeBrowser()

