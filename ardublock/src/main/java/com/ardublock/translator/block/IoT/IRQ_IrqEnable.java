package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IRQ_IrqEnable  extends TranslatorBlock {

	public IRQ_IrqEnable (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		//translator.addSetupCommand("Serial.begin(115200);");
		translator.setIRQProgram(true);
		//translator.addHeaderFile("#define USE_IRQ");
		
		
		String ret="";
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String test=translatorBlock.toCode();
		if(test.equals("false")){
			ret = "noInterrupts();";
		} else {
			ret = "interrupts();";
		}
		return ret;
	}
}

