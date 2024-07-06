package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTWS2812Ring_clock  extends TranslatorBlock {

	public IoTWS2812Ring_clock (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	//@Override
		public String toCode() throws SocketNullException, SubroutineNotDeclaredException
		{
		translator.addHeaderFile("Adafruit_NeoPixel.h");

		String Func="//--------- Neopixel Messanzeige (Gauge)\n" + 
				"void WSGauge(float val, float limit1, float limit2, float delta, int seg, int dir){\n" + 
				"  int bright = 32;\n" + 
				"  float current = 0;\n" + 
				"  int i;\n"+
				"  val = round(val/delta)*delta; // Runden der Anzeige auf Delta-Schritte \n"+
				"  for (int k=0;k<=(seg-1);k++) { // alle Pixel\n" + 
				"      current = (k+1)*delta;\n" +
				"      if (dir==1) i=k;else {i=seg-1-k; if (i == seg-1) i=0; else i=i+1;} // clockwise or opposite\n" +
				"      if ((val>=current) && (val < limit1)) // gruen\n" + 
				"        WSpixels.setPixelColor(i,0,bright,0);\n" + 
				"       else if ((val>=current) && (val <= limit2)) // gelb\n" + 
				"              WSpixels.setPixelColor(i,bright/2,bright/2,0);\n" + 
				"             else if ((val >= current) && (val > limit2)) // rot\n" + 
				"                    WSpixels.setPixelColor(i,bright,0,0);\n" + 
				"                   else\n" + 
				"                    WSpixels.setPixelColor(i,0,0,0);\n" + 
				"  }\n" + 
				"  WSpixels.show(); // Anzeige\n" + 
				"}\n" + 
				"";
   	      	    
   	    String setup=  "WSpixels.begin();//-------------- Initialisierung Neopixel\n"+
                       "WSpixels.show();  \n";

   	    translator.addSetupCommand(setup);
 		
			
			String value,GPIO,size,delta,limit1,limit2,type,dir;

			TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
			value= translatorBlock.toCode();
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
			limit1 = translatorBlock.toCode();
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
			limit2 = translatorBlock.toCode();
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
			delta = translatorBlock.toCode();
		    translatorBlock = this.getRequiredTranslatorBlockAtSocket(4);
			dir = translatorBlock.toCode();
			
			
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(5);
			size = translatorBlock.toCode();
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(6);
			GPIO = translatorBlock.toCode();
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(7);
			type = translatorBlock.toCode();
			type = type.substring(1, type.length()-1);
	   	    translator.addDefinitionCommand("Adafruit_NeoPixel WSpixels = Adafruit_NeoPixel(("+size+"<24)?"+size+":24,"+GPIO+","+type+");");

//	   	    translator.addDefinitionCommand("Adafruit_NeoPixel WSpixels = Adafruit_NeoPixel("+size+","+GPIO+","+type+");");
	  	    translator.addDefinitionCommand(Func);
	  	    
//	        String ret = "  WSGauge("+value+","+limit1+","+limit2+","+delta+","+size+");\n";		
	  	    
	        String ret = "  WSGauge("+value+","+limit1+","+limit2+","+delta+","+size+","+dir+");\n";		
			return codePrefix + ret + codeSuffix;
				
		}
}
