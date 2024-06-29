package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTTouchRot extends TranslatorBlock
{

  public IoTTouchRot (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
	  String ret;
      
	  String EncDef = "// Touch Encoder \r\n" + 
		  		"// Define Pins for Touch Sensors\r\n" +
		  		"#if defined(ESP32) \n "+
		  		"#define TOUCH_PIN_UP T7\r\n" + 
		  		"#define TOUCH_PIN_DOWN T9\r\n" + 
		  		"#define TOUCH_PIN_BUTTON T5\r\n" + 
		  		"#define TOUCH_UP_THRESHOLD 12\r\n" + 
		  		"#define TOUCH_DOWN_THRESHOLD 12\r\n" + 
		  		"#define TOUCH_BUTTON_THRESHOLD 12\r\n" + 
		  		"\r\n" + 
		  		"// Definitionen für Software-Entprellen\r\n" + 
		  		"const unsigned long TOUCH_DEBOUNCE_DELAY = 100; // Zeit in Millisekunden\r\n" + 
		  		"\r\n" + 
		  		"// Variablen für Software-Entprellen\r\n" + 
		  		"volatile unsigned long lastDebounceTimeUp = 0;\r\n" + 
		  		"volatile unsigned long lastDebounceTimeDown = 0;\r\n" + 
		  		"// Define Counter Variable\r\n" + 
		  		"volatile int touch_counter_rot = 0;\r\n" + 
		  		"\r\n" + 
		  		"void IRAM_ATTR ISR_touchCounterUp() {\r\n" + 
		  		"  // Entprellen für T7\r\n" + 
		  		"  if ((millis() - lastDebounceTimeUp) > TOUCH_DEBOUNCE_DELAY) {\r\n" + 
		  		"    touch_counter_rot++;\r\n" + 
		  		"    lastDebounceTimeUp = millis();\r\n" + 
		  		"  }\r\n" + 
		  		"}\r\n" + 
		  		"\r\n" + 
		  		"void IRAM_ATTR ISR_touchCounterDown() {\r\n" + 
		  		"  // Entprellen für T9\r\n" + 
		  		"  if ((millis() - lastDebounceTimeDown) > TOUCH_DEBOUNCE_DELAY) {\r\n" + 
		  		"    touch_counter_rot--;\r\n" + 
		  		"    lastDebounceTimeDown = millis();\r\n" + 
		  		"  }\r\n" + 
		  		"}\r\n" + 
		  		"\r\n" + 
		  		"\r\n"+ 
		  		"bool touchReadState(int pin) {\r\n" + 
		  		"  bool wert=0;\r\n" + 
		  		"  switch(pin) {\r\n" + 
		  		"    case 1: wert = (touchRead(TOUCH_PIN_UP)<TOUCH_UP_THRESHOLD);break;\r\n" + 
		  		"    case 2: wert = (touchRead(TOUCH_PIN_DOWN)<TOUCH_DOWN_THRESHOLD);break;\r\n" + 
		  		"    case 3: wert = (touchRead(TOUCH_PIN_BUTTON)<TOUCH_BUTTON_THRESHOLD); break;\r\n" + 
		  		"    default: Serial.println(\"touch button 1-3\\n\"); break;\r\n" + 
		  		"  }\r\n" + 
		  		"  return wert;\r\n" + 
		  		"}\n "+ 
		  		"#endif\n";
			      

	    translator.addDefinitionCommand(EncDef);

	    EncDef ="#if defined(ESP32) \n "
			   		+  "// Configure Touch Pins as Input\r\n" + 
			   		" touchAttachInterrupt(TOUCH_PIN_UP, ISR_touchCounterUp, TOUCH_UP_THRESHOLD);\r\n" + 
			   		" touchAttachInterrupt(TOUCH_PIN_DOWN, ISR_touchCounterDown, TOUCH_DOWN_THRESHOLD);\r\n" + 
			   		"#endif \n";
	    translator.addSetupCommand(EncDef);

		ret = "touch_counter_rot";
		

		return codePrefix + ret + codeSuffix;
  }
}