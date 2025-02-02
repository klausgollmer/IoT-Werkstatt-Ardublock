package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class HTTP_LuftdatenSend  extends TranslatorBlock {

	public HTTP_LuftdatenSend (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
		
		/*
		String httpPOST ="//-------------------------Luftdaten.info  ------ HTTP-POST\n" +
		"void sendLuftdaten(String server, String sensor_id, String Xpin, String data) {\n" + 
			"     HTTPClient http; //Declare object of class HTTPClient\n" + 
			"     String req=\"http://\"+server+\"/v1/push-sensor-data/\";\n" + 
			"     //IOTW_PRINTLN(req);\n" + 
			"     http.begin(wifiClient,req);              // Specify request destination\n" + 
			"     http.addHeader(\"X-PIN\",Xpin); //  \n" + 
			"     http.addHeader(\"X-Sensor\",sensor_id);\n" + 
			"     http.addHeader(\"Content-Type\", \"application/json\"); //\n" + 
			"     IOTW_PRINTLN(\"sensor.community: sent data to sensor with sensor id = \" + sensor_id);\n" + 
			"     IOTW_PRINTLN(data);\n" + 
			"     int httpCode = http.POST(data); //Send the request\n" + 
			"     String payload = http.getString(); //Get the response payload\n" + 
			"     //IOTW_PRINT(httpCode);  //Print HTTP return code\n" + 
			"     IOTW_PRINTLN(payload); //Print request response payload\n" + 
			"     http.end(); //Close connection\n"+
        "}\n";
        */
		
		String httpPOST = "// ----------------------------------------------------------------------------\n"
				+ "// sendLuftdaten: Sendet Messwerte an sensor.community (Luftdaten.info)\n"
				+ "//\n"
				+ "// Parameter:\n"
				+ "//   server:     z.B. \"api.sensor.community\"\n"
				+ "//   sensor_id:  Die ID des Sensors (String)\n"
				+ "//   Xpin:       Der X-PIN Header-Wert (String)\n"
				+ "//   data:       Die zu sendenden JSON-Daten\n"
				+ "//\n"
				+ "// ----------------------------------------------------------------------------\n"
				+ "void sendLuftdaten(String server, String sensor_id, String Xpin, String data) {\n"
				+ "  // Debug-/Fehlermeldungen\n"
				+ "  String errorString      = \"\";\n"
				+ "  String errorStringDebug = \"\";\n"
				+ "  IOTW_PRINT(F(\"SensorCommunity \"));\n"
				+ "  // 1) WLAN-Verbindung prüfen\n"
				+ "  if (WiFi.status() != WL_CONNECTED) {\n"
				+ "    errorString += \"⚠ no WiFi connection\";\n"
				+ "    IOTW_PRINTLN(errorString);\n"
				+ "    return;\n"
				+ "  }\n"
				+ "\n"
				+ "  // 2) URL korrekt zusammenbauen\n"
				+ "  String url;\n"
				+ "  if (server.startsWith(\"http://\") || server.startsWith(\"https://\")) {\n"
				+ "    url = server + \"/v1/push-sensor-data/\";\n"
				+ "  } \n"
				+ "  else {\n"
				+ "    url = \"http://\" + server + \"/v1/push-sensor-data/\";\n"
				+ "  }\n"
				+ "\n"
				+ "  // Debug-Informationen vorbereiten\n"
				+ "  errorStringDebug += \"[INFO] Using HTTPClient for POST:\\n\";\n"
				+ "  errorStringDebug += \"URL: \" + url + \"\\n\";\n"
				+ "  errorStringDebug += \"X-Sensor: \" + sensor_id + \"\\n\";\n"
				+ "  errorStringDebug += \"X-PIN: \" + Xpin + \"\\n\";\n"
				+ "  errorStringDebug += \"JSON: \" + data + \"\\n\";\n"
				+ "\n"
				+ "  // 3) HTTP-Request vorbereiten\n"
				+ "  WiFiClient wifiClient;\n"
				+ "  HTTPClient http;\n"
				+ "\n"
				+ "  http.begin(wifiClient, url);\n"
				+ "  http.addHeader(\"X-PIN\", Xpin);\n"
				+ "  http.addHeader(\"X-Sensor\", sensor_id);\n"
				+ "  http.addHeader(\"Content-Type\", \"application/json\");\n"
				+ "\n"
				+ "  // Debugging: Zeige, was gesendet wird\n"
				+ "  IOTW_PRINTLN(\"sensor.community: sending data to sensor with ID = \" + sensor_id);\n"
				+ "  IOTW_PRINTLN(data);\n"
				+ "\n"
				+ "  // 4) POST-Anfrage senden\n"
				+ "  int httpCode = http.POST(data);\n"
				+ "  String payload = http.getString();\n"
				+ "\n"
				+ "  if (httpCode > 0) {\n"
				+ "    if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_CREATED) {\n"
				+ "      errorString += \"✅ success\";\n"
				+ "    } \n"
				+ "    else {\n"
				+ "      errorString += \"❌ Unexpected HTTP-Code: \" + String(httpCode);\n"
				+ "    }\n"
				+ "  } \n"
				+ "  else {\n"
				+ "    errorString += \"❌ HTTP POST failed: \" + http.errorToString(httpCode);\n"
				+ "  }\n"
				+ "\n"
				+ "  // Verbindung schließen\n"
				+ "  http.end();\n"
				+ "\n"
				+ "  // 5) Debug- und Fehlermeldungen ausgeben\n"
				+ "  IOTW_PRINTLN(errorString);\n"
				+ "#if (IOTW_DEBUG_LEVEL >1)\n"
				+ "  IOTW_PRINTLN(errorStringDebug);\n"
				+ "#endif\n"
				+ "}\n";
		
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
	    
	     //JsonCode += " IOTW_PRINTLN(\"please register your device, your ChipID / Sensor ID = \"+myChipID);\n";
	 	
	    
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

