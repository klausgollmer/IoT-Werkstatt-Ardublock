package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class System_I2C_Scan  extends TranslatorBlock {

	public System_I2C_Scan (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		String def = "// I2C Adress Scan r\n"
	    		+ "void I2CScan() {\r\n"
	    		+ "  byte err, address;\r\n"
	    		+ "  Serial.println(F(\"Scanning I2C devices ...\"));\r\n"
	    		+ "  for(address = 1; address < 127; address++ )\r\n"
	    		+ "  { Wire.beginTransmission(address);\r\n"
	    		+ "    err = Wire.endTransmission();\r\n"
	    		+ "\r\n"
	    		+ "    if (err == 0) {\r\n"
	    		+ "      Serial.print(F(\"\\n0x\"));\r\n"
	    		+ "      if (address<16) Serial.print(F(\"0\"));\r\n"
	    		+ "      Serial.print(address,HEX);\r\n"
	    		+ "      switch (address) {\r\n"
	    		+ "       case 0x76: Serial.print(F(\": build in Bosch BME680 Environmental Sensor\")); break;\r\n"
	    		+ "       case 0x23: Serial.print(F(\": external, probably BH1750 Light Sensor?\"));break;\r\n"
	    		+ "       case 0x3C: Serial.print(F(\": build in OLED Display\"));break;\r\n"
	    		+ "       case 0x52: Serial.print(F(\": build in APDS9999 RGB Sensor\"));break;\r\n"
	    		+ "       case 0x39: Serial.print(F(\": external, probably APDS9960 RGB Sensor, or TSL2561 Light Sensor?\"));break;\r\n"
	    		+ "      }  \r\n"
	    		+ "    }\r\n"
	    		+ "    else if (err==4)\r\n"
	    		+ "    {\r\n"
	    		+ "      Serial.print(F(\"error 0x\"));\r\n"
	    		+ "      if (address<16) Serial.print(F(\"0\"));\r\n"
	    		+ "      Serial.println(address,HEX);\r\n"
	    		+ "    }\r\n"
	    		+ "  }\r\n"
	    		+ "  Serial.println(F(\"\\ndone\"));\r\n"
	    		+ "}";
		translator.addDefinitionCommand(def);
		
        return codePrefix + "I2CScan();"+ codeSuffix;
	 	}
}

