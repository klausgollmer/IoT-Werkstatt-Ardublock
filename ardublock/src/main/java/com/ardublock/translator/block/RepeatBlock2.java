package com.ardublock.translator.block;

import java.util.ResourceBundle;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class RepeatBlock2 extends TranslatorBlock
{

	private static ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	
	public RepeatBlock2(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		String varName="";//this.getRequiredTranslatorBlockAtSocket(0);
		String varNameDeclaration = "";
		TranslatorBlock teste = this.getRequiredTranslatorBlockAtSocket(0);
		
        boolean isGlobalVar = ((teste instanceof VariableNumberBlock || teste instanceof VariableNumberUnsignedLongBlock || teste instanceof VariableNumberDoubleBlock));
        if (!isGlobalVar) {
        	String internalVariableName = translator.buildVariableName(teste.toCode()); 	
   			translator.addLocalNumberVariable(label, internalVariableName);
   			varNameDeclaration = "int ";
        }
		varName=teste.toCode();
		teste = this.getRequiredTranslatorBlockAtSocket(1);
	    String From = teste.toCode();
	    teste = this.getRequiredTranslatorBlockAtSocket(2);
	    String To = teste.toCode();
      		
		
		String ret = "for (" + varNameDeclaration+varName + "="+From+";" + varName + "<= ( ";
		ret = ret + To;
		ret = ret + " ); " + varName + "="+ varName + "+1)\n{\n";
		
		
		TranslatorBlock translatorBlock = getTranslatorBlockAtSocket(3);
		while (translatorBlock != null)
		{
			ret = ret + translatorBlock.toCode();
			translatorBlock = translatorBlock.nextTranslatorBlock();
		}
		
		ret = ret + "yield();}\n";
		return ret;
	}

}
