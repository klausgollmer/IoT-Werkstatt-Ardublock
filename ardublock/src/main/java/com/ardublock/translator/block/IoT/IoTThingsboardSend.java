package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTThingsboardSend  extends TranslatorBlock {

	public IoTThingsboardSend (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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

		
		
		String httpPOST ="void sendThingsboard(String server, String token, String n1, String d1, String n2, String d2, String n3, String d3) {\n"
				+ "    HTTPClient http; // Declare object of class HTTPClient\n"
				+ "    WiFiClient wifiClient;\n"
				+ "    WiFiClientSecure wifiClientSecure;\n"
				+ "\n"
				+ "    // JSON-Daten erstellen\n"
				+ "    String json = \"{\";\n"
				+ "    if (n1 != \"\") json += \"\\\"\"+n1+\"\\\":\\\"\"+d1+\"\\\"\";\n"
				+ "    if (n2 != \"\") json += \",\\\"\"+n2+\"\\\":\\\"\"+d2+\"\\\"\";\n"
				+ "    if (n3 != \"\") json += \",\\\"\"+n3+\"\\\":\\\"\"+d3+\"\\\"\";\n"
				+ "    json += \"}\";\n"
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
				+ "    Serial.println(req);\n"
				+ "\n"
				+ "    // Verbindung aufbauen\n"
				+ "    if (isSecure) {\n"
				+ "        wifiClientSecure.setInsecure(); // Zertifikatsprüfung deaktivieren\n"
				+ "        http.begin(wifiClientSecure, req); // HTTPS-Verbindung\n"
				+ "    } else {\n"
				+ "        http.begin(wifiClient, req); // HTTP-Verbindung\n"
				+ "    }\n"
				+ "\n"
				+ "    http.addHeader(\"Content-Type\", \"application/json\");\n"
				+ "    Serial.println(json);\n"
				+ "    \n"
				+ "    // Anfrage senden\n"
				+ "    int httpCode = http.POST(json); // Send the request\n"
				+ "\n"
				+ "    // Antwort verarbeiten\n"
				+ "    if (httpCode > 0) {\n"
				+ "        String payload = http.getString(); // Get the response payload\n"
				+ "        Serial.println(\"Antwort: \" + payload); // Print request response payload\n"
				+ "    } else {\n"
				+ "        Serial.printf(\"Fehler bei der Anfrage, HTTP-Code: %d\\n\", httpCode);\n"
				+ "    }\n"
				+ "    \n"
				+ "    http.end(); // Verbindung schließen\n"
				+ "}\n"
				+ "";
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

