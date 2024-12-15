package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Frequency32 extends TranslatorBlock
{

	public Frequency32(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	String speed = translatorBlock.toCode();
	String ret = "//------- Change CPU Speed ---------------------------- \n"
			+ "#if defined(ESP8266)\n"
			+ "  system_update_cpu_freq("+speed+");\n"
			+ "#endif\n"
			+ "\n"
			+ "#if defined(ESP32)\n"
			+ "   setCpuFrequencyMhz("+speed+");\n"
			+ "#endif\n";
		return ret;
	}
}

