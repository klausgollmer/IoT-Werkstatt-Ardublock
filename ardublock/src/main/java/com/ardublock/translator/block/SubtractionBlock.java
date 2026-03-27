package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SubtractionBlock extends TranslatorBlock
{
	public SubtractionBlock(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
	//	System.out.println("subs");
		String ret = "( ";
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		ret = ret + translatorBlock.toCode();
	//	System.out.println(translatorBlock.toCode());
		ret = ret + " - ";
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	//	System.out.println(translatorBlock.toCode());
		ret = ret + translatorBlock.toCode();
		ret = ret + " )";
		return codePrefix + ret + codeSuffix;
	}

}
