package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SMTP_Send  extends TranslatorBlock {

	public SMTP_Send (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("time.h");
		translator.addHeaderFile("ESP_Mail_Client.h");
		//translator.addSetupCommand("Serial.begin(115200);");
		
		String name,email,sub,text;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    email = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    sub = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    text = translatorBlock.toCode();
        
	    String def = "void email_Send(String name, String email, String subject, String textMsg) {\r\n"
	    		+ "  boolean send_ok = true;\r\n"
	    		+ "  if (!SMTP_email_init) {\r\n"
	    		+ "    /* Set the session config */\r\n"
	    		+ "    config.server.host_name = SMTP_HOST;\r\n"
	    		+ "    config.server.port = SMTP_PORT;\r\n"
	    		+ "    config.login.email = SMTP_AUTHOR_EMAIL;\r\n"
	    		+ "    config.login.password = SMTP_AUTHOR_PASSWORD;\r\n"
	    		+ "    config.login.user_domain = F(\"127.0.0.1\");\r\n"
	    		+ "    //config.time.ntp_server = F(\"pool.ntp.org,time.nist.gov\");\r\n"
	    		+ "    //config.time.gmt_offset = 3;\r\n"
	    		+ "    //config.time.day_light_offset = 0;\r\n"
	    		+ "    // Automatische Sommer-/Winterzeit setzen\r\n"
	    		+ "    const char* tz = \"CET-1CEST,M3.5.0,M10.5.0/3\";\r\n"
	    		+ "    configTzTime(tz, \"pool.ntp.org\", \"time.nist.gov\");\r\n"
	    		+ "\r\n"
	    		+ "    // Warten auf Zeit-Sync\r\n"
	    		+ "    struct tm timeinfo;\r\n"
	    		+ "    while (!getLocalTime(&timeinfo)) {\r\n"
	    		+ "      Serial.println(\"Warte auf NTP...\");\r\n"
	    		+ "      delay(1000);\r\n"
	    		+ "    }\r\n"
	    		+ "\r\n"
	    		+ "    SMTP_email_init = 1;\r\n"
	    		+ "#if (SMTP_RATE < SMTP_RATE_MAX) \r\n"
	    		+ "    for (uint8_t i=0; i < SMTP_RATE;i++)\r\n"
	    		+ "      SMTP_buffer[i]= 0;\r\n"
	    		+ "    SMTP_buffer_cnt = 0;\r\n"
	    		+ "#endif\r\n"
	    		+ "}\r\n"
	    		+ "\r\n"
	    		+ "// Test rate limit\r\n"
	    		+ "#if (SMTP_RATE < SMTP_RATE_MAX) \r\n"
	    		+ "  uint8_t next_cnt = (SMTP_buffer_cnt + 1) % SMTP_RATE;\r\n"
	    		+ "  if ((SMTP_buffer[next_cnt] > 0) && (millis() - SMTP_buffer[next_cnt]) < HOUR) {\r\n"
	    		+ "    send_ok = false; // rate limit\r\n"
	    		+ "  } \r\n"
	    		+ "  else {            // send mail allowed\r\n"
	    		+ "    SMTP_buffer[next_cnt] = millis();\r\n"
	    		+ "    SMTP_buffer_cnt = next_cnt;\r\n"
	    		+ "  }\r\n"
	    		+ "#endif\r\n"
	    		+ "// Send\r\n"
	    		+ "  if (send_ok) {\r\n"
	    		+ "    SMTP_Message message;\r\n"
	    		+ "    message.sender.name = name;\r\n"
	    		+ "    message.sender.email = config.login.email;\r\n"
	    		+ "    message.subject = subject;\r\n"
	    		+ "\r\n"
	    		+ "    message.addRecipient(F(\"IoT-Werkstatt\"), email);\r\n"
	    		+ "    message.text.content = textMsg;\r\n"
	    		+ "    message.priority = esp_mail_smtp_priority::esp_mail_smtp_priority_high;\r\n"
	    		+ "\r\n"
	    		+ "    /* Set the custom message header */\r\n"
	    		+ "    //message.addHeader(F(\"Message-ID: <abcde.fghij@gmail.com>\"));\r\n"
	    		+ "\r\n"
	    		+ "    /* Connect to the server */\r\n"
	    		+ "    if (!smtp.connect(&config))\r\n"
	    		+ "    { \r\n"
	    		+ "      MailClient.printf(\"❌ Connection error, Status Code: %d, Error Code: %d, Reason: %s\\n\", smtp.statusCode(), smtp.errorCode(), smtp.errorReason().c_str());\r\n"
	    		+ "      return;\r\n"
	    		+ "    }\r\n"
	    		+ "\r\n"
	    		+ "    if (!smtp.isLoggedIn())\r\n"
	    		+ "    { \r\n"
	    		+ "      IOTW_PRINTLN(F(\"Not yet logged in.\"));\r\n"
	    		+ "    } \r\n"
	    		+ "    else {\r\n"
	    		+ "      if (smtp.isAuthenticated())\r\n"
	    		+ "        IOTW_PRINT(F(\"SMTP mail logged in, mail to \"));\r\n"
	    		+ "      else\r\n"
	    		+ "        IOTW_PRINTLN(F(\"⚠ SMTP connected with no Auth.\"));\r\n"
	    		+ "    }\r\n"
	    		+ "\r\n"
	    		+ "    /* Start sending Email and close the session */\r\n"
	    		+ "    if (!MailClient.sendMail(&smtp, &message)) {\r\n"
	    		+ "      Serial.printf(\"❌ Error, Status Code: %d, Error Code: %d, Reason: %s\\n\", smtp.statusCode(), smtp.errorCode(), smtp.errorReason().c_str());\r\n"
	    		+ "    } else {  \r\n"
	    		+ "      IOTW_PRINT(email);IOTW_PRINTLN(F(\" ✅\"));\r\n"
	    		+ "    }\r\n"
	    		+ "    // to clear sending result log\r\n"
	    		+ "    smtp.sendingResult.clear();\r\n"
	    		+ "  } \r\n"
	    		+ "  else {\r\n"
	    		+ "    IOTW_PRINTLN(F(\"⚠ SMTP rate limit, mail not send\"));\r\n"
	    		+ "  }\r\n"
	    		+ "}";
	    translator.addDefinitionCommand(def);
		
        String ret = " email_Send(\"IoT-Werkstatt\","+email+","+sub+","+text+");";	    
	    
        return codePrefix + ret + codeSuffix;
	 	}
}

