package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoT_REST_RequestJWT  extends TranslatorBlock {

	public IoT_REST_RequestJWT (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		translator.addHeaderFile("WiFiClientSecure.h");
		translator.addSetupCommand("Serial.begin(115200);");
		
		String JSON="//--------------------------------------- tiny JSON Parser\n" + 
				"String parseJSON(String xml,String suchtext) {\n" + 
				"  String valStr = \"\";                // Hilfsstring\n" + 
				"\n" + 
				"  \n" + 
				"  //Serial.println(\"string:\"+xml);\n" + 
				"  int start,ende,doppel,ende1,ende2; // Index im Text\n" + 
				"  String antwort =\"\";\n" + 
				"  start = xml.indexOf(suchtext);     // Suche Text\n" + 
				" \n" + 
				"  if (start > 0) {                   // Item gefunden\n" + 
				"    antwort = xml.substring(start+suchtext.length(),xml.length());\n" + 
				"   // Serial.println(\"antw:\"+antwort);\n" + 
				"    doppel = antwort.indexOf(':');\n" + 
				"   // Serial.println(\"doppel:\"+String(doppel));\n" + 
				"    \n" + 
				"    antwort = antwort.substring(doppel+1,antwort.length());\n" + 
				"   // Serial.println(\"antw:\"+antwort);\n" + 
				"    \n" + 
				"    ende1 =  antwort.indexOf(',');  // Ende der Zahl\n" + 
				"   // Serial.println(\", =\"+String(ende1));\n" + 
				"    ende2 =  antwort.indexOf('\"',1);  // Ende der Zahl\n" + 
				"   // Serial.println(\"# =\"+String(ende2));\n" + 
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
				"  Serial.print(\"error - no such item: \"+suchtext);\n" + 
				"  return valStr;\n" + 
				"}\n" + 
				"";
  	    translator.addDefinitionCommand(JSON);
		
		
		String httpPOST = "// Request JSON Web token\n"
				+ "String requestJWT(String server, String username, String password) {\n"
				+ "  HTTPClient http;              // HTTPClient-Objekt deklarieren\n"
				+ "  WiFiClient wifiClient;        // Für HTTP-Verbindungen\n"
				+ "  WiFiClientSecure wifiClientSecure; // Für HTTPS-Verbindungen\n"
				+ "\n"
				+ "  // Bestimme, ob die Verbindung HTTP oder HTTPS ist\n"
				+ "  bool isSecure = false;\n"
				+ "  String req;\n"
				+ "  if (server.startsWith(\"https\")) {\n"
				+ "    isSecure = true;\n"
				+ "    req = server + \"/api/auth/login\";\n"
				+ "  } else {\n"
				+ "    if (server.startsWith(\"http\")) {\n"
				+ "      req = server + \"/api/auth/login\"; \n"
				+ "    } else {  \n"
				+ "      req = \"http://\" + server + \"/api/auth/login\";\n"
				+ "    }\n"
				+ "  }\n"
				+ "\n"
				+ "  // JSON-Daten für den POST-Request\n"
				+ "  String PostData = \"{\\\"username\\\":\\\"\" + username + \"\\\", \\\"password\\\":\\\"\" + password + \"\\\"}\";\n"
				+ "  Serial.println(req);\n"
				+ "  Serial.println(PostData);\n"
				+ "\n"
				+ "  // Verbindung aufbauen\n"
				+ "  if (isSecure) {\n"
				+ "    // HTTPS-Verbindung\n"
				+ "    wifiClientSecure.setInsecure(); // Zertifikatsprüfung deaktivieren (anpassen, wenn Zertifikate verwendet werden)\n"
				+ "    http.begin(wifiClientSecure, req); // HTTPS-Verbindung aufbauen\n"
				+ "  } else {\n"
				+ "    // HTTP-Verbindung\n"
				+ "    http.begin(wifiClient, req); // HTTP-Verbindung aufbauen\n"
				+ "  }\n"
				+ "\n"
				+ "  // HTTP-Header hinzufügen\n"
				+ "  http.addHeader(\"Content-Type\", \"application/json\");\n"
				+ "  http.addHeader(\"Accept\", \"application/json\");\n"
				+ "\n"
				+ "  // POST-Anfrage senden\n"
				+ "  int httpCode = http.POST(PostData); // Sende POST-Daten\n"
				+ "  String payload = http.getString();   // Antwort des Servers auslesen\n"
				+ "  Serial.println(\"HTTP-Code: \" + String(httpCode)); // Statuscode anzeigen\n"
				+ "  Serial.println(\"Antwort: \" + payload); // Antwort anzeigen\n"
				+ "\n"
				+ "  http.end(); // Verbindung schließen\n"
				+ "\n"
				+ "  // JWT aus der Antwort parsen (angenommen, parseJSON extrahiert \"token\" aus JSON)\n"
				+ "  payload = parseJSON(payload, \"token\");\n"
				+ "  Serial.println(\"JWT: \" + payload);\n"
				+ "  \n"
				+ "  return payload; // JWT zurückgeben\n"
				+ "}";
		
		translator.addDefinitionCommand(httpPOST);
		
		String host,user,pass,ret;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    user = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    pass = translatorBlock.toCode();
	    
	    ret = "requestJWT("+host+","+user+","+pass+")"; 
        return codePrefix + ret + codeSuffix;
	 	}
}

