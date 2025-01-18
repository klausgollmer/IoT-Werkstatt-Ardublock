package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Ctrl_SubReturnBlock extends TranslatorBlock
{

	public Ctrl_SubReturnBlock(Long blockId, Translator translator,
			String codePrefix, String codeSuffix, String label) {
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		//String subroutineName = label.trim();
		String subroutineName = label.trim()+"_gen";
		String ret;
		ret = "int " + subroutineName + "()\n{\n";
		translator.clearLocalNumberVariable();
		TranslatorBlock translatorBlock_return = getTranslatorBlockAtSocket(0);
		TranslatorBlock translatorBlock = getTranslatorBlockAtSocket(1);
		while (translatorBlock != null)
		{
			ret = ret + translatorBlock.toCode();
			translatorBlock = translatorBlock.nextTranslatorBlock();
		}
		ret = ret + "return "+ translatorBlock_return.toCode() +";\n }\n";
		return ret;
	}
}