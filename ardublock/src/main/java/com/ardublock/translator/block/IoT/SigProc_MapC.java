package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SigProc_MapC extends TranslatorBlock
{

  public SigProc_MapC (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
	  
	float in_min,in_max,out_min,out_max;
	  
	  
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
    
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String in = translatorBlock.toCode();
    in = in.replace("\"", "").trim(); // Entfernt doppelte Anführungszeichen

    String[] parts = in.split(",");
    in_min = Float.parseFloat(parts[0].trim());
    in_max = Float.parseFloat(parts[1].trim());
    
    
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
    String out = translatorBlock.toCode();
    out = out.replace("\"", "").trim(); // Entfernt doppelte Anführungszeichen

    parts = out.split(",");
    out_min = Float.parseFloat(parts[0].trim());
    out_max = Float.parseFloat(parts[1].trim());
   	
    
    
    
    String sub= "/*------------------------ map with constrain */\n"
       	+   "float mapC(float x, float in_min, float in_max, float out_min, float out_max) {\r\n"
       	+ "    float result = (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;\r\n"
       	+ "    return constrain(result, out_min, out_max);\r\n"
       	+ "}\r\n"
       	+ "" ; 
    
    
    
 	translator.addDefinitionCommand(sub);
 	 
    
    // Code von der Mainfunktion
    String ret = "mapC("+code+","+in_min+","+in_max+","+out_min+","+out_max+")";
    return codePrefix + ret + codeSuffix;
  }
}