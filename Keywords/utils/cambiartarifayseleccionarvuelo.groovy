package utils

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.StaleElementReferenceException
import java.util.List


public class cambiartarifayseleccionarvuelo {

	@Keyword
	def cambiarTarifaYSeleccionarVuelo() {
		WebDriver driver = DriverFactory.getWebDriver()

		try {
			WebUI.comment("🔍 Buscando botón de reserva...")
			WebElement botonReserva = encontrarBotonReserva(driver)

			if (botonReserva != null) {
				WebUI.comment("✅ Botón encontrado, haciendo clic...")
				botonReserva.click()
			} else {
				WebUI.comment("❌ Botón no encontrado. Cambiando opción de tarifa...")
				cambiarOpcionDeRadio(driver)
				WebUI.delay(2) // Pequeña espera para que recargue el DOM

				WebElement nuevoBoton = encontrarBotonReserva(driver)
				if (nuevoBoton != null) {
					WebUI.comment("🔁 Botón encontrado tras cambiar opción. Haciendo clic...")
					nuevoBoton.click()
				} else {
					WebUI.comment("🚨 No se encontró el botón de reserva tras intentar recuperar.")
					KeywordUtil.markFailed("No se pudo encontrar el botón de reserva")
				}
			}
		} catch (StaleElementReferenceException e) {
			WebUI.comment("⚠️ Elemento stale. Reintentando desde cero...")
			WebUI.delay(1)
			cambiarTarifaYSeleccionarVuelo() // Reintento recursivo
		} catch (Exception e) {
			WebUI.comment("💥 Error inesperado: " + e.message)
			KeywordUtil.markFailed("Excepción durante selección de vuelo: " + e.message)
		}
	}

	// Función que intenta encontrar el botón de reserva
	private WebElement encontrarBotonReserva(WebDriver driver) {
		try {
			List<WebElement> resultados = driver.findElements(By.cssSelector(".flight-result"))
			for (WebElement resultado : resultados) {
				List<WebElement> botones = resultado.findElements(By.cssSelector(".flight-result__booking-button"))
				if (!botones.isEmpty()) {
					return botones[0]
				}
			}
		} catch (StaleElementReferenceException e) {
			WebUI.comment("🔄 DOM cambió mientras se buscaba el botón. Reintentando...")
			return null
		}
		return null
	}

	// Función para cambiar de opción de radio
	private void cambiarOpcionDeRadio(WebDriver driver) {
		List<WebElement> radios = driver.findElements(By.cssSelector("input[type='radio'][name='result-optin-radio']"))
		if (!radios.isEmpty()) {
			for (WebElement radio : radios) {
				if (!radio.isSelected()) {
					radio.click()
					WebUI.comment("🔘 Opción de tarifa cambiada.")
					break
				}
			}
		} else {
			WebUI.comment("⚠️ No se encontraron opciones de radio.")
		}
	}
}
