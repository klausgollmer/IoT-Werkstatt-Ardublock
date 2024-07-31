package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTAPDS9960Cal extends TranslatorBlock
{

  public IoTAPDS9960Cal (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
    translator.addSetupCommand("Serial.begin(115200);");
    translator.addSetupCommand("Wire.begin(GPIO_I2C_SDA, GPIO_I2C_SCL); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
 
    translator.addSetupCommand("if (!apds.begin()) Serial.println(\"Kein ADPS Gesture-Sensor gefunden\");\n");
    translator.addSetupCommand("apds.setADCGain(APDS9960_AGAIN_64X);");
    translator.addSetupCommand("apds.setProxGain(APDS9960_PGAIN_8X);");
    translator.addSetupCommand("apds.setLED( APDS9960_LEDDRIVE_100MA,APDS9960_LEDBOOST_300PCNT);");
    
    
    // Deklarationen hinzuf�gen
    String Def = "// https://github.com/adafruit/Adafruit_APDS9960 Copyright (c) 2012, Adafruit Industries\");\r\n"
    		+ "Adafruit_APDS9960 apds;\n;"
    		+ "float APDS9960_calibrate_r = 1.0;\r\n"
    		+ "float APDS9960_calibrate_g = 1.0;\r\n"
    		+ "float APDS9960_calibrate_b = 1.0;\r\n";
	translator.addDefinitionCommand(Def);
    
	
	
	
	//TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    //String Sensor = translatorBlock.toCode();
    
	String Read = "// https://github.com/adafruit/Adafruit_APDS9960 Copyright (c) 2012, Adafruit Industries\r\n"
    		+ "int readAPDS9960(int chan) { // APDS9960 Gesture Sensor Einlesefunktion\r\n"
    		+ "  uint16_t value = 0;\r\n"
    		+ "  uint8_t dum;\r\n"
    		+ "  uint16_t r, g, b, c;\r\n"
    		+ "  if (chan >1) {    //wait for color data to be ready\r\n"
    		+ "    apds.enableColor(true);\r\n"
    		+ "    while(!apds.colorDataReady()){\r\n"
    		+ "      delay(5);\r\n"
    		+ "    }\r\n"
    		+ "    apds.getColorData(&r, &g, &b, &c);\r\n"
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
    		+ "\r\n"
    		+ "  switch (chan) {\r\n"
    		+ "  case 0: // Gesture  \r\n"
    		+ "    value = apds.readGesture();\r\n"
    		+ "    break;\r\n"
    		+ "  case 1: // Proxi\r\n"
    		+ "    value=apds.readProximity();\r\n"
    		+ "    break;\r\n"
    		+ "  case 2: // AmbientLight\r\n"
    		+ "    //value = c;\r\n"
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
    		+ "}";
            translator.addDefinitionCommand(Read);
			     
    
    Read = "void calibrateAPDS9960(){\r\n"
    		+ "  uint16_t r=0,g=0,b=0,c,sum;\r\n"
    		+ "  float rs=0.,gs=0.,bs=0.;\r\n"
    		+ "  #define MEAN_N 64 \r\n"
    		+ "  Serial.println(\"Calibrate APDS9960 RGB Sensor\");\r\n"
    		+ "  apds.enableColor(true);\r\n"
    		+ "  // mean values\r\n"
    		+ "  for (int i = 1; i<=MEAN_N;i++) {\r\n"
    		+ "    while(!apds.colorDataReady()){\r\n"
    		+ "      delay(5);\r\n"
    		+ "    }\r\n"
    		+ "    apds.getColorData(&r, &g, &b, &c);\r\n"
    		+ "    rs+=r; gs+=g; bs+=b;\r\n"
    		+ "  }  \r\n"
    		+ "  rs = rs/MEAN_N;gs = gs/MEAN_N; bs = bs/MEAN_N;\r\n"
    		+ "  if ((rs+gs+bs) < (3*128)) {\r\n"
    		+ "    Serial.println(\"values to small, increase RGB AGAIN to 64\");\r\n"
    		+ "    apds.setADCGain(APDS9960_AGAIN_64X);\r\n"
    		+ "  } else if ((rs+gs+bs) > (3*4000)) {\r\n"
    		+ "      Serial.println(\"values to big, reduce RGB AGAIN to 16\");\r\n"
    		+ "      apds.setADCGain(APDS9960_AGAIN_16X);\r\n"
    		+ "  }\r\n"
    		+ "  // Measure again \r\n"
    		+ "  rs=0.,gs=0.,bs=0.;\r\n"
    		+ "  // mean values\r\n"
    		+ "  for (int i = 1; i<=MEAN_N;i++) {\r\n"
    		+ "    while(!apds.colorDataReady()){\r\n"
    		+ "      delay(5);\r\n"
    		+ "    }\r\n"
    		+ "    apds.getColorData(&r, &g, &b, &c);\r\n"
    		+ "    rs+=r; gs+=g; bs+=b;\r\n"
    		+ "  }  \r\n"
    		+ "  rs = rs/MEAN_N;gs = gs/MEAN_N; bs = bs/MEAN_N;\r\n"
    		+ "  APDS9960_calibrate_r = 4096./rs;\r\n"
    		+ "  APDS9960_calibrate_g = 4096./gs;\r\n"
    		+ "  APDS9960_calibrate_b = 4096./bs;\r\n"
    		+ "}";
    	    translator.addDefinitionCommand(Read);
    
    
	
    // Code von der Mainfunktion
	ret = "calibrateAPDS9960();\n";
   
    return codePrefix + ret + codeSuffix;
  }
}



