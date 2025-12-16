package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class HTTP_ThingspeakSend  extends TranslatorBlock {

	public HTTP_ThingspeakSend (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");

		String Client, Check;
		if (translator.isGPRSProgram()) {
			Client= " TinyGsmClient client(modem);// Client über GSM\n";
			Check=  " int ok = checkMobilfunk();\n";
		} else {
			Client= " WiFiClient client; // Client über WiFi\n";
			Check=  " int ok = 1;\n";
		}
	/*	
		String httpGET ="//--------------------------------------- HTTP-Get\n"
				+ "int httpGET(String host, String cmd, String &antwort,int Port) {\n"
				+ Client
				+ " String text = \"GET http://\"+ host + cmd + \" HTTP/1.1\\r\\n\";\n"
		        + " text = text + \"Host:\" + host + \"\\r\\n\";\n"
		        + " text = text + \"Connection:close\\r\\n\\r\\n\";\n"
		        + Check
		        + " if (ok) { // Netzwerkzugang vorhanden \n"
		        + "  ok = client.connect(host.c_str(),Port);// verbinde mit Client\n"
		        + "  if (ok) {\n"
		        + "   client.print(text);                 // sende an Client \n" 
		        + "   for (int tout=1000;tout>0 && client.available()==0; tout--)\n"
		        + "    delay(10);                         // und warte auf Antwort\n"
		        + "   if (client.available() > 0)         // Anwort gesehen \n"
		        + "    while (client.available())         // und ausgewertet\n"
		        +"      antwort = antwort + client.readStringUntil('\\r');\n"
		        +"    else ok = 0;\n"
		        +"    client.stop(); \n"
		        +"    IOTW_PRINTLN(antwort);\n"
		        + "  } \n"
		        + " } \n"
		        + " if (!ok) IOTW_PRINT(\" no connection\"); // Fehlermeldung\n"
		        + " return ok;\n"
		        + "}\n";
*/
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
		

		
		String apikey,host,ret,wert;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();
	
	  

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    apikey = translatorBlock.toCode();
	    
//	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
//	    feldindex = translatorBlock.toCode();
	    wert = "";
	    translatorBlock = this.getTranslatorBlockAtSocket(2);
	    if (translatorBlock!=null)
	       wert +="\"&field1=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(3);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&field2=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(4);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&field3=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(5);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&field4=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(6);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&field5=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(7);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&field6=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(8);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&field7=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(9);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&field8=\"+String("+ translatorBlock.toCode()+")";
	    
	   
	    
	    ret = "\n{ //Block------------------------------ sende Daten an Thingspeak (mit http GET) \n"
       		 +" //IOTW_PRINTLN(\"\\nThingspeak update \");\n"
        	 +" String cmd = \"/update?api_key=\"+ String(" + apikey +");\n" 
             +" String host = "+ host +";\n"
             +" String antwort= \" \";\n"
 	         +" cmd = cmd +" + "String(" + wert + ");\n"
 	         +" httpClientGET(host,cmd,antwort);// und absenden \n"   
 	         +" if (antwort.toInt() == 0) IOTW_PRINT(F(\"❌ rejected, update cycle-time > 15s on free accounts\"));"
             +"} // Blockende\n";
           

        return codePrefix + ret + codeSuffix;
	 	}
}

