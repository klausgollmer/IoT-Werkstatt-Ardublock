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
				+ "// httpClientGET: HTTP GET-Request mit HTTPClient\n"
				+ "//\n"
				+ "// Parameter:\n"
				+ "//   host:   z.B. \"api.thingspeak.com\" oder \"http://api.thingspeak.com\"\n"
				+ "//   cmd:    z.B. \"/update?api_key=DEIN_KEY&field1=123\"\n"
				+ "//   antwort: Enthält am Ende die Serverantwort\n"
				+ "//\n"
				+ "// Rückgabewert:\n"
				+ "//   1 = Erfolg (HTTP 200 oder Redirect 301)\n"
				+ "//   0 = Fehler (Verbindungsfehler oder HTTP-Code != 200/301)\n"
				+ "// ----------------------------------------------------------------------------\n"
				+ "int httpClientGET(String host, String cmd, String &antwort) {\n"
				+ "  // Debug-/Fehler-Strings\n"
				+ "  String errorString      = \"\";\n"
				+ "  String errorStringDebug = \"\";\n"
				+ "  IOTW_PRINT(\"http-GET \"+host+\" ... \");\n"
				+ "  int ret = 0;  // Rückgabewert: 1 = Erfolg, 0 = Fehler\n"
				+ "\n"
				+ "  // 1) WLAN-Verbindung prüfen\n"
				+ "  if (WiFi.status() != WL_CONNECTED) {\n"
				+ "    errorString += \"⚠ no WiFi connection\";\n"
				+ "    IOTW_PRINTLN(errorString);\n"
				+ "    return 0; \n"
				+ "  }\n"
				+ "\n"
				+ "  // 2) URL-Zusammenbau (Verhindert doppelte http:// Präfixe)\n"
				+ "  String url;\n"
				+ "  if (host.startsWith(\"http://\") || host.startsWith(\"https://\")) {\n"
				+ "    url = host + cmd;  // Falls host schon mit http(s) beginnt, direkt übernehmen\n"
				+ "  } \n"
				+ "  else {\n"
				+ "    url = \"http://\" + host + cmd;\n"
				+ "  }\n"
				+ "\n"
				+ "  // Debug-Ausgabe vorbereiten\n"
				+ "  errorStringDebug += \"[INFO] Using HTTPClient for GET:\\n\";\n"
				+ "  errorStringDebug += \"URL: \" + url + \"\\n\";\n"
				+ "\n"
				+ "  // 3) HTTPClient vorbereiten\n"
				+ "  {\n"
				+ "    WiFiClient client;\n"
				+ "    HTTPClient http;\n"
				+ "\n"
				+ "    if (!http.begin(client, url)) {\n"
				+ "      errorString += \"❌ Unable to connect\";\n"
				+ "      IOTW_PRINTLN(errorString);\n"
				+ "      return 0;\n"
				+ "    }\n"
				+ "\n"
				+ "    // 4) GET-Anfrage senden\n"
				+ "    int httpCode = http.GET();\n"
				+ "    if (httpCode > 0) {\n"
				+ "      // Server hat geantwortet\n"
				+ "      String payload = http.getString();\n"
				+ "      antwort = payload;\n"
				+ "      antwort.trim();\n"
				+ "      if (!antwort.isEmpty()) errorStringDebug+=antwort;\n"
				+ "      if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {\n"
				+ "        errorString += \"✅ success\";\n"
				+ "        ret = 1;  // Erfolg\n"
				+ "      } \n"
				+ "      else {\n"
				+ "        errorString += \"❌ Unexpected HTTP-Code: \" + String(httpCode);\n"
				+ "      }\n"
				+ "\n"
				+ "    } \n"
				+ "    else {\n"
				+ "      // Fehlerfall\n"
				+ "      errorString += \"❌ HTTP GET failed, error: \" + http.errorToString(httpCode);\n"
				+ "    }\n"
				+ "\n"
				+ "    http.end();\n"
				+ "  }\n"
				+ "\n"
				+ "  // 5) Finale Ausgaben\n"
				+ "  IOTW_PRINTLN(errorString);\n"
				+ "#if (IOTW_DEBUG_LEVEL >1)\n"
				+ "  IOTW_PRINTLN(errorStringDebug);\n"
				+ "#endif\n"
				+ "\n"
				+ "  return ret;\n"
				+ "}\n";

		
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

