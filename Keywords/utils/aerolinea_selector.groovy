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
import org.openqa.selenium.By
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.util.KeywordUtil
import org.openqa.selenium.JavascriptExecutor
import internal.GlobalVariable

public class aerolinea_selector {

	@Keyword
	def seleccionarVueloVolaris() {
		WebDriver driver = DriverFactory.getWebDriver()
		WebUI.comment("🔎 Buscando vuelos 100% operados por Volaris...")

		List<WebElement> resultados = driver.findElements(By.cssSelector(".flight-result"))

		for (WebElement resultado : resultados) {
			List<WebElement> nombres = resultado.findElements(By.cssSelector(".flight-result__airline-name"))
			boolean todosSonVolaris = true

			for (WebElement nombre : nombres) {
				String texto = nombre.getText().trim().toLowerCase()
				if (!texto.contains("volaris")) {
					todosSonVolaris = false
					break
				}
			}

			if (todosSonVolaris) {
				WebUI.comment("✅ Se encontró un vuelo con todos los segmentos de Volaris")

				try {
					WebElement radio = resultado.findElement(By.cssSelector("input[type='radio'][name='result-optin-radio']"))
					radio.click()
					WebUI.comment("🔘 Radio button de Volaris seleccionado")
				} catch (Exception e) {
					WebUI.comment("⚠️ No se encontró radio button, se intenta con botón directamente")
				}

				try {
					List<WebElement> botones = resultado.findElements(By.cssSelector("button.flight-result__booking-button"))
					boolean botonPresionado = false

					for (WebElement boton : botones) {
						String textoBoton = boton.getText().trim().toLowerCase()
						if (textoBoton.contains("seleccionar vuelo") || textoBoton.contains("cambiar")) {
							boton.click()
							WebUI.comment("✈️ Botón '${textoBoton}' presionado correctamente")
							botonPresionado = true
							break
						}
					}

					if (!botonPresionado) {
						WebUI.comment("❌ No se encontró botón 'Seleccionar vuelo' o 'Cambiar' en el bloque Volaris")
					}
				} catch (Exception e) {
					WebUI.comment("❌ Error al hacer click en el botón de Volaris: " + e.message)
				}

				break
			}
		}

		WebUI.comment("🚨 Finalizó la búsqueda de vuelos Volaris.")
	}

	@Keyword
	def seleccionarVueloAerobus() {
		WebDriver driver = DriverFactory.getWebDriver()
		WebUI.comment("🔎 Buscando vuelos 100% operados por Viva Aerobus...")

		List<WebElement> resultados = driver.findElements(By.cssSelector(".flight-result"))

		for (WebElement resultado : resultados) {
			List<WebElement> nombres = resultado.findElements(By.cssSelector(".flight-result__airline-name"))
			boolean todosSonAerobus = true

			for (WebElement nombre : nombres) {
				String texto = nombre.getText().trim().toLowerCase()
				if (!texto.contains("aerobus")) {
					todosSonAerobus = false
					break
				}
			}

			if (todosSonAerobus) {
				WebUI.comment("✅ Se encontró un vuelo con todos los segmentos de Viva Aerobus")

				try {
					WebElement radio = resultado.findElement(By.cssSelector("input[type='radio'][name='result-optin-radio']"))
					radio.click()
					WebUI.comment("🔘 Radio button de Aerobus seleccionado")
				} catch (Exception e) {
					WebUI.comment("⚠️ No se encontró radio button, se intenta con botón directamente")
				}

				try {
					List<WebElement> botones = resultado.findElements(By.cssSelector("button.flight-result__booking-button"))
					boolean botonPresionado = false

					for (WebElement boton : botones) {
						String textoBoton = boton.getText().trim().toLowerCase()
						if (textoBoton.contains("seleccionar vuelo") || textoBoton.contains("cambiar")) {
							boton.click()
							WebUI.comment("✈️ Botón '${textoBoton}' presionado correctamente")
							botonPresionado = true
							break
						}
					}

					if (!botonPresionado) {
						WebUI.comment("❌ No se encontró botón 'Seleccionar vuelo' o 'Cambiar' en el bloque Aerobus")
					}
				} catch (Exception e) {
					WebUI.comment("❌ Error al hacer click en el botón de Aerobus: " + e.message)
				}

				break
			}
		}

		WebUI.comment("🚨 Finalizó la búsqueda de vuelos Viva Aerobus.")
	}

	@Keyword
	def seleccionarVueloAeromexico() {
		WebDriver driver = DriverFactory.getWebDriver()
		WebUI.comment("🔎 Buscando vuelos 100% operados por Aeroméxico...")

		List<WebElement> resultados = driver.findElements(By.cssSelector(".flight-result"))

		for (WebElement resultado : resultados) {
			List<WebElement> nombres = resultado.findElements(By.cssSelector(".flight-result__airline-name"))
			boolean todosSonAeromexico = true

			for (WebElement nombre : nombres) {
				String texto = nombre.getText().trim().toLowerCase()
				if (!texto.contains("aeroméxico") && !texto.contains("aeromexico")) {
					todosSonAeromexico = false
					break
				}
			}

			if (todosSonAeromexico) {
				WebUI.comment("✅ Se encontró un vuelo con todos los segmentos de Aeroméxico")

				try {
					WebElement radio = resultado.findElement(By.cssSelector("input[type='radio'][name='result-optin-radio']"))
					radio.click()
					WebUI.comment("🔘 Radio button de Aeroméxico seleccionado")
				} catch (Exception e) {
					WebUI.comment("⚠️ No se encontró radio button, se intenta con botón directamente")
				}

				try {
					List<WebElement> botones = resultado.findElements(By.cssSelector("button.flight-result__booking-button"))
					boolean botonPresionado = false

					for (WebElement boton : botones) {
						String textoBoton = boton.getText().trim().toLowerCase()
						if (textoBoton.contains("seleccionar vuelo") || textoBoton.contains("cambiar")) {
							boton.click()
							WebUI.comment("✈️ Botón '${textoBoton}' presionado correctamente")
							botonPresionado = true
							break
						}
					}

					if (!botonPresionado) {
						WebUI.comment("❌ No se encontró botón 'Seleccionar vuelo' o 'Cambiar' en el bloque Aeroméxico")
					}
				} catch (Exception e) {
					WebUI.comment("❌ Error al hacer click en el botón de Aeroméxico: " + e.message)
				}

				break
			}
		}

		WebUI.comment("🚨 Finalizó la búsqueda de vuelos Aeroméxico.")
	}


	@Keyword
	def seleccionarVueloVolarisCharter() {
		WebDriver driver = DriverFactory.getWebDriver()
		WebUI.comment("🔎 Buscando vuelos Volaris con al menos un tramo 'Vuelo: YxxxxxN'...")

		List<WebElement> resultados = driver.findElements(By.cssSelector(".flight-result"))
		boolean vueloSeleccionado = false

		for (WebElement resultado : resultados) {

			// 1️⃣ Abrir la sección de más info
			try {
				WebElement botonMasInfo = resultado.findElement(By.cssSelector(".result-option-package__moreinfo-toggle"))
				botonMasInfo.click()
				WebUI.delay(0.5) // pequeño tiempo para que cargue la info
			} catch (Exception e) {
				WebUI.comment("⚠️ No se pudo abrir sección de más info en este resultado")
				continue
			}

			// 2️⃣ Buscar código de vuelo en formato válido
			List<WebElement> codigosVuelo = resultado.findElements(By.xpath(".//div[starts-with(normalize-space(.), 'Vuelo:')]"))
			boolean tieneCodigoValido = codigosVuelo.any { el ->
				String textoCodigo = el?.getText()?.trim()?.replaceAll("\\s+", " ")
				return textoCodigo?.matches(/^Vuelo:\s?[A-Z]\d{4,6}N$/)
			}

			if (!tieneCodigoValido) {
				WebUI.comment("⏭️ Este resultado no tiene código 'Vuelo: YxxxxxN'. Se omite...")
				continue
			}

			WebUI.comment("✅ Vuelo válido encontrado, seleccionando opción...")

			// 3️⃣ Seleccionar radio button del vuelo
			try {
				WebElement radio = resultado.findElement(By.cssSelector("input[type='radio'][name='result-optin-radio']"))
				radio.click()
				vueloSeleccionado = true
				WebUI.comment("🔘 Radio button seleccionado correctamente")
			} catch (Exception e) {
				WebUI.comment("❌ No se encontró el radio button para este vuelo: " + e.message)
			}

			break // si ya encontramos uno, salimos del loop
		}

		if (!vueloSeleccionado) {
			KeywordUtil.markFailedAndStop("🚨 No se encontró ningún vuelo Volaris válido con formato 'Vuelo: YxxxxxN'")
		}
	}
}