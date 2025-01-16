package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTMPR121Read extends TranslatorBlock
{

  public IoTMPR121Read (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }


	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    String pin = translatorBlock.toCode();
		
		 // Header hinzuf�gen
	    translator.addHeaderFile("Adafruit_MPR121.h");
	    translator.addHeaderFile("Wire.h");

	    
		String Dis="/* Adafruit MPR121 Touch Sensor \n"
				 + "Copyright (c) Adafruit Industries\r\n"
				 + "Disclaimer see https://github.com/adafruit/Adafruit_MPR121 */\n";
	   	translator.addDefinitionCommand(Dis);
	    
	   	translator.addDefinitionCommand("// MPR121 Breakout written by Limor Fried/Ladyada for Adafruit Industries https://www.adafruit.com/products/\r\n");
	   	translator.addDefinitionCommand("Adafruit_MPR121 cap1 = Adafruit_MPR121();");

	    
	    // Setupdeklaration
	    // I2C-initialisieren
	    translator.addSetupCommand("Serial.begin(115200);");
	    
	    translator.addSetupCommand("Wire.begin(GPIO_I2C_SDA, GPIO_I2C_SCL); // ---- Initialisiere den I2C-Bus \n");
	    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
	 
	   	   
	    String Setup = "if (!cap1.begin(0x5B)) { Serial.println(\"Failed to communicate MPR121 Touch\");while (1) {delay(1);};}// 0x5A \n";
	    translator.addSetupCommand(Setup);
	    
	
		String ret = "((cap1.touched() & (1<<"+pin+")) > 0)";
		

		return codePrefix + ret + codeSuffix;
	}
}