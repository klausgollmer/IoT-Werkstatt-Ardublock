package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class RoundBlock extends TranslatorBlock
	{

		public RoundBlock(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
		{
			super(blockId, translator, codePrefix, codeSuffix, label);
		}

		@Override
		public String toCode() throws SocketNullException, SubroutineNotDeclaredException
		{
			TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
			String val = translatorBlock.toCode();;
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
			String decimals = translatorBlock.toCode();;
			String ret = "(round("+val+"*pow(10.0,"+decimals+"))/pow(10.0,"+decimals+"))";
			return codePrefix + ret + codeSuffix;
		}
		
	}
