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
    
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String cpu = translatorBlock.toCode();

    
    String Reason = "int resetReason(int cpu) {\r\n" + 
    		"  Serial.print(\"CPU0 reset reason: \");\r\n" + 
    		"  Serial.println(rtc_get_reset_reason(0));\r\n" + 
    		"  Serial.print(\"CPU1 reset reason: \");\r\n" + 
    		"  Serial.println(rtc_get_reset_reason(1));\r\n" + 
    		"  switch (rtc_get_reset_reason(cpu))\r\n"
    		+ " {\r\n"
    		+ "    case 1 : Serial.println (\"POWERON_RESET\");break;          /**<1, Vbat power on reset*/\r\n"
    		+ "    case 3 : Serial.println (\"SW_RESET\");break;               /**<3, Software reset digital core*/\r\n"
    		+ "    case 4 : Serial.println (\"OWDT_RESET\");break;             /**<4, Legacy watch dog reset digital core*/\r\n"
    		+ "    case 5 : Serial.println (\"DEEPSLEEP_RESET\");break;        /**<5, Deep Sleep reset digital core*/\r\n"
    		+ "    case 6 : Serial.println (\"SDIO_RESET\");break;             /**<6, Reset by SLC module, reset digital core*/\r\n"
    		+ "    case 7 : Serial.println (\"TG0WDT_SYS_RESET\");break;       /**<7, Timer Group0 Watch dog reset digital core*/\r\n"
    		+ "    case 8 : Serial.println (\"TG1WDT_SYS_RESET\");break;       /**<8, Timer Group1 Watch dog reset digital core*/\r\n"
    		+ "    case 9 : Serial.println (\"RTCWDT_SYS_RESET\");break;       /**<9, RTC Watch dog Reset digital core*/\r\n"
    		+ "    case 10 : Serial.println (\"INTRUSION_RESET\");break;       /**<10, Instrusion tested to reset CPU*/\r\n"
    		+ "    case 11 : Serial.println (\"TGWDT_CPU_RESET\");break;       /**<11, Time Group reset CPU*/\r\n"
    		+ "    case 12 : Serial.println (\"SW_CPU_RESET\");break;          /**<12, Software reset CPU*/\r\n"
    		+ "    case 13 : Serial.println (\"RTCWDT_CPU_RESET\");break;      /**<13, RTC Watch dog Reset CPU*/\r\n"
    		+ "    case 14 : Serial.println (\"EXT_CPU_RESET\");break;         /**<14, for APP CPU, reseted by PRO CPU*/\r\n"
    		+ "    case 15 : Serial.println (\"RTCWDT_BROWN_OUT_RESET\");break;/**<15, Reset when the vdd voltage is not stable*/\r\n"
    		+ "    case 16 : Serial.println (\"RTCWDT_RTC_RESET\");break;      /**<16, RTC Watch dog reset digital core and rtc module*/\r\n"
    		+ "    default : Serial.println (\"NO_MEAN\");\r\n"
    		+ "  }\r\n"
    		+ "return rtc_get_reset_reason(cpu);\r\n" + 
    		"}";
    translator.addDefinitionCommand(Reason);
	ret = "resetReason("+cpu+")"; 
    return codePrefix + ret + codeSuffix;
  }
}