package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.model.FailureHandling
import org.openqa.selenium.WebElement
import java.text.DecimalFormat

public class ValidacionesTabla {

	@Keyword
	def validarTablaComisionesYTotales() {

		// ===== Config =====
		boolean FAIL_AL_FINAL_SI_HAY_HALLAZGOS = true   // ⇦ ponlo en false si SOLO quieres dejar comentarios sin fallar
		double TOL = 0.5                                 // tolerancia de comparación monetaria

		// ===== Helpers =====
		def df2 = new DecimalFormat("#,##0.00")

		def createTO = { String xpath ->
			TestObject to = new TestObject()
			to.addProperty("xpath", ConditionType.EQUALS, xpath)
			return to
		}

		def parseMoney = { String text ->
			if (!text) return 0.0
			String cleaned = text.replace('\u00A0',' ')
					.replaceAll("[^0-9,.-]", "")
					.replace(",", "")
					.trim()
			return cleaned ? Double.parseDouble(cleaned) : 0.0
		}

		def parsePercent = { String text ->
			if (!text) return 0.0
			String cleaned = text.replaceAll("[^0-9,.-]", "")
					.replace(",", ".")
					.trim()
			return cleaned ? Double.parseDouble(cleaned) : 0.0
		}

		def approxEqual = { double a, double b, double tol = TOL ->
			return Math.abs(a - b) <= tol
		}

		// ¿El texto sugiere que la BASE NO incluye IVA todavía?
		def needsIva = { String texto ->
			if (texto == null) return false
			texto = texto.toUpperCase()
			return texto.contains("SIN IVA") || texto.contains("+IVA")
		}

		// ===== 1) Tabla presente =====
		TestObject tablaTO = createTO("//*[@class='confirm-booking__tableBreakdown__table']")
		if (!WebUI.waitForElementPresent(tablaTO, 30, FailureHandling.OPTIONAL)) {
			WebUI.takeScreenshot()
			KeywordUtil.markFailedAndStop("❌ No se encontró la tabla de desglose de comisiones.")
		}

		// ===== 2) Detectar estructura (4 o 6 columnas) =====
		TestObject filaComisionTO = createTO("//*[@id='contenedor']//table/tbody/tr[3]/td")
		List<WebElement> columnas = WebUI.findWebElements(filaComisionTO, 10)
		int colCount = columnas.size()
		int baseCol, pctCol, commCol
		if (colCount <= 4) { baseCol = 2; pctCol = 3; commCol = 4; WebUI.comment("🧩 Estructura detectada: 4 columnas") }
		else { baseCol = 2; pctCol = 5; commCol = 6; WebUI.comment("🧩 Estructura detectada: 6 columnas") }

		// ===== 3) Leer valores clave =====
		TestObject baseTO  = createTO("//*[@id='contenedor']//table/tbody/tr[3]/td[${baseCol}]")
		TestObject pctTO   = createTO("//*[@id='contenedor']//table/tbody/tr[3]/td[${pctCol}]")
		TestObject commTO  = createTO("//*[@id='contenedor']//table/tbody/tr[3]/td[${commCol}]")

		String baseTxt = WebUI.getText(baseTO).trim()
		String pctTxt  = WebUI.getText(pctTO).trim()
		String commTxt = WebUI.getText(commTO).trim()

		double base  = parseMoney(baseTxt)
		double pct   = parsePercent(pctTxt)
		double comm  = parseMoney(commTxt)

		// ===== 4) Cálculos posibles (para diagnosticar tabla) =====
		// A) Comisión SIN IVA
		double calcSinIVA = base * pct / 100.0
		// B) Comisión CON IVA (16%) — usar solo si la base está SIN IVA
		double calcConIVA = calcSinIVA * 1.16
		// Heurística: ¿la base dice “SIN IVA” o “+IVA”? -> deberíamos usar CON IVA, si no usar SIN IVA
		boolean aplicarIVA = needsIva(baseTxt)
		double calcEsperado = aplicarIVA ? calcConIVA : calcSinIVA

		WebUI.comment("🧮 base=${df2.format(base)} | %=${pct} | sinIVA=${df2.format(calcSinIVA)} | conIVA=${df2.format(calcConIVA)} | mostrado=${df2.format(comm)} | regla=${aplicarIVA ? 'CON IVA' : 'SIN IVA'}")

		List<String> hallazgos = []

		// ===== 5) Comparación principal =====
		if (!approxEqual(calcEsperado, comm)) {
			// Dejar COMENTARIO explicativo y diagnóstico
			String msg = "❌ Comisión incorrecta: esperado ${df2.format(calcEsperado)} (${aplicarIVA ? 'con IVA' : 'sin IVA'}), obtenido ${df2.format(comm)}."
			WebUI.comment(msg)
			KeywordUtil.markWarning(msg)
			hallazgos.add(msg)

			// Diagnóstico adicional: ¿la tabla parece estar usando la regla contraria?
			if (approxEqual(calcSinIVA, comm)) {
				String diag = "ℹ️ Diagnóstico: La tabla parece calcular la comisión SIN IVA, pero el texto sugiere que debía incluir IVA (base indica 'sin IVA' o '+IVA')."
				WebUI.comment(diag)
				KeywordUtil.markWarning(diag)
				hallazgos.add(diag)
			} else if (approxEqual(calcConIVA, comm) && !aplicarIVA) {
				String diag = "ℹ️ Diagnóstico: La tabla parece sumar IVA a la comisión aunque la base ya lo incluiría (no hay 'sin IVA'/' +IVA' en el texto). Posible doble IVA."
				WebUI.comment(diag)
				KeywordUtil.markWarning(diag)
				hallazgos.add(diag)
			} else {
				String diag = "ℹ️ Diagnóstico: La comisión mostrada no coincide ni con la fórmula con IVA ni sin IVA. Revisar redondeos previos, tipo de cambio o base incorrecta."
				WebUI.comment(diag)
				KeywordUtil.markWarning(diag)
				hallazgos.add(diag)
			}
		} else {
			WebUI.comment("✅ Comisión correcta según regla detectada (${aplicarIVA ? 'con IVA' : 'sin IVA'}).")
		}

		// ===== 6) No comisionables (si existe fila 4) =====
		try {
			TestObject nonCommPctTO  = createTO("//*[@id='contenedor']//table/tbody/tr[4]/td[${pctCol}]")
			TestObject nonCommCommTO = createTO("//*[@id='contenedor']//table/tbody/tr[4]/td[${commCol}]")

			if (WebUI.verifyElementPresent(nonCommPctTO, 2, FailureHandling.OPTIONAL) &&
				WebUI.verifyElementPresent(nonCommCommTO, 2, FailureHandling.OPTIONAL)) {

				String nonPct = WebUI.getText(nonCommPctTO).trim()
				String nonCom = WebUI.getText(nonCommCommTO).trim()

				if ((nonPct && nonPct != "-") || (nonCom && nonCom != "-")) {
					String msg = "⚠️ Los servicios no comisionables muestran valores inesperados: %='${nonPct}', Comisión='${nonCom}'."
					WebUI.comment(msg)
					KeywordUtil.markWarning(msg)
					hallazgos.add(msg)
				} else {
					WebUI.comment("✅ No comisionables sin valores de comisión (correcto).")
				}
			}
		} catch (Throwable ignore) {
			// Si no existe fila de no comisionables, seguimos
		}

		// ===== 7) Totales =====
		TestObject totalTablaTO   = createTO("//*[@id='contenedor']//table/tbody/tr[last()]/td[${baseCol}]")
		TestObject totalReservaTO = createTO("//*[@id='main-content']//span[2]")

		double totalTabla   = parseMoney(WebUI.getText(totalTablaTO))
		double totalReserva = parseMoney(WebUI.getText(totalReservaTO))

		if (!approxEqual(totalTabla, totalReserva)) {
			String msg = "❌ Total de la tabla (${df2.format(totalTabla)}) ≠ Total de reserva (${df2.format(totalReserva)})."
			WebUI.comment(msg)
			KeywordUtil.markWarning(msg)
			hallazgos.add(msg)
		} else {
			WebUI.comment("✅ Totales coinciden: ${df2.format(totalTabla)}.")
		}

		// ===== 8) Resultado final =====
		if (!hallazgos.isEmpty()) {
			WebUI.takeScreenshot()
			String resumen = "⛔ Se detectaron ${hallazgos.size()} inconsistencias en la tabla:\n - " + hallazgos.join("\n - ")
			if (FAIL_AL_FINAL_SI_HAY_HALLAZGOS) {
				KeywordUtil.markFailedAndStop(resumen)
			} else {
				KeywordUtil.markWarning(resumen)
			}
		} else {
			KeywordUtil.logInfo("🎯 Validación completa del desglose de comisiones: SIN hallazgos.")
		}
	}
}


