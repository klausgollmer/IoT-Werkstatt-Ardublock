package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Ctrl_CCode_global  extends TranslatorBlock {

	public Ctrl_CCode_global (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		//// now in init translator.addHeaderFile("Wire.h");
		//translator.addHeaderFile("rgb_lcd.h");
		//translator.addDefinitionCommand(Def);
	
		
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String code = translatorBlock.toCode();
		code = code.substring(1, code.length() - 1) + "\n";	
		translator.addDefinitionCommand(code);
		return codePrefix + codeSuffix;
		
	}
}

