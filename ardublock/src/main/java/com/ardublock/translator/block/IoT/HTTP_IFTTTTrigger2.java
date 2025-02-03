package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class HTTP_IFTTTTrigger2  extends TranslatorBlock {

	public HTTP_IFTTTTrigger2 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");

		//translator.addSetupCommand("Serial.begin(115200);");
		
		String Client, Check;
		if (translator.isGPRSProgram()) {
			Client= " TinyGsmClient client(modem);// Client über GSM\n";
			Check=  " int ok = checkMobilfunk();\n";
		} else {
			Client= " WiFiClient client; // Client über WiFi\n";
			Check=  " int ok = 1;\n";
		}
		
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
		
		String apikey,ret,trigger;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    trigger = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    apikey = translatorBlock.toCode();
    
	    String wert = "";
	    translatorBlock = this.getTranslatorBlockAtSocket(2);
	    if (translatorBlock!=null)
	       wert +="\"?value1=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(3);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&value2=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(4);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&value3=\"+String("+ translatorBlock.toCode()+")";
	    
	    
	    
	    
	    
	    
        ret ="{ //Block------------------------------ HTTP-Get IfThisThenThat\n"
        	 + " String host = \"maker.ifttt.com\";\n"
             + " String cmd = \"/trigger/\" + String(" + trigger + ") + \"/with/key/\" + String("+apikey+")+String("+wert+");"
             + " String antwort = \" \";\n"
             +"  IOTW_PRINT(\"\\n Trigger IFTTT \");\n"
             +"  IOTW_PRINTLN("+trigger+");\n"
             + " httpClientGET(host,cmd,antwort); \n"
             +"  IOTW_PRINTLN(antwort);\n"
             +"} // Blockende\n";

        return codePrefix + ret + codeSuffix;
	 	}
}

