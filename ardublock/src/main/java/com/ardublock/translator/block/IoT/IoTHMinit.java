package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTHMinit  extends TranslatorBlock {

	public IoTHMinit (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		String type,ser;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    type = translatorBlock.toCode();
		//type = type.substring(1, type.length() - 1);	

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    ser = translatorBlock.toCode();
		ser = ser.substring(1, ser.length() - 1);	

		//translator.addSetupCommand("Serial.begin(115200);");
		String DTUDef = "#define HMxxx \"HM"+type+".h\"\n" + 
				"#define WR1_NAME \"HM-"+type+"\"\n" + 
				"#define WR1_MEASUREDEF hm"+type+"_measureDef\n" + 
				"#define WR1_MEASURECALC hm"+type+"_measureCalc\n" + 
				"#define WR1_FRAGMENTS hm"+type+"_fragmentLen\n"+
			    "#define WR1_SERIAL 0x"+ser+"ULL \n"+
				"#include <DTU.h>";
		translator.addDefinitionCommand(DTUDef);
		
	 
	    String ret = "initMyHM();";
        return codePrefix + ret + codeSuffix;
	 	}
}

