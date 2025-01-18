package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class WLAN_CountPax  extends TranslatorBlock {

	public WLAN_CountPax (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
	    translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
	    translator.addHeaderFile("#if defined(ESP32) \n #include <esp_WiFi.h>\n#endif\n");		
			translator.addHeaderFile("IoTSniffer.h");
		
		//translator.addSetupCommand("Serial.begin(115200);");
		translator.addSetupCommand("WiFi.mode(WIFI_STA); // Pax-counter");
		translator.addDefinitionCommand("unsigned int Sniff_channel = 1;");
		translator.setWiFiProgram(true);

//		String Set = " WiFi.mode(WIFI_STA);\n     // Promiscuous works only with station mode\n"+  
//				"  wifi_set_channel(Sniff_channel);\n" + 
//				"  wifi_promiscuous_enable(false);\n" + 
//                +" WiFi.mode(WIFI_STA);\n"+
//				"  wifi_set_promiscuous_rx_cb(promisc_cb);   // Set up promiscuous callback\n";
//				"  wifi_promiscuous_enable(true);\n";
				
//		translator.addSetupCommand(Set);
		
		
		
		String rssi,time,channel,mac,display;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    rssi = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    time = translatorBlock.toCode();
	    
	  	    
	    translatorBlock = this.getTranslatorBlockAtSocket(2);
	    if (translatorBlock!=null) {
	       channel = translatorBlock.toCode();
	    } else {
        	channel = "0";
	    }
	   
	    translatorBlock = this.getTranslatorBlockAtSocket(3);
	    if (translatorBlock!=null) {
	       mac = translatorBlock.toCode();
	    } else {
        	mac = "\"all mac\"";
	    }
	   
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(4);
	    display = translatorBlock.toCode();
	    
	    
	    
	    String Funct = "//------------ WiFi-Sniffer,  This software is based on the work of Andreas Spiess, https://github.com/SensorsIot/Wi-Fi-Sniffer-as-a-Human-detector//                            and Ray Burnette: https://www.hackster.io/rayburne/esp8266-mini-sniff-f6b93a \n"
	    		+ "int WiFiPaxCounter(int MinRSSI,  int timeout, int8_t mychannel,String myMAC,int mydisplay) {\n"
	    		+ "  int mycount=0;\n"
	    		+ "  int randMAC=0;\n"
	    		+ "  int ChanMin = 1, ChanMax =13; // europe channel 1-13, Japan 1-14\n"
	    		+ "  if (mychannel > 0) {\n"
	    		+ "    ChanMax = mychannel; \n"
	    		+ "    ChanMin = mychannel;\n"
	    		+ "  };\n"
	    		+ "  if (mychannel < 0) {\n"
	    		+ "    randMAC = mychannel;\n"
	    		+ "  };\n"
	    		+ "  #if defined(ESP8266)\n"
	    		+ "    wifi_set_promiscuous_rx_cb(promisc_cb);   // Set up promiscuous callback\n"
	    		+ "    Sniff_channel = ChanMin;\n"
	    		+ "    wifi_set_channel(Sniff_channel);\n"
	    		+ "    wifi_promiscuous_enable(true);\n"
	    		+ "    for (Sniff_channel = ChanMin; Sniff_channel <= ChanMax; Sniff_channel++) {\n"
	    		+ "      wifi_set_channel(Sniff_channel);\n"
	    		+ "      delay(300);            // 300 ms per channel\n"
	    		+ "    }\n"
	    		+ "    wifi_promiscuous_enable(false);\n"
	    		+ "  #endif\n"
	    		+ "  #if defined(ESP32)\n"
	    		+ "    //wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();\n"
	    		+ "    //esp_wifi_init(&cfg);\n"
	    		+ "    //esp_wifi_set_mode(WIFI_MODE_NULL);           // Kein AP oder Client-Modus\n"
	    		+ "    //esp_wifi_start();\n"
	    		+ "    esp_wifi_set_promiscuous(true);              // Promiscuous-Modus aktivieren\n"
	    		+ "    esp_wifi_set_promiscuous_rx_cb(&promisc_cb); // Callback setzen\n"
	    		+ "    Sniff_channel = ChanMin;\n"
	    		+ "    esp_wifi_set_channel(Sniff_channel, WIFI_SECOND_CHAN_NONE);\n"
	    		+ "    esp_wifi_set_promiscuous(true);              // Promiscuous-Modus aktivieren\n"
	    		+ "    for (Sniff_channel = ChanMin; Sniff_channel <= ChanMax; Sniff_channel++) {\n"
	    		+ "      esp_wifi_set_channel(Sniff_channel, WIFI_SECOND_CHAN_NONE);\n"
	    		+ "      delay(300);            // 300 ms per channel\n"
	    		+ "    }\n"
	    		+ "    esp_wifi_set_promiscuous(false); // Promiscuous-Modus deaktivieren\n"
	    		+ "  #endif\n"
	    		+ "  mycount = SnifferCountDevices(MinRSSI,timeout,myMAC,randMAC,mydisplay); // Anzeige/zaehlen der Clients \n"
	    		+ "  return mycount;\n"
	    		+ "}";

	    translator.addDefinitionCommand(Funct);
        
	    
	    return codePrefix + "WiFiPaxCounter("+rssi+","+time+","+channel+","+mac.toLowerCase()+"," +display+")"+ codeSuffix;
	 	}
}

