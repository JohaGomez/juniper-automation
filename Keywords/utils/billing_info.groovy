package utils

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.util.KeywordUtil
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import com.kms.katalon.core.webui.driver.DriverFactory

class billing_info {

	@Keyword
	def fillIfRequired(String xpath, String text) {
		TestObject input = new TestObject().addProperty("xpath", ConditionType.EQUALS, xpath)
		if (WebUI.verifyElementPresent(input, 5, FailureHandling.OPTIONAL)) {
			String requiredAttr = WebUI.getAttribute(input, "required")
			if (requiredAttr == "true") {
				WebUI.setText(input, text)
				WebUI.comment("📩 Campo normal llenado: ${text}")
			}
		}
	}

	@Keyword
	def fillAutocompleteIfRequired(String xpath, String text, String textoSugerido = "") {
		WebDriver driver = DriverFactory.getWebDriver()
		TestObject input = new TestObject().addProperty("xpath", ConditionType.EQUALS, xpath)

		if (WebUI.verifyElementPresent(input, 10, FailureHandling.OPTIONAL)) {
			String requiredAttr = WebUI.getAttribute(input, "required")
			if (requiredAttr == "true") {
				WebUI.click(input)
				WebUI.setText(input, text)
				WebUI.delay(1)

				// Buscar sugerencia de tipo <pre> con texto específico (visible)
				String sugerenciaTexto = textoSugerido ?: text
				TestObject sugerencia = new TestObject("sugerencia_pre")
				sugerencia.addProperty("xpath", ConditionType.EQUALS, "//pre[text()='${sugerenciaTexto}']")

				if (WebUI.waitForElementVisible(sugerencia, 5)) {
					WebUI.click(sugerencia)
					WebUI.comment("✈️ Autocomplete: '${text}' ➜ Seleccionado '${sugerenciaTexto}'")
				} else {
					KeywordUtil.markFailedAndStop("❌ No se encontró opción de autocomplete con <pre> para '${sugerenciaTexto}'")
				}
			}
		} else {
			KeywordUtil.markFailedAndStop("🚫 Campo de autocomplete no encontrado con xpath: ${xpath}")
		}
	}

	@Keyword
	def selectDropdownIfPresent(String xpath, int index = 1) {
		TestObject dropdownObj = new TestObject().addProperty("xpath", ConditionType.EQUALS, xpath)
		if (WebUI.verifyElementPresent(dropdownObj, 10, FailureHandling.OPTIONAL)) {
			WebElement dropdownElem = WebUiCommonHelper.findWebElement(dropdownObj, 10)
			new org.openqa.selenium.support.ui.Select(dropdownElem).selectByIndex(index)
			WebUI.comment("✅ Dropdown seleccionado en índice ${index}")
		} else {
			WebUI.comment("⚠️ No se encontró el dropdown para selección con xpath: ${xpath}")
		}
	}
}
