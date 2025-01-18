package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_SCD4xGet extends TranslatorBlock
{

  public ExtSen_SCD4xGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
  
    // Header hinzuf�gen
    translator.addHeaderFile("SparkFun_SCD4x_Arduino_Library.h");
    translator.addHeaderFile("Wire.h");

    // Setupdeklaration
    // I2C-initialisieren

    String Dis="/* Sparkfun Sensirion SCD 40 \n"
			 + "Copyright (c) 2020 SparkFun Electronics\r\n"
			 + "MIT Disclaimer see https://github.com/sparkfun/SparkFun_SCD4x_Arduino_Library \n"
			 + "*/\n";
 	translator.addDefinitionCommand(Dis);
    
    
	translator.addDefinitionCommand("//By: SparkFun Electronics, https://github.com/sparkfun/SparkFun_SCD4x_Arduino_Library\n");
	translator.addDefinitionCommand("SCD4x airSensorSCD40; // Objekt SDC40 Umweltsensor");
    
    translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
    //translator.setSCD30Program(true);
    
    String Setup = "if (airSensorSCD40.begin() == false) {Serial.println(\"The SCD40 did not respond. Please check wiring.\"); while(1) {yield(); delay(1);} }\n";
    translator.addSetupCommand(Setup);
    
    //translator.addSetupCommand("airSensorSCD40.useStaleData(true);            // do not wait for fresh data\n");

    //translator.addSetupCommand("airSensorSCD40.setAutoSelfCalibration(false); // Sensirion no auto calibration\n");  
    //translator.addSetupCommand("airSensorSCD40.setMeasurementInterval(10);     // CO2-Messung alle 10 s\n");  
    // Deklarationen hinzuf�gen
    
   	String read = "float readSensirionSCD40(int n){\r\n"
   			+ "  float value = NAN;\r\n"
   			+ "  switch(n) {\r\n"
   			+ "    case 1: value = airSensorSCD40.getCO2();\r\n"
   			+ "    break;\r\n"
   			+ "    case 2: value = airSensorSCD40.getTemperature();\r\n"
   			+ "    break;\r\n"
   			+ "    case 3: value = airSensorSCD40.getHumidity();\r\n"
   			+ "    break;\r\n"
   			+ "  }\r\n"
   			+ "  return value;\r\n"
   			+ "}";
   	translator.addDefinitionCommand(read);
    
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
    
    
          
    // Code von der Mainfunktion
	ret = "readSensirionSCD40("+code+")";
	return codePrefix + ret + codeSuffix;
  }
}