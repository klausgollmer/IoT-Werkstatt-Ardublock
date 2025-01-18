package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Print_CatString extends TranslatorBlock
{

  public Print_CatString (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
   
  }

  @Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String s = translatorBlock.toCode();
		
		
		translatorBlock = this.getTranslatorBlockAtSocket(1);
		if (translatorBlock!=null)
	  	       s += "+String("+translatorBlock.toCode()+")";
		return codePrefix + s + codeSuffix;
		
	}
}