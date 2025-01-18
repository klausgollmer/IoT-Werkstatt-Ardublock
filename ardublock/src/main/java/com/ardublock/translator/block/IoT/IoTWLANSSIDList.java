package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTWLANSSIDList  extends TranslatorBlock {

	public IoTWLANSSIDList (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("WiFi_SSIDListner.h");
		
		//translator.addSetupCommand("Serial.begin(115200);");
		translator.addSetupCommand("SSIDListner(); // from https://github.com/kalanda/esp8266-sniffer");
		translator.setWiFiProgram(true);
			    
	    return "";
	 	}
}

