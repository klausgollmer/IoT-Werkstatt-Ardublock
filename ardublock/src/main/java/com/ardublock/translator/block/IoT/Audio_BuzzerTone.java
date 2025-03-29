package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Audio_BuzzerTone extends TranslatorBlock
{

  public Audio_BuzzerTone (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
	  String ret;
      
	  TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	  String f = translatorBlock.toCode();
	  translatorBlock = this.getTranslatorBlockAtSocket(1);
	  String dur = translatorBlock.toCode();
	  if (dur == null) dur = "100000000";
	  translator.addHeaderFile("IoTW_Tone.h");
	  ret = "BuzzerTone("+f+","+dur+");\n";
	  return codePrefix + ret + codeSuffix;
  }
}