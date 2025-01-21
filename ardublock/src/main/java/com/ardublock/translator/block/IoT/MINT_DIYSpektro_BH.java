package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class MINT_DIYSpektro_BH  extends TranslatorBlock {

	public MINT_DIYSpektro_BH (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
	    translator.addHeaderFile("BH1750.h");
     	translator.addHeaderFile("Adafruit_NeoPixel.h");
		translator.addHeaderFile("DIY_Spectrometer_BH.h");

		String Dis ="// Neopixel, (c) Adafruit, GNU LESSER GENERAL PUBLIC LICENSE\n"
				+   "// https://github.com/adafruit/Adafruit_NeoPixel?tab=LGPL-3.0-1-ov-file\n";
	    translator.addDefinitionCommand(Dis);
	
	    translator.addDefinitionCommand("Adafruit_NeoPixel pixels = Adafruit_NeoPixel(2,IOTW_GPIO_NEO,NEO_GRBW + NEO_KHZ800);");

	    // Deklarationen hinzuf�gen
	    String dec = " // BH1750 driver https://github.com/claws/BH1750 MIT-License Copyright (c) 2018 claws \n"
	            + "BH1750 LightSensor;\n";
	    
	    translator.addDefinitionCommand(dec);

		
		//translator.addSetupCommand("Serial.begin(115200);");
		// now in init : translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
	    // now in init : translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
		String Setup = "if (!LightSensor.begin() while(1) {Serial.println(\"missing BH1750\");delay(100);}\r\n";
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

