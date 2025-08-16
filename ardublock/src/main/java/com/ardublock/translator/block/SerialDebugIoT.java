package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SerialDebugIoT extends TranslatorBlock
{
	public SerialDebugIoT(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		/**
		 * DO NOT add tab in code any more, we'll use arduino to format code, or the code will duplicated. 
		 */
		String ret="", text = "";
		TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0);
		if (translatorBlock != null)
		{
			text = translatorBlock.toCode();
		}
		
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String test=translatorBlock.toCode();
		if(test.equals("true")){
		    ret="IOTW_PRINTLN("+text+");\n";
		} else {
		    ret="IOTW_PRINT("+text+");\n";
		}
			
		return ret;
	}
}
