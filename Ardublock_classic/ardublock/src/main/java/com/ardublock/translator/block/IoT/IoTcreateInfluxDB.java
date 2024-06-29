package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTcreateInfluxDB  extends TranslatorBlock {

	public IoTcreateInfluxDB (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("InfluxDb.h");
		translator.addSetupCommand("Serial.begin(115200);");
		
		String host, dbname;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    dbname = translatorBlock.toCode();
	
		translator.addDefinitionCommand("Influxdb influx("+host+"); //https://github.com/tobiasschuerg/InfluxDB-Client-for-Arduino");
	    translator.addSetupCommand("influx.setDb("+dbname+");");

	    
	    return codePrefix + codeSuffix;
	 	}
}

