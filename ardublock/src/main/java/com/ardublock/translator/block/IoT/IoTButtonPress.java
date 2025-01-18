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
	    translator.addSetupCommand("pinMode(IOTW_GPIO_ROTARY_BUTTON,INPUT_PULLUP);// Button Press \n ");

		String ret = "digitalRead(IOTW_GPIO_ROTARY_BUTTON)==LOW";
		

		return codePrefix + ret + codeSuffix;
	}
}