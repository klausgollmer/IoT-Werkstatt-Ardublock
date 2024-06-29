package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTDIYSpektroVectGRBD1 extends TranslatorBlock
{

  public IoTDIYSpektroVectGRBD1 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }


	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		String ret = "DIY_SpectroOctiSetup(-2,3);// -2=ws"; 
		return codePrefix + ret + codeSuffix;
	}
}