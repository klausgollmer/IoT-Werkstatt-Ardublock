package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;
import com.ardublock.translator.block.TranslatorBlock;



public class System32_Frequency32 extends TranslatorBlock
{

	public System32_Frequency32(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
			+ " // system_update_cpu_freq("+speed+");\n"
			+ "    Serial.print(F(\"sorry frequency change 32 only\"));\n"
			+ "#endif\n"
			+ "\n"
			+ "#if defined(ESP32)\n"
			+ "   setCpuFrequencyMhz("+speed+");\n"
			+ "#endif\n";
		return ret;
	}
}

