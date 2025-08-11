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
    
    String read = "float readMaxbotUS_"+rxpin+"(long lower, long upper) {\r\n"
    		+ "  const int    MAX_TRIES  = 10;\r\n"
    		+ "  const auto   TIMEOUT_MS = 250UL;\r\n"
    		+ "\r\n"
    		+ "  if (!(upper > lower)) return NAN;\r\n"
    		+ "\r\n"
    		+ "  String payload; payload.reserve(8);\r\n"
    		+ "  float  lastBoundary = NAN;\r\n"
    		+ "\r\n"
    		+ "  for (int attempt = 0; attempt < MAX_TRIES; ++attempt) {\r\n"
    		+ "    unsigned long start = millis();\r\n"
    		+ "    bool foundR = false;\r\n"
    		+ "\r\n"
    		+ "    while (millis() - start < TIMEOUT_MS) {\r\n"
    		+ "      if (swSerMaxBot.available() && swSerMaxBot.read() == 'R') { foundR = true; break; }\r\n"
    		+ "      yield();\r\n"
    		+ "    }\r\n"
    		+ "    if (!foundR) continue;\r\n"
    		+ "\r\n"
    		+ "    start = millis();\r\n"
    		+ "    payload = \"\";\r\n"
    		+ "    while (millis() - start < TIMEOUT_MS) {\r\n"
    		+ "      if (swSerMaxBot.available()) {\r\n"
    		+ "        char c = swSerMaxBot.read();\r\n"
    		+ "        if (c == '\\r' || c == '\\n') break;\r\n"
    		+ "        payload += c;\r\n"
    		+ "      } else {\r\n"
    		+ "        yield();\r\n"
    		+ "      }\r\n"
    		+ "    }\r\n"
    		+ "\r\n"
    		+ "    if (payload.length() > 0 && isDigit(payload.charAt(0))) {\r\n"
    		+ "      long dist = payload.toInt();\r\n"
    		+ "\r\n"
    		+ "      if (dist > lower && dist < upper) return (float)dist;\r\n"
    		+ "      if (dist == lower) lastBoundary = (float)lower;\r\n"
    		+ "      else if (dist == upper) lastBoundary = (float)upper;\r\n"
    		+ "    }\r\n"
    		+ "  }\r\n"
    		+ "  return lastBoundary; // kann NaN bleiben, wenn keine Grenze gesehen\r\n"
    		+ "}";
    translator.addDefinitionCommand(read);		
	
	
    // Code von der Mainfunktion
	ret = "readMaxbotUS_"+rxpin+"("+bounds+")";
    return codePrefix + ret + codeSuffix;
  }
}