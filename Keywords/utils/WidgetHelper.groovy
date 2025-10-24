package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ObjectRepository

class WidgetHelper {

	@Keyword
	def seleccionarCiudad(String inputPath, String textoBusqueda) {
		// ✅ Se usa ObjectRepository.findTestObject() porque estamos dentro de una clase
		def inputField = ObjectRepository.findTestObject(inputPath)

		// 1️⃣ Click y escritura en el campo
		WebUI.click(inputField)
		WebUI.setText(inputField, textoBusqueda)

		// 2️⃣ Esperar a que aparezca la sugerencia en el autocomplete
		TestObject opcion = new TestObject('opcionAutocomplete')
		opcion.addProperty('xpath', ConditionType.CONTAINS,
				"//div[contains(@class,'tt-suggestion') and contains(.,'${textoBusqueda}')]")

		if (WebUI.waitForElementVisible(opcion, 10, FailureHandling.OPTIONAL)) {
			WebUI.click(opcion)
			KeywordUtil.logInfo("✅ Ciudad '${textoBusqueda}' seleccionada correctamente en el autocomplete.")
		} else {
			KeywordUtil.markWarning("⚠️ No se encontró '${textoBusqueda}' en las sugerencias.")
		}
	}
}
