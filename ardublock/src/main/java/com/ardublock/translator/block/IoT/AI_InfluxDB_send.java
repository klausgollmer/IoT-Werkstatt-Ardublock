package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class AI_InfluxDB_send  extends TranslatorBlock {

	public AI_InfluxDB_send (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("InfluxDb.h");
		//translator.addSetupCommand("Serial.begin(115200);");
		
		String device, sensorname,unit,val;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    device = translatorBlock.toCode();
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    sensorname = translatorBlock.toCode();
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    unit = translatorBlock.toCode();
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    val = translatorBlock.toCode();

	    String cmd="{//--------------------------------------- Influx-send\n" +
	    		"  InfluxData row(\"data\"); // erstellt eine Zeile der Tabelle \"data\"\n" + 
	    		"  row.addTag(\"Device\", "+device+");\n" + 
	    		"  row.addTag(\"Sensor\", "+sensorname+");\n" + 
	    		"  row.addTag(\"Unit\", "+unit+");\n" + 
	    		"  row.addValue(\"Value\", "+val+");\n" + 
	    		"  influx.write(row);}\n" + 
	    		"  IOTW_PRINTLN(\"influx data send\");\n"; 
	    String ret = cmd;
        return codePrefix + ret + codeSuffix;
	 	}
}

