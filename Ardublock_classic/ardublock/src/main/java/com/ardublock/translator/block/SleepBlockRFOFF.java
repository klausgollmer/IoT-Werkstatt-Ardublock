package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SleepBlockRFOFF extends TranslatorBlock
{

	public SleepBlockRFOFF(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
	String extern="extern \"C\" {  // zur Nutzung der speziellen ESP-Befehle wie Deep Sleep\n"
			     + "   #include \"user_interface.h\"\n"
			     +"}\n";
	translator.addDefinitionCommand(extern);
	
		String ret = "\tESP.deepSleep( ";
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		ret = ret + "(long)"+translatorBlock.toCode()+"*1000UL";
		ret = ret + ",WAKE_RF_DISABLED);//Tiefschlaf, danach Reset und weiter ohne WLAN \n";
		return ret;
	}

}

