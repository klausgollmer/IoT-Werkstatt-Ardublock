package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class WLAN_BuildAP  extends TranslatorBlock {

	public WLAN_BuildAP (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		//translator.addSetupCommand("Serial.begin(115200);");
	
		
		translator.addDefinitionCommand("String matrixausgabe_text  = \" \"; // Ausgabetext als globale Variable\n");
		translator.addDefinitionCommand("volatile int matrixausgabe_index = 0;// aktuelle Position in Matrix\n");
		translator.addDefinitionCommand("IPAddress myOwnIP; // ownIP for mDNS \n");

		translator.setAPProgram(true);
		translator.setWiFiProgram(true);

		String ssid,pass="\"\"",ret;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    ssid = translatorBlock.toCode();

	    translatorBlock = this.getTranslatorBlockAtSocket(1);

	    if (translatorBlock!=null)
 	        pass = translatorBlock.toCode();
   
    
	    ret = "//------------ eigenen WLAN - Accespoint aufbauen \n"
	         +"WiFi.softAP(" + ssid + "," + pass +");\n"
             +"IOTW_PRINT(\"\\nAccessPoint SSID:\"); IOTW_PRINT("+ssid+");\n"        
	         +"IOTW_PRINTLN (\"  IP:\"+ WiFi.softAPIP().toString());\n"
             +"myOwnIP = WiFi.softAPIP();\n"
	         +"matrixausgabe_text = String(\"Mein Netz:\") + String(" + ssid + ") + String( \" IP:\") + WiFi.softAPIP().toString();\n"
   		     +"matrixausgabe_index=0;\n";
	     
	    
        return codePrefix + ret + codeSuffix;
	 	}
}

