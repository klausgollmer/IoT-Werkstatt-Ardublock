package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoT7Seg extends TranslatorBlock
{

  public IoT7Seg (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    
    // Header hinzuf�gen
	translator.addHeaderFile("Wire.h");
	translator.addHeaderFile("Adafruit_GFX.h");
    translator.addHeaderFile("Adafruit_LEDBackpack.h");
    // Deklarationen hinzuf�gen
    translator.addDefinitionCommand("// Adafruit Feather 7 Segment https://learn.adafruit.com/adafruit-7-segment-led-featherwings/overview");
 	translator.addDefinitionCommand("Adafruit_7segment matrix7Seg = Adafruit_7segment(); // 7Segment Feather");
 	    
    // Setupdeklaration
	translator.addSetupCommand("Serial.begin(115200);");
    // I2C-initialisieren
	translator.addSetupCommand("Wire.begin(); // ---- Initialisiere den I2C-Bus \n");
	translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
	 
    
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