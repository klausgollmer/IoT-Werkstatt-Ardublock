package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTResetReason32 extends TranslatorBlock
{

  public IoTResetReason32 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
    translator.setWiFiProgram(true);
    String Reason = "int resetReason(int cpu) {\r\n" + 
    		"  Serial.print(\"CPU0 reset reason: \");\r\n" + 
    		"  Serial.println(rtc_get_reset_reason(0));\r\n" + 
    		"  Serial.print(\"CPU1 reset reason: \");\r\n" + 
    		"  Serial.println(rtc_get_reset_reason(1));\r\n" + 
    		"  return rtc_get_reset_reason(cpu);\r\n" + 
    		"}";
    translator.addDefinitionCommand(Reason);
	ret = "resetReason(0)"; 
    return codePrefix + ret + codeSuffix;
  }
}