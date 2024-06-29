package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTMatrixPrint  extends TranslatorBlock {

	public IoTMatrixPrint (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		translator.addHeaderFile("Ticker.h");
		translator.addHeaderFile("Wire.h");
		translator.addHeaderFile("Adafruit_GFX.h");
		translator.addHeaderFile("Adafruit_IS31FL3731.h");
		
		   // I2C-initialisieren
		translator.addSetupCommand("Serial.begin(115200);");
	   
		translator.addSetupCommand("Wire.begin(); // ---- Initialisiere den I2C-Bus \n");
		translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(\"Something wrong with I2C\"); \n  #endif \n");
		 
           // Matrix initialisieren	    
	    String Setup =        "matrix.begin();// Matrix initialisieren \n"
	    		     + "delay(10);\n"
	    		     + "matrix.clear(); \n";
	    translator.addSetupCommand(Setup);
	
	    Setup = "matrix.setTextColor(60); // Helligkeit begrenzen \n"
	          + "matrixausgabe.attach(0.09, matrixUpdate); // zyklisch aktualisieren\n";
	    translator.addSetupCommand(Setup);
	 	 
		translator.addDefinitionCommand("String matrixausgabe_text  = \" \"; // Ausgabetext als globale Variable\n");
		translator.addDefinitionCommand("volatile int matrixausgabe_index = 0;// aktuelle Position in Matrix\n");
		translator.addDefinitionCommand("volatile int matrixausgabe_wait  = 0;// warte bis Anzeige durchgelaufen ist\n");

		String webserverDef = "typedef void (*func_ptr)(void);\n" + 
				"func_ptr WebServerHousekeeping = yield;\n";
		translator.addDefinitionCommand(webserverDef);

		String TickerDef="//--------------------------------------- Charlieplex Matrix\n"
				  + "Adafruit_IS31FL3731_Wing matrix = Adafruit_IS31FL3731_Wing();\n";
		translator.addDefinitionCommand(TickerDef);

		
		TickerDef="Ticker matrixausgabe;\n"
          + "void matrixUpdate(){ // Update Charlieplexmatrix über Ticker\n"
		  + "    int anzahlPixel=(matrixausgabe_text.length())*6;\n" 
          + "    if (anzahlPixel > 15) { // Scrollen \n"
		  + "     if (matrixausgabe_index >= -anzahlPixel) {\n"
		  + "      matrix.clear();\n"
		  + "      matrix.setCursor(matrixausgabe_index,0);\n"
		  + "      matrix.print(matrixausgabe_text);\n"
		  + "      matrixausgabe_index--;\n"
		  + "     } else {matrixausgabe_index = 12;matrixausgabe_wait=0;}\n"
		  + "    } else {// nur 3 Zeichen lang-> kein Scroll \n" 
		  + "     matrix.setCursor(0,0);\n"
		  + "     matrix.print(matrixausgabe_text);\n"
		  + "     matrixausgabe_wait  = 0; // no wait\n"
		  + "   }\n"
		  + "}\n"
		  + "void matrixAnzeige(String text, int wait) { // Setze Ausgabetext\n"
		  + " if (matrixausgabe_text  != text) { // update nur bei neuem Text \n"
		  + "    matrix.clear();\n"
		  + "    matrixausgabe_index = 10;\n" // vorher 0
		  + "    matrixausgabe_text  = text;\n"
		  + "    matrixausgabe_wait  = wait;\n"
		  + "    while (matrixausgabe_wait) {\n" + 
		  "       delay(100);  \n" + 
		  "       (*WebServerHousekeeping)(); // look at WebCients\n" + 
		  "      } // warte bis Text einmal ausgegeben ist\n"
		  + " }\n"
		  + "};\n";
		
		translator.addDefinitionCommand(TickerDef);
		
        String text;		
		TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0);
		if (translatorBlock!=null) {
			   text = translatorBlock.toCode();
		} else {
		       text = "\" \"";
		}
		
		String ret  = "matrixAnzeige(String(" + text +"),1);";
		return codePrefix + ret + codeSuffix;
		
	}
}

