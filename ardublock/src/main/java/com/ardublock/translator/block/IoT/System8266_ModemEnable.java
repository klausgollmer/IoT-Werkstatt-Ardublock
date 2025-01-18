package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class System8266_ModemEnable  extends TranslatorBlock {

	public System8266_ModemEnable (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
		//translator.addSetupCommand("Serial.begin(115200);");
				
		String mode;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    mode = translatorBlock.toCode();

    
	    String Funct = "//------------ WLAN Modem ausschalten \n"
    	 	 +"void WiFiModemEnable(int mode) {\n"
	    	 +"  if (mode == 0) WiFi.forceSleepBegin(); // Wifi off\n"
    	 	 +"   else WiFi.forceSleepWake(); // Wifi on\n"
 	         +"}\n"; 
	    translator.addDefinitionCommand(Funct);
	   	      
        return codePrefix + "WiFiModemEnable("+mode+");" + codeSuffix;
	 	}
}

