package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtDisp_OLED_PrintTerminal  extends TranslatorBlock {

	public ExtDisp_OLED_PrintTerminal (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		//translator.addHeaderFile("Adafruit_GFX.h");
		translator.addHeaderFile("Adafruit_SH110X.h");
		
		String Dis="/* Adafruit SH110x OLED  / GFX \n"
				 + "Copyright (c) Adafruit Industries\r\n"
				 + "BSD, Disclaimer see https://github.com/adafruit/Adafruit_SH110x?tab=License-1-ov-file#readme \n"
				 + "https://github.com/adafruit/Adafruit-GFX-Library?tab=License-1-ov-file#readme  \n"
				 + "*/\n";
	   	translator.addDefinitionCommand(Dis);
	   	String Def="Adafruit_SH1107 myOLEDdisplay = Adafruit_SH1107(IOTW_SCREEN_HEIGHT, IOTW_SCREEN_WIDTH, &Wire);";
	    translator.addDefinitionCommand(Def);

		
	    String Setup ="if (!(myOLEDdisplay.begin(0x3C, true))) { // OLED Display Address 0x3C default\r\n"
   		  		+ "  IOTW_PRINTLN(F(\"\\nno OLED detected\"));\r\n"
   		  		+ "} \r\n";
 	    translator.addSetupCommand(Setup);
		
		
		Def = "// defines for OLED Terminal Mode \n"
			+ "#define MAX_LINES 8 // Anzahl der Zeilen, die auf dem Display angezeigt werden können\n"
			+ "#define LINE_HEIGHT 8 // Höhe jeder Textzeile in Pixeln\n"
			+ "#define CHAR_WIDTH 6 // Breite eines Zeichens bei Textgröße 1\n"
			+ "#define MAX_CHARS_PER_LINE (IOTW_SCREEN_WIDTH / CHAR_WIDTH) // Maximale Zeichen pro Zeile\n"
			+ "\n"
			+ "String textBuffer[MAX_LINES]; // Puffer für die Textzeilen\n"
			+ "int currentLine = 0; // Aktuelle Zeile im Puffer";	
		translator.addDefinitionCommand(Def);
		
	
		
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String t = translatorBlock.toCode();
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String LF = translatorBlock.toCode();
		
		// I2C-initialisieren
		// now in init : translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
		// now in init : translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) IOTW_PRINTLN(F(\"Something wrong with I2C\")); \n  #endif \n");
			

	    Setup =   "myOLEDdisplay.setRotation(0);\n"
	    		+ "myOLEDdisplay.setTextSize(1);\n"
	    		+ "myOLEDdisplay.setCursor(0,0);\n"
	    		+ "myOLEDdisplay.setTextColor(SH110X_WHITE);\n"
	    		+ "myOLEDdisplay.clearDisplay();\n";
	    translator.addSetupCommand(Setup);

	    Setup =   "for (int i = 0; i < MAX_LINES; i++) {\n"
	    		+ "    textBuffer[i] = \"\";\n"
	    		+ "}\n";
	    translator.addSetupCommand(Setup);

	    
	    
	    String Terminal = "// \n"
	    		+ "// Terminal-Emulation für OLED Display \n"
	    		+ "void printToTerminal(String text, bool LF) {\n"
	    		+ " while (text.length() > 0) {\n"
	    		+ "    String Line = textBuffer[currentLine];\n"
	    		+ "    int possibleLen = MAX_CHARS_PER_LINE - Line.length();\n"
	    		+ "    int textLen = text.length();\n"
	    		+ "    if (possibleLen >= 1) {\n"
	    		+ "      textBuffer[currentLine] += text.substring(0, possibleLen);\n"
	    		+ "      text = text.substring(possibleLen, textLen);\n"
	    		+ "    }\n"
	    		+ "    if (text.length() > 0) { // add new Line\n"
	    		+ "      if (currentLine < MAX_LINES - 1) {\n"
	    		+ "        currentLine++;\n"
	    		+ "        textBuffer[currentLine] = \"\";\n"
	    		+ "      } else {\n"
	    		+ "        // Wenn der Puffer voll ist, verschiebe alle Zeilen um eine nach oben\n"
	    		+ "        for (int i = 0; i < MAX_LINES - 1; i++) {\n"
	    		+ "          textBuffer[i] = textBuffer[i + 1];\n"
	    		+ "        }\n"
	    		+ "        textBuffer[MAX_LINES - 1] = \"\";\n"
	    		+ "      }\n"
	    		+ "    }\n"
	    		+ "  }\n"
	    		+ "\n"
	    		+ "\n"
	    		+ "  if (LF) { // add CR Line\n"
	    		+ "    if (currentLine < MAX_LINES-1) {\n"
	    		+ "       currentLine++;\n"
	    		+ "       textBuffer[currentLine] = \"\";\n"
	    		+ "    } else {\n"
	    		+ "       // Wenn der Puffer voll ist, verschiebe alle Zeilen um eine nach oben\n"
	    		+ "      for (int i = 0; i < MAX_LINES - 1; i++) {\n"
	    		+ "        textBuffer[i] = textBuffer[i + 1];\n"
	    		+ "      }\n"
	    		+ "      textBuffer[MAX_LINES - 1] = \"\";\n"
	    		+ "    }\n"
	    		+ "  }\n"
	    		+ "\n"
	    		+ "  // Aktualisiere das Display\n"
	    		+ "  myOLEDdisplay.setRotation(1);\n"
	    		+ "  myOLEDdisplay.setFont();\n"
	    		+ "  myOLEDdisplay.clearDisplay();\n"
	    		+ "  for (int i = 0; i <= currentLine; i++) {\n"
	    		+ "    myOLEDdisplay.setCursor(0, i * LINE_HEIGHT);\n"
	    		+ "    myOLEDdisplay.print(textBuffer[i]);\n"
	    		+ "  }\n"
	    		+ "  myOLEDdisplay.display();\n"
	    		+ "}";
	    translator.addDefinitionCommand(Terminal);
	    String ret = "printToTerminal("+t+","+LF+");\n";
		return codePrefix + ret + codeSuffix;
		
	}
}

