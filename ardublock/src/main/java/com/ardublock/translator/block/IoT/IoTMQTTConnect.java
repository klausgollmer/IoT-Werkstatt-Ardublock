package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTMQTTConnect  extends TranslatorBlock {

	public IoTMQTTConnect (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("PubSubClient.h");
		translator.addSetupCommand("Serial.begin(115200);");
		
		String broker,clientname,user,pass;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    broker = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    clientname = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    user = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    pass = translatorBlock.toCode();
    
	 
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
				+   "#define MAX_MQTT_SUB 10 // maximal 10 subscriptions erlaubt\n "
				+	" typedef void (*mqtthandle) (byte*,unsigned int);\n"
	 		    +   " typedef struct {       // Typdeklaration Callback\n"
			    +   "   String topic;        // mqtt-topic\n"      
			    +   "   mqtthandle fun;      // callback function \n"
			    +   " }subscribe_type; \n"
	            +   "subscribe_type mqtt_sub[MAX_MQTT_SUB];\n"
			    +   "int mqtt_sub_count=0;\n"; 
	        
		translator.addDefinitionCommand(def);		
		translator.addDefinitionCommand("String MQTT_Rx_Payload = \"\" ;");

		
		def = "//--------- mqtt callback function \n"
				+ "void mqttcallback(char* to, byte* pay, unsigned int len) {\n"
				+ "  String topic   = String(to);\n"
				+ "  String payload = String((char*)pay);\n"
				+ "  MQTT_Rx_Payload=payload.substring(0,len);\n"
				+ "  Serial.println(\"\\ncallback topic:\" + topic + \", payload:\" + MQTT_Rx_Payload);\n"
				+ "  for (int i=0;i<mqtt_sub_count;i++) { // durchsuche alle subscriptions, bis topic passt \n"
				+ "    if (topic==mqtt_sub[i].topic) \n"
				+ "     mqtt_sub[i].fun(pay,len);         // Aufruf der richtigen callback-Funktion\n"
				+ "  }\n"
				+"}\n";
					
		translator.addDefinitionCommand(def);		
			     
	    
	    
	     def =  "//------------ reconnect mqtt-client\n"
				+ "void mqttreconnect() { // Loop until we're reconnected \n"
	            + " if (!mqttclient.connected()) { \n"
				+ "  while (!mqttclient.connected()) { \n"
				+ "   Serial.print(\"Attempting MQTT connection...\");\n"
				+ "   if (mqttclient.connect("+ clientname +" , "+user+ ", "+pass+" )) {\n"
				+ "     Serial.println(\"connected\");\n"
				+ "     for (int i=0;i<mqtt_sub_count;i++) { // subscribe topic\n"
				+ "       mqttclient.subscribe(mqtt_sub[i].topic.c_str());\n"
				+ "       Serial.println(\"\\nsubscribe\");Serial.print(mqtt_sub[i].topic);\n"
				+ "     }\n"
				+"    } else { \n"
				+"      Serial.print(\"failed, rc=\");\n"
				+"      Serial.print(mqttclient.state());\n"
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

