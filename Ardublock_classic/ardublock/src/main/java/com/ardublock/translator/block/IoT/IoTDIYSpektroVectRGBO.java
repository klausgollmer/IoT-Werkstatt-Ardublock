package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTDIYSpektroVectRGBO extends TranslatorBlock
{

  public IoTDIYSpektroVectRGBO (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }


	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		String ret = "DIY_SpectroOctiSetup(-1,3);// Neopixel rgb"; 
		return codePrefix + ret + codeSuffix;
	}
}