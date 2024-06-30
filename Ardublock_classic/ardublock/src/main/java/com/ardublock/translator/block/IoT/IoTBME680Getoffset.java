package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTBME680Getoffset extends TranslatorBlock
{

  public IoTBME680Getoffset (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Header hinzuf�gen
    translator.addHeaderFile("Adafruit_BME680.h");
    translator.addHeaderFile("Wire.h");

	translator.addDefinitionCommand("// BME680 Lib written by Limor Fried & Kevin Townsend for Adafruit Industries, http://www.adafruit.com/products/3660");
	translator.addDefinitionCommand("Adafruit_BME680 boschBME680; // Objekt Bosch Umweltsensor");
    translator.addDefinitionCommand("int boschBME680_ready = 0;\n");
    
	// Setupdeklaration
    // I2C-initialisieren
    translator.addSetupCommand("Wire.begin(); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
 
    
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
    	 
    translator.setBME680Program(true);;
    // Deklarationen hinzuf�gen
    	
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
        
    String heat ="";
    translatorBlock = this.getTranslatorBlockAtSocket(2);
	if (translatorBlock!=null) {
        heat = translatorBlock.toCode();
//        System.out.println(code);
        
        if (code.startsWith("boschBME680.readGas")) {
          String myfun = "float boschBME680_readGas(float heat) { \n"
                        +"  boschBME680.setGasHeater(heat,150);\r\n" +
        		         "  delay(100); // wait for heating\n"+
                         "  uint32_t Val = boschBME680.readGas();\r\n" + 
                         "  int Timeout = 20;\r\n" + 
                         "  while ((Val == 0) && (Timeout > 0)) {\r\n" + 
                         "      delay(2);\r\n" + 
                         "      //boschBME680.setGasHeater(heat,150);\r\n" + 
                         "      Val = boschBME680.readGas();\r\n" + 
                         "      Timeout--;\r\n" + 
                         " }    \r\n" + 
        		         " return Val; \n "+
                         "}\n";
          translator.addDefinitionCommand(myfun);
              		  
          code = "boschBME680_readGas("+heat+")/1000."; 
        }
    }
    
    String offset ="";
    translatorBlock = this.getTranslatorBlockAtSocket(1);
	if (translatorBlock!=null) {
           offset = translatorBlock.toCode();
           code = "("+code + "+(" + offset +"))"; 
    }
    
    
    // Code von der Mainfunktion
	ret = code;
	
   
    return codePrefix + ret + codeSuffix;
  }
}