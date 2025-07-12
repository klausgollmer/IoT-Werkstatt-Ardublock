package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_OneWire_Request32 extends TranslatorBlock
{

  public ExtSen_OneWire_Request32 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
	  
  TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
  String gpio = translatorBlock.toCode();  
	  
    String ret;
    // Code von der Mainfunktion
	ret = "ds_"+gpio+"->request();"; 
    return codePrefix + ret + codeSuffix;
  }
}