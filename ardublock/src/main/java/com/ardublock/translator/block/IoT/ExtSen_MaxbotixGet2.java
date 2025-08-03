package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_MaxbotixGet2 extends TranslatorBlock
{

  public ExtSen_MaxbotixGet2 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
    
    String read = "float readMaxbotUS_16() {\r\n"
    		+ "  const int    MAX_TRIES   = 4;\r\n"
    		+ "  const auto   TIMEOUT_MS  = 250UL;   // maximal 250 ms pro Versuch\r\n"
    		+ "  String       payload;\r\n"
    		+ "  int          attempt     = 0;\r\n"
    		+ "\r\n"
    		+ "  while (attempt < MAX_TRIES) {\r\n"
    		+ "    attempt++;\r\n"
    		+ "\r\n"
    		+ "    //Serial.println(\"attempt \");\r\n"
    		+ "    //Serial.println(attempt);\r\n"
    		+ "    unsigned long start = millis();\r\n"
    		+ "    bool foundR = false;\r\n"
    		+ "\r\n"
    		+ "    // 1) Auf 'R' warten, max. TIMEOUT_MS\r\n"
    		+ "    while (millis() - start < TIMEOUT_MS) {\r\n"
    		+ "      if (swSerMaxBot.available() && swSerMaxBot.read() == 'R') {\r\n"
    		+ "        foundR = true;\r\n"
    		+ "        break;\r\n"
    		+ "      }\r\n"
    		+ "    }\r\n"
    		+ "    if (!foundR) continue;  // nächster Versuch\r\n"
    		+ "\r\n"
    		+ "    // 2) Nun die restliche Zeile bis CR einlesen\r\n"
    		+ "    start = millis();\r\n"
    		+ "    payload = \"\";\r\n"
    		+ "    while (millis() - start < TIMEOUT_MS) {\r\n"
    		+ "      if (swSerMaxBot.available()) {\r\n"
    		+ "        char c = swSerMaxBot.read();\r\n"
    		+ "        if (c == '\\r' || c == '\\n') break;\r\n"
    		+ "        payload += c;\r\n"
    		+ "      }\r\n"
    		+ "    }\r\n"
    		+ "\r\n"
    		+ "    // 3) Prüfen, ob es ein valider Wert ist\r\n"
    		+ "    if (payload.length() > 0 && isDigit(payload.charAt(0))) {\r\n"
    		+ "      float dist = payload.toFloat();\r\n"
    		+ "      if (dist >= 300.0f && dist <= 10000.0f) {\r\n"
    		+ "        return dist;  // gültiger Messwert\r\n"
    		+ "      }\r\n"
    		+ "    }\r\n"
    		+ "    // sonst: ungültig oder zu klein → nächster Versuch\r\n"
    		+ "  }\r\n"
    		+ "\r\n"
    		+ "  // nach MAX_TRIES ohne Erfolg\r\n"
    		+ "  return NAN;\r\n"
    		+ "}";
    translator.addDefinitionCommand(read);		
	
	
    // Code von der Mainfunktion
	ret = "readMaxbotUS_"+rxpin+"()";
    return codePrefix + ret + codeSuffix;
  }
}