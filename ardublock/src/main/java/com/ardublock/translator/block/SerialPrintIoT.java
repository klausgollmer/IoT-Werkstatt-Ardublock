package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SerialPrintIoT extends TranslatorBlock
{
	public SerialPrintIoT(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
			text = text.replace("\\\\n", "\\n");
		}
		
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String test=translatorBlock.toCode();
		if(test.equals("true")){
		    ret="Serial.println("+text+");\n";
		} else {
		    ret="Serial.print("+text+");\n";
		}
		return ret;
	}
}
