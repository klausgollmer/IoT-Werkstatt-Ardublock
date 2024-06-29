package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTADCI2CRead extends TranslatorBlock
{

  public IoTADCI2CRead (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }


	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    String adr = translatorBlock.toCode();
		
		 // Header hinzuf�gen
	    translator.addHeaderFile("Wire.h");
	    
	    String Setup = "// **************  Grove I2C-ADC Expander  *********************************\n"+
	    		"void I2C_adc_init(int adr) { \n"+
	            "   Wire.beginTransmission(adr);   // Initialisiere Wire - Transfer\n"+
	            "   Wire.write(0x02);              // Configuration Register\n"+
	            "   Wire.write(0x20);              // 27 ksps \n"+
	            "   Wire.endTransmission();  \n"+
	            " }\n"+
	            "\n"+
	            "int I2C_adc_read(int adr)  {      // lese aktuellen ADC-Wert vom I2C\n"+
	    	    "  int getData = 0;\n"+
	            "  Wire.beginTransmission(adr);\n"+      
	            "  Wire.write(0x00);               // hole register 0 ADC-result\n"+
	            "  Wire.endTransmission();\n"+
	            "  Wire.requestFrom(adr, 2);       // 12 Bit, request 2byte from device \n"+
	            "  delay(1);\n"+
	            "  if (Wire.available()<=2) {\n"+
	            "    getData = (Wire.read()&0x0f)<<8;\n"+
	            "    getData |= Wire.read();\n"+
	            "  }\n"+
	            "  return getData;\n"+
	            "}\n";
 	    translator.addDefinitionCommand(Setup);

	   	// Setupdeklaration
	    // I2C-initialisieren
	    translator.addSetupCommand("Serial.begin(115200);");
	    
	    Setup = "Wire.begin(); // ---- Initialisiere den I2C-Bus \n" 
	                 + "#if defined(ESP8266) \n      if (Wire.status() != I2C_OK) Serial.println(\"Something wrong with I2C\"); \n #endif\n";
	    translator.addSetupCommand(Setup);

	    translator.addSetupCommand("I2C_adc_init("+adr+");");
      		    
		
		String ret = "I2C_adc_read("+ adr +")";
		

		return codePrefix + ret + codeSuffix;
	}
}