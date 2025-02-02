package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class MQTT_Connect  extends TranslatorBlock {

	public MQTT_Connect (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("PubSubClient.h");
		//translator.addSetupCommand("Serial.begin(115200);");
		
		String broker,clientname,user="\"\"",pass="\"\"";
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    broker = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    clientname = translatorBlock.toCode();
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(2);
	    if (translatorBlock!=null) user = translatorBlock.toCode();
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(3);
	    if (translatorBlock!=null) pass = translatorBlock.toCode();

	    
	    
	    String Dis = "/* PubSubClient MQTT \n"
   	    		   + "Copyright (c) 2008-2020 Nicholas O'Leary\r\n"
	    		   + "MIT License, for Disclaimer see end of file */";
	    translator.addDefinitionCommand(Dis);
	 
	    String def;
	    if (translator.isGPRSProgram()) {
	    	def = "//-------------- definition mqtt-object ueber GSM\n"
		            + "TinyGsmClient 	espClient(modem); \n"
		            + "PubSubClient     mqttclient(espClient);\n";
		} else {
			def = "//-------------- definition mqtt-object ueber WiFi\n"
		            + "WiFiClient   espClient; \n"
		            + "PubSubClient mqttclient(espClient);\n";
		}
	    translator.addDefinitionCommand(def);
	    
	    def =   "//--------- list of mqtt callback functions \n"
				+	" typedef void (*mqtthandle) (byte*,unsigned int);\n"
	 		    +   " typedef struct {       // Typdeklaration Callback\n"
			    +   "   String topic;        // mqtt-topic\n"      
			    +   "   int    QoS;          // Quality of Service\n"      
			    +   "   mqtthandle fun;      // callback function \n"
			    +   " }subscribe_type; \n"
	            +   "subscribe_type mqtt_sub[IOTW_MAX_MQTT_SUB];\n"
			    +   "int mqtt_sub_count=0;\n"; 
	        
		translator.addDefinitionCommand(def);		
		translator.addDefinitionCommand("String MQTT_Rx_Payload = \"\" ;");

		
		def = "//--------- mqtt callback function \n"
				+ "void mqttcallback(char* to, byte* pay, unsigned int len) {\n"
				+ "  String topic   = String(to);\n"
				+ "  String payload = String((char*)pay);\n"
				+ "  MQTT_Rx_Payload=payload.substring(0,len);\n"
	       	    + "  #if (IOTW_DEBUG_LEVEL >1) \r\n"
				+ "      IOTW_PRINTLN(\"\\n📡 callback topic:\" + topic + \", payload:\" + MQTT_Rx_Payload);\n"
	       	    + "  #endif\n"
				+ "  for (int i=0;i<mqtt_sub_count;i++) { // durchsuche alle subscriptions, bis topic passt \n"
				+ "    if (topic==mqtt_sub[i].topic) \n"
				+ "     mqtt_sub[i].fun(pay,len);         // Aufruf der richtigen callback-Funktion\n"
				+ "  }\n"
				+"}\n";
					
		translator.addDefinitionCommand(def);		
		
		def = "const char* translateMqttError(int errorCode) {\r\n"
				+ "  switch (errorCode) {\r\n"
				+ "  case MQTT_CONNECTION_TIMEOUT:\r\n"
				+ "    return \"❌ Connection timed out\";\r\n"
				+ "  case MQTT_CONNECTION_LOST:\r\n"
				+ "    return \"❌ Connection lost\";\r\n"
				+ "  case MQTT_CONNECT_FAILED:\r\n"
				+ "    return \"❌ Failed to connect\";\r\n"
				+ "  case MQTT_DISCONNECTED:\r\n"
				+ "    return \"❌ Disconnected\";\r\n"
				+ "  case MQTT_CONNECTED:\r\n"
				+ "    return \"✅ Connected\";\r\n"
				+ "  case MQTT_CONNECT_BAD_PROTOCOL:\r\n"
				+ "    return \"❌ Bad protocol version\";\r\n"
				+ "  case MQTT_CONNECT_BAD_CLIENT_ID:\r\n"
				+ "    return \"❌ Invalid client ID\";\r\n"
				+ "  case MQTT_CONNECT_UNAVAILABLE:\r\n"
				+ "    return \"⚠ Server unavailable\";\r\n"
				+ "  case MQTT_CONNECT_BAD_CREDENTIALS:\r\n"
				+ "    return \"❌ Bad credentials\";\r\n"
				+ "  case MQTT_CONNECT_UNAUTHORIZED:\r\n"
				+ "    return \"❌ Unauthorized access\";\r\n"
				+ "  default:\r\n"
				+ "    return \"❌ Unknown error\";\r\n"
				+ "  }\r\n"
				+ "}\n";

		translator.addDefinitionCommand(def);
	    
		 def =  "//------------ reconnect mqtt-client\r\n"
	     		+ "void mqttreconnect(int maxAttempts) { \r\n"
	     		+ "  // 1) Prüfen, ob bereits verbunden\r\n"
	     		+ "  if (mqttclient.connected()) { \r\n"
	     		+ "    mqttclient.loop(); \r\n"
	     		+ "    return; \r\n"
	     		+ "  }\r\n"
	     		+ "  // 2) Debug-Ausgabe\r\n"
	     		+ "  int attempt = 0;\r\n"
	     		+ "  IOTW_PRINT(\"MQTT connecting ...\");\r\n"
	     		+ "\r\n"
	     		+ "\r\n"
	     		+ "  while (!mqttclient.connected() && attempt < maxAttempts) { \r\n"
	     		+ "    // 3) Verbindung versuchen\r\n"
	     		+ "    if (mqttclient.connect("+ clientname +" , "+user+ ", "+pass+" )) {\n"
	     		+ "      IOTW_PRINTLN(\"✅ connected!\");\r\n"
	     		+ "      // 4) Subscribe zu allen Topics\r\n"
	     		+ "      for (int i = 0; i < mqtt_sub_count; i++) {\r\n"
	     		+ "        mqttclient.subscribe(mqtt_sub[i].topic.c_str(), mqtt_sub[i].QoS);\r\n"
	     		+ "        IOTW_PRINT(\"📡 Subscribed to topic: \");\r\n"
	     		+ "        IOTW_PRINTLN(mqtt_sub[i].topic);\r\n"
	     		+ "      }\r\n"
	     		+ "      return;  // Erfolgreich verbunden -> Beende Funktion\r\n"
	     		+ "    } \r\n"
	     		+ "\r\n"
	     		+ "// 5) Verbindung fehlgeschlagen\r\n"
	     		+ "    delay(10);\r\n"
	     		+ "    attempt++;\r\n"
	     		+ " }\r\n"
	     		+ "\r\n"
	     		+ "  // 6) Falls alle Versuche scheitern\r\n"
	     		+ "  if (!mqttclient.connected()) {\r\n"
	     		+ "    IOTW_PRINTLN(translateMqttError(mqttclient.state()));\r\n"
	     		+ "  }\r\n"
	     		+ "}\n";
			
			translator.addDefinitionCommand(def);
			
	    String setup = "//----------------------------------MQTT-Client \n"
	    		+ "mqttclient.setServer("+ broker+ ", 1883);\n"
	            + "mqttclient.setCallback(mqttcallback);\n"
	    		;
	    
	    translator.addSetupCommand(setup);
		
         return codePrefix + "" + codeSuffix;
	 	}
}

