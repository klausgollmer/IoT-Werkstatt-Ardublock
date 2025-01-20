package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class WLAN_Connect  extends TranslatorBlock {

	public WLAN_Connect (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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

		translator.setWiFiProgram(true);
		System.out.println("set WiFi");
		String ssid,pass="\"\"",ret;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    ssid = translatorBlock.toCode();

	    translatorBlock = this.getTranslatorBlockAtSocket(1);
	    if (translatorBlock!=null)
    	    pass = translatorBlock.toCode();
	    

	    ret = "//------------ WLAN initialisieren \n"
	     	 +"WiFi.disconnect();WiFi.persistent(false);\n"
	         +"WiFi.mode(WIFI_STA);\n"
	         +"delay(100);\n"
	         +"Serial.print (\"\\nWLAN connect to:\");\n"
	         +"Serial.print("+ssid+");\n"        
	         +"WiFi.begin(" + ssid + "," + pass +");\n"
	         +"while (WiFi.status() != WL_CONNECTED) { // Warte bis Verbindung steht \n"
	         +"  delay(500); Serial.print(\".\");\n" 
	         +"};\n"
	         +"Serial.println (\"\\nconnected, meine IP:\"+ WiFi.localIP().toString());\n"
	         +"matrixausgabe_text = \" Meine IP:\" + WiFi.localIP().toString();\n"
             +"myOwnIP = WiFi.localIP();\n"
   		     +"matrixausgabe_index=0;\n";
	    
        return codePrefix + ret + codeSuffix;
	 	}
}

