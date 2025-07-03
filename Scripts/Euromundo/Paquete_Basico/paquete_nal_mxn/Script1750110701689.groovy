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

// 🚪 Login
WebUI.callTestCase(
    findTestCase('Euromundo/Login/Login_mxn'),
    [:],
    FailureHandling.STOP_ON_FAILURE
)

// ☰ Navegar al menú de paquete nacional
WebUI.waitForElementClickable(findTestObject('Euromundo/paquete_basico/repository_paquete_nal/menu_paquete_basico'), 10)
WebUI.mouseOver(findTestObject('Euromundo/paquete_basico/repository_paquete_nal/menu_paquete_basico'))
WebUI.click(findTestObject('Euromundo/paquete_basico/repository_paquete_nal/select_paquete_nal'))

// 📦 Cerrar cookies si aparecen
WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

// 📍 Origen y destino
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_inter'), 15)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_inter'), '35163', true)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/select_origin_inter'), '35908', true)

// 📅 Fecha aleatoria desde 3 meses a futuro
CustomKeywords.'utils.FechaUtils.setFechaAleatoriaDesdeTresMesesFuturo'('Euromundo/book_steps/origin_date_inter')

// 🔍 Buscar
WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))

// 🏨 Verificar si apareció el selector de hotel
boolean hotelVisible = WebUI.waitForElementVisible(
    findTestObject('Euromundo/vamos_con_todo/repository_vct_nal/select_hotel_vct_nal'),
    10,
    FailureHandling.OPTIONAL
)

if (hotelVisible) {
    WebUI.click(findTestObject('Euromundo/vamos_con_todo/repository_vct_nal/select_hotel_vct_nal'))
} else {
    WebUI.comment('🔄 Resultado no cargado a la primera, reintentando búsqueda')

    WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_edit'), 10)
    WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))

    WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))

    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_prebook'), 15)
    WebUI.click(findTestObject('Euromundo/book_steps/button_prebook'))
}

// 🔐 Paso final: Continuar con prebook
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)