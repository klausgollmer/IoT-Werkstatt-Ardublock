package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTBSECVectorGasRaw extends TranslatorBlock
{

  public IoTBSECVectorGasRaw (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }


	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		String ret = "iaqSensor.gasResistance";
		return codePrefix + ret + codeSuffix;
	}
}