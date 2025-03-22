package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Audio_SAM_Conf  extends TranslatorBlock {

	public Audio_SAM_Conf (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		//translator.addHeaderFile("Adafruit_GFX.h");
		translator.addHeaderFile("ESP8266SAM.h");
		translator.addHeaderFile("AudioOutputI2S.h");
		
		String Dis="/* Speech synthesis for ESP8266 using S.A.M. port, Earle F. Philhower, III  \n"
				 + "   Repository: https://github.com/earlephilhower/ESP8266SAM\n "
				 + "   ESP8266Audio, GPL-3.0 Licence https://github.com/earlephilhower/ESP8266Audio/?tab=GPL-3.0-1-ov-file#readme \n"
				 + "   (c) Earle F. Philhower, III */\n";
	   	translator.addDefinitionCommand(Dis);
	    	   	
	   	String Def="AudioOutputI2S *DAC_out = NULL;\r\n";
		translator.addDefinitionCommand(Def);
		
	   	Def="ESP8266SAM *sam_speech = new ESP8266SAM;\r\n";
		translator.addDefinitionCommand(Def);

	   	String Setup ="DAC_out = new AudioOutputI2S(0,AudioOutputI2S::INTERNAL_DAC);\r\n"
	   		    	+ "DAC_out->begin();\r\n";
	    translator.addSetupCommand(Setup);
		
 	   	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String speed = translatorBlock.toCode();
		
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String pitch = translatorBlock.toCode();
		
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
		String mouth = translatorBlock.toCode();
		
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
		String throat = translatorBlock.toCode();
		
		String ret = "sam_speech->SetSpeed("+speed+");\n" +
		             "sam_speech->SetPitch("+pitch+");\n" + 
		             "sam_speech->SetMouth("+mouth+");\n" + 
		             "sam_speech->SetThroat("+throat+");\n"; 
		return codePrefix + ret + codeSuffix;
		
	}
}

