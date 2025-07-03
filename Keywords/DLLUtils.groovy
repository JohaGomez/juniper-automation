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
import java.nio.file.Files
import java.nio.file.Paths

import internal.GlobalVariable

public class DLLUtils {
	@Keyword
    def imprimirVersionDLL(String rutaRelativa) {
        try {
            String rutaProyecto = RunConfiguration.getProjectDir()
            String rutaCompleta = rutaProyecto + "/" + rutaRelativa
            
            // Aquí va tu lógica real
            def version = obtenerVersionDesdeMetadata(rutaCompleta)
            KeywordUtil.logInfo("📦 Versión de la DLL: " + version)
        } catch (Exception e) {
            KeywordUtil.markWarning("❗ No se pudo obtener la versión de la DLL: " + e.message)
        }
    }

    private String obtenerVersionDesdeMetadata(String rutaDLL) {
        File dll = new File(rutaDLL)
        if (!dll.exists()) {
            throw new Exception("No existe el archivo DLL en la ruta: " + rutaDLL)
        }

        // Simulación: reemplázalo con la forma real de leer la versión
        return "1.0.0"
    }
}
