package com.ardublock.translator.block;

import com.ardublock.translator.Translator;

public class VariableDigitalBlock extends TranslatorBlock
{
	public VariableDigitalBlock(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode()
	{
		String internalVariableName = translator.getBooleanVariable(label);
		if (internalVariableName == null)
		{
			internalVariableName = translator.buildVariableName(label);
			translator.addBooleanVariable(label, internalVariableName);
//			translator.addDefinitionCommand("bool " + internalVariableName + "= false ;");
			String TypeDef="volatile bool " + internalVariableName + " = false;";
			
			TypeDef ="#if defined(ESP32) && defined(IOTW_USE_DEEPSLEEP)\n" + 
					 "  RTC_DATA_ATTR " + TypeDef +" // store during sleep\n"+
					 "#else \n"+
					 "  " + TypeDef + "\n"+
  					 "#endif\n";
				
			translator.addDefinitionCommand(TypeDef);

//			translator.addDefinitionCommand("volatile bool " + internalVariableName + "= false ;");
//			translator.addSetupCommand(internalVariableName + " = false;");
		}
		//String ret = " ( " + internalVariableName + " ? true : false )";
		String ret = internalVariableName;
		return codePrefix + ret + codeSuffix;
	}

}
