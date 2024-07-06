package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTTimerDetach  extends TranslatorBlock {

	public IoTTimerDetach (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
		String timer;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    timer = translatorBlock.toCode();
	    String timer_name = "mytimer"+timer;
				
		translator.addSetupCommand("Serial.begin(115200);");
	
        return codePrefix + timer_name+".detach();" + codeSuffix;
	 	}
}

