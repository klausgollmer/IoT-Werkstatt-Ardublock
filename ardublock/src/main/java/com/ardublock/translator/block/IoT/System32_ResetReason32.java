package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class System32_ResetReason32 extends TranslatorBlock
{

  public System32_ResetReason32 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Deklarationen hinzuf�gen
    // translator.addDefinitionCommand("ADC_MODE(ADC_VCC); // analogInput is uses for VCC measure");   		   	
    
   // Code von der Mainfunktion
	translator.addHeaderFile("#if defined(ESP32)\n #include <rom/rtc.h> \n #endif\n");
    //translator.setWiFiProgram(true);
    String cpu;
    TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0);
    if (translatorBlock!=null)
	       cpu = translatorBlock.toCode();
		else 
		   cpu = "-1";	// use last channel

    
    String Reason = "int resetReason(int cpu) {\r\n" + 
            "#ifdef ESP32\n"+
    		"#if (IOTW_DEBUG_LEVEL >1)\r\n"
    		+ "  IOTW_PRINT(F(\"CPU0 reset reason: \"));\r\n"
    		+ "  IOTW_PRINTLN(rtc_get_reset_reason(0));\r\n"
    		+ "  IOTW_PRINT(F(\"CPU1 reset reason: \"));\r\n"
    		+ "  IOTW_PRINTLN(rtc_get_reset_reason(1));\r\n"
    		+ "#endif\r\n"
    		+ "switch (rtc_get_reset_reason(cpu))\r\n"
    		+ " {\r\n"
    		+ "    case 1 : IOTW_PRINTLN (F(\"POWERON_RESET\"));break;          /**<1, Vbat power on reset*/\r\n"
    		+ "    case 3 : IOTW_PRINTLN (F(\"SW_RESET\"));break;               /**<3, Software reset digital core*/\r\n"
    		+ "    case 4 : IOTW_PRINTLN (F(\"OWDT_RESET\"));break;             /**<4, Legacy watch dog reset digital core*/\r\n"
    		+ "    case 5 : IOTW_PRINTLN (F(\"DEEPSLEEP_RESET\"));break;        /**<5, Deep Sleep reset digital core*/\r\n"
    		+ "    case 6 : IOTW_PRINTLN (F(\"SDIO_RESET\"));break;             /**<6, Reset by SLC module, reset digital core*/\r\n"
    		+ "    case 7 : IOTW_PRINTLN (F(\"TG0WDT_SYS_RESET\"));break;       /**<7, Timer Group0 Watch dog reset digital core*/\r\n"
    		+ "    case 8 : IOTW_PRINTLN (F(\"TG1WDT_SYS_RESET\"));break;       /**<8, Timer Group1 Watch dog reset digital core*/\r\n"
    		+ "    case 9 : IOTW_PRINTLN (F(\"RTCWDT_SYS_RESET\"));break;       /**<9, RTC Watch dog Reset digital core*/\r\n"
    		+ "    case 10 : IOTW_PRINTLN (F(\"INTRUSION_RESET\"));break;       /**<10, Instrusion tested to reset CPU*/\r\n"
    		+ "    case 11 : IOTW_PRINTLN (F(\"TGWDT_CPU_RESET\"));break;       /**<11, Time Group reset CPU*/\r\n"
    		+ "    case 12 : IOTW_PRINTLN (F(\"SW_CPU_RESET\"));break;          /**<12, Software reset CPU*/\r\n"
    		+ "    case 13 : IOTW_PRINTLN (F(\"RTCWDT_CPU_RESET\"));break;      /**<13, RTC Watch dog Reset CPU*/\r\n"
    		+ "    case 14 : IOTW_PRINTLN (F(\"EXT_CPU_RESET\"));break;         /**<14, for APP CPU, reseted by PRO CPU*/\r\n"
    		+ "    case 15 : IOTW_PRINTLN (F(\"RTCWDT_BROWN_OUT_RESET\"));break;/**<15, Reset when the vdd voltage is not stable*/\r\n"
    		+ "    case 16 : IOTW_PRINTLN (F(\"RTCWDT_RTC_RESET\"));break;      /**<16, RTC Watch dog reset digital core and rtc module*/\r\n"
    		+ "    default : IOTW_PRINTLN (F(\"NO_MEAN\"));\r\n"
    		+ " }\r\n"
    		+ "return rtc_get_reset_reason(cpu);\r\n" +
    		"#else\n"+
    		"  IOTW_PRINT(F(\"reset reason ESP32 only \")); return 0;\r\n" + 
    	    "#endif\n"	+	
    		"}";
    
    Reason = "int resetReason(int cpu) {\r\n"
    		+ "#ifdef ESP32\r\n"
    		+ "#if (IOTW_DEBUG_LEVEL >1)\r\n"
    		+ "  IOTW_PRINT(F(\"reset reason: \"));\r\n"
    		+ "#endif\r\n"
    		+ "  esp_reset_reason_t reason = esp_reset_reason();\r\n"
    		+ "\r\n"
    		+ "  switch (reason) {\r\n"
    		+ "    case ESP_RST_UNKNOWN:IOTW_PRINTLN(F(\"Unbekannt\"));break;\r\n"
    		+ "    case ESP_RST_POWERON:IOTW_PRINTLN(F(\"Power-On Reset\"));break;\r\n"
    		+ "    case ESP_RST_EXT:IOTW_PRINTLN(F(\"Externer Reset (Reset-Taster)\"));break;\r\n"
    		+ "    case ESP_RST_SW:IOTW_PRINTLN(F(\"Software Reset\"));break;\r\n"
    		+ "    case ESP_RST_PANIC:IOTW_PRINTLN(F(\"Panik-Reset (Absturz)\"));break;\r\n"
    		+ "    case ESP_RST_INT_WDT:IOTW_PRINTLN(F(\"Interrupt Watchdog Reset\"));break;\r\n"
    		+ "    case ESP_RST_TASK_WDT:IOTW_PRINTLN(F(\"Task Watchdog Reset\"));break;\r\n"
    		+ "    case ESP_RST_WDT:IOTW_PRINTLN(F(\"Allgemeiner Watchdog Reset\"));break;\r\n"
    		+ "    case ESP_RST_DEEPSLEEP:IOTW_PRINTLN(F(\"Aufwachen aus Deep Sleep\"));break;\r\n"
    		+ "    case ESP_RST_BROWNOUT:IOTW_PRINTLN(F(\"⚡ Brownout Reset – Vcc war zu niedrig\"));break;\r\n"
    		+ "    case ESP_RST_SDIO:IOTW_PRINTLN(F(\"SDIO Reset (selten)\"));break;\r\n"
    		+ "    default:IOTW_PRINTLN(F(\"Nicht definiert\"));break;\r\n"
    		+ "  }\r\n"
    		+ "  return (int) reason;\r\n"
    		+ "#else\r\n"
    		+ "  IOTW_PRINT(F(\"reset reason ESP32 only \")); \r\n"
    		+ "  return 0;\r\n"
    		+ "#endif\r\n"
    		+ "}";
    
    
    
    translator.addDefinitionCommand(Reason);
	ret = "resetReason("+cpu+")"; 
    return codePrefix + ret + codeSuffix;
  }
}