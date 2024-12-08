package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTDFRobotUSGet extends TranslatorBlock
{

  public IoTDFRobotUSGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
    String Setup = " swSerUS.begin(9600,SWSERIAL_8N1); // ultrasonic \n";
    translator.addSetupCommand(Setup);
    
    // Deklarationen hinzuf�gen
	translator.addDefinitionCommand("SoftwareSerial swSerUS("+rxpin+", -1); // RXPin, TX not used, Library: https://github.com/plerup/espsoftwareserial/, Peter Lerup");   		   	
    
    String read = "//------------------------------- Ultrasound Distance Measurement DFRobot, UART protocol \n"
    		+ "float readDFRobotUS(int maxLevel){\r\n"
    		+ "  unsigned char data[3]; // buffer\r\n"
    		+ "  float distance = NAN;\r\n"
    		+ "  int tout=100,checksum=0;\r\n"
    		+ "  //Serial.print(\"Start\");\r\n"
    		+ "  while (swSerUS.available() > 0) {swSerUS.read();}// skip old data\r\n"
    		+ "  //Serial.print(\" flush\");\r\n"
    		+ "  while ((tout > 0) && (isnan(distance))) {\r\n"
    		+ "    if (swSerUS.available()) { // new data\r\n"
    		+ "      if (swSerUS.read() == 0xFF) { // Start Message\r\n"
    		+ "        //Serial.println(\"Start:\");\r\n"
    		+ "        int len = swSerUS.readBytes(data,3); // message\r\n"
    		+ "        checksum = (0xFF + data[0] + data[1]) & 0x00FF;\r\n"
    		+ "        if ((len == 3) && (checksum == data[2])) {\r\n"
    		+ "          distance = (data[0] << 8) + data[1];\r\n"
    		+ "          if (distance < 280)  {\r\n"
    		+ "            if (distance == 0)    // upper level, no echo?\r\n"
    		+ "              distance = maxLevel;\r\n"
    		+ "            else distance = 280;  // lower level\r\n"
    		+ "          }\r\n"
    		+ "          //Serial.print(distance);\r\n"
    		+ "        } else {\r\n"
    		+ "           Serial.print(\"Checksum Error, data len=\");\r\n"
    		+ "           Serial.print(len);\r\n"
    		+ "           Serial.print(data[0],HEX);\r\n"
    		+ "           Serial.print(data[1],HEX);\r\n"
    		+ "           Serial.println(data[2],HEX);\r\n"
    		+ "        }\r\n"
    		+ "      }\r\n"
    		+ "    } else { // countdown\r\n"
    		+ "      tout--;\r\n"
    		+ "      //Serial.print('.');\r\n"
    		+ "      delay(2);\r\n"
    		+ "    }\r\n"
    		+ "  }\r\n"
    		+ "  return distance;\r\n"
    		+ "}";
    translator.addDefinitionCommand(read);		
	
	
    // Code von der Mainfunktion
	ret = "readDFRobotUS(7500)";
    return codePrefix + ret + codeSuffix;
  }
}