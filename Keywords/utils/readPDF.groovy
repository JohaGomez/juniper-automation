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
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.net.URL
import java.io.InputStream
import com.kms.katalon.core.util.KeywordUtil
import java.nio.file.Files
import java.nio.file.Paths
import internal.GlobalVariable


class ReadPDF {

	/**
	 * Verifica si un PDF cargado desde una URL contiene texto.
	 * @param urlPDF URL del PDF
	 * @return true si el PDF contiene texto, false si está vacío o hubo error
	 */
	static boolean validarPDFTieneTexto(String urlPDF) {
		if (urlPDF == null || urlPDF.trim().isEmpty()) {
			KeywordUtil.markFailed("🚨 La URL del PDF está vacía o es null.")
			return false
		}

		PDDocument document = null
		BufferedInputStream bis = null

		try {
			KeywordUtil.logInfo("🌐 Abriendo conexión a: $urlPDF")

			URL url = new URL(urlPDF)
			URLConnection connection = url.openConnection()
			InputStream is = connection.getInputStream()

			bis = new BufferedInputStream(is)
			document = PDDocument.load(bis)

			PDFTextStripper stripper = new PDFTextStripper()
			String texto = stripper.getText(document)

			if (texto == null || texto.trim().isEmpty()) {
				KeywordUtil.markWarning("⚠️ El PDF está vacío o no se extrajo texto.")
				return false
			} else {
				KeywordUtil.logInfo("✅ El PDF contiene texto.")
				return true
			}
		} catch (Exception e) {
			KeywordUtil.markFailed("❌ Error al leer el PDF desde URL: ${e.message}")
			return false
		} finally {
			if (document != null) document.close()
			if (bis != null) bis.close()
		}
	}
}