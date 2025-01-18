package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class PinModeIoT extends TranslatorBlock
{
	public PinModeIoT(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		/**
		 * DO NOT add tab in code any more, we'll use arduino to format code, or the code will duplicated. 
		 */
		//translator.addSetupCommand("Serial.begin(115200);");
		String ret="", pin;
//		TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0, "Serial.print(", ");\n");
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    pin = translatorBlock.toCode();
		
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String test=translatorBlock.toCode();
		if(test.equals("false")){
			ret = "pinMode( "+pin+" , INPUT);";
		} else {
//  		    ret = "pinMode("+pin+", INPUT_PULLUP)";
			ret = "pinMode( "+pin+" , INPUT_PULLUP);";
		}
		return ret;
	}
}
