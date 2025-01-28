package com.ardublock.translator.block.IoT;

import java.util.ResourceBundle;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.VariableDigitalBlock;
import com.ardublock.translator.block.VariablePolyBlock;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Var_PredefSetNum  extends TranslatorBlock {
	private static ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	public Var_PredefSetNum (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
		//TODO take out special character
		String Symbol = label;
		TranslatorBlock tb = this.getRequiredTranslatorBlockAtSocket(0);
		String Value = tb.toCode();
		Symbol = Symbol.toUpperCase();
		Symbol.replaceAll("[^a-zA-Z0-9_]", "");
		translator.addDefinitionCommand("#define "+Symbol+" "+ Value);
		return codePrefix + codeSuffix;
	 	}
}

