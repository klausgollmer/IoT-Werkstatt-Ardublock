package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Sen_BSEC2Get extends TranslatorBlock
{

  public Sen_BSEC2Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
      
    // Header hinzuf�gen
    // now in init translator.addHeaderFile("Wire.h");
    translator.addHeaderFile("bsec2.h");
    translator.addHeaderFile("Ticker.h");

    
    // Setupdeklaration
    // I2C-initialisieren
    //translator.addSetupCommand("Serial.begin(115200);");
    // now in init : translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
 
   
    
    String Setup = "/* Desired subscription list of BSEC2 outputs */\r\n"
    		+ "    bsecSensor sensorList[] = {\r\n"
    		+ "            BSEC_OUTPUT_IAQ,\r\n"
    		+ "            BSEC_OUTPUT_RAW_TEMPERATURE,\r\n"
    		+ "            BSEC_OUTPUT_RAW_PRESSURE,\r\n"
    		+ "            BSEC_OUTPUT_RAW_HUMIDITY,\r\n"
    		+ "            BSEC_OUTPUT_RAW_GAS,\r\n"
    		+ "            BSEC_OUTPUT_STABILIZATION_STATUS,\r\n"
    		+ "            BSEC_OUTPUT_RUN_IN_STATUS,\r\n"
    		+ "            BSEC_OUTPUT_SENSOR_HEAT_COMPENSATED_TEMPERATURE,\r\n"
    		+ "            BSEC_OUTPUT_SENSOR_HEAT_COMPENSATED_HUMIDITY,\r\n"
    		+ "            BSEC_OUTPUT_STATIC_IAQ,\r\n"
    		+ "            BSEC_OUTPUT_CO2_EQUIVALENT,\r\n"
    		+ "            BSEC_OUTPUT_BREATH_VOC_EQUIVALENT,\r\n"
    		+ "            BSEC_OUTPUT_GAS_PERCENTAGE,\r\n"
    		+ "            BSEC_OUTPUT_COMPENSATED_GAS\r\n"
    		+ "    };\r\n"
    		+ " if (!envSensor.begin(BME68X_I2C_ADDR_LOW, Wire))\r\n"
    		+ "    {\r\n"
    		+ "        checkBsecStatus(envSensor);\r\n"
    		+ "    }\r\n"
    		+ "	envSensor.setTemperatureOffset(TEMP_OFFSET_LP);\n"
    		+ " if (!envSensor.updateSubscription(sensorList, ARRAY_LEN(sensorList), BSEC_SAMPLE_RATE_LP))\r\n"
    		+ "    {\r\n"
    		+ "        checkBsecStatus(envSensor);\r\n"
    		+ "    }"
    		+ "/* Whenever new data is available call the newDataCallback function */\r\n"
    		+ "    envSensor.attachCallback(newDataCallback);\r\n"
    		+ "\r\n"
    		+ "     #if (IOTW_DEBUG_LEVEL >1)\n IOTW_PRINTLN(\"BSEC library version \" + \\\r\n"
    		+ "            String(envSensor.version.major) + \".\" \\\r\n"
    		+ "            + String(envSensor.version.minor) + \".\" \\\r\n"
    		+ "            + String(envSensor.version.major_bugfix) + \".\" \\\r\n"
    		+ "            + String(envSensor.version.minor_bugfix));\n#endif\n";
    translator.addSetupCommand(Setup);
    translator.addSetupCommand(" IOTW_PRINTLN(F(\"start BSEC2, waiting for initialization ...\"));");
    translator.addSetupCommand(" while((isnan(iaqSensor.temperature))){iaqSensor_Housekeeping(); delay(10);}");
    translator.addSetupCommand(" IOTW_PRINT(F(\" ready\"));");
    translator.addSetupCommand(" Bsec_Ticker.attach_ms(3000, iaqSensor_Housekeeping);\r\n");
    
    
    // Deklarationen hinzuf�gen
    	
	String Disclaimer = "/* \n"
			            + "Bosch BSEC2 Lib, https://github.com/boschsensortec/Bosch-BSEC2-Library/tree/master\n"
		            	+ "The BSEC2 software is only available for download or use after accepting the software license agreement.\n"
			            + "By using this library, you have agreed to the terms of the license agreement: \n"
                        + "https://www.bosch-sensortec.com/media/boschsensortec/downloads/software/bme688_development_software/2023_04/license_terms_bme688_bme680_bsec.pdf */\n"
			            + "Bsec2 envSensor;     // Create an object of the class Bsec \n"
                        + "Ticker Bsec_Ticker; // schedule cyclic update via Ticker \n";
			 translator.addDefinitionCommand(Disclaimer);
   	
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
		+ "};\r\n"
		+ "\r\n"
		+ "envData_t iaqSensor;\n"
		+ "";	
	
	translator.addDefinitionCommand(Helper);

	
	Helper = "void newDataCallback(const bme68xData data, const bsecOutputs outputs, Bsec2 bsec)\r\n"
			+ "{\r\n"
			+ "  if (!outputs.nOutputs)\r\n"
			+ "  {\r\n"
			+ "    return;\r\n"
			+ "  }\r\n"
			+ "\r\n"
			+ "  //IOTW_PRINTLN(\"BSEC outputs:\\n\\tTime stamp = \" + String((int) (outputs.output[0].time_stamp / INT64_C(1000000))));\r\n"
			+ "  for (uint8_t i = 0; i < outputs.nOutputs; i++)\r\n"
			+ "  {\r\n"
			+ "    const bsecData output  = outputs.output[i];\r\n"
			+ "    switch (output.sensor_id)\r\n"
			+ "    {\r\n"
			+ "    case BSEC_OUTPUT_IAQ:\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tIAQ = \" + String(output.signal));\r\n #endif\n"
			+ "      iaqSensor.iaq=output.signal;\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tIAQ accuracy = \" + String((int) output.accuracy));\r\n #endif\n"
			+ "      iaqSensor.iaqAccuracy = output.accuracy;\r\n"
			+ "      break;\r\n"
			+ "    case BSEC_OUTPUT_RAW_TEMPERATURE:\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tTemperature = \" + String(output.signal));\r\n #endif\n"
			+ "      iaqSensor.rawTemperature = output.signal;\r\n"
			+ "      break;\r\n"
			+ "    case BSEC_OUTPUT_RAW_PRESSURE:\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tPressure = \" + String(output.signal));\r\n #endif\n"
			+ "      iaqSensor.pressure = output.signal*100.;\r\n"
			+ "      break;\r\n"
			+ "    case BSEC_OUTPUT_RAW_HUMIDITY:\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tHumidity = \" + String(output.signal));\r\n #endif\n"
			+ "      iaqSensor.rawHumidity = output.signal;\r\n"
			+ "      break;\r\n"
			+ "    case BSEC_OUTPUT_RAW_GAS:\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tGas resistance = \" + String(output.signal));\r\n #endif\n"
			+ "      iaqSensor.gasResistance=output.signal;\r\n"
			+ "      break;\r\n"
			+ "    case BSEC_OUTPUT_STABILIZATION_STATUS:\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tStabilization status = \" + String(output.signal));\r\n #endif\n"
			+ "      iaqSensor.stabilizationStatus = output.signal;\r\n"
			+ "      break;\r\n"
			+ "    case BSEC_OUTPUT_RUN_IN_STATUS:\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tRun in status = \" + String(output.signal));\r\n #endif\n"
			+ "      iaqSensor.runInStatus = output.signal;\r\n"
			+ "      break;\r\n"
			+ "    case BSEC_OUTPUT_SENSOR_HEAT_COMPENSATED_TEMPERATURE:\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tCompensated temperature = \" + String(output.signal));\r\n#endif\n"
			+ "      iaqSensor.temperature = output.signal;\r\n"
			+ "      break;\r\n"
			+ "    case BSEC_OUTPUT_SENSOR_HEAT_COMPENSATED_HUMIDITY:\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tCompensated humidity = \" + String(output.signal));\r\n#endif\n"
			+ "      iaqSensor.humidity = output.signal;\r\n"
			+ "      break;\r\n"
			+ "    case BSEC_OUTPUT_STATIC_IAQ:\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tStatic IAQ = \" + String(output.signal));\r\n#endif\n"
			+ "      iaqSensor.staticIAQ = output.signal;\r\n"
			+ "      break;\r\n"
			+ "    case BSEC_OUTPUT_CO2_EQUIVALENT:\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tCO2 Equivalent = \" + String(output.signal));\r\n#endif\n"
			+ "      iaqSensor.co2Equivalent=output.signal;\r\n"
			+ "      break;\r\n"
			+ "    case BSEC_OUTPUT_BREATH_VOC_EQUIVALENT:\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tbVOC equivalent = \" + String(output.signal));\r\n#endif\n"
			+ "      iaqSensor.breathVocEquivalent = output.signal;\r\n"
			+ "      break;\r\n"
			+ "    case BSEC_OUTPUT_GAS_PERCENTAGE:\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tGas percentage = \" + String(output.signal));\r\n#endif\n"
			+ "      iaqSensor.gasPercentage = output.signal;\r\n"
			+ "      break;\r\n"
			+ "    case BSEC_OUTPUT_COMPENSATED_GAS:\r\n"
			+ "      #if (IOTW_DEBUG_LEVEL >2)\n IOTW_PRINTLN(\"\\tCompensated gas = \" + String(output.signal));\r\n#endif\n"
			+ "      iaqSensor.compensatedGas = output.signal;\r\n"
			+ "      break;\r\n"
			+ "    default:\r\n"
			+ "      break;\r\n"
			+ "    }\r\n"
			+ "  }\r\n"
			+ "}"
			+ "void checkBsecStatus(Bsec2 bsec)\r\n"
			+ "{\r\n"
			+ "    if (bsec.status < BSEC_OK)\r\n"
			+ "    {\r\n"
			+ "        IOTW_PRINTLN(\"BSEC error code : \" + String(bsec.status));\r\n"
			+ "        //errLeds(); /* Halt in case of failure */\r\n"
			+ "    }\r\n"
			+ "    else if (bsec.status > BSEC_OK)\r\n"
			+ "    {\r\n"
			+ "        IOTW_PRINTLN(\"BSEC warning code : \" + String(bsec.status));\r\n"
			+ "    }\r\n"
			+ "\r\n"
			+ "    if (bsec.sensor.status < BME68X_OK)\r\n"
			+ "    {\r\n"
			+ "        IOTW_PRINTLN(\"BME68X error code : \" + String(bsec.sensor.status));\r\n"
			+ "        //errLeds(); /* Halt in case of failure */\r\n"
			+ "    }\r\n"
			+ "    else if (bsec.sensor.status > BME68X_OK)\r\n"
			+ "    {\r\n"
			+ "        IOTW_PRINTLN(\"BME68X warning code : \" + String(bsec.sensor.status));\r\n"
			+ "    }\r\n"
			+ "}";
	
	translator.addDefinitionCommand(Helper);
	  
    Helper = "\n // Housekeeping: scheduled update using ticker-lib\n"
    		+ "void iaqSensor_Housekeeping(){  // get new data \r\n" + 
    		"   envSensor.run();\n" + 
    		"  }\r\n"; 
    translator.addDefinitionCommand(Helper);
  
	
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
          
    // Code von der Mainfunktion
	ret = code;
	
   
    return codePrefix + ret + codeSuffix;
  }
}