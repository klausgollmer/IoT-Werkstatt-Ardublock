package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class HTTP_ThingsboardSend  extends TranslatorBlock {

	public HTTP_ThingsboardSend (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
		//translator.addSetupCommand("Serial.begin(115200);");

		/*
		
		String httpPOST ="void sendThingsboard(String server, String token, String n1, String d1, String n2, String d2, String n3, String d3) {\n"
				+ "    HTTPClient http; // Declare object of class HTTPClient\n"
				+ "    WiFiClient wifiClient;\n"
				+ "    WiFiClientSecure wifiClientSecure;\n"
				+ "    String payload;\n"
				+ "\n"
				+ "    // JSON-Daten erstellen\n"
				+ "    String json = \"{\";\n"
				+ "    if (n1 != \"\") json += \"\\\"\"+n1+\"\\\":\\\"\"+d1+\"\\\"\";\n"
				+ "    if (n2 != \"\") json += \",\\\"\"+n2+\"\\\":\\\"\"+d2+\"\\\"\";\n"
				+ "    if (n3 != \"\") json += \",\\\"\"+n3+\"\\\":\\\"\"+d3+\"\\\"\";\n"
				+ "    json += \"}\";\n"
				+ "\n"
				+ "    IOTW_PRINT(F(\"\\nHTTP-POST Server:\"));\n"
				+ "    IOTW_PRINTLN(server);\n"
				+ "\n"
				+ "    String req=\"/api/v1/\"+token+\"/telemetry\";\n"
				+ "    // Bestimme, ob die Verbindung HTTP oder HTTPS ist\n"
				+ "    bool isSecure = false;\n"
				+ "    if (server.startsWith(\"https\")) {\n"
				+ "      isSecure = true;\n"
				+ "      req = server + req;\n"
				+ "     } else {\n"
				+ "      if (server.startsWith(\"http\")) {\n"
				+ "        req = server + req; \n"
				+ "      } else {  \n"
				+ "        req = \"http://\" + server + req;\n"
				+ "      }\n"
				+ "    }\n"
				+ "\n"
				+ "    // HTTP-POST Operation\n"
				+ "    // Verbindung aufbauen\n"
				+ "    if (isSecure) {\n"
				+ "        wifiClientSecure.setInsecure(); // Zertifikatsprüfung deaktivieren\n"
				+ "        http.begin(wifiClientSecure, req); // HTTPS-Verbindung\n"
				+ "    } else {\n"
				+ "        http.begin(wifiClient, req); // HTTP-Verbindung\n"
				+ "    }\n"
				+ "\n"
				+ "    http.addHeader(\"Content-Type\", \"application/json\");\n"
				+ "    // Anfrage senden\n"
				+ "    int httpCode = http.POST(json); // Send the request\n"
				+ "\n"
				+ "    // Antwort verarbeiten\n"
				+ "    if (httpCode > 0) {\n"
				+ "        payload = http.getString(); // Get the response payload\n"
				+ "    } else {\n"
				+ "        payload = \"Error: HTTP-Code:\"+String(httpCode);\n"
				+ "    }\n"
				+ "    \n"
				+ "    http.end(); // Verbindung schließen\n"
				+ "#if (IOTW_DEBUG_LEVEL >1)\n "
				+ "      IOTW_PRINT(F(\"\n HTTP-POST req:\"));\n"
				+ "      IOTW_PRINTLN(req);\n"
				+ "      IOTW_PRINTLN(json);\n"
				+ "      IOTW_PRINTLN(payload);\n"
				+ "#endif\n"
				+ "}\n"
				+ "";
				*/
		
		String httpPOST =  "// ------- http-POST Thingsboard\n"
                          +"void sendThingsboard(String server, String token, String n1, String d1, String n2, String d2, String n3, String d3) {\n"
                          + "  String errorString =      \"ThingsBoard send \"+server+\" ... \";  // Fehlermeldungen hier sammeln\n"
                          + "  String errorStringDebug = \"\";     // Fehlermeldungen hier sammeln\n"
                          + "  String payload = \"\";\n"
                          + "\n"
                          + "  if (WiFi.status() != WL_CONNECTED) {\n"
                          + "    errorString += \"⚠ WiFi\";\n"
                          + "    IOTW_PRINTLN(errorString);\n"
                          + "    return; // Keine Anfrage senden, wenn keine Verbindung besteht\n"
                          + "  }\n"
                          + "\n"
                          + "  WiFiClient wifiClient;\n"
                          + "  HTTPClient http;\n"
                          + "  String req = \"/api/v1/\" + token + \"/telemetry\";\n"
                          + "  bool isSecure = false;\n"
                          + "\n"
                          + "  // Prüfen, ob HTTPS genutzt wird\n"
                          + "  if (server.startsWith(\"https\")) {\n"
                          + "    isSecure = true;\n"
                          + "    req = server + req;\n"
                          + "  } else if (server.startsWith(\"http\")) {\n"
                          + "    req = server + req;\n"
                          + "  } else {\n"
                          + "    req = \"http://\" + server + req;\n"
                          + "  }\n"
                          + "\n"
                          + "  // JSON-Daten mit ArduinoJson erstellen\n"
                          + "  StaticJsonDocument<200> jsonDoc;\n"
                          + "  if (!n1.isEmpty()) jsonDoc[n1] = d1;\n"
                          + "  if (!n2.isEmpty()) jsonDoc[n2] = d2;\n"
                          + "  if (!n3.isEmpty()) jsonDoc[n3] = d3;\n"
                          + "  \n"
                          + "  String json;\n"
                          + "  serializeJson(jsonDoc, json); // JSON in String konvertieren\n"
                          + "\n"
                          + "  errorStringDebug += \"[INFO] HTTP-POST zu ThingsBoard:\\n\";\n"
                          + "  errorStringDebug += \"Server: \" + server + \"\\n\";\n"
                          + "  errorStringDebug += \"Request: \" + req + \"\\n\";\n"
                          + "  errorStringDebug += \"JSON: \" + json + \"\\n\";\n"
                          + "\n"
                          + "  // HTTP-Client vorbereiten\n"
                          + "  http.setTimeout(5000); // Timeout auf 5 Sekunden setzen\n"
                          + "\n"
                          + "  if (isSecure) {\n"
                          + "    WiFiClientSecure wifiClientSecure;\n"
                          + "    wifiClientSecure.setInsecure(); // Zertifikatsprüfung deaktivieren\n"
                          + "    http.begin(wifiClientSecure, req);\n"
                          + "  } else {\n"
                          + "    http.begin(wifiClient, req);\n"
                          + "  }\n"
                          + "\n"
                          + "  http.addHeader(\"Content-Type\", \"application/json\");\n"
                          + "\n"
                          + "  // POST-Anfrage senden\n"
                          + "  int httpCode = http.POST(json);\n"
                          + "\n"
                          + "  // Fehlerbehandlung\n"
                          + "  if (httpCode > 0) {\n"
                          + "    payload = http.getString();\n"
                          + "    //errorString += \" HTTP-Code: \" + String(httpCode) + \"\\n\";\n"
                          + "\n"
                          + "    switch (httpCode) {\n"
                          + "      case 200:\n"
                          + "      case 201:\n"
                          + "        errorString += \"✅\";\n"
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
                          + "        errorString += \"❌ 404 Server not Found\";\n"
                          + "        break;\n"
                          + "      case 500:\n"
                          + "        errorString += \"❌ 500 Internal Server Error\";\n"
                          + "        break;\n"
                          + "      default:\n"
                          + "        errorString += \"❌ HTTP-Code: \" + String(httpCode);\n"
                          + "    }\n"
                          + "  } else {\n"
                          + "    //errorString += \" code: \" + String(httpCode);\n"
                          + "    \n"
                          + "    // ESP-spezifische Fehlercodes\n"
                          + "    if (httpCode == -1) {\n"
                          + "      errorString += \"❌ no connection \";\n"
                          + "    } else if (httpCode == -4) {\n"
                          + "      errorString += \"❌ timeout\";\n"
                          + "    } else if (httpCode == -5) {\n"
                          + "      errorString += \"❌ no connection to server\";\n"
                          + "    }\n"
                          + "  }\n"
                          + "\n"
                          + "  http.end(); // Verbindung schließen \n"
                          + "  if ((httpCode != 200 && httpCode != 201) && !payload.isEmpty()) {\n"
                          + "    errorString += \"\\nAnswer:\" + payload;\n"
                          + "  }\n"
                          + "  \n"
                          + "  // Alle Debug-Ausgaben erst am Ende\n"
                          + "  IOTW_PRINTLN(errorString);\n"
                          + "  #if (IOTW_DEBUG_LEVEL >1)\n"
                          + "     IOTW_PRINTLN(errorStringDebug);\n"
                          + "  #endif\n"
                          + "}\n";
		translator.addDefinitionCommand(httpPOST);
		
		String host,access,n1,d1,n2="\"\"",d2="\"NAN\"",n3="\"\"",d3="\"NAN\"",ret;
		
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    access = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    n1 = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    d1 = translatorBlock.toCode();

	    translatorBlock = this.getTranslatorBlockAtSocket(4);
	    if (translatorBlock!=null) n2 = translatorBlock.toCode();
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(5);
	    if (translatorBlock!=null) d2 = translatorBlock.toCode();
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(6);
	    if (translatorBlock!=null) n3 = translatorBlock.toCode();
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(7);
	    if (translatorBlock!=null) d3 = translatorBlock.toCode();

	    ret = "sendThingsboard("+host+","+access+","+n1+","+d1+","+n2+","+d2+","+n3+","+d3+");// http REST, Post to thingsboard\n"; 
        return codePrefix + ret + codeSuffix;
	 	}
}

