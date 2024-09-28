package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTNeopixelColor  extends TranslatorBlock {

	public IoTNeopixelColor (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	//@Override
		public String toCode() throws SocketNullException, SubroutineNotDeclaredException
		{
			
    	String Dis ="// Neopixel, (c) Adafruit, GNU LESSER GENERAL PUBLIC LICENSE\n"
				+   "// https://github.com/adafruit/Adafruit_NeoPixel?tab=LGPL-3.0-1-ov-file\n";
	    translator.addDefinitionCommand(Dis);
		
		translator.addHeaderFile("Adafruit_NeoPixel.h");
   	    translator.addDefinitionCommand("Adafruit_NeoPixel pixels = Adafruit_NeoPixel(2,GPIO_NEO,NEO_GRBW + NEO_KHZ800);");
		
   	      	    
   	    String setup=  "pixels.begin();//-------------- Initialisierung Neopixel\n"
   	    		      +"delay(1);\n"
     	    		  +"pixels.show();\n"
   	    		      +"pixels.setPixelColor(0,0,0,0,0); // alle aus\n"
   	    		      +"pixels.setPixelColor(1,0,0,0,0);\n" 
   	    		      +"pixels.show();                 // und anzeigen\n"; 
   	    translator.addSetupCommand(setup);
		
			
			String Pixel_Nb;
			String Red;
			String Blue;
			String Green;
			TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
			Pixel_Nb = translatorBlock.toCode();
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
			Red = translatorBlock.toCode();
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
			Green = translatorBlock.toCode();
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
			Blue = translatorBlock.toCode();
			
	
			String ret = "pixels.setPixelColor("+Pixel_Nb+","+Red+"&0x3F,"+Green+"&0x3F,"+Blue+"&0x3F);\n"
					+ "pixels.show();";

			
			return codePrefix + ret + codeSuffix;
				
		}
}
