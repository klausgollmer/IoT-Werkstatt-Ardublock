package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Audio_Mic  extends TranslatorBlock {

	public Audio_Mic (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String mp3 = translatorBlock.toCode();
		
		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		
		translator.addHeaderFile("AudioFileSourceICYStream.h");
		translator.addHeaderFile("AudioFileSourceBuffer.h");
		translator.addHeaderFile("AudioGeneratorMP3.h");
		translator.addHeaderFile("AudioOutputI2S.h");
		
		String Dis="/* ESP8266Audio "
				 + "   GPL-3.0 Licence https://github.com/earlephilhower/ESP8266Audio/?tab=GPL-3.0-1-ov-file#readme \n"
				 + "   (c) Earle F. Philhower, III */\n";
	   	translator.addDefinitionCommand(Dis);
	    	   	
	   	String Def="AudioOutputI2S *DAC_out = NULL;\r\n";
		translator.addDefinitionCommand(Def);
	
	   	Def="AudioGeneratorMP3 *mp3;\r\n"
	   	  + "AudioFileSourceICYStream *file;\r\n"
	   	  + "AudioFileSourceBuffer *buff;\n"
	   	  + "boolean WebRadioInit = false;\n";
		translator.addDefinitionCommand(Def);

		String Helper ="		MEMS i2S Mic: https://www.adafruit.com/product/3421\r\n"
				+ "		*/\r\n"
				+ "		#include \"driver/i2s.h\"\r\n"
				+ "\r\n"
				+ "		/*\r\n"
				+ "		Configuration structure for the i2s interface\r\n"
				+ "		    \r\n"
				+ "		    The Adafruit breakout for the SPH0645LM4H MEMS mic states it works on LEFT by default. this is not true, so use the RIGHT ONLY config\r\n"
				+ "		    channel_format: I2S_CHANNEL_FMT_ONLY_RIGHT,\r\n"
				+ "		    \r\n"
				+ "		    The SPH0645LM4H sends out data in MSB but only LSB works in this config\r\n"
				+ "		    communication_format: (i2s_comm_format_t)(I2S_COMM_FORMAT_I2S | I2S_COMM_FORMAT_I2S_LSB)\r\n"
				+ "		*/\r\n"
				+ "		/*\r\n"
				+ "		MEMS i2S Mic: https://www.adafruit.com/product/3421\r\n"
				+ "		*/\r\n"
				+ "		#include \"driver/i2s.h\"\r\n"
				+ "\r\n"
				+ "		/*\r\n"
				+ "		Configuration structure for the i2s interface\r\n"
				+ "		    \r\n"
				+ "		    The Adafruit breakout for the SPH0645LM4H MEMS mic states it works on LEFT by default. this is not true, so use the RIGHT ONLY config\r\n"
				+ "		    channel_format: I2S_CHANNEL_FMT_ONLY_RIGHT,\r\n"
				+ "		    \r\n"
				+ "		    The SPH0645LM4H sends out data in MSB but only LSB works in this config\r\n"
				+ "		    communication_format: (i2s_comm_format_t)(I2S_COMM_FORMAT_I2S | I2S_COMM_FORMAT_I2S_LSB)\r\n"
				+ "		*/\r\n"
				+ "		#define I2S_MIC_SERIAL_CLOCK GPIO_NUM_12\r\n"
				+ "		#define I2S_MIC_LEFT_RIGHT_CLOCK GPIO_NUM_2\r\n"
				+ "		#define I2S_MIC_SERIAL_DATA GPIO_NUM_27\r\n"
				+ "		#define SAMPLE_BUFFER_SIZE 512\r\n"
				+ "		#define SAMPLE_RATE 8000\r\n"
				+ "\r\n"
				+ "		i2s_config_t i2s_config = {\r\n"
				+ "		      mode: (i2s_mode_t)(I2S_MODE_MASTER | I2S_MODE_RX),\r\n"
				+ "		      sample_rate: SAMPLE_RATE,\r\n"
				+ "		      bits_per_sample: I2S_BITS_PER_SAMPLE_32BIT,\r\n"
				+ "		      channel_format: I2S_CHANNEL_FMT_ONLY_LEFT,\r\n"
				+ "		      communication_format: (i2s_comm_format_t)(I2S_COMM_FORMAT_I2S | I2S_COMM_FORMAT_I2S_LSB),\r\n"
				+ "		      intr_alloc_flags: ESP_INTR_FLAG_LEVEL1,\r\n"
				+ "		      dma_buf_count: 8,\r\n"
				+ "		      dma_buf_len: 8\r\n"
				+ "		};\r\n"
				+ "\r\n"
				+ "		//  Configure whatever pins you have available and make sure you set them up with PiNmode() Before setting up i2s system\r\n"
				+ "		i2s_pin_config_t pin_config = {\r\n"
				+ "		    .bck_io_num = I2S_MIC_SERIAL_CLOCK, //this is BCK pin\r\n"
				+ "		    .ws_io_num = I2S_MIC_LEFT_RIGHT_CLOCK, // this is LRCK pin\r\n"
				+ "		    .data_out_num = I2S_PIN_NO_CHANGE, // this is DATA output pin\r\n"
				+ "		    .data_in_num = I2S_MIC_SERIAL_DATA   //DATA IN\r\n"
				+ "		};\r\n"
				+ "\r\n"
				+ "		void setup()\r\n"
				+ "		{\r\n"
				+ "		  // we need serial output for the plotter\r\n"
				+ "		  Serial.begin(115200);\r\n"
				+ "		  // start up the I2S peripheral\r\n"
				+ "		  i2s_driver_install(I2S_NUM_0, &i2s_config, 0, NULL);\r\n"
				+ "		  i2s_set_pin(I2S_NUM_0, &pin_config);\r\n"
				+ "		}\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "		int32_t raw_samples[SAMPLE_BUFFER_SIZE];\r\n"
				+ "		void loop()\r\n"
				+ "		{\r\n"
				+ "		  // read from the I2S device\r\n"
				+ "		  size_t bytes_read = 0;\r\n"
				+ "		  i2s_read(I2S_NUM_0, raw_samples, sizeof(int32_t) * SAMPLE_BUFFER_SIZE, &bytes_read, portMAX_DELAY);\r\n"
				+ "		  int samples_read = bytes_read / sizeof(int32_t);\r\n"
				+ "		  // dump the samples out to the serial channel.\r\n"
				+ "		  for (int i = 0; i < samples_read; i++)\r\n"
				+ "		  {\r\n"
				+ "		    Serial.printf(\"%ld\\n\", raw_samples[i]);\r\n"
				+ "		  }\r\n"
				+ "		}\r\n"
				+ "		\r\n"
				+ "		String Helper = \"// Called when a metadata event occurs (i.e. an ID3 tag, an ICY block, etc.\\r\\n\"\r\n"
				+ "				+ \"void MDCallback(void *cbData, const char *type, bool isUnicode, const char *string)\\r\\n\"\r\n"
				+ "				+ \"{\\r\\n\"\r\n"
				+ "				+ \"  const char *ptr = reinterpret_cast<const char *>(cbData);\\r\\n\"\r\n"
				+ "				+ \"  (void) isUnicode; // Punt this ball for now\\r\\n\"\r\n"
				+ "				+ \"  // Note that the type and string may be in PROGMEM, so copy them to RAM for printf\\r\\n\"\r\n"
				+ "				+ \"  char s1[32], s2[64];\\r\\n\"\r\n"
				+ "				+ \"  strncpy_P(s1, type, sizeof(s1));\\r\\n\"\r\n"
				+ "				+ \"  s1[sizeof(s1)-1]=0;\\r\\n\"\r\n"
				+ "				+ \"  strncpy_P(s2, string, sizeof(s2));\\r\\n\"\r\n"
				+ "				+ \"  s2[sizeof(s2)-1]=0;\\r\\n\"\r\n"
				+ "				+ \"  Serial.printf(\\\"METADATA(%s) '%s' = '%s'\\\\n\\\", ptr, s1, s2);\\r\\n\"\r\n"
				+ "				+ \"  Serial.flush();\\r\\n\"\r\n"
				+ "				+ \"}\\r\\n\"\r\n"
				+ "				+ \"\\r\\n\"\r\n"
				+ "				+ \"// Called when there's a warning or error (like a buffer underflow or decode hiccup)\\r\\n\"\r\n"
				+ "				+ \"void StatusCallback(void *cbData, int code, const char *string)\\r\\n\"\r\n"
				+ "				+ \"{\\r\\n\"\r\n"
				+ "				+ \"  const char *ptr = reinterpret_cast<const char *>(cbData);\\r\\n\"\r\n"
				+ "				+ \"  // Note that the string may be in PROGMEM, so copy it to RAM for printf\\r\\n\"\r\n"
				+ "				+ \"  char s1[64];\\r\\n\"\r\n"
				+ "				+ \"  strncpy_P(s1, string, sizeof(s1));\\r\\n\"\r\n"
				+ "				+ \"  s1[sizeof(s1)-1]=0;\\r\\n\"\r\n"
				+ "				+ \"  Serial.printf(\\\"STATUS(%s) '%d' = '%s'\\\\n\\\", ptr, code, s1);\\r\\n\"\r\n"
				+ "				+ \"  Serial.flush();\\r\\n\"\r\n"
				+ "				+ \"}\\r\\n\"\r\n"
				+ "				+ \"\";\r\n"
				+ "";
		translator.addDefinitionCommand(Helper);
		
		
		
	   	String Setup ="DAC_out = new AudioOutputI2S(0,AudioOutputI2S::INTERNAL_DAC);\r\n"
	   		    	+ "DAC_out->begin();\r\n";
	    translator.addSetupCommand(Setup);
		
	    
		String ret = " static int lastms = 0;\r\n"
				+ "\r\n"
				
				+"   if (!WebRadioInit) {\r\n"
				+ "    WebRadioInit = true;\r\n"
				+ "    char *URL="+mp3+";\n"
				+ "    audioLogger = &Serial;\r\n"
				+ "    file = new AudioFileSourceICYStream(URL);\r\n"
				+ "    file->RegisterMetadataCB(MDCallback, (void*)\"ICY\");\r\n"
				+ "    buff = new AudioFileSourceBuffer(file, 2048);\r\n"
				+ "    buff->RegisterStatusCB(StatusCallback, (void*)\"buffer\");\r\n"
				+ "    mp3 = new AudioGeneratorMP3();\r\n"
				+ "    mp3->RegisterStatusCB(StatusCallback, (void*)\"mp3\");\r\n"
				+ "    mp3->begin(buff, DAC_out);\r\n"
				+ "  }\r\n"
				
				+ "  if (mp3->isRunning()) {\r\n"
				+ "    if (millis()-lastms > 1000) {\r\n"
				+ "      lastms = millis();\r\n"
				+ "      Serial.printf(\"Running for %d ms...\\n\", lastms);\r\n"
				+ "      Serial.flush();\r\n"
				+ "     }\r\n"
				+ "    if (!mp3->loop()) mp3->stop();\r\n"
				+ "  } else {\r\n"
				+ "    Serial.printf(\"MP3 done\\n\");\r\n"
				+ "    delay(1000);\r\n"
				+ "  }";
		
		return codePrefix + ret + codeSuffix;
		
	}
}

