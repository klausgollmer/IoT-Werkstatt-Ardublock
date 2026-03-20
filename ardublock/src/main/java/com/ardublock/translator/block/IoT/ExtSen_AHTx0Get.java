package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_AHTx0Get extends TranslatorBlock
{

  public ExtSen_AHTx0Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
   
    // Header hinzuf�gen
    translator.addHeaderFile("Adafruit_AHTX0.h");

    translator.addSetupCommand("if (!aht.begin()) {\r\n"
    		+ "    IOTW_PRINTLN(F(\"Could not find AHT? Check wiring\"));\r\n"
    		+ "    while (1)\r\n"
    		+ "      delay(10);\r\n"
    		+ "  }\r\n"
    		+ "  IOTW_PRINTLN(F(\"AHT10 or AHT20 found\"));");

    // Deklarationen hinzuf�gen
    
   	translator.addDefinitionCommand("// Adafruit AHTX, BSD license, https://github.com/adafruit/Adafruit_ADS1X15");
	translator.addDefinitionCommand("Adafruit_AHTX0 aht; // AHT Umweltsensor");
	
	String Getter = "// AHT Umweltsensor \n"
	                +"float AHT_Get(int mode) {\r\n"
	                + "  sensors_event_t humidity, temp;\r\n"
	                + "  aht.getEvent(&humidity,\r\n"
	                + "               &temp); // populate temp and humidity objects with fresh data\r\n"
	                + "  if (mode == 1) return(temp.temperature); else return(humidity.relative_humidity);\r\n"
	                + "}\n";
	
	translator.addDefinitionCommand(Getter); 
	
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();

    // Code von der Mainfunktion
	ret = "AHT_Get("+code+")";
    return codePrefix + ret + codeSuffix;
  }
}