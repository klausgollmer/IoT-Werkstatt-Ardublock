package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTDIYSpektro  extends TranslatorBlock {

	public IoTDIYSpektro (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
		
		translator.setDIYSpectroProgram(true);
		
		String setup_typ;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    setup_typ = translatorBlock.toCode();
	    // Header hinzuf�gen
	    translator.addHeaderFile("Adafruit_Sensor.h");
	    translator.addHeaderFile("Adafruit_TSL2561_U.h");
     	translator.addHeaderFile("Adafruit_NeoPixel.h");
		translator.addHeaderFile("DIY_Spectrometer.h");

		String Dis ="// Neopixel, (c) Adafruit, GNU LESSER GENERAL PUBLIC LICENSE\n"
				+   "// https://github.com/adafruit/Adafruit_NeoPixel?tab=LGPL-3.0-1-ov-file\n";
	    translator.addDefinitionCommand(Dis);
	    translator.addDefinitionCommand("Adafruit_NeoPixel pixels = Adafruit_NeoPixel(2,IOTW_GPIO_NEO,NEO_GRBW + NEO_KHZ800);");

	    // Deklarationen hinzuf�gen
	    String dec = " // Adafruit TSL2561, https://github.com/adafruit/Adafruit_TSL2561, Written by Kevin (KTOWN) Townsend for Adafruit Industries \n"
	              + "Adafruit_TSL2561_Unified tsl = Adafruit_TSL2561_Unified(0x39, 12345);\n";

	    
	    translator.addDefinitionCommand(dec);

		
		//translator.addSetupCommand("Serial.begin(115200);");
		translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
	    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
		String Setup = "  if(!tsl.begin()) {\r\n" + 
				"    Serial.print(\"no TSL2561 detected ... Check your wiring or I2C ADDR!\");\r\n" + 
				"    while(1) {yield();};\r\n" + 
				"  }\r\n" + 
				"  /* Setup the sensor gain and integration time */\r\n" + 
				"  tsl.enableAutoRange(true);            /* Auto-gain ... switches automatically between 1x and 16x */\r\n" + 
				"  tsl.setIntegrationTime(TSL2561_INTEGRATIONTIME_101MS);  /* medium resolution and speed   */";
	    translator.addSetupCommand(Setup);

	       String setup=  "pixels.begin();//-------------- Initialisierung Neopixel\n"
	    		      +"delay(1);\n"
	 	    		  +"pixels.show();\n"
	    		      +"pixels.setPixelColor(0,0,0,0,0); // alle aus\n"
	    		      +"pixels.setPixelColor(1,0,0,0,0);\n" 
	    		      +"pixels.show();                 // und anzeigen\n"; 
	             translator.addSetupCommand(setup);
	 	
		
	     translator.addSetupCommand("//------------ DIY Spectrometer \n // https://www.haw-hamburg.de/fakultaeten-und-departments/ls/ls-forschung/projekte/projekte-aus-der-chemie/schuman/smartphone-photometer.html ");
	     translator.addSetupCommand(setup_typ);
		 return codePrefix + codeSuffix;
	 	}
}

