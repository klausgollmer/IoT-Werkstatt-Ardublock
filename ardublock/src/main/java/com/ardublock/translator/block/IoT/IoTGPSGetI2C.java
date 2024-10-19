package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTGPSGetI2C extends TranslatorBlock
{

  public IoTGPSGetI2C (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Header hinzuf�gen
    translator.addHeaderFile("Adafruit_GPS.h");
    translator.addHeaderFile("Wire.h");
    
    // Setupdeklaration
    // I2C-initialisieren

	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();

	translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String print = translatorBlock.toCode();
	String Dis="/* Adafruit GPS"
			 + "Software License Agreement (BSD License)\r\n"
			 + "Copyright (c) 2012, Adafruit Industries\r\n"
			 + "All rights reserved https://github.com/adafruit/Adafruit_GPS?tab=License-1-ov-file#readme */\n";
	    translator.addDefinitionCommand(Dis); 

    
    String Def = "Adafruit_GPS GPS(&Wire);\n";
    translator.addDefinitionCommand(Def);
   
	translator.addSetupCommand("Serial.begin(115200);");
	translator.addSetupCommand("Wire.begin(GPIO_I2C_SDA, GPIO_I2C_SCL); // ---- Initialisiere den I2C-Bus \n");
	translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");

    String Set = " // Adafruit GPS-Lib Written by Limor Fried/Ladyada for Adafruit Industries. BSD license\r\n" + 
    		"  // https://github.com/adafruit/Adafruit_GPS\r\n" + 
    		"  GPS.begin(0x10);  // The I2C address to use is 0x10\r\n" + 
    		"  GPS.sendCommand(PMTK_SET_NMEA_OUTPUT_RMCGGA);\r\n" + 
    		"  GPS.sendCommand(PMTK_SET_NMEA_UPDATE_1HZ); // 1 Hz update rate";
	translator.addSetupCommand(Set);

	String Util ="// -----------------  GPS \r\n" + 
			"// Adafruit GPS-Lib Written by Limor Fried/Ladyada for Adafruit Industries. BSD license\r\n" + 
			"// https://github.com/adafruit/Adafruit_GPS\r\n" + 
			"float updateGPSI2C(uint8_t sel, int printout) {\r\n" + 
			"  int   tout = 5000;     // 5 sec timeout\r\n" + 
			"  float ret  = 0;         // return value\r\n" + 
			"  uint8_t ok = 0;       // valid value\r\n" + 
			"  char c;\r\n" + 
			"  if (sel == 1) {        // Update GPS values\r\n" + 
			"    // processing all old messages in buffer\r\n" + 
			"    // Serial.println(\"\\nclear old\");\r\n" + 
			"    c = ' ';\r\n" + 
			"    while ((c > 0)) {\r\n" + 
			"       if (GPS.newNMEAreceived()) {\r\n" + 
			"         //Serial.println(GPS.lastNMEA());\r\n" + 
			"         GPS.parse(GPS.lastNMEA());\r\n" + 
			"       }\r\n" + 
			"       c = GPS.read();\r\n" + 
			"       if (c==0) c=GPS.read(); // only 2 consecutive NULL will end\r\n" + 
			"    }\r\n" + 
			"    \r\n" + 
			"    ok = 0;\r\n" + 
			"    //Serial.print(\"\\nGPS: listen \");\r\n" + 
			"    while ((!ok) && (tout > 0))  { \r\n" + 
			"      c = GPS.read();\r\n" + 
			"      if (GPS.newNMEAreceived()) {\r\n" + 
			"        ok = GPS.parse(GPS.lastNMEA());\r\n" + 
			"        //Serial.print(\"new: \");\r\n" + 
			"        //Serial.println(ok);\r\n" + 
			"      }\r\n" + 
			"      tout--; \r\n" + 
			"      delay(1);\r\n" + 
			"    } // wait for new input\r\n" + 
			"    if (tout == 0) {\r\n" + 
			"      Serial.println(\"GPS-timeout\");\r\n" + 
			"    }\r\n" + 
			"  }\r\n" + 
			"  switch (sel) {\r\n" + 
			"  case 1: \r\n" + 
			"    if (ok) ret = GPS.fix;\r\n" + 
			"      else  ret = NAN;\r\n" + 
			"    break;\r\n" + 
			"  case 2: \r\n" + 
			"    ret = GPS.latitude/100.;\r\n" + 
			"    break;\r\n" + 
			"  case 3: \r\n" + 
			"    ret = GPS.longitude/100.;\r\n" + 
			"    break;\r\n" + 
			"  case 4: \r\n" + 
			"    ret = GPS.altitude;\r\n" + 
			"    break;\r\n" + 
			"  case 5: \r\n" + 
			"    ret = GPS.HDOP;\r\n" + 
			"    break;\r\n" + 
			"  }\r\n" + 
			"\r\n" + 
			"  if (printout) {\r\n" + 
			"     Serial.print(\"\\nTime: \");\r\n" + 
			"    if (GPS.hour < 10) { Serial.print('0'); }\r\n" + 
			"    Serial.print(GPS.hour, DEC); Serial.print(':');\r\n" + 
			"    if (GPS.minute < 10) { Serial.print('0'); }\r\n" + 
			"    Serial.print(GPS.minute, DEC); Serial.print(':');\r\n" + 
			"    if (GPS.seconds < 10) { Serial.print('0'); }\r\n" + 
			"    Serial.print(GPS.seconds, DEC); Serial.print('.');\r\n" + 
			"    Serial.print(\"Date: \");\r\n" + 
			"    Serial.print(GPS.day, DEC); Serial.print('/');\r\n" + 
			"    Serial.print(GPS.month, DEC); Serial.print(\"/20\");\r\n" + 
			"    Serial.println(GPS.year, DEC);\r\n" + 
			"    Serial.print(\"Fix: \"); Serial.print((int)GPS.fix);\r\n" + 
			"    Serial.print(\" quality: \"); Serial.println((int)GPS.fixquality);\r\n" + 
			"    if (GPS.fix) {\r\n" + 
			"      Serial.print(\"Location: \");\r\n" + 
			"      Serial.print(GPS.latitude, 4); Serial.print(GPS.lat);\r\n" + 
			"      Serial.print(\", \");\r\n" + 
			"      Serial.print(GPS.longitude, 4); Serial.println(GPS.lon);\r\n" + 
			"      Serial.print(\"Speed (knots): \"); Serial.println(GPS.speed);\r\n" + 
			"      Serial.print(\"Angle: \"); Serial.println(GPS.angle);\r\n" + 
			"      Serial.print(\"Altitude: \"); Serial.println(GPS.altitude);\r\n" + 
			"      Serial.print(\"Satellites: \"); Serial.println((int)GPS.satellites);\r\n" + 
			"    }\r\n" + 
			"  }\r\n" + 
			"\r\n" + 
			"  return ret;   \r\n" + 
			"}";
	
    translator.addDefinitionCommand(Util);
	
   	
    // Code von der Mainfunktion
	ret = "updateGPSI2C("+code+","+print+")";
	
   
    return codePrefix + ret + codeSuffix;
  }
}