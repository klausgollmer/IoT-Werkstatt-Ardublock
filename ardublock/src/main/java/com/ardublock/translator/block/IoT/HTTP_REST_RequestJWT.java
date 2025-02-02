package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class HTTP_REST_RequestJWT  extends TranslatorBlock {

	public HTTP_REST_RequestJWT (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		translator.addHeaderFile("WiFiClientSecure.h");
		translator.addHeaderFile("ArduinoJson.h");
		
		String httpPOST = "// Request JSON Web token\n"
				+"String requestJWT(String server, String username, String password) {\n"
				+ "  // In diesen Strings sammeln wir Ausgaben und Debug-Infos:\n"
				+ "  String errorString      = \"ThingsBoard request JWT \";\n"
				+ "  String errorStringDebug = \"\";\n"
				+ "  String tokenValue       = \"\";  // Hier speichern wir später den Token\n"
				+ "\n"
				+ "  // 1) WLAN prüfen\n"
				+ "  if (WiFi.status() != WL_CONNECTED) {\n"
				+ "    errorString += \"⚠ WiFi not connected!\";\n"
				+ "    IOTW_PRINTLN(errorString);\n"
				+ "    return \"\";  // Abbrechen, wenn keine Verbindung besteht\n"
				+ "  }\n"
				+ "\n"
				+ "  // 2) URL zusammenbauen & HTTPS/HTTP unterscheiden\n"
				+ "  bool isSecure = false;\n"
				+ "  String req;\n"
				+ "  if (server.startsWith(\"https\")) {\n"
				+ "    isSecure = true;\n"
				+ "    req     = server + \"/api/auth/login\";\n"
				+ "  } else if (server.startsWith(\"http\")) {\n"
				+ "    req = server + \"/api/auth/login\";\n"
				+ "  } else {\n"
				+ "    req = \"http://\" + server + \"/api/auth/login\";\n"
				+ "  }\n"
				+ "\n"
				+ "  // 3) JSON-Daten für POST anlegen\n"
				+ "  StaticJsonDocument<200> jsonDoc;\n"
				+ "  jsonDoc[\"username\"] = username;\n"
				+ "  jsonDoc[\"password\"] = password;\n"
				+ "\n"
				+ "  String postData;\n"
				+ "  serializeJson(jsonDoc, postData);\n"
				+ "\n"
				+ "  // 4) Debug-Infos (nur bei Bedarf ausgeben)  errorStringDebug += \"[INFO] HTTP-POST requestJWT:\\n\";\n"
				+ "  errorStringDebug += \"Server: \" + server + \"\\n\";\n"
				+ "  errorStringDebug += \"Request: \" + req + \"\\n\";\n"
				+ "  errorStringDebug += \"JSON: \" + postData + \"\\n\";\n"
				+ "\n"
				+ "  // 5) HTTP-Client vorbereiten\n"
				+ "  HTTPClient http;\n"
				+ "  WiFiClient wifiClient;  // Für unverschlüsselte HTTP-Verbindungen\n"
				+ "  http.setTimeout(5000);  // Timeout auf 5 Sekunden\n"
				+ "\n"
				+ "  if (isSecure) {\n"
				+ "    // HTTPS-Verbindung\n"
				+ "    WiFiClientSecure wifiClientSecure;\n"
				+ "    wifiClientSecure.setInsecure();  // Zertifikatsprüfung deaktivieren\n"
				+ "    http.begin(wifiClientSecure, req);\n"
				+ "  } else {\n"
				+ "    // HTTP-Verbindung\n"
				+ "    http.begin(wifiClient, req);\n"
				+ "  }\n"
				+ "\n"
				+ "  // 6) Header für JSON-POST festlegen\n"
				+ "  http.addHeader(\"Content-Type\", \"application/json\");\n"
				+ "  http.addHeader(\"Accept\", \"application/json\");\n"
				+ "\n"
				+ "  // 7) POST-Anfrage stellen und Antwort auslesen\n"
				+ "  int httpCode   = http.POST(postData);\n"
				+ "  String payload = \"\";  // Antwort des Servers (JSON oder Fehlermeldung)\n"
				+ "  if (httpCode > 0) {\n"
				+ "    payload = http.getString();\n"
				+ "\n"
				+ "    // Erfolgs- und Fehlercodes unterscheiden\n"
				+ "    switch (httpCode) {\n"
				+ "      case 200:\n"
				+ "        errorString += \"✅ 200 OK\";\n"
				+ "        break;\n"
				+ "      case 201:\n"
				+ "        errorString += \"✅ 201 Created\";\n"
				+ "        break;\n"
				+ "      case 400:\n"
				+ "        errorString += \"❌ 400 Bad Request\";\n"
				+ "        break;\n"
				+ "      case 401:\n"
				+ "        errorString += \"❌ 401 Unauthorized\";\n"
				+ "        break;\n"
				+ "      case 403:\n"
				+ "        errorString += \"❌ 403 Forbidden\";\n"
				+ "        break;\n"
				+ "      case 404:\n"
				+ "        errorString += \"❌ 404 Not Found\";\n"
				+ "        break;\n"
				+ "      case 500:\n"
				+ "        errorString += \"❌ 500 Internal Server Error\";\n"
				+ "        break;\n"
				+ "      default:\n"
				+ "        errorString += \"❌ HTTP-Code: \" + String(httpCode);\n"
				+ "        break;\n"
				+ "    }\n"
				+ "  } else {\n"
				+ "    // HTTP-Code < 0 => Verbindungsfehler auf ESP\n"
				+ "    errorString += \"❌ connection failed code: \" + String(httpCode);\n"
				+ "    if (httpCode == -1) {\n"
				+ "      errorString += \" (host not reachable)\";\n"
				+ "    } else if (httpCode == -4) {\n"
				+ "      errorString += \" (request timeout)\";\n"
				+ "    } else if (httpCode == -5) {\n"
				+ "      errorString += \" (no connection to server)\";\n"
				+ "    }\n"
				+ "  }\n"
				+ "\n"
				+ "  http.end(); // Verbindung schließen\n"
				+ "\n"
				+ "  // 8) Nur bei Erfolg (httpCode 200/201) Token parsen\n"
				+ "  if (httpCode == 200 || httpCode == 201) {\n"
				+ "    // Direkt mit ArduinoJson parsen, z.B.:\n"
				+ "    StaticJsonDocument<512> doc;\n"
				+ "    DeserializationError err = deserializeJson(doc, payload);\n"
				+ "    if (!err) {\n"
				+ "      tokenValue = doc[\"token\"] | \"\";\n"
				+ "    }\n"
				+ "  } else {\n"
				+ "    // Bei Fehlern: vollständige Antwort anzeigen\n"
				+ "    if (!payload.isEmpty()) {\n"
				+ "       errorString += \"\\nAnswer: \" + payload;\n"
				+ "    }\n"
				+ "  }\n"
				+ "\n"
				+ "  // 9) Abschließende Ausgabe\n"
				+ "  IOTW_PRINTLN(errorString);\n"
				+ "#if (IOTW_DEBUG_LEVEL >1)\n"
				+ "  IOTW_PRINTLN(errorStringDebug);\n"
				+ "#endif\n"
				+ "\n"
				+ "  // 10) JWT zurückgeben\n"
				+ "  return tokenValue;\n"
				+ "}\n"
				+ "";
				
				
				
				
				
				
/*				
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
				+ "  IOTW_PRINTLN(req);\n"
				+ "  IOTW_PRINTLN(PostData);\n"
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
				+ "  IOTW_PRINTLN(\"HTTP-Code: \" + String(httpCode)); // Statuscode anzeigen\n"
				+ "  IOTW_PRINTLN(\"Antwort: \" + payload); // Antwort anzeigen\n"
				+ "\n"
				+ "  http.end(); // Verbindung schließen\n"
				+ "\n"
				+ "  // JWT aus der Antwort parsen (angenommen, parseJSON extrahiert \"token\" aus JSON)\n"
				+ "  payload = parseJSON(payload, \"token\");\n"
				+ "  IOTW_PRINTLN(\"JWT: \" + payload);\n"
				+ "  \n"
				+ "  return payload; // JWT zurückgeben\n"
				+ "}";
	*/	
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

