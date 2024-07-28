package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTArrayMatrixPrint  extends TranslatorBlock {

	public IoTArrayMatrixPrint (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		translator.addHeaderFile("Wire.h");
		translator.addHeaderFile("Adafruit_GFX.h");
		translator.addHeaderFile("Adafruit_IS31FL3731.h");
		translator.addDefinitionCommand("String matrixausgabe_text  = \" \"; // Ausgabetext als globale Variable\n");
		translator.addDefinitionCommand("volatile int matrixausgabe_index = 0;// aktuelle Position in Matrix\n");
		translator.addDefinitionCommand("volatile int matrixausgabe_wait  = 0;// warte bis Anzeige durchgelaufen ist\n");

		String ArrayStruct ="//--------------------------------  IoTDataArray for timeseries \n"
				+ "// Dimension IOTARRAYLEN, only the first 15 elements were displayed charlieplex matrix \n"
				+ "#define IOTARRAYLEN 64 \n"
				+ "#if defined(ESP32) && defined(USE_DEEPSLEEP)\n"
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
		translator.addSetupCommand("Serial.begin(115200);");
	   
		translator.addSetupCommand("Wire.begin(GPIO_I2C_SDA, GPIO_I2C_SCL); // ---- Initialisiere den I2C-Bus \n");
		translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");

		
		String TickerDef="//--------------------------------------- Charlieplex Matrix\n"
				  + "Adafruit_IS31FL3731_Wing matrix = Adafruit_IS31FL3731_Wing();\n";
		translator.addDefinitionCommand(TickerDef);

         
           // Matrix initialisieren	    
	    String Setup = "matrix.begin();// Matrix initialisieren \n"
	    		     + "delay(10);\n"
	    		     + "matrix.clear(); \n";
	    translator.addSetupCommand(Setup);
	    
	    Setup = "if (!IoTArrayDataInitDone) {\n"
	    		+ "  IoTArrayDataInitDone = 1;\n"
	    		+ "  for (uint8_t i=0; i<IOTARRAYLEN; i++) {\n"
	    		+ "    IoTArrayData[i] = NAN;\n"
	    		+ "  }\n"
	    		+ "}";
        translator.addSetupCommand(Setup);
	    
	
	    
	    String Display = "//------------------ LED-Matrix: Display first Values in DataArray\n"
	    		+ "void matrixDisplayIoTArrayData(float actValue, float mymin, float mymax) {\n"
	    		+ "  #define MAXLENGTH 15\n"
	    		+ "  matrixausgabe_text  = \"\"; // Textdisplay anhalten\n"
	    		+ "\n"
	    		+ "  if (!isnan(actValue)) { // add value\n"
	    		+ "    IoTArrayDataIndex++;\n"
	    		+ "    if (IoTArrayDataIndex  > (MAXLENGTH - 1) ) { // shift left\n"
	    		+ "      for (uint8_t i = 1; i < MAXLENGTH; i++) {\n"
	    		+ "        IoTArrayData[i-1] = IoTArrayData[i]; \n"
	    		+ "      } \n"
	    		+ "      IoTArrayDataIndex = MAXLENGTH-1;\n"
	    		+ "    }\n"
	    		+ "    IoTArrayData[IoTArrayDataIndex] = actValue;\n"
	    		+ "  }\n"
	    		+ "\n"
	    		+ "  /*\n"
	    		+ "  if (isnan(mymin) || isnan(mymax)) { // calculate min/max \n"
	    		+ "    mymin = IoTArrayData[0];\n"
	    		+ "    mymax = IoTArrayData[0];\n"
	    		+ "    for (uint8_t i = 1; i < MAXLENGTH; i++) {\n"
	    		+ "      if (mymin > IoTArrayData[i]) mymin = IoTArrayData[i];  \n"
	    		+ "      if (mymax < IoTArrayData[i]) mymax = IoTArrayData[i];  \n"
	    		+ "    } \n"
	    		+ "  }\n"
	    		+ "  */\n"
	    		+ "  for (int i=0; i<15;i++){ // Anzeige Grafik\n"
	    		+ "    matrix.drawLine(i,0, i, (matrix.height()-1), 0); // Lösche Anzeige\n"
	    		+ "    float high = (IoTArrayData[i]-mymin)/(mymax-mymin)*(matrix.height());\n"
	    		+ "    if ((high>=0.5) && (high<=matrix.height())) // Zeichne Linie\n"
	    		+ "      matrix.drawLine(i,matrix.height()-1, i, matrix.height() - high, 60);\n"
	    		+ "  }\n"
	    		+ "}";
		translator.addDefinitionCommand(Display);
		
		TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0);
		String val = "NAN";
		if (translatorBlock != null) {
			val = translatorBlock.toCode();
		}
		
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String min = translatorBlock.toCode();
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
		String max = translatorBlock.toCode();
		
		String ret  = "matrixDisplayIoTArrayData(" + val + "," + min +"," + max+ ");";
		return codePrefix + ret + codeSuffix;
		
	}
}

