package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class System_BasicOTA  extends TranslatorBlock {

	public System_BasicOTA (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
//		translator.addHeaderFile("ESPmDNS.h");
		translator.addHeaderFile("WiFiUdp.h");
		translator.addHeaderFile("ArduinoOTA.h");
		
		//translator.addSetupCommand("Serial.begin(115200);");
			
		String name;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    name = translatorBlock.toCode();

	    String setup;
	    translator.addSetupCommand("ArduinoOTA.setHostname(" + name + ");\n;");
	    
	    setup = "//-------- On the Air Update ermöglichen \n"
	    	  + "// ACHTUNG: python in der Windows Firewall zulassen! \n"
	    	  + "// Systemsteuerung\\System und Sicherheit\\Windows-Firewall\\Zugelassene Programme \n"	
	    	  +	"  ArduinoOTA.onStart([](){}); \n"
	    	  + "  ArduinoOTA.onEnd([](){}); \n"
	          + "  ArduinoOTA.onProgress([](unsigned int progress, unsigned int total){});\n"
              + "ArduinoOTA.onError([](ota_error_t error) {\n" + 
              "    Serial.printf(\"Error[%u]: \", error);\n" + 
              "    if (error == OTA_AUTH_ERROR) {\n" + 
              "      Serial.println(\"Auth Failed\");\n" + 
              "    } else if (error == OTA_BEGIN_ERROR) {\n" + 
              "      Serial.println(\"Begin Failed\");\n" + 
              "    } else if (error == OTA_CONNECT_ERROR) {\n" + 
              "      Serial.println(\"Connect Failed\");\n" + 
              "    } else if (error == OTA_RECEIVE_ERROR) {\n" + 
              "      Serial.println(\"Receive Failed\");\n" + 
              "    } else if (error == OTA_END_ERROR) {\n" + 
              "      Serial.println(\"End Failed\");\n" + 
              "    }\n" + 
              "  });";
	  		translator.addSetupCommand(setup);
//	    	setup = " ArduinoOTA.begin();\n";
//	  		translator.addSetupCommand(setup);
    
        return codePrefix + "ArduinoOTA.begin();\nArduinoOTA.handle();\n" + codeSuffix;
	 	}
}

