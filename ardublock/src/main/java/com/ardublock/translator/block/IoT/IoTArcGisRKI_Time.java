package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTArcGisRKI_Time  extends TranslatorBlock {

	public IoTArcGisRKI_Time (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
		
		translator.addDefinitionCommand("String   RKI_LastTimestamp = \"No Data\"; // Last update   RKI COVID 19 Dashboard");
		translator.addDefinitionCommand("uint32_t RKI_NextUpdate    = millis();    // Next status update RKI");
	
			    
	    String ret = "RKI_LastTimestamp";

        return codePrefix + ret + codeSuffix;
	 	}
}

