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

		String ArrayStruct ="//--------------------------------  DatenArray \n" 
				  + "// Felddimension ALEN, nur die ersten 15 Daten in diesem Feld werden auf der LED Matrix angezeigt \n"
	    		  + "#define ALEN 100 \n"
	    		  + "float ArrayData[ALEN];\n";
		translator.addDefinitionCommand(ArrayStruct);
		
		   // I2C-initialisieren
		translator.addSetupCommand("Serial.begin(115200);");
	   
		translator.addSetupCommand("Wire.begin(); // ---- Initialisiere den I2C-Bus \n");
		translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");

		
		String TickerDef="//--------------------------------------- Charlieplex Matrix\n"
				  + "Adafruit_IS31FL3731_Wing matrix = Adafruit_IS31FL3731_Wing();\n";
		translator.addDefinitionCommand(TickerDef);

         
           // Matrix initialisieren	    
	    String Setup =        "matrix.begin();// Matrix initialisieren \n"
	    		     + "delay(10);\n"
	    		     + "matrix.clear(); \n";
	    translator.addSetupCommand(Setup);
	
	    
	    String Display = "//------------------ Ausgabe der ersten 15 Werte als Grafik\n"
	    		+ "void matrixAnzeigeArray(float mymin, float mymax) {\n" + 
	    		"  matrixausgabe_text  = \"\"; // Textdisplay anhalten\n"+
     	  	    "  Serial.println();\n"+
	    		"  for (int i=0; i<15;i++){ // Anzeige Grafik\n" + 
	    		"     Serial.print(String(ArrayData[i])+String(\";\"));\n"+
	    		"     matrix.drawLine(i,0, i, (matrix.height()-1), 0); // Lösche Anzeige\n" + 
	    		"     float high = (ArrayData[i]-mymin)/(mymax-mymin)*(matrix.height());\n" + 
	    		"     if ((high>=0.5) && (high<=matrix.height())) // Zeichne Linie\n" + 
	    		"        matrix.drawLine(i,matrix.height()-1, i, matrix.height() - high, 60);\n" + 
	    		"  }\n" + 
    	  	    "  Serial.println();\n"+
	    		"}\n" + 
	    		"";
		translator.addDefinitionCommand(Display);

	    
		
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String min = translatorBlock.toCode();
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String max = translatorBlock.toCode();
		
		String ret  = "matrixAnzeigeArray(" + min +"," + max+ ");";
		return codePrefix + ret + codeSuffix;
		
	}
}

