package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTTLuftdatenSend  extends TranslatorBlock {

	public IoTTLuftdatenSend (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		//translator.addSetupCommand("Serial.begin(115200);");
	/*	
		String Client, Check;
		if (translator.isGPRSProgram()) {
			Client= " TinyGsmClient client(modem);// Client über GSM\n";
			Check=  " int ok = checkMobilfunk();\n";
		} else {
			Client= " WiFiClient client; // Client über WiFi\n";
			Check=  " int ok = 1;\n";
		}
		
		translator.addDefinitionCommand(Client);
		*/
		
		translator.addDefinitionCommand("WiFiClient wifiClient;");
		String httpPOST ="//-------------------------Luftdaten.info  ------ HTTP-POST\n" +
		"void sendLuftdaten(String server, String sensor_id, String Xpin, String data) {\n" + 
			"     HTTPClient http; //Declare object of class HTTPClient\n" + 
			"     String req=\"http://\"+server+\"/v1/push-sensor-data/\";\n" + 
			"     //Serial.println(req);\n" + 
			"     http.begin(wifiClient,req);              // Specify request destination\n" + 
			"     http.addHeader(\"X-PIN\",Xpin); //  \n" + 
			"     http.addHeader(\"X-Sensor\",sensor_id);\n" + 
			"     http.addHeader(\"Content-Type\", \"application/json\"); //\n" + 
			"     Serial.println(\"sensor.community: sent data to sensor with sensor id = \" + sensor_id);\n" + 
			"     Serial.println(data);\n" + 
			"     int httpCode = http.POST(data); //Send the request\n" + 
			"     String payload = http.getString(); //Get the response payload\n" + 
			"     //Serial.print(httpCode);  //Print HTTP return code\n" + 
			"     Serial.println(payload); //Print request response payload\n" + 
			"     http.end(); //Close connection\n"+
        "}\n";
		translator.addDefinitionCommand(httpPOST);
		

		
		String host;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();
        String 	JsonCode =  "//--------------   Send Data to luftdaten.info \n";

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    String ChipID = translatorBlock.toCode();
	    if (ChipID.length()<=11)
	     JsonCode += " String ChipID = \"esp8266-\"+String(ESP.getChipId());\n";
	     else 
         JsonCode += " String ChipID = "+translatorBlock.toCode() +";\n";
	    
	     //JsonCode += " Serial.println(\"please register your device, your ChipID / Sensor ID = \"+myChipID);\n";
	 	
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	     JsonCode +=  "//--------------   Send Data to sensor.community \n"+
	    		" String data = \"{\\\"sensordatavalues\\\":[{\";\n" + 
	    		" data += \"\\\"value_type\\\":\\\"temperature\\\",\\\"value\\\":\\\"\";\n" + 
	    		" data += String("+translatorBlock.toCode()+");\n" ; 
	    		
	    translatorBlock = this.getTranslatorBlockAtSocket(3);
	    if (translatorBlock!=null)
          JsonCode += 
                "  data += \"\\\"},{\";\n" + 
          		"  data += \"\\\"value_type\\\":\\\"humidity\\\",\\\"value\\\":\\\"\";\n" + 
        		"  data += String("+translatorBlock.toCode()+");\n" ; 
            	
	    translatorBlock = this.getTranslatorBlockAtSocket(4);
	    if (translatorBlock!=null)
          JsonCode += 
                "  data += \"\\\"},{\";\n" + 
          		"  data += \"\\\"value_type\\\":\\\"pressure\\\",\\\"value\\\":\\\"\";\n" + 
        		"  data += String("+translatorBlock.toCode()+"*100.0);\n" ; 
            	
	    JsonCode+="  data += \"\\\"}]}\";\n";	    	
	    JsonCode += "sendLuftdaten("+host+",ChipID,\"11\",data);// Post BME280 to Luftdaten.info\n";
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(5);
	    if (translatorBlock!=null) {
	         JsonCode += 
	         	" data = \"{\\\"sensordatavalues\\\":[{\";\n" + 
    	    	" data += \"\\\"value_type\\\":\\\"P1\\\",\\\"value\\\":\\\"\";\n" + 
        		" data += String("+translatorBlock.toCode()+");\n" ; 
	        		
	         translatorBlock = this.getTranslatorBlockAtSocket(6);
	         if (translatorBlock!=null) {
	           JsonCode += 
                " data += \"\\\"},{\";\n" + 
    	    	" data += \"\\\"value_type\\\":\\\"P2\\\",\\\"value\\\":\\\"\";\n" + 
    	        " data += String("+translatorBlock.toCode()+");\n" ; 
	         }		
	         JsonCode+="  data += \"\\\"}]}\";\n";	    	
	         JsonCode += "sendLuftdaten("+host+",ChipID,\"1\",data);// Post Dust to Luftdaten.info\n";
	    } 
	    
	    

        return codePrefix + JsonCode + codeSuffix;
	 	}
}

