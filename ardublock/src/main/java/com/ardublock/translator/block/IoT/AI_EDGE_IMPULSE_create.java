package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class AI_EDGE_IMPULSE_create  extends TranslatorBlock {

	public AI_EDGE_IMPULSE_create (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		translator.addHeaderFile("IoTW_Edge_Impulse.h");

/*
		String EI_Def ="// ---- EDGE AI data \n" + 
				"int     EI_NumSens=0,EI_Index=0;\n" + 
				"float   EI_Datenfeld[EI_MAXPOINTS][EI_MAXSENSOR]; \n" +
				"int     AI_Datentyp[EI_MAXPOINTS];\n"+
				"String  EI_nameOfSensor[EI_MAXSENSOR];\n" + 
				"String  EI_unitOfSensor[EI_MAXSENSOR];\n" + 
				"";
			translator.addDefinitionCommand(EI_Def);
*/
		
		String s,s1,u1,s2,u2,s3,u3,s4,u4;
		int pos;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    s = translatorBlock.toCode();
 	    pos = s.indexOf(",");
        if (pos>=0) { 
          s1 = s.substring( 0, pos)+"\"";
   	      u1= "\""+s.substring(pos+1,s.length()-1)+"\"";
        } else {u1="\"\"";s1=s;}

		translatorBlock = this.getTranslatorBlockAtSocket(1);
	    s = translatorBlock.toCode();
 	    pos = s.indexOf(",");
        if (pos>=0) { 
          s2 = s.substring( 0, pos)+"\"";
   	      u2= "\""+s.substring(pos+1,s.length()-1)+"\"";
        } else {u2="\"\"";s2=s;}

        translatorBlock = this.getTranslatorBlockAtSocket(2);
	    s = translatorBlock.toCode();
 	    pos = s.indexOf(",");
        if (pos>=0) { 
          s3 = s.substring( 0, pos)+"\"";
   	      u3= "\""+s.substring(pos+1,s.length()-1)+"\"";
        } else {u3="\"\"";s3=s;}
        
        translatorBlock = this.getTranslatorBlockAtSocket(3);
   	    s = translatorBlock.toCode();
    	pos = s.indexOf(",");
        if (pos>=0) { 
          s4 = s.substring( 0, pos)+"\"";
      	  u4= "\""+s.substring(pos+1,s.length()-1)+"\"";
        } else {u4="\"\"";s4=s;}
           
        String EI_init = "  //  ---- EDGE IMPULSE Name an Unit\n" + 
        		"  EI_nameOfSensor[0]= "+s1+";\n" + 
        		"  EI_nameOfSensor[1]= "+s2+";\n" + 
        		"  EI_nameOfSensor[2]= "+s3+";\n" + 
        		"  EI_nameOfSensor[3]= "+s4+";\n" + 
        		"  EI_unitOfSensor[0] ="+u1+";\n" + 
        		"  EI_unitOfSensor[1] ="+u2+";\n" + 
        		"  EI_unitOfSensor[2] ="+u3+";\n" + 
        		"  EI_unitOfSensor[3] ="+u4+";\n" + 
        		"  EI_Index = 0;\n" + 
        		"";
         
        return codePrefix + EI_init + codeSuffix;
	 	}
}

