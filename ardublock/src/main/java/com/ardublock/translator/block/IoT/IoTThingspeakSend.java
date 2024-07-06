package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTThingspeakSend  extends TranslatorBlock {

	public IoTThingspeakSend (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addSetupCommand("Serial.begin(115200);");
		
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
		

		
		String apikey,host,ret,wert;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();
	
	    if (host.contains("api.thingspeak.umwelt-campus.de")) {
	    	host = "thingspeak.umwelt-campus.de";
	    }
	    

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    apikey = translatorBlock.toCode();
	    
//	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
//	    feldindex = translatorBlock.toCode();
	    wert = "";
	    translatorBlock = this.getTranslatorBlockAtSocket(2);
	    if (translatorBlock!=null)
	       wert +="\"&field1=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(3);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&field2=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(4);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&field3=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(5);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&field4=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(6);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&field5=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(7);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&field6=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(8);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&field7=\"+String("+ translatorBlock.toCode()+")";
	    translatorBlock = this.getTranslatorBlockAtSocket(9);
	    if (translatorBlock!=null)
  	       wert +="\n   +\"&field8=\"+String("+ translatorBlock.toCode()+")";
	    
	   
	    
	    ret = "\n{ //Block------------------------------ sende Daten an Thingspeak (mit http GET) \n"
       		 +" Serial.println(\"\\nThingspeak update \");\n"
        	 +" String cmd = \"/update?api_key=\"+ String(" + apikey +");\n" 
             +" String host = "+ host +";\n"
             +" String antwort= \" \";\n"
 	         +" cmd = cmd +" + "String(" + wert + ")" + "+ \"\\n\\r\";\n"
 	         +" httpGET(host,cmd,antwort,80);// und absenden \n"   
             +"} // Blockende\n";
           

        return codePrefix + ret + codeSuffix;
	 	}
}

