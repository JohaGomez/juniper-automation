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



String url = 'https://euromundomxn.juniper.es/'

/**
 * Medir tiempo de carga de la página de login (donde están los campos de acceso)
 */
WebUI.openBrowser('')
long inicioLoginPage = System.currentTimeMillis()
WebUI.navigateToUrl(url)
WebUI.maximizeWindow()

// Espera que esté presente el botón "Acceder"
WebUI.waitForElementVisible(findTestObject('Euromundo/login/button_Login'), 20)
WebUI.delay(1)

long finLoginPage = System.currentTimeMillis()
double tiempoLoginPage = (finLoginPage - inicioLoginPage) / 1000.0
println "🔓 Tiempo de carga de la página de Login: ${tiempoLoginPage} segundos"

// Versión de DLL (opcional)
String version = CustomKeywords.'utils.DLLUtils.getVersionDLL'(url)
WebUI.comment("📦 Versión de DLL: ${version}")

/**
 * Login (ingresar credenciales y hacer clic)
 */
long inicioLogin = System.currentTimeMillis()
WebUI.setText(findTestObject('Euromundo/login/input_User'), GlobalVariable.username)
WebUI.setText(findTestObject('Euromundo/login/input_Pss'), GlobalVariable.password)
WebUI.click(findTestObject('Euromundo/login/checkbox_TyC'))
WebUI.click(findTestObject('Euromundo/login/button_Login'))

/**
 * Esperar carga del Home (pantalla de búsqueda con botón "Buscar")
 */
boolean loginOK = WebUI.waitForElementClickable(findTestObject('Euromundo/gran_deal/repository_GD_nal/button_Search_GD'), 20)
long finLogin = System.currentTimeMillis()
double tiempoCargaHome = (finLogin - inicioLogin) / 1000.0

if (loginOK) {
    println "✅ Login exitoso"
    println "🏠 Tiempo de carga del Home (post-login): ${tiempoCargaHome} segundos"
    println "🕓 Tiempo total desde inicio hasta Home: ${(tiempoLoginPage + tiempoCargaHome)} segundos"
} else {
    println "❌ Login fallido: no se encontró el botón de búsqueda"
    WebUI.takeScreenshot()
}

WebUI.closeBrowser()