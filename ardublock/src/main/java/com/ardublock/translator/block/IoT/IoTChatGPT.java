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
		translator.addHeaderFile("WiFiClientSecure.h");
		translator.addHeaderFile("ChatGPT.hpp");
		translator.addSetupCommand("Serial.begin(115200);");
		
		
		String Def,Dis;
		Dis=    "// ChatGPT Client For ESP8266, Author: Eric Nam, https://github.com/0015/ChatGPT_Client_For_Arduino\n"
			   +"// Copyright (c) 2023 Eric\r\n"
			   +"// MIT License, for Disclaimer see end of file \n"
		       +"// ArduinoJson Lib Copyright © 2014-2024, Benoit BLANCHON\r\n"
		       +"// https://github.com/bblanchon/ArduinoJson \n";
		
		translator.addDefinitionCommand(Dis);
		//Def ="BearSSL::WiFiClientSecure GPT_client;\n" + 
		//  	 "ChatGPT<BearSSL::WiFiClientSecure> chat_gpt(&GPT_client, \"v1\", "+api+");\n";  
		
		Def ="WiFiClientSecure GPT_client;\r\n"
		   + "ChatGPT<WiFiClientSecure> chat_gpt(&GPT_client, \"v1\","+api+",60000);\r\n";
		
		
		translator.addDefinitionCommand(Def);
	
		
		String Set;
		Set = "// Ignore SSL certificate validation\n" + 
			  "  GPT_client.setInsecure();";
	    translator.addSetupCommand(Set);
		
        String Get;
        Get ="String askChatGPT(String model, String content, bool debug) {\r\n"
        		+ "  String result = \"empty\";  // Initialize the result string\r\n"
        		+ "  if (debug) {\r\n"
        		+ "    Serial.println(\"[ChatGPT] Sending content message...\");\r\n"
        		+ "    Serial.println(content);\r\n"
        		+ "  }\r\n"
        		+ "  // Convert `String` to `const char*` using `.c_str()` method\r\n"
        		+ "  const char* model_cstr = model.c_str();\r\n"
        		+ "  const char* content_cstr = content.c_str();\r\n"
        		+ "  // Call the chat_message function and store the result if successful\r\n"
        		+ "  bool success = chat_gpt.chat_message(model_cstr, \"user\", content_cstr, 100, false, result); \r\n"
        		+ "  if (success) {\r\n"
        		+ "    if (debug) Serial.print(\"[ChatGPT] Response: \");\r\n"
        		+ "    if (debug) Serial.println(result);\r\n"
        		+ "  } else {\r\n"
        		+ "    Serial.print(\"[ChatGPT] Error: \");\r\n"
        		+ "    Serial.println(result);\r\n"
        		+ "  }\r\n"
        		+ "  return result;\r\n"
        		+ "}";
	    translator.addDefinitionCommand(Get);
		
		
	    
	    
	    ret = "askChatGPT("+model+","+content+","+print+")";

        return codePrefix + ret + codeSuffix;
	 	}
}

