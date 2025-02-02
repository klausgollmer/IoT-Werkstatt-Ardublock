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
		translator.addHeaderFile("ArduinoJson.h");
	
  	    String httpGET ="// request actual timeseries data from thingsboard\n"
  	    		+ "String askThingsboard(String server, String device, String JWT, String key) {\n"
  	    		+ "  // 1) Strings, in denen wir Ausgaben sammeln\n"
  	    		+ "  String errorString      = \"ThingsBoard askTimeseries \";\n"
  	    		+ "  String errorStringDebug = \"\";\n"
  	    		+ "  String payloadValue     = \"NAN\";\n"
  	    		+ "\n"
  	    		+ "  // 2) WLAN-Check\n"
  	    		+ "  if (WiFi.status() != WL_CONNECTED) {\n"
  	    		+ "    errorString += \"⚠ WiFi not connected!\";\n"
  	    		+ "    IOTW_PRINTLN(errorString);\n"
  	    		+ "    return \"NAN\";\n"
  	    		+ "  }\n"
  	    		+ "\n"
  	    		+ "  // 3) URL zusammensetzen (HTTP/HTTPS)\n"
  	    		+ "  bool isSecure = false;\n"
  	    		+ "  String req    = \"/api/plugins/telemetry/DEVICE/\" + device + \"/values/timeseries?keys=\" + key;\n"
  	    		+ "\n"
  	    		+ "  if (server.startsWith(\"https\")) {\n"
  	    		+ "    isSecure = true;\n"
  	    		+ "    req = server + req;\n"
  	    		+ "  } \n"
  	    		+ "  else if (server.startsWith(\"http\")) {\n"
  	    		+ "    req = server + req;\n"
  	    		+ "  } \n"
  	    		+ "  else {\n"
  	    		+ "    req = \"http://\" + server + req;\n"
  	    		+ "  }\n"
  	    		+ "\n"
  	    		+ "  // 4) Debug-Ausgaben aufbereiten (aber noch nicht ausgeben)\n"
  	    		+ "  errorStringDebug += \"[INFO] HTTP-GET askThingsboard:\\n\";\n"
  	    		+ "  errorStringDebug += \"Server: \" + server + \"\\n\";\n"
  	    		+ "  errorStringDebug += \"Request: \" + req + \"\\n\";\n"
  	    		+ "\n"
  	    		+ "  // 5) HTTP-Client vorbereiten\n"
  	    		+ "  HTTPClient http;\n"
  	    		+ "  WiFiClient wifiClient;\n"
  	    		+ "  http.setTimeout(5000); // Timeout auf 5 Sekunden\n"
  	    		+ "\n"
  	    		+ "  if (isSecure) {\n"
  	    		+ "    WiFiClientSecure wifiClientSecure;\n"
  	    		+ "    wifiClientSecure.setInsecure(); // Zertifikatsprüfung deaktivieren\n"
  	    		+ "    http.begin(wifiClientSecure, req);\n"
  	    		+ "  } \n"
  	    		+ "  else {\n"
  	    		+ "    http.begin(wifiClient, req);\n"
  	    		+ "  }\n"
  	    		+ "\n"
  	    		+ "  // 6) HTTP-Header hinzufügen\n"
  	    		+ "  http.addHeader(\"Authorization\", \"Bearer \" + JWT);\n"
  	    		+ "  http.addHeader(\"Accept\", \"application/json\");\n"
  	    		+ "\n"
  	    		+ "  // 7) GET-Anfrage senden\n"
  	    		+ "  int httpCode  = http.GET();\n"
  	    		+ "  String payload = \"\";\n"
  	    		+ "\n"
  	    		+ "  if (httpCode > 0) {\n"
  	    		+ "    payload = http.getString();\n"
  	    		+ "    // HTTP-Codes durchgehen (GET -> i.d.R. 200 bei Erfolg)\n"
  	    		+ "    switch (httpCode) {\n"
  	    		+ "    case 200:\n"
  	    		+ "      errorString += \"✅ success\";\n"
  	    		+ "      break;\n"
  	    		+ "    case 201:\n"
  	    		+ "      errorString += \"✅ 201 Created\";\n"
  	    		+ "      break;\n"
  	    		+ "    case 400:\n"
  	    		+ "      errorString += \"❌ 400 Bad Request\";\n"
  	    		+ "      break;\n"
  	    		+ "    case 401:\n"
  	    		+ "      errorString += \"❌ 401 Unauthorized\";\n"
  	    		+ "      break;\n"
  	    		+ "    case 403:\n"
  	    		+ "      errorString += \"❌ 403 Forbidden\";\n"
  	    		+ "      break;\n"
  	    		+ "    case 404:\n"
  	    		+ "      errorString += \"❌ 404 Not Found\";\n"
  	    		+ "      break;\n"
  	    		+ "    case 500:\n"
  	    		+ "      errorString += \"❌ 500 Internal Server Error\";\n"
  	    		+ "      break;\n"
  	    		+ "    default:\n"
  	    		+ "      errorString += \"❌ HTTP-Code: \" + String(httpCode);\n"
  	    		+ "    }\n"
  	    		+ "  } \n"
  	    		+ "  else {\n"
  	    		+ "    // Negative Codes sind ESP-spezifische Verbindungsfehler\n"
  	    		+ "    errorString += \"❌ connection failed code: \" + String(httpCode);\n"
  	    		+ "    if (httpCode == -1) {\n"
  	    		+ "      errorString += \" (no connection/host not reachable)\";\n"
  	    		+ "    } \n"
  	    		+ "    else if (httpCode == -4) {\n"
  	    		+ "      errorString += \" (request timeout)\";\n"
  	    		+ "    } \n"
  	    		+ "    else if (httpCode == -5) {\n"
  	    		+ "      errorString += \" (no connection to server)\";\n"
  	    		+ "    }\n"
  	    		+ "  }\n"
  	    		+ "\n"
  	    		+ "  http.end(); // Verbindung beenden\n"
  	    		+ "\n"
  	    		+ "  // 8) Nur wenn httpCode == 200/201 => JSON parse\n"
  	    		+ "  if (httpCode == 200 || httpCode == 201) {\n"
  	    		+ "    // \"value\" aus dem JSON holen. Evtl. per ArduinoJson:\n"
  	    		+ "    // Wir erwarten ein JSON-Objekt, das so aussieht:\n"
  	    		+ "    // { \"key\": [ { \"ts\": 12345, \"value\": \"xxx\" } ] }\n"
  	    		+ "    // oder: { \"humidity\": [ { \"ts\":..., \"value\":... } ] }\n"
  	    		+ "\n"
  	    		+ "    StaticJsonDocument<512> doc;\n"
  	    		+ "    DeserializationError err = deserializeJson(doc, payload);\n"
  	    		+ "    if (err) {\n"
  	    		+ "      // Falls ein Parsing-Fehler auftritt\n"
  	    		+ "      errorString += \"\\nJSON parse error: \";\n"
  	    		+ "      errorString += err.f_str();\n"
  	    		+ "    } \n"
  	    		+ "    else {\n"
  	    		+ "      // Prüfen, ob doc[key] existiert und min. 1 Element hat\n"
  	    		+ "      // doc[key] ist ein Array\n"
  	    		+ "      if (!doc[key].isNull() && doc[key].size() > 0) {\n"
  	    		+ "        // \"value\" aus dem ersten Array-Element\n"
  	    		+ "        payloadValue = doc[key][0][\"value\"].as<String>();\n"
  	    		+ "      } \n"
  	    		+ "      else {\n"
  	    		+ "        // Falls key nicht existiert oder Array leer\n"
  	    		+ "        payloadValue = \"NAN\";\n"
  	    		+ "        errorString += \"\\nNo timeseries found for key: \" + key;\n"
  	    		+ "      }\n"
  	    		+ "    }\n"
  	    		+ "  } \n"
  	    		+ "  else {\n"
  	    		+ "    // Bei Fehlern (oder allen Codes != 200/201) -> volle Antwort anzeigen\n"
  	    		+ "    payloadValue = \"NAN\";\n"
  	    		+ "    if (!payload.isEmpty()) errorString += \"\\nAnswer: \" + payload;\n"
  	    		+ "  }\n"
  	    		+ "\n"
  	    		+ "\n"
  	    		+ "  // 9) Debug-Ausgaben – erst jetzt\n"
  	    		+ "  IOTW_PRINTLN(errorString);\n"
  	    		+ "#if (IOTW_DEBUG_LEVEL >1)\n"
  	    		+ "  IOTW_PRINTLN(errorStringDebug);\n"
  	    		+ "#endif\n"
  	    		+ "\n"
  	    		+ "  // 10) Ergebnis zurückgeben (z.B. den extrahierten Timeseries-Wert)\n"
  	    		+ "  return payloadValue;\n"
  	    		+ "}\n";
  	    
  	    
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

