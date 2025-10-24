package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling

public class ValidacionPrecios {

	@Keyword
	def validarMejorPrecioEnResultados(TestObject precioObj = null, int timeout = 20, boolean stopOnFail = true, Double esperado = null) {
		TestObject targetObj = precioObj

		// ✅ Si no pasas un TestObject, crea uno dinámico con el XPath estándar
		if (targetObj == null) {
			targetObj = new TestObject("precio")
			targetObj.addProperty(
				"xpath",
				ConditionType.EQUALS,
				"//section[@class='result__bottom-price']//span[@class='js-currency-conversor']"
			)
		}

		// ⏳ Esperar elemento
		boolean presente = WebUI.waitForElementPresent(targetObj, timeout, FailureHandling.OPTIONAL)
		if (!presente) {
			WebUI.takeScreenshot()
			String msg = "❌ Elemento de mejor precio no encontrado en resultados."
			if (stopOnFail) {
				KeywordUtil.markFailedAndStop(msg)
			} else {
				KeywordUtil.logWarning(msg)
				return null
			}
		}

		// 👀 Asegurar visibilidad
		WebUI.scrollToElement(targetObj, 3, FailureHandling.OPTIONAL)

		// 1️⃣ Intentar con atributo data-amount
		String precioAttr = WebUI.getAttribute(targetObj, "data-amount", FailureHandling.OPTIONAL)
		double mejorPrecio = -1

		if (precioAttr && !precioAttr.trim().isEmpty()) {
			try {
				String normalizado = precioAttr.replaceAll("[^0-9.]", "").trim()
				mejorPrecio = Double.parseDouble(normalizado)
				KeywordUtil.logInfo("💾 Mejor precio capturado de atributo data-amount: ${mejorPrecio}")
			} catch (Exception e) {
				KeywordUtil.logWarning("⚠️ Error al parsear data-amount: '${precioAttr}'. Se intentará con el texto visible.")
			}
		}

		// 2️⃣ Si no logró con atributo, usar el texto visible
		if (mejorPrecio == -1) {
			String precioTexto = WebUI.getText(targetObj, FailureHandling.OPTIONAL)
			if (precioTexto && !precioTexto.trim().isEmpty()) {
				try {
					String normalizado = precioTexto.replaceAll("[^0-9.]", "").trim()
					mejorPrecio = Double.parseDouble(normalizado)
					KeywordUtil.logInfo("💾 Mejor precio capturado del texto visible: ${mejorPrecio}")
				} catch (Exception e) {
					WebUI.takeScreenshot()
					String msg = "❌ Error al parsear texto visible del precio: '${precioTexto}'"
					if (stopOnFail) {
						KeywordUtil.markFailedAndStop(msg)
					} else {
						KeywordUtil.logWarning(msg)
						return null
					}
				}
			}
		}

		// ❌ Si sigue sin valor
		if (mejorPrecio == -1) {
			WebUI.takeScreenshot()
			String msg = "❌ No se pudo capturar el mejor precio (ni data-amount ni texto)."
			if (stopOnFail) {
				KeywordUtil.markFailedAndStop(msg)
			} else {
				KeywordUtil.logWarning(msg)
			}
			return null
		}

		// ✅ Comparación con tolerancia (si se pasa un valor esperado)
		if (esperado != null) {
			double diferencia = Math.abs(mejorPrecio - esperado)
			double tolerancia = 1.0

			if (diferencia <= tolerancia) {
				KeywordUtil.logInfo("✅ Mejor precio correcto: esperado ${String.format('%.2f', esperado)}, obtenido ${String.format('%.2f', mejorPrecio)} (Δ=${String.format('%.2f', diferencia)})")
			} else {
				String msg = "❌ Mejor precio incorrecto: esperado ${String.format('%.2f', esperado)}, obtenido ${String.format('%.2f', mejorPrecio)} (Δ=${String.format('%.2f', diferencia)})"
				if (stopOnFail) {
					KeywordUtil.markFailedAndStop(msg)
				} else {
					KeywordUtil.markFailed(msg)
				}
			}
		}

		KeywordUtil.logInfo("✅ Validación de mejor precio exitosa. Precio final: ${mejorPrecio}")
		return mejorPrecio
	}
}
