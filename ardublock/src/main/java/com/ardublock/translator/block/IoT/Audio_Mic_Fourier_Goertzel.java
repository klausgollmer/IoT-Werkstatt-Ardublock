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
        boolean band = true;
        String delta = "0";
		translator.addHeaderFile("driver/i2s.h");
	
		
        TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String f = translatorBlock.toCode();
 		
		translatorBlock = this.getTranslatorBlockAtSocket(1);
		if (translatorBlock==null) { 
             band =false;
		} else {
 			 delta = translatorBlock.toCode();
 			 if (Integer.parseInt(delta) == 0) band = false;
		}
		
		
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
        		+ " *  Design by ChatGPT 5\r\n"
        		+ " * ──────────────────────────────────────────────────────────── */\r\n"
        		+ "// Parameter\r\n"
        		+ "#define FS   SAMPLE_RATE\r\n"
        		+ "#define N    2048\r\n"
        		+ "#define DF   ((float)FS/(float)N)\r\n"
        		+ "\r\n"
        		+ "static int32_t s[N];\r\n"
        		+ "static float   hannW[N];\r\n"
        		+ "static bool    winReady=false;\r\n"
        		+ "\r\n"
        		+ "static void goertzelInit() {\r\n"
        		+ "  for (int n=0;n<N;n++) hannW[n] = 0.5f - 0.5f * cosf(2.0f*PI*n/(N-1));\r\n"
        		+ "  winReady = true;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ "static void captureBlockN_startStop(int32_t* dst) {\r\n"
        		+ " \r\n"
        		+ "  i2s_zero_dma_buffer(I2S_NUM_1);\r\n"
        		+ "  i2s_start(I2S_NUM_1);\r\n"
        		+ "\r\n"
        		+ "  // Warm-up: kleinen Chunk verwerfen (verhindert „Nullblock“)\r\n"
        		+ "  int32_t dump[128];\r\n"
        		+ "  size_t br=0; (void)i2s_read(I2S_NUM_1, dump, sizeof(dump), &br, 5/portTICK_PERIOD_MS);\r\n"
        		+ "\r\n"
        		+ "  size_t need = N*sizeof(int32_t), off = 0;\r\n"
        		+ "  while (off < need) { size_t got=0; i2s_read(I2S_NUM_1, (uint8_t*)dst + off, need - off, &got, portMAX_DELAY); off += got; }\r\n"
        		+ "  i2s_stop(I2S_NUM_1);\r\n"
        		+ "\r\n"
        		+ "  int64_t acc=0;\r\n"
        		+ "  for (int i=0;i<N;i++){ dst[i] >>= 8; acc += dst[i]; }     // 24->32\r\n"
        		+ "  int32_t mean = (int32_t)(acc / N);\r\n"
        		+ "  for (int i=0;i<N;i++) dst[i] -= mean;                     // DC weg\r\n"
        		+ "}\r\n"
        		+ "\r\n";
                translator.addDefinitionCommand(Dis);
        
        		Dis = "static void goertzelAtFreq(float f, double& Re, double& Im) {\r\n"
        		+ "  const float omega = 2.0f * PI * (f / FS);\r\n"
        		+ "  const float coeff = 2.0f * cosf(omega);\r\n"
        		+ "  double q0=0.0, q1=0.0, q2=0.0;\r\n"
        		+ "  for (int n=0;n<N;n++) {\r\n"
        		+ "    const double x = (double)s[n] * (double)hannW[n];\r\n"
        		+ "    q0 = coeff*q1 - q2 + x;\r\n"
        		+ "    q2 = q1; q1 = q0;\r\n"
        		+ "  }\r\n"
        		+ "  Re = q1 - q2 * cosf(omega);\r\n"
        		+ "  Im = q2 * sinf(omega);\r\n"
        		+ "}\r\n"
        		+ "\r\n";
        	    translator.addDefinitionCommand(Dis);
        		
        	    
        	    if (band == false) {
        		Dis="static float goertzelPeakAround(float f0) {\r\n"
        		+ "\r\n"
        		+ "  float f_est= NAN;\r\n"
        		+ "  float dbfs = NAN;\r\n"
        		+ "  captureBlockN_startStop(s);\r\n"
        		+ "\r\n"
        		+ "  const float df = DF;\r\n"
        		+ "  // drei Punkte messen\r\n"
        		+ "  double Rm,Im, R0,I0, Rp,Ip;\r\n"
        		+ "  goertzelAtFreq(f0 - df, Rm,Im);\r\n"
        		+ "  goertzelAtFreq(f0,      R0,I0);\r\n"
        		+ "  goertzelAtFreq(f0 + df, Rp,Ip);\r\n"
        		+ "\r\n"
        		+ "  double P[3] = { Rm*Rm + Im*Im, R0*R0 + I0*I0, Rp*Rp + Ip*Ip };\r\n"
        		+ "  float  F[3] = { f0 - df,        f0,             f0 + df      };\r\n"
        		+ "\r\n"
        		+ "  // Index des Maximums finden und als Zentrum setzen\r\n"
        		+ "  int c = (P[1] >= P[0] && P[1] >= P[2]) ? 1 : (P[0] >= P[2] ? 0 : 2);\r\n"
        		+ "  int l = c - 1, r = c + 1;\r\n"
        		+ "  if (l < 0 || r > 2) { // Randfall: kein Nachbar -> keine Interpolation\r\n"
        		+ "    f_est = F[c];\r\n"
        		+ "    double amp = sqrt(P[c]) / (N * 0.5);                 // Hann CG=0.5\r\n"
        		+ "    double ratio = amp / (double)(1<<23);\r\n"
        		+ "    if (!isfinite(ratio) || ratio <= 0.0) ratio = 1e-30;\r\n"
        		+ "    dbfs = (float)(20.0 * log10(ratio));\r\n"
        		+ "    return (dbfs);\r\n"
        		+ "  }\r\n"
        		+ "\r\n"
        		+ "  double Pl = P[l], Pc = P[c], Pr = P[r];\r\n"
        		+ "  // Parabolische Interpolation um das Max\r\n"
        		+ "  double denom = (Pl - 2.0*Pc + Pr);\r\n"
        		+ "  double delta = (fabs(denom) > 1e-30) ? 0.5*(Pl - Pr)/denom : 0.0;\r\n"
        		+ "  if (delta < -1.0) delta = -1.0; if (delta > 1.0) delta = 1.0;\r\n"
        		+ "\r\n"
        		+ "  double Ppeak = Pc - 0.25*(Pl - Pr)*delta;\r\n"
        		+ "  if (!(Ppeak > 0.0)) Ppeak = Pc;  // Fallback: niemals ≤0\r\n"
        		+ "\r\n"
        		+ "  float f = F[c] + (float)delta * df;\r\n"
        		+ "\r\n"
        		+ "  // dBFS (24-bit FS ≈ 2^23), keine NaN/−inf\r\n"
        		+ "  double amp = sqrt(Ppeak) / (N * 0.5);\r\n"
        		+ "  double ratio = amp / (double)(1<<23);\r\n"
        		+ "  if (!isfinite(ratio) || ratio <= 1e-30) ratio = 1e-30;\r\n"
        		+ "\r\n"
        		+ "  f_est = f;\r\n"
        		+ "  dbfs  = (float)(20.0 * log10(ratio));\r\n"
        		+ "  return dbfs;\r\n"
        		+ "}";
       	    } else {
                Dis = "// -------- Band-Goertzel: Breite in Hz steuerbar --------\r\n"
        		+ "static float goertzelBandDb(float f0, float bw_hz) {\r\n"
        		+ "float f_est_out;\n" 
        		+ "  // Schutz & Normalisierung der Bandbreite\r\n"
        		+ "  if (bw_hz < DF)        bw_hz = DF;\r\n"
        		+ "  if (bw_hz > FS * 0.5f) bw_hz = FS * 0.5f;\r\n"
        		+ "\r\n"
        		+ "  captureBlockN_startStop(s);  // füllt s[N] (Start/Stop, 24->32, DC-Remove)\r\n"
        		+ "\r\n"
        		+ "  // Anzahl Nachbarn links/rechts so wählen, dass Gesamtband ~ bw_hz\r\n"
        		+ "  int K = (int)ceilf(0.5f * (bw_hz / DF));   // Nachbarn pro Seite\r\n"
        		+ "  if (K < 1)  K = 1;\r\n"
        		+ "  if (K > 24) K = 24;                        // Rechenzeit-Schutz\r\n"
        		+ "\r\n"
        		+ "  const int M = 2*K + 1;                     // Gesamtanzahl Punkte\r\n"
        		+ "  double Psum = 0.0;                         // Σ wF * P(f)\r\n"
        		+ "  double PFsum = 0.0;                        // Σ wF * P(f) * f\r\n"
        		+ "  double W2sum = 0.0;                        // Σ wF^2 (für Amplituden-Norm)\r\n"
        		+ "\r\n"
        		+ "  for (int j = -K; j <= K; ++j) {\r\n"
        		+ "    const float fj = f0 + j * DF;\r\n"
        		+ "    // Frequenzen außerhalb (0, fs/2) überspringen\r\n"
        		+ "    if (fj <= 0.0f || fj >= 0.5f * FS) continue;\r\n"
        		+ "\r\n"
        		+ "    // „Frequenzfenster“ (Hann) über die M Punkte\r\n"
        		+ "    const int    idx  = j + K;                         // 0..M-1\r\n"
        		+ "    const float  wF   = 0.5f - 0.5f * cosf(2.0f * PI * idx / (float)(M - 1));\r\n"
        		+ "\r\n"
        		+ "    double Re, Im;\r\n"
        		+ "    goertzelAtFreq(fj, Re, Im);\r\n"
        		+ "    const double P = Re*Re + Im*Im;                    // Leistung bei fj\r\n"
        		+ "\r\n"
        		+ "    Psum  += wF * P;\r\n"
        		+ "    PFsum += wF * P * fj;\r\n"
        		+ "    W2sum += (double)wF * (double)wF;\r\n"
        		+ "  }\r\n"
        		+ "\r\n"
        		+ "  // Frequenz-Schätzung: leistungsgewichteter Schwerpunkt\r\n"
        		+ "  float f_est = (Psum > 0.0) ? (float)(PFsum / Psum) : f0;\r\n"
        		+ "  if (!(f_est > 0.0f)) f_est = f0;\r\n"
        		+ "  if (f_est_out) f_est_out = f_est;\r\n"
        		+ "\r\n"
        		+ "  // Amplituden-Schätzung in dBFS (zeitliches Hann: coherent gain = 0.5)\r\n"
        		+ "  const double CG     = 0.5;                  // Zeitfenster-Kohärenzgewinn\r\n"
        		+ "  const double W2     = (W2sum > 0.0) ? W2sum : 1.0;\r\n"
        		+ "  double amp = sqrt(Psum) / (N * CG * sqrt(W2));\r\n"
        		+ "  const double FSAMP  = (double)(1 << 23);    // ~24-bit full-scale\r\n"
        		+ "  double ratio = amp / FSAMP;\r\n"
        		+ "  if (!isfinite(ratio) || ratio <= 1e-30) ratio = 1e-30; // keine NaN/−inf\r\n"
        		+ "  return (float)(20.0 * log10(ratio));\r\n"
        		+ "}\r\n"
        		+ "";		
       	    }
        		
        translator.addDefinitionCommand(Dis);
	    
		
		
	   	String Setup ="i2sInit();\r\n";
	    translator.addSetupCommand(Setup);
	   	Setup ="goertzelInit();\r\n";
	    translator.addSetupCommand(Setup);
		
	    String ret;
		// String ret = "goertzelPeakAround("+f+","+delta+")";
	    if (band == true)
		   ret = "goertzelBandDb("+f+","+delta+")";
	    else
	       ret = "goertzelPeakAround("+f+")";
			
		return codePrefix + ret + codeSuffix;
		
	}
}

