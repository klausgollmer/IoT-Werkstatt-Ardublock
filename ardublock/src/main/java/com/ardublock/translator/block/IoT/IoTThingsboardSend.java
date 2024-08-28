package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTThingsboardSend  extends TranslatorBlock {

	public IoTThingsboardSend (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");

		translator.addSetupCommand("Serial.begin(115200);");

		translator.addDefinitionCommand("WiFiClient wifiClient;");
		
		String httpPOST ="void sendThingsboard(String server, String token, String n1, String d1, String n2, String d2, String n3, String d3) {\n"
				+ "  HTTPClient http; //Declare object of class HTTPClient\n"
				+ "  // create Json data\n"
				+ "  String json = \"{\";\n"
				+ "  if (n1 != \"\") json += \"\\\"\"+n1+\"\\\":\\\"\"+d1+\"\\\"\";\n"
				+ "  if (n2 != \"\") json += \",\\\"\"+n2+\"\\\":\\\"\"+d2+\"\\\"\";\n"
				+ "  if (n3 != \"\") json += \",\\\"\"+n3+\"\\\":\\\"\"+d3+\"\\\"\";\n"
				+ "  json += \"}\";\n"
				+ "\n"
				+ "  //HTTP-POST Operation (REST API, https://thingsboard.io/docs/reference/http-api/#telemetry-upload-api) \n"
				+ "  String req=\"http://\"+server+\"/api/v1/\"+token+\"/telemetry\";\n"
				+ "  Serial.println(req);\n"
				+ "  http.begin(wifiClient,req); //Specify request destination\n"
				+ "  http.addHeader(\"Content-Type\", \"application/json\"); //\n"
				+ "  int httpCode = http.POST(json); //Send the request\n"
				+ "  Serial.println(json);\n"
				+ "  String payload = http.getString(); //Get the response payload\n"
				+ "  Serial.println(payload); //Print request response payload\n"
				+ "  http.end(); //Close connection\n"
				+ "}";
		/*
		String httpPOST = "//-------------------------Thingsboard Cloud, https://thingsboard.io/ ------ HTTP-POST\n"
				+ "void sendThingsboard(String server, String token, String n1, String d1, String n2, String d2, String n3, String d3) {\n"
				+ "  HTTPClient http; //Declare object of class HTTPClient\n"
				+ "  // create Json data\n"
				+ "  String json = \"{\";\n"
				+ "  if (n1 != \"\") json += \"\\\"\"+n1+\"\\\":\"+d1;\n"
				+ "  if (n2 != \"\") json += \",\\\"\"+n2+\"\\\":\"+d2;\n"
				+ "  if (n3 != \"\") json += \",\\\"\"+n3+\"\\\":\"+d3;\n"
				+ "  json += \"}\";\n"
				+ "  \n"
				+ "  //HTTP-POST Operation (REST API, https://thingsboard.io/docs/reference/http-api/#telemetry-upload-api) \n"
				+ "  String req=\"http://\"+server+\"/api/v1/\"+token+\"/telemetry\";\n"
				+ "  Serial.println(req);\n"
				+ "  http.begin(wifiClient,req); //Specify request destination\n"
				+ "  http.addHeader(\"Content-Type\", \"application/json\"); //\n"
				+ "  int httpCode = http.POST(json); //Send the request\n"
				+ "  Serial.println(json);\n"
				+ "  String payload = http.getString(); //Get the response payload\n"
				+ "  Serial.println(payload); //Print request response payload\n"
				+ "  http.end(); //Close connection\n"
				+ "}";
		 */
		translator.addDefinitionCommand(httpPOST);
		
		String host,access,n1,d1,n2="\"\"",d2="\"NAN\"",n3="\"\"",d3="\"NAN\"",ret;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    access = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    n1 = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    d1 = translatorBlock.toCode();

	    translatorBlock = this.getTranslatorBlockAtSocket(4);
	    if (translatorBlock!=null) n2 = translatorBlock.toCode();
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(5);
	    if (translatorBlock!=null) d2 = translatorBlock.toCode();
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(6);
	    if (translatorBlock!=null) n3 = translatorBlock.toCode();
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(7);
	    if (translatorBlock!=null) d3 = translatorBlock.toCode();

	    ret = "sendThingsboard("+host+","+access+","+n1+","+d1+","+n2+","+d2+","+n3+","+d3+");// http REST, Post to thingsboard\n"; 
        return codePrefix + ret + codeSuffix;
	 	}
}

