package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTTTNRFM95Send_abp  extends TranslatorBlock {

	public IoTTTNRFM95Send_abp (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{	translator.addHeaderFile("arduino_lmic_hal_boards.h");
		translator.addHeaderFile("lmic.h");
		translator.addHeaderFile("hal/hal.h");
		translator.addHeaderFile("#define LORA_TX_INTERVAL 10");
		translator.addSetupCommand("Serial.begin(115200);");
		translator.setLORAProgram(true);   
		
		String vardef = "volatile int LoRaWAN_Tx_Ready   = 0; // Flag for Tx Send \n" + 
	    		"long         LoRaWAN_ms_Wakeup  = 0; // ms at start message\n" + 
	    		"long         LoRaWAN_ms_EmExit  = 0; // max. ms  \n"; 
	    translator.addDefinitionCommand(vardef);
	    
	 	    
	    translator.addDefinitionCommand("int LoRaWAN_Rx_Payload = 0 ;");
	    translator.addDefinitionCommand("int LoRaWAN_Rx_Port = 0 ;");
	    translator.addDefinitionCommand("String LoRaWAN_Rx_Payload_Raw = \"\" ;");
	    
		
		String Defaults=""
			+"void os_getArtEui (u1_t* buf) { }"
		    +"void os_getDevEui (u1_t* buf) { }"
		    +"void os_getDevKey (u1_t* buf) { }";
		translator.addDefinitionCommand(Defaults);
		
		String Dis="/* LoRaWAN LMIC Lib\n"
				     + "Copyright (C) 2014-2016 IBM Corporation\n"
					 + "Copyright (c) 2015 Thomas Telkamp and Matthijs Kooijman\r\n"
					 + "Copyright (c) 2016-2021 MCCI Corporation\n"
					 + "MIT, Disclaimer see https://github.com/mcci-catena/arduino-lmic?tab=MIT-1-ov-file#readme \n"
					 + "*/\n";
		translator.addDefinitionCommand(Dis);
		

		String PinMapping = "// LoraWAN Copyright (c) 2015 Thomas Telkamp and Matthijs Kooijman\n" + 
				"// (c) 2018 Terry Moore, MCCI\n" + 
				"// https://github.com/mcci-catena/arduino-lmic\n" + 
				"// -------- LoRa PinMapping FeatherWing Octopus\n" + 
				"\n"  
				+ "#if defined(BOARD_TTGO_V1)\r\n"
				+ " #define SCK     5    // GPIO5  -- SX1278's SCK\r\n"
				+ " #define MISO    19   // GPIO19 -- SX1278's MISO\r\n"
				+ " #define MOSI    27   // GPIO27 -- SX1278's MOSI\r\n"
				+ " #define SS      18   // GPIO18 -- SX1278's CS\r\n"
				+ " #define RST     23   // GPIO14 -- SX1278's RESET\r\n"
				+ " #define DI0     26   // GPIO26 -- SX1278's IRQ(Interrupt Request)\r\n"
				+ "const lmic_pinmap lmic_pins = {\r\n"
				+ "    .nss = SS, \r\n"
				+ "    .rxtx = LMIC_UNUSED_PIN,\r\n"
				+ "    .rst = RST,\r\n"
				+ "    .dio = {/*dio0*/ DI0, /*dio1*/ 33, /*dio2*/ 32}\r\n"
				+ "};\r\n"
				+ "#else\r\n"
				+ "const lmic_pinmap lmic_pins = {  \r\n"
				+ "  .nss = LMIC_NSS,                            // Connected to pin D\r\n"
				+ "  .rxtx = LMIC_UNUSED_PIN,             // For placeholder only, Do not connected on RFM92/RFM95\r\n"
				+ "  .rst = LMIC_UNUSED_PIN,              // Needed on RFM92/RFM95? (probably not) D0/GPIO16 \r\n"
				+ "  .dio = {\r\n"
				+ "    LMIC_DIO, LMIC_DIO, LMIC_UNUSED_PIN           }\r\n"
				+ "};\r\n"
				+ "#endif\n"
				+ "#if defined(XIAO_ESP32S3)\n"
				+ "const Arduino_LMIC::HalConfiguration_t myConfig;\r\n"
				+ "const lmic_pinmap *pPinMap = Arduino_LMIC::GetPinmap_XIAO_S3_WIO_SX1262();"
				+ "#endif\n"; 

		
				translator.addDefinitionCommand(PinMapping);
				
		
		
		translator.addDefinitionCommand(PinMapping);
		
		
		String devadd,appkey,netkey,ret,wert;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    devadd= translatorBlock.toCode();

		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    netkey = translatorBlock.toCode();

		translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    appkey = translatorBlock.toCode();
	    
	    String Hex, ascii;

	    devadd = devadd.replace(" ","");
	    devadd = devadd.replace("\"","");
		
	    if (devadd.length() < 8) throw new SocketNullException(this.blockId);
	    Hex = "static const u4_t DEVADDR = 0x"+devadd+";\n";
	    translator.addDefinitionCommand(Hex);

	    ascii = appkey;
	    if (appkey.length() < 32+2) throw new SocketNullException(this.blockId);

	    Hex = "static const u1_t PROGMEM APPSKEY[16]={"
      	    +  "0x" + ascii.charAt(1)+ascii.charAt(2)+","
	  	    +  "0x" + ascii.charAt(3)+ascii.charAt(4)+","
	  	    +  "0x" + ascii.charAt(5)+ascii.charAt(6)+","
	  	    +  "0x" + ascii.charAt(7)+ascii.charAt(8)+","
	  	    +  "0x" + ascii.charAt(9)+ascii.charAt(10)+","
	  	    +  "0x" + ascii.charAt(11)+ascii.charAt(12)+","
	  	    +  "0x" + ascii.charAt(13)+ascii.charAt(14)+","
	       	+  "0x" + ascii.charAt(15)+ascii.charAt(16)+","
	        +  "0x" + ascii.charAt(17)+ascii.charAt(18)+","
	    	+  "0x" + ascii.charAt(19)+ascii.charAt(20)+","
	   		+  "0x" + ascii.charAt(21)+ascii.charAt(22)+","
	    	+  "0x" + ascii.charAt(23)+ascii.charAt(24)+","
	    	+  "0x" + ascii.charAt(25)+ascii.charAt(26)+","
	    	+  "0x" + ascii.charAt(27)+ascii.charAt(28)+","
	    	+  "0x" + ascii.charAt(29)+ascii.charAt(30)+","
   	    	+  "0x" + ascii.charAt(31)+ascii.charAt(32) + "};\n";
	    translator.addDefinitionCommand(Hex);
	    
	    ascii = netkey;
	    if (appkey.length() < 32+2) throw new SocketNullException(this.blockId);
	    Hex = "static const u1_t PROGMEM NWKSKEY[16]={"
      	    +  "0x" + ascii.charAt(1)+ascii.charAt(2)+","
	  	    +  "0x" + ascii.charAt(3)+ascii.charAt(4)+","
	  	    +  "0x" + ascii.charAt(5)+ascii.charAt(6)+","
	  	    +  "0x" + ascii.charAt(7)+ascii.charAt(8)+","
	  	    +  "0x" + ascii.charAt(9)+ascii.charAt(10)+","
	  	    +  "0x" + ascii.charAt(11)+ascii.charAt(12)+","
	  	    +  "0x" + ascii.charAt(13)+ascii.charAt(14)+","
	       	+  "0x" + ascii.charAt(15)+ascii.charAt(16)+","
	        +  "0x" + ascii.charAt(17)+ascii.charAt(18)+","
	    	+  "0x" + ascii.charAt(19)+ascii.charAt(20)+","
	   		+  "0x" + ascii.charAt(21)+ascii.charAt(22)+","
	    	+  "0x" + ascii.charAt(23)+ascii.charAt(24)+","
	    	+  "0x" + ascii.charAt(25)+ascii.charAt(26)+","
	    	+  "0x" + ascii.charAt(27)+ascii.charAt(28)+","
	    	+  "0x" + ascii.charAt(29)+ascii.charAt(30)+","
   	    	+  "0x" + ascii.charAt(31)+ascii.charAt(32) + "};\n";
	    translator.addDefinitionCommand(Hex);
	    
	    
	   
		
		String CRC = "// Berechne CRC-Prüfsumme für RTC-RAM \n"
				+   "uint32_t RTCcalculateCRC32(const uint8_t *data, size_t length) {\n" + 
					"  uint32_t crc = 0xffffffff;\n" + 
					"  while (length--) {\n" + 
					"    uint8_t c = *data++;\n" + 
					"    for (uint32_t i = 0x80; i > 0; i >>= 1) {\n" + 
					"      bool bit = crc & 0x80000000;\n" + 
					"      if (c & i) {\n" + 
					"        bit = !bit;\n" + 
					"      }\n" + 
					"      crc <<= 1;\n" + 
					"      if (bit) {\n" + 
					"        crc ^= 0x04c11db7;\n" + 
					"      }\n" + 
					"    }\n" + 
					"  }\n" + 
					"  return crc;\n" + 
					"}";
			translator.addDefinitionCommand(CRC);
		
		
			String LoadStore = "// ESP8266:  Load/Store LoRa LMIC to RTC-Mem \n" + 
					"#if defined(ESP8266) && defined(USE_DEEPSLEEP) \n" +
 				    "void LoadLMICFromRTC_ESP8266() {\n" + 
					"  lmic_t RTC_LMIC;\n" + 
					"  uint32_t crcOfData;\n" + 
					"  if (sizeof(lmic_t) <= 512) {\n" + 
					"     ESP.rtcUserMemoryRead(1, (uint32_t*) &RTC_LMIC, sizeof(RTC_LMIC));\n" + 
					"     ESP.rtcUserMemoryRead(0, (uint32_t*) &crcOfData, sizeof(crcOfData));\n" + 
					"     uint32_t crcOfData_RTC = RTCcalculateCRC32((uint8_t*) &RTC_LMIC, sizeof(RTC_LMIC));\n" + 
					"     if (crcOfData != crcOfData_RTC) {\n" + 
					"       Serial.println(\"CRC32 in RTC memory doesn't match CRC32 of data. Data is probably invalid!\");\n" + 
					"     } else {\n" + 
					"       Serial.print(F(\"load LMIC from RTC, FrameCounter =  \"));\n" + 
					"       LMIC = RTC_LMIC;\n" + 
					"       Serial.println(LMIC.seqnoUp);"+
					"     }  \n" + 
					"  } else {\n" + 
					"   Serial.println(F(\"sizelimit RTC-Mem, #define LMIC_ENABLE_long_messages in config.h\"));\n" + 
					"  };\n" + 
					"}; \n" + 
					" \n" + 
					"void SaveLMICToRTC_ESP8266(int deepsleep_sec) {\n" + 
					"  if (sizeof(lmic_t) <= 512) {\n" + 
					"    Serial.println(F(\"Save LMIC to RTC and deepsleep\"));\n" + 
					"    unsigned long now = millis();\n" + 
					"    // EU Like Bands\n" + 
					"#if defined(CFG_LMIC_EU_like)\n" + 
					"   // Serial.println(F(\"Reset CFG_LMIC_EU_like band avail\"));\n" + 
					"    for (int i = 0; i < MAX_BANDS; i++)\n" + 
					"    {\n" + 
					"      ostime_t correctedAvail = LMIC.bands[i].avail - ((now / 1000.0 + deepsleep_sec) * OSTICKS_PER_SEC);\n" + 
					"      if (correctedAvail < 0)\n" + 
					"      {\n" + 
					"        correctedAvail = 0;\n" + 
					"      }\n" + 
					"      LMIC.bands[i].avail = correctedAvail;\n" + 
					"    }\n" + 
					"\n" + 
					"    LMIC.globalDutyAvail = LMIC.globalDutyAvail - ((now / 1000.0 + deepsleep_sec) * OSTICKS_PER_SEC);\n" + 
					"    if (LMIC.globalDutyAvail < 0)\n" + 
					"    {\n" + 
					"      LMIC.globalDutyAvail = 0;\n" + 
					"    }\n" + 
					"#else\n" + 
					"    //Serial.println(F(\"No DutyCycle recalculation function!\"));\n" + 
					"#endif\n" + 
					"    // Write to RTC\n" + 
					"    uint32_t crcOfData = RTCcalculateCRC32((uint8_t*) &LMIC, sizeof(LMIC));\n" + 
					"    ESP.rtcUserMemoryWrite(1, (uint32_t*) &LMIC, sizeof(LMIC));\n" + 
					"    ESP.rtcUserMemoryWrite(0, (uint32_t*) &crcOfData, sizeof(crcOfData));\n" + 
					"  } \n" + 
					"  else {\n" + 
					"    Serial.println(F(\"sizelimit RTC-Mem, #define LMIC_ENABLE_long_messages in config.h\"));\n" + 
					"  }\n" + 
					"};\n"+
					"#endif \n";
            translator.addDefinitionCommand(LoadStore);
       
			LoadStore = "// ESP32 Load/Store LoRa LMIC to RTC-Mem \n" + 
					"#if defined(ESP32) && defined(USE_DEEPSLEEP) \n" +
					"void LoadLMICFromRTC_ESP32() {\n" + 
					"  if ((RTC_LMIC.seqnoUp != 0)) {\n" + 
					"     Serial.print(F(\"load LMIC from RTC, Frame = \"));\n" + 
					"     Serial.println(RTC_LMIC.seqnoUp);\n" + 
					"     LMIC = RTC_LMIC;\n" + 
					"  } else {\n" + 
					"     Serial.print(F(\"no valid LMIC Data, start from scratch \"));\n" + 
					"  };  \n" + 
					"}; \n" + 
					" \n" + 
					" \n" + 
					"void SaveLMICToRTC_ESP32(int deepsleep_sec) {\n" + 
					"    Serial.println(F(\"Save LMIC to RTC and deepsleep\"));\n" + 
					"    unsigned long now = millis();\n" + 
					"    // EU Like Bands\n" + 
					"#if defined(CFG_LMIC_EU_like)\n" + 
					"   // Serial.println(F(\"Reset CFG_LMIC_EU_like band avail\"));\n" + 
					"    for (int i = 0; i < MAX_BANDS; i++)\n" + 
					"    {\n" + 
					"      ostime_t correctedAvail = LMIC.bands[i].avail - ((now / 1000.0 + deepsleep_sec) * OSTICKS_PER_SEC);\n" + 
					"      if (correctedAvail < 0)\n" + 
					"      {\n" + 
					"        correctedAvail = 0;\n" + 
					"      }\n" + 
					"      LMIC.bands[i].avail = correctedAvail;\n" + 
					"    }\n" + 
					"\n" + 
					"    LMIC.globalDutyAvail = LMIC.globalDutyAvail - ((now / 1000.0 + deepsleep_sec) * OSTICKS_PER_SEC);\n" + 
					"    if (LMIC.globalDutyAvail < 0)\n" + 
					"    {\n" + 
					"      LMIC.globalDutyAvail = 0;\n" + 
					"    }\n" + 
					"#else\n" + 
					"    //Serial.println(F(\"No DutyCycle recalculation function!\"));\n" + 
					"#endif\n" + 
					"    // Write to RTC\n" + 
					"    RTC_LMIC = LMIC;\n" + 
					"  }; \n" + 
				//	"};\n"+
					"#endif \n";

		    translator.addDefinitionCommand(LoadStore);
		
		
	
		    String Event = "void onEvent (ev_t ev) {\n" + 
					"    Serial.print(os_getTime());\n" + 
					"    Serial.print(\": \");\n" + 
					"    switch(ev) {\n" + 
					"        case EV_SCAN_TIMEOUT:\n" + 
					"            Serial.println(F(\"EV_SCAN_TIMEOUT\"));\n" + 
					"            break;\n" + 
					"        case EV_BEACON_FOUND:\n" + 
					"            Serial.println(F(\"EV_BEACON_FOUND\"));\n" + 
					"            break;\n" + 
					"        case EV_BEACON_MISSED:\n" + 
					"            Serial.println(F(\"EV_BEACON_MISSED\"));\n" + 
					"            break;\n" + 
					"        case EV_BEACON_TRACKED:\n" + 
					"            Serial.println(F(\"EV_BEACON_TRACKED\"));\n" + 
					"            break;\n" + 
					"        case EV_JOINING:\n" + 
					"            Serial.println(F(\"EV_JOINING\"));\n" + 
					"            break;\n" + 
					"        case EV_JOINED:\n" + 
					"            Serial.println(F(\"EV_JOINED\"));\n" + 
					"            LoRaWAN_Tx_Ready =  !(LMIC.opmode & OP_TXDATA); // otherwise joined without TX blocks queue\n" + 
					"            break;\n" + 
					"        /*\n" + 
					"        || This event is defined but not used in the code. No\n" + 
					"        || point in wasting codespace on it.\n" + 
					"        ||\n" + 
					"        || case EV_RFU1:\n" + 
					"        ||     Serial.println(F(\"EV_RFU1\"));\n" + 
					"        ||     break;\n" + 
					"        */\n" + 
					"        case EV_JOIN_FAILED:\n" + 
					"            Serial.println(F(\"EV_JOIN_FAILED\"));\n" + 
					"            break;\n" + 
					"        case EV_REJOIN_FAILED:\n" + 
					"            Serial.println(F(\"EV_REJOIN_FAILED\"));\n" + 
					"            break;\n" + 
					"        case EV_TXCOMPLETE:\n" + 
					"            Serial.println(F(\"EV_TXCOMPLETE (includes waiting for RX windows)\"));\n" + 
					"            if (LMIC.txrxFlags & TXRX_ACK)\n" + 
					"              Serial.println(F(\"Received ack\"));\n" + 
					"            if (LMIC.dataLen) {\n" + 
					"              Serial.println(F(\"Received \"));\n" + 
					"              Serial.println(LMIC.dataLen);\n" + 
					"              Serial.println(F(\" bytes of payload\"));\n" + 
					"              LoRaWAN_Rx_Payload = 0; // Payload IoT-Werkstatt\n" +
					"              LoRaWAN_Rx_Payload_Raw = \"\"; // Payload IoT-Werkstatt\n" +
					"              LoRaWAN_Rx_Port    = LMIC.frame[LMIC.dataBeg-1];"+
					"              String Zeichen = \"\";"+
					"              for (int i = 0;i<LMIC.dataLen;i++) { \n" + 
					"                Serial.println(LMIC.frame[i+ LMIC.dataBeg],HEX);\n" + 
					"                LoRaWAN_Rx_Payload = 256*LoRaWAN_Rx_Payload+LMIC.frame[i+ LMIC.dataBeg];\n" + 
					"                Zeichen = String(LMIC.frame[i+ LMIC.dataBeg],HEX);\n"+
					"                if (Zeichen.length() == 1) Zeichen = \"0\"+Zeichen;\n"+
					"                LoRaWAN_Rx_Payload_Raw += Zeichen;\n" + 
					"              }\n" +
					"              #ifdef LORA_DOWNLINK_ENABLE \n"+
					"               LoRaWAN_DownlinkCallback();\n"+
					"              #endif \n"+
					"            }\n" + 
					"            LoRaWAN_Tx_Ready =  !(LMIC.opmode & OP_TXDATA);\n"+
					"            // Schedule next transmission\n" + 
					"            // os_setTimedCallback(&sendjob, os_getTime()+sec2osticks(TX_INTERVAL), do_send);\n" + 
					"            break;\n" + 
					"        case EV_LOST_TSYNC:\n" + 
					"            Serial.println(F(\"EV_LOST_TSYNC\"));\n" + 
					"            break;\n" + 
					"        case EV_RESET:\n" + 
					"            Serial.println(F(\"EV_RESET\"));\n" + 
					"            break;\n" + 
					"        case EV_RXCOMPLETE:\n" + 
					"            // data received in ping slot\n" + 
					"            Serial.println(F(\"EV_RXCOMPLETE\"));\n" + 
					"            break;\n" + 
					"        case EV_LINK_DEAD:\n" + 
					"            Serial.println(F(\"EV_LINK_DEAD\"));\n" + 
					"            break;\n" + 
					"        case EV_LINK_ALIVE:\n" + 
					"            Serial.println(F(\"EV_LINK_ALIVE\"));\n" + 
					"            break;\n" + 
					"        /*\n" + 
					"        || This event is defined but not used in the code. No\n" + 
					"        || point in wasting codespace on it.\n" + 
					"        ||\n" + 
					"        || case EV_SCAN_FOUND:\n" + 
					"        ||    Serial.println(F(\"EV_SCAN_FOUND\"));\n" + 
					"        ||    break;\n" + 
					"        */\n" + 
					"        case EV_TXSTART:\n" + 
					"            Serial.println(F(\"EV_TXSTART\"));\n" + 
					"            break;\n" + 
					"        case EV_TXCANCELED:\n" + 
					"            Serial.println(F(\"EV_TXCANCELED\"));\n" + 
					"            break;\n" + 
					"        case EV_RXSTART:\n" + 
					"            /* do not print anything -- it wrecks timing */\n" + 
					"            break;\n" + 
					"        case EV_JOIN_TXCOMPLETE:\n" + 
					"            Serial.println(F(\"EV_JOIN_TXCOMPLETE: no JoinAccept\"));\n" + 
					"            break;\n" + 
					"        default:\n" + 
					"            Serial.print(F(\"Unknown event: \"));\n" + 
					"            Serial.println((unsigned) ev);\n" + 
					"            break;\n" + 
					"    }\n" + 
					"}";
			


	
		translator.addDefinitionCommand(Event);
		
		String init = "// -- initialize LoraWAN LMIC structure\n"
				+ "void LoRaWAN_Start(int fromRTCMem) { // using ABP-Communication \n" 	
				+ "  #if defined(BOARD_TTGO_V1)\r\n"
				+ "   SPI.begin(SCK,MISO,MOSI,SS);\r\n"
				+ "  #endif\n"
				+ "#if defined(XIAO_ESP32S3)\n"
				+ "  os_init_ex(pPinMap);\n"
				+ "#else\n"
				+ "  os_init();             // LMIC LoraWAN\n"
				+ "#endif\n "
				+ "  LMIC_reset();          // Reset the MAC state \n"
			    + "  // Set static session parameters. Instead of dynamically establishing a session\n"
				+ "  // by joining the network, precomputed session parameters are be provided.\n"
				+ "  uint8_t appskey[sizeof(APPSKEY)];\n"
				+ "  uint8_t nwkskey[sizeof(NWKSKEY)];\n"
				+ "  memcpy_P(appskey, APPSKEY, sizeof(APPSKEY));\n"
				+ "  memcpy_P(nwkskey, NWKSKEY, sizeof(NWKSKEY));\n"
				+ "  LMIC_setSession (0x13, DEVADDR, nwkskey, appskey);\n"
				+ "// Set up the channels used by the Things Network, which corresponds \n"
	            + "// to the defaults of most gateways. Without this, only three base\n"
	            + "// channels from the LoRaWAN specification are used\n" 
	            + "  LMIC_setupChannel(0, 868100000, DR_RANGE_MAP(DR_SF12, DR_SF7),  BAND_CENTI);      // g-band\n"		
	            + "  LMIC_setupChannel(1, 868300000, DR_RANGE_MAP(DR_SF12, DR_SF7B), BAND_CENTI);      // g-band\n"
		        + "  LMIC_setupChannel(2, 868500000, DR_RANGE_MAP(DR_SF12, DR_SF7),  BAND_CENTI);      // g-band\n"
		        + "  LMIC_setupChannel(3, 867100000, DR_RANGE_MAP(DR_SF12, DR_SF7),  BAND_CENTI);      // g-band\n"
		        + "  LMIC_setupChannel(4, 867300000, DR_RANGE_MAP(DR_SF12, DR_SF7),  BAND_CENTI);      // g-band\n"
		        + "  LMIC_setupChannel(5, 867500000, DR_RANGE_MAP(DR_SF12, DR_SF7),  BAND_CENTI);      // g-band\n"
		        + "  LMIC_setupChannel(6, 867700000, DR_RANGE_MAP(DR_SF12, DR_SF7),  BAND_CENTI);      // g-band\n"
		        + "  LMIC_setupChannel(7, 867900000, DR_RANGE_MAP(DR_SF12, DR_SF7),  BAND_CENTI);      // g-band\n"
		        + "  LMIC_setupChannel(8, 868800000, DR_RANGE_MAP(DR_FSK,  DR_FSK),  BAND_MILLI);      // g2-band\n"
		        + "  LMIC_setLinkCheckMode(0);   // enable/disable link check validation\n"  
                + "  LMIC_setAdrMode(1);         // enable/disable ADR"
		        + "  LMIC.dn2Dr = DR_SF9;	     // TTN uses SF9 for its RX2 window.\n"
		        + "  LMIC_setDrTxpow(DR_SF7,14); // Set data rate and transmit power for uplink\n"
	            + "  LMIC_setClockError(MAX_CLOCK_ERROR * 5 / 100); // timing difference esp clock\n"
		        + "  if  (fromRTCMem) {"
		        +" #ifdef LORA_DEEPSLEEP \n"
		        +"   #ifdef ESP32 \n"
		        +"     LoadLMICFromRTC_ESP32(); // restart from deepsleep, get LMIC state from RTC \n"
		        +"   #elif ESP8266 \n"
		        +"     LoadLMICFromRTC_ESP8266(); // restart from deepsleep, get LMIC state from RTC \n"
		        +"   #endif\n"
		        + "#endif\n" 
		        + "  } // continue runing state-maschine\n" + 
		        "}\n" + 
		        "";
		
			translator.addDefinitionCommand(init);
			
			String schedule = "// LoRaLMIC: Wait for RX-Window in light sleep\r\n"
					+ "ostime_t get_next_deadline() {\r\n"
					+ "    // Zugriff auf den nächsten geplanten Job\r\n"
					+ "    if (LMIC.osjob.deadline > os_getTime()) {\r\n"
					+ "        return LMIC.osjob.deadline; // Rückgabe der nächsten Deadline\r\n"
					+ "    }\r\n"
					+ "    return 0; // Keine Deadline vorhanden\r\n"
					+ "}\r\n"
					+ "void os_runloop_once_sleep(){\r\n"
					+ "  os_runloop_once();\r\n"
					+ "  if (get_next_deadline() > 10) {\r\n"
					+ "     esp_sleep_enable_timer_wakeup(get_next_deadline()-1);\r\n"
					+ "     esp_light_sleep_start();\r\n"
					+ "  }\r\n"
					+ "}";
			translator.addDefinitionCommand(schedule);
			
			
			
            String setup = "  LoRaWAN_Start(true); // Prepare LMIC-Engine\n";
			translator.addSetupCommand(setup);
		
		
 	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    String port = translatorBlock.toCode();
		
	    int count = 0;
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(4);
  	       wert ="int wert=round("+translatorBlock.toCode()+"*1000);\n"
     	       + "mydata[0] = wert >> 16; mydata[1] = wert >> 8; mydata[2] = wert ;\n";
  	       count = 3;
	    translatorBlock = this.getTranslatorBlockAtSocket(5);
	    if (translatorBlock!=null) {
	       wert +="wert=round("+translatorBlock.toCode()+"*1000);\n"
	    	       + "mydata[3] = wert >> 16; mydata[4] = wert >> 8; mydata[5] = wert ;\n";
	  	        count = 6;
	    }
	    translatorBlock = this.getTranslatorBlockAtSocket(6);
	    if (translatorBlock!=null) {
	       wert +="wert=round("+translatorBlock.toCode()+"*1000);\n"
	    	       + "mydata[6] = wert >> 16; mydata[7] = wert >> 8; mydata[8] = wert ;\n";
	       count = 9;
	    }
	 	translatorBlock = this.getTranslatorBlockAtSocket(7);
	    if (translatorBlock!=null) {
		   wert +="wert=round("+translatorBlock.toCode()+"*1000);\n"
			      + "mydata[9] = wert >> 16; mydata[10] = wert >> 8; mydata[11] = wert ;\n";

	       count = 12;
	    }
	    translatorBlock = this.getTranslatorBlockAtSocket(8);
	    if (translatorBlock!=null) {
		   wert +="wert=round("+translatorBlock.toCode()+"*1000);\n"
      	         + "mydata[12] = wert >> 16; mydata[13] = wert >> 8; mydata[14] = wert ;\n";
	       count = 15;
	    }	
    	translatorBlock = this.getTranslatorBlockAtSocket(9);
	    if (translatorBlock!=null) {
		    wert +="wert=round("+translatorBlock.toCode()+"*1000);\n"
			     + "mydata[15] = wert >> 16; mydata[16] = wert >> 8; mydata[17] = wert ;\n";
	        count = 18;
	    }

	    ret = "\n{ //Block------------------------------ send data to network\n"
	    		+ "int port = " + port + ";\n"
	    		+ "static uint8_t mydata["+ count + "];\n"
	       		+ wert +
		          " int Retry = 2, tout = 30000,  err=1;\r\n"
		          + "    while (Retry > 0) {\r\n"
		          + "      // Check if there is not a current TX/RX job running, wait until finished\r\n"
		          + "      if (!((LMIC.opmode & OP_TXRXPEND) || (LMIC.opmode & OP_TXDATA) || (LMIC.opmode & OP_POLL) || (LMIC.opmode & OP_JOINING))) {\r\n"
		          + "        //LoRaWAN_Tx_Ready = 0;\r\n"
		          + "        err = LMIC_setTxData2(port, mydata, sizeof(mydata), 0);     // Sende  \r\n"
		          + "        if (err==1) {\r\n"
		          + "          Serial.println(F(\"------------------------  setTxData: error\"));\r\n"
		          + "        } else { // err = 0, packet queued\r\n"
		          + "          Serial.println(F(\"Packet queued: \"));\r\n"
		          + "          long m = millis();\r\n"
		          + "          while (LMIC.opmode & OP_TXDATA) { //(!LoRaWAN_Tx_Ready) {            \r\n"
		          + "            yield();\r\n"
		          + "            os_runloop_once_sleep();\r\n"
		          + "            if ((millis()- m) > tout ) {\r\n"
		          + "              Serial.print(F(\"timeout abort\"));\r\n"
		          + "              err = 1;\r\n"
		          + "              break;\r\n"
		          + "            }\r\n"
		          + "          }      \r\n"
		          + "          if (err == 0) {\r\n"
		          + "            Serial.print(F(\" packet send \"));\r\n"
		          + "            Retry = 0;\r\n"
		          + "          }\r\n"
		          + "        }\r\n"
		          + "      }\r\n"
		          + "      if (err) { \r\n"
		          + "        Retry=Retry-1;\r\n"
		          + "        Serial.print(F(\", retry \"));\r\n"
		          + "        long m = millis();\r\n"
		          + "        while ((millis()-m) < tout) {\r\n"
		          + "          yield();\r\n"
		          + "          os_runloop_once_sleep();\r\n"
		          + "        }\r\n"
		          + "      }\r\n"
		          + "    } \r\n"
		          + "    \r\n"
		          + "    if ((LMIC.opmode & OP_TXRXPEND) || (LMIC.opmode & OP_TXDATA) || (LMIC.opmode & OP_POLL) || (LMIC.opmode & OP_JOINING)) {\r\n"
		          + "      Serial.print(F(\"some MAC-TXRX activ, mode = \"));\r\n"
		          + "      Serial.println(LMIC.opmode,HEX);\r\n"
		          + "      long m = millis();\r\n"
		          + "      while ((LMIC.opmode & OP_TXRXPEND) || (LMIC.opmode & OP_TXDATA) || (LMIC.opmode & OP_POLL) || (LMIC.opmode & OP_JOINING)) {\r\n"
		          + "        yield();\r\n"
		          + "        os_runloop_once_sleep();\r\n"
		          + "        if  ((millis()- m) > tout ) {\r\n"
		          + "              Serial.println(F(\"abort communication, lost job\"));\r\n"
		          + "              break;\r\n"
		          + "        }\r\n"
		          + "      }\r\n"
		          + "    }\r\n"
		          + "    Serial.println(F(\"Tx finished\"));"
		          + " } // Block \n";
	    
		translator.setLORAProgram(true);
    	    
        return codePrefix + ret + codeSuffix;
	 	}
}

