package com.ardublock.translator.block.IoT;

import java.util.ResourceBundle;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.VariableDigitalBlock;
import com.ardublock.translator.block.VariablePolyBlock;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Ctrl_SwitchcaseBlock  extends TranslatorBlock {
	private static ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	public Ctrl_SwitchcaseBlock (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		String ret = "switch(";
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		ret = ret + translatorBlock.toCode();
		ret = ret + ")\n{\n";
		
		translatorBlock = this.getTranslatorBlockAtSocket(1);
		if (translatorBlock != null) {
			ret = ret + "case ";
			ret = ret + translatorBlock.toCode()+ ":";
			translatorBlock = getTranslatorBlockAtSocket(2);
			while (translatorBlock != null)
			{
				ret = ret + translatorBlock.toCode();
				translatorBlock = translatorBlock.nextTranslatorBlock();
			}
			ret = ret + "\nbreak;";
		}

		translatorBlock = this.getTranslatorBlockAtSocket(3);
		if (translatorBlock != null) {
			ret = ret + "case ";
			ret = ret + translatorBlock.toCode()+ ":";
			translatorBlock = getTranslatorBlockAtSocket(4);
			while (translatorBlock != null)
			{
				ret = ret + translatorBlock.toCode();
				translatorBlock = translatorBlock.nextTranslatorBlock();
			}
			ret = ret + "\nbreak;";
		}
	
		translatorBlock = this.getTranslatorBlockAtSocket(5);
		if (translatorBlock != null) {
			ret = ret + "case ";
			ret = ret + translatorBlock.toCode()+ ":";
			translatorBlock = getTranslatorBlockAtSocket(6);
			while (translatorBlock != null)
			{
				ret = ret + translatorBlock.toCode();
				translatorBlock = translatorBlock.nextTranslatorBlock();
			}
			ret = ret + "\nbreak;";
		}

		translatorBlock = this.getTranslatorBlockAtSocket(7);
		if (translatorBlock != null) {
			ret = ret + "case ";
			ret = ret + translatorBlock.toCode()+ ":";
			translatorBlock = getTranslatorBlockAtSocket(8);
			while (translatorBlock != null)
			{
				ret = ret + translatorBlock.toCode();
				translatorBlock = translatorBlock.nextTranslatorBlock();
			}
			ret = ret + "\nbreak;";
		}
		ret = ret + "default:";
		translatorBlock = getTranslatorBlockAtSocket(9);
		while (translatorBlock != null)
		{
			ret = ret + translatorBlock.toCode();
			translatorBlock = translatorBlock.nextTranslatorBlock();
		}
		ret = ret + "\nbreak;";

		
		
		ret = ret + "}\n";
		return ret;
 	}
}

