package com.ardublock.translator.block.IoT;

import java.util.ResourceBundle;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.VariableDigitalBlock;
import com.ardublock.translator.block.VariablePolyBlock;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class System32_SleepBlockDeep32  extends TranslatorBlock {
	private static ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	public System32_SleepBlockDeep32 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String Delay_ms = translatorBlock.toCode();
		//translator.setWiFiProgram(true);
		translator.setDeepSleepProgram(true);
		translator.addHeaderFile("#define IOTW_USE_DEEPSLEEP");
		translator.addHeaderFile("#if defined(ESP32)\n #include <rom/rtc.h> \n #endif\n");
		
		
		String ret = "//------- deep SLEEP ----------------------------\n";
	    if (translator.isLORAProgram()) {
	    	translator.addHeaderFile("#define IOTW_LORA_DEEPSLEEP");
//	    	translator.addHeaderFile("#ifdef ESP32 \n RTC_DATA_ATTR lmic_t RTC_LMIC;\n #endif\n");
	    	translator.addHeaderFile("IoTW_LMIC.h");
	    	ret += "SaveLMICToRTC_ESP32("+Delay_ms+"/1000);\n" ;
		}
		ret += "IOTW_PRINT(F(\"deep sleep \"));\n"
			+  "IOTW_PRINTLN("+Delay_ms+");\n"	
	   		+  "Serial.flush();\n"
	   		+  "Serial.end();\n"
	        +  "esp_sleep_enable_timer_wakeup("+Delay_ms+" * 1000ULL);\n"
	   	    +  "esp_deep_sleep_start();\n";
	 	ret = "#ifdef ESP32\n "+ret+"#else IOTW_PRINTLN(F(\"deep sleep ESP32 only\"));\n #endif \n";
	    return ret;
 	}
}

