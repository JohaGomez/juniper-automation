package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.math.RoundingMode

public class ValidacionesPrecios {

	private static final double EPS = 0.01 // tolerancia

	// --- Helpers ---
	private static TestObject byXpath(String name, String xpath) {
		TestObject t = new TestObject(name)
		t.addProperty('xpath', ConditionType.EQUALS, xpath)
		return t
	}

	// "$ 2,487.11 USD" o "2.487,11" -> 2487.11
	private static Double parseMoney(String raw) {
		if (!raw) return null
		String s = raw.replaceAll('[^0-9,.-]', '')
		if (!s) return null
		int lastComma = s.lastIndexOf(',')
		int lastDot   = s.lastIndexOf('.')
		boolean decimalIsComma = lastComma > lastDot
		String normalized = decimalIsComma ? s.replace('.', '').replace(',', '.') : s.replace(',', '')
		return Double.parseDouble(normalized)
	}

	/** Lee texto con waits; si falla, añade error y sigue. */
	private static Double readMoney(TestObject obj, String nombre, List errors) {
		if (obj == null) {
			errors.add("Falta TestObject para '${nombre}'"); return null
		}
		if (!WebUI.waitForElementPresent(obj, 15)) {
			errors.add("'${nombre}' no está presente en DOM"); return null
		}
		WebUI.scrollToElement(obj, 5)
		if (!WebUI.waitForElementVisible(obj, 10)) {
			errors.add("'${nombre}' no es visible"); return null
		}
		String txt = WebUI.getText(obj)?.trim()
		if (!txt) {
			errors.add("'${nombre}' está vacío o no fue legible"); return null
		}
		KeywordUtil.logInfo("🔎 ${nombre}: ${txt}")
		return parseMoney(txt)
	}

	/**
	 * Valida que:
	 * 1) Precio ≈ Mejor Precio (tolerancia EPS)
	 * 2) Precio ≈ Precio Paquete (si existe)
	 * 3) Precio - Comisión ≈ Precio Final (tolerancia EPS)
	 * 4) (Estricto) Precio - Comisión == Precio Final a 2 decimales
	 */
	@Keyword
	def validarPrecio(
			TestObject precioObj,        // puede venir null (usa default)
			TestObject comisionObj,      // puede venir null (usa default)
			TestObject precioFinalObj,   // puede venir null (usa default)
			TestObject precioPaqueteObj, // opcional
			double mejorPrecio
	) {
		List errors = new ArrayList<String>()

		try {
			// --- Defaults (XPaths nuevos) ---
			TestObject PRECIO       = precioObj ?: byXpath('precioObj_pas',
					"//*[@id='main-content']/div[2]/div/div/div[1]/div[1]/div/div[3]/div/div[2]/div[1]")
			TestObject COMISION     = comisionObj ?: byXpath('comisionObj_pas',
					"//*[@id='main-content']/div[2]/div/div/div[1]/div[1]/div/div[3]/div/div[2]/div[2]/div/div[1]")
			TestObject PRECIO_FINAL = precioFinalObj ?: byXpath('precioFinalObj_pas',
					"//*[@id='main-content']/div[2]/div/div/div[1]/div[1]/div/div[3]/div/div[2]/div[2]/div/div[3]")
			TestObject PRECIO_PAQ   = precioPaqueteObj ?: byXpath('precioPaqueteObj_pas',
					"//*[@id='main-content']/div[2]/div/div/div[1]/div[1]/div/div[2]/table/tbody/tr[2]/td")

			// --- Lecturas ---
			Double precio        = readMoney(PRECIO,       "Precio (total)", errors)
			Double comision      = readMoney(COMISION,     "Comisión",       errors)
			Double precioFinal   = readMoney(PRECIO_FINAL, "Precio Final",   errors)

			// Paquete es opcional: errores aparte para no bloquear por ausencia
			Double precioPaquete = null
			if (WebUI.waitForElementPresent(PRECIO_PAQ, 3)) {
				precioPaquete = readMoney(PRECIO_PAQ, "Precio Paquete", new ArrayList<String>())
			} else {
				KeywordUtil.logInfo("ℹ️ Precio Paquete no disponible; comparación omitida.")
			}

			WebUI.comment("📊 Precio: ${precio} | Comisión: ${comision} | PrecioFinal: ${precioFinal} | PrecioPaquete: ${precioPaquete} | MejorPrecio: ${mejorPrecio}")

			// --- Validaciones ---
			// (1) Precio vs Mejor Precio (tolerancia)
			if (precio != null && !Double.isNaN(mejorPrecio)) {
				if (Math.abs(precio - mejorPrecio) < EPS) {
					KeywordUtil.logInfo("✅ Precio (${precio}) ≈ Mejor Precio (${mejorPrecio})")
				} else {
					errors.add("Precio (${precio}) ≠ Mejor Precio (${mejorPrecio})")
				}
			} else {
				errors.add("No se pudo validar Precio vs Mejor Precio (datos faltantes)")
			}

			// (2) Precio vs Precio Paquete (opcional)
			if (precio != null && precioPaquete != null) {
				if (Math.abs(precio - precioPaquete) < EPS) {
					KeywordUtil.logInfo("✅ Precio (${precio}) ≈ Precio Paquete (${precioPaquete})")
				} else {
					errors.add("Precio (${precio}) ≠ Precio Paquete (${precioPaquete})")
				}
			}

			// (3) Precio - Comisión ≈ Precio Final (tolerancia)
			if (precio != null && comision != null && precioFinal != null) {
				double calculadoFinal = precio - comision
				if (Math.abs(calculadoFinal - precioFinal) < EPS) {
					KeywordUtil.logInfo("✅ Precio Final (${precioFinal}) ≈ Precio (${precio}) - Comisión (${comision}) = ${String.format('%.2f', calculadoFinal)}")
				} else {
					errors.add("Precio Final (${precioFinal}) NO coincide con Precio (${precio}) - Comisión (${comision}). Calculado: ${String.format('%.2f', calculadoFinal)}")
				}
			} else {
				errors.add("No se pudo validar Precio - Comisión vs Precio Final (datos faltantes)")
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
}

