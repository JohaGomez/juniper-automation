import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.model.FailureHandling

class Listener_ReservaLogger {

	@AfterTestCase
	def logReserva(TestCaseContext testCaseContext) {
		try {
			WebUI.waitForPageLoad(10)

			String xpathReserva = "//h4[contains(@class,'booking-details__booking-code')]"
			TestObject reservaObj = new TestObject('reservaObj')
			reservaObj.addProperty('xpath', ConditionType.EQUALS, xpathReserva)

			if (WebUI.waitForElementVisible(reservaObj, 5, FailureHandling.OPTIONAL)) {
				String textoLocalizador = WebUI.getText(reservaObj)
				String codigo = textoLocalizador.replace('Localizador:', '').trim()

				KeywordUtil.logInfo("💾 Localizador capturado: ${codigo}")
				println("💾 Localizador capturado: ${codigo}")
			} else {
				KeywordUtil.logInfo("⚠️ No se encontró el elemento del localizador.")
			}
		} catch (Exception e) {
			KeywordUtil.logInfo("⚠️ Error al capturar el localizador: ${e.message}")
		}
	}
}
