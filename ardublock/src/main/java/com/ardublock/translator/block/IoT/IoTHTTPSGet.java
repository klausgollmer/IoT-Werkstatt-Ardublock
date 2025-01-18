package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTHTTPSGet  extends TranslatorBlock {

	public IoTHTTPSGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
		
	
		String httpGET ="//--------------------------------------- https-GET\n"
				+ "int httpsGET(String host, String cmd, String &antwort, const char* fingerprint) {\n"
				+ "  int ok = 0;\n"
				+ "  WiFiClientSecure client;\n"
				+ "  String message = host+cmd;\n"
				+ "  const char* host_cstr = host.c_str();\n"
				+ "\n"
				+ "#if defined(ESP8266)  \n"
				+ "  if (strlen(fingerprint) > 1) {  \n"
				+ "     client.setFingerprint(fingerprint);\n"
				+ "     Serial.println(\"check certificate\");\n"
				+ "\n"
				+ "  } else { \n"
				+ "     client.setInsecure();\n"
				+ "  }\n"
				+ "#else\n"
				+ "  if (strlen(fingerprint) > 1) {\n"
				+ "    //if (client.verify(fingerprint, host_cstr)) {\n"
				+ "    //  Serial.println(\"certificate matches\");\n"
				+ "    //} else {\n"
				+ "    //  Serial.println(\"certificate doesn't match\");\n"
				+ "    //}\n"
				+ "     Serial.println(\"sorry, ESP32 lib not check SHA-1 certificate\");\n"
				+ "  } else {\n"
				+ "     client.setInsecure();\n"
				+ "  }\n"
				+ "#endif\n"
				+ "  HTTPClient https;\n"
				+ "  // Serial.println(message);\n"
				+ "  if (https.begin(client, message)){  // HTTPS\n"
				+ "    // start connection and send HTTP header\n"
				+ "    int httpCode = https.GET();\n"
				+ "    // httpCode will be negative on error\n"
				+ "    if (httpCode > 0) {\n"
				+ "      // HTTP header has been send and Server response header has been handled\n"
				+ "      String payload = https.getString();\n"
				+ "      antwort = payload;\n"
				+ "      // Serial.println(payload);\n"
				+ "      // file found at server\n"
				+ "      if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {\n"
				+ "        ok = 1;\n"
				+ "      }\n"
				+ "    } \n"
				+ "    else {\n"
				+ "      Serial.printf(\"[HTTPS] GET... failed, error: %s\\n\", https.errorToString(httpCode).c_str());\n"
				+ "    }\n"
				+ "    https.end();\n"
				+ "  } \n"
				+ "  else {\n"
				+ "    Serial.printf(\"[HTTPS] Unable to connect\\n\");\n"
				+ "  }\n"
				+ "  return ok;\n"
				+ "}";
		translator.addDefinitionCommand(httpGET);
		
		
		httpGET ="//--------------------------------------- http-GET with wifi-Client\n" + 
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