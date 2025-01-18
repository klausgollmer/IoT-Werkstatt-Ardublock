package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ESPNOW_Receive extends TranslatorBlock
{

  public ESPNOW_Receive (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
    translator.addHeaderFile("#if defined(ESP8266)\n #include <espnow.h> \n#elif defined(ESP32) \n #include <esp_now.h>\n#endif\n");		
    translator.setWiFiProgram(true);
    
    TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0);
    String Callback="";
    while (translatorBlock != null)
	{
		Callback = Callback + translatorBlock.toCode();
		translatorBlock = translatorBlock.nextTranslatorBlock();
	}
    
  
    String CallbackCode = "// ESP-Now, Einführung unter https://wolles-elektronikkiste.de/esp-now\r\n"
    		+"// ------------ ESP-NOW Payloads \n"
    		+ "String ESPNOW_Payload_String =\"\";\r\n";
    translator.addDefinitionCommand(CallbackCode);
  
    
    CallbackCode = "// ------------ ESP-NOW Callback\n"
    		+ "#if defined(ESP32)\n"
    		+ "void NOWmessageReceived(const esp_now_recv_info *info, const uint8_t* incomingData, int len){\r\n"
    		+ "#else\n"
    		+ "void NOWmessageReceived(uint8_t *macAddr, uint8_t *incomingData, uint8_t len) { \n"
    		+ "#endif\n"
    		+ "    ESPNOW_Payload_String =\"\";\r\n"
    		+ "    for(int i=0; i<len; i++){\r\n"
    		+ "        ESPNOW_Payload_String +=String((char)incomingData[i]);\r\n"
    		+ "    }\r\n"
    		+ "    Serial.println(ESPNOW_Payload_String);\r\n"
    		+ Callback + "\n"
    		+ "}";
    translator.addDefinitionCommand(CallbackCode);
    
    String Setup = "// ESP-NOW initialisieren\r\n"
    		+ "WiFi.mode(WIFI_STA);\r\n"
    		+ "if (esp_now_init() != 0) {\r\n"
    		+ "    Serial.println(\"Fehler beim Initialisieren von ESP-NOW\");\r\n"
    		+ "    return;\r\n"
    		+ "}\n";
    translator.addSetupCommand(Setup);
    Setup = "#if defined(ESP8266) \n esp_now_set_self_role(ESP_NOW_ROLE_SLAVE); // Gerät als Empfänger konfigurieren\n #endif\n"
    		+ "esp_now_register_recv_cb(NOWmessageReceived);\n"
    		+ "Serial.print(\"ESP-Now callback is waiting for data\");\n";
    translator.addSetupCommand(Setup);
    
    String ret = "";
    return codePrefix + ret + codeSuffix;
  }
}