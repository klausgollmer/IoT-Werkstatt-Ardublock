package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTBSECGet extends TranslatorBlock
{

  public IoTBSECGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
      
    // Header hinzuf�gen
    translator.addHeaderFile("bsec.h");
    translator.addHeaderFile("Wire.h");
    translator.addHeaderFile("Ticker.h");

    
    // Setupdeklaration
    // I2C-initialisieren
    translator.addSetupCommand("Serial.begin(115200);");
    translator.addSetupCommand("Wire.begin(); // ---- Initialisiere den I2C-Bus \n");
 
    
    String Setup = " iaqSensor.begin(BME68X_I2C_ADDR_LOW, Wire);\r\n" + 
    		"  String output = \"\\nBSEC library version \" + String(iaqSensor.version.major) + \".\" + String(iaqSensor.version.minor) + \".\" + String(iaqSensor.version.major_bugfix) + \".\" + String(iaqSensor.version.minor_bugfix);\r\n" + 
    		"  Serial.println(output);\r\n" + 
    		"  iaqSensor.setConfig(bsec_config_iaq);\r\n" + 
    		"  checkIaqSensorStatus();\r\n" + 
    		"\r\n" + 
    		"  bsec_virtual_sensor_t sensorList[10] = {\r\n" + 
    		"    BSEC_OUTPUT_RAW_TEMPERATURE,\r\n" + 
    		"    BSEC_OUTPUT_RAW_PRESSURE,\r\n" + 
    		"    BSEC_OUTPUT_RAW_HUMIDITY,\r\n" + 
    		"    BSEC_OUTPUT_RAW_GAS,\r\n" + 
    		"    BSEC_OUTPUT_IAQ,\r\n" + 
    		"    BSEC_OUTPUT_STATIC_IAQ,\r\n" + 
    		"    BSEC_OUTPUT_CO2_EQUIVALENT,\r\n" + 
    		"    BSEC_OUTPUT_BREATH_VOC_EQUIVALENT,\r\n" + 
    		"    BSEC_OUTPUT_SENSOR_HEAT_COMPENSATED_TEMPERATURE,\r\n" + 
    		"    BSEC_OUTPUT_SENSOR_HEAT_COMPENSATED_HUMIDITY,\r\n" + 
    		"  };\r\n" + 
    		"\r\n" + 
    		"  iaqSensor.updateSubscription(sensorList, 10, BSEC_SAMPLE_RATE_LP);\r\n" + 
    		"  checkIaqSensorStatus();"+
    		"  iaqSensor_Housekeeping();";
    translator.addSetupCommand(Setup);

    translator.addSetupCommand(" Bsec_Ticker.attach_ms(3000, iaqSensor_Housekeeping);\r\n");

    
    // Deklarationen hinzuf�gen
    	
	String Disclaimer = "/* \n"
			            + "Bosch BSEC Lib, https://github.com/BoschSensortec/BSEC-Arduino-library\n"
		            	+ "The BSEC software is only available for download or use after accepting the software license agreement.\n"
			            + "By using this library, you have agreed to the terms of the license agreement: \n"
                        + "https://ae-bst.resource.bosch.com/media/_tech/media/bsec/2017-07-17_ClickThrough_License_Terms_Environmentalib_SW_CLEAN.pdf */ \n"
			            + "Bsec iaqSensor;     // Create an object of the class Bsec \n"
                        + "Ticker Bsec_Ticker; // schedule cyclic update via Ticker \n"
			            + "const uint8_t bsec_config_iaq[] = {\r\n" + 
			              "#include \"config/generic_33v_3s_28d_2d_iaq_50_200/bsec_iaq.txt\"\r\n" + 
			              "};\r\n" + 
			            "";
   	translator.addDefinitionCommand(Disclaimer);
  	
   	
	String Helper = "// ------------------------   Helper functions Bosch Bsec - Lib \r\n" + 
			"void checkIaqSensorStatus(void)\r\n" + 
			"{ String output; \r\n" + 
			"  if (iaqSensor.bme68xStatus != BSEC_OK) {\r\n" + 
			"    if (iaqSensor.bme68xStatus < BSEC_OK) {\r\n" + 
			"      output = \"BSEC error code : \" + String(iaqSensor.bme68xStatus);\r\n" + 
			"      for (;;) {Serial.println(output);delay(500);} // Halt in case of failure \r\n" + 
			"    } else {\r\n" + 
			"      output = \"BSEC warning code : \" + String(iaqSensor.bme68xStatus);\r\n" + 
			"      Serial.println(output);\r\n" + 
			"    }\r\n" + 
			"  }\r\n" + 
			"\r\n" + 
			"  if (iaqSensor.bme68xStatus != BME68X_OK) {\r\n" + 
			"    if (iaqSensor.bme68xStatus < BME68X_OK) {\r\n" + 
			"      output = \"BME680 error code : \" + String(iaqSensor.bme68xStatus);\r\n" + 
			"      for (;;){Serial.println(output);delay(500);}  // Halt in case of failure \r\n" + 
			"    } else {\r\n" + 
			"      output = \"BME680 warning code : \" + String(iaqSensor.bme68xStatus);\r\n" + 
			"      Serial.println(output);\r\n" + 
			"    }\r\n" + 
			"  }\r\n" + 
			"}";
	
	translator.addDefinitionCommand(Helper);

    Helper = "\n // Housekeeping: scheduled update using ticker-lib\n"
    		+ "void iaqSensor_Housekeeping(){  // get new data \r\n" + 
    		"   iaqSensor.run();\n" + 
    		"  }\r\n"; 
    translator.addDefinitionCommand(Helper);
  
	
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
          
    // Code von der Mainfunktion
	ret = code;
	
   
    return codePrefix + ret + codeSuffix;
  }
}