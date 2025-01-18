package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class MINT_DIYSpektroVectWD1 extends TranslatorBlock
{

  public MINT_DIYSpektroVectWD1 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }


	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		String ret = "DIY_SpectroOctiSetup(15,1);// Pin15 monochrom"; 
		return codePrefix + ret + codeSuffix;
	}
}