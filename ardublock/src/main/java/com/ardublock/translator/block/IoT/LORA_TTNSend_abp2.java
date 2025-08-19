package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class LORA_TTNSend_abp2  extends TranslatorBlock {

	public LORA_TTNSend_abp2 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{	translator.addHeaderFile("arduino_lmic_hal_boards.h");
		translator.addHeaderFile("lmic.h");
		translator.addHeaderFile("hal/hal.h");
		translator.addHeaderFile("IoTW_LMIC.h");
		translator.addHeaderFile("#define IOTW_LORA_DEEPSLEEP");
		translator.setLORAProgram(true);   
		
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
		
/*
		String PinMapping = "// -------- LoRa PinMapping \n" + 
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
				+ "    .dio = {DI0, 33,  32}\r\n"
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
	*/
		
		
		String PinMapping = "// -------- LoRa PinMapping \r\n"
				+ "const lmic_pinmap lmic_pins = {  \r\n"
				+ "  .nss = IOTW_GPIO_LMIC_NSS,     \n"
				+ "  .rxtx = LMIC_UNUSED_PIN,  // For placeholder only, Do not connected on RFM92/RFM95\r\n"
				+ "  .rst = IOTW_GPIO_LMIC_RST,      \r\n"
				+ "  .dio = {IOTW_GPIO_LMIC_DIO0, IOTW_GPIO_LMIC_DIO1, LMIC_UNUSED_PIN}\r\n"
				+ "};\r\n";
				translator.addDefinitionCommand(PinMapping);
				translator.addDefinitionCommand("int IOTW_debug_level = IOTW_DEBUG_LEVEL; // Debug print auch in den IOTW_ Libs nutzen\n");
				
		
		
		translator.addDefinitionCommand(PinMapping);
		
		
		String devadd,appkey,netkey,ret,wert,SFtxt, ADR_CMD;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    devadd= translatorBlock.toCode();

		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    netkey = translatorBlock.toCode();

		translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    appkey = translatorBlock.toCode();
	    
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(3);
	    if (translatorBlock==null) { 
          SFtxt = "0";
	    } else SFtxt = translatorBlock.toCode();
		
	    int SF = Integer.parseInt(SFtxt);
	    
	    if (SF > 0 ) {
	       ADR_CMD = "  LMIC_setAdrMode(0); \n LMIC_setDrTxpow(DR_SF"+(SF+6)+", 14);\n"; 	
	    } else {
		   ADR_CMD = "  LMIC_setAdrMode(1); \n LMIC_setDrTxpow(DR_SF7, 14); \n"; 	
	    } 
	
	    
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
	    
	    String init = "// -- initialize LoraWAN LMIC structure\n"
				+ "void LoRaWAN_Start_ABP(int fromRTCMem) { // using ABP-Communication \n" 	
				+ "  os_init();             // LMIC LoraWAN\n"
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
                + "  LMIC.dn2Dr = DR_SF9;	     // TTN uses SF9 for its RX2 window.\n"
		        + ADR_CMD
		        + "  LMIC_setClockError(MAX_CLOCK_ERROR * 5 / 100); // timing difference esp clock\n"
		        + "  if  (fromRTCMem) {"
		        +" #ifdef IOTW_LORA_DEEPSLEEP \n"
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
			
			
            String setup = "  LoRaWAN_Start_ABP(true); // Prepare LMIC-Engine\n";
			translator.addSetupCommand(setup);
		
		
 	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(4);
	    String port = translatorBlock.toCode();
		
	    int count = 0;
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(5);
  	       wert ="int wert=round("+translatorBlock.toCode()+"*1000);\n"
     	       + "mydata[0] = wert >> 16; mydata[1] = wert >> 8; mydata[2] = wert ;\n";
  	       count = 3;
	    translatorBlock = this.getTranslatorBlockAtSocket(6);
	    if (translatorBlock!=null) {
	       wert +="wert=round("+translatorBlock.toCode()+"*1000);\n"
	    	       + "mydata[3] = wert >> 16; mydata[4] = wert >> 8; mydata[5] = wert ;\n";
	  	        count = 6;
	    }
	    translatorBlock = this.getTranslatorBlockAtSocket(7);
	    if (translatorBlock!=null) {
	       wert +="wert=round("+translatorBlock.toCode()+"*1000);\n"
	    	       + "mydata[6] = wert >> 16; mydata[7] = wert >> 8; mydata[8] = wert ;\n";
	       count = 9;
	    }
	 	translatorBlock = this.getTranslatorBlockAtSocket(8);
	    if (translatorBlock!=null) {
		   wert +="wert=round("+translatorBlock.toCode()+"*1000);\n"
			      + "mydata[9] = wert >> 16; mydata[10] = wert >> 8; mydata[11] = wert ;\n";

	       count = 12;
	    }
	    translatorBlock = this.getTranslatorBlockAtSocket(9);
	    if (translatorBlock!=null) {
		   wert +="wert=round("+translatorBlock.toCode()+"*1000);\n"
      	         + "mydata[12] = wert >> 16; mydata[13] = wert >> 8; mydata[14] = wert ;\n";
	       count = 15;
	    }	
    	translatorBlock = this.getTranslatorBlockAtSocket(10);
	    if (translatorBlock!=null) {
		    wert +="wert=round("+translatorBlock.toCode()+"*1000);\n"
			     + "mydata[15] = wert >> 16; mydata[16] = wert >> 8; mydata[17] = wert ;\n";
	        count = 18;
	    }

	    ret = "\n{ //Block------------------------------ send data to network\n"
	    		+ "int port = " + port + ";\n"
	    		+ "static uint8_t mydata["+ count + "];\n"
	       		+ wert 
		        + "  LoRaWAN_Event_No_Join = 0;\r\n"
		        + "  LoRaWAN_Event_TxComplete = 0;\r\n"
		        + "  // Check if there is not a current TX/RX job running, wait until finished\r\n"
		        + "  if (!((LMIC.opmode & OP_TXRXPEND) || (LMIC.opmode & OP_TXDATA) || (LMIC.opmode & OP_JOINING))) {\r\n"
		        + "    if (LMIC_setTxData2(port, mydata, sizeof(mydata), 0)) {\r\n"
		        + "        IOTW_PRINTLN(F(\"❌ setTxData: error\"));\r\n"
		        + "    }\r\n"
		        + "  }\r\n"
		        + "  uint32_t tout = millis()+30000; // harter Timeout\r\n"
		        + "    while ((millis() < tout && ((LMIC.opmode & OP_TXRXPEND) || (LMIC.opmode & OP_TXDATA) || (LMIC.opmode & OP_POLL) || (LMIC.opmode & OP_JOINING))) && !LoRaWAN_Event_No_Join) {\r\n"
		        + "      yield();\r\n"
		        + "      os_runloop_once_sleep();\r\n"
		        + "  }\r\n"
		        + "  if (millis() < tout) IOTW_PRINTLN(F(\"Tx finished ✅\")); "
		        + "    else IOTW_PRINTLN(F(\"❌ Timeout\")); "
		        + "} // Block \n";

	    
		translator.setLORAProgram(true);
    	    
        return codePrefix + ret + codeSuffix;
	 	}
}



/* vor lightsleep ota ret = "\n{ //Block------------------------------ send data to network\n"
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
		          + "          IOTW_PRINTLN(F(\"------------------------  setTxData: error\"));\r\n"
		          + "        } else { // err = 0, packet queued\r\n"
		          + "          IOTW_PRINTLN(F(\"Packet queued: \"));\r\n"
		          + "          long m = millis();\r\n"
		          + "          while (LMIC.opmode & OP_TXDATA) { //(!LoRaWAN_Tx_Ready) {            \r\n"
		          + "            yield();\r\n"
		          + "            os_runloop_once_sleep();\r\n"
		          + "            if ((millis()- m) > tout ) {\r\n"
		          + "              IOTW_PRINT(F(\"timeout abort\"));\r\n"
		          + "              err = 1;\r\n"
		          + "              break;\r\n"
		          + "            }\r\n"
		          + "          }      \r\n"
		          + "          if (err == 0) {\r\n"
		          + "            IOTW_PRINT(F(\" packet send \"));\r\n"
		          + "            Retry = 0;\r\n"
		          + "          }\r\n"
		          + "        }\r\n"
		          + "      }\r\n"
		          + "      if (err) { \r\n"
		          + "        Retry=Retry-1;\r\n"
		          + "        IOTW_PRINT(F(\", retry \"));\r\n"
		          + "        long m = millis();\r\n"
		          + "        while ((millis()-m) < tout) {\r\n"
		          + "          yield();\r\n"
		          + "          os_runloop_once_sleep();\r\n"
		          + "        }\r\n"
		          + "      }\r\n"
		          + "    } \r\n"
		          + "    \r\n"
		          + "    if ((LMIC.opmode & OP_TXRXPEND) || (LMIC.opmode & OP_TXDATA) || (LMIC.opmode & OP_POLL) || (LMIC.opmode & OP_JOINING)) {\r\n"
		          + "      IOTW_PRINT(F(\"some MAC-TXRX activ, mode = \"));\r\n"
		          + "      IOTW_PRINTLN(LMIC.opmode,HEX);\r\n"
		          + "      long m = millis();\r\n"
		          + "      while ((LMIC.opmode & OP_TXRXPEND) || (LMIC.opmode & OP_TXDATA) || (LMIC.opmode & OP_POLL) || (LMIC.opmode & OP_JOINING)) {\r\n"
		          + "        yield();\r\n"
		          + "        os_runloop_once_sleep();\r\n"
		          + "        if  ((millis()- m) > tout ) {\r\n"
		          + "              IOTW_PRINTLN(F(\"abort communication, lost job\"));\r\n"
		          + "              break;\r\n"
		          + "        }\r\n"
		          + "      }\r\n"
		          + "    }\r\n"
		          + "    IOTW_PRINTLN(F(\"Tx finished\"));"
		          + " } // Block \n";
*/

