package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTNOWShowMAC extends TranslatorBlock
{

  public IoTNOWShowMAC (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Deklarationen hinzuf�gen
    // translator.addDefinitionCommand("ADC_MODE(ADC_VCC); // analogInput is uses for VCC measure");   		   	
    
   // Code von der Mainfunktion
    translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
    translator.setWiFiProgram(true);
    
   // TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
   // String cpu = translatorBlock.toCode();

    
    String Code = "// ESP-Now, Einführung unter https://wolles-elektronikkiste.de/esp-now\r\n"
    		+ "String NOWShowMAC(){\r\n"
    		+ "    WiFi.mode(WIFI_STA);\r\n"
    		+ "    #if defined(ESP32)\r\n"
    		+ "      while (!(WiFi.STA.started())) { // comment the while loop for ESP8266\r\n"
    		+ "         delay(10);\r\n"
    		+ "      }\r\n"
    		+ "    #else\r\n"
    		+ "      delay(1000); // uncomment for ESP8266\r\n"
    		+ "    #endif\r\n"
    		+ "    String mac = WiFi.macAddress();\r\n"
    		+ "    return mac;\r\n"
    		+ "}";
    translator.addDefinitionCommand(Code);
	ret = "NOWShowMAC()"; 
    return codePrefix + ret + codeSuffix;
  }
}