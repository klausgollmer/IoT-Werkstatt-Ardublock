package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTEMAsmooth extends TranslatorBlock
{

  public IoTEMAsmooth (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
       
    String sub= "/*------------------------ Exponential Moving Average, variable time */\n"
    		+   "// http://www.eckner.com/papers/Algorithms%20for%20Unevenly%20Spaced%20Time%20Series.pdf \n"
       	+   "float EMAsmooth(float val, float tau, float& out, unsigned long& t_old) {\r\n" + 
    		"   unsigned long dt = (millis() - t_old); // time between measurements (ms)\r\n" + 
    		"   t_old = millis();\r\n" +
    		"   if (tau <= 0.1) tau = 0.1;    // tau in ms, must be a positive number \r\n"+
      		"   float w = exp(-(float)dt/tau);// dt in ms \r\n"+   		
    		"   out = out*w + val*(1.0-w);    // update filtered \r\n" +
    		"   return out;\r\n" + 
    		"}\r\n" ; 
    
    // Header hinzuf�gen
    // translator.addHeaderFile("MHZ19.h");
       
  
    
  	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
    
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String tau = translatorBlock.toCode();
   	
    
    int n = code.length();
    long hash=0;   
    for (int i=0;i<n;i++) {
    	hash=hash+code.charAt(i)*(i+1);
    }
    
    n = tau.length();
    for (int i=0;i<n;i++) {
    	hash=hash+tau.charAt(i)*(i+1);
    }    	

    String var_name;
    var_name=Long.toString(hash);

    String var="// global var moving average filter for "+code+"\n"+
       		  "float         out_EMA"+var_name+"= 0.0;\r\n" + 
    		  "unsigned long t_EMA"+var_name+"  = 0;\r\n";
    
    translator.addDefinitionCommand(var);
   	translator.addDefinitionCommand(sub);
 

    String Setup = "t_EMA"+var_name+"  = millis();  // Init EMA smooth \n"+
                   "out_EMA"+var_name+"= "+code+"; \n"; 
    translator.addSetupCommand(Setup);
    
    // Code von der Mainfunktion
    String ret = "EMAsmooth("+code+","+tau+"*1000.,out_EMA"+var_name+",t_EMA"+var_name+")";
    return codePrefix + ret + codeSuffix;
  }
}