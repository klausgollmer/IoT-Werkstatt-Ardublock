package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTTimerAttach  extends TranslatorBlock {

	public IoTTimerAttach (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		String ret;
		String timer;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    timer = translatorBlock.toCode();

		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    String ms = translatorBlock.toCode();
	    
	    int rand = (int)(Math.random() * 1000000);
	    
	    
	    String isr_name = "ISR_for_mytimer"+timer+"_"+Integer.toString(rand);
	    String timer_name = "mytimer"+timer;

	    ret = "void ICACHE_RAM_ATTR "+isr_name+"(){ // ---------- interrupt service timer\n";
		translatorBlock = getTranslatorBlockAtSocket(2);
		while (translatorBlock != null)
		{
			ret = ret + translatorBlock.toCode();
			translatorBlock = translatorBlock.nextTranslatorBlock();
		}
		ret = ret + "}\n\n";
		
		translator.addHeaderFile("Ticker.h");
		translator.addDefinitionCommand(ret);
		
		translator.addDefinitionCommand("Ticker "+ timer_name + ";  // ---- my timer \n");
		
		
		//translator.addSetupCommand("Serial.begin(115200);");
	
        return codePrefix + timer_name+".attach_ms("+ms+","+isr_name+");" + codeSuffix;
	 	}
}

