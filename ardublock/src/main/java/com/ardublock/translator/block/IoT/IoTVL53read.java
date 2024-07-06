package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTVL53read extends TranslatorBlock
{

  public IoTVL53read (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Header hinzuf�gen
    translator.addHeaderFile("VL53L0X.h");

	translator.addSetupCommand("Serial.begin(115200);");
    translator.addSetupCommand("Wire.begin(GPIO_I2C_SDA, GPIO_I2C_SCL); // ---- Initialisiere den I2C-Bus \n");
	translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
		
    String Setup = "VL53sensor.setTimeout(500);\r\n" + 
    		"  if (!VL53sensor.init())\r\n" + 
    		"  {\r\n" + 
    		"    Serial.println(\"Failed to detect and initialize VL53sensor!\");\r\n" + 
    		"    while (1) {}\r\n" + 
    		"  }";
    
    // Setupdeklaration
    // I2C-initialisieren
    translator.addSetupCommand(Setup);

    
    // Deklarationen hinzuf�gen
    translator.addDefinitionCommand("// https://github.com/pololu/vl53l0x-arduino by kevin (pololu) \n"
    		+ "VL53L0X VL53sensor; // https://www.pololu.com/product/2490;\n");
   	
    
    String read = "float readVL53L0X(int longrange) { // pololu lib \r\n" + 
    		"  float Val = NAN;\r\n" + 
    		"  if (longrange == 1) {\r\n" + 
    		"    VL53sensor.setSignalRateLimit(0.1);\r\n" + 
    		"    // increase laser pulse periods (defaults are 14 and 10 PCLKs)\r\n" + 
    		"    VL53sensor.setVcselPulsePeriod(VL53L0X::VcselPeriodPreRange, 18);\r\n" + 
    		"    VL53sensor.setVcselPulsePeriod(VL53L0X::VcselPeriodFinalRange, 14);\r\n" + 
    		"  }else{\r\n" + 
    		"    VL53sensor.setSignalRateLimit(0.25);\r\n" + 
    		"    VL53sensor.setVcselPulsePeriod(VL53L0X::VcselPeriodPreRange, 14);\r\n" + 
    		"    VL53sensor.setVcselPulsePeriod(VL53L0X::VcselPeriodFinalRange, 10);\r\n" + 
    		"  }\r\n" + 
    		"  Val = VL53sensor.readRangeSingleMillimeters();\r\n" + 
    		"  if (VL53sensor.timeoutOccurred()){\r\n" + 
    		"    Val = NAN;\r\n" + 
    		"    Serial.println(\"VL53 Sensor TIMEOUT\");\r\n" + 
    		"  }\r\n" + 
    		"  return Val;\r\n" + 
    		"}";

    translator.addDefinitionCommand(read);		
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String range = translatorBlock.toCode();
          
    // Code von der Mainfunktion
	ret = "readVL53L0X("+range+")";
	
   
    return codePrefix + ret + codeSuffix;
  }
}