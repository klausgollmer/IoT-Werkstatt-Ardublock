package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTTCS34725Get extends TranslatorBlock
{

  public IoTTCS34725Get (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    
    // Header hinzuf�gen
    translator.addHeaderFile("Wire.h");
    translator.addHeaderFile("Adafruit_TCS34725.h");
    
    
    // Setupdeklaration
    // I2C-initialisieren
    translator.addSetupCommand("Serial.begin(115200);");
    translator.addSetupCommand("Wire.begin(); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(\"Something wrong with I2C\"); \n  #endif \n");
 
    translator.addSetupCommand("if (!tcs.begin()) Serial.println(\"Kein TCS34725 RGB-Sensor gefunden\");\n");
    
    
    // Deklarationen hinzuf�gen
    translator.addDefinitionCommand("// https://github.com/adafruit/Adafruit_TCS34725 Copyright (c) 2012, Adafruit Industries");
	translator.addDefinitionCommand("Adafruit_TCS34725 tcs = Adafruit_TCS34725(TCS34725_INTEGRATIONTIME_614MS, TCS34725_GAIN_1X);\r\n");

	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String Sensor = translatorBlock.toCode();
    
    String Read = "int readTCS34725(int chan) { // TCS RGB Sensor Einlesefunktion\r\n" + 
    		"  uint16_t r, g, b, c, colorTemp, lux;\r\n" + 
    		"  int wert = -1;\r\n" + 
    		"  tcs.getRawData(&r, &g, &b, &c);\r\n" + 
    		"  // colorTemp = tcs.calculateColorTemperature(r, g, b);\r\n" + 
    		"  //colorTemp = tcs.calculateColorTemperature_dn40(r, g, b, c);\r\n" + 
    		"  lux = tcs.calculateLux(r, g, b);\r\n" + 
    		"  \r\n" + 
    		"  switch (chan) {\r\n" + 
    		"  case 2: // Lux\r\n" + 
    		"    wert = lux;\r\n" + 
    		"    break;\r\n" + 
    		"  case 3: // red\r\n" + 
    		"    wert = r;\r\n" + 
    		"    break;\r\n" + 
    		"  case 4: // green\r\n" + 
    		"    wert = g;     \r\n" + 
    		"    break;\r\n" + 
    		"  case 5: // blue\r\n" + 
    		"    wert = b;\r\n" + 
    		"    break;\r\n" + 
    		"  }\r\n" + 
    		"  return wert;\r\n" + 
    		"}";

    translator.addDefinitionCommand(Read);
			     
	
    // Code von der Mainfunktion
	ret = "readTCS34725(" + Sensor + ")";
   
    return codePrefix + ret + codeSuffix;
  }
}



/* 
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
    translator.addHeaderFile("SparkFun_APDS9960.h");
    
    
    // Setupdeklaration
    // I2C-initialisieren
    translator.addSetupCommand("Serial.begin(115200);");
    translator.addSetupCommand("Wire.begin(); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(\"Something wrong with I2C\"); \n  #endif \n");
 
    translator.addSetupCommand("if (!apds.init()) Serial.println(\"Kein ADPS Gesture-Sensor gefunden\");\n");
    translator.addSetupCommand("apds.enableLightSensor(false);// Gesture Sensor mit Licht und Abstandsmessung");
    translator.addSetupCommand("apds.enableProximitySensor(false);");
    translator.addSetupCommand("apds.enableGestureSensor(false);");
    translator.addSetupCommand("delay(500); // Sensorkalibration");
    
    // Deklarationen hinzuf�gen
    translator.addDefinitionCommand("// Sparkfun ADS9960 Lib https://learn.sparkfun.com/tutorials/apds-9960-rgb-and-gesture-sensor-hookup-guide");
	translator.addDefinitionCommand("SparkFun_APDS9960 apds = SparkFun_APDS9960();");

	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String Sensor = translatorBlock.toCode();
    
    String Read = "int readAPDS9960(int chan) { // Sparkfun Gesture Sensor Einlesefunktion\n"
    	+"  uint16_t value = 0;\n"
    	+"  uint8_t dum;\n" 
    	+"  switch (chan) {\n"
    	+"     case 0: // Gesture\n"
    	+"      if ( apds.isGestureAvailable() ) {\n"
    	+"         value = apds.readGesture();\n"
    	+"       }\n"
    	+"      break;\n"
    	+"     case 1: // Proxi\n"
    	+"      apds.disableGestureSensor(); // Gestenerkennung aus\n"
    	+"      apds.readProximity(dum);\n"
    	+"      value = dum;\n"
    	+"      break;\n"
    	+"     case 2: // AmbientLight\n"
    	+"      apds.disableGestureSensor(); // Gestenerkennung aus\n"
    	+"      apds.readAmbientLight(value);\n"
    	+"      break;\n"
    	+"     case 3: // red\n"
    	+"      apds.disableGestureSensor(); // Gestenerkennung aus\n"
    	+"      apds.readRedLight(value);\n"
    	+"      break;\n"
    	+"     case 4: // green\n"
    	+"      apds.disableGestureSensor(); // Gestenerkennung aus\n"
    	+"      apds.readGreenLight(value);\n"
    	+"      break;\n"
    	+"     case 5: // blue\n"
    	+"      apds.disableGestureSensor(); // Gestenerkennung aus\n"
    	+"      apds.readBlueLight(value);\n"
    	+"      break;\n"
        +"    }\n"
    	+"    return value;\n"
    	+"}\n";

    translator.addDefinitionCommand(Read);
			     
	
    // Code von der Mainfunktion
	ret = "readAPDS9960(" + Sensor + ")";
   
    return codePrefix + ret + codeSuffix;
  }
}


*/