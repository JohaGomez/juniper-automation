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
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject

// URL a probar
String url = 'https://euromundootn.juniper.es'

// Abrir navegador y maximizar
WebUI.openBrowser(url)

WebUI.maximizeWindow()

// Obtener versión DLL y mostrarla
String version = CustomKeywords.'utils.DLLUtils.getVersionDLL'(url)

WebUI.comment("📦 Versión de DLL detectada: $version")

// Ingresar usuario correcto y contraseña incorrecta
WebUI.setText(findTestObject('Euromundo/login/input_User'), GlobalVariable.username)

WebUI.setText(findTestObject('Euromundo/login/input_Pss'), '1234' // ❌ intencionalmente incorrecta
	)

// Clic en botón acceder
WebUI.click(findTestObject('Euromundo/login/button_Login'))

WebUI.click(findTestObject('Euromundo/login/checkbox_TyC'))

WebUI.click(findTestObject('Euromundo/login/button_Login'))

// Definir objetos dinámicos
TestObject errorPassword = new TestObject('errorPassword')
errorPassword.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class,'alert') and contains(text(),'Password incorrecto')]")

TestObject errorTyC = new TestObject('errorTyC')
errorTyC.addProperty("xpath", ConditionType.EQUALS, "//div[contains(@class,'alert') and contains(text(),'términos y condiciones')]")

// Esperar hasta 5 segundos por cada mensaje
boolean isPasswordErrorVisible = WebUI.waitForElementVisible(errorPassword, 5, FailureHandling.OPTIONAL)
boolean isTyCErrorVisible = WebUI.waitForElementVisible(errorTyC, 5, FailureHandling.OPTIONAL)

// Validar que al menos uno de los mensajes haya aparecido
assert (isPasswordErrorVisible || isTyCErrorVisible) : 'El login falló pero no se mostraron los mensajes esperados.'

WebUI.comment('✅ Mensaje de error mostrado correctamente.')
// Cerrar navegador
WebUI.closeBrowser()

