package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTMaxbotixGet2 extends TranslatorBlock
{

  public IoTMaxbotixGet2 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String rxpin = translatorBlock.toCode();

    // Header hinzuf�gen
    translator.addHeaderFile("SoftwareSerial.h");
    translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
    
    // Setupdeklaration
    // I2C-initialisieren
    String Setup = " swSerMaxBot.begin(9600,SWSERIAL_8N1); // Maxbotix ultrasonic \n";
    translator.addSetupCommand(Setup);
    translator.addSetupCommand(Setup);
    
  
    // Deklarationen hinzuf�gen
	translator.addDefinitionCommand("SoftwareSerial swSerMaxBot("+rxpin+", 100,true); // RXPin, TX not used, Library: https://github.com/plerup/espsoftwareserial/, Peter Lerup");   		   	
    
    String read = "float readMaxbotUS(){ // ----------------------- Maxbotix serial protocol\r\n" + 
    		"  float reading = NAN;\r\n" + 
    		"  int tout = 250;\r\n" + 
    		"  while (swSerMaxBot.available() > 0) swSerMaxBot.read(); // skip old data\r\n" + 
    		"  while ((tout > 0) && isnan(reading)) {\r\n" + 
    		"    if (swSerMaxBot.available()) {\r\n" + 
    		"      if (swSerMaxBot.read() == 'R') { // Start Message\r\n" + 
    		"        reading =swSerMaxBot.parseInt();\r\n" + 
    		"      }\r\n" + 
    		"    } \r\n" + 
    		"    else {\r\n" + 
    		"      tout--;\r\n" + 
    		"      delay(1);\r\n" + 
    		"    }\r\n" + 
    		"  }\r\n" + 
    		"  return reading;\r\n" + 
    		"}\r\n" + 
    		"";
    translator.addDefinitionCommand(read);		
	
	
    // Code von der Mainfunktion
	ret = "readMaxbotUS()";
    return codePrefix + ret + codeSuffix;
  }
}