package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTOLED_Display  extends TranslatorBlock {

	public IoTOLED_Display (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

	//	translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("Adafruit_GFX.h");
		translator.addHeaderFile("Adafruit_SH110X.h");
		translator.addHeaderFile("#if defined(ESP32)\n #include <rom/rtc.h> \n #endif\n");
		String Dis="/* Adafruit SH110x OLED  / GFX \n"
				 + "Copyright (c) Adafruit Industries\r\n"
				 + "BSD, Disclaimer see https://github.com/adafruit/Adafruit_SH110x?tab=License-1-ov-file#readme \n"
				 + "https://github.com/adafruit/Adafruit-GFX-Library?tab=License-1-ov-file#readme  \n"
				 + "*/\n";
	   	translator.addDefinitionCommand(Dis);
	
	   	String Def="extern Adafruit_SH1107 myOLEDdisplay;"
				 + "GFXcanvas1 canvas(SCREEN_WIDTH, SCREEN_HEIGHT);"
				 + "#define LOGO_WAIT";
		translator.addDefinitionCommand(Def);

		// I2C-initialisieren
		translator.addSetupCommand("Wire.begin(GPIO_I2C_SDA, GPIO_I2C_SCL); // ---- Initialisiere den I2C-Bus \n");
		translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
		
		String Setup = "#ifndef BOARD_MAKEY \n initOLED(0);\n #endif\n";
		translator.addSetupCommand(Setup);

		
		
		
		 String ret="// Display Canvas \n"	+ 
	            "myOLEDdisplay.drawBitmap(0,0, canvas.getBuffer(), SCREEN_WIDTH, SCREEN_HEIGHT, SH110X_WHITE, SH110X_BLACK);\n" + 
			    "myOLEDdisplay.display();\n"; 
		return codePrefix + ret + codeSuffix;
	}
}

