package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class HTTP_ThingsboardAsk  extends TranslatorBlock {

	public HTTP_ThingsboardAsk (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		translator.addHeaderFile("WiFiClientSecure.h");
		//translator.addSetupCommand("Serial.begin(115200);");
		
		String JSON="//--------------------------------------- tiny JSON Parser\n" + 
				"String parseJSON(String xml,String suchtext) {\n" + 
				"  String valStr = \"\";                // Hilfsstring\n" + 
				"\n" + 
				"  \n" + 
				"  //IOTW_PRINTLN(\"string:\"+xml);\n" + 
				"  int start,ende,doppel,ende1,ende2; // Index im Text\n" + 
				"  String antwort =\"\";\n" + 
				"  start = xml.indexOf(suchtext);     // Suche Text\n" + 
				" \n" + 
				"  if (start >= 0) {                   // Item gefunden\n" + 
				"    antwort = xml.substring(start+suchtext.length(),xml.length());\n" + 
				"   // IOTW_PRINTLN(\"antw:\"+antwort);\n" + 
				"    doppel = antwort.indexOf(':');\n" + 
				"   // IOTW_PRINTLN(\"doppel:\"+String(doppel));\n" + 
				"    \n" + 
				"    antwort = antwort.substring(doppel+1,antwort.length());\n" + 
				"   // IOTW_PRINTLN(\"antw:\"+antwort);\n" + 
				"    \n" + 
				"    ende1 =  antwort.indexOf(',');  // Ende der Zahl\n" + 
				"   // IOTW_PRINTLN(\", =\"+String(ende1));\n" + 
				"    ende2 =  antwort.indexOf('\"',1);  // Ende der Zahl\n" + 
				"   // IOTW_PRINTLN(\"# =\"+String(ende2));\n" + 
				"    if (ende1 >= 0) {\n" + 
				"      ende = ende1;\n" + 
				"    } else {\n" + 
				"      ende = ende2;\n" + 
				"    }\n" + 
				"    valStr= antwort.substring(0,ende);// itemtext\n" + 
				"    valStr.replace(\"\\\"\",\" \");          // delete ggf. vorhandene \"\n" + 
				"    valStr.trim();\n" + 
				"  } \n" + 
				"  else                             // Item nicht gefunden\n" + 
				"  IOTW_PRINT(\"error - no such item: \"+suchtext);\n" + 
				"  return valStr;\n" + 
				"}\n" + 
				"";
  	    translator.addDefinitionCommand(JSON);
		
		String httpGET ="// request actual timeseries data from thingsboard\n"
				+ "String askThingsboard(String server, String device, String JWT,String key) {\n"
				+ "  HTTPClient http;              // HTTPClient-Objekt deklarieren\n"
				+ "  WiFiClient wifiClient;        // Für HTTP-Verbindungen\n"
				+ "  WiFiClientSecure wifiClientSecure; // Für HTTPS-Verbindungen\n"
				+ "\n"
				+ "  // Bestimme, ob die Verbindung HTTP oder HTTPS ist\n"
				+ "  bool isSecure = false;\n"
				+ "  String req = \"/api/plugins/telemetry/DEVICE/\"+device+\"/values/timeseries?keys=\"+key;\n"
				+ "\n"
				+ "  if (server.startsWith(\"https\")) {\n"
				+ "    isSecure = true;\n"
				+ "    req = server + req;\n"
				+ "  } \n"
				+ "  else {\n"
				+ "    if (server.startsWith(\"http\")) {\n"
				+ "      req = server + req; \n"
				+ "    } \n"
				+ "    else {  \n"
				+ "      req = \"http://\" + server + req;\n"
				+ "    }\n"
				+ "  }\n"
				+ "\n"
				+ "  // JSON-Daten für den GET-Request\n"
				+ "  IOTW_PRINTLN(req);\n"
				+ "\n"
				+ "  // Verbindung aufbauen\n"
				+ "  if (isSecure) {\n"
				+ "    // HTTPS-Verbindung\n"
				+ "    wifiClientSecure.setInsecure(); // Zertifikatsprüfung deaktivieren (anpassen, wenn Zertifikate verwendet werden)\n"
				+ "    http.begin(wifiClientSecure, req); // HTTPS-Verbindung aufbauen\n"
				+ "  } \n"
				+ "  else {\n"
				+ "    // HTTP-Verbindung\n"
				+ "    http.begin(wifiClient, req); // HTTP-Verbindung aufbauen\n"
				+ "  }\n"
				+ "\n"
				+ "  // HTTP-Header hinzufügen\n"
				+ "  http.addHeader(\"Authorization\", \"Bearer \"+JWT);\n"
				+ "  http.addHeader(\"Accept\", \"application/json\");\n"
				+ "\n"
				+ "  // POST-Anfrage senden\n"
				+ "  int httpCode = http.GET(); // Sende Daten\n"
				+ "  String payload = http.getString(); // Antwort des Servers auslesen\n"
				+ "  IOTW_PRINTLN(\"HTTP-Code: \" + String(httpCode)); // Statuscode anzeigen\n"
				+ "  IOTW_PRINTLN(\"Antwort: \" + payload); // Antwort anzeigen\n"
				+ "\n"
				+ "  http.end(); // Verbindung schließen\n"
				+ "  payload = parseJSON(payload,\"value\");\n"
				+ "  return payload; // payload zurückgeben\n"
				+ "}";

		translator.addDefinitionCommand(httpGET);
		
		String host,device,JWT,key,ret;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    device = translatorBlock.toCode();
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    key = translatorBlock.toCode();
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    JWT = translatorBlock.toCode();
	    
	    ret = "askThingsboard("+host+","+device+","+JWT+","+key+")"; 
        return codePrefix + ret + codeSuffix;
	 	}
}

