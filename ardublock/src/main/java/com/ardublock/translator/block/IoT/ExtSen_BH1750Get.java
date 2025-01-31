package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_BH1750Get extends TranslatorBlock
{

  public ExtSen_BH1750Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Header hinzuf�gen
    translator.addHeaderFile("BH1750.h");
    
    // Setupdeklaration
    //translator.addSetupCommand("Serial.begin(115200);");
    // I2C-initialisieren
    
    String Setup;
    // now in init : translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
    // now in init : translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) IOTW_PRINTLN(F(\"Something wrong with I2C\")); \n  #endif \n");
    // Deklarationen hinzuf�gen
    
	String Dis="/* Lib for BH1750 Light Sensor \n"
			 + "Copyright (c) 2018 Claws\r\n"
			 + "MIT License, for Disclaimer see end of file */\n";
    translator.addDefinitionCommand(Dis);
    
    String dec = "// BH1750 driver https://github.com/claws/BH1750 MIT-License Copyright (c) 2018 claws \n"
            + "BH1750 LightSensor;\n";
    translator.addDefinitionCommand(dec);
    
	Setup = "if (!LightSensor.begin()) while(1) {IOTW_PRINTLN(\"missing BH1750\");delay(100);}\r\n";

    translator.addSetupCommand(Setup);

   	      
    // Code von der Mainfunktion
	ret = "LightSensor.readLightLevel()";
	
   
    return codePrefix + ret + codeSuffix;
  }
}