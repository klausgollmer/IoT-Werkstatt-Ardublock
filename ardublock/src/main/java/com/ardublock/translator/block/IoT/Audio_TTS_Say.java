package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Audio_TTS_Say  extends TranslatorBlock {

	public Audio_TTS_Say (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("UrlEncode.h");
		translator.addHeaderFile("AudioFileSourceBuffer.h");
		translator.addHeaderFile("AudioGeneratorMP3.h");
		translator.addHeaderFile("AudioOutputI2S.h");
		translator.addHeaderFile("AudioFileSourceHTTPStream.h");
		translator.addHeaderFile("SPIFFS.h");
		translator.addHeaderFile("AudioFileSourceSPIFFS.h");
		
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String text = translatorBlock.toCode();
 	   	translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String lang = translatorBlock.toCode();
	 	translatorBlock = this.getTranslatorBlockAtSocket(2);
		String vol = translatorBlock.toCode();
		if (vol == null) vol = "50.";
	    	   	
		String Dis="/* ESP8266Audio "
				 + "   GPL-3.0 Licence https://github.com/earlephilhower/ESP8266Audio/?tab=GPL-3.0-1-ov-file#readme \n"
				 + "   (c) Earle F. Philhower, III */\n"
				 + "String ttsFileName = \"/tts.mp3\";\r\n"
				 + "AudioGeneratorMP3 *tts_mp3 = nullptr;\r\n"
				 + "AudioFileSourceSPIFFS *tts_file = nullptr;\r\n"
				 + "AudioOutputI2S *out = nullptr;\r\n"
				 + "boolean ttsActive = false;"
				 + "String  ttsLast_text =\" \";";
	   	translator.addDefinitionCommand(Dis);
	    	   	
		
	 	String Def="// Sprachausgabe Text to Speech\r\n"
	 			+ "// Google TTS: load MP3 to file and play \r\n"
	            + "\n" 			
	 			+ "void playTTSFromSPIFFS() {\r\n"
	 			+ "  tts_file = new AudioFileSourceSPIFFS();\r\n"
	 			+ "  if (!tts_file->open(ttsFileName.c_str())) {\r\n"
	 			+ "    IOTW_PRINTLN(F(\"❌ File error\"));\r\n"
	 			+ "    delete tts_file;\r\n"
	 			+ "    return;\r\n"
	 			+ "  }\r\n"
	 			+ "\r\n"
	 			+ "  tts_mp3 = new AudioGeneratorMP3();\r\n"
	 			+ "  if (tts_mp3->begin(tts_file, out)) {\r\n"
	 			+ "    ttsActive = true;\r\n"
	 			+ "  }\r\n"
	 			+ "\r\n"
	 			+ "  while (ttsActive && tts_mp3 && tts_mp3->isRunning()) {\r\n"
	 			+ "    if (!tts_mp3->loop()) {\r\n"
	 			+ "      IOTW_PRINTLN(\" ✓\");\r\n"
	 			+ "      tts_mp3->stop();\r\n"
	 			+ "      delete tts_mp3;\r\n"
	 			+ "      delete tts_file;\r\n"
	 			+ "      tts_mp3 = nullptr;\r\n"
	 			+ "      tts_file = nullptr;\r\n"
	 			+ "      ttsActive = false;\r\n"
	 			+ "     // SPIFFS.remove(ttsFileName);\r\n"
	 			+ "    }\r\n"
	 			+ "    out->SetGain(0.039*"+vol+");\n"
	 			+ "  } "
	 			+ "} "	
	 			+ "\n"
	 			+ "void downloadAndPlayTTS(String text, String lang) {\r\n"
	 			+ "  String encoded = urlEncode(text);\r\n"
	 			+ "  if (ttsLast_text != text) {\r\n"
	 			+ "    IOTW_PRINT(F(\"▶ Google 📥 say: \")); \r\n"
	 			+ "    IOTW_PRINT(text);\r\n"
	 			+ "  \r\n"
	 			+ "    String url = \"http://translate.google.com/translate_tts?\"\r\n"
	 			+ "      \"ie=UTF-8&\"\r\n"
	 			+ "      \"q=\" + encoded +\r\n"
	 			+ "      \"&tl=\"+lang+\"&\"\r\n"
	 			+ "      \"client=tw-ob\";\r\n"
	 			+ "    HTTPClient http;\r\n"
	 			+ "    http.begin(url);\r\n"
	 			+ "    int code = http.GET();\r\n"
	 			+ "    if (code == 200) {\r\n"
	 			+ "      File f = SPIFFS.open(ttsFileName, \"w\");\r\n"
	 			+ "      if (f) {\r\n"
	 			+ "        http.writeToStream(&f);\r\n"
	 			+ "        f.close();\r\n"
	 			+ "        // Serial.println(F(\"📥 save MP3\"));\r\n"
	 			+ "        ttsLast_text = text;\r\n"
	 			+ "        playTTSFromSPIFFS();\r\n"
	 			+ "      } \r\n"
	 			+ "      else {\r\n"
	 			+ "        IOTW_PRINTLN(F(\" ❌ SPIFFS open failed\"));\r\n"
	 			+ "      }\r\n"
	 			+ "    } \r\n"
	 			+ "    else {\r\n"
	 			+ "      IOTW_PRINTLN(\" ❌ HTTP: \" + String(code));\r\n"
	 			+ "    }\r\n"
	 			+ "    http.end();\r\n"
	 			+ "   } else {\r\n"
	 			+ "     IOTW_PRINT(F(\"📥 say: \")); \r\n"
	 			+ "     IOTW_PRINT(text);\r\n"
	 			+ "     playTTSFromSPIFFS();\r\n"
	 			+ "   }\r\n"
	 			+ "}\r\n"
	 			+ "\r\n";
	 	translator.addDefinitionCommand(Def);

	   	String Setup ="Serial.setDebugOutput(false);        // ESP Internas nicht auf Serial\r\n"
	   			+ "  if (!SPIFFS.begin(false)) {   // false = kein Format!\r\n"
	   			+ "    IOTW_PRINTLN(F(\"❌ SPIFFS failed\"));\r\n"
	   			+ "    while(1);\r\n"
	   			+ "  }\r\n"
	   			+ "  IOTW_PRINTLN(F(\"✅ SPIFFS for MP3 OK\"));\r\n"
	   			+ "\r\n"
	   			+ "  out = new AudioOutputI2S(0, AudioOutputI2S::INTERNAL_DAC);\r\n"
	   			+ "  out->begin();\r\n"
	   			+ "  out->SetGain(3.9);\n";
	   	
	    translator.addSetupCommand(Setup);
		
		String ret = "downloadAndPlayTTS("+text+","+lang+");\n";
		return codePrefix + ret + codeSuffix;
		
	}
}

