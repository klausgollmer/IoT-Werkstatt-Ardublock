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
	  
	  translator.addHeaderFile("IoTW_Tone.h");
	  
	  String Dis ="void PlaySong(int song, int dur) {\r\n"
	  		+ "  switch (song) {\r\n"
	  		+ "    case 0:HappyBirthday(dur);break;\r\n"
	  		+ "    case 1:Song1(dur);break;\r\n"
	  		+ "    case 2:Song2(dur);break;\r\n"
	  		+ "    case 3:Song3(dur);break;\r\n"
	  		+ "    case 4:Song4(dur);break;\r\n"
	  		+ "    case 5:Song5(dur);break;\r\n"
	  		+ "  }\r\n"
	  		+ "}";
      translator.addDefinitionCommand(Dis);
	  
     

		String ret = "PlaySong("+song+","+dur+");\n";
		

		return codePrefix + ret + codeSuffix;
  }
}