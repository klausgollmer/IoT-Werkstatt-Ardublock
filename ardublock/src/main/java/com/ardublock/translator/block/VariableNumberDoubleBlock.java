package com.ardublock.translator.block;

import com.ardublock.translator.Translator;

public class VariableNumberDoubleBlock extends TranslatorBlock
{
  public VariableNumberDoubleBlock(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
      translator.addNumberVariable(label, internalVariableName);
// irq      translator.addDefinitionCommand("float " + internalVariableName + " = 0.0 ;");
//      translator.addDefinitionCommand("volatile float " + internalVariableName + " = 0.0 ;");
    String TypeDef="volatile float " + internalVariableName + " = 0;\n";
		
		TypeDef ="#if defined(ESP32) && defined(USE_DEEPSLEEP)\n" + 
				 "  RTC_DATA_ATTR " + TypeDef +"// store during sleep\n"+
				 "#else \n"+
				 "  " + TypeDef +
				 "#endif\n";
			
		translator.addDefinitionCommand(TypeDef);
//      translator.addSetupCommand(internalVariableName + " = 0;");
    }
    return codePrefix + internalVariableName + codeSuffix;
  }

}