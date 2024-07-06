package com.ardublock.translator.block.IoT;

import java.util.ResourceBundle;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.VariableDigitalBlock;
import com.ardublock.translator.block.VariablePolyBlock;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTArraySetVar  extends TranslatorBlock {
	private static ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	public IoTArraySetVar (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addSetupCommand("Serial.begin(115200);");
		String ArrayStruct ="//--------------------------------  DatenArray \n" 
				  + "// Felddimension ALEN, nur die ersten 15 Daten in diesem Feld werden auf der LED Matrix angezeigt \n"
	    		  + "#define ALEN 100 \n"
	    		  + "float ArrayData[ALEN];\n";

		translator.addDefinitionCommand(ArrayStruct);


		String Vali = "// Array hat ALEN Einträge - Check Index zur Laufzeit \n" + 
				"int CheckIndex(float input) {\n" + 
				"  int index = 0;\n" + 
				"  if ((round(input) < 0) || (round(input) > (ALEN-1))) {\n" + 
				"    Serial.println(\"Array index fehler\");\n" + 
				"    Serial.println(String(\"index \") + String(input) + String(\" not valid\"));\n" + 
				"  } else index = round(input);\n" + 
				"  return(index);  \n" + 
				"}\n"; 
		translator.addDefinitionCommand(Vali);
		
//      "    ESP.rtcUserMemoryRead(0, (uint32_t*) &RTCData, sizeof(RTCData)); \n"+	
		
		String slot,val;
		TranslatorBlock tb = this.getRequiredTranslatorBlockAtSocket(0);
		slot = tb.toCode();
		tb = this.getRequiredTranslatorBlockAtSocket(1);
		val = tb.toCode();
		String Code = "ArrayData[CheckIndex("+slot+")] = " + val+ ";";
	    return codePrefix + Code + codeSuffix;
	 	}
}

