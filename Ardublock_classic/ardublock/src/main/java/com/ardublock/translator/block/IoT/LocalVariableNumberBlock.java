package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;


public class LocalVariableNumberBlock extends TranslatorBlock
{
	public LocalVariableNumberBlock(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode()
	{
		
		String internalVariableName = translator.getLocalNumberVariable(label);
		String declaration = "";
		
		if (internalVariableName == null )
		{
			internalVariableName = translator.buildVariableName(label);	
			declaration = "int ";						
			translator.addLocalNumberVariable(label, internalVariableName);
		}
		return declaration + codePrefix + internalVariableName + codeSuffix;
	}
}

