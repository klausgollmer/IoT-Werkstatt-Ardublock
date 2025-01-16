package com.ardublock.translator.block;

import com.ardublock.translator.Translator;

public class VariableNumberBlock extends TranslatorBlock
{
	public VariableNumberBlock(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode()
	{
		String internalVariableName = translator.getNumberVariable(label);
		if (internalVariableName == null)
		{
			internalVariableName = translator.buildVariableName(label);

			if (internalVariableName.equals("LoRaWAN_Rx_Payload") ||
			    internalVariableName.equals("LoRaWAN_Rx_Port"))	{
				   // schon im Block definiert
			  } else { 
   			  translator.addNumberVariable(label, internalVariableName);
					
			  String TypeDef="volatile int " + internalVariableName + " = 0;";
						
			  TypeDef ="#if defined(ESP32) && defined(USE_DEEPSLEEP)\n" + 
				   	 "  RTC_DATA_ATTR " + TypeDef +"// store during sleep\n"+
					 "#else \n"+
					 "  " + TypeDef + "\n" +
  					 "#endif\n";
				
			  translator.addDefinitionCommand(TypeDef);
		    }
//	irq		translator.addDefinitionCommand("int " + internalVariableName + " = 0 ;");
//			translator.addSetupCommand(internalVariableName + " = 0;");
		}
		return codePrefix + internalVariableName + codeSuffix;
	}

}
