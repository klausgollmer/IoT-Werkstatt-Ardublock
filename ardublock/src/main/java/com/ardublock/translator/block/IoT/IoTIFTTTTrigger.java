package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTIFTTTTrigger  extends TranslatorBlock {

	public IoTIFTTTTrigger (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		//translator.addSetupCommand("Serial.begin(115200);");
		
		String Client, Check;
		if (translator.isGPRSProgram()) {
			Client= " TinyGsmClient client(modem);// Client über GSM\n";
			Check=  " int ok = checkMobilfunk();\n";
		} else {
			Client= " WiFiClient client; // Client über WiFi\n";
			Check=  " int ok = 1;\n";
		}
		
		String httpGET ="//--------------------------------------- HTTP-Get\n"
				+ "int httpGET(String host, String cmd, String &antwort,int Port) {\n"
				+ Client
				+ " String text = \"GET http://\"+ host + cmd + \" HTTP/1.1\\r\\n\";\n"
		        + " text = text + \"Host:\" + host + \"\\r\\n\";\n"
		        + " text = text + \"Connection:close\\r\\n\\r\\n\";\n"
		        + Check
		        + " if (ok) { // Netzwerkzugang vorhanden \n"
		        + "  ok = client.connect(host.c_str(),Port);// verbinde mit Client\n"
		        + "  if (ok) {\n"
		        + "   client.print(text);                 // sende an Client \n" 
		        + "   for (int tout=1000;tout>0 && client.available()==0; tout--)\n"
		        + "    delay(10);                         // und warte auf Antwort\n"
		        + "   if (client.available() > 0)         // Anwort gesehen \n"
		        + "    while (client.available())         // und ausgewertet\n"
		        +"      antwort = antwort + client.readStringUntil('\\r');\n"
		        +"    else ok = 0;\n"
		        +"    client.stop(); \n"
		        +"    Serial.println(antwort);\n"
		        + "  } \n"
		        + " } \n"
		        + " if (!ok) Serial.print(\" no connection\"); // Fehlermeldung\n"
		        + " return ok;\n"
		        + "}\n";

		translator.addDefinitionCommand(httpGET);
		
		String apikey,ret,trigger;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    trigger = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    apikey = translatorBlock.toCode();
    

        ret ="{ //Block------------------------------ HTTP-Get IfThisThenThat\n"
        	 + " String host = \"maker.ifttt.com\";\n"
             + " String cmd = \"/trigger/\" + String(" + trigger + ") + \"/with/key/\" + String("+apikey+");"
             + " String antwort = \" \";\n"
             +"  Serial.print(\"\\n Trigger IFTTT \");\n"
             +"  Serial.println("+trigger+");\n"
             + " httpGET(host,cmd,antwort,80); \n"
             +"  Serial.println(antwort);\n"
             +"} // Blockende\n";

        return codePrefix + ret + codeSuffix;
	 	}
}

