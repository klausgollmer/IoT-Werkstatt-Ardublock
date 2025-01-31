package com.ardublock.translator.block.IoT;

import java.util.ResourceBundle;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.VariableDigitalBlock;
import com.ardublock.translator.block.VariablePolyBlock;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class System8266_RTCReadVar  extends TranslatorBlock {
	private static ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	public System8266_RTCReadVar (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		//translator.addSetupCommand("Serial.begin(115200);");
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

		String Vali = "// RTC-Memory has 512 Bytes, 128 Slots, minus one Slot for CRC\n" + 
				"uint8_t RTCvalidateSlot(float slot) {\n" + 
				"  uint8_t index = 0;\n" + 
				"  if ((round(slot) < 0) || (round(slot) > 126)) {\n" + 
				"    IOTW_PRINTLN(\"Sorry RTC-Memory allows for 127 Slots\");\n" + 
				"    IOTW_PRINTLN(String(\"slot \") + String(slot) + String(\" not valid\"));\n" + 
				"  } else index = round(slot);\n" + 
				"  return(index);  \n" + 
				"}\n"; 
		translator.addDefinitionCommand(Vali);

		
//      "    ESP.rtcUserMemoryRead(0, (uint32_t*) &RTCData, sizeof(RTCData)); \n"+	
		
		String Check = "// Überprüfe Checksumme RTC-Variable\n"+
	            "int RTCVarCheck() {" +
				"    uint32_t crcOfData = RTCcalculateCRC32((uint8_t*) &RTCData.data[0], sizeof(RTCData.data));\n" + 
				"    if (crcOfData != RTCData.crc32) {\n" + 
				"      IOTW_PRINTLN(\"doesn't match CRC32 of RTCVar, Data is probably invalid!\");\n" + 
				"    return 0;\n"+
				"    } else {\n" + 
				"    return 1;\n"+
				"    }\n" + 
				"  }\n" + 
				"";
		translator.addDefinitionCommand(Check);
		
		translator.setRTCVarProgram(true);
		
		
		
		String slot;
		TranslatorBlock tb = this.getRequiredTranslatorBlockAtSocket(0);
		slot = tb.toCode();	
		String Code = "RTCData.data[RTCvalidateSlot("+slot+")]";
	    return codePrefix + Code + codeSuffix;
        }
}

