package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTHerukoSend  extends TranslatorBlock {

	public IoTHerukoSend (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		translator.addHeaderFile("WiFiClientSecureBearSSL.h");
		//translator.addSetupCommand("Serial.begin(115200);");
		
		String httpGET ="//--------------------------------------- https-GET\n" + 
				 "int httpsGET(String host, String cmd, String &antwort, const uint8_t fingerprint[] ) {\n" + 
				 "  int ok = 0;\n" + 
				 "  std::unique_ptr<BearSSL::WiFiClientSecure>client(new BearSSL::WiFiClientSecure);\n" + 
				 "  String message = host+cmd;\n" + 
				 "  if (sizeof(fingerprint) > 4) { // Validiere SHA-1 Fingerprint\n" + 
				 "     client->setFingerprint(fingerprint);\n" + 
				 "  } else { // keine Validierung, Achtung Sicherheitslücke\n" + 
				 "     client->setInsecure();\n" + 
				 "  }\n" + 
				 "  HTTPClient https;\n" + 
				 "  // Serial.println(message);\n" + 
				 "  if (https.begin(*client, message)){  // HTTPS\n" + 
				 "    // start connection and send HTTP header\n" + 
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
				 "}";
		translator.addDefinitionCommand(httpGET);

		
		String apikey,host,ret,wert;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    String finger = translatorBlock.toCode();
	    finger = finger.substring(1,finger.length()-1);
        finger = finger.replaceAll(":",",0x");
        finger = "{0x"+finger+"}";        

        
        
        
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    apikey = translatorBlock.toCode();

	    
	    
	    wert = "";
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(3);
	    if (translatorBlock!=null)
	       wert +="\"&volume=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(4);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&alarm=\"+String("+ translatorBlock.toCode()+")";
//	       wert +="\n   +\"&alarm=\"+String(\"false\")";
	    translatorBlock = this.getTranslatorBlockAtSocket(5);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&SPO2=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(6);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&CO2=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(7);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&Humidity=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(8);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&heartbeat=\"+String("+ translatorBlock.toCode()+")";
//	    translatorBlock = this.getTranslatorBlockAtSocket(9);
//	    if (translatorBlock!=null)
//  	       wert +="\n   +\"&field7=\"+String("+ translatorBlock.toCode()+")";
//	    translatorBlock = this.getTranslatorBlockAtSocket(10);
//	    if (translatorBlock!=null)
//  	       wert +="\n   +\"&field8=\"+String("+ translatorBlock.toCode()+")";
	    
	   
	    
	    ret = "\n{ //Block------------------------------ sende Daten an Heruko (mit http GET) \n"
       		 +" Serial.println(\"\\nHeruko update \");\n"
	    	 +" const uint8_t fingerprint[20] ="+finger+";\n"	
        	 +" String cmd = \"/beatmungsgeraet?serialnumber=\"+ String(" + apikey +");\n" 
             +" String host = "+ host +";\n"
             +" String antwort= \" \";\n"
 	         +" cmd = cmd +" + "String(" + wert + ");\n"
 	         +" httpsGET(host,cmd,antwort,fingerprint);// und absenden \n"   
             +"} // Blockende\n";
           

        return codePrefix + ret + codeSuffix;
	 	}
}

