package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTTouchRotMinMax extends TranslatorBlock
{

  public IoTTouchRotMinMax (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

	
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		 String ret;
		 TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		 String min = translatorBlock.toCode();
		 translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		 String max = translatorBlock.toCode();
 
		  
		 String EncDef = "// Touch Encoder \r\n" + 
			  		"// Define Pins for Touch Sensors\r\n" +
			  		"#if defined(ESP32) \n "+
			  		"\r\n" + 
			  		"// Variablen für Software-Entprellen\r\n" + 
			  		"volatile unsigned long lastDebounceTimeUp = 0;\r\n" + 
			  		"volatile unsigned long lastDebounceTimeDown = 0;\r\n" + 
			  		"// Define Counter Variable\r\n" + 
			  		"volatile int touch_counter_rot = 0;\r\n" + 
			  		"volatile int touch_counter_rot_min = -100;\r\n" + 
			  		"volatile int touch_counter_rot_max = 100;\r\n" + 
			  		"\r\n" + 
			  		"void IRAM_ATTR ISR_touchCounterUp() {\r\n" + 
			  		"  // Entprellen für T7\r\n" + 
			  		"  if ((millis() - lastDebounceTimeUp) > IOTW_TOUCH_DEBOUNCE_DELAY) {\r\n" +
			  		"    touch_counter_rot = (touch_counter_rot < touch_counter_rot_max) ? (touch_counter_rot + 1) : touch_counter_rot_max;\r\n"+
			  		"    lastDebounceTimeUp = millis();\r\n" + 
			  		"  }\r\n" + 
			  		"}\r\n" + 
			  		"\r\n" + 
			  		"void IRAM_ATTR ISR_touchCounterDown() {\r\n" + 
			  		"  // Entprellen für T9\r\n" + 
			  		"  if ((millis() - lastDebounceTimeDown) > IOTW_TOUCH_DEBOUNCE_DELAY) {\r\n" + 
			  		"    touch_counter_rot = (touch_counter_rot > touch_counter_rot_min) ? (touch_counter_rot - 1) : touch_counter_rot_min;\r\n"+
			  		"    lastDebounceTimeDown = millis();\r\n" + 
			  		"  }\r\n" + 
			  		"}\r\n" + 
			  		"\r\n" + 
			  		"\r\n"+ 
			  		"bool touchReadState(int pin) {\r\n" + 
			  		"  bool wert=0;\r\n" + 
			  		"  switch(pin) {\r\n" + 
			  		"    case 1: wert = (touchRead(IOTW_TOUCH_PIN_UP)<IOTW_TOUCH_UP_THRESHOLD);break;\r\n" + 
			  		"    case 2: wert = (touchRead(IOTW_TOUCH_PIN_DOWN)<IOTW_TOUCH_DOWN_THRESHOLD);break;\r\n" + 
			  		"    case 3: wert = (touchRead(IOTW_TOUCH_PIN_BUTTON)<IOTW_TOUCH_BUTTON_THRESHOLD); break;\r\n" + 
			  		"    default: Serial.println(\"touch button 1-3\\n\"); break;\r\n" + 
			  		"  }\r\n" + 
			  		"  return wert;\r\n" + 
			  		"}\n "+ 
			  		"#endif\n";
				      

		    translator.addDefinitionCommand(EncDef);

		    ret        ="#if defined(ESP32) \n "
				   	+   "// Configure upper/lower bounds touch\r\n" +
		    		    " touch_counter_rot_min="+min+";\n"+
		    		    " touch_counter_rot_max="+max+";\n"+
		    		    "#endif \n";
		return codePrefix + ret + codeSuffix;
	}
}