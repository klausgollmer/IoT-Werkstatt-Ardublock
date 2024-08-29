package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTMQTTPublish  extends TranslatorBlock {

	public IoTMQTTPublish (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("PubSubClient.h");
		translator.addSetupCommand("Serial.begin(115200);");
		
		String topic,payload;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    topic = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    payload = translatorBlock.toCode();
        
	    String ret = "mqttreconnect();\n   {String pay=String("+payload+");\n mqttclient.publish("+topic+",pay.c_str());\n"+
	                 "Serial.print(\"mqtt publish on topic: \"); Serial.print(String("+topic+")+String(\" data: \"));Serial.println(pay);};\n"; 
        return codePrefix + ret + codeSuffix;
	 	}
}

