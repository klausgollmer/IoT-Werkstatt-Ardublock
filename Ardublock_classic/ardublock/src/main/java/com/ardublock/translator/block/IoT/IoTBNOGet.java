package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTBNOGet extends TranslatorBlock
{

  public IoTBNOGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
       
    // Header hinzuf�gen
    translator.addHeaderFile("Wire.h");
    translator.addHeaderFile("Adafruit_Sensor.h");
    translator.addHeaderFile("Adafruit_BNO055.h");
    translator.addHeaderFile("utility/imumaths.h");
    
    
    // Setupdeklaration
    // I2C-initialisieren
    translator.addSetupCommand("Serial.begin(115200);");
    translator.addSetupCommand("Wire.begin(GPIO_I2C_SDA, GPIO_I2C_SCL); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
 
    translator.addSetupCommand("if (!boschBNE055.begin()) Serial.println(\"Kein BNO055 Lagesensor gefunden\");\n");
    translator.addSetupCommand("boschBNE055.setExtCrystalUse(true);\n");
    
    // Deklarationen hinzuf�gen
    translator.addDefinitionCommand("// Adafruit BMO-Library https://github.com/adafruit/Adafruit_BNO055");
	translator.addDefinitionCommand("Adafruit_BNO055 boschBNE055=Adafruit_BNO055();");

	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String Sensor = translatorBlock.toCode();
    
	String readSensor =	"float readBNO_"+Sensor+"(int achse) { // ------  Einlesefunktion BOSCH Lagesensor \n"
			+ " float wert;\n"
			+ " sensors_event_t event;\n"
            + " boschBNE055.getEvent(&event);\n"
			+ " switch (achse) {// Achse auswählen\n"
			+"   case 1: wert = event." + Sensor + ".x;\n"
			+"           break;\n"
			+"   case 2: wert = event." + Sensor + ".y;\n"
			+"           break;\n"
			+"   case 3: wert = event." + Sensor + ".z;\n"
			+"           break;\n"
			+"  }\n"
			+ "return wert;\n"
			+ "}\n";
	translator.addDefinitionCommand(readSensor);
			     
	translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String Achse = translatorBlock.toCode();
	
    // Code von der Mainfunktion
	ret = "readBNO_" + Sensor + "(" + Achse + ")";
   
    return codePrefix + ret + codeSuffix;
  }
}