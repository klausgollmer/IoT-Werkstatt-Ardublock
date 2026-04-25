package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class PinModeIoT_out extends TranslatorBlock
{
	public PinModeIoT_out(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		String ret="", pin;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    pin = translatorBlock.toCode();
		ret = "pinMode( "+pin+" , OUTPUT);";
		return ret;
	}
}
