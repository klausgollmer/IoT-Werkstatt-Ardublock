package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTNTPctime  extends TranslatorBlock {

	public IoTNTPctime (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("time.h");
		//translator.addSetupCommand("Serial.begin(115200);");
 
		String help ="// Umwandlung Unix-time in Textausgabe\n" + 
				"String NTPtime(){\n" + 
				"  timeval tv;\n" + 
				"  gettimeofday(&tv, NULL); \n" + 
				"  time_t now=tv.tv_sec;\n" + 
				"  struct tm * timeinfo;\n" + 
				"  timeinfo = localtime(&now);  \n" + 
				"  String txt = String(timeinfo->tm_hour)+\":\"+String(timeinfo->tm_min)+\":\"+String(timeinfo->tm_sec);  \n" + 
				"  return txt;\n" + 
				"}\n" + 
				"";
		
		translator.addDefinitionCommand(help);
        return codePrefix + "NTPtime()" + codeSuffix;
	 	}
}

