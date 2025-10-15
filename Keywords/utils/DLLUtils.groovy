package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

public class DLLUtils {
	@Keyword
	def String getVersionDLL(String urlStr) {
		try {
			def url = new URL(urlStr + '/checkversion.ashx?lib=bookingengine')
			def connection = url.openConnection()
			connection.setRequestMethod("GET")

			def xmlText = connection.inputStream.text
			WebUI.comment("🔍 Respuesta recibida: " + xmlText)

			// Validar si empieza con XML
			if (!xmlText.trim().startsWith("<")) {
				WebUI.comment("❌ La respuesta no es XML válido.")
				return "N/A"
			}

			def xml = new XmlSlurper().parseText(xmlText)
			def version = xml.libreria.@version.toString()

			if (!version) {
				WebUI.comment("⚠️ Versión no encontrada en el XML.")
				return "N/A"
			}

			return version
		} catch (Exception e) {
			WebUI.comment("❌ Error al obtener la versión DLL: ${e.message}")
			return "N/A"
		}
	}
}