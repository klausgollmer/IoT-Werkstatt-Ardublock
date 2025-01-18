package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTEcoflowGet  extends TranslatorBlock {

	public IoTEcoflowGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		translator.addHeaderFile("WiFiClientSecureBearSSL.h");
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		//translator.addSetupCommand("Serial.begin(115200);");
		
	
		 String httpRESTGET = "//--------------------------------------- https-GET Ecoflow\n" + 
	    		"int httpsGET_Eco(String host, String cmd, String &antwort, const uint8_t fingerprint[],String akey,String skey) {\n" + 
	    		"  int ok = 0;\n" + 
	    		"  std::unique_ptr<BearSSL::WiFiClientSecure>client(new BearSSL::WiFiClientSecure);\n" + 
	    		"  String message = host+cmd;\n" + 
	    		"  if (sizeof(fingerprint) > 4) { // Validiere SHA-1 Fingerprint\n" + 
	    		"    client->setFingerprint(fingerprint);\n" + 
	    		"  } \n" + 
	    		"  else { // keine Validierung, Achtung Sicherheitslücke\n" + 
	    		"    client->setInsecure();\n" + 
	    		"  }\n" + 
	    		"  HTTPClient https;\n" + 
	    		"  // Serial.println(message);\n" + 
	    		"  if (https.begin(*client, message)){  // HTTPS\n" + 
	    		"    // start connection and send HTTP header\n" + 
	    		"   https.addHeader(\"Content-Type\",\"application/json\");    \n" + 
	    		"   https.addHeader(\"appKey\",akey);    \n" + 
	    		"   https.addHeader(\"secretKey\",skey);\n" + 
	    		"\n" + 
	    		"    int httpCode = https.GET();\n" + 
	    		"    // httpCode will be negative on error\n" + 
	    		"    if (httpCode > 0) {\n" + 
	    		"      // HTTP header has been send and Server response header has been handled\n" + 
	    		"      String payload = https.getString();\n" + 
	    		"      antwort = payload;\n" + 
	    		"      // Serial.println(payload);\n" + 
	    		"      // file found at server\n" + 
	    		"      if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {\n" + 
	    		"        ok = 1;\n" + 
	    		"      }\n" + 
	    		"    } \n" + 
	    		"    else {\n" + 
	    		"      Serial.printf(\"[HTTPS] GET... failed, error: %s\\n\", https.errorToString(httpCode).c_str());\n" + 
	    		"    }\n" + 
	    		"    https.end();\n" + 
	    		"  } \n" + 
	    		"  else {\n" + 
	    		"    Serial.printf(\"[HTTPS] Unable to connect\\n\");\n" + 
	    		"  }\n" + 
	    		"  return ok;\n" + 
	    		"}\n" + 
	    		"String EcoflowGET(String host, String cmd,String sn,String akey,String skey) { // REST - Interface GET\n" + 
	    		"  const uint8_t fingerprint[1] ={\n" + 
	    		"    0x00    }; // SA-1 fingerprint, option\n" + 
	    		"  String json= \" \";\n" + 
	    		"  String myhost=host;\n" + 
	    		"  myhost.toUpperCase();\n" + 
	    		"  if (cmd.charAt(0) != '/') cmd = \"/\"+cmd;\n" + 
	    		"  httpsGET_Eco(host,cmd+sn,json,fingerprint,akey,skey);// und absenden \n" + 
	    		"  return json;\n" + 
	    		"}\n" + 
	    		"";
	    translator.addDefinitionCommand(httpRESTGET);
		
		String host,cmd,ret,sn,ak,sk;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    cmd = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    sn = translatorBlock.toCode();
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    ak = translatorBlock.toCode();
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(4);
	    sk = translatorBlock.toCode();
	    
	    
	    ret = "EcoflowGET("+host+","+cmd+","+sn+","+ak+","+sk+")";

        return codePrefix + ret + codeSuffix;
	 	}
}

