package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTEDGEsample  extends TranslatorBlock {

	public IoTEDGEsample (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		
		String EI_Def ="// ---- EDGE AI data \n" + 
				"int     EI_NumSens=0,EI_Index=0;\n" + 
				"float   EI_Datenfeld[EI_MAXPOINTS][EI_MAXSENSOR]; \n" +
				"int     AI_Datentyp[EI_MAXPOINTS];\n"+
				"String  EI_nameOfSensor[EI_MAXSENSOR];\n" + 
				"String  EI_unitOfSensor[EI_MAXSENSOR];\n" + 
				"";
			translator.addDefinitionCommand(EI_Def);
		
		translator.addSetupCommand("Serial.begin(115200);");

		String v1,v2,v3,v4;
		
	   	String no="1";
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    v1 = translatorBlock.toCode();

	    String Code="// ---------------  Sample EDGE IMPULSE\n" + 
	    		"  if (EI_Index < EI_MAXPOINTS) {\n" + 
	    		"     EI_Datenfeld[EI_Index][0] = "+v1+";\n"; 
		
	    
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(1);
	    if (translatorBlock!=null) {
	       v2 = translatorBlock.toCode();
	       Code += 	"     EI_Datenfeld[EI_Index][1] = "+v2+";\n"; 
	       no="2";
	    }
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(2);
	    if (translatorBlock!=null) {
	       v3 = translatorBlock.toCode();
	       Code += 	"     EI_Datenfeld[EI_Index][2] = "+v3+";\n"; 
	       no="3";
	    }
	    translatorBlock = this.getTranslatorBlockAtSocket(3);
	    if (translatorBlock!=null) {
	       v4 = translatorBlock.toCode();
	       Code += 	"     EI_Datenfeld[EI_Index][3] = "+v4+";\n"; 
	       no="4";
	    }
	
	    Code+=	"     EI_Index++;\n" +
	    		"     EI_NumSens = "+no+";\n;"+ 
	    		"  } else {\n" + 
	    		"    Serial.println(\"EDGE IMPULSE: EI_Index exceeds EI_MAXPOINTS\");\n" + 
	    		"  }";

	    return codePrefix + Code + codeSuffix;
	 	}
}

