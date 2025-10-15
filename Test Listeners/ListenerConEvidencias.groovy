import com.kms.katalon.core.annotation.*
import com.kms.katalon.core.context.*
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.driver.DriverFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ListenerConEvidencias {

    static String basePath = ""

    @BeforeTestSuite
    def crearRutaCaptura(TestSuiteContext testSuiteContext) {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        String suite = testSuiteContext.getTestSuiteId()?.replaceAll("[^a-zA-Z0-9_-]", "_") ?: "TestCase"
        basePath = "${RunConfiguration.getProjectDir()}/Capturas/${suite}_${fecha}"
        new File(basePath).mkdirs()
        WebUI.comment("📁 Carpeta de capturas creada: ${basePath}")
    }

    @AfterTestCase
    def capturarFin(TestCaseContext testCaseContext) {
        try {
            if (DriverFactory.getWebDriver() != null) {
                String id = testCaseContext.getTestCaseId()?.replaceAll("[^a-zA-Z0-9_-]", "_")
                String ruta = "${basePath}/Fin_${id}.png"
                WebUI.takeScreenshot(ruta)
                WebUI.comment("📸 Captura final guardada en: ${ruta}")
            } else {
                WebUI.comment("⚠️ No se pudo tomar captura: navegador no abierto")
            }
        } catch (Exception e) {
            WebUI.comment("❌ Error al capturar fin: ${e.message}")
        }
    }
}
