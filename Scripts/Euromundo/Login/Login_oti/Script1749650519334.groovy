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

// ✅ Define la URL que deseas usar en este test case
String url = 'https://euromundooti.juniper.es'
'Abrir el sitio web para iniciar con el Login'
WebUI.openBrowser('https://euromundooti.juniper.es')

// Maximiza ventana
WebUI.maximizeWindow()

// Obtener versión de DLL a partir de la URL definida
String version = CustomKeywords.'utils.DLLUtils.getVersionDLL'(url)
WebUI.comment("📦 Versión de DLL detectada: ${version}")

// Ingreso de credenciales desde variables globales
'Ingresar Usuario'
WebUI.setText(findTestObject('Euromundo/login/input_User'), GlobalVariable.username)
'Ingresar Password'
WebUI.setText(findTestObject('Euromundo/login/input_Pss'), GlobalVariable.password)

// Aceptar Términos y Condiciones
'Click en aceptar Terminos y Condiciones'
WebUI.click(findTestObject('Euromundo/login/checkbox_TyC'))

// Clic en botón de login
'Click en botón Acceder'
WebUI.click(findTestObject('Euromundo/login/button_Login'))

'Espere a que muestre el botón buscar del search para validar que hizo login correctamente'
WebUI.waitForElementClickable(findTestObject('Euromundo/gran_deal/repository_GD_nal/button_Search_GD'), 10)
