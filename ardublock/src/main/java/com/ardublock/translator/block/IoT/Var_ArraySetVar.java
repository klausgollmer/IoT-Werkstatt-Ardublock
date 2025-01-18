package com.ardublock.translator.block.IoT;

import java.util.ResourceBundle;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.VariableDigitalBlock;
import com.ardublock.translator.block.VariablePolyBlock;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Var_ArraySetVar  extends TranslatorBlock {
	private static ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	public Var_ArraySetVar (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		//translator.addSetupCommand("Serial.begin(115200);");
		String ArrayStruct ="//--------------------------------  IoTDataArray for timeseries \n"
				+ "// Dimension IOTARRAYLEN, only the first 15 elements were displayed charlieplex matrix \n"
				+ "#define IOTARRAYLEN 64 \n"
				+ "#if defined(ESP32) && defined(USE_DEEPSLEEP)\n"
				+ "  RTC_DATA_ATTR float   IoTArrayData[IOTARRAYLEN];\n"
				+ "  RTC_DATA_ATTR uint8_t IoTArrayDataIndex     = 0;\n"
				+ "  RTC_DATA_ATTR uint8_t IoTArrayDataInitDone  = 0;\n"
				+ "#else\n"
				+ "  float   IoTArrayData[IOTARRAYLEN];\n"
				+ "  uint8_t IoTArrayDataIndex     = 0;\n"
				+ "  uint8_t IoTArrayDataInitDone  = 0;\n"
				+ "#endif\n"
				+ "";
		translator.addDefinitionCommand(ArrayStruct);

		String Vali = "// Array hat IOTARRAYLEN Einträge - Check Index zur Laufzeit \n" + 
				"int IoTArrayCheckIndex(float input) {\n" + 
				"  int index = 0;\n" + 
				"  if ((round(input) < 0) || (round(input) > (IOTARRAYLEN-1))) {\n" + 
				"    Serial.println(\"IoTArrayData index fehler\");\n" + 
				"    Serial.println(String(\"index \") + String(input) + String(\" not valid\"));\n" + 
				"  } else index = round(input);\n" + 
				"  return(index);  \n" + 
				"}\n"; 
		translator.addDefinitionCommand(Vali);
		
		String Setup = "if (!IoTArrayDataInitDone) {\n"
	    		+ "  IoTArrayDataInitDone = 1;\n"
	    		+ "  for (uint8_t i=0; i<IOTARRAYLEN; i++) {\n"
	    		+ "    IoTArrayData[i] = NAN;\n"
	    		+ "  }\n"
	    		+ "}";
        translator.addSetupCommand(Setup);
	
		
//      "    ESP.rtcUserMemoryRead(0, (uint32_t*) &RTCData, sizeof(RTCData)); \n"+	
		
		String slot,val;
		TranslatorBlock tb = this.getRequiredTranslatorBlockAtSocket(0);
		slot = tb.toCode();
		tb = this.getRequiredTranslatorBlockAtSocket(1);
		val = tb.toCode();
		String Code = "IoTArrayData[IoTArrayCheckIndex("+slot+")] = " + val+ ";";
	    return codePrefix + Code + codeSuffix;
	 	}
}

