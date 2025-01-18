package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTShelly2LED  extends TranslatorBlock {

	public IoTShelly2LED (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
				
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

		
						
		String Shelly = "void ShellyLED(String host,int r,int g,int b,int w,int bright) { \n" + 
				"  String shelly_r=\"0\",shelly_g=\"0\",shelly_b=\"0\";\n" + 
				"  if (r>0) shelly_r = \"100\";\n" + 
				"  if (g>0) shelly_g = \"100\";\n" + 
				"  if (b>0) shelly_b = \"100\";\n" + 
				"  if (w>0) {shelly_r =100;shelly_b=\"100\";shelly_g=\"100\";}\n" + 
				"  String shelly_vect = shelly_r + \",\" + shelly_g + \",\" + shelly_b;\n" + 
				"  String shelly_bright = String(bright);\n" + 
				"  \n" + 
				"  String antwort;\n" + 
				"  String cmd = \"/rpc/PLUGS_UI.SetConfig?config={%22leds%22:{%22mode%22:%22switch%22,%22colors%22:{%22switch:0%22:{%22on%22:{%22rgb%22:[\"+shelly_vect+\"],%22brightness%22:\"+shelly_bright+\"},%22off%22:{%22rgb%22:[\"+shelly_vect+\"],%22brightness%22:\"+shelly_bright+\"}}}}\";\n" + 
				"  \n" + 
				"  host=\"http://\"+host;\n" + 
				"  int tout = 2; // Retry\n" + 
				"  int ok = 0;\n" + 
				"  while ((tout > 0) && (~ok)) {\n" + 
				"    tout--; \n" + 
				"    if (httpClientGET(host,cmd,antwort)) { // success\n" + 
				"      ok = (antwort == \"{\\\"restart_required\\\":false}\");\n" + 
				"    }\n" + 
				"  }\n" + 
				"  //Serial.println(ok);\n" + 
				"  //delay(10000);\n" + 
				"  return;\n" + 
				"}";
					
	  	    translator.addDefinitionCommand(Shelly);

    	
		String host,bright,gen;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    bright = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    gen = translatorBlock.toCode();
	    
	    String ret = "ShellyLED("+host+","+gen+","+bright+");";
        return codePrefix + ret + codeSuffix;
	 	}
}

