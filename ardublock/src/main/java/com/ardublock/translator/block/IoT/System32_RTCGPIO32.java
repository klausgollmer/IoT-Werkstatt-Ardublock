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
			+ "void RTC_GPIO_set(uint8_t pin, uint8_t state) {\n"
			+ "#ifdef ESP32\n"
			+ "  // state = 0,1,2, State 2 ist floating (tristate)\n"
			+ "  // 1) Hold abschalten, damit wir das Pad neu konfigurieren\n"
			+ "  if ((pin==0) || (pin==2) || (pin==4) || ((pin>=12) && (pin<=15)) || ((pin>=25) && (pin<=27)) || ((pin>=32) && (pin<=39))) {\n"
			+ "    rtc_gpio_hold_dis((gpio_num_t)pin);\n"
			+ "\n"
			+ "    if (state <= 1) {\n"
			+ "      // 2) Als Output konfigurieren,\n"
			+ "      rtc_gpio_init((gpio_num_t)pin);\n"
			+ "      rtc_gpio_set_direction((gpio_num_t)pin, RTC_GPIO_MODE_OUTPUT_ONLY);\n"
			+ "      // 3) Level setzen\n"
			+ "      rtc_gpio_set_level((gpio_num_t)pin, state);\n"
			+ "      // 4) Pulls so abschalten, dass sie nicht gegen den Level arbeiten\n"
			+ "      if (state == 1) {\n"
			+ "        rtc_gpio_pulldown_dis((gpio_num_t)pin);\n"
			+ "      } \n"
			+ "      else {\n"
			+ "        rtc_gpio_pullup_dis((gpio_num_t)pin);\n"
			+ "      }\n"
			+ "      // 5) Hold aktivieren, damit der Pegel auch über Deep-Sleep erhalten bleibt\n"
			+ "      rtc_gpio_hold_en((gpio_num_t)pin);\n"
			+ "    } else { \n"
			+ "      rtc_gpio_hold_dis((gpio_num_t)pin);\n"
			+ "      // 2) Digital-Domäne in den echten Hi-Z-Zustand versetzen\n"
			+ "      esp_rom_gpio_pad_select_gpio(pin);            // IOMUX auf „GPIO“ setzen\n"
			+ "      gpio_reset_pin((gpio_num_t)pin);              // GPIO-Controller zurücksetzen\n"
			+ "      gpio_set_pull_mode((gpio_num_t)pin, GPIO_FLOATING); // intern Floating\n"
			+ "      // 3) RTC-Domäne ebenfalls freigeben\n"
			+ "      rtc_gpio_init((gpio_num_t)pin);\n"
			+ "      rtc_gpio_set_direction((gpio_num_t)pin, RTC_GPIO_MODE_INPUT_ONLY);\n"
			+ "      rtc_gpio_pullup_dis((gpio_num_t)pin);\n"
			+ "      rtc_gpio_pulldown_dis((gpio_num_t)pin);\n"
			+ "    }\n"
			+ "  } else {\n"
			+ "      IOTW_PRINTLN(F(\"sorry, only RTC-GPIO 0,2,4,12-15,25-27,32-39 allowed\"));\n"
			+ "  }\n"
			+ "#endif\n"
			+ "  } ";
	translator.addDefinitionCommand(def);
	
	String ret = "RTC_GPIO_set("+pin+","+level+");";
		return ret;
	}
}

