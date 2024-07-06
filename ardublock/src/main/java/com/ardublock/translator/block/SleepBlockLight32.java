package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SleepBlockLight32 extends TranslatorBlock
{

	public SleepBlockLight32(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	String Delay_ms = translatorBlock.toCode();
	translator.setWiFiProgram(true);

	String ret = "//------- Light SLEEP ---------------------------- \n"
		+ "Serial.flush();"
        + "esp_sleep_enable_timer_wakeup("+Delay_ms+" * 1000ULL);\n"
        + "esp_light_sleep_start();";
    	return ret;
	}
	}



