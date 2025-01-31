package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class WLAN_SniffPackets  extends TranslatorBlock {

	public WLAN_SniffPackets (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
	    translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
	    translator.addHeaderFile("#if defined(ESP32) \n #include <esp_WiFi.h>\n#endif\n");		
		translator.addHeaderFile("IoTSniffer.h");
	
		translator.setWiFiProgram(true);


		String channel;
		TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0);
		if (translatorBlock!=null)
   	       channel = translatorBlock.toCode();
		else 
		   channel = "0";	// use last channel

	    String Funct = "volatile int WiFiPacketCount = 0;\n"
	    		+ "\n"
	    		+ "// Callback-Funktion, um empfangene Pakete zu zählen\n"
	    		+ "#if defined(ESP8266)\n"
	    		+ "void packet_cb(uint8_t* buf, uint16_t len) {\n"
	    		+ "    WiFiPacketCount++;\n"
	    		+ "}\n"
	    		+ "#elif defined(ESP32)\n"
	    		+ "void packet_cb(void* buf, wifi_promiscuous_pkt_type_t type) {\n"
	    		+ "    WiFiPacketCount++;\n"
	    		+ "}\n"
	    		+ "#endif\n"
	    		+ "\n"
	    		+ "int WiFiPacketCounter(int mychannel) {\n"
	    		+ "#if defined(ESP8266)\n"
	    		+ "    if (mychannel < 1) mychannel = wifi_get_channel();\n"
	    		+ "    if (mychannel > 14) mychannel = 1;\n"
	    		+ "\n"
	    		+ "    wifi_set_opmode(STATION_MODE);         // Station Mode (kein AP)\n"
	    		+ "    wifi_promiscuous_enable(0);            // Deaktiviere den Promiscuous-Modus, falls aktiv\n"
	    		+ "    wifi_set_promiscuous_rx_cb(packet_cb); // Setze Callback\n"
	    		+ "    wifi_set_channel(mychannel);           // Stelle den Wi-Fi-Kanal ein\n"
	    		+ "    WiFi.disconnect();                     // Trenne bestehende Verbindungen\n"
	    		+ "    WiFiPacketCount = 0;                   // Zähler zurücksetzen\n"
	    		+ "    wifi_promiscuous_enable(1);            // Promiscuous-Modus aktivieren\n"
	    		+ "\n"
	    		+ "    delay(1000);                           // Wartezeit für das Zählen von Paketen\n"
	    		+ "\n"
	    		+ "    wifi_promiscuous_enable(0);            // Promiscuous-Modus deaktivieren"    
	    		+ "\n"
	    		+ "#elif defined(ESP32)\n"
	    		+ "    if (mychannel < 1) mychannel = 1;\n"
	    		+ "    if (mychannel > 14) mychannel = 1;\n"
	    		+ "\n"
	    		+ "    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();\n"
	    		+ "    esp_wifi_init(&cfg);\n"
	    		+ "    esp_wifi_set_mode(WIFI_MODE_NULL);           // Kein AP oder Station Mode\n"
	    		+ "    esp_wifi_start();\n"
	    		+ "    esp_wifi_set_promiscuous(true);              // Promiscuous-Modus aktivieren\n"
	    		+ "    esp_wifi_set_promiscuous_rx_cb(&packet_cb);  // Callback setzen\n"
	    		+ "    esp_wifi_set_channel(mychannel, WIFI_SECOND_CHAN_NONE); // Kanal einstellen\n"
	    		+ "    WiFiPacketCount = 0;                         // Zähler rücksetzen\n"
	    		+ "    delay(1000);                                 // Warte 1 Sekunde\n"
	    		+ "    esp_wifi_set_promiscuous(false);             // Stoppen\n"
	    		+ "#endif\n"
	    		+ "\n"
	    		+ "    IOTW_PRINTLN(\"channel \" + String(mychannel) + \": \" + String(WiFiPacketCount) + \" packets per second\");\n"
	    		+ "    return WiFiPacketCount;\n"
	    		+ "}\n"
	    		+ ""; 
	    	 	
	    translator.addDefinitionCommand(Funct);
        
	    
	    return codePrefix + "WiFiPacketCounter("+channel+")"+ codeSuffix;
	 	}
}

