package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTBoschSpexSend  extends TranslatorBlock {

	public IoTBoschSpexSend (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		//translator.addSetupCommand("Serial.begin(115200);");
		
	
		String Send ="//-------------------------Bosch Spexor ------ HTTP-POST\n" + 
				"int SpexOldCO2Val = 400; // Old Value for trend \n" + 
				"void sendBosch(String URL, String auth_code, String device,String dstatus, int CO2, int IAQ) {\n" + 
				"  WiFiClientSecure client;\n" + 
				"  const char* host = \"bosch-iot-insights.com\";\n" + 
				"  const char* fingerprint =\"74:AF:00:2B:22:9B:32:6B:0F:B2:C4:C0:6A:19:B5:64:24:7B:6D:D9\";\n" + 
				"  const int   httpsPort = 443;\n" + 
				"  String url      = \"/data-recorder-service/v2/\"+URL;\n" + 
				"  \n" + 
				"  // Trend calculation\n" + 
				"  int CO2_DZ = 30;  // Totzone für Trend, um Rauschen zu unterdrücken\n" + 
				"  String dtrend    = \"STEADY\";\n" + 
				"  if ((CO2-SpexOldCO2Val) > CO2_DZ) {\n" + 
				"     dtrend    = \"IMPROVING\";\n" + 
				"     SpexOldCO2Val = CO2;\n" + 
				"  }\n" + 
				"  if ((SpexOldCO2Val-CO2) > CO2_DZ) {\n" + 
				"     dtrend    = \"DEGRADING\";\n" + 
				"     SpexOldCO2Val = CO2;\n" + 
				"  }\n" + 
				"\n" + 
				"  //JSON Message\n" + 
				"  String msg      = \"{\\\"deviceId\\\":\\\"\"+device+\"\\\",\\\"status\\\":\\\"\"+dstatus+\"\\\",\\\"trend\\\":\\\"\"+dtrend+\"\\\",\\\"CO2\\\":\\\"\"+String(CO2)+\"\\\",\\\"IAQ\\\":\\\"\"+String(IAQ)+\"\\\"}\";\n" + 
				"\n" + 
				"  /*\n" + 
				"  Serial.print(String(\"POST \") + url + \" HTTP/1.1\\r\\n\" +\n" + 
				"    \"Host: \" + host + \"\\r\\n\" +\n" + 
				"    \"Connection: close\\r\\n\" +\n" + 
				"    \"Content-Type: application/json\" + \"\\r\\n\" +\n" + 
				"    \"Authorization: Basic \" + auth_code + \"\\r\\n\" +\n" + 
				"    \"Content-Length: \" + msg.length() + \"\\r\\n\" +\n" + 
				"    \"\\r\\n\" +\n" + 
				"    msg + \"\\r\\n\");\n" + 
				"  */\n" + 
				"  Serial.print(\"connecting to : '\");Serial.print(host); Serial.print(\"'\");\n" + 
				"  client.setFingerprint(fingerprint);\n" + 
				"  if (!client.connect(host, httpsPort)) {\n" + 
				"    Serial.println(\"Connection failed\");\n" + 
				"    return;\n" + 
				"  }\n" + 
				"  Serial.print(\" requesting URL: '\");Serial.print(url);Serial.println(\"'\");\n" + 
				"\n" + 
				"  client.print(String(\"POST \") + url + \" HTTP/1.1\\r\\n\" +\n" + 
				"    \"Host: \" + host + \"\\r\\n\" +\n" + 
				"    \"Connection: close\\r\\n\" +\n" + 
				"    \"Content-Type: application/json\" + \"\\r\\n\" +\n" + 
				"    \"Authorization: Basic \" + auth_code + \"\\r\\n\" +\n" + 
				"    \"Content-Length: \" + msg.length() + \"\\r\\n\" +\n" + 
				"    \"\\r\\n\" +\n" + 
				"    msg + \"\\r\\n\");\n" + 
				"\n" + 
				"  Serial.println(msg);\n" + 
				"  unsigned long timeout = millis();\n" + 
				"  while (client.available() == 0) {\n" + 
				"    if (millis() - timeout > 5000) {\n" + 
				"        Serial.println(\">>> Client Timeout !\");\n" + 
				"        client.stop();\n" + 
				"        return;\n" + 
				"    }\n" + 
				"  }\n" + 
				"\n" + 
				"  String line = client.readStringUntil('\\n');\n" + 
				"  Serial.println(line);\n" + 
				"}\n" + 
				"";
		
	    translator.addDefinitionCommand(Send);
		
		
		String url,aut,dev,state,co2,iaq,ret;
		iaq = "-1";
        co2 = "-1";
        
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    url = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    aut = translatorBlock.toCode();
	    
        translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    dev = translatorBlock.toCode();
    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    state = translatorBlock.toCode();
        
	    translatorBlock = this.getTranslatorBlockAtSocket(4);
	    if (translatorBlock!=null)
	       co2=translatorBlock.toCode();
	    translatorBlock = this.getTranslatorBlockAtSocket(5);
	    if (translatorBlock!=null)
  	       iaq = translatorBlock.toCode();

	    ret = "sendBosch("+url+","+aut+","+dev+","+state+","+co2+","+iaq+");// Post to BoschCloud";
	
        return codePrefix + ret + codeSuffix;
	 	}
}

