package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTServoWrite extends TranslatorBlock
{

  public IoTServoWrite (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }


	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    String pin = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    String pos = translatorBlock.toCode();
		
		 // Header hinzuf�gen
	    translator.addHeaderFile("Servo.h");
      
	   	translator.addDefinitionCommand("Servo myservo_"+pin+"; // Servo-Objekt");

	    
	    // Setupdeklaration
	    // I2C-initialisieren
	    translator.addSetupCommand("Serial.begin(115200);");
	    
	    String Setup = "myservo_"+pin+".attach("+pin+"); // Servo mit Pin verknüpfen" ;
	    translator.addSetupCommand(Setup);
		String ret = "myservo_"+pin+".write("+pos+");    // Ausgabe an Servo \n";
		

		return codePrefix + ret + codeSuffix;
	}
}