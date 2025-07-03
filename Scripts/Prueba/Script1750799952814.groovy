import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

WebUI.openBrowser('')

WebUI.navigateToUrl('https://euromundooti.juniper.es/login/?returnURL=%2F')

WebUI.click(findTestObject('Object Repository/Prueba/Page_Acceder  Euronuevo Internacional/div_Usuario                        Por favo_610635'))

WebUI.click(findTestObject('Object Repository/Prueba/Page_Acceder  Euronuevo Internacional/input_Usuario_user'))

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Acceder  Euronuevo Internacional/input_Usuario_user'), 'avg2873')

WebUI.setEncryptedText(findTestObject('Object Repository/Prueba/Page_Acceder  Euronuevo Internacional/input_Contrasea_password'), 
    '/WKsGlZmRsFIgGZuQpPs1g==')

WebUI.click(findTestObject('Object Repository/Prueba/Page_Acceder  Euronuevo Internacional/input_Por favor, introduzca la contrasea_pr_d431d7'))

WebUI.click(findTestObject('Object Repository/Prueba/Page_Acceder  Euronuevo Internacional/button_Acceder'))

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Acceder  Euronuevo Internacional/input_Usuario_user'), 'agv2873')

WebUI.click(findTestObject('Object Repository/Prueba/Page_Acceder  Euronuevo Internacional/button_Acceder'))

WebUI.click(findTestObject('Object Repository/Prueba/Page_Euronuevo Internacional/a_Gran Deal'))

WebUI.click(findTestObject('Object Repository/Prueba/Page_Euronuevo Internacional/span_Seleccione un destino'))

WebUI.click(findTestObject('Object Repository/Prueba/Page_Euronuevo Internacional/span_Seleccione un origen'))

WebUI.click(findTestObject('Object Repository/Prueba/Page_Euronuevo Internacional/button_Buscar'))

WebUI.click(findTestObject('Object Repository/Prueba/Page_Euronuevo Internacional/input_Ida_packagesaer-searcher-_ctl1__ctl1__a72932'))

WebUI.click(findTestObject('Object Repository/Prueba/Page_Euronuevo Internacional/a_18'))

WebUI.click(findTestObject('Object Repository/Prueba/Page_Euronuevo Internacional/button_Buscar'))

WebUI.click(findTestObject('Object Repository/Prueba/Page_AER EMA XX  EUR Gdeals  EL Lminute  De_836df4/button_Reservar'))

WebUI.click(findTestObject('Object Repository/Prueba/Page_Reserva  AER EMA XX  EUR Gdeals  EL Lm_a4674d/span_---'))

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Reserva  AER EMA XX  EUR Gdeals  EL Lm_a4674d/input_Nombre_pax-name'), 
    'Johan')

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Reserva  AER EMA XX  EUR Gdeals  EL Lm_a4674d/input_Apellidos_pax-surname'), 
    'Gomez')

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Reserva  AER EMA XX  EUR Gdeals  EL Lm_a4674d/input_F. nacimiento_pax-birthday'), 
    '25/10/1988')

WebUI.click(findTestObject('Object Repository/Prueba/Page_Reserva  AER EMA XX  EUR Gdeals  EL Lm_a4674d/span_---_1'))

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Reserva  AER EMA XX  EUR Gdeals  EL Lm_a4674d/input_Nombre_pax-name_1'), 
    'Juan')

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Reserva  AER EMA XX  EUR Gdeals  EL Lm_a4674d/input_Apellidos_pax-surname_1'), 
    'Gomez')

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Reserva  AER EMA XX  EUR Gdeals  EL Lm_a4674d/input_F. nacimiento_pax-birthday_1'), 
    '22/09/1999')

WebUI.click(findTestObject('Object Repository/Prueba/Page_Reserva  AER EMA XX  EUR Gdeals  EL Lm_a4674d/button_Finalizar reserva'))

WebUI.click(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/button_Cerrar'))

WebUI.selectOptionByValue(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/select_Selecciona                          _88e379'), 
    '{"name":"Johan","surname":"Gomez","treatment":"MRS","countryCode":"AR","phoneCountryCode":"54","birthDate":"1988-10-25T00:00:00","email":"noe.lawler@ejuniper.com","agency":"Agencia Prepago Juniper","agencyPhone":"321654987","lDataExtra":[],"dataOriginType":["passenger","agency"],"age":36,"mobilePhoneCountryCode":"54","agencyEmail":"noe.lawler@ejuniper.com"}', 
    true)

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/input_Documento de identidad  Pasaporte_holder_id'), 
    '75765765')

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/input_Ciudad_holder_city'), 
    'Bogota')

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/input_Cdigo postal_holder_zipcode'), 
    '10101')

WebUI.click(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/input_Cdigo postal_holder_zipcode'))

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/input_Direccin_holder_address'), 
    'Virrey')

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/input_Telfono_holder_phone'), 
    '3176646465')

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/input_Tramitado por_booked_by'), 
    'johana gomez')

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/input_Nombre_billing_name'), 
    'johana')

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/input_Direccin_billing_address'), 
    'virrey')

WebUI.setText(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/input_E-mail de facturacin_billing_email'), 
    'johana.gomez@ejuniper.com')

WebUI.click(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/input_El horario antes informado, es Horari_4c8ba5'))

WebUI.click(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/input_Consulte las condiciones_termsAndConditions'))

WebUI.rightClick(findTestObject('Object Repository/Prueba/Page_Confirmar reserva  Euronuevo Internacional/button_Finalizar reserva'))

