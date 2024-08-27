package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTMHZ19Cal extends TranslatorBlock
{

  public IoTMHZ19Cal (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Header hinzuf�gen
    translator.addHeaderFile("MHZ19.h");
    translator.addHeaderFile("SoftwareSerial.h");
    
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String pins = translatorBlock.toCode();
    pins = pins.substring(1, pins.length()-1);
    translator.addDefinitionCommand("SoftwareSerial MHZSerial("+pins+", false); // Library: https://github.com/plerup/espsoftwareserial/, Peter Lerup \n");

    
    
    //translator.addDefinitionCommand("SoftwareSerial MHZSerial(4, 5, false); // 4 -> TX, 5 -> RX\n");
   	
    String Setup = "MHZSerial.begin(9600);         // MHZ19 CO2-Sensor 9600 Baud\n"+
                   "myMHZ19.begin(MHZSerial);\n" +
    		       "myMHZ19.autoCalibration(false);// keine Autokalibrierung\n";
    translator.addSetupCommand(Setup);
    
    
   	translator.addDefinitionCommand("//Reading CO2, temperature from the MHZ19 By: Jonathan Dempsey\n");
   	translator.addDefinitionCommand("//https://github.com/WifWaf/MH-Z19\n");
   	
	translator.addDefinitionCommand("MHZ19 myMHZ19; // Objekt MHZ19 Umweltsensor");
	
    String cmd = "// Forced Calibration MH-Z19\n"+
          		 "Serial.print(\"Start MH-Z19 calibration, please wait 10 s ...\");delay(10000);\n"+
                 "myMHZ19.setRange(2000);\n" + 
                 "myMHZ19.calibrateZero();\n" + 
                 "myMHZ19.setSpan(2000); \n" + 
                 "myMHZ19.autoCalibration(false);\n" + 
  		         "Serial.println(\" done\");\n";

    return codePrefix + cmd + codeSuffix;
  }
}