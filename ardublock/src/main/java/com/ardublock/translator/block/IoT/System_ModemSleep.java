package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class System_ModemSleep  extends TranslatorBlock {

	public System_ModemSleep (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		
	    /*
	    def = "// ---- switch Wifi moden on/off"
	      	  "void WifiModemON(boolean onoff) {\n"	
	    	+ "#if defined(ESP8266) \r\n"
	    	+ "	  if (onoff)\n"
	       	+ "      WiFi.forceSleepWake(); // Modem wake up \n"
		    + "     else   \n"
	      	+ "      WiFi.forceSleepBegin(); // Modem Sleep, save Energy\r\n"
	    	+ "#elif defined(ESP32) \r\n"
	    	+ "	  if (onoff) \n"
	       	+ "      WiFi.mode(WIFI_ON); // Modem wake up \n"
		    + "     else   \n"
	    	+ "		 WiFi.mode(WIFI_OFF);   // Modem Sleep, save Energy\r\n"
	    	+ "#endif \r\n";
	    */
        return codePrefix + "WiFi.mode(WIFI_OFF);"+ codeSuffix;
	 	}
}

