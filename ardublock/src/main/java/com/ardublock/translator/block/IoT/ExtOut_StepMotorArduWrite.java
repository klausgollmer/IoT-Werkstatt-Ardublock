package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtOut_StepMotorArduWrite extends TranslatorBlock
{

  public ExtOut_StepMotorArduWrite (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }


	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    String puls = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    String dir = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    String steps = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    String speed = translatorBlock.toCode();

		 // Header hinzuf�gen
	   
	   	String set = "// Control Feather Motor Shield\n"+
	    "void setArduMotorSteps(int gpio_puls, int gpio_dir, int steps, int micro) {\n"+
	    "   int dir=0; 	\n"+
	    "   pinMode(gpio_puls,OUTPUT);\n"+
	    "   pinMode(gpio_dir,OUTPUT);\n"+
	    "	if (steps > 0)\n"+
	    "		dir = 1;\n"+
	    "	else {\n"+
	    "		dir = 0;\n"+
	    "		steps = - steps;\n"+
	    "	}\n"+
	    "   digitalWrite(gpio_dir,dir);\n" +
	    "   for(int Index = 0; Index < steps; Index++) {\r\n" + 
	    "       digitalWrite(gpio_puls,HIGH);\r\n" + 
	    "       delayMicroseconds(micro);\r\n" + 
	    "       digitalWrite(gpio_puls,LOW);\r\n" + 
	    "       delayMicroseconds(micro);\r\n" + 
	    "   }\r\n" + 
	    "}\n";
	   	translator.addDefinitionCommand(set);
   	
    
	  	String ret = "setArduMotorSteps("+puls+","+dir+","+steps+","+speed+");    // Ausgabe an Motor \n";
		

		return codePrefix + ret + codeSuffix;
	}
}