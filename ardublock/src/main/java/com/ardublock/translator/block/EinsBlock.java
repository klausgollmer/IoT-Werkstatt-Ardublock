package com.ardublock.translator.block;

import com.ardublock.translator.Translator;

public class EinsBlock extends ConstBlock
{
	public EinsBlock(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
		this.setCode("1");
	}
}
