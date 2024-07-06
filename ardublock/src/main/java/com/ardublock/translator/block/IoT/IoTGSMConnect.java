package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTGSMConnect  extends TranslatorBlock {

	public IoTGSMConnect (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("SoftwareSerial.h");
		
	
		String apn,user,pass;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    apn = translatorBlock.toCode();
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    user = translatorBlock.toCode();
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    pass = translatorBlock.toCode();
	    
	    
	    
	    translator.addDefinitionCommand("// TinyGSM Library https://github.com/vshymanskyy/TinyGSM");
	    translator.addDefinitionCommand("#define TINY_GSM_MODEM_SIM800");
	    translator.addDefinitionCommand("#include <TinyGsmClient.h>");
		translator.addDefinitionCommand("#define GSM_APN  "+apn+"");
		translator.addDefinitionCommand("#define GSM_USER "+user+"");
		translator.addDefinitionCommand("#define GSM_PASS "+pass+"");
		translator.addDefinitionCommand("SoftwareSerial SerialAT(12, 14); // serielle Schnittstelle Modem per Software RX, TX");
		translator.addDefinitionCommand("TinyGsm modem(SerialAT);         // Objekt GSM-Modem");
	
			    
	    translator.addSetupCommand("Serial.begin(115200);");
		translator.addSetupCommand("SerialAT.begin(19200);");
		
		String checkGSM ="//--------------------------------------- Ueberpruefe Mobilfunknetz \n"
				+ "int checkMobilfunk() {\n"
				+ "   int OK = 0;\n"
				+ "   if (!modem.waitForNetwork()) {   \n"      
				+ "	    Serial.print(\" kein Mobilfunknetz\");\n"
				+ "   } else { \n"
				+ "     Serial.print(\"\\n GSM signal quality (2..30): \");\n"
				+ "     Serial.println(modem.getSignalQuality());\n"
				+ "     if (!modem.gprsConnect(GSM_APN,GSM_USER,GSM_PASS)) {\n"
				+ "	       Serial.print(\" Mobilfunk ok, aber kein APN Zugang\");\n"
				+ "	    } else { \n" 
				+ "	       OK =1;   // alles klar, es kann losgehen\n"
				+ "	    }\n"
				+ "	  }\n"
				+ "	  return OK; \n"
				+ "	};\n";
		translator.addDefinitionCommand(checkGSM);
		
		translator.setGPRSProgram(true); // Merke das Kommunikation über GSM-Läuft
		
	    String ret = "//------------ GSM-Modem initialisieren \n"
	         +"Serial.print (\"\\nGSM-Modem initialisieren\");\n"
	         +"modem.restart();\n"
	         +"Serial.print (\"\\nund jetzt im Mobilfunknetz einbuchen ... \");\n"
	         +"if (checkMobilfunk()) {\n"
	         +"   Serial.println(\" alles OK \"); \n"
	         +"  } else { \n"
	         +"   Serial.println(\" , versuche es spaeter erneut\"); \n"
	         +"};\n";
	         
	    
        return codePrefix + ret + codeSuffix;
	 	}
}

