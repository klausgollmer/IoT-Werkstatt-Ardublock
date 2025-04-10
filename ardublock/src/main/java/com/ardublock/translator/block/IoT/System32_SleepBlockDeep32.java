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
	   		+  "/*\n"
	   		+ "  gpio_reset_pin((gpio_num_t)1);\n"
	   		+ "  gpio_reset_pin((gpio_num_t)3);\n"
	   		+ "  gpio_reset_pin((gpio_num_t)16);\n"
	   		+ "  gpio_reset_pin((gpio_num_t)17);\n"
	   		+ "\n"
	   		+ "\n"
	   		+ "\n"
	   		+ " // Alternativ viele RTC GPIOs gemeinsam terminieren\n"
	   		+ "  const gpio_num_t rtc_gpio_pins_low[] = {\n"
	   		+ "    GPIO_NUM_4, GPIO_NUM_12, GPIO_NUM_13, \n"
	   		+ "    GPIO_NUM_15, GPIO_NUM_25, GPIO_NUM_26, GPIO_NUM_27,\n"
	   		+ "    GPIO_NUM_32, GPIO_NUM_33, GPIO_NUM_34, GPIO_NUM_35,\n"
	   		+ "    GPIO_NUM_36, GPIO_NUM_39\n"
	   		+ "  };\n"
	   		+ "\n"
	   		+ "  for (auto pin : rtc_gpio_pins_low) {\n"
	   		+ "    rtc_gpio_init(pin);\n"
	   		+ "    rtc_gpio_set_direction(pin, RTC_GPIO_MODE_INPUT_ONLY);\n"
	   		+ "    rtc_gpio_pulldown_en(pin);\n"
	   		+ "    rtc_gpio_pullup_dis(pin);\n"
	   		+ "  }\n"
	   		+ "\n"
	   		+ "  const gpio_num_t rtc_gpio_pins_high[] = {\n"
	   		+ "    GPIO_NUM_0,GPIO_NUM_2,GPIO_NUM_14\n"
	   		+ "  };\n"
	   		+ "\n"
	   		+ "  for (auto pin : rtc_gpio_pins_high) {\n"
	   		+ "    rtc_gpio_init(pin);\n"
	   		+ "    rtc_gpio_set_direction(pin, RTC_GPIO_MODE_INPUT_ONLY);\n"
	   		+ "    rtc_gpio_pullup_en(pin);\n"
	   		+ "    rtc_gpio_pulldown_dis(pin);\n"
	   		+ "  }\n"
	   		+ "\n"
	   		+ "\n"
	   		+ "  // Peripherien deaktivieren\n"
	   		+ "  WiFi.disconnect(true);\n"
	   		+ "  WiFi.mode(WIFI_OFF);\n"
	   		+ "  btStop();\n"
	   		+ "  esp_wifi_stop();\n"
	   		+ "  */\n "
	        +  "esp_sleep_enable_timer_wakeup("+Delay_ms+" * 1000ULL);\n"
	   	    +  "esp_deep_sleep_start();\n";
	 	ret = "#ifdef ESP32\n "+ret+"#else IOTW_PRINTLN(F(\"deep sleep ESP32 only\"));\n #endif \n";
	    return ret;
 	}
}

