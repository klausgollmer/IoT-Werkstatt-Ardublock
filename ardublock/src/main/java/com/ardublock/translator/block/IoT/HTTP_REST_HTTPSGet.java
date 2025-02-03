package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class HTTP_REST_HTTPSGet  extends TranslatorBlock {

	public HTTP_REST_HTTPSGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
		
	
		String httpGET ="// ----------------------------------------------------------------------------\n"
				+ "// httpsGET: Sicherer HTTPS-GET-Request mit SHA-1 Fingerprint-Prüfung für ESP8266\n"
				+ "//\n"
				+ "// Parameter:\n"
				+ "//   host:         \"example.com\" oder \"https://example.com\"\n"
				+ "//   cmd:          \"/api/getdata\"\n"
				+ "//   antwort:      Enthält am Ende die Serverantwort\n"
				+ "//   fingerprint:  SHA-1 Fingerprint (NUR für ESP8266, ESP32 wird unsicher verwendet)\n"
				+ "//\n"
				+ "// Rückgabewert:\n"
				+ "//   1 = Erfolg (HTTP 200 oder Redirect 301)\n"
				+ "//   0 = Fehler (Verbindungsfehler oder HTTP-Code != 200/301)\n"
				+ "// ----------------------------------------------------------------------------\n"
				+ "int httpsGET(String host, String cmd, String &antwort, const char* fingerprint) {\n"
				+ "  // Debug- und Fehlermeldungen\n"
				+ "  String errorString      = \"\";\n"
				+ "  String errorStringDebug = \"\";\n"
				+ "  IOTW_PRINT(\"httpsGET \"+host+\" \"); \n"
				+ "  int ret = 0; // 1 = Erfolg, 0 = Fehler\n"
				+ "\n"
				+ "  // 1) WLAN-Verbindung prüfen\n"
				+ "  if (WiFi.status() != WL_CONNECTED) {\n"
				+ "    errorString += \"⚠ no WiFi connection\";\n"
				+ "    IOTW_PRINTLN(errorString);\n"
				+ "    return 0;\n"
				+ "  }\n"
				+ "\n"
				+ "  // 2) URL korrekt zusammenbauen\n"
				+ "  String url;\n"
				+ "  if (host.startsWith(\"https://\")) {\n"
				+ "    url = host + cmd;\n"
				+ "  } else {\n"
				+ "    url = \"https://\" + host + cmd;\n"
				+ "  }\n"
				+ "\n"
				+ "  // Debug-Infos\n"
				+ "  errorStringDebug += \"[INFO] Using HTTPS GET request:\\n\";\n"
				+ "  errorStringDebug += \"URL: \" + url + \"\\n\";\n"
				+ "  errorStringDebug += \"Fingerprint: \" + String(fingerprint) + \"\\n\";\n"
				+ "\n"
				+ "  // 3) Sicheren Client für HTTPS-Request erstellen\n"
				+ "  #if defined(ESP8266)\n"
				+ "    BearSSL::WiFiClientSecure client;\n"
				+ "  #elif defined(ESP32)\n"
				+ "    WiFiClientSecure client;\n"
				+ "  #endif\n"
				+ "\n"
				+ "  // 4) Fingerprint-Prüfung für ESP8266\n"
				+ "  #if defined(ESP8266)\n"
				+ "    if (strlen(fingerprint) > 1) {  // Falls Fingerprint übergeben wurde\n"
				+ "      client.setFingerprint(fingerprint);\n"
				+ "      errorStringDebug += \"Using SHA-1 fingerprint validation.\\n\";\n"
				+ "    } else {\n"
				+ "      client.setInsecure();  // Unsichere Verbindung zulassen\n"
				+ "      errorStringDebug += \"No fingerprint provided - using insecure connection!\\n\";\n"
				+ "    }\n"
				+ "  #elif defined(ESP32)\n"
				+ "    client.setInsecure();  // ESP32 unterstützt KEIN SHA-1 Fingerprint-Check\n"
				+ "    errorStringDebug += \"ESP32 does not support SHA-1 certificate validation.\\n\";\n"
				+ "  #endif\n"
				+ "\n"
				+ "  // 5) HTTPClient vorbereiten\n"
				+ "  HTTPClient https;\n"
				+ "  if (!https.begin(client, url)) {\n"
				+ "    errorString += \"❌ Unable to connect\";\n"
				+ "    IOTW_PRINTLN(errorString);\n"
				+ "    return 0;\n"
				+ "  }\n"
				+ "\n"
				+ "  // 6) GET-Anfrage senden\n"
				+ "  int httpCode = https.GET();\n"
				+ "  if (httpCode > 0) {\n"
				+ "    // Server hat geantwortet\n"
				+ "    String payload = https.getString();\n"
				+ "    antwort = payload;\n"
				+ "\n"
				+ "    if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {\n"
				+ "      errorString += \"✅ success\";\n"
				+ "      ret = 1;\n"
				+ "    } else {\n"
				+ "      errorString += \"❌ Unexpected HTTP-Code: \" + String(httpCode);\n"
				+ "    }\n"
				+ "  } else {\n"
				+ "    errorString += \"❌ HTTPS GET failed: \" + https.errorToString(httpCode);\n"
				+ "  }\n"
				+ "\n"
				+ "  https.end(); // Verbindung beenden\n"
				+ "\n"
				+ "  // 7) Debug- und Fehlermeldungen ausgeben\n"
				+ "  IOTW_PRINTLN(errorString);\n"
				+ "#if (IOTW_DEBUG_LEVEL >1)\n"
				+ "  IOTW_PRINTLN(errorStringDebug);\n"
				+ "#endif\n"
				+ "\n"
				+ "  return ret;\n"
				+ "}\n";
		translator.addDefinitionCommand(httpGET);
		
		

		httpGET = "//--------------------------------------- http-GET with wifi-Client\r\n"
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
				
	    String httpRESTGET = "String http_s_GET(String host, String cmd,const char* fingerprint) { // REST - Interface GET\n" + 
	    		"  String json= \" \";\n" + 
	    		"  String myhost=host;\n" + 
	    		"  myhost.toUpperCase();\n" + 
	    		"  if (cmd.charAt(0) != '/') cmd = \"/\"+cmd;\n" + 
	    		"  if (myhost.indexOf(\"HTTPS\")>=0) {\n" + 
	    		"     httpsGET(host,cmd,json,fingerprint);// und absenden \n" + 
	    		"  } else {\n" + 
	    		"     httpClientGET(host,cmd,json);// und absenden \n" + 
	    		"  }   \n" + 
	    		"  return json;\n" + 
	    		"}\n" + 
	    		"";
	    
	    translator.addDefinitionCommand(httpRESTGET);
		
		String host,cmd,ret,finger = "\" \"";
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    cmd = translatorBlock.toCode();
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(2);
	    if (translatorBlock!=null) {
			  finger = translatorBlock.toCode();
	    }	    	
		
	    
	    
	    ret = "http_s_GET("+host+","+cmd+","+finger+")";

        return codePrefix + ret + codeSuffix;
	 	}
}