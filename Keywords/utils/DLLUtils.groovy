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
import com.kms.katalon.core.util.KeywordUtil
import groovy.xml.XmlSlurper
import java.net.URL

import internal.GlobalVariable

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