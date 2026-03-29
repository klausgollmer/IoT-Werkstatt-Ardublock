package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Audio_BuzzerSong extends TranslatorBlock
{

  public Audio_BuzzerSong (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
	  TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	  String song = translatorBlock.toCode();
	  translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	  String dur = translatorBlock.toCode();
//	  translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
//	  String vol = translatorBlock.toCode();
	  
	  translator.addHeaderFile("IoTW_Tone.h");
	  String ret = ""
			  +    "IoTW_Tone_PlaySong("+song+","+dur+",100);\n";
	  
 	  return codePrefix + ret + codeSuffix;
  }
}