import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile

import internal.GlobalVariable as GlobalVariable

import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.annotation.Keyword
import utils.DLLUtils
import com.kms.katalon.core.util.KeywordUtil



class GlobalHooks {
	@BeforeTestCase
    def logDLLVersion(TestCaseContext testCaseContext) {
        try {
            // Mapeo de nombre identificador en test case con su URL de dominio correspondiente
            Map<String, String> dominioPorTipo = [
                'otn'  : 'https://euromundootn.juniper.es',
                'oti'  : 'https://euromundooti.juniper.es',
                'mxn'  : 'https://euromundomxn.juniper.es',
                'walm' : 'https://euromundowalm.juniper.es',
                'auv'  : 'https://euromundoauv.juniper.es'
            ]

            String testCaseId = testCaseContext.getTestCaseId().toLowerCase()

            // Encuentra el dominio correspondiente en base al nombre del test case
            String dominio = dominioPorTipo.find { key, val -> testCaseId.contains(key) }?.value

            if (dominio == null) {
                WebUI.comment("⚠️ No se encontró dominio para el test case: ${testCaseId}")
                return
            }

            // Llama al Keyword que extrae la versión
            String version = CustomKeywords.'utils.DLLUtils.getVersionDLL'(dominio)

            // Muestra los datos en el log
            WebUI.comment("🌐 TestCase: ${testCaseId}")
            WebUI.comment("🔗 Dominio detectado: ${dominio}")
            WebUI.comment("📦 Versión de DLL: ${version}")

        } catch (Exception e) {
            WebUI.comment("❌ Error obteniendo la versión DLL: " + e.message)
        }
    }
}