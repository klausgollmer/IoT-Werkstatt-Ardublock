package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTButtonPress extends TranslatorBlock
{

  public IoTButtonPress (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

	
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
	    translator.addSetupCommand("pinMode(2,INPUT_PULLUP);// Button Press \n ");

		String ret = "digitalRead(2)==LOW";
		

		return codePrefix + ret + codeSuffix;
	}
}