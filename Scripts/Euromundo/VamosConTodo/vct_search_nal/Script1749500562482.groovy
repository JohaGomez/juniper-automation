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

WebUI.callTestCase(findTestCase('Euromundo/Login_otn'), [:] // Si no necesitas pasarle usuario/contraseña diferentes
    , FailureHandling.STOP_ON_FAILURE)

WebUI.waitForElementClickable(findTestObject('Euromundo/repository_vct_inter/menu_vct_inter'), 10)

WebUI.mouseOver(findTestObject('Euromundo/repository_vct_inter/menu_vct_inter'))

WebUI.click(findTestObject('Euromundo/repository_vct_nal/select_vct_nal'))

WebUI.waitForElementClickable(findTestObject('Euromundo/repository_GD_Inter/button_box_inter'), 10)

WebUI.click(findTestObject('Euromundo/repository_GD_Inter/button_box_inter'))

WebUI.waitForElementClickable(findTestObject('Euromundo/repository_GD_Inter/input_destination_inter'), 15)

WebUI.selectOptionByValue(findTestObject('Euromundo/repository_GD_Inter/input_destination_inter'), '29476', true)

WebUI.selectOptionByValue(findTestObject('Euromundo/repository_GD_Inter/select_origin_inter'), '35908', true)

WebUI.setText(findTestObject('Euromundo/repository_GD_Inter/origin_date_inter'), '10/08/2025')

WebUI.click(findTestObject('Euromundo/repository_GD_Inter/button_search_inter'))

// Paso 2: Esperar hasta 10 segundos por el elemento select_hotel
boolean hotelVisible = WebUI.waitForElementVisible(findTestObject('Euromundo/repository_vct_inter/select_hotel_vct_inter'), 
    10, FailureHandling.OPTIONAL)

WebUI.click(findTestObject('Euromundo/repository_vct_inter/select_vct_inter'))

if (!(hotelVisible)) {
    // Paso 3a: Esperar que aparezca el botón editar
    WebUI.waitForElementVisible(findTestObject('Euromundo/button_edit'), 10)

    // Paso 3b: Hacer clic en el botón editar
    WebUI.click(findTestObject('Euromundo/button_edit'))

    // Paso 3c: Hacer clic en el botón buscar
    WebUI.click(findTestObject('Euromundo/repository_GD_Inter/button_search_inter'))

    // Paso 3d: Esperar nuevamente el select_hotel
    WebUI.waitForElementVisible(findTestObject('Euromundo/repository_vct_inter/select_hotel_vct_inter'), 10)

    // Paso 3e: Hacer clic en el botón reservar hotel
    WebUI.click(findTestObject('Euromundo/repository_vct_inter/select_hotel_vct_inter'))
}

WebUI.waitForElementClickable(findTestObject('Euromundo/hotel_steps/button_finalization_preBook'), 10)

WebUI.scrollToElement(findTestObject('Euromundo/hotel_steps/button_finalization_preBook'), 10)

