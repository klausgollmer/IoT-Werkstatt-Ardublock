package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Sen_ButtonWaitUntilPress extends TranslatorBlock
{

  public Sen_ButtonWaitUntilPress (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

	
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
	    translator.addSetupCommand("pinMode(IOTW_GPIO_ROTARY_BUTTON,INPUT_PULLUP);// Button Press \n ");

		String ret = "#if (IOTW_DEBUG_LEVEL >1)\n"
				+ "IOTW_PRINTLN(F(\"Wait for encoder button - please press\"));\n"
				+ "#endif\n"
				+ "while (digitalRead(IOTW_GPIO_ROTARY_BUTTON)==LOW) delay(10); // wait not pressed \n"
				+ "   while (digitalRead(IOTW_GPIO_ROTARY_BUTTON)) delay(10); // wait until pressed \n";
		

		return codePrefix + ret + codeSuffix;
	}
}