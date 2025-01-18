package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtDisp_OLED_DispArray  extends TranslatorBlock {

	public ExtDisp_OLED_DispArray (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		translator.addHeaderFile("Wire.h");
		translator.addHeaderFile("Adafruit_GFX.h");
		translator.addHeaderFile("Adafruit_SH110X.h");
		
		String Dis="/* Adafruit SH110x OLED  / GFX \n"
				 + "Copyright (c) Adafruit Industries\r\n"
				 + "BSD, Disclaimer see https://github.com/adafruit/Adafruit_SH110x?tab=License-1-ov-file#readme \n"
				 + "https://github.com/adafruit/Adafruit-GFX-Library?tab=License-1-ov-file#readme  \n"
				 + "*/\n";
	   	translator.addDefinitionCommand(Dis);
	   	String Def="extern Adafruit_SH1107 myOLEDdisplay;"
				 + "GFXcanvas1 canvas(SCREEN_WIDTH, SCREEN_HEIGHT);"
				 + "#define IOTW_LOGO_WAIT";
		translator.addDefinitionCommand(Def);
	
		String ArrayStruct ="//--------------------------------  IoTDataArray for timeseries \n"
				+ "// Dimension IOTARRAYLEN, only the first 15 elements were displayed charlieplex matrix \n"
				+ "#define IOTARRAYLEN 64 \n"
				+ "#if defined(ESP32) && defined(IOTW_USE_DEEPSLEEP)\n"
				+ "  RTC_DATA_ATTR float   IoTArrayData[IOTARRAYLEN];\n"
				+ "  RTC_DATA_ATTR uint8_t IoTArrayDataIndex     = 0;\n"
				+ "  RTC_DATA_ATTR uint8_t IoTArrayDataInitDone  = 0;\n"
				+ "#else\n"
				+ "  float   IoTArrayData[IOTARRAYLEN];\n"
				+ "  uint8_t IoTArrayDataIndex     = 0;\n"
				+ "  uint8_t IoTArrayDataInitDone  = 0;\n"
				+ "#endif\n"
				+ "";
		translator.addDefinitionCommand(ArrayStruct);

		// I2C-initialisieren
		translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
		translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
		
		String Setup = "#ifndef IOTW_BOARD_MAKEY \n initOLED(0);\n #endif\n";
		translator.addSetupCommand(Setup);

		  	    
	    Setup = "if (!IoTArrayDataInitDone) {\n"
	    		+ "  IoTArrayDataInitDone = 1;\n"
	    		+ "  for (uint8_t i=0; i<IOTARRAYLEN; i++) {\n"
	    		+ "    IoTArrayData[i] = NAN;\n"
	    		+ "  }\n"
	    		+ "}";
        translator.addSetupCommand(Setup);
	    
	
	    
	    String Display = "//------------------ OLED: Display first Values of IoTDataArray\n"
	    		+ "void OLEDDisplayIoTArrayData(float actValue, String IoTDataName, float minVal, float maxVal) {\n"
	    		+ "  if (!isnan(actValue)) { // add value\n"
	    		+ "    if (IoTArrayDataIndex  > (IOTARRAYLEN - 1) ) { // shift left\n"
	    		+ "      for (uint8_t i = 1; i < IOTARRAYLEN; i++) {\n"
	    		+ "        IoTArrayData[i-1] = IoTArrayData[i]; \n"
	    		+ "      } \n"
	    		+ "      IoTArrayDataIndex = IOTARRAYLEN-1;\n"
	    		+ "    }\n"
	    		+ "    IoTArrayData[IoTArrayDataIndex] = actValue;\n"
	    		+ "    IoTArrayDataIndex++; \n"
	    		+ "  }\n"
	    		+ "  //myOLEDdisplay.clearDisplay();\n"
	    		+ "  //myOLEDdisplay.setRotation(0);\n"
	    		+ "  canvas.fillScreen(SH110X_BLACK);\n"
	    		+ "  // Draw the title\n"
	    		+ "  canvas.setTextSize(1);\n"
	    		+ "  canvas.setTextColor(SH110X_WHITE);\n"
	    		+ "  canvas.setCursor(40, 0);\n"
	    		+ "  canvas.print(IoTDataName+\":\");\n"
	    		+ "  // Draw the last value on the right side\n"
	    		+ "  canvas.print(IoTArrayData[IoTArrayDataIndex-1], 1); // Print last value with 1 decimal place\n"
	    		+ "\n"
	    		+ "  // Draw the axis labels\n"
	    		+ "  canvas.setCursor(2, SCREEN_HEIGHT - 8);\n"
	    		+ "  canvas.print(minVal, 1); // Print min value with 1 decimal place\n"
	    		+ "  canvas.setCursor(2, 0);\n"
	    		+ "  canvas.print(maxVal, 1); // Print max value with 1 decimal place\n"
	    		+ "\n"
	    		+ "  // Draw Coordinates\n"
	    		+ "  int zero = map(0, minVal, maxVal, SCREEN_HEIGHT - 1, 0);\n"
	    		+ "  canvas.drawLine(0, 0, 0, SCREEN_HEIGHT-1, SH110X_WHITE);\n"
	    		+ "  canvas.drawLine(0, zero, SCREEN_WIDTH - 1, zero, SH110X_WHITE);\n"
	    		+ "   \n"
	    		+ "  // Draw the data graph\n"
	    		+ "  int prevX = 0;\n"
	    		+ "  int prevY = map(IoTArrayData[0], minVal, maxVal, SCREEN_HEIGHT - 1, 10);\n"
	    		+ "  for (int i = 0; i <= IoTArrayDataIndex - 1; i++) {\n"
	    		+ "    int x = map(i, 0, IOTARRAYLEN-1, 0, SCREEN_WIDTH - 1);\n"
	    		+ "    int y = map(IoTArrayData[i], minVal, maxVal, SCREEN_HEIGHT - 1, 10);\n"
	    		+ "    canvas.drawLine(prevX, prevY, x, y, SH110X_WHITE);\n"
	    		+ "    canvas.drawLine(prevX, prevY+1, x, y+1, SH110X_WHITE);\n"
	    		+ "    prevX = x;\n"
	    		+ "    prevY = y;\n"
	    		+ "  }\n"
	    		+ "\n"
	    		+ "  myOLEDdisplay.drawBitmap(0,0, canvas.getBuffer(), SCREEN_WIDTH, SCREEN_HEIGHT, SH110X_WHITE, SH110X_BLACK);\n"
	    		+ "  myOLEDdisplay.display();\n"
	    		+ "}";
		translator.addDefinitionCommand(Display);
		
		TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0);
		String val = "NAN";
		if (translatorBlock != null) {
			val = translatorBlock.toCode();
		}
	
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String text = translatorBlock.toCode();
	
		
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
		String min = translatorBlock.toCode();
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
		String max = translatorBlock.toCode();
		
		String ret  = "OLEDDisplayIoTArrayData(" + val + "," + text + "," + min +"," + max+ ");";
		return codePrefix + ret + codeSuffix;
		
	}
}

