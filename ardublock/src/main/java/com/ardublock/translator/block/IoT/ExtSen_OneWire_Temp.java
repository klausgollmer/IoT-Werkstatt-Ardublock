package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_OneWire_Temp extends TranslatorBlock
{

  public ExtSen_OneWire_Temp (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
	  
	  
	  
    String ret;
        
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String gpio = translatorBlock.toCode();
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String index = translatorBlock.toCode();
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
    String res = translatorBlock.toCode();
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
    String mode = translatorBlock.toCode();

    translator.addHeaderFile("OneWire.h");
    translator.addHeaderFile("DallasTemperature.h");
    
    
    String Dis="// DALLAS Temperature Sensor Lib, MIT License Copyright (c) 2024 Miles Burton\r\n"
    		+ "// https://github.com/milesburton/Arduino-Temperature-Control-Library\r\n"
    		+ "OneWire oneWire("+gpio+");\r\n"
    		+ "DallasTemperature ds(&oneWire);\n"
    		+ " float DS18B20_get(uint8_t i, uint8_t m) {\r\n"
    		+ "  float val =NAN;\r\n"
    		+ "  switch (m) {\r\n"
    		+ "    case 0:\r\n"
    		+ "        val = ds.getTempCByIndex(i);// Ergebnis holen\r\n"
    		+ "        ds.requestTemperatures();   // startet Wandlung  \r\n"
    		+ "    break;\r\n"
    		+ "    case 1: \r\n"
    		+ "         ds.requestTemperatures();   // startet Wandlung  \r\n"
    		+ "    break;          \r\n"
    		+ "    case 2: \r\n"
    		+ "         val = ds.getTempCByIndex(i);// Ergebnis holen\r\n"
    		+ "    break;\r\n"
      		+ "    case 3:\r\n"
    		+ "        ds.requestTemperatures();   // startet Wandlung  \r\n"
    		+ "        delay(750);                 // warte \r\n"
    		+ "        val = ds.getTempCByIndex(i);// Ergebnis holen\r\n"
    		+ "    break;\r\n"
  
    		+ "  }\r\n"
    		+ " "
    		+ "  return val;\r\n"
    		+ " }\r\n"
    		+ "\r\n"
    		+ "void DS18B20_printSensorTable() {\r\n"
    		+ "  uint8_t n = ds.getDeviceCount();\r\n"
    		+ "  Serial.println(F(\"\\n=== DS18B20 Scan ===\"));\r\n"
    		+ "  Serial.println(F(\"| Index | ROM-Adresse       | Res |\"));\r\n"
    		+ "  DeviceAddress addr;\r\n"
    		+ "  for (uint8_t i = 0; i < n; ++i) {\r\n"
    		+ "    if (ds.getAddress(addr, i)) {\r\n"
    		+ "      Serial.printf(F(\"| %5u | \"), i);\r\n"
    		+ "      for (uint8_t j = 0; j < 8; ++j) Serial.printf(F(\"%02X\"), addr[j]);  // 64-Bit-ID\r\n"
    		+ "      Serial.printf(F(\"  | %3u |\\n\"),\r\n"
    		+ "                    ds.getResolution(addr)  );                     // 9-..12 bit\r\n"
    		+ "    }\r\n"
    		+ "  }\r\n"
    		+ "}\r\n"
    		+ "";
   	translator.addDefinitionCommand(Dis);

    String Set = "ds.begin();\r\n"
  		+ "  ds.setResolution("+res+");\n"
		+ "  #if (IOTW_DEBUG_LEVEL >1)\r\n"
		+ "    DS18B20_printSensorTable();\r\n"
		+ "  #endif\"\r\n"
		+ "  ds.requestTemperatures();\n";
	translator.addSetupCommand(Set);
   	
	// Code von der Mainfunktion
	ret = "DS18B20_get("+index+","+mode+")"; 
    return codePrefix + ret + codeSuffix;
  }
}