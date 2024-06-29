package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTWLANSniffPackets  extends TranslatorBlock {

	public IoTWLANSniffPackets (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
	    translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("WiFi_Sniffer.h");
		
		translator.addSetupCommand("Serial.begin(115200);");
		translator.setWiFiProgram(true);


		String channel;
		TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0);
		if (translatorBlock!=null)
   	       channel = translatorBlock.toCode();
		else 
		   channel = "0";	// use last channel

	    String Funct = "//------------ callback function WiFi-packets count \n"
	    		 +"volatile unsigned int WiFiPacketCount = 0;\n"
	    	 	 +"void packet_cb(uint8_t *buf, uint16_t len) {\n" + 
	    	 	 "  WiFiPacketCount++;\n" + 
	    	 	 "}\n";
	    translator.addDefinitionCommand(Funct);
	    
	    Funct = "// ------------------------  Zählt die auf einem WiFi Kanal transportierten Datenpackete \n" + 
	    		"int WiFiPacketCounter(int mychannel) {\n" + 
	    		"  if (mychannel < 1) mychannel = wifi_get_channel();\n" +
	    		"  if (mychannel > 14) mychannel = 1;"+
	    		"  wifi_set_promiscuous_rx_cb(packet_cb); // callback fürs zählen\n" + 
	    		"  WiFi.disconnect();                     // Station mode ausschalten\n" + 
	    		"  wifi_set_channel(mychannel);           // Kanal einstellen\n" + 
	    		"  WiFiPacketCount=0;                     // Zähler rücksetzen\n" + 
	    		"  wifi_promiscuous_enable(1);            // Start\n" + 
	    		"  delay(1000);                           // warte 1 Sekunde\n" + 
	    		"  wifi_promiscuous_enable(0);            // Stoppen\n" + 
	    		"  Serial.println(\"channel \"+String(mychannel)+ \": \"+ String(WiFiPacketCount) + \" packets per second\"); \n" + 
	    		"  return WiFiPacketCount;\n" + 
	    		"}\n"; 
	    	 	
	    translator.addDefinitionCommand(Funct);
        
	    
	    return codePrefix + "WiFiPacketCounter("+channel+")"+ codeSuffix;
	 	}
}

