package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTBSEC2Debug extends TranslatorBlock
{

  public IoTBSEC2Debug (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
      
    // Header hinzuf�gen
    translator.addHeaderFile("Wire.h");
    translator.addHeaderFile("bsec2.h");
    translator.addHeaderFile("Ticker.h");

    
    // Setupdeklaration
    // I2C-initialisieren
    translator.addSetupCommand("Serial.begin(115200);");
    translator.addSetupCommand("Wire.begin(GPIO_I2C_SDA, GPIO_I2C_SCL); // ---- Initialisiere den I2C-Bus \n");
 
   
    
    
   	
	String Helper = "// ------------------------   Helper functions Bosch Bsec - Lib \r\n" + 
		"struct envData_t {\r\n"
		+ "  float humidity            = NAN;\r\n"
		+ "  float iaq                 = NAN;\r\n"
		+ "  float co2Equivalent       = NAN;\r\n"
		+ "  float gasResistance       = NAN;\r\n"
		+ "  float rawHumidity         = NAN;\r\n"
		+ "  float iaqAccuracy         = NAN;\r\n"
		+ "  float pressure            = NAN;\r\n"
		+ "  float temperature         = NAN;\r\n"
		+ "  float rawTemperature      = NAN;\r\n"
		+ "  float breathVocEquivalent = NAN;\r\n"
		+ "  float stabilizationStatus = NAN;\r\n"
		+ "  float compensatedGas      = NAN;\r\n"
		+ "  float staticIAQ           = NAN;\r\n"
		+ "  float gasPercentage       = NAN;\r\n"
		+ "  float runInStatus         = NAN;\r\n"
		+ "  int   debug               = 0;"
		+ "};\r\n"
		+ "\r\n"
		+ "envData_t iaqSensor;\n"
		+ "";	
	translator.addDefinitionCommand(Helper);
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
          
    // Code von der Mainfunktion
	ret = "iaqSensor.debug= " +code+"; // Bosch BSEC2 Debug\n";
   
    return codePrefix + ret + codeSuffix;
  }
}