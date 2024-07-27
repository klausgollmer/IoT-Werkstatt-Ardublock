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
		
		String Def="//OLED https://www.adafruit.com/product/4650 Adafruit Author:ladyada kick\n" + 
		           "#define SCREEN_WIDTH 128 // OLED display width, in pixels\n"
		         + "#define SCREEN_HEIGHT 64 // OLED display height, in pixels\n"
				 + "Adafruit_SH1107 myOLEDdisplay = Adafruit_SH1107(SCREEN_HEIGHT, SCREEN_WIDTH, &Wire);\n" + 
				   "\n" + 
				   "GFXcanvas1 canvas(SCREEN_HEIGHT,SCREEN_WIDTH);";
		translator.addDefinitionCommand(Def);
		
		   // I2C-initialisieren
		translator.addSetupCommand("Serial.begin(115200);");
		translator.addSetupCommand("Wire.begin(GPIO_I2C_SDA, GPIO_I2C_SCL); // ---- Initialisiere den I2C-Bus \n");
		translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
		 
		   // OLED initialisieren	    
	    String Setup = "delay(250); // wait for the OLED to power up\n"
	    	          +"myOLEDdisplay.begin(0x3C, true); // Address 0x3C default\n";
	    translator.addSetupCommand(Setup);
	    
	     String ret="// Display Canvas \n"	+ 
	            "myOLEDdisplay.drawBitmap(0,0, canvas.getBuffer(), 64, 128, SH110X_WHITE, SH110X_BLACK);\n" + 
			    "myOLEDdisplay.display();\n"; 
		return codePrefix + ret + codeSuffix;
	}
}

