package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class BLE_sendPhyphox  extends TranslatorBlock {

	public BLE_sendPhyphox (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP32)\n #include <phyphoxBle.h> \n#endif\n");		
//		translator.addDefinitionCommand(httpPOST);
		String Server, wert1="", wert2="", wert3="",setup, ret;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    Server = translatorBlock.toCode();

        translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    wert1 = translatorBlock.toCode();

	    String Var = "float phyphoxWert1="+wert1+";\n";
	    String List = "phyphoxWert1";
		
	    translatorBlock = this.getTranslatorBlockAtSocket(2);
	    if (translatorBlock!=null) {
	       wert2 = translatorBlock.toCode();
	       Var  += "float phyphoxWert2="+wert2+";\n";
	       List += ",phyphoxWert2";
	    }

	    translatorBlock = this.getTranslatorBlockAtSocket(3);
	    if (translatorBlock!=null) {
	   	   wert3 = translatorBlock.toCode();
	       Var  += "float phyphoxWert3="+wert3+";\n";
	       List += ",phyphoxWert3";
	    }

	    setup = "  PhyphoxBLE::start("+Server+");\n";
        translator.addSetupCommand(setup);
        
        
        
        ret = "{ // Phyphox send \n"
        		+ Var +         		
                "PhyphoxBLE::write("+List+");\n"
        		+ "}";
        
        return codePrefix + ret + codeSuffix;
	 	}
}

