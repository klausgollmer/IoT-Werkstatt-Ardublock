package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtDisp_7Seg extends TranslatorBlock
{

  public ExtDisp_7Seg (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    
    // Header hinzuf�gen
	// now in init translator.addHeaderFile("Wire.h");
	translator.addHeaderFile("Adafruit_GFX.h");
    translator.addHeaderFile("Adafruit_LEDBackpack.h");
    // Deklarationen hinzuf�gen
    
    String Dis = "/*************************************************** \r\n"
    		+ "  This is a library for our I2C LED Backpacks\r\n"
    		+ "  Designed specifically to work with the Adafruit LED 7-Segment backpacks \r\n"
    		+ "  ----> http://www.adafruit.com/products/881\r\n"
    		+ "  ----> http://www.adafruit.com/products/880\r\n"
    		+ "  ----> http://www.adafruit.com/products/879\r\n"
    		+ "  ----> http://www.adafruit.com/products/878\r\n"
    		+ "  These displays use I2C to communicate, 2 pins are required to \r\n"
    		+ "  interface. There are multiple selectable I2C addresses. For backpacks\r\n"
    		+ "  with 2 Address Select pins: 0x70, 0x71, 0x72 or 0x73. For backpacks\r\n"
    		+ "  with 3 Address Select pins: 0x70 thru 0x77\r\n"
    		+ "  Adafruit invests time and resources providing this open source code, \r\n"
    		+ "  please support Adafruit and open-source hardware by purchasing \r\n"
    		+ "  products from Adafruit!\r\n"
    		+ "  Written by Limor Fried/Ladyada for Adafruit Industries.  \r\n"
    		+ "  BSD license, all text above must be included in any redistribution\r\n"
    		+ " ****************************************************/";
    translator.addDefinitionCommand(Dis);
    translator.addDefinitionCommand("// Adafruit Feather 7 Segment https://learn.adafruit.com/adafruit-7-segment-led-featherwings/overview");
 	translator.addDefinitionCommand("Adafruit_7segment matrix7Seg = Adafruit_7segment(); // 7Segment Feather");
 	    
    // Setupdeklaration
	//translator.addSetupCommand("Serial.begin(115200);");
    // I2C-initialisieren
	// now in init : translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
	// now in init : translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
	 
    
    String Setup = "matrix7Seg.begin(0x70); // ---- Initialisiere 7Segment Matrix\n"
    +       "matrix7Seg.clear();     // \n"
    +       "matrix7Seg.writeDisplay();\n";
    
    translator.addSetupCommand(Setup);
    	
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String Value = translatorBlock.toCode();

    //translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    //String index = translatorBlock.toCode();
          
    // Code von der Mainfunktion
	ret = "matrix7Seg.print("+ Value +");\n";
	ret = ret+"matrix7Seg.writeDisplay();\n";
    return codePrefix + ret + codeSuffix;
  }
}