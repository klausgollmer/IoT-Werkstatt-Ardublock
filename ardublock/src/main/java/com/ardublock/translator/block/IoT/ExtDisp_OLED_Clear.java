package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtDisp_OLED_Clear  extends TranslatorBlock {

	public ExtDisp_OLED_Clear (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("Adafruit_SH110X.h");

		//translator.addHeaderFile("#if defined(ESP32)\n #include <rom/rtc.h> \n #endif\n");
		String Dis="/* Adafruit SH110x OLED  / GFX \n"
				 + "Copyright (c) Adafruit Industries\r\n"
				 + "BSD, Disclaimer see https://github.com/adafruit/Adafruit_SH110x?tab=License-1-ov-file#readme \n"
				 + "https://github.com/adafruit/Adafruit-GFX-Library?tab=License-1-ov-file#readme  \n"
				 + "*/\n";
	   	translator.addDefinitionCommand(Dis);
	   
		translator.addHeaderFile("Adafruit_SH110X.h");
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
	
		
	     String ret="// Clear Canvas \n"	+ 
	            "canvas.fillScreen(SH110X_BLACK);\n";
		return codePrefix + ret + codeSuffix;
	}
}

