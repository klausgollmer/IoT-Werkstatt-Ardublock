package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTChatGPT  extends TranslatorBlock {

	public IoTChatGPT (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
		String api,model,content,ret,print;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    api = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    model = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    content = translatorBlock.toCode();
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    print = translatorBlock.toCode();
	    
		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("ArduinoJson.h");
		translator.addHeaderFile("WiFiClientSecureBearSSL.h");
		translator.addHeaderFile("ChatGPT.hpp");
		translator.addSetupCommand("Serial.begin(115200);");
		
		
		String Def,Dis;
		Dis=    "// ChatGPT Client For ESP8266, Author: Eric Nam, https://github.com/0015/ChatGPT_Client_For_Arduino\n"
			   +"// Copyright (c) 2023 Eric\r\n"
			   +"// MIT License, for Disclaimer see end of file \n";
		translator.addDefinitionCommand(Dis);
		Def ="BearSSL::WiFiClientSecure GPT_client;\n" + 
		  	 "ChatGPT<BearSSL::WiFiClientSecure> chat_gpt(&GPT_client, \"v1\", "+api+");\n";  
		translator.addDefinitionCommand(Def);
	
		
		String Set;
		Set = "// Ignore SSL certificate validation\n" + 
			  "  GPT_client.setInsecure();";
	    translator.addSetupCommand(Set);
		
        String Get;
        Get ="String askChatGPT(String model, String content,int print){\n" + 
        		"  String result = \"empty\";\n" + 
        		"  if (print) Serial.println(\"[ChatGPT] Only print a content message\");\n" + 
        		"  if (chat_gpt.simple_message(model, \"user\", content, result)) {\n" + 
        		"    if (print) Serial.println(\"OK:\");\n" + 
        		"  } else {\n" + 
        		"    if (print) Serial.println(\"ERROR:\");\n" + 
        		"  }\n" + 
        		"  if (print) Serial.println(result);\n" +
        		"  return result;\n" + 
        		"}\n" + 
        		"";
	    translator.addDefinitionCommand(Get);
		
		
	    
	    
	    ret = "askChatGPT("+model+","+content+","+print+")";

        return codePrefix + ret + codeSuffix;
	 	}
}

