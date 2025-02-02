package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class WLAN_ConnectTimeout  extends TranslatorBlock {

	public WLAN_ConnectTimeout (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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

		
		String ssid,pass="\"\"",time;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    ssid = translatorBlock.toCode();

    
	    translatorBlock = this.getTranslatorBlockAtSocket(1);
	    if (translatorBlock!=null)
 	        pass = translatorBlock.toCode();
	    
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    time = translatorBlock.toCode();


	    String Funct = "//------------ WLAN initialisieren \n"
    	 	 +"bool WiFiConnect(const char* ssid, const char* pass, int Tout) {\n"
	    	 +" int cnt = Tout;\n" 	
    	 	 +" WiFi.disconnect();WiFi.persistent(false);\n"
	         +" WiFi.mode(WIFI_STA);\n"
	         +" delay(100);\n"
	         +" IOTW_PRINT (\"\\nWLAN connect to: \");\n"
	         +" IOTW_PRINT(ssid);\n"     
	         + "IOTW_PRINT(\" \");\n"
	         +" WiFi.begin(ssid,pass);\n"
	         +" while ((WiFi.status() != WL_CONNECTED) && (cnt>0)) { // Warte bis Verbindung steht \n"
	         +"  delay(1000); IOTW_PRINT(\".\");\n" 
	         +"  cnt--;\n" 
	  	     +" };\n"
	         +" if (cnt>0) {"
	       	 +"   IOTW_PRINT(F(\" meine IP: \"));\n"
	         +"   IOTW_PRINT(WiFi.localIP().toString());\n"
	       	 +"   IOTW_PRINTLN(F(\" ✅ connected\"));\n"
	     	         +"   myOwnIP = WiFi.localIP();\n"
	         +" } else {\n"
	     	 +"    IOTW_PRINT(F(\"⚠ no WiFi connection\"));\n"
	         +" }\n"
//	         +" matrixausgabe_text = \" Meine IP:\" + WiFi.localIP().toString();\n"
//   		     +" matrixausgabe_index=0;\n"
   		     +" return cnt>0;\n"
 	         +"}\n"; 
	    translator.addDefinitionCommand(Funct);
	   	      
        return codePrefix + "WiFiConnect("+ssid+","+pass+","+time+")" + codeSuffix;
	 	}
}

