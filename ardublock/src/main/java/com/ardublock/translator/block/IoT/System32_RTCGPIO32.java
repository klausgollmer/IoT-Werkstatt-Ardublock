package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;
import com.ardublock.translator.block.TranslatorBlock;

public class System32_RTCGPIO32 extends TranslatorBlock
{

	public System32_RTCGPIO32(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	String pin = translatorBlock.toCode();
	translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	String level = translatorBlock.toCode();
	translator.addHeaderFile("#ifdef ESP32\n #include <driver/rtc_io.h>\n #endif\n");
	
	
	String def= "//------- set RTC GPIO for static level during deepsleep ---------------------------- \n"
			+ "void RTC_GPIO_set(int pin, int level) {\n"
			+ "  #ifdef ESP32\n"
			+ "  if ((pin==0) || (pin==2) || (pin==4) || ((pin>=12) && (pin<=15)) || ((pin>=25) && (pin<=27)) || ((pin>=32) && (pin<=39))) {\n"
			+ "     rtc_gpio_init((gpio_num_t)pin); // Initialisiere den Pin als RTC_GPIO\n"
			+ "     rtc_gpio_set_direction((gpio_num_t)pin, RTC_GPIO_MODE_OUTPUT_ONLY); // Ausgang setzen\n"
			+ "     rtc_gpio_set_level((gpio_num_t)pin, level); // Level setzen\n"
			+ "		if (level) {\n"
			+ "			rtc_gpio_pulldown_dis((gpio_num_t)pin);      // Interne Pull-Downs deaktivieren (optional)\n"
			+ "		} else {\n"
			+ "			rtc_gpio_pullup_dis((gpio_num_t)pin);       // Interne Pull-Ups deaktivieren\n"
			+ "		}\n"
			+ "  } else {\n"
			+ "    IOTW_PRINTLN(F(\"sorry, only RTC-GPIO 0,2,4,12-15,25-27,32-39 allowed\"));\n"
			+ "  }\n"
			+ "  #else\n"
			+ "    IOTW_PRINTLN(F(\"RTC-GPIO ESP32 only\"));\n"
			+ "  #endif\n"
			+ "}";
	translator.addDefinitionCommand(def);
	
	String ret = "RTC_GPIO_set("+pin+","+level+");";
		return ret;
	}
}

