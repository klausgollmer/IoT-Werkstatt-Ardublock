package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SleepBlockDeep extends TranslatorBlock
{

	public SleepBlockDeep(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
	String extern="extern \"C\" {  // zur Nutzung der speziellen ESP-Befehle wie Deep Sleep\n"
			     + "   #include \"user_interface.h\"\n"
			     +"}\n";
	translator.addDefinitionCommand(extern);
	translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
	translator.addHeaderFile("#define USE_DEEPSLEEP");
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	String Delay_ms = translatorBlock.toCode(); 
    String ret = "";
	if (translator.isRTCVarProgram()) {
		  String RTCStruct ="//--------------------------------  RTC - Memory \n" 
				  + "// Daten in dieser Structur werden im gespeichert und überleben Reset und deep-sleep \n"
				  + "struct {\n" + 
					"  uint32_t crc32;\n" + 
					"  float data[127];\n" + 
					"} RTCData;";
		translator.addDefinitionCommand(RTCStruct);

		
		
		String CRC = "// Berechne CRC-Prüfsumme für RTC-RAM \n"
			+   "uint32_t RTCcalculateCRC32(const uint8_t *data, size_t length) {\n" + 
				"  uint32_t crc = 0xffffffff;\n" + 
				"  while (length--) {\n" + 
				"    uint8_t c = *data++;\n" + 
				"    for (uint32_t i = 0x80; i > 0; i >>= 1) {\n" + 
				"      bool bit = crc & 0x80000000;\n" + 
				"      if (c & i) {\n" + 
				"        bit = !bit;\n" + 
				"      }\n" + 
				"      crc <<= 1;\n" + 
				"      if (bit) {\n" + 
				"        crc ^= 0x04c11db7;\n" + 
				"      }\n" + 
				"    }\n" + 
				"  }\n" + 
				"  return crc;\n" + 
				"}";
		translator.addDefinitionCommand(CRC);
	
		String Check = "// Überprüfe Checksumme RTC-Memory\n"+
	            "int RTCLoadCheck() {" +
				"    uint32_t crcOfData = RTCcalculateCRC32((uint8_t*) &RTCData.data[0], sizeof(RTCData.data));\n" + 
				"    if (crcOfData != RTCData.crc32) {\n" + 
				"      Serial.println(\"CRC32 in RTC memory doesn't match CRC32 of data. Data is probably invalid!\");\n" + 
				"    return 0;\n"+
				"    } else {\n" + 
				"    return 1;\n"+
				"    }\n" + 
				"  }\n" + 
				"";
		translator.addDefinitionCommand(Check);
	
	}
		
	

	//translator.setDeepSleepProgram(true);
	
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String wake = translatorBlock.toCode();
		//String on = "t"+wake.charAt(0);
		//String on = "t"+wake.charAt(0);
		
		
		if (translator.isLORAProgram()) {
			translator.addHeaderFile("#define LORA_DEEPSLEEP");
			ret = "if (os_queryTimeCriticalJobs(ms2osticks("+Delay_ms+"))) { \n" + 
				  "    Serial.println(\"busywaiting for criticalJobs\");\n" + 
				  "    while (os_queryTimeCriticalJobs(ms2osticks("+Delay_ms+"))) { \n" + 
				  "     yield();  \n" + 
				  "     os_runloop_once();\n" + 
				  "    }\n" + 
				  "  }\n";
			
			if (translator.isRTCVarProgram()) {
  			    ret += "SaveLMICToRTC_ESP8266("+Delay_ms+"/1000); // Save LMIC-State \n ";
		    } else {
  			    ret += "SaveLMICToRTC_ESP8266("+Delay_ms+"/1000); // Save LMIC-State \n ";
		    }
		}
		
			
			
		
		if (wake.charAt(0)=='H') {
		   ret += "\tESP.deepSleep( ";
		   ret = ret + "(long)"+Delay_ms+"*1000UL";
		   ret = ret + ",WAKE_RF_DEFAULT);//Tiefschlaf, danach Reset und von vorn\n";
		} else {
		   ret += "WiFi.disconnect( true );delay(2); // Wifi Off \n";
		   ret += "\tESP.deepSleep( ";
		   ret = ret + "(long)"+Delay_ms+"*1000UL";
		   ret = ret + ",WAKE_RF_DISABLED);//Tiefschlaf, danach Reset und von vorn\n";
		}			
		
		return ret;
	}
	}



