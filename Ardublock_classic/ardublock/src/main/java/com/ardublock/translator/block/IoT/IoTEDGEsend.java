package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTEDGEsend  extends TranslatorBlock {

	public IoTEDGEsend (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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

		String host,api,file,device,ts,type;
		
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();
    
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    api = translatorBlock.toCode();

		translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    file = translatorBlock.toCode();
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    device = translatorBlock.toCode();
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(4);
	    ts = translatorBlock.toCode();
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(5);
	    type = translatorBlock.toCode();
		

	    String Code = "//---- EDGE IMPULSE and its logo are trademark of Edgeimpulse Inc. \n" + 
	    		      "//---- send data, Open-Source Library by Martin Seidinger, Nicolas Kiebel, Robin Barton, UCB \n" + 
	    		"  sendEdgeImpulse("+host+","+api+","+file+","+device+",\n"+ 
	    		"EI_Index, EI_NumSens,"+ts+", EI_nameOfSensor, EI_unitOfSensor, EI_Datenfeld,"+type+");\n"+
	    		"EI_Index = 0;\n";	    
	    
	    return codePrefix + Code + codeSuffix;
	 	}
}

