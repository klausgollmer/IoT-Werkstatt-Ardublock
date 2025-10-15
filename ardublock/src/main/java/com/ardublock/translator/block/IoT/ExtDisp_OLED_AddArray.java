package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtDisp_OLED_AddArray  extends TranslatorBlock {

	public ExtDisp_OLED_AddArray (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
		
		
		String Setup ="if (!(myOLEDdisplay.begin(0x3C, true))) { // OLED Display Address 0x3C default\r\n"
   		  		+ "  IOTW_PRINTLN(F(\"\\nno OLED detected\"));\r\n"
   		  		+ "} \r\n";
   	    translator.addSetupCommand(Setup);
   	    Setup = "IoT_WerkstattPreventDiplayClear(); // disable Einbrennschutz für Logo \n";
        translator.addSetupCommand(Setup);
		
	    Setup =   "myOLEDdisplay.setRotation(1);\n"
	    		+ "if (!IoTArrayDataInitDone) {\n"
	    		+ "  IoTArrayDataInitDone = 1;\n"
	    		+ "  for (uint8_t i=0; i<IOTW_ARRAYLEN; i++) {\n"
	    		+ "    IoTArrayData[i] = NAN;\n"
	    		+ "  }\n"
	    		+ "}";
        translator.addSetupCommand(Setup);
	
	    
	    String Display = "//------------------ OLED: add Values of IoTDataArray\n"
	    		+ "void OLEDDisplayIoTArrayData_Add(float actValue) {\n"
	    		+ "  if (!isnan(actValue)) { // add value\n"
	    		+ "    if (IoTArrayDataIndex  > (IOTW_ARRAYLEN - 1) ) { // shift left\n"
	    		+ "      for (uint8_t i = 1; i < IOTW_ARRAYLEN; i++) {\n"
	    		+ "        IoTArrayData[i-1] = IoTArrayData[i]; \n"
	    		+ "      } \n"
	    		+ "      IoTArrayDataIndex = IOTW_ARRAYLEN-1;\n"
	    		+ "    }\n"
	    		+ "    IoTArrayData[IoTArrayDataIndex] = actValue;\n"
	    		+ "    IoTArrayDataIndex++; \n"
	    		+ "  }\n"
	    		+ "}";
		translator.addDefinitionCommand(Display);
		
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String val = "NAN";
		if (translatorBlock != null) {
			val = translatorBlock.toCode();
		}
		
		String ret  = "OLEDDisplayIoTArrayData_Add(" + val + ");";
		return codePrefix + ret + codeSuffix;
		
	}
}

