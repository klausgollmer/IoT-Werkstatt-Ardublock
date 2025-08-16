package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_OneWire_Temp32 extends TranslatorBlock
{

  public ExtSen_OneWire_Temp32 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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

    translator.addHeaderFile("OneWireESP32.h");
    
    
    String Dis="// OneWire für den ESP32\r\n"
    		+ "// MIT License Copyright (c) 2023 htmltiger, https://github.com/junkfix/esp32-ds18b20\r\n"
    		+ "const uint8_t MaxDev = 4;\r\n"
    		+ "const char *errt[] = {\"\", \"CRC\", \"BAD\",\"DC\",\"DRV\"};\r\n"
    		+ "\r\n";
  	        translator.addDefinitionCommand(Dis);
  	
  	       Dis = "OneWire32* ds_"+gpio+" = nullptr;\r\n"
       		+ "RTC_DATA_ATTR bool     DS18B20_addressesCached_"+gpio+" = false;\r\n"
    		+ "RTC_DATA_ATTR uint64_t DS18B20_addr_"+gpio+"[MaxDev];\r\n"
    		+ "RTC_DATA_ATTR uint8_t  DS18B20_Resolution_"+gpio+";\r\n"
    		+ "void  DS18B20_InitESP32_"+gpio+"(uint8_t bits){\r\n"
    		+ "  if (!DS18B20_addressesCached_"+gpio+") {\r\n"
    		+ "    uint8_t devices = ds_"+gpio+"->search(DS18B20_addr_"+gpio+", MaxDev);\r\n"
    		+ "    DS18B20_addressesCached_"+gpio+" = true;\r\n"
    		+ "    #if (IOTW_DEBUG_LEVEL >1)\r\n"
    		+ "       Serial.printf(\"GPIO %d, gefundene Devices: %d\\n\", "+gpio+",devices);\r\n"
    		+ "    #endif\r\n"
    		+ "    for (uint8_t i = 0; i < devices; i++) {\r\n"
    		+ "      #if (IOTW_DEBUG_LEVEL >1)\r\n"
    		+ "         Serial.printf(\" Addr[%d] = 0x%llx\\n\", i, DS18B20_addr_"+gpio+"[i]);\r\n"
    		+ "      #endif\r\n"
    		+ "      DS18B20_setResolutionESP32_"+gpio+"(DS18B20_addr_"+gpio+"[i],bits);\r\n"
    		+ "    }\r\n"
    		+ "    DS18B20_Resolution_"+gpio+" = bits;\r\n"
    		+ "    if (devices < 1) IOTW_PRINTLN(F(\"no DS\")); \r\n"
    		+ "  }\r\n"
    		+ "}\r\n"
    		+ "\r\n"
    		+ "float DS18B20_getESP32_"+gpio+"(uint8_t i, uint8_t m) {\r\n"
    		+ "  float val   = NAN;\r\n"
    		+ "  uint8_t err = 0;\r\n"
    		+ "  \r\n"
    		+ "  switch (m) {\r\n"
    		+ "  case 0:\r\n"
    		+ "    err = ds_"+gpio+"->getTemp(DS18B20_addr_"+gpio+"[i], val);\r\n"
    		+ "    ds_"+gpio+"->request();            \r\n"
    		+ "    break;\r\n"
    		+ "  case 1: \r\n"
    		+ "    ds_"+gpio+"->request();            \r\n"
    		+ "    break;          \r\n"
    		+ "  case 2: \r\n"
    		+ "    err = ds_"+gpio+"->getTemp(DS18B20_addr_"+gpio+"[i], val);\r\n"
    		+ "    break;\r\n"
    		+ "  case 3:\r\n"
    		+ "    ds_"+gpio+"->request();            \r\n"
		    + "    delay(750);\r\n"
    		+ "    err = ds_"+gpio+"->getTemp(DS18B20_addr_"+gpio+"[i], val);\r\n"
    		+ "    break;\r\n"
    		+ "  }\r\n"
    		+ "  if(err){\r\n"
    		+ "   Serial.println(errt[err]);\r\n"
    		+ "   val = NAN;\r\n"
    		+ "  }\r\n"
    		+ "  return val;\r\n"
    		+ "}\r\n"
    		+ "\r\n"
    		+ "bool DS18B20_setResolutionESP32_"+gpio+"(uint64_t addr, uint8_t bits) {\r\n"
    		+ "  // 1) Config-Byte ermitteln\r\n"
    		+ "  uint8_t cfg;\r\n"
    		+ "  switch(bits) {\r\n"
    		+ "    case  9: cfg = 0x1F; break;  // R1=0, R0=0\r\n"
    		+ "    case 10: cfg = 0x3F; break;  // R1=0, R0=1\r\n"
    		+ "    case 11: cfg = 0x5F; break;  // R1=1, R0=0\r\n"
    		+ "    case 12: cfg = 0x7F; break;  // R1=1, R0=1\r\n"
    		+ "    default: return false;       // ungültig\r\n"
    		+ "  }\r\n"
    		+ "\r\n"
    		+ "  // 2) Scratchpad beschreiben\r\n"
    		+ "  if(!ds_"+gpio+"->reset()) return false;\r\n"
    		+ "  ds_"+gpio+"->write(0x55);                   // MATCH ROM\r\n"
    		+ "  uint8_t *a = (uint8_t *)&addr;\r\n"
    		+ "  for(int i = 0; i < 8; i++) ds_"+gpio+"->write(a[i]);\r\n"
    		+ "  ds_"+gpio+"->write(0x4E);                   // WRITE SCRATCHPAD\r\n"
    		+ "  ds_"+gpio+"->write(0x00);                   // TH (Alarm-High, hier egal)\r\n"
    		+ "  ds_"+gpio+"->write(0x00);                   // TL (Alarm-Low, hier egal)\r\n"
    		+ "  ds_"+gpio+"->write(cfg);                    // Config-Byte mit R1/R0\r\n"
    		+ "\r\n"
    		+ "  // 3) Ins EEPROM kopieren\r\n"
    		+ "  if(!ds_"+gpio+"->reset()) return false;\r\n"
    		+ "  ds_"+gpio+"->write(0x55);                   // MATCH ROM\r\n"
    		+ "  for(int i = 0; i < 8; i++) ds_"+gpio+"->write(a[i]);\r\n"
    		+ "  ds_"+gpio+"->write(0x48);                   // COPY SCRATCHPAD\r\n"
    		+ "  delay(10);                        // Copy dauert ein paar ms\r\n"
    		+ "\r\n"
    		+ "  return true;\r\n"
    		+ "}";
   	translator.addDefinitionCommand(Dis);
    String Set = "ds_"+gpio+" = new OneWire32("+gpio+");\r\n"
    		   + "DS18B20_InitESP32_"+gpio+"("+res+");\n";
	translator.addSetupCommand(Set);
   	
	// Code von der Mainfunktion
	ret = "DS18B20_getESP32_"+gpio+"("+index+","+mode+")"; 
    return codePrefix + ret + codeSuffix;
  }
}