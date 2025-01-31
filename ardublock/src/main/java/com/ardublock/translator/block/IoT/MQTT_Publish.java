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
        
	    String ret = "mqttreconnect();\n   {String pay=String("+payload+");\n mqttclient.publish("+topic+",pay.c_str(),"+retrained+");\n"+
	                 "IOTW_PRINT(\"mqtt publish on topic: \"); IOTW_PRINT(String("+topic+")+String(\" data: \"));IOTW_PRINTLN(pay);};\n"; 
        return codePrefix + ret + codeSuffix;
	 	}
}

