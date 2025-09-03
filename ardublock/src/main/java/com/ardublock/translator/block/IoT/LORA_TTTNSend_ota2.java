package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class LORA_TTTNSend_ota2  extends TranslatorBlock {

	public LORA_TTTNSend_ota2 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("arduino_lmic_hal_boards.h");
		translator.addHeaderFile("lmic.h");
		translator.addHeaderFile("hal/hal.h");
//		translator.addHeaderFile("#define LORA_TX_INTERVAL 10");
		translator.addHeaderFile("IoTW_LMIC.h");
		translator.addHeaderFile("#define IOTW_LORA_DEEPSLEEP");
		translator.setLORAProgram(true);   
	
		String Dis="/* LoRaWAN LMIC Lib\n"
			     + "Copyright (C) 2014-2016 IBM Corporation\n"
				 + "Copyright (c) 2015 Thomas Telkamp and Matthijs Kooijman\r\n"
				 + "Copyright (c) 2016-2021 MCCI Corporation\n"
				 + "MIT, Disclaimer see https://github.com/mcci-catena/arduino-lmic?tab=MIT-1-ov-file#readme \n"
				 + "*/\n";
	translator.addDefinitionCommand(Dis);

	String PostJoin = "// LoRaWAN TX startet nach dem Join mit SF10 (wird dann vom System ggf. runtergestuft) \n"
			+ "#define IOTW_LMIC_JOIN_TOUT 120000 \n"
			+ "#define IOTW_LMIC_JOIN_MAXFAIL 3 \n"
					+ "#if defined(ESP32)\r\n"
			+ "RTC_DATA_ATTR bool LMIC_postJoinConfigured = false;\r\n"
			+ "RTC_DATA_ATTR uint8_t LMIC_JoinFailCount = 0;\r\n"
			+ "#else\r\n"
			+ "bool LMIC_postJoinConfigured = false;\r\n"
			+ "bool uint8_t LMIC_JoinFailCount = 0;\r\n"
			+ "#endif\r\n";
	translator.addDefinitionCommand(PostJoin);
		
	
		
	String PinMapping = "// -------- LoRa PinMapping \r\n"
			+ "const lmic_pinmap lmic_pins = {  \r\n"
			+ "  .nss = IOTW_GPIO_LMIC_NSS,     \n"
			+ "  .rxtx = LMIC_UNUSED_PIN,  // For placeholder only, Do not connected on RFM92/RFM95\r\n"
			+ "  .rst = IOTW_GPIO_LMIC_RST,      \r\n"
			+ "  .dio = {IOTW_GPIO_LMIC_DIO0, IOTW_GPIO_LMIC_DIO1, LMIC_UNUSED_PIN}\r\n"
			+ "};\r\n";
			translator.addDefinitionCommand(PinMapping);
			translator.addDefinitionCommand("int IOTW_debug_level = IOTW_DEBUG_LEVEL; // Debug print auch in den IOTW_ Libs nutzen\n");
						
			
			
			String deveui,appeui,appkey,ret,wert,SFtxt,ADR_CMD;
			
			TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		    deveui = translatorBlock.toCode();

			translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		    appeui = translatorBlock.toCode();

			translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
		    appkey = translatorBlock.toCode();
		    
		    translatorBlock = this.getTranslatorBlockAtSocket(3);
		    if (translatorBlock==null) { 
              SFtxt = "0";
		    } else SFtxt = translatorBlock.toCode();
			
		    int SF = Integer.parseInt(SFtxt);
		    
		    if (SF > 0 ) {
		       ADR_CMD = "  LMIC_setAdrMode(0); LMIC_setDrTxpow(DR_SF"+(SF+6)+", 14);\n"; 	
		    } else {
			   ADR_CMD = "  LMIC_setAdrMode(1); \n"
					   + "  #if defined(CFG_LMIC_EU_like)\r\n"
					   + "  for (uint8_t ch = 0; ch < MAX_CHANNELS; ch++) {\r\n"
					   + "   if (LMIC.channelFreq[ch]) {\r\n"
					   + "       LMIC_setupChannel(ch,\r\n"
					   + "       LMIC.channelFreq[ch],\r\n"
					   + "       DR_RANGE_MAP(DR_SF7, DR_SF10),   // erlaubt: DR5..DR2 = SF7..SF10\r\n"
					   + "       BAND_CENTI);\r\n"
					   + "    }\r\n"
					   + "  }\r\n"
					   + "  #endif \n";
		    } 
		    
		    String Hex, ascii;

		    ascii = deveui;
		    if (deveui.length() < 16+2) throw new SocketNullException(this.blockId);

		    
		    Hex = "static const u1_t PROGMEM DEVEUI[8]={"
		        +  "0x" + ascii.charAt(15)+ascii.charAt(16)+","
		    	+  "0x" + ascii.charAt(13)+ascii.charAt(14)+","
		   		+  "0x" + ascii.charAt(11)+ascii.charAt(12)+","
		    	+  "0x" + ascii.charAt(9)+ascii.charAt(10)+","
		    	+  "0x" + ascii.charAt(7)+ascii.charAt(8)+","
		    	+  "0x" + ascii.charAt(5)+ascii.charAt(6)+","
		    	+  "0x" + ascii.charAt(3)+ascii.charAt(4)+","
	   	    	+  "0x" + ascii.charAt(1)+ascii.charAt(2) + "};\n"
	   	    	+  " void os_getDevEui (u1_t* buf) { memcpy_P(buf, DEVEUI, 8);}\n";
		    translator.addDefinitionCommand(Hex);


		    ascii = appeui;
		    if (appeui.length() < 16+2) throw new SocketNullException(this.blockId);

		    Hex = "static const u1_t PROGMEM APPEUI[8]={"
		        +  "0x" + ascii.charAt(15)+ascii.charAt(16)+","
		    	+  "0x" + ascii.charAt(13)+ascii.charAt(14)+","
		   		+  "0x" + ascii.charAt(11)+ascii.charAt(12)+","
		    	+  "0x" + ascii.charAt(9)+ascii.charAt(10)+","
		    	+  "0x" + ascii.charAt(7)+ascii.charAt(8)+","
		    	+  "0x" + ascii.charAt(5)+ascii.charAt(6)+","
		    	+  "0x" + ascii.charAt(3)+ascii.charAt(4)+","
	   	    	+  "0x" + ascii.charAt(1)+ascii.charAt(2) + "};\n"
	  		    +  " void os_getArtEui (u1_t* buf) { memcpy_P(buf, APPEUI, 8);}\n";									
		    translator.addDefinitionCommand(Hex);
		    
		    ascii = appkey;
		    if (appkey.length() < 32+2) throw new SocketNullException(this.blockId);

		    Hex = "static const u1_t PROGMEM APPKEY[16]={"
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
	   	    	+  "0x" + ascii.charAt(31)+ascii.charAt(32) + "};\n"
	   	    	+  " void os_getDevKey (u1_t* buf) {  memcpy_P(buf, APPKEY, 16);};\n";
		    									
		    translator.addDefinitionCommand(Hex);
		    
		
		String init = "// -- initialize LoraWAN LMIC structure\n"
				+ "void LoRaWAN_Start_OTA(int fromRTCMem) { // using OTA-Communication \n" 	
				+ "  os_init();             // LMIC LoraWAN\n"
				+ "  LMIC_reset();          // Reset the MAC state \n"
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
			
            String setup = "  LoRaWAN_Start_OTA(true); // Prepare LMIC-Engine\n";
			translator.addSetupCommand(setup);
			
	 String Debug = "// -----------------------  LMIC Debug info \n"
			 + "#if (IOTW_DEBUG_LEVEL >1)\r\n"
			 + "void LMIC_debug_status() {\r\n"
			 + "  const char* dr2str[] = {\r\n"
			 + "    \"SF12\",\"SF11\",\"SF10\",\"SF9\",\"SF8\",\"SF7\",\"SF7B\",\"FSK\"\r\n"
			 + "  };\r\n"
			 + "\r\n"
			 + "  ostime_t now = os_getTime();\r\n"
			 + "\r\n"
			 + "  // --- Downlink-Info ---\r\n"
			 + "  if (LMIC.txrxFlags & TXRX_DNW1) Serial.println(F(\"Downlink in RX1\"));\r\n"
			 + "  if (LMIC.txrxFlags & TXRX_DNW2) Serial.println(F(\"Downlink in RX2\"));\r\n"
			 + "\r\n"
			 + "  Serial.print(F(\"RX2 DR = \"));\r\n"
			 + "  Serial.print(LMIC.dn2Dr);\r\n"
			 + "  Serial.print(F(\" (\"));\r\n"
			 + "  Serial.print(dr2str[LMIC.dn2Dr]);\r\n"
			 + "  Serial.println(F(\")\"));\r\n"
			 + "\r\n"
			 + "  // --- ADR ---\r\n"
			 + "  if (LMIC.adrEnabled) Serial.println(F(\"ADR ist AKTIV ✅\"));\r\n"
			 + "  else                 Serial.println(F(\"ADR ist AUS ❌\"));\r\n"
			 + "\r\n"
			 + "  // --- aktuelle TX Parameter ---\r\n"
			 + "  Serial.print(F(\"Aktuelle Tx DR: \"));\r\n"
			 + "  Serial.print(dr2str[LMIC.datarate]);\r\n"
			 + "  Serial.print(F(\" / TX-Power: \"));\r\n"
			 + "  Serial.print(LMIC.txpow);\r\n"
			 + "  Serial.println(F(\" dBm\"));\r\n"
			 + "\r\n"
			 + "  // --- Opmode Flags ---\r\n"
			 + "  Serial.printf(\"Opmode = 0x%X  -> \", LMIC.opmode);\r\n"
			 + "  struct {\r\n"
			 + "    uint32_t mask;\r\n"
			 + "    const char* name;\r\n"
			 + "  } flags[] = {\r\n"
			 + "    { OP_SCAN,      \"SCAN\" },\r\n"
			 + "    { OP_TRACK,     \"TRACK\" },\r\n"
			 + "    { OP_JOINING,   \"JOINING\" },\r\n"
			 + "    { OP_TXDATA,    \"TXDATA\" },\r\n"
			 + "    { OP_TXRXPEND,  \"TXRXPEND\" },\r\n"
			 + "    { OP_POLL,      \"POLL\" },\r\n"
			 + "    { OP_REJOIN,    \"REJOIN\" },\r\n"
			 + "    { OP_LINKDEAD,  \"LINKDEAD\" },\r\n"
			 + "    { OP_NEXTCHNL,  \"NEXTCHNL\" },\r\n"
			 + "    { OP_SHUTDOWN,  \"SHUTDOWN\" }\r\n"
			 + "  };\r\n"
			 + "  bool first = true;\r\n"
			 + "  for (unsigned i=0; i<sizeof(flags)/sizeof(flags[0]); i++) {\r\n"
			 + "    if (LMIC.opmode & flags[i].mask) {\r\n"
			 + "      if (!first) Serial.print(F(\" | \"));\r\n"
			 + "      Serial.print(flags[i].name);\r\n"
			 + "      first = false;\r\n"
			 + "    }\r\n"
			 + "  }\r\n"
			 + "  if (first) Serial.print(F(\"NONE\"));\r\n"
			 + "  Serial.println();\r\n"
			 + "\r\n"
			 + "  // --- Duty-Cycle / nächste TX ---\r\n"
			 + "  long ms_until_tx = osticks2ms(LMIC.txend - now);\r\n"
			 + "  Serial.print(F(\"Nächster geplanter TX in \"));\r\n"
			 + "  Serial.print(ms_until_tx);\r\n"
			 + "  Serial.println(F(\" ms\"));\r\n"
			 + "\r\n"
			 + "  Serial.print(F(\"Global DutyAvail in \"));\r\n"
			 + "  Serial.print(osticks2ms(LMIC.globalDutyAvail - now));\r\n"
			 + "  Serial.println(F(\" ms\"));\r\n"
			 + "\r\n"
			 + "} " 
			 + "#endif\n"
			 + "static inline long app_ms_until_next_tx() {\r\n"
			 + "  // frühestmögliche Sendezeit = max(LMIC.txend, LMIC.globalDutyAvail)\r\n"
			 + "  os_runloop_once();  \r\n"
			 + "  ostime_t now = os_getTime();\r\n"
			 + "  long a = osticks2ms(LMIC.txend          - now);\r\n"
			 + "  long b = osticks2ms(LMIC.globalDutyAvail - now);\r\n"
			 + "  if (a < 0) a = 0; \r\n"
			 + "  if (b < 0) b = 0;\r\n"
			 + "  return (a > b) ? a : b;\r\n"
			 + "}";
		translator.addDefinitionCommand(Debug);
			
			
		
    	
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
	    
	    
	    ret = ret = "\n{ //Block------------------------------ send data to network\n"
	    		+ "int port = " + port + ";\n"
	    		+ "static uint8_t mydata["+ count + "];\n"
	       		+ wert 
		        + "   LoRaWAN_Event_No_Join = 0;\r\n"
		        + "   LoRaWAN_Event_TxComplete = 0;\r\n"
		        + "   uint32_t next_tx = app_ms_until_next_tx() ; \n"
		        + "   // Check if there is not a current TX/RX job running, and time to wait until finished\r\n"
		        + "   bool tx_ok = false;\r\n"
		        + "   if (!((LMIC.opmode & OP_TXRXPEND) || \r\n"
		        + "      (LMIC.opmode & OP_TXDATA)   ||\r\n"
		        + "      (LMIC.opmode & OP_JOINING)) && \r\n"
		        + "      (next_tx <= IOTW_LMIC_JOIN_TOUT)) {\r\n"
		        + "        if (LMIC_setTxData2(port, mydata, sizeof(mydata), 0)) {\r\n"
		        + "          IOTW_PRINTLN(F(\"❌ TxData2 Error busy\"));\r\n"
		        + "          tx_ok = false;\r\n"
		        + "        } else\r\n"
		        + "          tx_ok = true;\r\n"
		        + "      }           \r\n"
		        + "      if (tx_ok == false) {\r\n"
		        + "        IOTW_PRINTLN(F(\"❌ DutyCycle or busy -> skip TX -> data not send, deepsleep\"));\r\n"
		        + "        SaveLMICToRTC_ESP32(IOTW_LMIC_JOIN_TOUT/1000);\r\n"
		        + "        esp_sleep_enable_timer_wakeup(IOTW_LMIC_JOIN_TOUT * 1000ULL);\r\n"
		        + "        esp_deep_sleep_start();\r\n"
		        + "        #if (IOTW_DEBUG_LEVEL >1)\r\n"
		        + "          IOTW_PRINT(F(\"ms to next tx-slot: \"));\r\n"
		        + "          IOTW_PRINTLN(app_ms_until_next_tx());\r\n"
		        + "        #endif            \r\n"
		        + "   }" 
		        + "#ifdef IOTW_LORA_JOIN_LED_PIN\r\n"
		        + "   if (LMIC.opmode & OP_JOINING) {\r\n"
		        + "    rtc_gpio_deinit((gpio_num_t)IOTW_LORA_JOIN_LED_PIN);\r\n"
		        + "    pinMode(IOTW_LORA_JOIN_LED_PIN,OUTPUT);\r\n"
		        + "    digitalWrite(IOTW_LORA_JOIN_LED_PIN,HIGH);\r\n"
		        + "   }\r\n"
		        + "#endif\n"
		        + "  uint32_t tout = millis()+IOTW_LMIC_JOIN_TOUT; // harter Timeout Joining SF10\r\n"
		        
		        +"   while ((millis() < tout \r\n"
		        + "        && ((LMIC.opmode & OP_TXRXPEND) \r\n"
		        + "         || (LMIC.opmode & OP_TXDATA) \r\n"
		        + "         || (LMIC.opmode & OP_POLL) \r\n"
		        + "         || (LMIC.opmode & OP_JOINING))) \r\n"
		        + "       && !LoRaWAN_Event_No_Join) {\r\n"
		        + "       yield();\r\n"
		        + "       os_runloop_once_sleep();\r\n"
		        + "  //     Serial.println(LMIC.opmode,HEX);\r\n"
		        + "  }"
		        
		        + "  if (millis() < tout) IOTW_PRINTLN(F(\"Tx finished ✅\")); "
		        + "    else IOTW_PRINTLN(F(\"❌ Timeout\")); "
		        + "\n"
		        + "  if (LMIC.devaddr == 0) { // Count for Timeout Join\r\n"
		        + "      LMIC_JoinFailCount++;\r\n"
		        + "      IOTW_PRINT(F(\"Join FailCount = \"));\r\n"
		        + "      IOTW_PRINTLN(LMIC_JoinFailCount);\r\n"
				        + "      if (LMIC_JoinFailCount >= IOTW_LMIC_JOIN_MAXFAIL) {\r\n"
		        + "        #ifdef ESP32 \r\n"
		        + "           //------- deep SLEEP ----------------------------\r\n"
		        +             "#if defined(IOTW_LORA_DEEPSLEEP)\r\n"
		        + "              IOTW_PRINTLN(F(\"No Gateway, go 1 h sleep \"));\r\n"
		        + "              SaveLMICToRTC_ESP32(3600);\r\n"
		        + "              Serial.flush();\r\n"
		        + "              Serial.end();\r\n"
		        + "              rtc_gpio_init(GPIO_NUM_2);// GPIO2 is a Boot-Pin and connected to blue LED and pullup \r\n"
		        + "              rtc_gpio_set_direction(GPIO_NUM_2, RTC_GPIO_MODE_INPUT_ONLY);\r\n"
		        + "              rtc_gpio_pullup_dis(GPIO_NUM_2);\r\n"
		        + "              rtc_gpio_pulldown_dis(GPIO_NUM_2);\r\n"
		        + "              esp_sleep_enable_timer_wakeup(60*60000 * 1000ULL);\r\n"
		        + "              esp_deep_sleep_start();\r\n"
		        + "            #endif \n"
		        + "         #endif \n"
		        + "      }\r\n"
		        + "    } else {"
		        + "      LMIC_JoinFailCount=0;\r\n"
		        + "      // erste TX nach Join mit SF10, kann durch ADR runtergestuft werden \n"
		        + "      if (!LMIC_postJoinConfigured) {    //  Session vorhanden\r\n"
		        +           ADR_CMD
		        + "         LMIC_postJoinConfigured = true;\r\n"
		        + "      } \n"
		        + "  }\n"
		        + "  #if (IOTW_DEBUG_LEVEL >1)\r\n"
		        + "    LMIC_debug_status();\r\n"
		        + "  #endif\n"
		        + "} // Block \n";
	    		
	    
	    translator.setLORAProgram(true);
 	    
	               
	    return codePrefix + ret + codeSuffix;
	 	}

}
/*
 *    ret = "\n{ //Block------------------------------ send data to network\n"
	    		+ "int port = " + port + ";\n"
	    		+ "static uint8_t mydata["+ count + "];\n"
	       		+ wert 
		        + "  LoRaWAN_Event_No_Join = 0;\r\n"
		        + "  LoRaWAN_Event_TxComplete = 0;\r\n"
		        + "  // Check if there is not a current TX/RX job running, wait until finished\r\n"
		        + "  if (!((LMIC.opmode & OP_TXRXPEND) || (LMIC.opmode & OP_TXDATA) || (LMIC.opmode & OP_JOINING))) {\r\n"
		        + "    if (LMIC_setTxData2(port, mydata, sizeof(mydata), 0)) {\r\n"
		        + "        IOTW_PRINTLN(F(\"------------------------  setTxData: error\"));\r\n"
		        + "    }\r\n"
		        + "  }\r\n"
		        + "#ifdef IOTW_LORA_JOIN_LED_PIN\r\n"
		        + "   if (LMIC.opmode & OP_JOINING) {\r\n"
		        + "    rtc_gpio_deinit((gpio_num_t)IOTW_LORA_JOIN_LED_PIN);\r\n"
		        + "    pinMode(IOTW_LORA_JOIN_LED_PIN,OUTPUT);\r\n"
		        + "    digitalWrite(IOTW_LORA_JOIN_LED_PIN,HIGH);\r\n"
		        + "   }\r\n"
		        + "#endif\n"
		        + "  uint32_t tout = millis()+30000; // harter Timeout\r\n"
		        + "    while ((millis() < tout && ((LMIC.opmode & OP_TXRXPEND) || (LMIC.opmode & OP_TXDATA) || (LMIC.opmode & OP_POLL) || (LMIC.opmode & OP_JOINING))) && !LoRaWAN_Event_No_Join) {\r\n"
		        + "      yield();\r\n"
		        + "      os_runloop_once_sleep();\r\n"
		        + "  }\r\n"
		        + "  if (millis() < tout) IOTW_PRINTLN(F(\"Tx finished ✅\")); "
		        + "    else IOTW_PRINTLN(F(\"❌ Timeout\")); "
		        + "\n"
		        + "  // erste TX nach Join mit SF9, kann durch ADR runtergestuft werden \n"
		        + "  if (!LMIC_postJoinConfigured && (LMIC.devaddr != 0)) {    //  Session vorhanden\r\n"
		        + "      LMIC_setDrTxpow(DR_SF9, 14);                          //  Uplink künftig SF9 starten\r\n"
		        + "      LMIC_setAdrMode(1);                                   //  Netz darf auf SF8/SF7 runterstufen\r\n"
		        + "      LMIC_postJoinConfigured = true;\r\n"
		        + "  }"
		        + "  #if (IOTW_DEBUG_LEVEL >1)\r\n"
		        + "    LMIC_print_downlink_info();\r\n"
		        + "  #endif\n"
		        + "} // Block \n";
 */


/*
 * ret = "\n{ //Block------------------------------ send data to network\n"
	    	  + "  int port = " + port + ";\n"
	    	  + "  static uint8_t mydata["+ count + "];\n"
	       	  + wert 
		      + "  LoRaWAN_Event_No_Join = 0;\r\n"
		      + "  LoRaWAN_Event_TxComplete = 0;\r\n"
		      + "   // Check if there is not a current TX/RX job running, wait until finished\r\n"
		      + "    if (!((LMIC.opmode & OP_TXRXPEND) || (LMIC.opmode & OP_TXDATA) || (LMIC.opmode & OP_JOINING))) {\r\n"
		      + "      if (LMIC_setTxData2(port, mydata, sizeof(mydata), 0)) {\r\n"
		      + "        IOTW_PRINTLN(F(\"------------------------  setTxData: error\"));\r\n"
		      + "      }\r\n"
		      + "    }\r\n"
		      + "#ifdef IOTW_LORA_JOIN_LED_PIN\r\n"
		      + "    if (LMIC.opmode & OP_JOINING) {\r\n"
		      + "      rtc_gpio_deinit((gpio_num_t)IOTW_LORA_JOIN_LED_PIN);\r\n"
		      + "      pinMode(IOTW_LORA_JOIN_LED_PIN,OUTPUT);\r\n"
		      + "      digitalWrite(IOTW_LORA_JOIN_LED_PIN,HIGH);\r\n"
		      + "    }\r\n"
		      + "#endif\r\n"
		      + "    uint32_t tout = millis()+30000; // harter Timeout\r\n"
		      + "    while ((millis() < tout && ((LMIC.opmode & OP_TXRXPEND) || (LMIC.opmode & OP_TXDATA) || (LMIC.opmode & OP_POLL) || (LMIC.opmode & OP_JOINING))) && !LoRaWAN_Event_No_Join) {\r\n"
		      + "      yield();\r\n"
		      + "      os_runloop_once_sleep();\r\n"
		      + "    }\r\n"
		      + "if (millis() < tout) {\r\n"
		      + "  IOTW_PRINTLN(F(\"Tx finished ✅\"));\r\n"
		      + "\r\n"
		      + "  // Frischer Join erkannt? Dann einmalig Start-DR & ADR setzen, Zähler resetten\r\n"
		      + "  if ((LMIC.devaddr != 0) && !LMIC_postJoinConfigured) {\r\n"
		      + "    LMIC_setDrTxpow(DR_SF9, 14);  // Uplink ab jetzt mit SF9 starten\r\n"
		      + "    LMIC_setAdrMode(1);           // Netz darf weiter runterstufen\r\n"
		      + "    LMIC_postJoinConfigured = true;\r\n"
		      + "    LMIC_JoinFailCount = 0;\r\n"
		      + "  }\r\n"
		      + "} else {\r\n"
		      + "  IOTW_PRINTLN(F(\"❌ Timeout / Join fail\"));\r\n"
		      + "\r\n"
		      + "#ifdef ESP32\r\n"
		      + "  // Nur wenn wirklich keine Session existiert, den Fail zählen\r\n"
		      + "  if (LMIC.devaddr == 0) {\r\n"
		      + "    if (LMIC_JoinFailCount < 10) {\r\n"
		      + "      LMIC_JoinFailCount++;\r\n"
		      + "      IOTW_PRINT(F(\"Join fail count = \"));\r\n"
		      + "      IOTW_PRINTLN(LMIC_JoinFailCount);\r\n"
		      + "      // Nächster Versuch im normalen Messzyklus\r\n"
		      + "    } else {\r\n"
		      + "      // 10 Fehlschläge -> 1h schlafen.\r\n"
		      + "      uint32_t sleep_ms = 3600000UL;\r\n"
		      + "      IOTW_PRINTLN(F(\"Join backoff: sleep 1h\"));\r\n"
		      + "\r\n"
		      + "#ifdef IOTW_LORA_DEEPSLEEP\r\n"
		      + "      SaveLMICToRTC_ESP32(sleep_ms / 1000);\r\n"
		      + "#endif\r\n"
		      + "      Serial.flush(); Serial.end();\r\n"
		      + "      rtc_gpio_init(GPIO_NUM_2);\r\n"
		      + "      rtc_gpio_set_direction(GPIO_NUM_2, RTC_GPIO_MODE_INPUT_ONLY);\r\n"
		      + "      rtc_gpio_pullup_dis(GPIO_NUM_2);\r\n"
		      + "      rtc_gpio_pulldown_dis(GPIO_NUM_2);\r\n"
		      + "      esp_sleep_enable_timer_wakeup((uint64_t)sleep_ms * 1000ULL);\r\n"
		      + "      esp_deep_sleep_start();\r\n"
		      + "    }\r\n"
		      + "  }\r\n"
		      + "#endif\r\n"
		      + "}\r\n"
		      + "\r\n"
		      + "#if (IOTW_DEBUG_LEVEL >1)\r\n"
		      + "    LMIC_print_downlink_info();\r\n"
		      + "#endif\r\n"
		      + "  } // Block \n";
 */
