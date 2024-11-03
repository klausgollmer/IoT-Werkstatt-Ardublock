package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTBlynkSend  extends TranslatorBlock {

	public IoTBlynkSend (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		

		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		//translator.addHeaderFile("BlynkMultiClient.h");
		translator.addSetupCommand("Serial.begin(115200);");
		
			
		String pin,wert,ret;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    pin = translatorBlock.toCode();

	        
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
        wert = translatorBlock.toCode();
	   
        ret = "Blynk.virtualWrite(V"+pin+","+wert+");// Wert an Blynk-Server übermitteln\n"+
              "Blynk.run();// Blynk Housekeeping\n";
         

        return codePrefix + ret + codeSuffix;
	 	}
}

