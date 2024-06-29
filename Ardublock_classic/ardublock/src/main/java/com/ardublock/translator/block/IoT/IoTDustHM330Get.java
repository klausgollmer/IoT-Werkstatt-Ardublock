package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTDustHM330Get extends TranslatorBlock
{

  public IoTDustHM330Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Header hinzuf�gen
    translator.addHeaderFile("Seeed_HM330X.h");

    // Setupdeklaration
    translator.addSetupCommand("Serial.begin(115200);");
    // I2C-initialisieren
    
    String Setup;
    translator.addSetupCommand("Wire.begin(); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(\"Something wrong with I2C\"); \n  #endif \n");
 
	Setup = "while (!HM330sensor_ready) {\r\n" + 
			"    HM330sensor_ready = !HM330sensor.init(); // HM330 Feinstaubsensor \r\n" + 
			"    delay(300);\r\n" + 
			"}";
    translator.addSetupCommand(Setup);

    
    // Deklarationen hinzuf�gen
    translator.addDefinitionCommand("HM330X HM330sensor; // www.seeed.cc, Author: downey\n");
    translator.addDefinitionCommand("int HM330sensor_ready=0; // for init after deep-sleep\n");
    
    String read = "// Feinstaubsensor HM330 Copyright (c) 2018 Seeed Technology Co., Ltd. \n" +
        "int readFeinstaubHM330(int chan) {\r\n" + 
        "  int myvalue = -1;\r\n" + 
        "  int i=1;\r\n" + 
        "  u8  data[30];\r\n" + 
        "  if(!HM330sensor_ready) {\r\n" + 
        "    HM330sensor_ready = !HM330sensor.init(); \r\n" + 
        "    delay(1000); \r\n" + 
        "  }\r\n" + 
        "  if (chan == 2) i=6; // 2.5\r\n" + 
        "  if (chan == 3) i=5; // 1.0\r\n" + 
        "  if (chan == 1) i=7; // 10\r\n" + 
        "  if(HM330sensor.read_sensor_value(data,29)) {\r\n" + 
        "        Serial.println(\"HM330X read result failed!!!\");\r\n" + 
        "    } else {\r\n" + 
        "     // Test Checksum\r\n" + 
        "       u8 sum=0;\r\n" + 
        "       for(int i=0;i<28;i++){\r\n" + 
        "        sum+=data[i];\r\n" + 
        "       }\r\n" + 
        "       if(sum!=data[28]) Serial.println(\"wrong checkSum!!!!\");\r\n" + 
        "       else {\r\n" + 
        "         myvalue = (u16)data[i*2]<<8|data[i*2+1];\r\n" + 
        "       }\r\n" + 
        "  }\r\n" + 
        "  return myvalue;\r\n" + 
        "}\r\n"; 
   	
    	  
    translator.addDefinitionCommand(read);		
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
          
    // Code von der Mainfunktion
	ret = "readFeinstaubHM330("+code+")";
	
   
    return codePrefix + ret + codeSuffix;
  }
}