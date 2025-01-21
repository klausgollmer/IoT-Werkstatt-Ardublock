package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_SCD30Toff extends TranslatorBlock
{

  public ExtSen_SCD30Toff (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
        
 
    // Header hinzuf�gen
    translator.addHeaderFile("SparkFun_SCD30_Arduino_Library.h");
    // now in init translator.addHeaderFile("Wire.h");

    // Setupdeklaration
    // I2C-initialisieren
   
    // now in init : translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
    // now in init : translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
    translator.setSCD30Program(true);;
    
    
    String Setup = "if (airSensorSCD30.begin() == false) {Serial.println(\"The SCD30 did not respond. Please check wiring.\"); while(1) {yield(); delay(1);} }\n";
    translator.addSetupCommand(Setup);
    
    
    // Deklarationen hinzuf�gen
    
   	translator.addDefinitionCommand("//Reading CO2, humidity and temperature from the SCD30 By: Nathan Seidle SparkFun Electronics \n");
   	translator.addDefinitionCommand("//https://github.com/sparkfun/SparkFun_SCD30_Arduino_Library\n");
	translator.addDefinitionCommand("SCD30 airSensorSCD30; // Objekt SDC30 Umweltsensor");
	//translator.addSetupCommand("Serial.begin(115200);");

	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
	
	
    String cmd = "// Forced Temperature Offset SCD 30\n"+
    		     "float T_Act=airSensorSCD30.getTemperature();// actual Temperature\n"+
         	     "float T_OffOld=airSensorSCD30.getTemperatureOffset();// old Offset\n"+
      		     "float T_OffNew="+code+";// new Offset\n"+
         	     "if (T_OffNew<0) T_OffNew=0; // only positive offset \n"+
      		     "Serial.print(\"Actual T=\");Serial.println(T_Act);\n"+
          		 "Serial.print(\"Old Offset=\");Serial.println(T_OffOld);\n"+
          		 "Serial.print(\"New Offset=\");Serial.println(T_OffNew);\n"+
        		 "Serial.print(\"Set SCD 30 Temperature offset, please wait 10 s ...\");\n"+
                 "airSensorSCD30.setTemperatureOffset(T_OffNew); // set offset \n"+
        		 "delay(10000);\n"+ 
         		 "Serial.println(\" done, valid after power on\");\n";

    return codePrefix + cmd + codeSuffix;
  }
}