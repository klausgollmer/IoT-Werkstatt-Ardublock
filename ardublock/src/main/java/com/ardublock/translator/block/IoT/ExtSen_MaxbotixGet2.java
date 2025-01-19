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
    
    String read = "float readMaxbotUS_"+rxpin+"(){ // ----------------------- Ultrasound Distance Measurement Maxbotix serial protocol\r\n"
    		+ "  float reading = NAN;\r\n"
    		+ "  int tout = 250;\r\n"
    		+ "  while (swSerMaxBot.available() > 0) {swSerMaxBot.read();} // skip old data\r\n"
    		+ "  \r\n"
    		+ "  while ((tout > 0) && isnan(reading)) {\r\n"
    		+ "    if (swSerMaxBot.available()) {\r\n"
    		+ "      if (swSerMaxBot.read() == 'R') { // Start Message\r\n"
    		+ "        reading =swSerMaxBot.parseInt();\r\n"
    		+ "        //Serial.print(reading);\r\n"
    		+ "      }\r\n"
    		+ "    } else {\r\n"
    		+ "      tout--;\r\n"
    		+ "      //Serial.print('.');\r\n"
    		+ "      delay(2);\r\n"
    		+ "    }\r\n"
    		+ "  }\r\n"
    		+ "  return reading;\r\n"
    		+ "}";
    translator.addDefinitionCommand(read);		
	
	
    // Code von der Mainfunktion
	ret = "readMaxbotUS_"+rxpin+"()";
    return codePrefix + ret + codeSuffix;
  }
}