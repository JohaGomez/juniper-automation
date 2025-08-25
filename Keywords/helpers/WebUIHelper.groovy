package helpers

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
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import org.openqa.selenium.WebElement
import java.util.Arrays

import internal.GlobalVariable

public class WebUIHelper {

	@Keyword
	def safeClick(TestObject testObject, int timeout = 10) {
		try {
			if (WebUI.waitForElementPresent(testObject, timeout, FailureHandling.OPTIONAL)) {
				WebUI.waitForElementVisible(testObject, timeout, FailureHandling.OPTIONAL)
				WebUI.waitForElementClickable(testObject, timeout, FailureHandling.OPTIONAL)

				// Scroll hacia el elemento
				WebUI.scrollToElement(testObject, 3, FailureHandling.OPTIONAL)

				// Intento de clic normal
				try {
					WebUI.click(testObject)
				} catch (Exception e) {
					WebUI.comment("⚠️ Clic normal falló. Intentando con JavaScript...")
					WebElement element = WebUiCommonHelper.findWebElement(testObject, timeout)
					WebUI.executeJavaScript("arguments[0].click();", Arrays.asList(element))
				}
			} else {
				WebUI.comment("❌ Elemento no encontrado para clic: ${testObject.getObjectId()}")
				WebUI.takeScreenshot()
			}
		} catch (Exception ex) {
			WebUI.comment("❌ Error al hacer clic en el elemento: ${testObject.getObjectId()}")
			WebUI.takeScreenshot()
			throw ex
		}
	}

	//CustomKeywords.'helpers.WebUIHelper.safeClick'(findTestObject('...'))

	@Keyword
	def safeSetText(TestObject testObject, String text, int timeout = 10) {
		if (WebUI.waitForElementPresent(testObject, timeout, FailureHandling.OPTIONAL)) {
			WebUI.waitForElementVisible(testObject, 5, FailureHandling.OPTIONAL)
			WebUI.setText(testObject, text)
		} else {
			WebUI.comment("❌ Campo de texto no encontrado: ${testObject.getObjectId()}")
			WebUI.takeScreenshot()
		}
	}

	//CustomKeywords.'helpers.WebUIHelper.safeSetText'(findTestObject('...'), '21/09/2031')

	@Keyword
	def safeSelectOptionByLabel(TestObject testObject, String label, boolean isRegex = false, int timeout = 10) {
		if (WebUI.waitForElementPresent(testObject, timeout, FailureHandling.OPTIONAL)) {
			WebUI.waitForElementVisible(testObject, 5, FailureHandling.OPTIONAL)
			WebUI.selectOptionByLabel(testObject, label, isRegex)
		} else {
			WebUI.comment("❌ Combo no encontrado: ${testObject.getObjectId()}")
			WebUI.takeScreenshot()
		}
	}

	//CustomKeywords.'helpers.WebUIHelper.safeSelectOptionByLabel'(findTestObject('...'), 'Colombia')

	@Keyword
	def safeScrollToElement(TestObject testObject, int timeout = 10) {
		try {
			if (WebUI.waitForElementPresent(testObject, timeout, FailureHandling.OPTIONAL)) {
				WebUI.waitForElementVisible(testObject, timeout, FailureHandling.OPTIONAL)
				WebUI.scrollToElement(testObject, timeout)
			} else {
				WebUI.comment("⚠️ No se pudo hacer scroll porque no se encontró el elemento: ${testObject.getObjectId()}")
				WebUI.takeScreenshot()
			}
		} catch (Exception e) {
			WebUI.comment("❌ Error durante scroll: ${e.message}")
			WebUI.takeScreenshot()
		}
	}
}