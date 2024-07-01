package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTButtonRot extends TranslatorBlock
{

  public IoTButtonRot (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
	  String ret;
      
	  String EncDef = "#if defined(ESP8266) \n"
				      + "// Autor Paul Stoffregen, http://www.pjrc.com/teensy/td_libs_Encoder.html\n"
				      + " #include <Encoder.h> \n"
				      + "#elif defined(ESP32) \n"
				      + "// Autor: Kevin Harrington, https://www.arduino.cc/reference/en/libraries/esp32encoder\n"
				      + " #include <ESP32Encoder.h>;\n"
				      + "#endif\n";

		translator.addDefinitionCommand(EncDef);

		EncDef = "// Helper Rotary Encoder \n"
				  + "#if defined(ESP8266) \n"
				  + "Encoder button_encoder(GPIO_ROTARY_B,GPIO_ROTARY_A);\n"
			      + "#elif defined(ESP32) \n"
			      +  "ESP32Encoder button_encoder; \n"
			      + "#endif\n"
 				  + " \n" 
                  + "int encoderRead() {\r\n" + 
				    "  int wert = 0;\r\n" + 
				    "  #if defined(ESP8266) \r\n" + 
				    "    wert = button_encoder.read();\r\n" + 
				    "  #elif defined(ESP32)\r\n" + 
				    "    wert = button_encoder.getCount();\r\n" + 
				    "  #endif\r\n" + 
				    "  return wert;\r\n" + 
				    "}\r\n" + 
				  "";
		translator.addDefinitionCommand(EncDef);
	    EncDef ="#if defined(ESP32) \n "
			   		+  "    ESP32Encoder::useInternalWeakPullResistors = puType::up;\n"
			   		+  "    button_encoder.attachHalfQuad(GPIO_ROTARY_B, GPIO_ROTARY_A); \n "
			   		+  "    pinMode(GPIO_ROTARY_B,INPUT_PULLUP); \n "
			   		+  "    pinMode(GPIO_ROTARY_A,INPUT_PULLUP); \n "
			   		+  "#endif \n";
	    translator.addSetupCommand(EncDef);

		ret = "encoderRead()";
		

		return codePrefix + ret + codeSuffix;
  }
}