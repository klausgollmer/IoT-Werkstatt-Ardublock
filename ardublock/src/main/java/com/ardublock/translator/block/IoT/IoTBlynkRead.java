package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTBlynkRead  extends TranslatorBlock {

	public IoTBlynkRead (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
		translator.setBlynkProgram(true);
		
		String pin;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    pin = translatorBlock.toCode();

	    String CallBack;
	    CallBack="";
		translatorBlock = getTranslatorBlockAtSocket(1);
		while (translatorBlock != null)
		{
			CallBack = CallBack + translatorBlock.toCode();
			translatorBlock = translatorBlock.nextTranslatorBlock();
		}

	    
    
	    String ReadCall ="//--------------------------------------- Blynk-Callback Read VirtualPin \n"
				+ "BLYNK_WRITE(V"+pin+") {\n"
				+ CallBack 
				+ "}\n\n";
	    
		        
		translator.addDefinitionCommand(ReadCall);
		
	
        return codePrefix + "Blynk.run();// Blynk Housekeeping\n" + codeSuffix;
	 	}
}

