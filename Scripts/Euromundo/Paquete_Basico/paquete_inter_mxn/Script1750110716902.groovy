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
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

// 🚪 Login
WebUI.callTestCase(
    findTestCase('Euromundo/Login/Login_mxn'),
    [:],
    FailureHandling.STOP_ON_FAILURE
)

// ☰ Navegar al menú
WebUI.waitForElementClickable(findTestObject('Euromundo/paquete_basico/repository_paquete_nal/menu_paquete_basico'), 10)
WebUI.mouseOver(findTestObject('Euromundo/paquete_basico/repository_paquete_nal/menu_paquete_basico'))

WebUI.click(findTestObject('Euromundo/paquete_basico/repository_paquete_inter/select_paquete_inter'))

// 📦 Paso 1: Cerrar cookies y abrir box
WebUI.click(findTestObject('Euromundo/book_steps/button_box'))
WebUI.click(findTestObject('Euromundo/book_steps/button_close_cookies'))

// 📍 Origen y destino
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/input_destination_inter'), 15)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/input_destination_inter'), '29476', true)
WebUI.selectOptionByValue(findTestObject('Euromundo/book_steps/select_origin_inter'), '35163', true)

// 📅 Fecha
CustomKeywords.'utils.FechaUtils.setFechaSabadosEnTresMeses'('Euromundo/book_steps/origin_date_inter')

// 🔍 Buscar
WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))

// 🏨 Paso 2: Verificar si aparece el botón de prebook
boolean hotelVisible = WebUI.waitForElementVisible(
    findTestObject('Euromundo/book_steps/button_prebook_gd'),
    10,
    FailureHandling.OPTIONAL
)

if (!hotelVisible) {
    WebUI.comment("🔄 Resultado no cargado a la primera, reintentando búsqueda")

    WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_edit'), 10)
    WebUI.click(findTestObject('Euromundo/book_steps/button_edit'))

    WebUI.click(findTestObject('Euromundo/book_steps/button_search_inter'))

    WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps/button_prebook_gd'), 15)
}

// ✅ Reservar hotel
WebUI.click(findTestObject('Euromundo/book_steps/button_prebook_gd'))

// 🔐 Paso final: Continuar con prebook
WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)

