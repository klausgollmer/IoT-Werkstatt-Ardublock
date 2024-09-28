package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTNeopixelWingColor  extends TranslatorBlock {

	public IoTNeopixelWingColor (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	//@Override
		public String toCode() throws SocketNullException, SubroutineNotDeclaredException
		{
			
		translator.addHeaderFile("Adafruit_NeoPixel.h");
		
	  	String Dis ="// Neopixel, (c) Adafruit, GNU LESSER GENERAL PUBLIC LICENSE\n"
				+   "// https://github.com/adafruit/Adafruit_NeoPixel?tab=LGPL-3.0-1-ov-file\n";
	    translator.addDefinitionCommand(Dis);

		String Cmd = "// -----------------------  NeoPixelWing-Farbansteuerung\n"
				+ "Adafruit_NeoPixel neoWing = Adafruit_NeoPixel(32,GPIO_NEOWING,NEO_GRB + NEO_KHZ800);\n"
			   	+"void neoWingColor(uint8_t r,uint8_t g,uint8_t b) {\n"
			   	+"  for(uint16_t i=0; i<neoWing.numPixels(); i++) {\n"
			   	+"    neoWing.setPixelColor(i, r&0x3F,g&0x3F,b&0x3F);\n"
			   	+"  }\n"
			   	+"  neoWing.show();\n"
			   	+"}\n";
		
   	    translator.addDefinitionCommand(Cmd);

  	    
   	    
   	    
   	    translator.addSetupCommand(" neoWing.begin();\n delay(1);\n neoWingColor(0,0,0);\n"); 
			
			String Red;
			String Blue;
			String Green;
			TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
			Red = translatorBlock.toCode();
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
			Green = translatorBlock.toCode();
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
			Blue = translatorBlock.toCode();
			
	
//			String ret = "neoWingColor("+Red+"&0x3F,"+Green+"&0x3F,"+Blue+"&0x3F);\n";
			String ret = "neoWingColor(("+Red+"<63)?"+Red+":63,("+Green+"<63)?"+Green+":63,("+Blue+"<63)?"+Blue+":63);\n";

			
			return codePrefix + ret + codeSuffix;
				
		}
}
