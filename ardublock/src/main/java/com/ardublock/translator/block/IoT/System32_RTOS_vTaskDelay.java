package com.ardublock.translator.block.IoT;


import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class System32_RTOS_vTaskDelay  extends TranslatorBlock {
	public System32_RTOS_vTaskDelay (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
 	String wait = translatorBlock.toCode();
 	 
  	
  	//translator.setWiFiProgram(true);
	translator.addHeaderFile("#if defined(ESP32)\n #include <freertos/task.h> \n #endif\n");
	
	String ret = "vTaskDelay(pdMS_TO_TICKS("+wait+"));\n";
  	return ret;
 	}
}

