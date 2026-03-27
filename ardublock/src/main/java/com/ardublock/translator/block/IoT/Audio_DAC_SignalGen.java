package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.NumberBlock;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Audio_DAC_SignalGen extends TranslatorBlock
{
	public Audio_DAC_SignalGen(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		  translator.addHeaderFile("IoTW_Tone.h");
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String dac = translatorBlock.toCode();
		
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String f = translatorBlock.toCode();

		translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
		String vol = translatorBlock.toCode();
		
		String ret = "IoTW_Tone_SignalGen(" + dac + " , " + vol + " , " + f + ");\n";
		return ret;
	}

}


