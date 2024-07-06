package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SerialPrintBlock extends TranslatorBlock
{
	public SerialPrintBlock(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		/**
		 * DO NOT add tab in code any more, we'll use arduino to format code, or the code will duplicated. 
		 */
		translator.addSetupCommand("Serial.begin(115200);");
		translator.addSetupCommand("Serial.println();");
//		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0, "Serial.print(", ");\nSerial.print(\" \");\n");
//		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0, "Serial.print(", ");\n");
	// kgo to generate an empty message (CR only) -> no message at socket 0
		String ret="";
		TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0, "Serial.print(", ");\n");
		if (translatorBlock != null)
		{
			ret = translatorBlock.toCode();
		}
		
//		String ret = translatorBlock.toCode();
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String test=translatorBlock.toCode();
//		ret+=test;
		if(test.equals("true")){
		    ret+="Serial.println();\n";
		}
		return ret;
	}
}
