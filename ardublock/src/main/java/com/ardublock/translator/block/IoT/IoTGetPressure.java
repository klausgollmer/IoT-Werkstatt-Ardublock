package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTGetPressure extends TranslatorBlock
{

  public IoTGetPressure (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
   
    // Header hinzuf�gen
    translator.addHeaderFile("SparkFunBME280.h");
    translator.addHeaderFile("Wire.h");

    // Setupdeklaration
    // I2C-initialisieren
    translator.addSetupCommand("Wire.begin(GPIO_I2C_SDA, GPIO_I2C_SCL); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
 
    
    translator.addSetupCommand("boschBME280.settings.runMode = 3; // Normal Mode\n"
    		+ "boschBME280.settings.tempOverSample  = 4; \n"
    		+ "boschBME280.settings.pressOverSample = 4;\n"
    		+ "boschBME280.settings.humidOverSample = 4;\n"
    		+ "boschBME280.begin();");
    
    // Deklarationen hinzuf�gen
	translator.addDefinitionCommand("// Marshall Tylor@sparkfun  https://github.com/sparkfun/SparkFun_BME280_Arduino_Library");
	translator.addDefinitionCommand("BME280 boschBME280; // Objekt Bosch Umweltsensor");
	
          
    // Code von der Mainfunktion
	ret = "boschBME280.readFloatPressure()";
	
   
    return codePrefix + ret + codeSuffix;
  }
}