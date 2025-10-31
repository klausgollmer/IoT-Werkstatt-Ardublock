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
 
    translator.addSetupCommand("if (!apds99.begin()) IOTW_PRINTLN(\"Kein ADPS Gesture-Sensor gefunden\"); else delay(10);\n");
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
    		+ "void readMeanColorAPDS9999(float &r_mean, float &g_mean, float &b_mean, float &c_mean, int N_mean) {\r\n"
    		+ "  uint32_t r, g, b, c;\r\n"
    		+ "  r_mean = 0.0;\r\n"
    		+ "  g_mean = 0.0;\r\n"
    		+ "  b_mean = 0.0;\r\n"
    		+ "  c_mean = 0.0;\r\n"
    		+ "  // Mean N samples \r\n"
    		+ "  apds99.enableColor(true);\r\n"
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
    		+ "    c_mean += (float)c;\r\n"
    		+ "  }\r\n"
    		+ "  apds99.enableColor(false);\r\n"
    		+ "\r\n"
    		+ "  // calculate mean\r\n"
    		+ "  r_mean /= N_mean;\r\n"
    		+ "  g_mean /= N_mean;\r\n"
    		+ "  b_mean /= N_mean;\r\n"
    		+ "  c_mean /= N_mean;\r\n"
    		+ "\r\n"
    		+ "#if (IOTW_DEBUG_LEVEL >1)\r\n"
    		+ "  IOTW_PRINTLN(F(\"APDS9999:\"));\r\n"
    		+ "  IOTW_PRINT(F(\"\\ncolor raw: \"));\r\n"
    		+ "  IOTW_PRINT(r_mean);\r\n"
    		+ "  IOTW_PRINT(F(\", \"));  \r\n"
    		+ "  IOTW_PRINT(g_mean);\r\n"
    		+ "  IOTW_PRINT(F(\", \"));  \r\n"
    		+ "  IOTW_PRINT(b_mean);\r\n"
    		+ "  IOTW_PRINT(F(\", \"));  \r\n"
    		+ "  IOTW_PRINTLN(c_mean); \r\n"
    		+ "  if ((APDS9999_calibrate_r+APDS9999_calibrate_r+APDS9999_calibrate_r) != 3) {\r\n"
    		+ "    IOTW_PRINT(F(\"color cal: \"));\r\n"
    		+ "    IOTW_PRINT(r_mean*APDS9999_calibrate_r);\r\n"
    		+ "    IOTW_PRINT(F(\", \"));  \r\n"
    		+ "    IOTW_PRINT(g_mean*APDS9999_calibrate_g);\r\n"
    		+ "    IOTW_PRINT(F(\", \"));  \r\n"
    		+ "    IOTW_PRINT(b_mean*APDS9999_calibrate_b);\r\n"
    		+ "  }\r\n"
    		+ "#endif\r\n"
    		+ "\r\n"
    		+ "}\r\n"
    		+ "\r\n"
    		+ "// APDS9999 adjust gain and integration time\r\n"
    		+ "void adjustSensitivityAPDS9999() {\r\n"
    		+ "  float r_mean,g_mean,b_mean,c_mean;\r\n"
    		+ "  bool saturated = true;\r\n"
    		+ "  int  N         = 2; // Mean values  \r\n"
    		+ "\r\n"
    		+ "  // Start with highest sensitivity\r\n"
    		+ "  uint16_t res      = APDS9999_LS_RESOLUTION_200MS;\r\n"
    		+ "  uint16_t gain     = APDS9999_AGAIN_18X;\r\n"
    		+ "  float saturationValue = 0.75*(float)((1UL << (20 - res)) - 1);\r\n"
    		+ "  apds99.setLSRes(res);\r\n"
    		+ "  apds99.setLSGain(gain);\r\n"
    		+ "  IOTW_PRINT(F(\"\\nAPDS9999 adjust: \"));\r\n"
    		+ "\r\n"
    		+ "#if (IOTW_DEBUG_LEVEL >1)\r\n"
    		+ "  IOTW_PRINT(F(\"sat=\"));\r\n"
    		+ "  IOTW_PRINT(saturationValue);\r\n"
    		+ "  IOTW_PRINT(F(\", res=\"));\r\n"
    		+ "  IOTW_PRINT(res);\r\n"
    		+ "  IOTW_PRINT(F(\", gain=\"));\r\n"
    		+ "  IOTW_PRINTLN(gain); \r\n"
    		+ "#endif\r\n"
    		+ "  while (saturated) {\r\n"
    		+ "    // Lese und mittlere die RGB und Helligkeit\r\n"
    		+ "    readMeanColorAPDS9999(r_mean, g_mean, b_mean, c_mean,N);\r\n"
    		+ "\r\n"
    		+ "    // Prüfe, ob eine der Farben gesättigt ist\r\n"
    		+ "    if (r_mean < saturationValue && g_mean < saturationValue && b_mean < saturationValue) {\r\n"
    		+ "      saturated = false; // Keine Sättigung -> Belichtungszeit beibehalten\r\n"
    		+ "      IOTW_PRINTLN(\"finished\");\r\n"
    		+ "    } \r\n"
    		+ "    else { // mach was dagegen -> reduce integrationTime and gain\r\n"
    		+ "      // Zuerst die Auflösung (= IntegrationTime = Geschwindigkeit)\r\n"
    		+ "      if (res <  APDS9999_LS_RESOLUTION_100MS){ // Achtung hohe res = kleine Auflösung\r\n"
    		+ "        res++;\r\n"
    		+ "      } \r\n"
    		+ "      else {  // dann Verstärkung reduzieren    \r\n"
    		+ "        N = 4;  // jetzt auch mehr Abtastungen möglich   \r\n"
    		+ "        if (gain > APDS9999_AGAIN_3X) {\r\n"
    		+ "          gain--;\r\n"
    		+ "        } \r\n"
    		+ "        else { // Ansonsten beides\r\n"
    		+ "          res++;\r\n"
    		+ "          gain--;\r\n"
    		+ "        }\r\n"
    		+ "      }\r\n"
    		+ "      if (res > APDS9999_LS_RESOLUTION_25MS) res = APDS9999_LS_RESOLUTION_25MS;\r\n"
    		+ "      if (gain < APDS9999_AGAIN_1X) gain = APDS9999_AGAIN_1X;\r\n"
    		+ "      apds99.setLSRes(res);\r\n"
    		+ "      apds99.setLSGain(gain);\r\n"
    		+ "      saturationValue = 0.75*(float)((1UL << (20 - res)) - 1);\r\n"
    		+ "      if ((res == APDS9999_LS_RESOLUTION_25MS) && (gain == APDS9999_AGAIN_1X)) {\r\n"
    		+ "        saturated = false; // Notausgang weil zu viel Licht\r\n"
    		+ "        IOTW_PRINTLN(F(\"Overflow\"));\r\n"
    		+ "      } \r\n"
    		+ "    }\r\n"
    		+ "\r\n"
    		+ "#if (IOTW_DEBUG_LEVEL >1)\r\n"
    		+ "    IOTW_PRINT(F(\"sat=\"));\r\n"
    		+ "    IOTW_PRINT(saturationValue);\r\n"
    		+ "    IOTW_PRINT(F(\"res=\"));\r\n"
    		+ "    IOTW_PRINT(res);\r\n"
    		+ "    IOTW_PRINT(F(\", gain=\"));\r\n"
    		+ "    IOTW_PRINTLN(gain); \r\n"
    		+ "#endif\r\n"
    		+ "  }\r\n"
    		+ "}\r\n"
    		+ "\r\n"
    		+ "// APDS9999 whitebalance \r\n"
    		+ "void whitebalanceAPDS9999(){\r\n"
    		+ "  float r_mean = 0.0, g_mean = 0.0, b_mean = 0.0, c_mean = 0.0;\r\n"
    		+ "  adjustSensitivityAPDS9999();\r\n"
    		+ "  readMeanColorAPDS9999(r_mean, g_mean, b_mean, c_mean,8);\r\n"
    		+ "  APDS9999_calibrate_r = 10000./r_mean;\r\n"
    		+ "  APDS9999_calibrate_g = 10000./g_mean;\r\n"
    		+ "  APDS9999_calibrate_b = 10000./b_mean;\r\n"
    		+ "}\r\n"
    		+ "\r\n"
    		+ "// APDS9999 read channel \r\n"
    		+ "float readAPDS9999(int chan,int N) { // APDS9999 Sensor Einlesefunktion\r\n"
    		+ "  float value = NAN;\r\n"
    		+ "  static float r, g, b, c; // remember last values (RGB ist one vector)\r\n"
    		+ "  static uint32_t lastRGBMeasurement = 0;\r\n"
    		+ "  if ((chan > 1) && (chan <= 6)) {    // get color, all elements from the same measurement\r\n"
    		+ "    if ((millis()-lastRGBMeasurement) > 10) {\r\n"
    		+ "      readMeanColorAPDS9999(r, g, b, c,N);\r\n"
    		+ "      lastRGBMeasurement = millis();\r\n"
    		+ "    }\r\n"
    		+ "  } \r\n"
    		+ "  switch (chan) {\r\n"
    		+ "  case 0: // Gesture  \r\n"
    		+ "    break;\r\n"
    		+ "  case 1: // Proxi (raw)\r\n"
    		+ "    apds99.enableProximity(true);\r\n"
    		+ "    while(!apds99.proxDataReady()){\r\n"
    		+ "      delay(5);\r\n"
    		+ "    }\r\n"
    		+ "    value=apds99.readProximity();\r\n"
    		+ "    apds99.enableProximity(false);\r\n"
    		+ "    break;\r\n"
    		+ "  case 2: // Light in lux\r\n"
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
    		+ "  case 7: // Distance cm\r\n"
    		+ "    apds99.enableProximity(true);\r\n"
    		+ "    while(!apds99.proxDataReady()){\r\n"
    		+ "      delay(5);\r\n"
    		+ "    }\r\n"
    		+ "    value=apds99.readProximity();\r\n"
    		+ "    value=apds99.calcDistCM(value);\r\n"
    		+ "    apds99.enableProximity(false);\r\n"
    		+ "    break;\r\n"
    		+ "  }\r\n"
    		+ "  return value;\r\n"
    		+ "}\r\n"
    		+ "";
            translator.addDefinitionCommand(Read);
    
    // Code von der Mainfunktion
	ret = "readAPDS9999(" + Sensor + ",4)";
   
    return codePrefix + ret + codeSuffix;
  }
}



