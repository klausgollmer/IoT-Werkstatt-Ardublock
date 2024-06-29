package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTShellyMeter  extends TranslatorBlock {

	public IoTShellyMeter (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		translator.addHeaderFile("WiFiUdp.h");
				
		String webserverDef = "typedef void (*func_ptr)(void);\n" + 
				"func_ptr WebServerHousekeeping = yield;\n";
		translator.addDefinitionCommand(webserverDef);
		translator.addSetupCommand("Serial.begin(115200);");
				
		String httpGET ="//--------------------------------------- http-GET with wifi-Client\n" + 
				"int httpClientGET(String host, String cmd, String &antwort) {\n" + 
				"  int ok = 0;\n" + 
				"  String message = host+cmd;\n" + 
				"  WiFiClient client;\n" + 
				"  #if defined(ESP8266)\n ESP8266HTTPClient http;\n#elif defined(ESP32) \n HTTPClient http;\n#endif\n" + 
				"  //Serial.println(message);\n" + 
				"  if (http.begin(client, message)){  // HTTP\n" + 
				"    // start connection and send HTTP header\n" + 
				"    int httpCode = http.GET();\n" + 
				"    // httpCode will be negative on error\n" + 
				"    if (httpCode > 0) {\n" + 
				"      // HTTP header has been send and Server response header has been handled\n" + 
				"      String payload = http.getString();\n" + 
				"      antwort = payload;\n" + 
				"      //Serial.println(payload);\n" + 
				"      // file found at server\n" + 
				"      if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {\n" + 
				"        ok = 1;\n" + 
				"      }\n" + 
				"    } \n" + 
				"    else {\n" + 
				"      Serial.printf(\"[HTTP] GET... failed, error: %s\\n\", http.errorToString(httpCode).c_str());\n" + 
				"    }\n" + 
				"    http.end();\n" + 
				"  } \n" + 
				"  else {\n" + 
				"    Serial.printf(\"[HTTP] Unable to connect\\n\");\n" + 
				"  }\n" + 
				"  return ok;\n" + 
				"}\n" + 
				"";
		translator.addDefinitionCommand(httpGET);

		String Shelly="//--------------------------------------- Shelly Plug (Switch/Metering)\n" + 
	            "WiFiUDP udp;\n"+
		        "// Energy measurement on shelly plug needs a NTP-Server \n" + 
  	    		"// ESP do not have valid time, but millis counts for runtime. This is a FakeServer \n" + 
  	    		"void ShellyNTPServer(){\n" + 
  	    		"   if (NTP_udp_init) {udp.begin(123); NTP_udp_init=0; }\n"+
  	    		"   if (udp.parsePacket()){\n" + 
  	    		"      #define NTP_PACKET_SIZE 48\n" + 
  	    		"      byte packetBuffer[NTP_PACKET_SIZE];\n" + 
  	    		"      udp.read(packetBuffer, NTP_PACKET_SIZE);\n" + 
  	    		"      IPAddress clientIP      = udp.remoteIP();\n" + 
  	    		"      unsigned int clientPort = udp.remotePort();\n" + 
  	    		"      Serial.print(\"Received packet from NTP client IP \");\n" + 
  	    		"      Serial.println(clientIP);\n" + 
  	    		"      // send a reply to the client with the current time\n" + 
  	    		"      packetBuffer[0] = 0b00100100;   // LI, Version, Mode\n" + 
  	    		"      packetBuffer[1] = 4;     // Stratum, or type of clock\n" + 
  	    		"      packetBuffer[2] = 6;     // Polling Interval\n" + 
  	    		"      packetBuffer[3] = 0xFA;  // Peer Clock Precision\n" + 
  	    		"      // 8 bytes of zero for Root Delay & Root Dispersion\n" + 
  	    		"      packetBuffer[4] = 0;\n" + 
  	    		"      packetBuffer[5] = 0;\n" + 
  	    		"      packetBuffer[6] = 0;\n" + 
  	    		"      packetBuffer[7] = 0;\n" + 
  	    		"      packetBuffer[8] = 0;\n" + 
  	    		"      packetBuffer[9] = 8;\n" + 
  	    		"      packetBuffer[10] = 0;\n" + 
  	    		"      packetBuffer[11] = 0;\n" + 
  	    		"      packetBuffer[12]  = 71;\n" + 
  	    		"      packetBuffer[13]  = 80;\n" + 
  	    		"      packetBuffer[14]  = 83;\n" + 
  	    		"      packetBuffer[15]  = 0;\n" + 
  	    		"      // get the current time and put it in the NTP packet\n" + 
  	    		"      unsigned long epoch = millis()/1000; // current time in seconds since Jan 1 1970\n" + 
  	    		"      unsigned long secsSince2022 = epoch + 3880297646UL;\n" + 
  	    		"      packetBuffer[16] = lowByte(secsSince2022 >> 24);\n" + 
  	    		"      packetBuffer[17] = lowByte(secsSince2022 >> 16);\n" + 
  	    		"      packetBuffer[18] = lowByte(secsSince2022 >> 8);\n" + 
  	    		"      packetBuffer[19] = lowByte(secsSince2022);\n" + 
  	    		"      packetBuffer[20] = 0;\n" + 
  	    		"      packetBuffer[21] = 0;\n" + 
  	    		"      packetBuffer[22] = 0;\n" + 
  	    		"      packetBuffer[23] = 0;\n" + 
  	    		"      packetBuffer[24]=packetBuffer[40];\n" + 
  	    		"      packetBuffer[25]=packetBuffer[41];\n" + 
  	    		"      packetBuffer[26]=packetBuffer[42];\n" + 
  	    		"      packetBuffer[27]=packetBuffer[43];\n" + 
  	    		"      packetBuffer[28]=packetBuffer[44];\n" + 
  	    		"      packetBuffer[29]=packetBuffer[45];\n" + 
  	    		"      packetBuffer[30]=packetBuffer[46];\n" + 
  	    		"      packetBuffer[31]=packetBuffer[47];\n" + 
  	    		"      packetBuffer[32] = lowByte(secsSince2022 >> 24);\n" + 
  	    		"      packetBuffer[33] = lowByte(secsSince2022 >> 16);\n" + 
  	    		"      packetBuffer[34] = lowByte(secsSince2022 >> 8);\n" + 
  	    		"      packetBuffer[35] = lowByte(secsSince2022);\n" + 
  	    		"      packetBuffer[36] = 0;\n" + 
  	    		"      packetBuffer[37] = 0;\n" + 
  	    		"      packetBuffer[38] = 0;\n" + 
  	    		"      packetBuffer[39] = 0;\n" + 
  	    		"      packetBuffer[40] = lowByte(secsSince2022 >> 24);\n" + 
  	    		"      packetBuffer[41] = lowByte(secsSince2022 >> 16);\n" + 
  	    		"      packetBuffer[42] = lowByte(secsSince2022 >> 8);\n" + 
  	    		"      packetBuffer[43] = lowByte(secsSince2022);\n" + 
  	    		"      packetBuffer[44]  = 0;\n" + 
  	    		"      packetBuffer[45]  = 0;\n" + 
  	    		"      packetBuffer[46]  = 0;\n" + 
  	    		"      packetBuffer[47]  = 0;\n" + 
  	    		"      // send the NTP packet\n" + 
  	    		"      udp.beginPacket(clientIP,clientPort);\n" + 
  	    		"      udp.write(packetBuffer, NTP_PACKET_SIZE);\n" + 
  	    		"      udp.endPacket();\n" + 
  	    		"   }\n" + 
  	    		"}\n" + 
  	    		"";	  
		        translator.addDefinitionCommand(Shelly);
		
					Shelly = "// https://shelly-api-docs.shelly.cloud/gen1/#shelly-plug-plugs\n" + 
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
					"  Serial.print(\"error - no such item: \"+suchtext);\n" + 
					"  return valStr;\n" + 
					"}\n" + 
					"void ShellySwitch(String host,int state) { \n" + 
					"  String cmd;\n" + 
					"  ShellyNTPServer();\n"+
					"  host=\"http://\"+host;\n"+
					"  if (state == 1) cmd = \"/relay/0?turn=on\" ;\n" + 
					"  else cmd = \"/relay/0?turn=off\";\n" + 
					"  String antwort;\n" + 
					"  httpClientGET(host,cmd,antwort);\n" + 
					"  ShellyNTPServer();\n"+
					"}\n";
			  	    translator.addDefinitionCommand(Shelly);
 
					Shelly = "float ShellyMeter(String host,int para) { \n" + 
					"  float  val    =  NAN;\n" + 
					"  String valStr = \"NAN\";\n" + 
					"  String antwort;\n" + 
					"  host=\"http://\"+host;\n"+
					"  int tout = 2; // Retry\n" + 
					"  while ((tout > 0) && (isnan(val))) {\n" + 
					"   ShellyNTPServer();\n"+
					"   tout--; \n" + 
				    "   (*WebServerHousekeeping)(); // look at WebCients\n" + 
					"   if (httpClientGET(host,\"/status\",antwort)) { // success\n" + 
					"    if (parseShellyInfo(antwort,\"\\\"is_valid\\\":\") == \"true\") {\n" + 
					"      if (para == 1) {         \n" + 
					"        valStr=parseShellyInfo(antwort,\"\\\"power\\\":\");\n" + 
					"        val = valStr.toFloat();\n" + 
					"      }\n" + 
					"      if (para == 2) {         \n" + 
					"        valStr=parseShellyInfo(antwort,\"\\\"total\\\":\");\n" + 
					"        val = valStr.toFloat()/60.0;\n" + 
					"      }\n" + 
					"    }\n" + 
					"   }\n" + 
					"  }\n" + 
					"  ShellyNTPServer();\n"+
					"  return val;\n" + 
					"}\n" + 
					"";
	  	    translator.addDefinitionCommand(Shelly);
	  	    //translator.addSetupCommand("udp.begin(123);");

    	translator.setNTPServerProgram(true);
    	
		String host,state;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    state = translatorBlock.toCode();
	    
	    String ret = "ShellyMeter("+host+","+state+")";
        return codePrefix + ret + codeSuffix;
	 	}
}

