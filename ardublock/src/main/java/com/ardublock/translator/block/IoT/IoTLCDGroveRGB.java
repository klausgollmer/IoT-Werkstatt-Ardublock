package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTLCDGroveRGB  extends TranslatorBlock {

	public IoTLCDGroveRGB (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		translator.addHeaderFile("Wire.h");
		translator.addHeaderFile("rgb_lcd.h");
		
		String Dis = "/*  Grove - Serial LCD RGB Backlight\n"
		+ "    2013 Copyright (c) Seeed Technology Inc.  All right reserved.\n"
		+ "    Author:Loovee 2013-9-18\n"
		+ "    This library is free software; you can redistribute it and/or\n"
		+ "    modify it under the terms of the GNU Lesser General Public\n"
		+ "    License as published by the Free Software Foundation; either\n"
		+ "    version 2.1 of the License, or (at your option) any later version.\n"
		+ "    This library is distributed in the hope that it will be useful,\n"
		+ "    but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
		+ "    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n"
		+ "    Lesser General Public License for more details.\n"
		+ "    You should have received a copy of the GNU Lesser General Public\n"
		+ "    License along with this library; if not, write to the Free Software\n"
		+ "    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA\n"
		+ "*/\n"
		+ "";
		translator.addDefinitionCommand(Dis);
		String Def="rgb_lcd lcd;\n";
		translator.addDefinitionCommand(Def);
	
		   // I2C-initialisieren
		translator.addSetupCommand("Serial.begin(115200);");
	   
		translator.addSetupCommand("Wire.begin(GPIO_I2C_SDA, GPIO_I2C_SCL); // ---- Initialisiere den I2C-Bus \n");
		translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
		 
           // Matrix initialisieren	    
	    String Setup = "lcd.begin(16, 2);// LCD Backlight initialisieren \n";
	    translator.addSetupCommand(Setup);
	
		
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String x = translatorBlock.toCode();
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String y = translatorBlock.toCode();
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
		String text = translatorBlock.toCode();

		//text += "1234567890123456";
		//text = text.substring(0,15);
		
		String ret  = "lcd.setCursor(" + x +"," + y +");\n"+
		              "lcd.print(String("+text+")+\"                \");\n";
		return codePrefix + ret + codeSuffix;
		
	}
}

