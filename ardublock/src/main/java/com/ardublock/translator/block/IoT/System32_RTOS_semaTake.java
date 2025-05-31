package com.ardublock.translator.block.IoT;


import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class System32_RTOS_semaTake  extends TranslatorBlock {
	public System32_RTOS_semaTake (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		  	String Sema = translatorBlock.toCode();
		  	Sema = Sema.substring(1, Sema.length() - 1);
		  	
		  	//translator.setWiFiProgram(true);
			translator.addHeaderFile("#if defined(ESP32)\n #include <freertos/task.h> \n #endif\n");
			
			
			String Def = "SemaphoreHandle_t "+Sema+" = NULL; // FreeRTOS Sema \n";
			
			translator.addDefinitionCommand(Def);
			
		  	String SetupCMD = "   //------- Create FreeRTOS Task ---------------------------- \n"
		  			+ "   "+Sema+" = xSemaphoreCreateMutex(); \n";

		  	translator.addSetupCommand(SetupCMD);
			String ret = "xSemaphoreTake("+Sema+",portMAX_DELAY);";
		  	return ret;
 	}
}

