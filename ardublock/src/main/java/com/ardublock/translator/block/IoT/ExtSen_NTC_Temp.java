package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_NTC_Temp extends TranslatorBlock
{

  public ExtSen_NTC_Temp (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String R_ntc = translatorBlock.toCode();
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String R_25 = translatorBlock.toCode();
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
    String beta = translatorBlock.toCode();

    	
    String Dis="// NTC Temperature \r\n"
    		+ "float NTC_Temp(float r_ntc,float R_NTC_25,float BETA) {\n "
    		+ "  // Beta-Gleichung → Celsius\r\n"
    		+ "  float inv_T = (1.0f/298.15f) + log(r_ntc / R_NTC_25) / BETA;\r\n"
    		+ "  float tempC = (1.0f / inv_T) - 273.15f;\r\n"
    		+ "  return tempC;\n"
    		+ "} \r\n";
   	translator.addDefinitionCommand(Dis);
    
   	
	// Code von der Mainfunktion
	ret = "NTC_Temp("+R_ntc+","+R_25+","+beta+")"; 
	
   
    return codePrefix + ret + codeSuffix;
  }
}