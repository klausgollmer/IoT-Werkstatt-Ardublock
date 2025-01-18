package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_HX711GetScale extends TranslatorBlock
{

  public ExtSen_HX711GetScale (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
  
    // Header hinzuf�gen
    translator.addHeaderFile("HX711.h");
    translator.addDefinitionCommand("HX711 scale; //MIT License, Library: https://github.com/RobTillaart/HX711,  Rob Tillaart \\n");

    // Code von der Mainfunktion
	ret = "scale.get_scale()";
	return codePrefix + ret + codeSuffix;
  }
}