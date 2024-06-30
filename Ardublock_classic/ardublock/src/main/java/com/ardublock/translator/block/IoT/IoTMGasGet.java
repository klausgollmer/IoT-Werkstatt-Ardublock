package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTMGasGet extends TranslatorBlock
{

  public IoTMGasGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
   
    // Header hinzuf�gen
    translator.addHeaderFile("MutichannelGasSensor.h");
    translator.addHeaderFile("Wire.h");
    translator.addSetupCommand("Serial.begin(115200);");
    // Setupdeklaration
    // I2C-initialisieren
    translator.addSetupCommand("Wire.begin(); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
 
    translator.addSetupCommand("multigas.begin(); // Grove Multigas, I2C Address 0x04\n"
    		+ "multigas.powerOn(); \n"
    		+ "multigas.getVersion();\n");
    
    // Deklarationen hinzuf�gen
    	
   	translator.addDefinitionCommand("// Jacky Zhang, qi.zhang@seeed.cc, https://github.com/Seeed-Studio/Mutichannel_Gas_Sensor");
   	translator.addDefinitionCommand("MutichannelGasSensor multigas; // Grove Muligas Sensor ");
   	
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
          
    // Code von der Mainfunktion
	ret = code;
	
   
    return codePrefix + ret + codeSuffix;
  }
}