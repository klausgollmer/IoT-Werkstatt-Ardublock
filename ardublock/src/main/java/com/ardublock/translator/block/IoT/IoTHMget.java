package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTHMget  extends TranslatorBlock {

	public IoTHMget (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
	
		//translator.addSetupCommand("Serial.begin(115200);");
				
			
		String index;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    index = translatorBlock.toCode();
	    
	    String all;
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    all = translatorBlock.toCode();
	    
	    String DTUGET ="float readMyHM(int i, int mitprint) { "
				+ " // https://github.com/hm-soft/Hoymiles-DTU-Simulation \n" +
				"   // hm-soft,  GNU GENERAL PUBLIC LICENSE\n "+
				"   uint8_t wr = aktWR;\n" + 
				"   float val = NAN;\n" + 
				"   checkRF24isWorking();\n" + 
                "   if (i == 999)\n" + 
                "    val = (float)(millis()-tickLastMessage);"+
				"   if (i <= inverters[wr].anzTotalMeasures) {\n" + 
				"    isTime2Send(); // check update or use actual values\n" + 
				"    val = getMeasureValue(wr,i);\n" +
				"    if (mitprint) myHMDisplay(mitprint);\n"+
				"   }\n" + 
				"   return val;\n"+
				"} ";
		translator.addDefinitionCommand(DTUGET);

	    
	    
	    
	    String ret = "readMyHM("+index+","+all+")";
        return codePrefix + ret + codeSuffix;
	 	}
}

