package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Audio_WebRadioStreamTitle  extends TranslatorBlock {

	public Audio_WebRadioStreamTitle (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		
		translator.addHeaderFile("AudioFileSourceICYStream.h");
		translator.addHeaderFile("AudioFileSourceBuffer.h");
		translator.addHeaderFile("AudioGeneratorMP3.h");
		translator.addHeaderFile("AudioOutputI2S.h");
		translator.addHeaderFile("esp_wifi.h");
		
		String Dis="/* ESP8266Audio "
				 + "   GPL-3.0 Licence https://github.com/earlephilhower/ESP8266Audio/?tab=GPL-3.0-1-ov-file#readme \n"
				 + "   (c) Earle F. Philhower, III */\n";
	   	translator.addDefinitionCommand(Dis);
	    	   	
	   	String Def="AudioOutputI2S *DAC_out = NULL;\r\n";
		translator.addDefinitionCommand(Def);
	
	   	Def="AudioGeneratorMP3 *mp3;\r\n"
	   	  + "AudioFileSourceICYStream *file;\r\n"
	   	  + "AudioFileSourceBuffer *buff;\n"
	   	  + "boolean WebRadioInit = false;\n"
	   	  + "String WebRadioStreamTitle=\"\";\n";
		translator.addDefinitionCommand(Def);

		Def =     "String GetWebRadioStreamTitle() {\n"
				+ "return WebRadioStreamTitle;\n"
				+ "}\n";
		translator.addDefinitionCommand(Def);
		return codePrefix + "GetWebRadioStreamTitle()" + codeSuffix;
		
	}
}

