package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class HTTP_SenseboxSend  extends TranslatorBlock {

	public HTTP_SenseboxSend (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");

		//translator.addSetupCommand("Serial.begin(115200);");
/*		
		String Client, Check;
		if (translator.isGPRSProgram()) {
			Client= " TinyGsmClient client(modem);// Client über GSM\n";
			Check=  " int ok = checkMobilfunk();\n";
		} else {
			Client= " WiFiClient client; // Client über WiFi\n";
			Check=  " int ok = 1;\n";
		}
*/
		translator.addDefinitionCommand("WiFiClient wifiClient;");
		/*
		String httpPOST ="//-------------------------OpenSenseMAP  ------ HTTP-POST\n" +
		"void sendSenseMap(String server, float measurement, int digits, String SENSEBOX_ID, String sensorId) {\n" +
        "  HTTPClient http; //Declare object of class HTTPClient\n" + 
        "  //Json erstellen\n" + 
        "  String jsonValue = \"{\\\"value\\\":\" + String(measurement)+ \"}\";\n" + 
        "  //Mit OSeM Server verbinden und POST Operation durchführen\n" + 
        "  String req=\"http://\"+server+\"/boxes/\"+SENSEBOX_ID+\"/\"+sensorId;\n" + 
        "  IOTW_PRINTLN(req);\n" + 
        "  http.begin(wifiClient,req); //Specify request destination\n" + 
        "  http.addHeader(\"Content-Type\", \"application/json\"); //\n" + 
        "  int httpCode = http.POST(jsonValue); //Send the request\n" + 
        "  String payload = http.getString(); //Get the response payload\n" + 
        "  //IOTW_PRINT(httpCode);  //Print HTTP return code\n" + 
        "  IOTW_PRINTLN(payload); //Print request response payload\n" + 
        "  http.end(); //Close connection\n" + 
        " }";
		*/
		
		String httpPOST = "// ----------------------------------------------------------------------------\n"
				+ "// sendSenseMap: Sendet Messwerte an OpenSenseMap per HTTP POST\n"
				+ "//\n"
				+ "// Parameter:\n"
				+ "//   server:       z.B. \"api.opensensemap.org\"\n"
				+ "//   measurement:  Der Messwert (float)\n"
				+ "//   digits:       Anzahl der Nachkommastellen\n"
				+ "//   SENSEBOX_ID:  ID der SenseBox\n"
				+ "//   sensorId:     ID des Sensors\n"
				+ "//\n"
				+ "// ----------------------------------------------------------------------------\n"
				+ "void sendSenseMap(String server, float measurement, int digits, String SENSEBOX_ID, String sensorId) {\n"
				+ "  // Debug-/Fehlermeldungen\n"
				+ "  String errorString      = \"\";\n"
				+ "  String errorStringDebug = \"\";\n"
				+ "  \n"
				+ "  IOTW_PRINT(F(\"OpenSenseMap \"));\n"
				+ "  // 1) WLAN-Verbindung prüfen\n"
				+ "  if (WiFi.status() != WL_CONNECTED) {\n"
				+ "    errorString += \"⚠ no WiFi connection\";\n"
				+ "    IOTW_PRINTLN(errorString);\n"
				+ "    return;\n"
				+ "  }\n"
				+ "\n"
				+ "  // 2) JSON-Wert erstellen\n"
				+ "  String jsonValue = \"{\\\"value\\\":\" + String(measurement, digits) + \"}\";\n"
				+ "\n"
				+ "  // 3) URL zusammenbauen\n"
				+ "  String url;\n"
				+ "  if (server.startsWith(\"http://\") || server.startsWith(\"https://\")) {\n"
				+ "    url = server + \"/boxes/\" + SENSEBOX_ID + \"/\" + sensorId;\n"
				+ "  } \n"
				+ "  else {\n"
				+ "    url = \"http://\" + server + \"/boxes/\" + SENSEBOX_ID + \"/\" + sensorId;\n"
				+ "  }\n"
				+ "\n"
				+ "  // Debug-Ausgabe vorbereiten\n"
				+ "  errorStringDebug += \"[INFO] Using HTTPClient for POST:\\n\";\n"
				+ "  errorStringDebug += \"URL: \" + url + \"\\n\";\n"
				+ "  errorStringDebug += \"JSON: \" + jsonValue + \"\\n\";\n"
				+ "\n"
				+ "  // 4) HTTP-Request vorbereiten\n"
				+ "  WiFiClient wifiClient;\n"
				+ "  HTTPClient http;\n"
				+ "\n"
				+ "  http.begin(wifiClient, url);\n"
				+ "  http.addHeader(\"Content-Type\", \"application/json\");\n"
				+ "\n"
				+ "  // 5) POST-Anfrage senden\n"
				+ "  int httpCode = http.POST(jsonValue);\n"
				+ "  String payload = http.getString();\n"
				+ "\n"
				+ "  if (httpCode > 0) {\n"
				+ "    if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_CREATED) {\n"
				+ "      errorString += \"✅ success\");\n"
				+ "    } \n"
				+ "    else {\n"
				+ "      errorString += \"❌ Unexpected HTTP-Code: \" + String(httpCode);\n"
				+ "    }\n"
				+ "  } \n"
				+ "  else {\n"
				+ "    errorString += \"❌ HTTP POST failed: \" + http.errorToString(httpCode);\n"
				+ "  }\n"
				+ "\n"
				+ "  // Verbindung schließen\n"
				+ "  http.end();\n"
				+ "\n"
				+ "  // 6) Debug-/Fehlermeldungen ausgeben\n"
				+ "  IOTW_PRINTLN(errorString);\n"
				+ "#if (IOTW_DEBUG_LEVEL >1)\n"
				+ "  IOTW_PRINTLN(errorStringDebug);\n"
				+ "#endif\n"
				+ "}\n";
		translator.addDefinitionCommand(httpPOST);
		

		
		String host,boxID,senseID,ret,wert;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    boxID = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    senseID = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    wert = translatorBlock.toCode();
	    
	    ret = "sendSenseMap("+host+","+wert+",1,"+boxID+","+senseID+");// Post to OpenSenseMap\n"; 
           

        return codePrefix + ret + codeSuffix;
	 	}
}

