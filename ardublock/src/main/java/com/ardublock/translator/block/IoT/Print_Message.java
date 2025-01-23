package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Print_Message extends TranslatorBlock
{

  public Print_Message (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
   
  }

  @Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
	//TODO take out special character
			String ret;
			ret = label;
			ret = ret.replace("\\u0020", " ");
			ret = ret.replaceAll("\\\\", "\\\\\\\\");
			ret = ret.replaceAll("\"", "\\\\\"");
			//A way to have 'space' at start or end of message
			ret = ret.replaceAll("<&space>", " ");
			//A way to have other block settings applied but no message sent
			ret = ret.replaceAll("<&nothing>", "");
			// A way to add \t to messages
			ret = ret.replaceAll("<&tab>", "\\\\t");
			ret = "\""+ ret + "\"";
			TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0);
			if (translatorBlock != null)
			{
				ret = ret + "+String(" + translatorBlock.toCode()+")";
			}
			return ret;
		
	}
}