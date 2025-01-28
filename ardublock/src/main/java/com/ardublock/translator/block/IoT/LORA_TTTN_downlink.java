package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class LORA_TTTN_downlink  extends TranslatorBlock {

	public LORA_TTTN_downlink (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
	
		translator.addHeaderFile("IoTW_LMIC.h");
		translator.addSetupCommand("LoRaWANCallbackPointer=LoRaWAN_DownlinkCallback;");
		
		
//		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    String ret;
		ret = "// LoRaWAN Downlink enabled -> Get Commands from TTN Gateway\n"
				+ "void LoRaWAN_DownlinkCallback(){ // ---------- my callbackfunction downlink\n";
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

