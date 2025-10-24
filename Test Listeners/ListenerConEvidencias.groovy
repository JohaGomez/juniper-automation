import com.kms.katalon.core.annotation.*
import com.kms.katalon.core.context.*
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.driver.DriverFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ListenerConEvidencias {

	static String basePath = ""

	// ============================================================
	// 📁 CREACIÓN DE CARPETA DE CAPTURAS
	// ============================================================
	@BeforeTestSuite
	def crearRutaCaptura(TestSuiteContext testSuiteContext) {
		String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
		String suite = testSuiteContext.getTestSuiteId()?.replaceAll("[^a-zA-Z0-9_-]", "_") ?: "TestSuite"
		basePath = "${RunConfiguration.getProjectDir()}/Capturas/${suite}_${fecha}"
		new File(basePath).mkdirs()
		WebUI.comment("📁 Carpeta de capturas creada: ${basePath}")
	}

	// ============================================================
	// 📸 CAPTURA FINAL O DE ERROR POR TEST CASE
	// ============================================================
	@AfterTestCase
	def capturarFin(TestCaseContext testCaseContext) {
		try {
			def driver = DriverFactory.getWebDriver()
			if (driver == null) {
				WebUI.comment("⚠️ No se pudo tomar captura: navegador no abierto")
				return
			}

			String id = testCaseContext.getTestCaseId()?.replaceAll("[^a-zA-Z0-9_-]", "_")
			String estado = testCaseContext.getTestCaseStatus()
			String nombreArchivo = (estado == "FAILED") ? "Error_${id}.png" : "Fin_${id}.png"
			String ruta = "${basePath}/${nombreArchivo}"

			WebUI.takeFullPageScreenshot(ruta)

			if (estado == "FAILED") {
				WebUI.comment("❌ Error detectado: se capturó imagen en ${ruta}")
				WebUI.comment("<p style='color:red;font-weight:bold;'>❌ Captura de error del test: ${id}</p>")
			} else {
				WebUI.comment("📸 Captura final guardada en: ${ruta}")
			}

			WebUI.comment("<img src='${ruta}' width='800'/>")

		} catch (Exception e) {
			WebUI.comment("❌ Error al capturar fin: ${e.message}")
		}
	}

	// ============================================================
	// 📄 LOG FINAL DE REPORTE (SIN ENVÍO POR CORREO)
	// ============================================================
	@AfterTestSuite
	def generarLogFinal(TestSuiteContext testSuiteContext) {
		try {
			String reportDir = testSuiteContext.getReportFolder()
			File htmlReport = new File("${reportDir}/execution0.html")

			if (!htmlReport.exists()) {
				WebUI.comment("⚠️ No se encontró el reporte HTML en: ${reportDir}")
				return
			}

			WebUI.comment("📄 Reporte generado correctamente para la suite: ${testSuiteContext.getTestSuiteId()}")
			WebUI.comment("📁 Ruta del reporte local: ${reportDir}")
			WebUI.comment("📋 Envío de correo desactivado — solo registro local.")
		} catch (Exception e) {
			WebUI.comment("❌ Error al procesar el reporte: ${e.message}")
		}
	}
}
