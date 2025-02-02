package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class HTTP_ShellyMeter2  extends TranslatorBlock {

	public HTTP_ShellyMeter2 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
				
		//translator.addSetupCommand("Serial.begin(115200);");
				
		String httpGET = "//--------------------------------------- http-GET with wifi-Client\r\n"
				+ "// ----------------------------------------------------------------------------\r\n"
				+ "// httpClientGET: HTTP GET-Request mit HTTPClient\r\n"
				+ "//\r\n"
				+ "// Parameter:\r\n"
				+ "//   host:   z.B. \"api.thingspeak.com\" oder \"http://api.thingspeak.com\"\r\n"
				+ "//   cmd:    z.B. \"/update?api_key=DEIN_KEY&field1=123\"\r\n"
				+ "//   antwort: Enthält am Ende die Serverantwort\r\n"
				+ "//\r\n"
				+ "// Rückgabewert:\r\n"
				+ "//   1 = Erfolg (HTTP 200 oder Redirect 301)\r\n"
				+ "//   0 = Fehler (Verbindungsfehler oder HTTP-Code != 200/301)\r\n"
				+ "// ----------------------------------------------------------------------------\r\n"
				+ "int httpClientGET(String host, String cmd, String &antwort) {\r\n"
				+ "  // Debug-/Fehler-Strings\r\n"
				+ "  String errorString      = \"\";\r\n"
				+ "  String errorStringDebug = \"\";\r\n"
				+ "  IOTW_PRINT(\"httpClientGET \"+host+\" \");\r\n"
				+ "  int ret = 0;  // Rückgabewert: 1 = Erfolg, 0 = Fehler\r\n"
				+ "\r\n"
				+ "  // 1) WLAN-Verbindung prüfen\r\n"
				+ "  if (WiFi.status() != WL_CONNECTED) {\r\n"
				+ "    errorString += \"⚠ no WiFi connection\";\r\n"
				+ "    IOTW_PRINTLN(errorString);\r\n"
				+ "    return 0; \r\n"
				+ "  }\r\n"
				+ "\r\n"
				+ "  // 2) URL-Zusammenbau (Verhindert doppelte http:// Präfixe)\r\n"
				+ "  String url;\r\n"
				+ "  if (host.startsWith(\"http://\") || host.startsWith(\"https://\")) {\r\n"
				+ "    url = host + cmd;  // Falls host schon mit http(s) beginnt, direkt übernehmen\r\n"
				+ "  } \r\n"
				+ "  else {\r\n"
				+ "    url = \"http://\" + host + cmd;\r\n"
				+ "  }\r\n"
				+ "\r\n"
				+ "  // Debug-Ausgabe vorbereiten\r\n"
				+ "  errorStringDebug += \"[INFO] Using HTTPClient for GET:\\n\";\r\n"
				+ "  errorStringDebug += \"URL: \" + url + \"\\n\";\r\n"
				+ "\r\n"
				+ "  // 3) HTTPClient vorbereiten\r\n"
				+ "  {\r\n"
				+ "    WiFiClient client;\r\n"
				+ "    HTTPClient http;\r\n"
				+ "\r\n"
				+ "    if (!http.begin(client, url)) {\r\n"
				+ "      errorString += \"❌ Unable to connect\";\r\n"
				+ "      IOTW_PRINTLN(errorString);\r\n"
				+ "      return 0;\r\n"
				+ "    }\r\n"
				+ "\r\n"
				+ "    // 4) GET-Anfrage senden\r\n"
				+ "    int httpCode = http.GET();\r\n"
				+ "    if (httpCode > 0) {\r\n"
				+ "      // Server hat geantwortet\r\n"
				+ "      String payload = http.getString();\r\n"
				+ "      antwort = payload;\r\n"
				+ "\r\n"
				+ "      if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {\r\n"
				+ "        errorString += \"✅ success\";\r\n"
				+ "        ret = 1;  // Erfolg\r\n"
				+ "      } \r\n"
				+ "      else {\r\n"
				+ "        errorString += \"❌ Unexpected HTTP-Code: \" + String(httpCode);\r\n"
				+ "      }\r\n"
				+ "\r\n"
				+ "    } \r\n"
				+ "    else {\r\n"
				+ "      // Fehlerfall\r\n"
				+ "      errorString += \"❌ HTTP GET failed, error: \" + http.errorToString(httpCode);\r\n"
				+ "    }\r\n"
				+ "\r\n"
				+ "    http.end();\r\n"
				+ "  }\r\n"
				+ "\r\n"
				+ "  // 5) Finale Ausgaben\r\n"
				+ "  IOTW_PRINTLN(errorString);\r\n"
				+ "#if (IOTW_DEBUG_LEVEL >1)\r\n"
				+ "  IOTW_PRINTLN(errorStringDebug);\r\n"
				+ "#endif\r\n"
				+ "\r\n"
				+ "  return ret;\r\n"
				+ "}";
		
		translator.addDefinitionCommand(httpGET);

		String Shelly = "// https://shelly-api-docs.shelly.cloud/gen1/#shelly-plug-plugs\n" + 
				"String parseShellyInfo(String xml,String suchtext) {\n" + 
				"  String valStr = \"\";                // Hilfsstring\n" + 
				"  int start, ende;                   // Index im Text\n" + 
				"  start = xml.indexOf(suchtext);     // Suche Text\n" + 
				"  if (start > 0) {                   // Item gefunden\n" + 
				"    start = start+suchtext.length(); // hinter Item kommt Zahl\n" + 
				"    ende =  xml.indexOf(',',start);  // Ende der Zahl\n" + 
				"    valStr= xml.substring(start,ende);// Zahltext\n" + 
				"  } \n" + 
				"  else                             // Item nicht gefunden\n" + 
				"  IOTW_PRINT(\"error - no such item: \"+suchtext);\n" + 
				"  return valStr;\n" + 
				"}\n" + 
				"void ShellySwitch(String host,int state) { \n" + 
				"  String cmd;\n" + 
				"  host=\"http://\"+host;\n"+
				"  if (state == 1) cmd = \"/relay/0?turn=on\" ;\n" + 
				"  else cmd = \"/relay/0?turn=off\";\n" + 
				"  String antwort;\n" + 
				"  httpClientGET(host,cmd,antwort);\n" + 
				"}\n";
                translator.addDefinitionCommand(Shelly);
		
 
					Shelly = "float ShellyMeter2(String host,int para,int gen) { \n" + 
							"  float  val    =  NAN;\n" + 
							"  String valStr = \"NAN\";\n" + 
							"  String antwort;\n" + 
							"  host=\"http://\"+host;\n" + 
							"  int tout = 2; // Retry\n" + 
							"  while ((tout > 0) && (isnan(val))) {\n" + 
							"    tout--; \n" + 
							"    if (gen == 1) {\n" + 
							"      if (httpClientGET(host,\"/status\",antwort)) { // success\n" + 
							"        if (parseShellyInfo(antwort,\"\\\"is_valid\\\":\") == \"true\") {\n" + 
							"          if (para == 1) {         \n" + 
							"            valStr=parseShellyInfo(antwort,\"\\\"power\\\":\");\n" + 
							"            val = valStr.toFloat();\n" + 
							"          }\n" + 
							"          if (para == 2) {         \n" + 
							"            valStr=parseShellyInfo(antwort,\"\\\"total\\\":\");\n" + 
							"            val = valStr.toFloat()/60.0;\n" + 
							"          }\n" + 
							"        }\n" + 
							"      }\n" + 
							"    } // generation 1 \n" + 
							"\n" + 
							"    if (gen == 2) {\n" + 
							"      if (httpClientGET(host,\"/rpc/Switch.GetStatus?id=0\",antwort)) { // success\n" + 
							"        if (1) { // parseShellyInfo(antwort,\"\\\"is_valid\\\":\") == \"true\") {\n" + 
							"          if (para == 1) {         \n" + 
							"            valStr=parseShellyInfo(antwort,\"\\\"apower\\\":\");\n" + 
							"            val = valStr.toFloat();\n" + 
							"          }\n" + 
							"          if (para == 2) {         \n" + 
							"            valStr=parseShellyInfo(antwort,\"\\\"total\\\":\");\n" + 
							"            val = valStr.toFloat();\n" + 
							"          }\n" + 
							"\n" + 
							"          if (para == 3) {         \n" + 
							"            valStr=parseShellyInfo(antwort,\"\\\"tC\\\":\");\n" + 
							"            val = valStr.toFloat();\n" + 
							"          }\n" + 
							"        }\n" + 
							"      }\n" + 
							"    } // generation 2 \n" + 
							"  }\n" + 
							"  return val;\n" + 
							"}\n" + 
							"";
					
	  	    translator.addDefinitionCommand(Shelly);
	  	    //translator.addSetupCommand("udp.begin(123);");

    	//translator.setNTPServerProgram(true);
    	
		String host,state,gen;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    gen = translatorBlock.toCode();
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    state = translatorBlock.toCode();
	    
	    String ret = "ShellyMeter2("+host+","+state+","+gen+")";
        return codePrefix + ret + codeSuffix;
	 	}
}

