package utils
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling

public class ValidacionesPreciosTabla {

	@Keyword
	def validacionDesglose(double mejorPrecio) {

		// --- Función auxiliar para obtener valor Double --- //
		def obtenerValorDouble = { String xpath, String nombre ->
			TestObject obj = new TestObject(nombre)
			obj.addProperty("xpath", ConditionType.EQUALS, xpath)

			if (!WebUI.waitForElementVisible(obj, 10, FailureHandling.OPTIONAL)) {
				KeywordUtil.logInfo("⚠️ No se encontró '${nombre}'")
				return null
			}
			String text = WebUI.getText(obj).trim()
			if (text == "" || text == "-") return null
			KeywordUtil.logInfo("🔎 ${nombre}: ${text}")
			return Double.parseDouble(text.replaceAll("[^0-9.]", ""))
		}

		// --- SERVICIOS COMISIONABLE --- //
		Double totalReservaComisionable = obtenerValorDouble("(//tr[@class='js-dropdown-commissionable']/td)[2]", "Total Reserva Comisionable")
		Double ivaComisionable          = obtenerValorDouble("(//tr[@class='js-dropdown-commissionable']/td)[3]", "IVA Comisionable")
		Double tarifaComisionable       = obtenerValorDouble("(//tr[@class='js-dropdown-commissionable']/td)[4]", "Tarifa Comisionable")
		String porcentajeComisionStr    = WebUI.getText(new TestObject().addProperty("xpath", ConditionType.EQUALS, "(//tr[@class='js-dropdown-commissionable']/td)[5]"))
		Double comisionTotal            = obtenerValorDouble("(//tr[@class='js-dropdown-commissionable']/td)[6]", "Comisión Total")

		// Convertir % Comisión a número decimal
		Double porcentajeComision = porcentajeComisionStr?.replace("%", "")?.replace(",", ".")?.trim()?.isDouble() ?
				Double.parseDouble(porcentajeComisionStr.replace("%", "").replace(",", ".").trim()) / 100 : null

		// --- SERVICIOS NO COMISIONABLE --- //
		Double totalReservaNoCom = obtenerValorDouble("(//tr[@class='js-dropdown-nonCommissionable']/td)[2]", "Total Reserva No Comisionable")

		// --- TOTALES --- //
		Double totalGeneral      = obtenerValorDouble("(//table[@class='confirm-booking__tableBreakdown__table']//tr[last()]/td)[2]", "Total General")

		// --- Validaciones adicionales --- //

		// 1️⃣ Comisión total = (Tarifa Comisionable [+ IVA Comisionable]) * % Comisión
		if (tarifaComisionable != null && porcentajeComision != null && comisionTotal != null) {

			double baseComisionable = tarifaComisionable
			if (ivaComisionable != null && ivaComisionable > 0) {
				baseComisionable += ivaComisionable
				KeywordUtil.logInfo("🧮 Base comisionable con IVA incluido: ${baseComisionable} (Tarifa ${tarifaComisionable} + IVA ${ivaComisionable})")
			} else {
				KeywordUtil.logInfo("🧾 Base comisionable sin IVA: ${baseComisionable}")
			}

			double esperado = baseComisionable * porcentajeComision
			if (Math.abs(esperado - comisionTotal) < 0.01) {
				KeywordUtil.logInfo("✅ Comisión correcta: ${comisionTotal} coincide con (${baseComisionable} x ${porcentajeComisionStr})")
			} else {
				KeywordUtil.markFailed("❌ Comisión incorrecta: esperado ${String.format('%.2f', esperado)}, obtenido ${comisionTotal}")
			}
		}

		// 2️⃣ Servicio No Comisionable = Total General - Servicio Comisionable
		if (totalGeneral != null && totalReservaComisionable != null && totalReservaNoCom != null) {
			double esperado = totalGeneral - totalReservaComisionable
			if (Math.abs(esperado - totalReservaNoCom) < 0.01) {
				KeywordUtil.logInfo("✅ Servicio NO comisionable correcto: ${totalReservaNoCom}")
			} else {
				KeywordUtil.markFailed("❌ Servicio NO comisionable incorrecto: esperado ${esperado}, obtenido ${totalReservaNoCom}")
			}
		}

		// 3️⃣ Mejor precio = Servicio No Comisionable + Tarifa Comisionable (con tolerancia ±1.0)
		if (totalReservaNoCom != null && tarifaComisionable != null) {
			double esperado = totalReservaNoCom + tarifaComisionable
			double diferencia = Math.abs(esperado - mejorPrecio)
			double tolerancia = 1.0  // 💡 Tolerancia para evitar falsos negativos

			if (diferencia <= tolerancia) {
				KeywordUtil.logInfo("✅ Mejor precio correcto: esperado ${String.format('%.2f', esperado)}, obtenido ${String.format('%.2f', mejorPrecio)} (Δ=${String.format('%.2f', diferencia)})")
			} else {
				KeywordUtil.markFailed("❌ Mejor precio incorrecto: esperado ${String.format('%.2f', esperado)}, obtenido ${String.format('%.2f', mejorPrecio)} (Δ=${String.format('%.2f', diferencia)})")
			}
		}
	}
}
