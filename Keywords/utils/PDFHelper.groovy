package utils

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.testobject.ConditionType
import org.openqa.selenium.WebDriver
import internal.GlobalVariable

public class PDFHelper {

	@Keyword
	def validarCotizacionPDF() {
		WebDriver driver = DriverFactory.getWebDriver()

		// 1. Capturar el localizador
		String localizador = WebUI.getText(findTestObject('Object Repository/LocalizadorTexto'))
		localizador = localizador.replace("Localizador: ", "").trim()
		WebUI.comment("📌 Localizador capturado: " + localizador)

		// 2. Hacer clic en el botón "Imprimir Cotización"
		WebUI.click(findTestObject('Euromundo/book_steps_button_print_quote'))

		// 3. Cambiar a la nueva ventana donde se abre el PDF
		WebUI.switchToWindowIndex(1)
		WebUI.delay(3) // espera que cargue el PDF embebido

		// 4. Validar que la URL del embed contenga el localizador
		String pdfSrc = WebUI.getAttribute(findTestObject('Object Repository/PDFEmbed'), "original-url")
		if (pdfSrc.contains(localizador)) {
			WebUI.comment("📄 PDF contiene el localizador en la URL.")
		} else {
			WebUI.comment("⚠️ El localizador no está presente en la URL del PDF.")
			KeywordUtil.markFailed("Fallo en validación de PDF.")
		}

		// 5. Cerrar ventana de PDF y volver a la principal
		WebUI.closeWindowIndex(1)
		WebUI.switchToWindowIndex(0)

		// 6. Ir a la sección de "Cotizaciones"
		WebUI.click(findTestObject('Euromundo/book_steps_button_quotes'))

		// 7. Esperar el campo de búsqueda
		WebUI.waitForElementVisible(findTestObject('Euromundo/book_steps_input_quote_locator'), 10)

		// 8. Ingresar el localizador
		WebUI.setText(findTestObject('Euromundo/book_steps_input_quote_locator'), localizador)

		// 9. Buscar
		WebUI.click(findTestObject('Euromundo/book_steps/button_search_quote'))

		// 10. Validar mensaje de éxito
		TestObject mensajeExito = new TestObject().addProperty(
				"xpath", ConditionType.EQUALS,
				"//p[@class='text' and contains(text(), 'Su cotización ha sido guardada')]"
				)

		boolean apareceMensaje = WebUI.verifyElementPresent(mensajeExito, 10)

		if (apareceMensaje) {
			WebUI.comment("✅ Cotización guardada con éxito.")
		} else {
			WebUI.comment("❌ No se encontró el mensaje de cotización guardada.")
			KeywordUtil.markFailed("Fallo en validación de cotización.")
		}
	}
}