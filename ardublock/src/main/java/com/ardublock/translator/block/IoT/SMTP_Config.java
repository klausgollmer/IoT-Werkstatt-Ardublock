package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SMTP_Config  extends TranslatorBlock {

	public SMTP_Config (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("ESP_Mail_Client.h");
		
		String host,port,sender="\"\"",pass="\"\"",Delay;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    port = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    sender = translatorBlock.toCode();
		    
	    translatorBlock = this.getTranslatorBlockAtSocket(3);
	    pass = translatorBlock.toCode();
	    
	    
	    String Dis = "/* ESP-Mail-Client, https://github.com/mobizt/ESP-Mail-Client/tree/master, \r\n"
	    		+ "   MIT License Copyright (c) 2025 mobizt, for Disclaimer see end of file \r\n"
	    		+ "   https://github.com/mobizt/ESP-Mail-Client/tree/master?tab=MIT-1-ov-file#readme\r\n";
	    translator.addDefinitionCommand(Dis);
	 
	    String def = "#define SMTP_HOST "+host+ "\r\n"
	    		+ "#define SMTP_PORT "+port+"\r\n"
	    		+ "#define AUTHOR_EMAIL "+sender+"\r\n"
	    		+ "#define AUTHOR_PASSWORD "+pass+"\r\n"
	    		+ "int SMTP_email_init = 0;\r\n"
	    		+ "SMTPSession smtp;\r\n"
	    		+ "void smtpCallback(SMTP_Status status);\r\n"
	    		+ "Session_Config config;\r\n"
	    		+ "\n" 
	    		+ "/* Callback function to get the Email sending status */\r\n"
	    		+ "void smtpCallback(SMTP_Status status)\r\n"
	    		+ "{\r\n"
	    		+ "  /* Print the current status */\r\n"
	    		+ "  Serial.println(status.info());\r\n"
	    		+ "\r\n"
	    		+ "  /* Print the sending result */\r\n"
	    		+ "  if (status.success())\r\n"
	    		+ "  {\r\n"
	    		+ "    // MailClient.printf used in the examples is for format printing via debug Serial port\r\n"
	    		+ "    // that works for all supported Arduino platform SDKs e.g. SAMD, ESP32 and ESP8266.\r\n"
	    		+ "    // In ESP8266 and ESP32, you can use Serial.printf directly.\r\n"
	    		+ "\r\n"
	    		+ "    Serial.println(\"----------------\");\r\n"
	    		+ "    MailClient.printf(\"Message sent success: %d\\n\", status.completedCount());\r\n"
	    		+ "    MailClient.printf(\"Message sent failed: %d\\n\", status.failedCount());\r\n"
	    		+ "    Serial.println(\"----------------\\n\");\r\n"
	    		+ "\r\n"
	    		+ "    for (size_t i = 0; i < smtp.sendingResult.size(); i++)\r\n"
	    		+ "    {\r\n"
	    		+ "      /* Get the result item */\r\n"
	    		+ "      SMTP_Result result = smtp.sendingResult.getItem(i);\r\n"
	    		+ "\r\n"
	    		+ "      // In case, ESP32, ESP8266 and SAMD device, the timestamp get from result.timestamp should be valid if\r\n"
	    		+ "      // your device time was synched with NTP server.\r\n"
	    		+ "      // Other devices may show invalid timestamp as the device time was not set i.e. it will show Jan 1, 1970.\r\n"
	    		+ "      // You can call smtp.setSystemTime(xxx) to set device time manually. Where xxx is timestamp (seconds since Jan 1, 1970)\r\n"
	    		+ "\r\n"
	    		+ "      MailClient.printf(\"Message No: %d\\n\", i + 1);\r\n"
	    		+ "      MailClient.printf(\"Status: %s\\n\", result.completed ? \"success\" : \"failed\");\r\n"
	    		+ "      MailClient.printf(\"Date/Time: %s\\n\", MailClient.Time.getDateTimeString(result.timestamp, \"%B %d, %Y %H:%M:%S\").c_str());\r\n"
	    		+ "      MailClient.printf(\"Recipient: %s\\n\", result.recipients.c_str());\r\n"
	    		+ "      MailClient.printf(\"Subject: %s\\n\", result.subject.c_str());\r\n"
	    		+ "    }\r\n"
	    		+ "    Serial.println(\"----------------\\n\");\r\n"
	    		+ "\r\n"
	    		+ "    // You need to clear sending result as the memory usage will grow up.\r\n"
	    		+ "    smtp.sendingResult.clear();\r\n"
	    		+ "  }\r\n"
	    		+ "}"; 
	        
		translator.addDefinitionCommand(def);	
		
	    String setup = "//---------------------------------- STMP-Client \n"
	    		+ "smtp.debug(IOTW_DEBUG_LEVEL);\r\n"
	    		+ "/* Set the callback function to get the sending results */\r\n"
	    		+ "smtp.callback(smtpCallback);\n";
	    
	    translator.addSetupCommand(setup);
		
         return codePrefix + "" + codeSuffix;
	 	}
}

