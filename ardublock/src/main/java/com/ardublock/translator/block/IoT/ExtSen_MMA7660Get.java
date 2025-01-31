package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_MMA7660Get extends TranslatorBlock
{

  public ExtSen_MMA7660Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
 
    // Header hinzuf�gen
    // now in init translator.addHeaderFile("Wire.h");
    translator.addHeaderFile("MMA7660.h");
    
    
    // Setupdeklaration
    // I2C-initialisieren
    //translator.addSetupCommand("Serial.begin(115200);");
    // now in init : translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
    // now in init : translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) IOTW_PRINTLN(F(\"Something wrong with I2C\")); \n  #endif \n");
 
    translator.addSetupCommand("seeedMMA.init();\n");
    
    // Deklarationen hinzuf�gen
    
    translator.addDefinitionCommand("// Grove - 3-Axis Digital Accelerometer(±1.5g) seeedstudio.com, Frankie.Chu");
    translator.addDefinitionCommand("// https://github.com/Seeed-Studio/Accelerometer_MMA7660");
    translator.addDefinitionCommand("// MIT License, for Disclaimer see end of file ");
	translator.addDefinitionCommand("MMA7660 seeedMMA;");

	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String Achse = translatorBlock.toCode();
    
	String readSensor =	"float readMMA_Acc(int achse) { // ------  Einlesefunktion Seeed MMA Beschleunigungssensor \n"
			+ " float wert,ax,ay,az;\n"
			+ " "
            + " seeedMMA.getAcceleration(&ax,&ay,&az);// Abfrage Sensor \n"
			+ " switch (achse) {// Achse auswählen\n"
			+"   case 1: wert = ax;\n"
			+"           break;\n"
			+"   case 2: wert = ay;\n"
			+"           break;\n"
			+"   case 3: wert = az;\n"
			+"           break;\n"
			+"  }\n"
			+ "return wert;\n"
			+ "}\n";
	translator.addDefinitionCommand(readSensor);
			     
	
    // Code von der Mainfunktion
	ret = "readMMA_Acc(" + Achse + ")";
   
    return codePrefix + ret + codeSuffix;
  }
}