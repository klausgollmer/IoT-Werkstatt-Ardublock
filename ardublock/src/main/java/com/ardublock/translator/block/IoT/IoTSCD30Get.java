package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTSCD30Get extends TranslatorBlock
{

  public IoTSCD30Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
  
    // Header hinzuf�gen
    translator.addHeaderFile("SparkFun_SCD30_Arduino_Library.h");
    translator.addHeaderFile("Wire.h");

    // Setupdeklaration
    // I2C-initialisieren
   
    translator.addSetupCommand("Wire.begin(GPIO_I2C_SDA, GPIO_I2C_SCL); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
    translator.setSCD30Program(true);
    
    String Setup = "if (airSensorSCD30.begin() == false) {Serial.println(\"The SCD30 did not respond. Please check wiring.\"); while(1) {yield(); delay(1);} }\n";
    translator.addSetupCommand(Setup);
    
    
    translator.addSetupCommand("airSensorSCD30.setAutoSelfCalibration(false); // Sensirion no auto calibration\n");  
    translator.addSetupCommand("airSensorSCD30.setMeasurementInterval(2);     // CO2-Messung alle 2 s\n");  
    translator.addSetupCommand("airSensorSCD30.useStaleData(true);            // do not wait for fresh data\n");
    // Deklarationen hinzuf�gen
    
   	translator.addDefinitionCommand("//Reading CO2, humidity and temperature from the SCD30 By: Nathan Seidle SparkFun Electronics \n");
   	translator.addDefinitionCommand("//https://github.com/sparkfun/SparkFun_SCD30_Arduino_Library\n");
   	
	translator.addDefinitionCommand("SCD30 airSensorSCD30; // Objekt SDC30 Umweltsensor");
	
	String read = "float readSensirionSCD30(int n){\r\n"
   			+ "  float value = NAN;\r\n"
   			+ "  switch(n) {\r\n"
   			+ "    case 1: value = airSensorSCD30.getCO2();\r\n"
   			+ "    break;\r\n"
   			+ "    case 2: value = airSensorSCD30.getTemperature();\r\n"
   			+ "    break;\r\n"
   			+ "    case 3: value = airSensorSCD30.getHumidity();\r\n"
   			+ "    break;\r\n"
   			+ "  }\r\n"
   			+ "  return value;\r\n"
   			+ "}";
   	translator.addDefinitionCommand(read);
    
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
    
    
          
    // Code von der Mainfunktion
	ret = "readSensirionSCD30("+code+")";
	return codePrefix + ret + codeSuffix;	
  }
}