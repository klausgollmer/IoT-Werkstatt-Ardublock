package com.ardublock.translator.block.IoT;


import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class System32_RTOS_Task  extends TranslatorBlock {
	public System32_RTOS_Task (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
  	String Taskname = translatorBlock.toCode();
  	String Taskfunction = Taskname.substring(1, Taskname.length() - 1)+"_gen";
  	
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
  	String Prio = translatorBlock.toCode();
  	
  	translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
  	String Stack = translatorBlock.toCode();
  	
  	
  	String Setup ="", Loop="";
  	
	translatorBlock = getTranslatorBlockAtSocket(3);
	while (translatorBlock != null)
	{
		Setup = Setup + "   "+ translatorBlock.toCode();
		translatorBlock = translatorBlock.nextTranslatorBlock();
	}
  	
	translatorBlock = getRequiredTranslatorBlockAtSocket(4);
	while (translatorBlock != null)
	{
		Loop = Loop + "   " + translatorBlock.toCode();
		translatorBlock = translatorBlock.nextTranslatorBlock();
	}
  	
  	
  	//translator.setWiFiProgram(true);
	translator.addHeaderFile("#if defined(ESP32)\n #include <freertos/task.h> \n #endif\n");
	
	
	String Task = "// FreeRTOS Task \n"
 			+ "#if defined(ESP32)\n"
			+ " void "+Taskfunction+"(void* pvParameters) {\n"
			+ "  // Setup\n"
			+ Setup
			+ "  // Loop\n"
			+ "  for (;;) {\n"
			+ Loop
			+ "    vTaskDelay(1);  \n"
			+ "  }\n"
			+ " }"
			+ "#endif\n";
	
	translator.addDefinitionCommand(Task);
	
  	String SetupCMD = "   //------- Create FreeRTOS Task ---------------------------- \n"
  			+"#if defined(ESP32)\n"
  			+"   xTaskCreate("+Taskfunction+", "+Taskname+", "+Stack+", NULL, "+Prio+", NULL);\n"
  			+"#else\n"
  			+"   Serial.println(F(\"sorry, FreeRTOS not available (ESP32 only)\"));\n"
  			+"#endif\n";
  	

  	translator.addSetupCommand(SetupCMD);
	String ret = "";
  	return ret;
 	}
}

