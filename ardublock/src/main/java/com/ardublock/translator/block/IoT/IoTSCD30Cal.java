package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTSCD30Cal extends TranslatorBlock
{

  public IoTSCD30Cal (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
   
    translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
    translator.setSCD30Program(true);;
    
    
    String Setup = "if (airSensorSCD30.begin() == false) {Serial.println(\"The SCD30 did not respond. Please check wiring.\"); while(1) {yield(); delay(1);} }\n";
    translator.addSetupCommand(Setup);
    
    
    // Deklarationen hinzuf�gen
    
   	translator.addDefinitionCommand("//Reading CO2, humidity and temperature from the SCD30 By: Nathan Seidle SparkFun Electronics \n");
   	translator.addDefinitionCommand("//https://github.com/sparkfun/SparkFun_SCD30_Arduino_Library\n");
	translator.addDefinitionCommand("SCD30 airSensorSCD30; // Objekt SDC30 Umweltsensor");
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
	
	
    String cmd = "// Forced Calibration Sensirion SCD 30\n"+
          		 "Serial.print(\"Start SCD 30 calibration, please wait 10 s ...\");delay(10000);\n"+
                 "airSensorSCD30.setAltitudeCompensation("+code+"); // Altitude in m ü NN \n"+
                 "airSensorSCD30.setForcedRecalibrationFactor(400); // fresh air \n"+
  		         "Serial.println(\" done\");\n";

    return codePrefix + cmd + codeSuffix;
  }
}