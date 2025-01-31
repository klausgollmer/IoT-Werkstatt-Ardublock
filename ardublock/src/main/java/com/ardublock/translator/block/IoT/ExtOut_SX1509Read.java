package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtOut_SX1509Read extends TranslatorBlock
{

  public ExtOut_SX1509Read (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }


	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    String pin = translatorBlock.toCode();
		
		 // Header hinzuf�gen
	    // now in init translator.addHeaderFile("Wire.h");
	    translator.addHeaderFile("SparkFunSX1509.h");
	   
	    translator.addDefinitionCommand("// SparkFun SX1509 I/O Expander https://github.com/sparkfun/SparkFun_SX1509_Arduino_Library\r\n");
	   	translator.addDefinitionCommand("SX1509 io;");

	    
	    // Setupdeklaration
	    // I2C-initialisieren
	    //translator.addSetupCommand("Serial.begin(115200);");
	    
	    // now in init : translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
	    // now in init : translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) IOTW_PRINTLN(F(\"Something wrong with I2C\")); \n  #endif \n");
	 
	   	   
	    String Setup = "if (!io.begin(0x3E)) { IOTW_PRINTLN(\"Failed to communicate SX1509 Expander\");while (1) {delay(1);};}\n";
	    translator.addSetupCommand(Setup);
	    
	    Setup = "io.pinMode("+pin+", INPUT_PULLUP); // Port-Expander Input Pin\n";
	    translator.addSetupCommand(Setup);
	   		    
		
		String ret = "io.digitalRead("+ pin +")";
		

		return codePrefix + ret + codeSuffix;
	}
}