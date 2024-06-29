package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SleepBlockDeep32 extends TranslatorBlock
{

	public SleepBlockDeep32(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	String Delay_ms = translatorBlock.toCode();
	translator.setWiFiProgram(true);
	translator.setDeepSleepProgram(true);
	translator.addHeaderFile("#define USE_DEEPSLEEP");
	translator.addHeaderFile("rom/rtc.h");
	String ret = "//------- deep SLEEP ----------------------------\n"
       	+ "Serial.println(\"Going to sleep now\");\n"  
   		+ "Serial.flush();\n"
        + "esp_sleep_enable_timer_wakeup("+Delay_ms+" * 1000ULL);\n"
   	    + "esp_deep_sleep_start();";
    	return ret;
	}
	}



