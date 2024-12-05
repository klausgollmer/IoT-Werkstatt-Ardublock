package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTHX711Get extends TranslatorBlock
{

  public IoTHX711Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
  
    // Header hinzuf�gen
    translator.addHeaderFile("HX711.h");
    translator.addDefinitionCommand("HX711 scale; //MIT License, Library: https://github.com/RobTillaart/HX711,  Rob Tillaart \\n");

	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String pins = translatorBlock.toCode();
    pins = pins.substring(1, pins.length()-1);
    translator.addSetupCommand("scale.begin("+pins+"); // Library: https://github.com/RobTillaart/HX711,  Rob Tillaart \n");

    // Code von der Mainfunktion
	ret = "scale.get_units(10)";
	return codePrefix + ret + codeSuffix;
  }
}