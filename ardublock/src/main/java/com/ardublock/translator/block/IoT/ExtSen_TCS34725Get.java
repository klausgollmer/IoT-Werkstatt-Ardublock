package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_TCS34725Get extends TranslatorBlock
{

  public ExtSen_TCS34725Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    
    // Header hinzuf�gen
    translator.addHeaderFile("Wire.h");
    translator.addHeaderFile("Adafruit_TCS34725.h");
    
    
    // Setupdeklaration
    // I2C-initialisieren
    //translator.addSetupCommand("Serial.begin(115200);");
    translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
 
    translator.addSetupCommand("if (!tcs.begin()) Serial.println(\"Kein TCS34725 RGB-Sensor gefunden\");\n");
    
    String Dis="/* Adafruit TCS34725 Color Sensor\n"
			 + "Copyright (c) 2012 Adafruit Industries\r\n"
			 + "BSD, Disclaimer see https://github.com/adafruit/Adafruit_TCS34725?tab=License-1-ov-file#readme \n"
			 + "*/\n";
  	translator.addDefinitionCommand(Dis);
    
    // Deklarationen hinzuf�gen
	translator.addDefinitionCommand("Adafruit_TCS34725 tcs = Adafruit_TCS34725(TCS34725_INTEGRATIONTIME_614MS, TCS34725_GAIN_1X);\r\n");

	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String Sensor = translatorBlock.toCode();
    
    String Read = "int readTCS34725(int chan) { // TCS RGB Sensor Einlesefunktion\r\n" + 
    		"  uint16_t r, g, b, c, colorTemp, lux;\r\n" + 
    		"  int wert = -1;\r\n" + 
    		"  tcs.getRawData(&r, &g, &b, &c);\r\n" + 
    		"  // colorTemp = tcs.calculateColorTemperature(r, g, b);\r\n" + 
    		"  //colorTemp = tcs.calculateColorTemperature_dn40(r, g, b, c);\r\n" + 
    		"  lux = tcs.calculateLux(r, g, b);\r\n" + 
    		"  \r\n" + 
    		"  switch (chan) {\r\n" + 
    		"  case 2: // Lux\r\n" + 
    		"    wert = lux;\r\n" + 
    		"    break;\r\n" + 
    		"  case 3: // red\r\n" + 
    		"    wert = r;\r\n" + 
    		"    break;\r\n" + 
    		"  case 4: // green\r\n" + 
    		"    wert = g;     \r\n" + 
    		"    break;\r\n" + 
    		"  case 5: // blue\r\n" + 
    		"    wert = b;\r\n" + 
    		"    break;\r\n" + 
    		"  }\r\n" + 
    		"  return wert;\r\n" + 
    		"}";

    translator.addDefinitionCommand(Read);
			     
	
    // Code von der Mainfunktion
	ret = "readTCS34725(" + Sensor + ")";
   
    return codePrefix + ret + codeSuffix;
  }
}


