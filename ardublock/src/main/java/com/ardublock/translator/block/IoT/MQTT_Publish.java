package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class MQTT_Publish  extends TranslatorBlock {

	public MQTT_Publish (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("PubSubClient.h");
		//translator.addSetupCommand("Serial.begin(115200);");
		
		String topic,payload,retrained;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    topic = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    payload = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    retrained = translatorBlock.toCode();
        
	    String ret = "mqttreconnect(1);\r\n"
	    		+ "{\r\n"
	    		+ "  if (mqttclient.connected()) {\r\n"
	      		+ "   String pay=String("+payload+");\n"
	      	 	+ "   mqttclient.publish("+topic+",pay.c_str(),"+retrained+");\n"
	    		+ "   IOTW_PRINTLN(F(\"✅ mqtt publish\")); \r\n"
	    		+ "   #if (IOTW_DEBUG_LEVEL >1) \r\n"
	    		+ "      IOTW_PRINT(F(\"on topic: \")); \r\n"
	    		+ "      IOTW_PRINT(String("+topic+")+String(\" data: \"));\n"
	    		+ "      IOTW_PRINTLN(pay);\r\n"
	    		+ "   #endif\r\n"
	    		+ "  } \r\n"
	    		+ "};\r\n";
	    
        return codePrefix + ret + codeSuffix;
	 	}
}

