package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class LORA_TTTNSend_ota  extends TranslatorBlock {

	public LORA_TTTNSend_ota (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
		translator.setLORAProgram(true);   
	
		String Dis="/* LoRaWAN LMIC Lib\n"
			     + "Copyright (C) 2014-2016 IBM Corporation\n"
				 + "Copyright (c) 2015 Thomas Telkamp and Matthijs Kooijman\r\n"
				 + "Copyright (c) 2016-2021 MCCI Corporation\n"
				 + "MIT, Disclaimer see https://github.com/mcci-catena/arduino-lmic?tab=MIT-1-ov-file#readme \n"
				 + "*/\n";
	translator.addDefinitionCommand(Dis);

		
	String PinMapping = "// -------- LoRa PinMapping \r\n"
			+ "#if defined(BOARD_TTGO_V1)\r\n"
			+ "const lmic_pinmap lmic_pins = {\r\n"
			+ "  .nss = LORA_CS, \r\n"
			+ "  .rxtx = LMIC_UNUSED_PIN,\r\n"
			+ "  .rst = LORA_RST,\r\n"
			+ "  .dio = {\r\n"
			+ "    LORA_IRQ, 33,  LMIC_UNUSED_PIN }\r\n"
			+ "};\r\n"
			+ "#else\r\n"
			+ "const lmic_pinmap lmic_pins = {  \r\n"
			+ "  .nss = LMIC_NSS,                            // Connected to pin D\r\n"
			+ "  .rxtx = LMIC_UNUSED_PIN,             // For placeholder only, Do not connected on RFM92/RFM95\r\n"
			+ "  .rst = LMIC_UNUSED_PIN,              // Needed on RFM92/RFM95? (probably not) D0/GPIO16 \r\n"
			+ "  .dio = {\r\n"
			+ "    LMIC_DIO, LMIC_DIO, LMIC_UNUSED_PIN             }\r\n"
			+ "};\r\n"
			+ "#endif\r\n"
			+ "#if defined(XIAO_ESP32S3)\r\n"
			+ "const Arduino_LMIC::HalConfiguration_t myConfig;\r\n"
			+ "const lmic_pinmap *pPinMap = Arduino_LMIC::GetPinmap_XIAO_S3_WIO_SX1262();\r\n"
			+ "#endif\r\n";
			translator.addDefinitionCommand(PinMapping);
			
			
			String deveui,appeui,appkey,ret,wert;
			TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		    deveui = translatorBlock.toCode();

			translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		    appeui = translatorBlock.toCode();

			translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
		    appkey = translatorBlock.toCode();
		    
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
				+ " // #if defined(BOARD_TTGO_V1)\r\n"
				+ " //    SPI.begin(SCK,MISO,MOSI,SS);\r\n"
				+ " // #endif\n"
				+ "#if defined(XIAO_ESP32S3)\n"
				+ "  os_init_ex(pPinMap);\n"
				+ "#else\n"
				+ "  os_init();             // LMIC LoraWAN\n"
				+ "#endif\n "
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
	       		+ wert 
		        + "  LoRaWAN_Event_No_Join = 0;\r\n"
		        + "  LoRaWAN_Event_TxComplete = 0;\r\n"
		        + "  // Check if there is not a current TX/RX job running, wait until finished\r\n"
		        + "  if (!((LMIC.opmode & OP_TXRXPEND) || (LMIC.opmode & OP_TXDATA) || (LMIC.opmode & OP_JOINING))) {\r\n"
		        + "    if (LMIC_setTxData2(port, mydata, sizeof(mydata), 0)) {\r\n"
		        + "        Serial.println(F(\"------------------------  setTxData: error\"));\r\n"
		        + "    }\r\n"
		        + "  }\r\n"
		        + "  uint32_t tout = millis()+30000; // harter Timeout\r\n"
		        + "  while ( millis() < tout && !LoRaWAN_Event_No_Join && !LoRaWAN_Event_TxComplete) {\r\n"
		        + "     yield();\r\n"
		        + "     os_runloop_once_sleep();\r\n"
		        + "  }\r\n"
		        + "  Serial.println(F(\"Tx finished\")); "
		        + "} // Block \n";
	    
	    
	    
	    	    
	    
		translator.setLORAProgram(true);
 	    
	               
	    return codePrefix + ret + codeSuffix;
	 	}
}

/*
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
*/
