package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtDisp_OLED_Font  extends TranslatorBlock {

	public ExtDisp_OLED_Font (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

	
		translator.addHeaderFile("Adafruit_SH110X.h");
		String Dis="/* Adafruit SH110x OLED  / GFX \n"
				 + "Copyright (c) Adafruit Industries\r\n"
				 + "BSD, Disclaimer see https://github.com/adafruit/Adafruit_SH110x?tab=License-1-ov-file#readme \n"
				 + "https://github.com/adafruit/Adafruit-GFX-Library?tab=License-1-ov-file#readme  \n"
				 + "*/\n";
	   	translator.addDefinitionCommand(Dis);
	   	String Def="Adafruit_SH1107 myOLEDdisplay = Adafruit_SH1107(IOTW_SCREEN_HEIGHT, IOTW_SCREEN_WIDTH, &Wire);\r\n";
		translator.addDefinitionCommand(Def);

	   	Def="GFXcanvas1 canvas(IOTW_SCREEN_WIDTH, IOTW_SCREEN_HEIGHT);\n";
		translator.addDefinitionCommand(Def);
		
		String Setup ="if (!(myOLEDdisplay.begin(0x3C, true))) { // OLED Display Address 0x3C default\r\n"
   		  		+ "  Serial.println(F(\"\\nno OLED detected\"));\r\n"
   		  		+ "} \r\n";
  	    translator.addSetupCommand(Setup);
	
		String f,t,s;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		f = translatorBlock.toCode();
		
		
		//f=f.substring(1, f.length() - 1);

		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		t = translatorBlock.toCode();
		//t=t.substring(1, t.length() - 1);
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
		s = translatorBlock.toCode();
		//s=s.substring(1, s.length() - 1);
		
		
		String fontlib="Fonts/Free"+f+t+s+"7b.h";
		String font="&Free"+f+t+s+"7b";
  	    translator.addHeaderFile(fontlib);
		
		String ret  = "canvas.setFont("+font+");\n";		
		return codePrefix + ret + codeSuffix;
		
	}
}

