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
	//translator.setWiFiProgram(true);
	translator.setDeepSleepProgram(true);
	translator.addHeaderFile("#define USE_DEEPSLEEP");
	translator.addHeaderFile("rom/rtc.h");
	
	String ret = "//------- deep SLEEP ----------------------------\n";
    if (translator.isLORAProgram()) {
    	translator.addHeaderFile("#define LORA_DEEPSLEEP");
    	translator.addHeaderFile("#ifdef ESP32 \n RTC_DATA_ATTR lmic_t RTC_LMIC;\n #endif\n");
    	ret += "SaveLMICToRTC_ESP32("+Delay_ms+"/1000);\n" ;
	}
	ret += "Serial.println(\"Going to deep sleep now\");\n"  
   		+  "Serial.flush();\n"
   		+  "Serial.end();\n"
        +  "esp_sleep_enable_timer_wakeup("+Delay_ms+" * 1000ULL);\n"
   	    +  "esp_deep_sleep_start();";
    	return ret;
	}
	}


