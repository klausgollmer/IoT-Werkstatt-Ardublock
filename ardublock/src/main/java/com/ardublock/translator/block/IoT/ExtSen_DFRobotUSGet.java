package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_DFRobotUSGet extends TranslatorBlock
{

  public ExtSen_DFRobotUSGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String rxpin = translatorBlock.toCode();

    boolean use_uart = true;
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String UART = translatorBlock.toCode();
    if ((UART.contains("3")))
	    use_uart = false;
    
    
    
    String Setup,Def; 
    if (use_uart) {
       translator.addHeaderFile("HardwareSerial.h");
       translator.addHeaderFile("#if defined(ESP32)\n #include <driver/uart.h> \n #endif\n");

       Setup ="#if defined(ESP32)\n"
       		+ " swSerUS.begin(9600, SERIAL_8N1,"+rxpin+", -1); // UART kein TX\r\n"
       		+ "#else \n"
            + " swSerUS.begin(9600,SWSERIAL_8N1); // Maxbotix ultrasonic \n"
       	    + "#endif \n";   
       Def =   "#if defined(ESP32) \n"
       		+  " HardwareSerial swSerUS("+UART+"); // Hardware UART \n"
       		+ "#else \n"
            + "  SoftwareSerial swSerUS("+rxpin+", -1); // RXPin, TX not used, Library: https://github.com/plerup/espsoftwareserial/, LGPL-2.1 license, Peter Lerup\n"   		   	
       	    + "#endif \n";   
       
    } else {
       translator.addHeaderFile("SoftwareSerial.h");
       Setup = "swSerUS.begin(9600,SWSERIAL_8N1); // Maxbotix ultrasonic \n";
       Def =   "SoftwareSerial swSerUS("+rxpin+", -1); // RXPin, TX not used, Library: https://github.com/plerup/espsoftwareserial/, LGPL-2.1 license, Peter Lerup\n";   		   	
    }
    
    translator.addSetupCommand(Setup);
   	translator.addDefinitionCommand(Def);   
    
    
   	/*
    
    String read = "//------------------------------- Ultrasound Distance Measurement DFRobot, UART protocol \n"
    		+ "float readDFRobotUS_"+rxpin+"(int maxLevel){\r\n"
    		+ "  unsigned char data[3]; // buffer\r\n"
    		+ "  float distance = NAN;\r\n"
    		+ "  int tout=100,checksum=0;\r\n"
    		+ "  //IOTW_PRINT(\"Start\");\r\n"
    		+ "  while (swSerUS.available() > 0) {swSerUS.read();}// skip old data\r\n"
    		+ "  //IOTW_PRINT(\" flush\");\r\n"
    		+ "  while ((tout > 0) && (isnan(distance))) {\r\n"
    		+ "    if (swSerUS.available()) { // new data\r\n"
    		+ "      if (swSerUS.read() == 0xFF) { // Start Message\r\n"
    		+ "        //IOTW_PRINTLN(\"Start:\");\r\n"
    		+ "        int len = swSerUS.readBytes(data,3); // message\r\n"
    		+ "        checksum = (0xFF + data[0] + data[1]) & 0x00FF;\r\n"
    		+ "        if ((len == 3) && (checksum == data[2])) {\r\n"
    		+ "          distance = (data[0] << 8) + data[1];\r\n"
    		+ "          if (distance < 280)  {\r\n"
    		+ "            if (distance == 0)    // upper level, no echo?\r\n"
    		+ "              distance = maxLevel;\r\n"
    		+ "            else distance = 280;  // lower level\r\n"
    		+ "          }\r\n"
    		+ "          //IOTW_PRINT(distance);\r\n"
    		+ "        } else {\r\n"
    		+ "           IOTW_PRINT(\"Checksum Error, data len=\");\r\n"
    		+ "           IOTW_PRINT(len);\r\n"
    		+ "           IOTW_PRINT(data[0],HEX);\r\n"
    		+ "           IOTW_PRINT(data[1],HEX);\r\n"
    		+ "           IOTW_PRINTLN(data[2],HEX);\r\n"
    		+ "        }\r\n"
    		+ "      }\r\n"
    		+ "    } else { // countdown\r\n"
    		+ "      tout--;\r\n"
    		+ "      //IOTW_PRINT('.');\r\n"
    		+ "      delay(2);\r\n"
    		+ "    }\r\n"
    		+ "  }\r\n"
    		+ "  return distance;\r\n"
    		+ "}";
    		*/
   	
   	String read = "// ------------------  Driver DFRobot \n"
   			+ "//     https://wiki.dfrobot.com/A01NYUB%20Waterproof%20Ultrasonic%20Sensor%20SKU_SEN0313 \n"  
   			+ "float readDFRobotUS_"+rxpin+"(long lower, long upper) {\r\n"
   			+ "  const int      MAX_TRIES         = 8;\r\n"
   			+ "  const uint32_t TIMEOUT_START_MS  = 350; // auf 0xFF warten\r\n"
   			+ "  const uint32_t TIMEOUT_FRAME_MS  = 40;  // 3 Bytes nach 0xFF (bei SoftwareSerial ggf. 30–40 ms)\r\n"
   			+ "\r\n"
   			+ "  if (!(upper > lower)) return NAN;\r\n"
   			+ "\r\n"
   			+ "  uint8_t data[3];\r\n"
   			+ "  float   lastBoundary = NAN;\r\n"
   			+ "\r\n"
   			+ "  // alte Bytes verwerfen (nach Wartezeit/Sleep etc.)\r\n"
   			+ "  while (swSerUS.available()) swSerUS.read();\r\n"
   			+ "\r\n"
   			+ "  for (int attempt = 0; attempt < MAX_TRIES; ++attempt) {\r\n"
   			+ "\r\n"
   			+ "#ifdef ESP32\r\n"
   			+ "    // einmal \"Luft holen\": kurz schlafen und danach erneut mit frischem Puffer versuchen\r\n"
   			+ "    if ((attempt == MAX_TRIES - 2) && (lastBoundary == (float) upper)) {\r\n"
   	 		+ "      #if (IOTW_DEBUG_LEVEL >1)\r\n"
	        + "         IOTW_PRINTLN(F(\"lightsleep 1.5 s\"));\r\n"
			+ "      #endif\r\n"
   			+ "      Serial.flush();\r\n"
   			+ "      esp_sleep_enable_timer_wakeup(1500ULL * 1000ULL); // ~1.5 s\r\n"
   			+ "      esp_light_sleep_start();\r\n"
   			+ "      while (swSerUS.available()) swSerUS.read();       // nach Sleep: RX-Puffer leeren\r\n"
   			+ "    }\r\n"
   			+ "#endif\r\n"
   			+ "\r\n"
    		+ "  #if (IOTW_DEBUG_LEVEL >1)\r\n"
	        + "    IOTW_PRINT(F(\" DFRobot, attempt=\"));\r\n"
		    + "    IOTW_PRINTLN((int)attempt);\r\n"
    		+ "  #endif\r\n"
		   
   			+ "    // 1) Start-Byte 0xFF suchen\r\n"
   			+ "    unsigned long start = millis();\r\n"
   			+ "    bool foundFF = false;\r\n"
   			+ "    while (millis() - start < TIMEOUT_START_MS) {\r\n"
   			+ "      if (swSerUS.available()) {\r\n"
   			+ "        if ((uint8_t)swSerUS.read() == 0xFF) { foundFF = true; break; }\r\n"
   			+ "      } else {\r\n"
   			+ "        yield();\r\n"
   			+ "      }\r\n"
   			+ "    }\r\n"
   			+ "    if (!foundFF) { \n"
   			+ "      #if (IOTW_DEBUG_LEVEL >1)\r\n"
   			+ "       IOTW_PRINTLN(F(\"no FF\"));\r\n"
    		+ "      #endif\r\n"
   			+ "      continue;\r\n"
   			+ "    }\n"
   			+ "\r\n"
   			+ "    // 2) Exakt 3 Bytes (High, Low, Checksumme) einlesen\r\n"
   			+ "    size_t n = 0;\r\n"
   			+ "    start = millis();\r\n"
   			+ "    while ((n < 3) && (millis() - start < TIMEOUT_FRAME_MS)) {\r\n"
   			+ "      if (swSerUS.available()) data[n++] = (uint8_t)swSerUS.read();\r\n"
   			+ "      else yield();\r\n"
   			+ "    }\r\n"
   			+ "    if (n < 3) {\n"
  			+ "      #if (IOTW_DEBUG_LEVEL >1)\r\n"
   			+ "       IOTW_PRINTLN(F(\"unvollständiger Frame\"));\r\n"
    		+ "      #endif\r\n"
   			+ "      continue; // unvollständiger Frame\r\n"
    		+ "    }\n"
   			+ "\r\n"
   			+ "    // 3) Checksumme prüfen\r\n"
   			+ "    const uint8_t hi   = data[0];\r\n"
   			+ "    const uint8_t lo   = data[1];\r\n"
   			+ "    const uint8_t sum  = data[2];\r\n"
   			+ "    const uint8_t calc = (uint8_t)((0xFF + hi + lo) & 0xFF);\r\n"
   			+ "    if (calc != sum) {\r\n"
   			+ "        // Debug (optional)\r\n"
    		+ "      #if (IOTW_DEBUG_LEVEL >1)\r\n"
   			+ "       IOTW_PRINT(F(\"Checksum Error, n=\"));\r\n"
   			+ "       IOTW_PRINT((int)n);\r\n"
   			+ "       IOTW_PRINT(\" bytes: \");\r\n"
   			+ "       IOTW_PRINT(hi, HEX); IOTW_PRINT(' ');\r\n"
   			+ "       IOTW_PRINT(lo, HEX); IOTW_PRINT(' ');\r\n"
   			+ "       IOTW_PRINTLN(sum, HEX);\r\n"
    		+ "      #endif\r\n"
   			+ "      continue; // Checksum-Fehler → nächster Versuch\r\n"
   			+ "    }\r\n"
   			+ "\r\n"
   			+ "    // 4) Distanz (mm)\r\n"
   			+ "    const long dist = ((long)hi << 8) | (long)lo;\r\n"
   			+ "\r\n"
   			+ "    // A01NYUB-Semantik:\r\n"
   			+ "    //   dist == 0  → \"kein Echo\"  → obere Grenze\r\n"
   			+ "    //   dist < lower → unterhalb Blindzone → untere Grenze\r\n"
   			+ "    //   dist > upper → oberhalb Messbereich → obere Grenze\r\n"
   			+ "    if (dist == 0) {\r\n"
   			+ "      lastBoundary = (float)upper;\r\n"
  			+ "      #if (IOTW_DEBUG_LEVEL >1)\r\n"
   			+ "       IOTW_PRINTLN(F(\"kein Echo\"));\r\n"
    		+ "      #endif\r\n"
   			+ "      continue;\r\n"
   			+ "    }\r\n"
   			+ "\r\n"
   			+ "    if (dist > lower && dist < upper) {\r\n"
   			+ "      return (float)dist;                 // gültiger In-Range-Wert\r\n"
   			+ "    } else if (dist <= lower) {\r\n"
   			+ "      lastBoundary = (float)lower;        // als untere Grenze merken\r\n"
   			+ "    } else { // dist >= upper\r\n"
   			+ "      lastBoundary = (float)upper;        // als obere Grenze merken\r\n"
   			+ "    }\r\n"
   			+ "    // weiter mit nächstem Attempt\r\n"
   			+ "  }\r\n"
   			+ "\r\n"
   			+ "  // 5) Kein In-Range-Wert → ggf. zuletzt gesehene Grenze zurück\r\n"
   			+ "  return lastBoundary; // kann NAN bleiben, falls nie 0/unterhalb/oberhalb gesehen\r\n"
   			+ "}";
    translator.addDefinitionCommand(read);		
	
	
    // Code von der Mainfunktion
	ret = "readDFRobotUS_"+rxpin+"(280,7500)";
    return codePrefix + ret + codeSuffix;
  }
}