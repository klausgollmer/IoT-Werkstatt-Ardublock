package com.ardublock.translator.block.IoT;

import java.util.ResourceBundle;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.VariableDigitalBlock;
import com.ardublock.translator.block.VariablePolyBlock;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class System32_SleepBlockLight32  extends TranslatorBlock {
	private static ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	public System32_SleepBlockLight32 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
      TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
  	String Delay_ms = translatorBlock.toCode();
  	//translator.setWiFiProgram(true);

  	String ret = "//------- Light SLEEP ---------------------------- \n"
  		+ "Serial.flush();"
          + "esp_sleep_enable_timer_wakeup("+Delay_ms+" * 1000ULL);\n"
          + "esp_light_sleep_start();";
      	return ret;
 	}
}

