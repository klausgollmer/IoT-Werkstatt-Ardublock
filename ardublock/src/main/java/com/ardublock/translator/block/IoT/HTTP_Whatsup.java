package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class HTTP_Whatsup  extends TranslatorBlock {

	public HTTP_Whatsup (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		translator.addHeaderFile("UrlEncode.h");
		//translator.addSetupCommand("Serial.begin(115200);");
		
		String phone,api,text;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    phone = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    api = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    text = translatorBlock.toCode();
        
	    String def = "/* \r\n"
	    		+ "  whatsup by Rui Santos, Complete project details at https://RandomNerdTutorials.com/esp32-send-messages-whatsapp/\r\n"
	    		+ "  Permission is hereby granted, free of charge, to any person obtaining a copy of this software \r\n"
	    		+ "  and associated documentation files. The above copyright notice and this permission notice shall be included in all\r\n"
	    		+ "  copies or substantial portions of the Software.\r\n"
	    		+ "*/\r\n"
	    		+ "void whatsup_Send(String phoneNumber, String apiKey, String message){\r\n"
	    		+ "\r\n"
	    		+ "  // Data to send with HTTP POST\r\n"
	    		+ "  String url = \"https://api.callmebot.com/whatsapp.php?phone=\" + phoneNumber + \"&apikey=\" + apiKey + \"&text=\" + urlEncode(message);    \r\n"
	    		+ "  HTTPClient http;\r\n"
	    		+ "  http.begin(url);\r\n"
	    		+ "\r\n"
	    		+ "  // Specify content-type header\r\n"
	    		+ "  http.addHeader(\"Content-Type\", \"application/x-www-form-urlencoded\");\r\n"
	    		+ "  \r\n"
	    		+ "  // Send HTTP POST request\r\n"
	    		+ "  int httpResponseCode = http.POST(url);\r\n"
	    		+ "  if (httpResponseCode == 200){\r\n"
	    		+ "    IOTW_PRINT(F(\"whatsup message sent successfully\"));\r\n"
	    		+ "  }\r\n"
	    		+ "  else{\r\n"
	    		+ "    IOTW_PRINT(F(\"Error sending the message\"));\r\n"
	    		+ "    IOTW_PRINT(F(\"HTTP response code: \"));\r\n"
	    		+ "    IOTW_PRINTLN(httpResponseCode);\r\n"
	    		+ "  }\r\n"
	    		+ "\r\n"
	    		+ "  // Free resources\r\n"
	    		+ "  http.end();\r\n"
	    		+ "}";
	    translator.addDefinitionCommand(def);
		
        String ret = "whatsup_Send("+phone+","+api+","+text+");";	    
	    
        return codePrefix + ret + codeSuffix;
	 	}
}

