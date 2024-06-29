package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTYahooGet  extends TranslatorBlock {

	public IoTYahooGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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

		String parse ="//--------------------------------------- Parse XML\n"
		+"//Hilfsfunktion: Extrahiere den Suchtext aus der XML-Antwort\n"
		+"float parseSensorInfo(String xml,String suchtext) {\n"
		+"  float val = NAN;                   // Rückgabewert NaN\n"
		+"  String valStr;                     // Hilfsstring\n"
		+"  int start, ende;                   // Index im Text\n"
		+"  suchtext = suchtext+'='+'\"';       // Suchtext\n"
		+"  start = xml.indexOf(suchtext);     // Suche Text\n"
		+"  delay(10);\n"  
		+"  if (start > 0) {                   // Item gefunden\n"
		+"    start = start+suchtext.length(); // hinter Item kommt Zahl\n"
		+"    ende =  xml.indexOf('\"',start);  // Ende der Zahl\n" 
		+"    valStr= xml.substring(start,ende);// Zahltext\n"     
		+"    val = valStr.toFloat();          // Text in Float\n" 
		+"  } else                             // Item nicht gefunden\n"
		+"    Serial.print(\"error - no such item: \"+suchtext);\n"
		+"  return val;\n"
		+"}\n";  

		translator.addDefinitionCommand(parse);

		
		String yahoo="//--------------------------------------- Abfrage bei Yahoo\n"
		+"float askYahoo(String what,String woeid) { \n"		
		+"  String cmd = \"/v1/public/yql?q=select%20wind%20from%20weather.forecast%20where%20woeid=\"+String(woeid); \n"
		+"  String host = \"query.yahooapis.com\";\n"
		+"  String antwort;\n"  
		+"  httpGET(host,cmd,antwort,80)\n;                     // Anfrage senden, Antwort holen\n"
		+"  float s=parseSensorInfo(antwort,what);              // Zahlenwert aus der Antwort extrahieren\n"
	    +"  return s;\n"			
		+"}\n";
		translator.addDefinitionCommand(yahoo);

		
		
		
		String what,woeid;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    what = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    woeid = translatorBlock.toCode();
	    String ret = "askYahoo("+what+","+woeid+")";
        return codePrefix + ret + codeSuffix;
	 	}
}

