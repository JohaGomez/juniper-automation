package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling

class ValidacionesPrebook {

	private static final double EPS = 0.01d // tolerancia mínima

	// --------------------------------------------------------
	// 🔹 Normalizador de montos
	// --------------------------------------------------------
	static Double parseMoney(String raw) {
		if (!raw) return null

		String cleanedRaw = raw.replaceAll("\\u00A0", " ").trim()
		String s = cleanedRaw.replaceAll("[^0-9,.-]", "")
		if (!s) return null

		int lastComma = s.lastIndexOf(",")
		int lastDot = s.lastIndexOf(".")
		boolean decimalIsComma = lastComma > lastDot

		String normalized
		if (decimalIsComma) {
			normalized = s.replace(".", "").replace(",", ".")
		} else {
			normalized = s.replace(",", "")
		}

		KeywordUtil.logInfo("🔎 parseMoney → raw='${raw}' | cleaned='${s}' | normalized='${normalized}'")

		try {
			return Double.parseDouble(normalized)
		} catch (Exception e) {
			KeywordUtil.markWarning("❌ parseMoney no pudo convertir '${raw}' → '${normalized}' (${e.message})")
			return null
		}
	}

	// --------------------------------------------------------
	// 🔹 Helper para leer precios desde objetos
	// --------------------------------------------------------
	static Double readMoney(TestObject obj, String nombre) {
		if (obj == null) {
			KeywordUtil.logInfo("⚠️ No se pasó objeto para '${nombre}'")
			return null
		}
		if (!WebUI.waitForElementVisible(obj, 10, FailureHandling.OPTIONAL)) {
			KeywordUtil.logInfo("⚠️ '${nombre}' no está visible")
			return null
		}
		String txt = WebUI.getText(obj)?.trim()
		if (!txt) {
			KeywordUtil.logInfo("⚠️ '${nombre}' está vacío")
			return null
		}
		KeywordUtil.logInfo("🔎 ${nombre}: ${txt}")
		return parseMoney(txt)
	}

	// --------------------------------------------------------
	// 🔹 Keyword principal de validación
	// --------------------------------------------------------
	@Keyword
	def validarPrecioPrebook(
			TestObject precioObj,
			TestObject comisionObj,
			TestObject precioFinalObj,
			TestObject totalAdeudadoObj,
			def mejorPrecio
	) {
		List<String> errors = []
		Double mejorPrecioNumber = null

		try {
			Double precio = readMoney(precioObj, "Precio")
			Double comision = readMoney(comisionObj, "Comisión")
			Double precioFinal = readMoney(precioFinalObj, "Precio Final")
			Double totalAdeudado = readMoney(totalAdeudadoObj, "Total Adeudado")

			// Normalizar mejorPrecio si viene como texto
			if (mejorPrecio != null) {
				if (mejorPrecio instanceof Number) {
					mejorPrecioNumber = mejorPrecio as Double
				} else {
					mejorPrecioNumber = parseMoney(mejorPrecio.toString())
				}
			}

			WebUI.comment("📌 Precio: ${precio} | Comisión: ${comision} | Precio Final: ${precioFinal} | Total Adeudado: ${totalAdeudado} | Mejor Precio: ${mejorPrecioNumber}")

			// (1) Precio - Comisión ≈ Precio Final
			if (precio != null && comision != null && precioFinal != null) {
				double esperado = precio - comision
				if (Math.abs(esperado - precioFinal) < EPS) {
					KeywordUtil.logInfo("✅ Precio Final (${precioFinal}) ≈ Precio (${precio}) - Comisión (${comision})")
				} else {
					errors.add("Precio Final (${precioFinal}) NO coincide con Precio (${precio}) - Comisión (${comision}). Calculado: ${String.format('%.2f', esperado)}")
				}
			}

			// (2) Precio ≈ Total Adeudado
			if (precio != null && totalAdeudado != null) {
				if (Math.abs(precio - totalAdeudado) < EPS) {
					KeywordUtil.logInfo("✅ Precio (${precio}) ≈ Total Adeudado (${totalAdeudado})")
				} else {
					errors.add("Precio (${precio}) ≠ Total Adeudado (${totalAdeudado})")
				}
			}

			// (3) Mejor Precio ≈ Total Adeudado
			if (mejorPrecioNumber != null && totalAdeudado != null) {
				if (Math.abs(mejorPrecioNumber - totalAdeudado) < EPS) {
					KeywordUtil.logInfo("✅ Mejor Precio (${mejorPrecioNumber}) ≈ Total Adeudado (${totalAdeudado})")
				} else {
					errors.add("Mejor Precio (${mejorPrecioNumber}) ≠ Total Adeudado (${totalAdeudado})")
				}
			}
		} catch (Exception e) {
			errors.add("Excepción durante la validación: " + e.message)
		} finally {
			if (!errors.isEmpty()) {
				WebUI.takeScreenshot()
				KeywordUtil.markWarning("❌ Validaciones con errores:\n - " + errors.join("\n - "))
			} else {
				WebUI.comment("✅ Todas las validaciones de precios OK")
			}
		}
	}

	// --------------------------------------------------------
	// 🔹 XPath actualizados (robustos y tolerantes)
	// --------------------------------------------------------
	static Map<String, TestObject> getPrebookBreakdownObjects() {

		TestObject precioObj = new TestObject("precioObj")
		precioObj.addProperty("xpath", ConditionType.EQUALS,
				"//div[contains(@class,'booking-breakdown__item') and contains(@class,'total-price')]//span[contains(@class,'booking-breakdown__item-price')]")

		TestObject comisionObj = new TestObject("comisionObj")
		comisionObj.addProperty("xpath", ConditionType.EQUALS,
				"//div[contains(@class,'booking-breakdown__item') and contains(@class,'comission')]//span[contains(@class,'booking-breakdown__item-price')]")

		TestObject precioFinalObj = new TestObject("precioFinalObj")
		precioFinalObj.addProperty("xpath", ConditionType.EQUALS,
				"//div[contains(@class,'booking-breakdown__item--final-price')]//span[contains(@class,'booking-breakdown__item-price')]//h3 | //div[contains(@class,'booking-breakdown__item--final-price')]//span[contains(@class,'booking-breakdown__item-price')]")

		// Si el Total Adeudado es igual al precio final, se usa el mismo
		TestObject totalAdeudadoObj = new TestObject("totalAdeudadoObj")
		totalAdeudadoObj.addProperty("xpath", ConditionType.EQUALS,
				"//div[contains(@class,'booking-breakdown__item--final-price')]//span[contains(@class,'booking-breakdown__item-price')]//h3 | //div[contains(@class,'booking-breakdown__item--final-price')]//span[contains(@class,'booking-breakdown__item-price')]")

		return [
			precioObj       : precioObj,
			comisionObj     : comisionObj,
			precioFinalObj  : precioFinalObj,
			totalAdeudadoObj: totalAdeudadoObj
		]
	}

	// --------------------------------------------------------
	// 🔹 Extrae el último precio de un warning de cambio
	// --------------------------------------------------------
	static Double getNuevoPrecioWarning(TestObject warningPrecioObj) {
		if (!WebUI.verifyElementPresent(warningPrecioObj, 5, FailureHandling.OPTIONAL)) {
			KeywordUtil.logInfo("ℹ️ No apareció warning de precio")
			return null
		}

		String warningText = WebUI.getText(warningPrecioObj)?.trim()
		KeywordUtil.logInfo("📌 Texto warning: ${warningText}")

		def matcher = (warningText =~ /\d{1,3}(?:[.,]\d{3})*(?:[.,]\d+)?/)
		def precios = matcher.collect { it[0]?.trim() }

		if (precios.isEmpty()) {
			KeywordUtil.markWarning("⚠️ No se pudo extraer ningún precio del warning")
			return null
		}

		String nuevoPrecioStr = precios.last()
		return parseMoney(nuevoPrecioStr)
	}
}
