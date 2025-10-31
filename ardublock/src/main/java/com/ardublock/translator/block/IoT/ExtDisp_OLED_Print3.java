package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtDisp_OLED_Print3  extends TranslatorBlock {

	public ExtDisp_OLED_Print3 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		//translator.addHeaderFile("Adafruit_GFX.h");
		translator.addHeaderFile("Adafruit_SH110X.h");
		
		//translator.addHeaderFile("#if defined(ESP32)\n #include <rom/rtc.h> \n #endif\n");
		
		String Dis="/* Adafruit SH110x OLED  / GFX \n"
				 + "Copyright (c) Adafruit Industries\r\n"
				 + "BSD, Disclaimer see https://github.com/adafruit/Adafruit_SH110x?tab=License-1-ov-file#readme \n"
				 + "https://github.com/adafruit/Adafruit-GFX-Library?tab=License-1-ov-file#readme  \n"
				 + "*/\n";
	   	translator.addDefinitionCommand(Dis);
	    	   	
	   	String Def="Adafruit_SH1107 myOLEDdisplay = Adafruit_SH1107(IOTW_SCREEN_HEIGHT, IOTW_SCREEN_WIDTH, &Wire);";
		translator.addDefinitionCommand(Def);

	   	Def="GFXcanvas1 canvas(IOTW_SCREEN_WIDTH, IOTW_SCREEN_HEIGHT);\n";
		translator.addDefinitionCommand(Def);

		
		String Setup ="if (!(myOLEDdisplay.begin(0x3C, true))) { // OLED Display Address 0x3C default\r\n"
   		  		+ "  IOTW_PRINTLN(F(\"\\nno OLED detected\"));\r\n"
   		  		+ "} else delay(10);\r\n";
 	    translator.addSetupCommand(Setup);
 	    Setup = "#if defined(ESP32)\n IoT_WerkstattPreventDisplayClear();\n #endif\n // disable Einbrennschutz für Logo \n";
        translator.addSetupCommand(Setup);
	
 	    Setup =   " myOLEDdisplay.setRotation(1);\r\n"
 	    		+ " myOLEDdisplay.clearDisplay(); \r\n"
 	    		+ " myOLEDdisplay.display();\r\n";
 	    translator.addSetupCommand(Setup);
 	    
 	    
		String s1 = "\"\"";
		String s2 = "\"\"";
		String s3 = "\"\"";
	
	    String s ="";
		
		TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0);
		if (translatorBlock!=null) {
		  s1 = translatorBlock.toCode();
	    }	    	
		translatorBlock = this.getTranslatorBlockAtSocket(1);
		if (translatorBlock!=null) {
		  s2 = translatorBlock.toCode();
	    }	    	
		translatorBlock = this.getTranslatorBlockAtSocket(2);
		if (translatorBlock!=null) {
		  s3 = translatorBlock.toCode();
	    }	    	
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
		s = translatorBlock.toCode(); // size

		String fontlib="Fonts/FreeMono"+s+"7b.h";
		String font="&FreeMono"+s+"7b";
  	    translator.addHeaderFile(fontlib);
		
		
	 	String ret = "// OLED print 3 lines "
	 			+" // Clear Canvas \r\n"
	 			+ "canvas.fillScreen(SH110X_BLACK);\r\n"
	 			+ "\r\n"
	 			+ "canvas.setFont("+font+");\r\n"
	 			+ "\r\n"
	 			+ "// Print text to OLED Canvas 1. line \r\n"
	 			+ "canvas.setCursor(0,15);\r\n"
	 			+ "canvas.print("+s1+");\r\n"
	 			+ "  \r\n"
	 			+ "canvas.setCursor(0,38);\r\n"
	 			+ "canvas.print("+s2+");\r\n"
	 			+ "  \r\n"
	 			+ "canvas.setCursor(0,61);\r\n"
	 			+ "canvas.print("+s3+");"
	 			+ "// Display Canvas \r\n"
	 			+ "myOLEDdisplay.drawBitmap(0,0, canvas.getBuffer(), IOTW_SCREEN_WIDTH, IOTW_SCREEN_HEIGHT, SH110X_WHITE, SH110X_BLACK);\r\n"
	 			+ "myOLEDdisplay.display();\r\n";
	 	
		return codePrefix + ret + codeSuffix;
		
	}
}

