package com.ardublock.translator.block.IoT;

import java.util.ResourceBundle;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.VariableDigitalBlock;
import com.ardublock.translator.block.VariablePolyBlock;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class System8266_RTCfromEEPROM  extends TranslatorBlock {
	private static ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	public System8266_RTCfromEEPROM (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
        //translator.addSetupCommand("Serial.begin(115200);");
		
        String extern="extern \"C\" {  // zur Nutzung der speziellen ESP-Befehle wie Deep Sleep\n"
			     + "   #include \"user_interface.h\"\n"
			     +"}\n";
	    translator.addDefinitionCommand(extern);
	    translator.addHeaderFile("EEPROM.h");
		
	    String RTCStruct ="//--------------------------------  RTC - Memory \n" 
				  + "// Daten in dieser Structur werden im gespeichert und überleben Reset und deep-sleep \n"
				  + "struct {\n" + 
					"  uint32_t crc32;\n" + 
					"  float data[127];\n" + 
					"} RTCData;";
		translator.addDefinitionCommand(RTCStruct);



        String EEPROM ="// -------------------------- read/write RTCMemory to EEPROM\n" + 
        		"void writeEEPROM() {\n" + 
        		"  EEPROM.begin(sizeof(RTCData));\n" + 
        		"  EEPROM.put(0,RTCData);\n" + 
        		"  EEPROM.commit();\n" + 
        		"  EEPROM.end();\n" + 
        		"};\n" + 
        		"\n" + 
        		"void readEEPROM() {\n" + 
        		"  EEPROM.begin(sizeof(RTCData));\n" + 
        		"  EEPROM.get(0,RTCData);\n" + 
        		"  EEPROM.commit();\n" + 
        		"  EEPROM.end();\n" + 
        		"};\n" + 
        		"";
		translator.addDefinitionCommand(EEPROM);
		
		//translator.setRTCVarProgram(true);

		String Code = "// ------  Load RTC-Structure from EEPROM\n"+
			     "  readEEPROM(); \n"; 
		
        return Code;
	 	}
}

