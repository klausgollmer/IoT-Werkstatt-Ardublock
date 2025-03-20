package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Sen_APDS9999Get extends TranslatorBlock
{

  public Sen_APDS9999Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    
    // Header hinzuf�gen
    // now in init translator.addHeaderFile("Wire.h");
    translator.addHeaderFile("IoTW_APDS9999.h");
    
    
    // Setupdeklaration
    // I2C-initialisieren
    //translator.addSetupCommand("Serial.begin(115200);");
    // now in init : translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
    // now in init : translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) IOTW_PRINTLN(F(\"Something wrong with I2C\")); \n  #endif \n");
 
    translator.addSetupCommand("if (!apds99.begin()) IOTW_PRINTLN(\"Kein ADPS Gesture-Sensor gefunden\");\n");
    translator.addSetupCommand("apds99.setLSGain(APDS9999_AGAIN_18X);;");
    translator.addSetupCommand("apds99.setProxRes(APDS9999_PS_RESOLUTION_11BIT);");
    translator.addSetupCommand("apds99.setLED(APDS9999_LEDDRIVE_25MA);");
    translator.addSetupCommand("apds99.setProxPulse(255);");
    
    // Deklarationen hinzuf�gen
    String Def = "IoTW_APDS9999 apds99;\n;"
    		+ "float APDS9999_calibrate_r = 1.0;\r\n"
    		+ "float APDS9999_calibrate_g = 1.0;\r\n"
    		+ "float APDS9999_calibrate_b = 1.0;\r\n";
	translator.addDefinitionCommand(Def);

	
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String Sensor = translatorBlock.toCode();
    
    String Read = "// APDS9999 read color values\r\n"
	 		+ "void readMeanColorAPDS9999(float &r_mean, float &g_mean, float &b_mean, float &brightness_mean, int N_mean) {\r\n"
	 		+ "  uint32_t r, g, b, c;\r\n"
	 		+ "  r_mean = 0.0;\r\n"
	 		+ "  g_mean = 0.0;\r\n"
	 		+ "  b_mean = 0.0;\r\n"
	 		+ "  brightness_mean = 0.0;\r\n"
	 		+ "  // Mean N samples \r\n"
	 		+ "  for (int i = 0; i < N_mean; i++) {\r\n"
	 		+ "    // read RGB values\r\n"
	 		+ "    while(!apds99.colorDataReady()){\r\n"
	 		+ "      delay(5);\r\n"
	 		+ "    }\r\n"
	 		+ "    apds99.getColorData(&r, &g, &b, &c);\r\n"
	 		+ "    // Accumulate\r\n"
	 		+ "    r_mean += (float)r;\r\n"
	 		+ "    g_mean += (float)g;\r\n"
	 		+ "    b_mean += (float)b;\r\n"
	 		+ "    brightness_mean += (float)c;\r\n"
	 		+ "  }\r\n"
	 		+ "\r\n"
	 		+ "  // calculate mean\r\n"
	 		+ "  r_mean /= N_mean;\r\n"
	 		+ "  g_mean /= N_mean;\r\n"
	 		+ "  b_mean /= N_mean;\r\n"
	 		+ "  brightness_mean /= N_mean;\r\n"
	 		+ "}\r\n"
	 		+ "\r\n"
	 		+ "// APDS9999 adjust gain an integration time\r\n"
	 		+ "void adjustSensitivityAPDS9999() {\r\n"
	 		+ "  float r_mean,g_mean,b_mean,brightness_mean;\r\n"
	 		+ "  bool saturated = true;\r\n"
	 		+ "  int  N         = 2; // Mean values  \r\n"
	 		+ "#define MAX_COLOR_VALUE 3000\r\n"
	 		+ "#define MIN_INTEGRATION_TIME 1\r\n"
	 		+ "#define MAX_INTEGRATION_TIME 400\r\n"
	 		+ "#define MIN_RESOLUTION APDS9999_LS_RESOLUTION_3MS \r\n"
	 		+ "#define MAX_RESOLUTION APDS9999_LS_RESOLUTION_400MS \r\n"
	 		+ "\r\n"
	 		+ "  // Start with highest sensitivity\r\n"
	 		+ "  uint16_t res      = MAX_RESOLUTION;\r\n"
	 		+ "  uint16_t gain     = APDS9999_AGAIN_18X;\r\n"
	 		+ "  apds99.setLSRes(res);\r\n"
	 		+ "  apds99.setLSGain(gain);\r\n"
	 		+ "\r\n"
	 		+ "  while (saturated) {\r\n"
	 		+ "    // Lese und mittlere die RGB und Helligkeit\r\n"
	 		+ "    readMeanColorAPDS9999(r_mean, g_mean, b_mean, brightness_mean,N);\r\n"
	 		+ "\r\n"
	 		+ "    // Prüfe, ob eine der Farben gesättigt ist\r\n"
	 		+ "    if (r_mean < MAX_COLOR_VALUE && g_mean < MAX_COLOR_VALUE && b_mean < MAX_COLOR_VALUE) {\r\n"
	 		+ "      saturated = false; // Keine Sättigung -> Belichtungszeit beibehalten\r\n"
	 		+ "    } \r\n"
	 		+ "    else { // mach was dagegen -> reduce integrationTime and gain\r\n"
	 		+ "      if ((res <  APDS9999_LS_RESOLUTION_100MS) && (gain > APDS9999_AGAIN_1X)) {\r\n"
	 		+ "        if (gain > APDS9999_AGAIN_1X) gain--;\r\n"
	 		+ "        apds99.setLSGain(gain);\r\n"
	 		+ "        IOTW_PRINTLN(\"reduce gain\");\r\n"
	 		+ "      } \r\n"
	 		+ "      else {      \r\n"
	 		+ "        if (res > MIN_RESOLUTION) res--;\r\n"
	 		+ "        apds99.setLSRes(res);\r\n"
	 		+ "        IOTW_PRINT(\"reduce integrationTime \");\r\n"
	 		+ "      }\r\n"
	 		+ "    }\r\n"
	 		+ "  }\r\n"
	 		+ "  IOTW_PRINT(\"APDS9999 adjust: resolution = \");\r\n"
	 		+ "  IOTW_PRINT(res);\r\n"
	 		+ "  IOTW_PRINT(\" ms and AGAIN = \");\r\n"
	 		+ "  IOTW_PRINTLN(gain);\r\n"
	 		+ "}\r\n"
	 		+ "\r\n"
	 		+ "\r\n"
	 		+ "// APDS9999 whitebalance \r\n"
	 		+ "void whitebalanceAPDS9999(){\r\n"
	 		+ "  float r_mean = 0.0, g_mean = 0.0, b_mean = 0.0, brightness_mean = 0.0;\r\n"
	 		+ "  adjustSensitivityAPDS9999();\r\n"
	 		+ "  readMeanColorAPDS9999(r_mean, g_mean, b_mean, brightness_mean,4);\r\n"
	 		+ "  IOTW_PRINTLN(\"APDS9999 whitebalance\");\r\n"
	 		+ "  APDS9999_calibrate_r = 4095./r_mean;\r\n"
	 		+ "  APDS9999_calibrate_g = 4095./g_mean;\r\n"
	 		+ "  APDS9999_calibrate_b = 4095./b_mean;\r\n"
	 		+ "}\r\n"
	 		+ "\r\n"
	 		+ "// APDS9999 read channel \r\n"
	 		+ "int readAPDS9999(int chan) { // APDS9999 Sensor Einlesefunktion\r\n"
	 		+ "  uint16_t value = 0;\r\n"
	 		+ "  uint8_t dum;\r\n"
	 		+ "  static float r, g, b, c; // remember last values (RGB ist one vector)\r\n"
	 		+ "  static uint32_t lastRGBMeasurement;\r\n"
	 		+ "  if (chan > 1) {    // get color, all elements from the same measurement\r\n"
	 		+ "    apds99.enableProximity(false);\r\n"
	 		+ "    apds99.enableColor(true);\r\n"
	 		+ "    if ((millis()-lastRGBMeasurement) > 10) {\r\n"
	 		+ "      readMeanColorAPDS9999(r, g, b, c,4);\r\n"
	 		+ "      lastRGBMeasurement = millis();\r\n"
	 		+ "    }\r\n"
	 		+ "  } \r\n"
	 		+ "  else {\r\n"
	 		+ "    apds99.enableColor(false);\r\n"
	 		+ "    apds99.enableProximity(true);\r\n"
	 		+ "    while(!apds99.proxDataReady()){\r\n"
	 		+ "      delay(5);\r\n"
	 		+ "    }\r\n"
	 		+ "  }\r\n"
	 		+ "  switch (chan) {\r\n"
	 		+ "  case 0: // Gesture  \r\n"
	 		+ "    break;\r\n"
	 		+ "  case 1: // Proxi\r\n"
	 		+ "    value=apds99.readProximity();\r\n"
	 		+ "    break;\r\n"
	 		+ "  case 2: // AmbientLight\r\n"
	 		+ "    //value = c;\r\n"
	 		+ "    IOTW_PRINT(\"readLux\");\r\n"
	 		+ "    value = apds99.calculateLux();\r\n"
	 		+ "    break;\r\n"
	 		+ "  case 3: // red\r\n"
	 		+ "    value = APDS9999_calibrate_r*r;\r\n"
	 		+ "    break;\r\n"
	 		+ "  case 4: // green\r\n"
	 		+ "    value = APDS9999_calibrate_g*g;     \r\n"
	 		+ "    break;\r\n"
	 		+ "  case 5: // blue\r\n"
	 		+ "    value = APDS9999_calibrate_b*b;\r\n"
	 		+ "    break;\r\n"
	 		+ "  case 6: // IR\r\n"
	 		+ "    value = c;\r\n"
	 		+ "    break;\r\n"
	 		+ "  }\r\n"
	 		+ "  return value;\r\n"
	 		+ "}";
	    	    translator.addDefinitionCommand(Read);
    // Code von der Mainfunktion
	ret = "readAPDS9999(" + Sensor + ")";
   
    return codePrefix + ret + codeSuffix;
  }
}



