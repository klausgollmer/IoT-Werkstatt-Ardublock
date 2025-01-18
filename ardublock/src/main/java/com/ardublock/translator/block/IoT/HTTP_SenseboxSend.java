package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class HTTP_SenseboxSend  extends TranslatorBlock {

	public HTTP_SenseboxSend (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");

		//translator.addSetupCommand("Serial.begin(115200);");
/*		
		String Client, Check;
		if (translator.isGPRSProgram()) {
			Client= " TinyGsmClient client(modem);// Client über GSM\n";
			Check=  " int ok = checkMobilfunk();\n";
		} else {
			Client= " WiFiClient client; // Client über WiFi\n";
			Check=  " int ok = 1;\n";
		}
*/
		translator.addDefinitionCommand("WiFiClient wifiClient;");
		String httpPOST ="//-------------------------OpenSenseMAP  ------ HTTP-POST\n" +
		"void sendSenseMap(String server, float measurement, int digits, String SENSEBOX_ID, String sensorId) {\n" +
        "  HTTPClient http; //Declare object of class HTTPClient\n" + 
        "  //Json erstellen\n" + 
        "  String jsonValue = \"{\\\"value\\\":\" + String(measurement)+ \"}\";\n" + 
        "  //Mit OSeM Server verbinden und POST Operation durchführen\n" + 
        "  String req=\"http://\"+server+\"/boxes/\"+SENSEBOX_ID+\"/\"+sensorId;\n" + 
        "  Serial.println(req);\n" + 
        "  http.begin(wifiClient,req); //Specify request destination\n" + 
        "  http.addHeader(\"Content-Type\", \"application/json\"); //\n" + 
        "  int httpCode = http.POST(jsonValue); //Send the request\n" + 
        "  String payload = http.getString(); //Get the response payload\n" + 
        "  //Serial.print(httpCode);  //Print HTTP return code\n" + 
        "  Serial.println(payload); //Print request response payload\n" + 
        "  http.end(); //Close connection\n" + 
        " }";
		
		translator.addDefinitionCommand(httpPOST);
		

		
		String host,boxID,senseID,ret,wert;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    boxID = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    senseID = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    wert = translatorBlock.toCode();
	    
	    ret = "sendSenseMap("+host+","+wert+",1,"+boxID+","+senseID+");// Post to OpenSenseMap\n"; 
           

        return codePrefix + ret + codeSuffix;
	 	}
}

