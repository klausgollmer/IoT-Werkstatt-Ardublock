package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtDisp_OLED_Display  extends TranslatorBlock {

	public ExtDisp_OLED_Display (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
	
	   	String Def="Adafruit_SH1107 myOLEDdisplay = Adafruit_SH1107(IOTW_SCREEN_HEIGHT, IOTW_SCREEN_WIDTH, &Wire);";
		translator.addDefinitionCommand(Def);
		Def="GFXcanvas1 canvas(IOTW_SCREEN_WIDTH, IOTW_SCREEN_HEIGHT);\n";
		translator.addDefinitionCommand(Def);
		
		String Setup ="if (!(myOLEDdisplay.begin(0x3C, true))) { // OLED Display Address 0x3C default\r\n"
   		  		+ "  IOTW_PRINTLN(F(\"\\nno OLED detected\"));\r\n"
   		  		+ "} \r\n";
  	    translator.addSetupCommand(Setup);
  	    Setup = "IoT_WerkstattPreventDiplayClear(); // disable Einbrennschutz für Logo \n";
        translator.addSetupCommand(Setup);
	
		// I2C-initialisieren
		//// now in init : translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
		//// now in init : translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) IOTW_PRINTLN(F(\"Something wrong with I2C\")); \n  #endif \n");
		
		
		 String ret="// Display Canvas \n"	+ 
	            "myOLEDdisplay.drawBitmap(0,0, canvas.getBuffer(), IOTW_SCREEN_WIDTH, IOTW_SCREEN_HEIGHT, SH110X_WHITE, SH110X_BLACK);\n" + 
			    "myOLEDdisplay.display();\n"; 
		return codePrefix + ret + codeSuffix;
	}
}

