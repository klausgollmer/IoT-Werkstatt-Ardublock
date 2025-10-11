package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Audio_WebRadio  extends TranslatorBlock {

	public Audio_WebRadio (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String mp3 = translatorBlock.toCode();
		
		String gain;
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		gain = translatorBlock.toCode();

		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		
		translator.addHeaderFile("AudioFileSourceICYStream.h");
		translator.addHeaderFile("AudioFileSourceBuffer.h");
		translator.addHeaderFile("AudioGeneratorMP3.h");
		translator.addHeaderFile("AudioOutputI2S.h");
		translator.addHeaderFile("esp_wifi.h");
		
		String Dis="/* ESP8266Audio "
				 + "   GPL-3.0 Licence https://github.com/earlephilhower/ESP8266Audio/?tab=GPL-3.0-1-ov-file#readme \n"
				 + "   (c) Earle F. Philhower, III */\n";
	   	translator.addDefinitionCommand(Dis);
	    	   	
	   	String Def="AudioOutputI2S *DAC_out = NULL;\r\n";
		translator.addDefinitionCommand(Def);
	
	 	Def="AudioGeneratorMP3 *mp3;\r\n"
	 		   	  + "AudioFileSourceICYStream *file;\r\n"
	 		   	  + "AudioFileSourceBuffer *buff;\n"
	 		   	  + "boolean WebRadioInit = false;\n"
	 		   	  + "String WebRadioStreamTitle=\"\";\n";
	 			translator.addDefinitionCommand(Def);
		
		String Helper = "// Called when a metadata event occurs (i.e. an ID3 tag, an ICY block, etc.\r\n"
				+ "void MDCallback(void *cbData, const char *type, bool isUnicode, const char *string)\r\n"
				+ "{\r\n"
				+ "  const char *ptr = reinterpret_cast<const char *>(cbData);\r\n"
				+ "  (void) isUnicode; // Punt this ball for now\r\n"
				+ "  // Note that the type and string may be in PROGMEM, so copy them to RAM for printf\r\n"
				+ "  char s1[32], s2[64];\r\n"
				+ "  strncpy_P(s1, type, sizeof(s1));\r\n"
				+ "  s1[sizeof(s1)-1]=0;\r\n"
				+ "  strncpy_P(s2, string, sizeof(s2));\r\n"
				+ "  s2[sizeof(s2)-1]=0;\r\n"
				+ "  //Serial.printf(\"METADATA(%s) '%s' = '%s'\\n\", ptr, s1, s2);\r\n"
				+ "  WebRadioStreamTitle=s2;\n"
				+ "  //Serial.flush();\r\n"
				+ "}\r\n"
				+ "\r\n"
				+ "// Called when there's a warning or error (like a buffer underflow or decode hiccup)\r\n"
				+ "void StatusCallback(void *cbData, int code, const char *msg){\r\n"
				+ "  static uint16_t lostCnt = 0;\r\n"
				+ "  if(code == 257){                       // MAD_ERROR_LOSTSYNC\r\n"
				+ "      if(++lostCnt > 100){               // ~100 aufeinanderfolgende Fehler\r\n"
				+ "          mp3->desync();                 // Frame-Suche zurücksetzen\r\n"
				+ "          lostCnt = 0;\r\n"
				+ "      }\r\n"
				+ "  } else {\r\n"
				+ "      lostCnt = 0;                       // alles wieder gut\r\n"
				+ "  }\r\n"
				+ "}\n"
				+ "";
		translator.addDefinitionCommand(Helper);
		
		
		
	   	String Setup ="DAC_out = new AudioOutputI2S(0,AudioOutputI2S::INTERNAL_DAC);\r\n"
	   		    	+ "DAC_out->begin();\r\n"
	   		    	+ "float volume = 0.25;             // 25 % der Originalamplitude\r\n"
	   		    	+ "DAC_out->SetGain(volume);       // 0.0 … 4.0   (1.0 = 100 %)\n";
	    translator.addSetupCommand(Setup);
		
	    
		String ret = " static int lastms = 0;\r\n"
				+ "\r\n"
				
				+ "   if (!WebRadioInit) {\r\n"
				+ "      if (WiFi.status() != WL_CONNECTED) {\r\n"
				+ "      Serial.println(\"❌ Kein WLAN! Webradio wird nicht gestartet.\");\r\n"
				+ "      // Optional: WLAN-Reconnect versuchen\r\n"
				+ "      WiFi.reconnect();\r\n"
				+ "      delay(2000);\r\n"
				+ "    // Wenn immer noch kein WLAN → Schleife beenden\r\n"
				+ "      if (WiFi.status() != WL_CONNECTED) {\r\n"
				+ "       Serial.println(\"⏸️ Warten auf WLAN-Verbindung...\");\r\n"
				+ "       return; // ⛔ Hier wird abgebrochen, keine Nullpointer mehr!\r\n"
				+ "      }\r\n"
				+ "    }"
				+ "    WebRadioInit = true;\r\n"
				+ "    esp_wifi_set_ps(WIFI_PS_NONE);\r\n"
				+ "    WiFi.setSleep(false);          //no sleep, reduce dropout\n"
				+ "    char *URL="+mp3+";\n"
				+ "    audioLogger = &Serial;\r\n"
				+ "    file = new AudioFileSourceICYStream(URL);\r\n"
				+ "    file->RegisterMetadataCB(MDCallback, (void*)\"ICY\");\r\n"
				+ "    buff = new AudioFileSourceBuffer(file, 4*8192);\r\n"
				+ "    buff->RegisterStatusCB(StatusCallback, (void*)\"buffer\");\r\n"
				+ "    mp3 = new AudioGeneratorMP3();\r\n"
				+ "    mp3->RegisterStatusCB(StatusCallback, (void*)\"mp3\");\r\n"
				+ "    mp3->begin(buff, DAC_out);\r\n"
				+ "  }\r\n"
				
				+ "  if (mp3->isRunning()) {\r\n"
				+ "    if (DAC_out) DAC_out->SetGain("+gain+"/10.);\n"
				+ "    // if (millis()-lastms > 1000) {\r\n"
				+ "    //  lastms = millis();\r\n"
				+ "    //  Serial.printf(\"Running for %d ms...\\n\", lastms);\r\n"
				+ "    //  Serial.flush();\r\n"
				+ "    //  }\r\n"
				+ "    if (!mp3->loop()) mp3->stop();\r\n"
				+ "  } else {\r\n"
				+ "    Serial.printf(\"MP3 restart\\n\");\r\n"
				+ "    mp3->stop(); delete mp3;buff->close(); delete buff; delete file;\n"
				+ "    WebRadioInit=false;\n"
				+ "  }";
		
		return codePrefix + ret + codeSuffix;
		
	}
}

