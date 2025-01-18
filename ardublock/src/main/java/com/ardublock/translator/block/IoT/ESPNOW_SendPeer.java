package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ESPNOW_SendPeer extends TranslatorBlock
{

  public ESPNOW_SendPeer (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
    translator.addHeaderFile("#if defined(ESP8266)\n #include <espnow.h> \n#elif defined(ESP32) \n #include <esp_now.h>\n#endif\n");		
    translator.setWiFiProgram(true);
    
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
 	String MAC = translatorBlock.toCode();
 	MAC = MAC.replace("\"", ""); // lösche alle Vorkommen von "
    MAC = "0x" + MAC.replace(":", ",0x");
 	
 	translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
  	String Message = translatorBlock.toCode();
 	
 	translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
 	String chan = translatorBlock.toCode();
	
    // individueller Variablenname
    String ext = MAC.replace("0x", ""); // lösche alle Vorkommen von ',0x'
    ext = ext.replace(" ", ""); //  
    ext = ext.replace(",", ""); //  
    String MACname = "peerMAC_"+ext;
    translator.addDefinitionCommand("uint8_t "+MACname+"[] = {"+MAC+"};\n");
    
    //translator.addSetupCommand("Serial.begin(115200);");
    
    String Setup = "// ESP-NOW initialisieren\r\n"
    		+ "WiFi.mode(WIFI_STA);\r\n"
    		+ "if (esp_now_init() != 0) {\r\n"
    		+ "    Serial.println(\"Fehler beim Initialisieren von ESP-NOW\");\r\n"
    		+ "    return;\r\n"
    		+ "}\n";
    translator.addSetupCommand(Setup);
    Setup = "#if defined(ESP8266) \n esp_now_set_self_role(ESP_NOW_ROLE_CONTROLLER); // Gerät als Empfänger konfigurieren\n #endif\n";
    translator.addSetupCommand(Setup);
    
    String Code = ""
    		+ "#if defined(ESP8266)\n"
    		+ "if (esp_now_add_peer("+MACname+",ESP_NOW_ROLE_SLAVE,"+chan+", NULL, 0) != 0) {\r\n"
    		+ "    Serial.println(\"Fehler beim Hinzufügen des Peers\");\r\n"
    		+ "    return;"
    		+ "}"
    		+ "#endif";
    translator.addSetupCommand(Code);
    
    Code  = ""
       		+ "#if defined(ESP32)\n"
       	    + "  esp_now_peer_info_t peerInfo_"+ext+" = {};\r\n"
    		+ "  memcpy(peerInfo_"+ext+".peer_addr, "+MACname+", 6);\r\n"
    		+ "  peerInfo_"+ext+".channel = "+chan+"; // Kanal\r\n"
    		+ "  peerInfo_"+ext+".encrypt = false;\r\n"
    		+ "\r\n"
    		+ "  if (esp_now_add_peer(&peerInfo_"+ext+") != ESP_OK) {\r\n"
    		+ "    Serial.println(\"Fehler beim Hinzufügen des Peers\");\r\n"
    		+ "    return;\r\n"
    		+ "  }"
    		+ "#endif";
    translator.addSetupCommand(Code);
    
    String Send="// Nachricht an Peer senden\r\n"    	
    		+ "  String txt_"+ext+"="+Message+";\n"
    		+ "  uint8_t data_"+ext+"[txt_"+ext+".length() + 1];\r\n"
    		+ "  txt_"+ext+".getBytes(data_"+ext+", txt_"+ext+".length() + 1);"
    		+ "  if (esp_now_send("+MACname+", data_"+ext+", sizeof(data_"+ext+")) != 0) {\r\n"
    		+ "    Serial.println(\"Fehler beim Senden der Nachricht an Peer \");\r\n"
    		+ "  }";
    
    return codePrefix + Send + codeSuffix;
  }
}