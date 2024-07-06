package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTMEANsmooth extends TranslatorBlock
{

  public IoTMEANsmooth (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
    
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String count = translatorBlock.toCode();

    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
    String wait = translatorBlock.toCode();
   	
    String sub_name = "CalcMean_"+code.replaceAll("[^A-Za-z0-9]", "");
    
    
    String sub= "/*------------------------ Average */\n"
       	+   "float "+sub_name+"(int count, int wait) {\r\n" + 
    		"   float mean_val = " + code + "; \r\n"+ 
    		"   for (int i = 2; i<=count; i++) { \r\n" +
    		"      delay("+wait+");    \r\n"+
            "      mean_val = mean_val + " + code + "; \r\n"+
    		"   } \r\n"+
    		"   return (mean_val/count); \r\n" + 
    		"}\r\n" ; 
    
    
    
 	translator.addDefinitionCommand(sub);
 	 
    
    // Code von der Mainfunktion
    String ret = sub_name+"("+count+","+wait+")";
    return codePrefix + ret + codeSuffix;
  }
}