package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTNTPServer  extends TranslatorBlock {

	public IoTNTPServer (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("time.h");
	
		//	translator.addHeaderFile("coredecls.h");
 	    //	translator.addHeaderFile("TZ.h");
		
		translator.addSetupCommand("Serial.begin(115200);");

		
		String help = "// ----------  NTP-Zeitservice\n" + 
				"int NTPtimeset=0;\n" + 
				"void NTPtime_is_set(void) { // Callback Zeitsync\n" + 
				"  NTPtimeset = 1;\n" + 
				"  Serial.println(\"NTP-Time set\");\n" + 
				"}\n";
	   translator.addDefinitionCommand(help);
		
	   help ="// Umwandlung Unix-time in Textausgabe\n" + 
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
		
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    String URL = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    String TS = translatorBlock.toCode();
	    TS = TS.replace("\"", ""); 


// 	    help =  " setenv(\"TZ\", "+TS+", 0); // set timezone for NTP\n";
// 	    translator.addSetupCommand(help);
	        	

	    String ret = "//------------ NTP Network-Time-Protocoll, Service starten \n"+
	    	" Serial.println(\"sync with NTP-pool ...\");\n"+
         	" configTime(0,0,"+URL+",\"pool.ntp.org\");\n"+ 
//    		" settimeofday_cb(NTPtime_is_set);\n" + 
            " setenv(\"TZ\",\""+TS+"\",1);\n" + 
            " tzset();\n" + 
            " NTPtime_is_set();\n"+
    		" int tout = 50;\n" + 
    		" while ((tout > 0) && (!NTPtimeset)) {tout--;delay(100);}\n" + 
    		" if (tout <=0) Serial.println(\"Sorry, no NTP-service available\");\n" + 
    		" Serial.println(NTPtime());\n";

		translator.setRTCNTPProgram(true);

	  
        return codePrefix + ret + codeSuffix;
	 	}
}

