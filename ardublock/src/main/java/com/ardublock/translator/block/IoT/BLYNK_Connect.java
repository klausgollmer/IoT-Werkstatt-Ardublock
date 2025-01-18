package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class BLYNK_Connect  extends TranslatorBlock {

	public BLYNK_Connect (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
		translator.setBlynkProgram(true);

		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		//translator.addHeaderFile("BlynkMultiClient.h");

		//translator.addSetupCommand("Serial.begin(115200);");
		
		String ret, ID, NAME,TOKEN;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    ID = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    NAME = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    TOKEN = translatorBlock.toCode();
	    
	    String Dis="/*************************************************************\n"
	    		+ "  Blynk is a platform with iOS and Android apps to control\n"
	    		+ "  ESP32, Arduino, Raspberry Pi and the likes over the Internet.\n"
	    		+ "  You can easily build mobile and web interfaces for any\n"
	    		+ "  projects by simply dragging and dropping widgets.\n"
	    		+ "    Downloads, docs, tutorials: https://www.blynk.io\n"
	    		+ "    Sketch generator:           https://examples.blynk.cc\n"
	    		+ "    Blynk community:            https://community.blynk.cc\n"
	    		+ "    Follow us:                  https://www.fb.com/blynkapp\n"
	    		+ "                                https://twitter.com/blynk_app\n"
	    		+ "  Blynk library is licensed under MIT license\n"
	    		+ "  This example code is in public domain.\n"
	    		+ "*************************************************************/\n";
		translator.addDefinitionCommand(Dis);
	    String def = "#define BLYNK_TEMPLATE_ID "+ID+"\n" + 
	    		     "#define BLYNK_TEMPLATE_NAME "+NAME+"\n" + 
	    		     "#define BLYNK_AUTH_TOKEN "+TOKEN+"\n" +
	    		     "#include <BlynkMultiClient.h>\n" + 
	    		     "static WiFiClient blynkWiFiClient;\n"+
	    		     "/* Comment this out to disable prints and save space */\n" + 
	    		     "#define BLYNK_PRINT Serial"; 	    
		translator.addDefinitionCommand(def);
	    
	    
	    
	    
	    ret = "Blynk.addClient(\"WiFi\", blynkWiFiClient, 80);\n" + 
	    	  "Blynk.config(BLYNK_AUTH_TOKEN); // Downloads, docs, tutorials: http://www.blynk.cc\n";
	    
	    ret +="int BlynkCon = 0;\n"
	        + "while (BlynkCon == 0) {\n"
	        + "Serial.print (\"\\nBlynk connect ... \");\n" 
	        + "BlynkCon=Blynk.connect();\n"
	        + "if (BlynkCon == 0) {Serial.println(\"failed, try again\");delay(1000);}\n"
	        + "}\n"
	        + "Serial.println(\"connected\");\n";
	    
	    
        return codePrefix + ret + codeSuffix;
	 	}
}

