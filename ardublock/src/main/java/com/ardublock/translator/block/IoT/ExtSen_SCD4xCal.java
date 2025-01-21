package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_SCD4xCal extends TranslatorBlock
{

  public ExtSen_SCD4xCal (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Header hinzuf�gen
    translator.addHeaderFile("SparkFun_SCD4x_Arduino_Library.h");
    // now in init translator.addHeaderFile("Wire.h");

    // Setupdeklaration
    // I2C-initialisieren

	translator.addDefinitionCommand("//By: SparkFun Electronics, https://github.com/sparkfun/SparkFun_SCD4x_Arduino_Library\n");
	translator.addDefinitionCommand("SCD4x airSensorSCD40; // Objekt SDC40 Umweltsensor");
	//translator.addSetupCommand("Serial.begin(115200);");
	  
    // now in init : translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
    // now in init : translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
    //translator.setSCD30Program(true);
    
    String Setup = "if (airSensorSCD40.begin() == false) {Serial.println(\"The SCD40 did not respond. Please check wiring.\"); while(1) {yield(); delay(1);} }\n";
    translator.addSetupCommand(Setup);
    
    
    //translator.addSetupCommand("airSensorSCD40.setAutoSelfCalibration(false); // Sensirion no auto calibration\n");  
    //translator.addSetupCommand("airSensorSCD40.setMeasurementInterval(10);     // CO2-Messung alle 10 s\n");  
    // Deklarationen hinzuf�gen
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
	
	
    String cmd = "// Forced Calibration Sensirion SCD 30\n"+
          		 "Serial.print(\"Start SCD 40 calibration, please wait 10 s ...\");delay(10000);\n"+
                 "airSensorSCD40.performForcedRecalibration(400); // fresh air \n"+
  		         "Serial.println(\" done\");\n";

    return codePrefix + cmd + codeSuffix;
  }
}