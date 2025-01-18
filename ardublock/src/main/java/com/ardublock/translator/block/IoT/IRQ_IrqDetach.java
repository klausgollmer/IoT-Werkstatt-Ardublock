package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IRQ_IrqDetach  extends TranslatorBlock {

	public IRQ_IrqDetach (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		String ret;
		String pin;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    pin = translatorBlock.toCode();
				
		//translator.addSetupCommand("Serial.begin(115200);");
	
        return codePrefix + "detachInterrupt(digitalPinToInterrupt("+pin+"));" + codeSuffix;
	 	}
}

