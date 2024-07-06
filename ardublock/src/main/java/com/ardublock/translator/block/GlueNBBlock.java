package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class GlueNBBlock extends TranslatorBlock
{
	public GlueNBBlock(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		String ret = "";
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0, codePrefix, codeSuffix);
		if (translatorBlock != null)
		{
			ret = translatorBlock.toCode();
		}
		return ret;
	}

}
