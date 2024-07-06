package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTIrqAttach  extends TranslatorBlock {

	public IoTIrqAttach (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		String ret;
		String pin;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    pin = translatorBlock.toCode();

		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    String mode = translatorBlock.toCode();
	    
	    int rand = (int)(Math.random() * 1000000);
	    
	    
	    String isr_name = "ISR_for_pin"+pin+"_"+Integer.toString(rand);
		ret = "void ICACHE_RAM_ATTR "+isr_name+"(){ // ---------- interrupt service \n";
		translatorBlock = getTranslatorBlockAtSocket(2);
		while (translatorBlock != null)
		{
			ret = ret + translatorBlock.toCode();
			translatorBlock = translatorBlock.nextTranslatorBlock();
		}
		ret = ret + "}\n\n";
		translator.addDefinitionCommand(ret);
		
		translator.addSetupCommand("Serial.begin(115200);");
	
        return codePrefix + "attachInterrupt(digitalPinToInterrupt("+pin+"),"+isr_name+","+mode+");" + codeSuffix;
	 	}
}

