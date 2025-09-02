package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Audio_Mic_Fourier_Goertzel  extends TranslatorBlock {

	public Audio_Mic_Fourier_Goertzel (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		translator.addHeaderFile("driver/i2s.h");
	
		
        TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String f = translatorBlock.toCode();
		
		
		
		
		String Dis="/* Mikrofon I2S "
				 + "/* ─── Audio-Parameter ─────────────────────────────────────── */\r\n"
				 + "#define SAMPLE_RATE       16000    // 16 kHz → 1,024 MHz BCLK\r\n"
				 + "#define DMA_BLOCK_WORDS   64\r\n"
				 + "#define DMA_BLOCKS        8\r\n"
				 + "#define SAMPLE_BUF_WORDS  512      // 2 kB-Puffer\r\n"
				 + "static int32_t raw[SAMPLE_BUF_WORDS];\r\n"
				 + "\r\n"
				 + "/* ─── Globale Kalibrier-Parameter ─────────────────────────── */\r\n"
				 + "static float noiseFloor  = 1.0f;   // mittlerer Max-Wert in Stille\r\n"
				 + "static float scaleFactor = 1.0f;   // 4095 / noiseFloor\r\n"
				 + "\r\n"
				 + "// ─────────────────────────────────────────────────────────────\r\n"
				 + "//  I²S-Initialisierung\r\n"
				 + "// ─────────────────────────────────────────────────────────────\r\n"
				 + "static void i2sInit()\r\n"
				 + "{\r\n"
				 + "  const i2s_config_t cfg = {\r\n"
				 + "    .mode                 = (i2s_mode_t)(I2S_MODE_MASTER | I2S_MODE_RX),\r\n"
				 + "    .sample_rate          = SAMPLE_RATE,\r\n"
				 + "    .bits_per_sample      = I2S_BITS_PER_SAMPLE_32BIT,\r\n"
				 + "    .channel_format       = I2S_CHANNEL_FMT_ONLY_LEFT,\r\n"
				 + "    .communication_format = I2S_COMM_FORMAT_I2S,\r\n"
				 + "    .intr_alloc_flags     = ESP_INTR_FLAG_LEVEL1,\r\n"
				 + "    .dma_buf_count        = DMA_BLOCKS,\r\n"
				 + "    .dma_buf_len          = DMA_BLOCK_WORDS,\r\n"
				 + "    .use_apll             = false,\r\n"
				 + "    .tx_desc_auto_clear   = false,\r\n"
				 + "    .fixed_mclk           = 0\r\n"
				 + "  };\r\n"
				 + "  const i2s_pin_config_t pins = {\r\n"
				 + "    .mck_io_num   = I2S_PIN_NO_CHANGE,\r\n"
				 + "    .bck_io_num   = IOTW_GPIO_MIC_BCK,\r\n"
				 + "    .ws_io_num    = IOTW_GPIO_MIC_WS,\r\n"
				 + "    .data_out_num = I2S_PIN_NO_CHANGE,\r\n"
				 + "    .data_in_num  = IOTW_GPIO_MIC_SD\r\n"
				 + "  };\r\n"
				 + "  ESP_ERROR_CHECK(i2s_driver_install(I2S_NUM_1, &cfg, 0, nullptr));\r\n"
				 + "  ESP_ERROR_CHECK(i2s_set_pin       (I2S_NUM_1, &pins));\r\n"
				 + "  i2s_stop(I2S_NUM_1);\r\n"
				 + "}\r\n"
				 + "\r\n";
	   	translator.addDefinitionCommand(Dis);
	    	   	
	    	   	
        Dis = "/* ─────────────────────────────────────────────────────────────\r\n"
        		+ " *  Betrag eines Fourier‑Koeffizienten (Goertzel) für freq in dB\r\n"
        		+ " *  Design by ChatGPT o3\r\n"
        		+ " * ──────────────────────────────────────────────────────────── */\r\n"
        		+ "static float captureMagnitude_dB(float targetFreq)\r\n"
        		+ "{\r\n"
        		+ "  size_t bytes_read;\r\n"
        		+ "  i2s_start(I2S_NUM_1);\r\n"
        		+ "  i2s_read(I2S_NUM_1, raw, sizeof(raw), &bytes_read, portMAX_DELAY);\r\n"
        		+ "  i2s_stop(I2S_NUM_1);\r\n"
        		+ "\r\n"
        		+ "  uint32_t samples = bytes_read / sizeof(int32_t);\r\n"
        		+ "  if (samples == 0) return 0.0f;\r\n"
        		+ "\r\n"
        		+ "  float k     = 0.5f + ((float)samples * targetFreq) / (float)SAMPLE_RATE;\r\n"
        		+ "  float omega = 2.0f * PI * k / (float)samples;\r\n"
        		+ "  float coeff = 2.0f * cosf(omega);\r\n"
        		+ "\r\n"
        		+ "  float q0 = 0.0f, q1 = 0.0f, q2 = 0.0f;\r\n"
        		+ "  for (uint32_t i = 0; i < samples; ++i) {\r\n"
        		+ "    float w = 0.5f - 0.5f * cosf((2.0f * PI * i) / (samples - 1)); // Hanning\r\n"
        		+ "    float x = (float)raw[i] * w;\r\n"
        		+ "    q0 = coeff * q1 - q2 + x;\r\n"
        		+ "    q2 = q1;\r\n"
        		+ "    q1 = q0;\r\n"
        		+ "  }\r\n"
        		+ "  float real = q1 - q2 * cosf(omega);\r\n"
        		+ "  float imag = q2 * sinf(omega);\r\n"
        		+ "\r\n"
        		+ "  float Mag = sqrtf(real * real + imag * imag) / (float)samples;\r\n"
        		+ "  float dB        = 20.0f * log10f(Mag / (float)INT32_MAX);\r\n"
        		+ "  return(dB);\r\n"
        		+ "}\r\n"
        		+ "";	   
        translator.addDefinitionCommand(Dis);
	    
		
		
	   	String Setup ="i2sInit();\r\n";
	    translator.addSetupCommand(Setup);
		
	    
		String ret = "captureMagnitude_dB("+f+")";
		
		return codePrefix + ret + codeSuffix;
		
	}
}

