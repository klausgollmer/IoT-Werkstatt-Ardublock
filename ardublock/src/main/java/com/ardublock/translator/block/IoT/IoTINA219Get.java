package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTINA219Get extends TranslatorBlock
{

  public IoTINA219Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
    
    
    // Header hinzuf�gen
    translator.addHeaderFile("Wire.h");
    translator.addHeaderFile("Adafruit_INA219.h");
    
    
    // Setupdeklaration
    // I2C-initialisieren
    //translator.addSetupCommand("Serial.begin(115200);");
    translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
 
    translator.addSetupCommand("if (!ina219.begin()) Serial.println(\"no INA219\");\n");

    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String Sensor = translatorBlock.toCode();
    
    translatorBlock = this.getTranslatorBlockAtSocket(1);
    if (translatorBlock!=null) {
    	String Range = translatorBlock.toCode();
    	Range=Range.substring(1, Range.length() - 1);
	    translator.addSetupCommand("ina219.setCalibration_"+Range+"();\r\n"); 
    }	    	

    // Deklarationen hinzuf�gen
    String Dis="/* Adafruit ina219 Lib\n"
  			 + "Copyright (c) 2012 Adafruit Industries\r\n"
  			 + "BSD License, Disclaimer see github */\n";
  	translator.addDefinitionCommand(Dis);
  	translator.addDefinitionCommand("//https://learn.adafruit.com/adafruit-ina219-current-sensor-breakout Copyright (c), Adafruit Industries");
    translator.addDefinitionCommand("Adafruit_INA219 ina219;\n");

    String Read = "float readINA219(int chan) { // INA219 Load- and current Sensor\r\n" + 
    	    		"  float wert = NAN;\r\n" + 
    	    		"  switch (chan) {\r\n" + 
    	    		"  case 1: // current\r\n" + 
    	    		"    wert = ina219.getCurrent_mA();\r\n" + 
    	    		"    break;\r\n" + 
    	    		"  case 2: // voltage\r\n" + 
    	    		"    wert = ina219.getBusVoltage_V()+ ina219.getShuntVoltage_mV()/100.;\r\n" + 
    	    		"    break;\r\n" + 
    	    		"  case 3: // power\r\n" + 
    	    		"    wert = ina219.getPower_mW();     \r\n" + 
    	    		"    break;\r\n" + 
    	    		"  }\r\n" + 
    	    		"  return wert;\r\n" + 
    	    		"}";

    translator.addDefinitionCommand(Read);
			     
	
    // Code von der Mainfunktion
	ret = "readINA219(" + Sensor + ")";
   
    return codePrefix + ret + codeSuffix;
  }
}

