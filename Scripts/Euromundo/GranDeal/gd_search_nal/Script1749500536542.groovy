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

WebUI.waitForElementClickable(findTestObject('Euromundo/repository_GD_nal/input_Destination'), 20)

WebUI.selectOptionByValue(findTestObject('Object Repository/Euromundo/repository_GD_nal/input_Destination'), '35163', true)

WebUI.selectOptionByValue(findTestObject('Euromundo/repository_GD_nal/input_Origin'), '174008', true)

WebUI.setText(findTestObject('Euromundo/repository_GD_nal/origin_Date'), '02/09/2025', FailureHandling.STOP_ON_FAILURE)

WebUI.selectOptionByValue(findTestObject('Euromundo/set_Nights'), '5', true)

WebUI.click(findTestObject('Euromundo/set_RoomsPax'))

WebUI.selectOptionByLabel(findTestObject('Euromundo/set_Adults'), '1', true)

WebUI.selectOptionByLabel(findTestObject('Euromundo/set_Chds'), '0', true)

WebUI.selectOptionByLabel(findTestObject('Euromundo/set_Infs'), '0', true)

WebUI.click(findTestObject('Euromundo/repository_GD_nal/button_Search_GD'))

WebUI.click(findTestObject('Euromundo/hotel_steps/button_select_hotel'))

WebUI.waitForElementClickable(findTestObject('Euromundo/hotel_steps/button_prebook'), 10)

WebUI.scrollToElement(findTestObject('Euromundo/hotel_steps/button_prebook'), 10)

WebUI.click(findTestObject('Euromundo/hotel_steps/button_prebook'))

WebUI.waitForElementClickable(findTestObject('Euromundo/hotel_steps/button_finalization_preBook'), 10)

WebUI.scrollToElement(findTestObject('Euromundo/hotel_steps/button_finalization_preBook'), 10)

