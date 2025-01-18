package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTAPDS9960Get extends TranslatorBlock
{

  public IoTAPDS9960Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    
    // Header hinzuf�gen
    translator.addHeaderFile("Wire.h");
    translator.addHeaderFile("Adafruit_APDS9960.h");
    
    
    // Setupdeklaration
    // I2C-initialisieren
    //translator.addSetupCommand("Serial.begin(115200);");
    translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
 
    translator.addSetupCommand("if (!apds.begin()) Serial.println(\"Kein ADPS Gesture-Sensor gefunden\");\n");
    translator.addSetupCommand("apds.setADCGain(APDS9960_AGAIN_64X);");
    translator.addSetupCommand("apds.setProxGain(APDS9960_PGAIN_8X);");
    translator.addSetupCommand("apds.setLED( APDS9960_LEDDRIVE_100MA,APDS9960_LEDBOOST_300PCNT);");
    
    String Dis="/***************************************************************************\r\n"
    		+ "  This is a library for the APDS9960 digital proximity, ambient light, RGB, and gesture sensor\r\n"
    		+ "  This sketch puts the sensor in color mode and reads the RGB and clear values.\r\n"
    		+ "  Designed specifically to work with the Adafruit APDS9960 breakout\r\n"
    		+ "  ----> http://www.adafruit.com/products/3595\r\n"
    		+ "  These sensors use I2C to communicate. The device's I2C address is 0x39\r\n"
    		+ "  Adafruit invests time and resources providing this open source code,\r\n"
    		+ "  please support Adafruit andopen-source hardware by purchasing products\r\n"
    		+ "  from Adafruit!\r\n"
    		+ "  Written by Dean Miller for Adafruit Industries.\r\n"
    		+ "  BSD license, all text above must be included in any redistribution\r\n"
    		+ " ***************************************************************************/\n";
	translator.addDefinitionCommand(Dis);
    
    // Deklarationen hinzuf�gen
    String Def = "// https://github.com/adafruit/Adafruit_APDS9960 Copyright (c) 2012, Adafruit Industries\");\r\n"
    		+ "Adafruit_APDS9960 apds;\n;"
    		+ "float APDS9960_calibrate_r = 1.0;\r\n"
    		+ "float APDS9960_calibrate_g = 1.0;\r\n"
    		+ "float APDS9960_calibrate_b = 1.0;\r\n";
	translator.addDefinitionCommand(Def);

	
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String Sensor = translatorBlock.toCode();
    
    String Read = "// APDS9960 read color values\r\n"
    		+ "void readMeanColorAPDS9960(float &r_mean, float &g_mean, float &b_mean, float &brightness_mean, int N_mean) {\r\n"
    		+ "  uint16_t r, g, b, c;\r\n"
    		+ "  apds.enableColor(true);\r\n"
    		+ "  r_mean = 0.0;\r\n"
    		+ "  g_mean = 0.0;\r\n"
    		+ "  b_mean = 0.0;\r\n"
    		+ "  brightness_mean = 0.0;\r\n"
    		+ "  // Mean N samples \r\n"
    		+ "  for (int i = 0; i < N_mean; i++) {\r\n"
    		+ "    // read RGB values\r\n"
    		+ "    while(!apds.colorDataReady()){\r\n"
    		+ "      delay(5);\r\n"
    		+ "    }\r\n"
    		+ "    apds.getColorData(&r, &g, &b, &c);\r\n"
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
    		+ "// APDS9960 adjust gain an integration time\r\n"
    		+ "void adjustSensitivityAPDS9960() {\r\n"
    		+ "  float r_mean,g_mean,b_mean,brightness_mean;\r\n"
    		+ "  bool saturated = true;\r\n"
    		+ "  int  gain      = 64;\r\n"
    		+ "  int  N         = 2; // Mean values  \r\n"
    		+ "#define MAX_COLOR_VALUE 3000\r\n"
    		+ "#define MIN_INTEGRATION_TIME 1\r\n"
    		+ "#define MAX_INTEGRATION_TIME 400\r\n"
    		+ "\r\n"
    		+ "  // Start with highest sensitivity\r\n"
    		+ "  uint16_t integrationTime = MAX_INTEGRATION_TIME;\r\n"
    		+ "  apds.setADCIntegrationTime(integrationTime);\r\n"
    		+ "  apds.setADCGain(APDS9960_AGAIN_64X);\r\n"
    		+ "\r\n"
    		+ "  while (saturated && integrationTime >= MIN_INTEGRATION_TIME) {\r\n"
    		+ "    // Lese und mittlere die RGB und Helligkeit\r\n"
    		+ "    readMeanColorAPDS9960(r_mean, g_mean, b_mean, brightness_mean,N);\r\n"
    		+ "\r\n"
    		+ "    // Prüfe, ob eine der Farben gesättigt ist\r\n"
    		+ "    if (r_mean < MAX_COLOR_VALUE && g_mean < MAX_COLOR_VALUE && b_mean < MAX_COLOR_VALUE) {\r\n"
    		+ "      saturated = false; // Keine Sättigung -> Belichtungszeit beibehalten\r\n"
    		+ "    } \r\n"
    		+ "    else {\r\n"
    		+ "      // saturation -> reduce integrationTime and gain\r\n"
    		+ "      if ((integrationTime < 100) && (gain > 1)) {\r\n"
    		+ "        gain = gain/4;\r\n"
    		+ "        integrationTime *= 4;\r\n"
    		+ "        N = 4;\r\n"
    		+ "        switch (gain) {\r\n"
    		+ "        case 1:\r\n"
    		+ "          apds.setADCGain(APDS9960_AGAIN_1X);\r\n"
    		+ "          //Serial.println(\"reduce RGB AGAIN 1x\");\r\n"
    		+ "          break;           \r\n"
    		+ "        case 4:\r\n"
    		+ "          apds.setADCGain(APDS9960_AGAIN_4X);\r\n"
    		+ "          //Serial.println(\"reduce RGB AGAIN 4x\");\r\n"
    		+ "          break;           \r\n"
    		+ "        case 16:\r\n"
    		+ "          apds.setADCGain(APDS9960_AGAIN_16X);\r\n"
    		+ "          //Serial.println(\"reduce RGB AGAIN 16x\");\r\n"
    		+ "          break;           \r\n"
    		+ "        }\r\n"
    		+ "      } \r\n"
    		+ "      else {      \r\n"
    		+ "        integrationTime /= 2;\r\n"
    		+ "        apds.setADCIntegrationTime(integrationTime);\r\n"
    		+ "        //Serial.print(\"reduce integrationTime \");\r\n"
    		+ "        //Serial.println(integrationTime);\r\n"
    		+ "      }\r\n"
    		+ "    }\r\n"
    		+ "  }\r\n"
    		+ "  Serial.print(\"APDS9960 adjust: integrationTime = \");\r\n"
    		+ "  Serial.print(integrationTime);\r\n"
    		+ "  Serial.print(\" ms and AGAIN = \");\r\n"
    		+ "  Serial.println(gain);\r\n"
    		+ "}\r\n"
    		+ "\r\n"
    		+ "\r\n"
    		+ "// APDS9960 whitebalance \r\n"
    		+ "void whitebalanceAPDS9960(){\r\n"
    		+ "  float r_mean = 0.0, g_mean = 0.0, b_mean = 0.0, brightness_mean = 0.0;\r\n"
    		+ "  adjustSensitivityAPDS9960();\r\n"
    		+ "  readMeanColorAPDS9960(r_mean, g_mean, b_mean, brightness_mean,4);\r\n"
    		+ "  Serial.println(\"APDS9960 whitebalance\");\r\n"
    		+ "  APDS9960_calibrate_r = 4095./r_mean;\r\n"
    		+ "  APDS9960_calibrate_g = 4095./g_mean;\r\n"
    		+ "  APDS9960_calibrate_b = 4095./b_mean;\r\n"
    		+ "}\r\n"
    		+ "// APDS9960 read channel \r\n"
    		+ "int readAPDS9960(int chan) { // APDS9960 Gesture Sensor Einlesefunktion\r\n"
    		+ "  uint16_t value = 0;\r\n"
    		+ "  uint8_t dum;\r\n"
    		+ "  static float r, g, b, c; // remember last values (RGB ist one vector)\r\n"
    		+ "  static uint32_t lastRGBMeasurement;\r\n"
    		+ "  if (chan >1) {    // get color, all elements from the same measurement\r\n"
    		+ "    if ((millis()-lastRGBMeasurement) > 10) {\r\n"
    		+ "      apds.enableGesture(false);\r\n"
    		+ "      readMeanColorAPDS9960(r, g, b, c,4);\r\n"
    		+ "      lastRGBMeasurement = millis();\r\n"
    		+ "    }\r\n"
    		+ "  } \r\n"
    		+ "  else {\r\n"
    		+ "    apds.enableColor(false);\r\n"
    		+ "    if (chan == 0)\r\n"
    		+ "      apds.enableGesture(true);\r\n"
    		+ "    else \r\n"
    		+ "      apds.enableGesture(false);\r\n"
    		+ "\r\n"
    		+ "    if (chan <= 1)\r\n"
    		+ "      apds.enableProximity(true);\r\n"
    		+ "    else \r\n"
    		+ "      apds.enableProximity(false);\r\n"
    		+ "  }\r\n"
    		+ "  switch (chan) {\r\n"
    		+ "  case 0: // Gesture  \r\n"
    		+ "    value = apds.readGesture();\r\n"
    		+ "    break;\r\n"
    		+ "  case 1: // Proxi\r\n"
    		+ "    value=apds.readProximity();\r\n"
    		+ "    break;\r\n"
    		+ "  case 2: // AmbientLight\r\n"
    		+ "    //value = c;\r\n"
    		+ "    Serial.print(\"readLux\");\r\n"
    		+ "    value = apds.calculateLux(r,g,b);\r\n"
    		+ "    break;\r\n"
    		+ "  case 3: // red\r\n"
    		+ "    value = APDS9960_calibrate_r*r;\r\n"
    		+ "    break;\r\n"
    		+ "  case 4: // green\r\n"
    		+ "    value = APDS9960_calibrate_g*g;     \r\n"
    		+ "    break;\r\n"
    		+ "  case 5: // blue\r\n"
    		+ "    value = APDS9960_calibrate_b*b;\r\n"
    		+ "    break;\r\n"
    		+ "  }\r\n"
    		+ "  return value;\r\n"
    		+ "}\r\n"
    		+ "";
    	    translator.addDefinitionCommand(Read);
    			     
	
    // Code von der Mainfunktion
	ret = "readAPDS9960(" + Sensor + ")";
   
    return codePrefix + ret + codeSuffix;
  }
}



