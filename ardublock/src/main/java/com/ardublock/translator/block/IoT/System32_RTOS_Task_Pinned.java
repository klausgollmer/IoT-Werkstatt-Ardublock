package com.ardublock.translator.block.IoT;


import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class System32_RTOS_Task_Pinned  extends TranslatorBlock {
	public System32_RTOS_Task_Pinned (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
  	
  	String core ="";
  	translatorBlock = getTranslatorBlockAtSocket(3);
  	if (translatorBlock != null)
  		core = translatorBlock.toCode();
  	
  	
  	String Setup ="", Loop="";
  	
	translatorBlock = getTranslatorBlockAtSocket(4);
	while (translatorBlock != null)
	{
		Setup = Setup + "   "+ translatorBlock.toCode();
		translatorBlock = translatorBlock.nextTranslatorBlock();
	}
  	
	translatorBlock = getTranslatorBlockAtSocket(5);
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
			+ "  Serial.printf(\"Create task %s on core=%d prio=%u\\n\",\n"
			+ "              pcTaskGetName(NULL),\n"
			+ "              xPortGetCoreID(),\n"
			+ "              uxTaskPriorityGet(NULL));\n"
			+ Setup
			+ "  // Loop\n"
			+ "  for (;;) {\n"
			+ Loop
			+ "   \n"
			+ "  }\n"
			+ " }"
			+ "#endif\n";
	
	translator.addDefinitionCommand(Task);

	String Create ="";
	if (Integer.parseInt(core) > 1)
		Create = "   xTaskCreate("+Taskfunction+", "+Taskname+", "+Stack+", NULL, "+Prio+", NULL);\n";
	else
		Create = "   xTaskCreatePinnedToCore("+Taskfunction+", "+Taskname+", "+Stack+", NULL, "+Prio+", NULL,"+core+");\n";
		
	
  	String SetupCMD = "   //------- Create FreeRTOS Task ---------------------------- \n"
  			+"#if defined(ESP32)\n"
  			+ Create
  			+"#else\n"
  			+"   Serial.println(F(\"sorry, FreeRTOS not available (ESP32 only)\"));\n"
  			+"#endif\n";
  	

  	translator.addSetupCommand(SetupCMD);
	String ret = "";
  	return ret;
 	}
}

