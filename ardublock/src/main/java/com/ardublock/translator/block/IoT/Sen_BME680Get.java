package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Sen_BME680Get extends TranslatorBlock
{

  public Sen_BME680Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Wert von dem ersten Blockeingang auslesen
           
    // Header hinzuf�gen
    translator.addHeaderFile("Adafruit_BME680.h");
    // now in init translator.addHeaderFile("Wire.h");
    String Dis ="/***************************************************************************\r\n"
    		+ "  This is a library for the BME680 gas, humidity, temperature & pressure sensor\r\n"
    		+ "  Designed specifically to work with the Adafruit BME680 Breakout\r\n"
    		+ "  ----> http://www.adafruit.com/products/3660\r\n"
    		+ "  These sensors use I2C or SPI to communicate, 2 or 4 pins are required\r\n"
    		+ "  to interface.\r\n"
    		+ "  Adafruit invests time and resources providing this open source code,\r\n"
    		+ "  please support Adafruit and open-source hardware by purchasing products\r\n"
    		+ "  from Adafruit!\r\n"
    		+ "  Written by Limor Fried & Kevin Townsend for Adafruit Industries.\r\n"
    		+ "  BSD license, all text above must be included in any redistribution\r\n"
    		+ " ***************************************************************************/\r\n";
	translator.addDefinitionCommand(Dis);
	translator.addDefinitionCommand("Adafruit_BME680 boschBME680; // Objekt Bosch Umweltsensor");
    translator.addDefinitionCommand("int boschBME680_ready = 0;\n");
    
	// Setupdeklaration
    // I2C-initialisieren
    //translator.addSetupCommand("Serial.begin(115200);");
    // now in init : translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
    // now in init : translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
 
    
    translator.addSetupCommand("boschBME680_ready = boschBME680.begin(118);\n");

    translator.addSetupCommand("if (boschBME680_ready == 0) {"+
    		"while(1) { Serial.println(\"BME680 nicht vorhanden - der alte Octopus nutzt BME280, ggf. Puzzleteile tauschen\");delay(500);}\n"+
    		"}\n");

    
//    String Setup = "if (!boschBME680.begin(118)) { Serial.println(\"Failed to communicate BME680\");while (1) {delay(1);};}\n";
//    translator.addSetupCommand(Setup);
    
    
  String  Setup = "// Set up Bosch BME 680\n"+
    "boschBME680.setTemperatureOversampling(BME680_OS_8X);\n"+
    "boschBME680.setHumidityOversampling(BME680_OS_2X);\n"+
    "boschBME680.setPressureOversampling(BME680_OS_4X);\n"+
    "boschBME680.setIIRFilterSize(BME680_FILTER_SIZE_3);\n"+
    "boschBME680.setGasHeater(0, 0); // off\n";    
    translator.addSetupCommand(Setup);
    
    // Deklarationen hinzuf�gen
    	
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();

   /* 
    String offset ="";
    translatorBlock = this.getTranslatorBlockAtSocket(1);
	if (translatorBlock!=null) {
           offset = translatorBlock.toCode();
           code = code + "+" + offset; 
    }
    */
    
    
    // Code von der Mainfunktion
	ret = code;
	
   
    return codePrefix + ret + codeSuffix;
  }
}