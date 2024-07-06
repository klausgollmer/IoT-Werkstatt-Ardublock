package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SerialReadString extends TranslatorBlock
{
	public SerialReadString(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		//translator.addSetupCommand("Serial.begin(9600);");
		translator.addSetupCommand("Serial.begin(115200);");

		String Read;
	    Read= "// -----------  Einlesefunktion String von serieller Schnittstelle\n"
	  		+ "String SerialReadEndless(String Ausgabe) { \n"
	   	    + "  String ret;\n"
	        + "  Serial.print(Ausgabe);\n"
	        + "  Serial.setTimeout(2147483647UL); // warte bis Eingabe\n"
			+ "  ret = Serial.readStringUntil('\\n');\n"
		    + "  return ret;\n"
	        +" }\n";
	        
	    
	    
	    String Ausgabe="\"\"";
	    translator.addDefinitionCommand(Read); 		
	    TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0);
	    if (translatorBlock!=null)
	       Ausgabe = translatorBlock.toCode();
	    
		String ret = "SerialReadEndless("+ Ausgabe +")"; 
	
		return codePrefix+ret+codeSuffix;
	}
}
