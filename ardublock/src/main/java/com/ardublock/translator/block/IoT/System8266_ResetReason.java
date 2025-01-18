package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class System8266_ResetReason extends TranslatorBlock
{

  public System8266_ResetReason (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Deklarationen hinzuf�gen
    // translator.addDefinitionCommand("ADC_MODE(ADC_VCC); // analogInput is uses for VCC measure");   		   	
    
   // Code von der Mainfunktion
	ret = "(ESP.getResetInfoPtr()->reason)"; 
    return codePrefix + ret + codeSuffix;
  }
}