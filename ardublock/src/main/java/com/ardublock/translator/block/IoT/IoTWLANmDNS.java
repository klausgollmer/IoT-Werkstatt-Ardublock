package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTWLANmDNS  extends TranslatorBlock {

	public IoTWLANmDNS (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("ESPmDNS.h");
		//translator.addSetupCommand("Serial.begin(115200);");
		translator.addDefinitionCommand("String matrixausgabe_text  = \" \"; // Ausgabetext als globale Variable\n");
		translator.addDefinitionCommand("volatile int matrixausgabe_index = 0;// aktuelle Position in Matrix\n");
		translator.addDefinitionCommand("IPAddress myOwnIP; // ownIP for mDNS \n");

   		
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    String URL = translatorBlock.toCode();
		translator.setmDNSProgram(true);
		translator.setWiFiProgram(true);


	    String ret = "//------------ mDNS - Responder starten \n"
	    	+ "if (MDNS.begin("+URL+")) {              // Start the mDNS responder \n" + 
	    	"    Serial.println(\"mDNS responder started, hostname = \"+String("+URL+")+String(\".local\"));\n" + 
	        "    MDNS.addService(\"http\", \"tcp\", 80);\n"+
	    	"  } else {\n" + 
	    	"    Serial.println(\"Error setting up mDNS responder!\");\n" + 
	    	"  }\n" + 
	    	"matrixausgabe_text = String(\" Meine URL:\") + String("+URL+")+String(\".local\");\n" +
   		    "matrixausgabe_index=0;\n";
	    
        return codePrefix + ret + codeSuffix;
	 	}
}

