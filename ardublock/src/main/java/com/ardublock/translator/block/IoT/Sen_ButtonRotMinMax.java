package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Sen_ButtonRotMinMax extends TranslatorBlock
{

  public Sen_ButtonRotMinMax (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
 
		  String EncDef =   "#if defined(ESP8266) \n"
			      + "// Autor Paul Stoffregen, http://www.pjrc.com/teensy/td_libs_Encoder.html\n"
			      + " #include <Encoder.h> \n"
			      + "#elif defined(ESP32) \n"
			      + "// Autor: Kevin Harrington, https://www.arduino.cc/reference/en/libraries/esp32encoder\n"
			      + " #include <ESP32Encoder.h>;\n"
			      + "#endif\n"
			      + "// encoder max range\n"
			      + "int encoder_counter_rot_min = -100;\n"
			      + "int encoder_counter_rot_max = 100;\n"
			      + "";
	     translator.addDefinitionCommand(EncDef);
   	     translator.addDefinitionCommand(EncDef);
		  
		 ret = "// Configure upper/lower bounds encoder\r\n" +
		    		    "encoder_counter_rot_min="+min+";\n"+
		    		    "encoder_counter_rot_max="+max+";\n";
		 return codePrefix + ret + codeSuffix;
	}
}