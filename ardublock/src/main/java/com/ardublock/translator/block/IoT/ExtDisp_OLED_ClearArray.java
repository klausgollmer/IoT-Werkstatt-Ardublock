package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtDisp_OLED_ClearArray  extends TranslatorBlock {

	public ExtDisp_OLED_ClearArray (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		// now in init translator.addHeaderFile("Wire.h");
		translator.addHeaderFile("Adafruit_GFX.h");
		translator.addHeaderFile("Adafruit_SH110X.h");
		
		String Dis="/* Adafruit SH110x OLED  / GFX \n"
				 + "Copyright (c) Adafruit Industries\r\n"
				 + "BSD, Disclaimer see https://github.com/adafruit/Adafruit_SH110x?tab=License-1-ov-file#readme \n"
				 + "https://github.com/adafruit/Adafruit-GFX-Library?tab=License-1-ov-file#readme  \n"
				 + "*/\n";
	   	translator.addDefinitionCommand(Dis);
	   
		String Def="Adafruit_SH1107 myOLEDdisplay = Adafruit_SH1107(IOTW_SCREEN_HEIGHT, IOTW_SCREEN_WIDTH, &Wire);";
		translator.addDefinitionCommand(Def);

	   	Def="GFXcanvas1 canvas(IOTW_SCREEN_WIDTH, IOTW_SCREEN_HEIGHT);\n";
		translator.addDefinitionCommand(Def);
		
		String ArrayStruct ="//--------------------------------  IoTDataArray for timeseries \n"
				+ "// Dimension IOTW_ARRAYLEN, only the first 15 elements were displayed charlieplex matrix \n"
				+ "#if defined(ESP32) && defined(IOTW_USE_DEEPSLEEP)\n"
				+ "  RTC_DATA_ATTR float   IoTArrayData[IOTW_ARRAYLEN];\n"
				+ "  RTC_DATA_ATTR uint8_t IoTArrayDataIndex     = 0;\n"
				+ "  RTC_DATA_ATTR uint8_t IoTArrayDataInitDone  = 0;\n"
				+ "#else\n"
				+ "  float   IoTArrayData[IOTW_ARRAYLEN];\n"
				+ "  uint8_t IoTArrayDataIndex     = 0;\n"
				+ "  uint8_t IoTArrayDataInitDone  = 0;\n"
				+ "#endif\n"
				+ "";
		translator.addDefinitionCommand(ArrayStruct);

		
		
		/*
	   	String Setup = "if (myOLEDdisplay.begin(0x3C, true)) { // OLED Display Address 0x3C default\r\n"
	   			+ "	 myOLEDdisplay.setRotation(1);\r\n"
	   			+ "	 myOLEDdisplay.clearDisplay(); \r\n"
	   			+ "	 myOLEDdisplay.display();\r\n"
	   			+ "  canvas.setFont(&FreeMonoBold18pt7b);\n"
	   			+ "} else {\r\n"
	   			+ "  IOTW_PRINTLN(F(\"\\nno OLED detected\"));\r\n"
	   			+ "} \r\n";
	   			*/
		
	    String Setup = "IoT_WerkstattPreventDiplayClear(); // disable Einbrennschutz für Logo \n";
        translator.addSetupCommand(Setup);
	
		
		
	    
	    
	    		
		String ret  = "// Clear Array Timeseries OLED \n"
				+ "IoTArrayDataIndex=0;\n"
				+ "for (uint8_t i=0; i<IOTW_ARRAYLEN; i++) {\r\n"
				+ "  IoTArrayData[i] = NAN;\r\n"
				+ "}";
		return codePrefix + ret + codeSuffix;
		
	}
}

