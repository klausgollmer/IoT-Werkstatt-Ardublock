package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTWS2812  extends TranslatorBlock {

	public IoTWS2812 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	//@Override
		public String toCode() throws SocketNullException, SubroutineNotDeclaredException
		{
		translator.addHeaderFile("Adafruit_NeoPixel.h");
  	    
	    String setup=  "WSpixels.begin();//-------------- Initialisierung Neopixel\n"+
                       "WSpixels.show();  \n";

   	    translator.addSetupCommand(setup);
			
			
			String GPIO,Pixel_Nb,type,size;
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
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(4);
			size = translatorBlock.toCode();
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(5);
			GPIO = translatorBlock.toCode();
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(6);
			type = translatorBlock.toCode();
			type = type.substring(1, type.length()-1);

//	   	    translator.addDefinitionCommand("Adafruit_NeoPixel WSpixels = Adafruit_NeoPixel("+size+","+GPIO+","+type+");");
			String Dis="/* Adafruit WS2801 Lib\n"
					 + "https://github.com/adafruit/Adafruit-WS2801-Library"
					 + "Adafruit invests time and resources providing this open source code, please support Adafruit and open-source hardware by purchasing products from Adafruit!\n"
					 + "Written by Limor Fried/Ladyada for Adafruit Industries.\n"
					 + "BSD license, all text above must be included in any redistribution*/\n";
			translator.addDefinitionCommand(Dis);
			translator.addDefinitionCommand("Adafruit_NeoPixel WSpixels = Adafruit_NeoPixel(("+size+"<24)?"+size+":24,"+GPIO+","+type+");");

			
	
			String ret = "WSpixels.setPixelColor("+Pixel_Nb+",("+Red+"<32)?"+Red+":32,("+Green+"<32)?"+Green+":32,("+Blue+"<48)?"+Blue+":48);\n"
					+ "WSpixels.show();";

			
			return codePrefix + ret + codeSuffix;
				
		}
}
