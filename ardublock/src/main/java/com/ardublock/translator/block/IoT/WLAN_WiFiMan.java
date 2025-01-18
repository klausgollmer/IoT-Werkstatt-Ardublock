package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class WLAN_WiFiMan  extends TranslatorBlock {

	public WLAN_WiFiMan (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("WiFiManager.h");
		//translator.addSetupCommand("Serial.begin(115200);");
		translator.addDefinitionCommand("String matrixausgabe_text  = \" \"; // Ausgabetext als globale Variable\n");
		translator.addDefinitionCommand("volatile int matrixausgabe_index = 0;// aktuelle Position in Matrix\n");

		//translator.setWiFiProgram(true);

		String ssid,pass,ret;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    ssid = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    pass = translatorBlock.toCode();

	    ret = "//------------ WLAN initialisieren \n"
    	 	 +"Serial.println(\"\\n Starting\");\n" + 
    	 	 "  unsigned long startedAt = millis();\n" + 
    	 	 " \n" + 
    	 	 "  WiFi.printDiag(Serial); //Remove this line if you do not want to see WiFi password printed\n" + 
    	 	 "  Serial.println(\"Opening configuration portal\");\n" + 
    	 	 "  digitalWrite(PIN_LED, LOW); // turn the LED on by making the voltage LOW to tell us we are in configuration mode.\n" + 
    	 	 "  //Local intialization. Once its business is done, there is no need to keep it around\n" + 
    	 	 " \n" + 
    	 	 "  WiFiManager wifiManager;  \n" + 
    	 	 "  //sets timeout in seconds until configuration portal gets turned off.\n" + 
    	 	 "  //If not specified device will remain in configuration mode until\n" + 
    	 	 "  //switched off via webserver.\n" + 
    	 	 "  if (WiFi.SSID()!=\"\") wifiManager.setConfigPortalTimeout(60); //If no access point name has been previously entered disable timeout.\n" + 
    	 	 " \n" + 
    	 	 "  //it starts an access point \n" + 
    	 	 "  //and goes into a blocking loop awaiting configuration\n" + 
    	 	 "  if (!wifiManager.startConfigPortal("+ssid+","+pass+"))  //Delete these two parameters if you do not want a WiFi password on your configuration access point\n" + 
    	 	 "  {\n" + 
    	 	 "     Serial.println(\"Not connected to WiFi but continuing anyway.\");\n" + 
    	 	 "  } \n" + 
    	 	 "  else \n" + 
    	 	 "  {\n" + 
    	 	 "     //if you get here you have connected to the WiFi\n" + 
    	 	 "     Serial.println(\"connected...yeey :)\");\n" + 
    	 	 "  }\n" + 
       	 	 " \n" + 
    	 	 "  Serial.print(\"After waiting \");\n" + 
    	 	 "  int connRes = WiFi.waitForConnectResult();\n" + 
    	 	 "  float waited = (millis()- startedAt);\n" + 
    	 	 "  Serial.print(waited/1000);\n" + 
    	 	 "  Serial.print(\" secs in setup() connection result is \");\n" + 
    	 	 "  Serial.println(connRes);\n" + 
    	 	 "  if (WiFi.status()!=WL_CONNECTED)\n" + 
    	 	 "  {\n" + 
    	 	 "      Serial.println(\"failed to connect, finishing setup anyway\");\n" + 
    	 	 "  } \n" + 
    	 	 "  else\n" + 
    	 	 "  {\n" + 
    	 	 "    Serial.print(\"local ip: \");\n" + 
    	 	 "    Serial.println(WiFi.localIP());\n" + 
    	 	 "  }"
	         +"Serial.println (\"\\nconnected, meine IP:\"+ WiFi.localIP().toString());\n"
	         +"matrixausgabe_text = \" Meine IP:\" + WiFi.localIP().toString();\n"
   		     +"matrixausgabe_index=0;\n";
	    
        return codePrefix + ret + codeSuffix;
	 	}
}

