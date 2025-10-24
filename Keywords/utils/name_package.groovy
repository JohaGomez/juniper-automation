package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.util.KeywordUtil

public class ValidacionesFlujo {

	private TestObject createTO(String xpath) {
		TestObject to = new TestObject()
		to.addProperty("xpath", ConditionType.EQUALS, xpath)
		return to
	}

	@Keyword
	def validarFlujoSearchPrebookBook() {

		// --- Capturas ---
		String searchTxt   = WebUI.getText(createTO("//*[@id='results-list']/div[2]/div/div[1]/article/div[1]/div[2]/div[1]")).trim()
		String prebookTxt  = WebUI.getText(createTO("//*[@id='main-content']/div[2]/div/div[3]/div[1]/div/div/div/div[1]/div[2]/div[1]")).trim()
		String preebookTxt = WebUI.getText(createTO("//*[@id='main-content']/div[2]/div/div[2]/div[2]/div/div[2]/div/div/div[1]/div/span[1]")).trim()
		String bookTxt     = WebUI.getText(createTO("//*[@id='main-content']/div[2]/div/div/div[1]/div[1]/div/div[2]/table[1]/tbody/tr[1]/td")).trim()

		KeywordUtil.logInfo("📌 Search:   ${searchTxt}")
		KeywordUtil.logInfo("📌 Prebook:  ${prebookTxt}")
		KeywordUtil.logInfo("📌 Preebook: ${preebookTxt}")
		KeywordUtil.logInfo("📌 Book:     ${bookTxt}")

		// --- Validaciones ---
		if (searchTxt != prebookTxt) {
			KeywordUtil.markFailedAndStop("❌ Validación falló: Search != Prebook → '${searchTxt}' vs '${prebookTxt}'")
		} else {
			KeywordUtil.logInfo("✅ Validación exitosa: Search y Prebook coinciden.")
		}

		if (preebookTxt != bookTxt) {
			KeywordUtil.markFailedAndStop("❌ Validación falló: Preebook != Book → '${preebookTxt}' vs '${bookTxt}'")
		} else {
			KeywordUtil.logInfo("✅ Validación exitosa: Preebook y Book coinciden.")
		}

		KeywordUtil.logInfo("🎉 Todas las validaciones del flujo fueron exitosas.")
	}
}

