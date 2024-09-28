package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTMHZ19Get extends TranslatorBlock
{

  public IoTMHZ19Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
  
    // Header hinzuf�gen
    translator.addHeaderFile("MHZ19.h");
    translator.addHeaderFile("SoftwareSerial.h");
 //   translator.addDefinitionCommand("SoftwareSerial MHZSerial(4, 5, false); // 4 -> TX, 5 -> RX\n");
    
    String Setup = "MHZSerial.begin(9600);         // MHZ19 CO2-Sensor 9600 Baud\n"+
                   "myMHZ19.begin(MHZSerial);\n" +
    		       "myMHZ19.autoCalibration(false);// keine Autokalibrierung\n";
    translator.addSetupCommand(Setup);
    
    String Dis = "// MHZ Gas Sensor, WifWaf, GNU LESSER GENERAL PUBLIC LICENSE\n";
   	translator.addDefinitionCommand(Dis);
   	translator.addDefinitionCommand("//Reading CO2, temperature from the MHZ19 By: Jonathan Dempsey\n");
   	translator.addDefinitionCommand("//https://github.com/WifWaf/MH-Z19\n");
   	
	translator.addDefinitionCommand("MHZ19 myMHZ19; // Objekt MHZ19 Umweltsensor");
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
    
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String pins = translatorBlock.toCode();
    pins = pins.substring(1, pins.length()-1);
    translator.addDefinitionCommand("SoftwareSerial MHZSerial("+pins+", false); // Library: https://github.com/plerup/espsoftwareserial/, Peter Lerup \n");

    
    // Code von der Mainfunktion
	ret = code;
	
   
    return codePrefix + ret + codeSuffix;
  }
}