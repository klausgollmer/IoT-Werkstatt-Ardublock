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
		
		translator.addDefinitionCommand("WiFiManager wm; // MIT Licence (c) tzapu, https://github.com/tzapu/WiFiManager");
		
		translator.setWiFiProgram(true);

		
		
		String ssid="",pass="",ret;
		boolean pass_ok = false, ssid_ok = false;
		
		TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0);
	    if (translatorBlock!=null) {
		    ssid = translatorBlock.toCode();
    	    ssid_ok = true;
	    }

	    translatorBlock = this.getTranslatorBlockAtSocket(1);
	    if (translatorBlock!=null) {
    	    pass = translatorBlock.toCode();
    	    pass_ok = true;
	    }

	    if (ssid_ok) {
	      if (pass_ok) {
	       ret = "int res = wm.autoConnect("+ssid+","+pass+");\n";
	      } else
		   ret = "int res = wm.autoConnect("+ssid+");\n";
	    } else {
	       ret = "int res = wm.autoConnect();\n";
	    }
	    
	    ret +=    "if(!res) {\n"
	    		+ "   Serial.println(F(\"Failed to connect\"));\n"
	    		+ "} else {\n"
	    		+ "   Serial.println (\"\\nconnected, meine IP:\"+ WiFi.localIP().toString());\n"
	    		+ "}";
	    
        return codePrefix + ret + codeSuffix;
	 	}
}

