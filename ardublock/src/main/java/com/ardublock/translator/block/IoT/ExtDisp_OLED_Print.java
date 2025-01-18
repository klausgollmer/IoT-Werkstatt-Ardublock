package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtDisp_OLED_Print  extends TranslatorBlock {

	public ExtDisp_OLED_Print (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

	//	translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("Adafruit_GFX.h");
		translator.addHeaderFile("Adafruit_SH110X.h");
		translator.addHeaderFile("Fonts/FreeMonoBold18pt7b.h");
		//translator.addHeaderFile("#if defined(ESP32)\n #include <rom/rtc.h> \n #endif\n");
		
		String Dis="/* Adafruit SH110x OLED  / GFX \n"
				 + "Copyright (c) Adafruit Industries\r\n"
				 + "BSD, Disclaimer see https://github.com/adafruit/Adafruit_SH110x?tab=License-1-ov-file#readme \n"
				 + "https://github.com/adafruit/Adafruit-GFX-Library?tab=License-1-ov-file#readme  \n"
				 + "*/\n";
	   	translator.addDefinitionCommand(Dis);
	    
		
		
	   	String Def="extern Adafruit_SH1107 myOLEDdisplay;"
				 + "GFXcanvas1 canvas(SCREEN_WIDTH, SCREEN_HEIGHT);"
				 + "#define IOTW_LOGO_WAIT";
		translator.addDefinitionCommand(Def);
	
		
		String x = "";
		String y = "";
	
		
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String t = translatorBlock.toCode();
		
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		if (translatorBlock!=null) {
		  x = translatorBlock.toCode();
	    }	    	

		translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
		if (translatorBlock!=null) {
		  y = translatorBlock.toCode();
	    }	    	
	
		
	   	    
	    String Setup =   "canvas.setFont(&FreeMonoBold18pt7b);\n";
	    translator.addSetupCommand(Setup);

	    
	 	String ret="// Print text to OLED Canvas \n"	+ 
		          "canvas.setCursor("+x+","+y+");\n" + 
				  "canvas.print("+t+");\n";
		
		return codePrefix + ret + codeSuffix;
		
	}
}

