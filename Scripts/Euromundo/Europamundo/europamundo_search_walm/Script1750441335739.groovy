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

WebUI.callTestCase(findTestCase('Euromundo/Login/Login_walm'), [:], FailureHandling.STOP_ON_FAILURE)

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
    WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/button_search_package'))

    // Paso 3d: Esperar nuevamente el select_hotel
    WebUI.waitForElementVisible(findTestObject('Euromundo/europamundo/repository_europamundo/select_package'), 10)

    // Paso 3e: Hacer clic en el botón reservar hotel
    WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/select_package'))
}

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/select_package'))

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/button_quote1'))

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/button_book'))

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/button_accept1'))

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/button_accept2'))

WebUI.selectOptionByValue(findTestObject('Euromundo/europamundo/repository_europamundo/input_arrival_transfers'), 'D66217B8BCB9C8D4CF8D027AFD4949DF', 
    true)

WebUI.selectOptionByValue(findTestObject('Euromundo/europamundo/repository_europamundo/input_departure_transfers'), 'D66217B8BCB9C8D4CF8D027AFD4949DF', 
    true)

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/button_quote2'))

WebUI.click(findTestObject('Euromundo/europamundo/repository_europamundo/button_continue'))

WebUI.waitForElementClickable(findTestObject('Euromundo/book_steps/button_finalization_prebook'), 10)

WebUI.closeBrowser()

