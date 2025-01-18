package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Ctrl_SubRefReturnBlock extends TranslatorBlock
{

	public Ctrl_SubRefReturnBlock(Long blockId, Translator translator,
			String codePrefix, String codeSuffix, String label) {
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		String subroutineName = label.trim()+"_gen";
		if (!translator.containFunctionName(subroutineName))
		{
		  throw new SubroutineNotDeclaredException(blockId);
		}
		return subroutineName + "()";
	}

}