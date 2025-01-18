package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SigProc_DosisIntegral extends TranslatorBlock
{

  public SigProc_DosisIntegral (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
       
    String sub= "/*------------------------ integration formula */\n"
    		+ "float DosisIntegral(float val, int res, float& sum, unsigned long& t_old, float& val_old) {\r\n" + 
    		"  unsigned long dt = (millis() - t_old); // time between measurements (ms)\r\n" + 
    		"  t_old = millis();\r\n" + 
    		"  if (res >= 1) \r\n" + 
    		"     sum = 0;\r\n" + 
    		"   else\r\n" + 
    		"     sum = sum + (float)dt*(val_old+val)/7200e3; // integral, trapz, time in h\r\n" + 
    		"  val_old = val;\r\n" + 
    		"  return sum;\r\n" + 
    		"}\r\n" + 
    		"";
    
    // Header hinzuf�gen
    // translator.addHeaderFile("MHZ19.h");
       
  
    
  	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
    
    int n = code.length();
    long hash=0;   
    for (int i=0;i<n;i++) {
    	hash=hash+code.charAt(i)*(i+1);
    }    	
    String var_name;
    var_name=Long.toString(hash);

    String var="// global var DosisIntegral for "+code+"\n"
    		+ "float         val_Dosis"+var_name+"= 0.0;\r\n" + 
       		  "float         sum_Dosis"+var_name+"= 0.0;\r\n" + 
    		  "unsigned long t_Dosis"+var_name+"  = 0;\r\n";
    
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String reset = translatorBlock.toCode();
   	translator.addDefinitionCommand(var);
   	translator.addDefinitionCommand(sub);
 

    String Setup = "t_Dosis"+var_name+"  = millis();  // Init DosisIntegral \n"+
                   "sum_Dosis"+var_name+"= 0.0; \n"+
    		       "val_Dosis"+var_name+"= "+code+"; \n"; 
    translator.addSetupCommand(Setup);

    
    // Code von der Mainfunktion
    String ret = "DosisIntegral("+code+","+reset+",sum_Dosis"+var_name+",t_Dosis"+var_name+",val_Dosis"+var_name+")";
    return codePrefix + ret + codeSuffix;
  }
}