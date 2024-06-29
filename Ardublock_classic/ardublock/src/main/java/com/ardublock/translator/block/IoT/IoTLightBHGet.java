package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTLightBHGet extends TranslatorBlock
{

  public IoTLightBHGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Header hinzuf�gen
    translator.addHeaderFile("BH1750.h");
    
    // Setupdeklaration
    translator.addSetupCommand("Serial.begin(115200);");
    // I2C-initialisieren
    
    String Setup;
    translator.addSetupCommand("Wire.begin(); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(\"Something wrong with I2C\"); \n  #endif \n");
    // Deklarationen hinzuf�gen
    String dec = " // BH1750 driver https://github.com/claws/BH1750 MIT-License Copyright (c) 2018 claws \n"
            + "BH1750 LightSensor;\n";
    translator.addDefinitionCommand(dec);

    
    
	Setup = "if (!LightSensor.begin(BH1750::ONE_TIME_HIGH_RES_MODE_2)) while(1) {Serial.println(\"missing BH1750\");delay(100);}\r\n";

    translator.addSetupCommand(Setup);

   	      
    // Code von der Mainfunktion
	ret = "LightSensor.readLightLevel(true)";
	
   
    return codePrefix + ret + codeSuffix;
  }
}