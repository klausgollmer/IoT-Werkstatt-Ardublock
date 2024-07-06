package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTTTN_downlink  extends TranslatorBlock {

	public IoTTTN_downlink (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addSetupCommand("Serial.begin(115200);");
		translator.addHeaderFile("#define LORA_DOWNLINK_ENABLE");
		
		
		
	    String vardef = "volatile int LoRaWAN_Tx_Ready   = 0; // Flag for Tx Send \n" + 
	    		"long         LoRaWAN_ms_Wakeup  = 0; // ms at start message\n" + 
	    		"long         LoRaWAN_ms_EmExit  = 0; // max. ms  \n"; 
	    translator.addDefinitionCommand(vardef);
	   
	
	    
	    translator.addDefinitionCommand("int LoRaWAN_Rx_Payload = 0 ;");
	    translator.addDefinitionCommand("int LoRaWAN_Rx_Port = 0 ;");
	    translator.addDefinitionCommand("String LoRaWAN_Rx_Payload_Raw = \"\" ;");
	    
//		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    String ret;
		ret = "void LoRaWAN_DownlinkCallback(){ // ---------- my callbackfunction downlink\n";
		TranslatorBlock translatorBlock = getTranslatorBlockAtSocket(0);
		while (translatorBlock != null)
		{
			ret = ret + translatorBlock.toCode();
			translatorBlock = translatorBlock.nextTranslatorBlock();
		}
		ret = ret + "}\n\n";
		translator.addDefinitionCommand(ret);
			
 	    translator.setLORAProgram(true);              
	    return "";
	 	}
}

