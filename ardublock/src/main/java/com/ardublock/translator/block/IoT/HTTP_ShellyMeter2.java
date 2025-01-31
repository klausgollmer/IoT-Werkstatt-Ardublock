package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class HTTP_ShellyMeter2  extends TranslatorBlock {

	public HTTP_ShellyMeter2 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
				
		//translator.addSetupCommand("Serial.begin(115200);");
				
		String httpGET ="//--------------------------------------- http-GET with wifi-Client\n" + 
				"int httpClientGET(String host, String cmd, String &antwort) {\n" + 
				"  int ok = 0;\n" + 
				"  String message = host+cmd;\n" + 
				"  WiFiClient client;\n" + 
				"  #if defined(ESP8266)\n HTTPClient http;\n#elif defined(ESP32) \n HTTPClient http;\n#endif\n" + 
				"  //IOTW_PRINTLN(message);\n" + 
				"  if (http.begin(client, message)){  // HTTP\n" + 
				"    // start connection and send HTTP header\n" + 
				"    int httpCode = http.GET();\n" + 
				"    // httpCode will be negative on error\n" + 
				"    if (httpCode > 0) {\n" + 
				"      // HTTP header has been send and Server response header has been handled\n" + 
				"      String payload = http.getString();\n" + 
				"      antwort = payload;\n" + 
				"      //IOTW_PRINTLN(payload);\n" + 
				"      // file found at server\n" + 
				"      if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {\n" + 
				"        ok = 1;\n" + 
				"      }\n" + 
				"    } \n" + 
				"    else {\n" + 
				"      IOTW_PRINTF(\"[HTTP] GET... failed, error: %s\\n\", http.errorToString(httpCode).c_str());\n" + 
				"    }\n" + 
				"    http.end();\n" + 
				"  } \n" + 
				"  else {\n" + 
				"    IOTW_PRINTF(\"[HTTP] Unable to connect\\n\");\n" + 
				"  }\n" + 
				"  return ok;\n" + 
				"}\n" + 
				"";
		translator.addDefinitionCommand(httpGET);

		String Shelly = "// https://shelly-api-docs.shelly.cloud/gen1/#shelly-plug-plugs\n" + 
				"String parseShellyInfo(String xml,String suchtext) {\n" + 
				"  String valStr = \"\";                // Hilfsstring\n" + 
				"  int start, ende;                   // Index im Text\n" + 
				"  start = xml.indexOf(suchtext);     // Suche Text\n" + 
				"  if (start > 0) {                   // Item gefunden\n" + 
				"    start = start+suchtext.length(); // hinter Item kommt Zahl\n" + 
				"    ende =  xml.indexOf(',',start);  // Ende der Zahl\n" + 
				"    valStr= xml.substring(start,ende);// Zahltext\n" + 
				"  } \n" + 
				"  else                             // Item nicht gefunden\n" + 
				"  IOTW_PRINT(\"error - no such item: \"+suchtext);\n" + 
				"  return valStr;\n" + 
				"}\n" + 
				"void ShellySwitch(String host,int state) { \n" + 
				"  String cmd;\n" + 
				"  host=\"http://\"+host;\n"+
				"  if (state == 1) cmd = \"/relay/0?turn=on\" ;\n" + 
				"  else cmd = \"/relay/0?turn=off\";\n" + 
				"  String antwort;\n" + 
				"  httpClientGET(host,cmd,antwort);\n" + 
				"}\n";
                translator.addDefinitionCommand(Shelly);
		
 
					Shelly = "float ShellyMeter2(String host,int para,int gen) { \n" + 
							"  float  val    =  NAN;\n" + 
							"  String valStr = \"NAN\";\n" + 
							"  String antwort;\n" + 
							"  host=\"http://\"+host;\n" + 
							"  int tout = 2; // Retry\n" + 
							"  while ((tout > 0) && (isnan(val))) {\n" + 
							"    tout--; \n" + 
							"    if (gen == 1) {\n" + 
							"      if (httpClientGET(host,\"/status\",antwort)) { // success\n" + 
							"        if (parseShellyInfo(antwort,\"\\\"is_valid\\\":\") == \"true\") {\n" + 
							"          if (para == 1) {         \n" + 
							"            valStr=parseShellyInfo(antwort,\"\\\"power\\\":\");\n" + 
							"            val = valStr.toFloat();\n" + 
							"          }\n" + 
							"          if (para == 2) {         \n" + 
							"            valStr=parseShellyInfo(antwort,\"\\\"total\\\":\");\n" + 
							"            val = valStr.toFloat()/60.0;\n" + 
							"          }\n" + 
							"        }\n" + 
							"      }\n" + 
							"    } // generation 1 \n" + 
							"\n" + 
							"    if (gen == 2) {\n" + 
							"      if (httpClientGET(host,\"/rpc/Switch.GetStatus?id=0\",antwort)) { // success\n" + 
							"        if (1) { // parseShellyInfo(antwort,\"\\\"is_valid\\\":\") == \"true\") {\n" + 
							"          if (para == 1) {         \n" + 
							"            valStr=parseShellyInfo(antwort,\"\\\"apower\\\":\");\n" + 
							"            val = valStr.toFloat();\n" + 
							"          }\n" + 
							"          if (para == 2) {         \n" + 
							"            valStr=parseShellyInfo(antwort,\"\\\"total\\\":\");\n" + 
							"            val = valStr.toFloat();\n" + 
							"          }\n" + 
							"\n" + 
							"          if (para == 3) {         \n" + 
							"            valStr=parseShellyInfo(antwort,\"\\\"tC\\\":\");\n" + 
							"            val = valStr.toFloat();\n" + 
							"          }\n" + 
							"        }\n" + 
							"      }\n" + 
							"    } // generation 2 \n" + 
							"  }\n" + 
							"  return val;\n" + 
							"}\n" + 
							"";
					
	  	    translator.addDefinitionCommand(Shelly);
	  	    //translator.addSetupCommand("udp.begin(123);");

    	//translator.setNTPServerProgram(true);
    	
		String host,state,gen;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    gen = translatorBlock.toCode();
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    state = translatorBlock.toCode();
	    
	    String ret = "ShellyMeter2("+host+","+state+","+gen+")";
        return codePrefix + ret + codeSuffix;
	 	}
}

