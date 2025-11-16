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
		String s,u1="\"\"",u2="\"\"",u3="\"\"",u4="\"\"",u5="\"\"";
		String n1="\"Sensor 1\"",n2="\"Sensor 2\"",n3 = "\"Sensor 3\"",n4="\"Sensor 4\"",n5="\"Sensor 5\"";
		
		translator.addHeaderFile("#if defined(ESP32)\n #include <phyphoxBle.h> \n#endif\n");		
        String Lic = "// Phyphox-Arduino BLE Library, LGPL-3.0, GPL-3.0 licenses \n"
        		   + "// https://github.com/phyphox/phyphox-arduino/tree/master?tab=LGPL-3.0-1-ov-file\n"
        		   + "PhyphoxBleExperiment myexp;\n";
        translator.addDefinitionCommand(Lic);
        
		String Server, wert1="", wert2="", wert3="",wert4="",wert5="",setup, ret;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    Server = translatorBlock.toCode();

	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    if (translatorBlock!=null) {
		    s = translatorBlock.toCode();
	 	    pos = s.indexOf(",");
            if (pos>=0) { 
	          n1 = s.substring( 0, pos)+"\"";
	   	      u1= "\""+s.substring(pos+1,s.length()-1)+"\"";
	        } else {u1="\"\"";n1=s;}
	    }
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
	
        translatorBlock = this.getTranslatorBlockAtSocket(3);
	    if (translatorBlock!=null) {
		    s = translatorBlock.toCode();
	 	    pos = s.indexOf(",");
	        if (pos>=0) { 
	          n2 = s.substring( 0, pos)+"\"";
	   	      u2= "\""+s.substring(pos+1,s.length()-1)+"\"";
	        } else {u2="\"\"";n2=s;}
	    }
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

	    
        translatorBlock = this.getTranslatorBlockAtSocket(5);
        if (translatorBlock!=null) {
	    	s = translatorBlock.toCode();
	 	    pos = s.indexOf(",");
	        if (pos>=0) { 
	          n3 = s.substring( 0, pos)+"\"";
	   	      u3= "\""+s.substring(pos+1,s.length()-1)+"\"";
	        } else {u3="\"\"";n3=s;}
	    }
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
	       cnt++;
	    }

	    translatorBlock = this.getTranslatorBlockAtSocket(7);
        if (translatorBlock!=null) {
	    	s = translatorBlock.toCode();
	 	    pos = s.indexOf(",");
	        if (pos>=0) { 
	          n4 = s.substring( 0, pos)+"\"";
	   	      u4= "\""+s.substring(pos+1,s.length()-1)+"\"";
	        } else {u4="\"\"";n4=s;}
	    }
	    translatorBlock = this.getTranslatorBlockAtSocket(8);
	    if (translatorBlock!=null) {
	   	   wert4 = translatorBlock.toCode();
	       Var  += "float phyphoxWert4="+wert4+";\n";
	       List += ",phyphoxWert4";
	       Graph+="  // ----- Graph 4 -----\n"
	            + "  PhyphoxBleExperiment::Graph g4;\n"
	   	        + "  g4.setLabel("+n4+");\n"
	   		    + "  g4.setLabelX(\"t\");\n"
	   		    + "  g4.setUnitX(\"s\");\n"
	   		    + "  g4.setLabelY(\"\");\n"
	   		    + "  g4.setUnitY("+u4+");\n"
  		        + "  g4.setChannel(0,"+cnt+");\n"
     		    + "  v.addElement(g4);\n";
	       cnt++;
	    }
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(9);
        if (translatorBlock!=null) {
	    	s = translatorBlock.toCode();
	 	    pos = s.indexOf(",");
	        if (pos>=0) { 
	          n5 = s.substring( 0, pos)+"\"";
	   	      u5= "\""+s.substring(pos+1,s.length()-1)+"\"";
	        } else {u5="\"\"";n5=s;}
	    }
	    translatorBlock = this.getTranslatorBlockAtSocket(10);
	    if (translatorBlock!=null) {
	   	   wert5 = translatorBlock.toCode();
	       Var  += "float phyphoxWert5="+wert5+";\n";
	       List += ",phyphoxWert5";
	       Graph+="  // ----- Graph 5 -----\n"
	            + "  PhyphoxBleExperiment::Graph g5;\n"
	   	        + "  g5.setLabel("+n5+");\n"
	   		    + "  g5.setLabelX(\"t\");\n"
	   		    + "  g5.setUnitX(\"s\");\n"
	   		    + "  g5.setLabelY(\"\");\n"
	   		    + "  g5.setUnitY("+u5+");\n"
  		        + "  g5.setChannel(0,"+cnt+");\n"
     		    + "  v.addElement(g5);\n";
	       cnt++;
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
                +"delay(1);\n"
        		+ "}";
        
        return codePrefix + ret + codeSuffix;
	 	}
}

