package com.ardublock.translator.block.IoT;

import java.util.ResourceBundle;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.VariableDigitalBlock;
import com.ardublock.translator.block.VariablePolyBlock;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Var_PredefReadNum  extends TranslatorBlock {
	private static ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	public Var_PredefReadNum (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		String Symbol = label;
		Symbol = Symbol.toUpperCase();
		Symbol.replaceAll("[^a-zA-Z0-9_]", "");
	    return codePrefix + Symbol + codeSuffix;
        }
}

