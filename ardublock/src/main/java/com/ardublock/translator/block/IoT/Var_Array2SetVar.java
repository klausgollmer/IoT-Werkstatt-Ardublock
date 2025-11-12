package com.ardublock.translator.block.IoT;

import java.util.ResourceBundle;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.VariableDigitalBlock;
import com.ardublock.translator.block.VariablePolyBlock;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Var_Array2SetVar  extends TranslatorBlock {
	private static ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	public Var_Array2SetVar (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		//translator.addSetupCommand("Serial.begin(115200);");
		String ArrayStruct ="//--------------------------------  IoTDataArray2 \n"
				+ "// Dimension IOTW_ARRAYLEN\n"
				+ "#if defined(ESP32) && defined(IOTW_USE_DEEPSLEEP)\n"
				+ "  RTC_DATA_ATTR float   IoTArrayData2[IOTW_ARRAYLEN];\n"
				+ "  RTC_DATA_ATTR uint8_t IoTArrayDataInitDone2 = 0;\n"
				+ "#else\n"
				+ "  float   IoTArrayData2[IOTW_ARRAYLEN];\n"
				+ "  uint8_t IoTArrayDataInitDone2 = 0;\n"
				+ "#endif\n"
				+ "";
		translator.addDefinitionCommand(ArrayStruct);

		String Vali = "// Array hat IOTW_ARRAYLEN Einträge - Check Index zur Laufzeit \n" + 
				"int IoTArrayCheckIndex(float input) {\n" + 
				"  int index = 0;\n" + 
				"  if ((round(input) < 0) || (round(input) > (IOTW_ARRAYLEN-1))) {\n" + 
				"    IOTW_PRINTLN(\"IoTArrayData index fehler\");\n" + 
				"    IOTW_PRINTLN(String(\"index \") + String(input) + String(\" not valid\"));\n" + 
				"  } else index = round(input);\n" + 
				"  return(index);  \n" + 
				"}\n"; 
		translator.addDefinitionCommand(Vali);
		
		String Setup = "if (!IoTArrayDataInitDone2) {\n"
	    		+ "  IoTArrayDataInitDone2 = 1;\n"
	    		+ "  for (uint8_t i=0; i<IOTW_ARRAYLEN; i++) {\n"
	    		+ "    IoTArrayData2[i] = NAN;\n"
	    		+ "  }\n"
	    		+ "}";
        translator.addSetupCommand(Setup);
	
		
//      "    ESP.rtcUserMemoryRead(0, (uint32_t*) &RTCData, sizeof(RTCData)); \n"+	
		
		String slot,val;
		TranslatorBlock tb = this.getRequiredTranslatorBlockAtSocket(0);
		slot = tb.toCode();
		tb = this.getRequiredTranslatorBlockAtSocket(1);
		val = tb.toCode();
		String Code = "IoTArrayData2[IoTArrayCheckIndex("+slot+")] = " + val+ ";";
	    return codePrefix + Code + codeSuffix;
	 	}
}

