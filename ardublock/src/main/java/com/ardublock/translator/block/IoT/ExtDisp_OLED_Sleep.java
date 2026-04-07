package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtDisp_OLED_Sleep  extends TranslatorBlock {

	public ExtDisp_OLED_Sleep (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
	   	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
   	    String sleep = translatorBlock.toCode();	   	
		return codePrefix + "IoTW_sleepOLED("+sleep+");" + codeSuffix;
	}
}

