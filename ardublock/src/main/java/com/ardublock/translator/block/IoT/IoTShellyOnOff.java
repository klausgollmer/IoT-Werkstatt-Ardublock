package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTShellyOnOff  extends TranslatorBlock {

	public IoTShellyOnOff (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
//		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		//translator.addSetupCommand("Serial.begin(115200);");
		
		String httpGET ="//--------------------------------------- http-GET with wifi-Client\n" + 
				"int httpClientGET(String host, String cmd, String &antwort) {\n" + 
				"  int ok = 0;\n" + 
				"  String message = host+cmd;\n" + 
				"  WiFiClient client;\n" + 
				"  #if defined(ESP8266)\n HTTPClient http;\n#elif defined(ESP32) \n HTTPClient http;\n#endif\n" + 
				"  //Serial.println(message);\n" + 
				"  if (http.begin(client, message)){  // HTTP\n" + 
				"    // start connection and send HTTP header\n" + 
				"    int httpCode = http.GET();\n" + 
				"    // httpCode will be negative on error\n" + 
				"    if (httpCode > 0) {\n" + 
				"      // HTTP header has been send and Server response header has been handled\n" + 
				"      String payload = http.getString();\n" + 
				"      antwort = payload;\n" + 
				"      //Serial.println(payload);\n" + 
				"      // file found at server\n" + 
				"      if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {\n" + 
				"        ok = 1;\n" + 
				"      }\n" + 
				"    } \n" + 
				"    else {\n" + 
				"      Serial.printf(\"[HTTP] GET... failed, error: %s\\n\", http.errorToString(httpCode).c_str());\n" + 
				"    }\n" + 
				"    http.end();\n" + 
				"  } \n" + 
				"  else {\n" + 
				"    Serial.printf(\"[HTTP] Unable to connect\\n\");\n" + 
				"  }\n" + 
				"  return ok;\n" + 
				"}\n" + 
				"";
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
	    						"  Serial.print(\"error - no such item: \"+suchtext);\n" + 
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
	  	

		String host,state;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    state = translatorBlock.toCode();
	    
	    String ret = "ShellySwitch("+host+","+state+");\n";
        return codePrefix + ret + codeSuffix;
	 	}
}

