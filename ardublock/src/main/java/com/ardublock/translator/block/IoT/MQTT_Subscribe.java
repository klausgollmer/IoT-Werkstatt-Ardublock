package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class MQTT_Subscribe  extends TranslatorBlock {

	public MQTT_Subscribe (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
		translator.setMQTTProgram(true);
		
		String topic,QoS="0";
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    topic = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    QoS = translatorBlock.toCode();
	    
	    
	    
        String call_topic = topic.replace('.','_');
	    //translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    //payload = translatorBlock.toCode();
       
	    //translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    //fun = translatorBlock.toCode();
	    //String funtrim = fun.substring(1,fun.length()-1); // remove ""
	    
	    
//	    String myfun = "mqtt_callback_topic_"+topic.substring(1,topic.length()-1)+"_"+payload.substring(1,payload.length()-1);
	    String myfun = "mqtt_callback_topic_"+call_topic.substring(1,call_topic.length()-1);
	   
	    myfun=myfun.replaceAll("[/+ #$-@]", "_");

	    String ret;
		ret = "void "+myfun+"(byte* pay, unsigned int len){ // ---------- my callbackfunction mqtt\n";
		translatorBlock = getTranslatorBlockAtSocket(2);
		ret = ret + "  String payload = String((char*)pay); // payload als String interpretieren\n"
                  + "  MQTT_Rx_Payload=payload.substring(0,len);    // mit Länge von len Zeichen\n"
 			      + "  IOTW_PRINTLN(String(\"callback on topic: \")+ String("+topic+"));\n"; 	
				
		while (translatorBlock != null)
		{
			ret = ret + translatorBlock.toCode();
			translatorBlock = translatorBlock.nextTranslatorBlock();
		}
		ret = ret + "}\n\n";
		translator.addDefinitionCommand(ret);
		
		translator.addHeaderFile("PubSubClient.h");
		////translator.addSetupCommand("Serial.begin(115200);");
		//translator.addSetupCommand("mqttclient.setCallback(mqttcallback);\n");    	
		
		String setup = "//--------- prepare mqtt subscription \n"
				+ "mqtt_sub_count++; // add new element \n"
				+ "if (mqtt_sub_count < IOTW_MAX_MQTT_SUB) { \n"
                + "  mqtt_sub[mqtt_sub_count-1].topic = " + topic +";\n"
                + "  mqtt_sub[mqtt_sub_count-1].QoS = " + QoS +";\n"

             //   + "  mqtt_sub[mqtt_sub_count-1].payload = " + payload +";\n"
                + "  mqtt_sub[mqtt_sub_count-1].fun = "+myfun+"; //callback function\n"
		       
		        + "} else IOTW_PRINTLN(\" err max. mqtt subscription\");\n";
		translator.addSetupCommand(setup);		
	
        return codePrefix + "mqttreconnect();" + codeSuffix;
	 	}
}

