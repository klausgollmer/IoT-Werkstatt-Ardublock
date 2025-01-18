package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Sen_BME280Get extends TranslatorBlock
{

  public Sen_BME280Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
    //translator.addSetupCommand("Serial.begin(115200);");
    translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
 
    translator.addSetupCommand("boschBME280.settings.runMode = 3; // Normal Mode\n"
    		+ "boschBME280.settings.tempOverSample  = 4; \n"
    		+ "boschBME280.settings.pressOverSample = 4;\n"
    		+ "boschBME280.settings.humidOverSample = 4;\n"
    		+ "boschBME280_ready = boschBME280.begin();");

    translator.addSetupCommand("if (boschBME280_ready == 0) {"+
    		"while(1) {Serial.println(F(\"BME280 nicht vorhanden, ggf. Puzzleteile tauschen\")); delay(500);}\n"+
    		"}\n");

    
    // Deklarationen hinzuf�gen
    	
   	translator.addDefinitionCommand("// Marshall Tylor@sparkfun  https://github.com/sparkfun/SparkFun_BME280_Arduino_Library");
	translator.addDefinitionCommand("BME280 boschBME280; // Objekt Bosch Umweltsensor");
	translator.addDefinitionCommand("int boschBME280_ready = 0; // Objekt Bosch Umweltsensor");
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();

    // Code von der Mainfunktion
	ret = code;
	
   
    return codePrefix + ret + codeSuffix;
  }
}