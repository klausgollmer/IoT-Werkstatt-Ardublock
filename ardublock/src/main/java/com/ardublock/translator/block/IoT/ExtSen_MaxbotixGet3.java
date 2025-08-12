 package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_MaxbotixGet3 extends TranslatorBlock
{

  public ExtSen_MaxbotixGet3 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
    
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
    String bounds = translatorBlock.toCode();
    
    
   // System.out.println("rx:" + rxpin+"---");
    
	//System.out.println("UART:" + UART+"---");
    
	//System.out.println("flagUART: " + use_uart);

    
    
    String Setup,Def; 
    if (use_uart) {
       translator.addHeaderFile("HardwareSerial.h");
       translator.addHeaderFile("#if defined(ESP32)\n #include <driver/uart.h> \n #endif\n");

       Setup ="#if defined(ESP32)\n"
       		+ " swSerMaxBot.begin(9600, SERIAL_8N1,"+rxpin+", -1); // UART kein TX\r\n"
       		+ " uart_set_line_inverse(UART_NUM_"+UART+", UART_SIGNAL_RXD_INV); //RX-Pegelinvertierung aktivieren\n"
       		+ "#else \n"
            + " swSerMaxBot.begin(9600,SWSERIAL_8N1); // Maxbotix ultrasonic \n"
       	    + "#endif \n";   
       Def =   "#if defined(ESP32) \n"
       		+  " HardwareSerial swSerMaxBot("+UART+"); // Hardware UART \n"
       		+ "#else \n"
            + " SoftwareSerial swSerMaxBot("+rxpin+", -1,true); // RXPin, TX not used, inverse logic, Library: https://github.com/plerup/espsoftwareserial/, Peter Lerup\n"   		   	
       	    + "#endif \n";   
       
    } else {
       translator.addHeaderFile("SoftwareSerial.h");
       Setup = "swSerMaxBot.begin(9600,SWSERIAL_8N1); // Maxbotix ultrasonic \n";
       Def =   "SoftwareSerial swSerMaxBot("+rxpin+", -1,true); // RXPin, TX not used, inverse logic, Library: https://github.com/plerup/espsoftwareserial/, Peter Lerup\n";   		   	
    }
    	
    translator.addSetupCommand(Setup);
	translator.addDefinitionCommand(Def);   		   	
    
    String read = "// ----------------------- Maxbotix Driver for 7368/69 \n "
    		+ "float readMaxbotUS_"+rxpin+"(long lower, long upper) {\r\n"
    		+ "  const int      MAX_TRIES        = 10;\r\n"
    		+ "  const uint32_t TIMEOUT_R_MS     = 250; // Suche nach 'R'\r\n"
    		+ "  const uint32_t TIMEOUT_LINE_MS  = 50;  // nach 'R' für die Ziffern\r\n"
    		+ "\r\n"
    		+ "  if (!(upper > lower)) return NAN;\r\n"
    		+ "\r\n"
    		+ "  String payload;\r\n"
    		+ "  payload.reserve(8);\r\n"
    		+ "  float lastBoundary = NAN;\r\n"
    		+ "\r\n"
    		+ "  // alten Kram verwerfen\r\n"
    		+ "  while (swSerMaxBot.available()) swSerMaxBot.read();\r\n"
    		+ "\r\n"
    		+ "  for (int attempt = 0; attempt < MAX_TRIES; ++attempt) {\r\n"
    		+ "  #ifdef ESP32\r\n"
   			+ "    if ((attempt == MAX_TRIES - 2) && (lastBoundary == (float) upper)) {\r\n"
	 		+ "      #if (IOTW_DEBUG_LEVEL >1)\r\n"
	        + "         IOTW_PRINTLN(F(\"lightsleep 1.5 s\"));\r\n"
			+ "      #endif\r\n"
   			+ "      Serial.flush();\r\n"
    		+ "      esp_sleep_enable_timer_wakeup(1500ULL * 1000ULL);\r\n"
    		+ "      esp_light_sleep_start();\r\n"
    		+ "      while (swSerMaxBot.available()) swSerMaxBot.read(); // nach Sleep flushen\r\n"
    		+ "    }\r\n"
    		+ "  #endif\r\n"
    		+ "  #if (IOTW_DEBUG_LEVEL >1)\r\n"
	        + "    IOTW_PRINT(F(\"Maxbotix attempt=\"));\r\n"
		    + "    IOTW_PRINTLN((int)attempt);\r\n"
			+ "  #endif\r\n"
			
		    + "    unsigned long start = millis();\r\n"
    		+ "    bool foundR = false;\r\n"
    		+ "\r\n"
    		+ "    // 1) Auf 'R' warten\r\n"
    		+ "    while (millis() - start < TIMEOUT_R_MS) {\r\n"
    		+ "      if (swSerMaxBot.available() && swSerMaxBot.read() == 'R') { \r\n"
    		+ "        foundR = true; \r\n"
    		+ "        break; \r\n"
    		+ "      }\r\n"
    		+ "      yield();\r\n"
    		+ "    }\r\n"
    		+ "    if (!foundR) {"
    		+ "      #if (IOTW_DEBUG_LEVEL >1)\r\n"
	        + "        IOTW_PRINTLN(F(\"found no R\"));\r\n"
		 	+ "      #endif\r\n"
    		+ "      continue;\r\n"
    		+ "    }\r\n"
    		+ "    // 2) Zeile bis CR/LF lesen\r\n"
    		+ "    bool gotCR = false;\r\n"
    		+ "    start = millis();\r\n"
    		+ "    payload = \"\";\r\n"
    		+ "    while (millis() - start < TIMEOUT_LINE_MS) {\r\n"
    		+ "      if (swSerMaxBot.available()) {\r\n"
    		+ "        char c = swSerMaxBot.read();\r\n"
    		+ "        if (c == '\\r' || c == '\\n') { gotCR = true; break; }\r\n"
    		+ "        payload += c;\r\n"
    		+ "      } else {\r\n"
    		+ "        yield();\r\n"
    		+ "      }\r\n"
    		+ "    }\r\n"
    		+ "     #if (IOTW_DEBUG_LEVEL >1)\r\n"
	        + "       IOTW_PRINT(F(\"payload=\"));\r\n"
		    + "       IOTW_PRINTLN(payload);\r\n"
    		+ "     #endif\r\n"
    		+ "    if (!gotCR || payload.length() == 0) { "
    		+ "     #if (IOTW_DEBUG_LEVEL >1)\r\n"
	        + "       IOTW_PRINTLN(F(\"no CR, payload=\"));\r\n"
    		+ "     #endif\r\n"
    		+ "     continue;\r\n"
    		+ "    }\r\n"
    		+ "    // 3) Nur Ziffern zulassen\r\n"
    		+ "    bool allDigits = true;\r\n"
    		+ "    for (size_t i = 0; i < payload.length(); ++i) {\r\n"
    		+ "      if (!isDigit(payload[i])) { allDigits = false; break; }\r\n"
    		+ "    }\r\n"
    		+ "    if (!allDigits) continue;\r\n"
    		+ "\r\n"
    		+ "    long dist = payload.toInt();\r\n"
    		+ "\r\n"
    		+ "    if (dist > lower && dist < upper) return (float)dist; // gültig\r\n"
    		+ "    if (dist == lower)      lastBoundary = (float)lower;   // Grenze merken\r\n"
    		+ "    else if (dist == upper) lastBoundary = (float)upper;\r\n"
    		+ "  }\r\n"
    		+ "\r\n"
    		+ "  return lastBoundary; // kann NaN bleiben, wenn keine Grenze gesehen\r\n"
    		+ "}\r\n"
    		+ "";
    translator.addDefinitionCommand(read);		
	
	
    // Code von der Mainfunktion
	ret = "readMaxbotUS_"+rxpin+"("+bounds+")";
    return codePrefix + ret + codeSuffix;
  }
}