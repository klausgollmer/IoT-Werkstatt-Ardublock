package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class BLE_sendPhyphox  extends TranslatorBlock {

	public BLE_sendPhyphox (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		int pos,cnt=2;
		String s,u1,u2,u3,n1,n2,n3;
		
		translator.addHeaderFile("#if defined(ESP32)\n #include <phyphoxBle.h> \n#endif\n");		
        String Lic = "// Phyphox-Arduino BLE Library, LGPL-3.0, GPL-3.0 licenses \n"
        		   + "// https://github.com/phyphox/phyphox-arduino/tree/master?tab=LGPL-3.0-1-ov-file\n"
        		   + "PhyphoxBleExperiment myexp;\n";
        translator.addDefinitionCommand(Lic);
        
		String Server, wert1="", wert2="", wert3="",setup, ret;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    Server = translatorBlock.toCode();

	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    s = translatorBlock.toCode();
 	    pos = s.indexOf(",");
         if (pos>=0) { 
          n1 = s.substring( 0, pos)+"\"";
   	      u1= "\""+s.substring(pos+1,s.length()-1)+"\"";
        } else {u1="\"\"";n1=s;}

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    wert1 = translatorBlock.toCode();
	    String Var = "float phyphoxWert1="+wert1+";\n";
	    String List = "phyphoxWert1";
        String Graph = "  // ----- Graph 1 -----\n"
        		+ "  PhyphoxBleExperiment::Graph g1;\n"
        		+ "  g1.setLabel("+n1+");\n"
        		+ "  g1.setLabelX(\"t\");\n"
        		+ "  g1.setUnitX(\"s\");\n"
        		+ "  g1.setLabelY(\"\");\n"
        		+ "  g1.setUnitY("+u1+");\n"
        		+ "  g1.setChannel(0, 1);\n"
        		+ "  v.addElement(g1);\n"
        		+ "";
	
        translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    s = translatorBlock.toCode();
 	    pos = s.indexOf(",");
        if (pos>=0) { 
          n2 = s.substring( 0, pos)+"\"";
   	      u2= "\""+s.substring(pos+1,s.length()-1)+"\"";
        } else {u2="\"\"";n2=s;}

        translatorBlock = this.getTranslatorBlockAtSocket(4);
	    if (translatorBlock!=null) {
	       wert2 = translatorBlock.toCode();
	       Var  += "float phyphoxWert2="+wert2+";\n";
	       List += ",phyphoxWert2";
	       Graph+="  // ----- Graph 2 -----\n"
          		+ "  PhyphoxBleExperiment::Graph g2;\n"
   	        	+ "  g2.setLabel("+n2+");\n"
   		        + "  g2.setLabelX(\"t\");\n"
   		        + "  g2.setUnitX(\"s\");\n"
   		        + "  g2.setLabelY(\"\");\n"
   		        + "  g2.setUnitY("+u2+");\n"
   		        + "  g2.setChannel(0,"+cnt+");\n"
   		        + "  v.addElement(g2);\n";
	       cnt++;
      }

	    
        translatorBlock = this.getRequiredTranslatorBlockAtSocket(5);
	    s = translatorBlock.toCode();
 	    pos = s.indexOf(",");
        if (pos>=0) { 
          n3 = s.substring( 0, pos)+"\"";
   	      u3= "\""+s.substring(pos+1,s.length()-1)+"\"";
        } else {u3="\"\"";n3=s;}

	    translatorBlock = this.getTranslatorBlockAtSocket(6);
	    if (translatorBlock!=null) {
	   	   wert3 = translatorBlock.toCode();
	       Var  += "float phyphoxWert3="+wert3+";\n";
	       List += ",phyphoxWert3";
	       Graph+="  // ----- Graph 3 -----\n"
	            + "  PhyphoxBleExperiment::Graph g3;\n"
	   	        + "  g3.setLabel("+n3+");\n"
	   		    + "  g3.setLabelX(\"t\");\n"
	   		    + "  g3.setUnitX(\"s\");\n"
	   		    + "  g3.setLabelY(\"\");\n"
	   		    + "  g3.setUnitY("+u3+");\n"
  		        + "  g3.setChannel(0,"+cnt+");\n"
     		    + "  v.addElement(g3);\n";
	    }

	    
	    
	    setup =   "  PhyphoxBLE::start("+Server+");\n"
	       		+ "  myexp.setTitle("+Server+");\n"
	     		+ "  PhyphoxBleExperiment::View v;\n"
	    		+ Graph
	    		+ "  myexp.addView(v);\n"
	    		+ "  PhyphoxBLE::addExperiment(myexp);";
	    
        translator.addSetupCommand(setup);
        
        ret = "{ // Phyphox send \n"
        		+ Var +         		
                 "PhyphoxBLE::write("+List+");\n"
                +"delay(50);\n"
        		+ "}";
        
        return codePrefix + ret + codeSuffix;
	 	}
}

