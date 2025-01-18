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
//				+ "  Serial.println(\"\\ncallback topic:\" + topic + \", payload:\" + MQTT_Rx_Payload);\n"
				+ "  for (int i=0;i<mqtt_sub_count;i++) { // durchsuche alle subscriptions, bis topic passt \n"
				+ "    if (topic==mqtt_sub[i].topic) \n"
				+ "     mqtt_sub[i].fun(pay,len);         // Aufruf der richtigen callback-Funktion\n"
				+ "  }\n"
				+"}\n";
					
		translator.addDefinitionCommand(def);		
		
		def = "const char* translateMqttError(int errorCode) {\n"
				+ "  switch (errorCode) {\n"
				+ "    case MQTT_CONNECTION_TIMEOUT:\n"
				+ "      return \"Connection timed out\";\n"
				+ "    case MQTT_CONNECTION_LOST:\n"
				+ "      return \"Connection lost\";\n"
				+ "    case MQTT_CONNECT_FAILED:\n"
				+ "      return \"Failed to connect\";\n"
				+ "    case MQTT_DISCONNECTED:\n"
				+ "      return \"Disconnected\";\n"
				+ "    case MQTT_CONNECTED:\n"
				+ "      return \"Connected\";\n"
				+ "    case MQTT_CONNECT_BAD_PROTOCOL:\n"
				+ "      return \"Bad protocol version\";\n"
				+ "    case MQTT_CONNECT_BAD_CLIENT_ID:\n"
				+ "      return \"Invalid client ID\";\n"
				+ "    case MQTT_CONNECT_UNAVAILABLE:\n"
				+ "      return \"Server unavailable\";\n"
				+ "    case MQTT_CONNECT_BAD_CREDENTIALS:\n"
				+ "      return \"Bad credentials\";\n"
				+ "    case MQTT_CONNECT_UNAUTHORIZED:\n"
				+ "      return \"Unauthorized access\";\n"
				+ "    default:\n"
				+ "      return \"Unknown error\";\n"
				+ "  }\n"
				+ "}\n"
				+ "";
		translator.addDefinitionCommand(def);
	    
	    
	     def =  "//------------ reconnect mqtt-client\n"
				+ "void mqttreconnect() { // Loop until we're reconnected \n"
	            + " if (!mqttclient.connected()) { \n"
				+ "  while (!mqttclient.connected()) { \n"
				+ "   Serial.print(\"Attempting MQTT connection...\");\n"
				+ "   if (mqttclient.connect("+ clientname +" , "+user+ ", "+pass+" )) {\n"
				+ "     Serial.println(\"connected\");\n"
				+ "     for (int i=0;i<mqtt_sub_count;i++) { // subscribe topic\n"
				+ "       mqttclient.subscribe(mqtt_sub[i].topic.c_str(),mqtt_sub[i].QoS);\n"
				+ "       Serial.print(\"subscribe on topic: \");Serial.println(mqtt_sub[i].topic);\n"
				+ "     }\n"
				+"    } else { \n"
				+"      Serial.print(\"failed: \");\n"
				+"      Serial.print(translateMqttError(mqttclient.state()));\n"
				+"      Serial.println(\" try again in 5 seconds\");\n"
				+"      delay(5000);\n"
				+"   }\n"
				+"  }\n"
				+" } else { \n"
				+"   mqttclient.loop(); \n"
				+" }\n"
                +"}\n";
			
			translator.addDefinitionCommand(def);
			
	    String setup = "//----------------------------------MQTT-Client \n"
	    		+ "mqttclient.setServer("+ broker+ ", 1883);\n"
	            + "mqttclient.setCallback(mqttcallback);\n";
	    
	    translator.addSetupCommand(setup);
		
         return codePrefix + "" + codeSuffix;
	 	}
}

