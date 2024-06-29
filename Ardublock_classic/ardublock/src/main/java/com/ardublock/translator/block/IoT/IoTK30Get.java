package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTK30Get extends TranslatorBlock
{

  public IoTK30Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
  
    // Header hinzuf�gen
    translator.addHeaderFile("Wire.h");
   	
    String Def = "float K30read() {\r\n" + 
    		"  float co2_value = NAN;\r\n" + 
    		"  int co2_raw = 0;\r\n" + 
    		"  int Tout = 2;\r\n" + 
    		"  while ((Tout > 0) && (isnan(co2_value) || (co2_value == 0.))) {\r\n" + 
    		"    Wire.beginTransmission(0x68);\r\n" + 
    		"    Wire.write(0x22);\r\n" + 
    		"    Wire.write(0x00);\r\n" + 
    		"    Wire.write(0x08);\r\n" + 
    		"    Wire.write(0x2A);\r\n" + 
    		"    Wire.endTransmission();\r\n" + 
    		"    delay( 100 );\r\n" + 
    		"    Wire.requestFrom(0x68,4);\r\n" + 
    		"    byte i = 0;\r\n" + 
    		"    byte buffer[4] = {\r\n" + 
    		"      0, 0, 0, 0    };\r\n" + 
    		"    while (Wire.available()){\r\n" + 
    		"      buffer[i] = Wire.read();\r\n" + 
    		"      i++;\r\n" + 
    		"    }\r\n" + 
    		"    co2_raw |= buffer[1] & 0xFF;\r\n" + 
    		"    co2_raw = co2_raw << 8;\r\n" + 
    		"    co2_raw |= buffer[2] & 0xFF;\r\n" + 
    		"    byte sum = 0; //Checksum Byte\r\n" + 
    		"    sum = buffer[0] + buffer[1] + buffer[2]; //Byte addition utilizes overflow\r\n" + 
    		"    if (sum == buffer[3])\r\n" + 
    		"      co2_value = co2_raw;\r\n" + 
    		"    else\r\n" + 
    		"      Serial.print(\"K30 failed reading.\");\r\n" + 
    		"    Tout--;\r\n" + 
    		"  }\r\n" + 
    		"  return co2_value;\r\n" + 
    		"} //end ";
    
    
   	translator.addDefinitionCommand("//Reading CO2 from the K30\n");
   	translator.addDefinitionCommand(Def);
	
    translator.addSetupCommand("Wire.begin(); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(\"Something wrong with I2C\"); \n  #endif \n");
 
   	
    
    // Code von der Mainfunktion
	ret = "K30read()";
	
   
    return codePrefix + ret + codeSuffix;
  }
}