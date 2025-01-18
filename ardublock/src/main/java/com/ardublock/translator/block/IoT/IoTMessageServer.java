package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTMessageServer  extends TranslatorBlock {

	public IoTMessageServer (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
		String title, Sensorik;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    title = translatorBlock.toCode();
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    Sensorik = translatorBlock.toCode();

		translator.setHTTPServerProgram(true);;
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WebServer.h> \n#elif defined(ESP32) \n #include <WebServer.h>;\n#endif\n");		
		if (!Sensorik.equals("false")) {
 	      translator.addHeaderFile("SparkFunBME280.h");
   	      translator.addHeaderFile("Adafruit_BME680.h");
		  translator.addHeaderFile("Wire.h");
		  String EncDef =   "#if defined(ESP8266) \n"
			      + "// Autor Paul Stoffregen, http://www.pjrc.com/teensy/td_libs_Encoder.html\n"
			      + " #include <Encoder.h> \n"
			      + "#elif defined(ESP32) \n"
			      + "// Autor: Kevin Harrington, https://www.arduino.cc/reference/en/libraries/esp32encoder\n"
			      + " #include <ESP32Encoder.h>;\n"
			      + "#endif\n"
			      + "// encoder max range\n"
			      + "int encoder_counter_rot_min = -100;\n"
			      + "int encoder_counter_rot_max = 100;\n"
			      + "";
	translator.addDefinitionCommand(EncDef);

	EncDef = "// Helper rotary encoder\n"
	        + "#if defined(ESP8266) \n"
			+ "Encoder button_encoder(IOTW_GPIO_ROTARY_B,IOTW_GPIO_ROTARY_A);\n"
		    + "#elif defined(ESP32) \n"
		    +  "ESP32Encoder button_encoder; \n"
		    + "#endif\n"
			+ " \n"
			+ "int encoderRead() {\r\n"
			+ "  int wert = 0;\r\n"
			+ "#if defined(ESP8266) \r\n"
			+ "  wert = button_encoder.read();\r\n"
			+ "  if (wert > encoder_counter_rot_max) {\r\n"
			+ "     button_encoder.write(encoder_counter_rot_max);\r\n"
			+ "     wert = encoder_counter_rot_max;\r\n"
			+ "  }\r\n"
			+ "  if (wert < encoder_counter_rot_min) {\r\n"
			+ "     button_encoder.write(encoder_counter_rot_min);\r\n"
			+ "     wert = encoder_counter_rot_min;\r\n"
			+ "  }\r\n"
			+ "#elif defined(ESP32)\r\n"
			+ "  wert = button_encoder.getCount()/2;\r\n"
			+ "  if (wert > encoder_counter_rot_max) {\r\n"
			+ "     button_encoder.setCount(encoder_counter_rot_max * 2);\r\n"
			+ "     wert = encoder_counter_rot_max;\r\n"
			+ "  }\r\n"
			+ "  if (wert < encoder_counter_rot_min) {\r\n"
			+ "     button_encoder.setCount(encoder_counter_rot_min * 2);\r\n"
			+ "     wert = encoder_counter_rot_min;\r\n"
			+ "  }\r\n"
			+ "#endif\r\n"
			+ "  return wert;\r\n"
			+ "}";
	translator.addDefinitionCommand(EncDef);

		 
   		  
   		  
   		  
		   translator.addDefinitionCommand(EncDef);
		   EncDef ="#if defined(ESP32) \n "
			   		+  "    ESP32Encoder::useInternalWeakPullResistors = puType::up;\n"
			   		+  "    button_encoder.attachHalfQuad(IOTW_GPIO_ROTARY_B, IOTW_GPIO_ROTARY_A); \n "
			   		+  "    pinMode(IOTW_GPIO_ROTARY_B,INPUT_PULLUP); \n "
			   		+  "    pinMode(IOTW_GPIO_ROTARY_A,INPUT_PULLUP); \n "
			   		+  "#endif \n";
		   translator.addSetupCommand(EncDef);
		} 
				
  	    
		
		//translator.addSetupCommand("Serial.begin(115200);");
	
		translator.addSetupCommand("//------------ HTML-Server initialisieren");
		translator.addSetupCommand("server.on(\"/\", serverHomepage);");
		
		if (!Sensorik.equals("false")) {	 	    
			translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
			translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
			 
			translator.addSetupCommand("boschBME280.settings.runMode = 3; // Normal Mode\n"
         		+ "boschBME280.settings.tempOverSample  = 4; \n"
         		+ "boschBME280.settings.pressOverSample = 4;\n"
         		+ "boschBME280.settings.humidOverSample = 4;\n"
         		+ "boschBME280_ready = boschBME280.begin();");
		
         translator.addSetupCommand("boschBME680_ready = boschBME680.begin(118);\n");
         String  Setup = "// Set up Bosch BME 680\n"+
        		    "boschBME680.setTemperatureOversampling(BME680_OS_8X);\n"+
        		    "boschBME680.setHumidityOversampling(BME680_OS_2X);\n"+
        		    "boschBME680.setPressureOversampling(BME680_OS_4X);\n"+
        		    "boschBME680.setIIRFilterSize(BME680_FILTER_SIZE_3);\n"+
        		    "boschBME680.setGasHeater(0, 0); // off\n";    
        		    translator.addSetupCommand(Setup);
        			//translator.addSetupCommand(" server.begin();// Server starten");

   		}       
			    		
		String HomepageCode;
		if (!Sensorik.equals("false")) {
	 	  HomepageCode ="//------------------------------ Server Hompage html-Code\n" 
			+"const char INDEX_HTML_START[] =\n" 
			+" \"<!DOCTYPE HTML>\"\n"
			+"  \"<html>\"\n"
			+"  \"<META HTTP-EQUIV=\\\"refresh\\\" CONTENT=\\\"15\\\">\"\n"
			+"   \"<head>\"\n"
			+"   \"<meta name = \\\"viewport\\\" content = \\\"width = device-width, initial-scale = 1.0, maximum-scale = 1.0, user-scalable=0\\\">\"\n"
			+"	   \"<title>ESP8266 IoT.OCTOPUS</title>\"\n"
			+"	 \"</head>\"\n"
			+"	   \"<body>\"\n"
			+"     \"<CENTER>\"\n" + 
			"      \"  <img src=' data:image/jpeg;base64,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' alt=''>\"\n" + 
			"      \"</CENTER> \"\n"  
			+"	   \"<h1>\"\n"
			+                title+"\n" 
			+                    "\"</h1>\"\n"
			+"	   \"<FORM action=\\\"/\\\" method=\\\"post\\\">\"\n"
			+"     \"<P>\"\n"
			+"        \"Deine Nachricht<br>\"\n"
			+"        \"<INPUT type=\\\"text\\\" name=\\\"message\\\"><br>\"\n"
			+"        \"<INPUT type=\\\"submit\\\" value=\\\"Send\\\">\"\n"
			+"	    \"</P>\"\n"
			+"	   \"</FORM>\";\n"			

			+"const char INDEX_HTML_END[] =\n"
			+"	 \"</body>\"\n"
			+"	\"</html>\";\n"

			+"//------------------------------ Server Ausgabe Messwerte in Form einer html-Tabelle\n" 
            +"String messwertTabelle(){ \n"
            +"  float Tmess,pmess,hmess;\n"          
            +"  if (boschBME680_ready) {\n"
            +"    Tmess=boschBME680.readTemperature();\n" 
            +"    pmess=boschBME680.readPressure()/100.;\n"
            +"    hmess=boschBME680.readHumidity();\n"
            +"  } else { \n"
            +"    Tmess=boschBME280.readTempC();\n" 
            +"    pmess=boschBME280.readFloatPressure()/100.;\n"
            +"    hmess=boschBME280.readFloatHumidity();\n"
            +"  }\n"            
            +"  String html =  \"<h2>Aktuelle Informationen:</h2>\";\n"
            
			+"  html = html +  \"<table>\";\n"
			+"  html = html +  \"<tr><td>\" + \"Aktuelle Nachricht\" + \"</td> <td>\" + String(\" \") + \"</td><td>\"+ String(matrixausgabe_text)+ \"</td></tr>\";\n"
			+"  html = html +  \"<tr><td>\" + \"\" + \"</td> <td>\" + String(\" \") + \"</td><td>\"+ \"\"+\" </td></tr>\";\n"
			+"  html = html +  \"<tr><td>\" + \"Temperatur    \" + \"</td> <td>\" + String(Tmess) + \"</td><td>\"+ \"Grad Celsius\"+\" </td></tr>\";\n"
			+"  html = html +  \"<tr><td>\" + \"Luftdruck     \" + \"</td> <td>\" + String(pmess) + \"</td><td>\"+ \"hPa\"+\" </td></tr>\";\n"
			+"  html = html +  \"<tr><td>\" + \"rel. Feuchte  \" + \"</td> <td>\" + String(hmess) + \"</td><td>\"+ \"%\"+\" </td></tr>\";\n"
			+"  html = html +  \"<tr><td>\" + \"Analog Input  \" + \"</td> <td>\" + String(analogRead(IOTW_GPIO_A0)) + \"</td><td>\"+ \"-\"+\" </td></tr>\";\n"
			+"  html = html +  \"<tr><td>\" + \"Drehknopf     \" + \"</td> <td>\" + String(encoderRead()) + \"</td><td>\"+ \"-\"+\" </td></tr>\";\n"
			+"  html = html +  \"<tr><td>\" + \"Knopfdruck    \" + \"</td> <td>\" + String(digitalRead(IOTW_GPIO_ROTARY_BUTTON)==LOW) + \"</td><td>\"+ \"on/off\"+\" </td></tr>\";\n"

			+"  html=html+\"</table>\";\n"
			+"  return html;\n"
			+"}\n"
			
			+"//------------------------------ Server Unterprogramm zur Bearbeitung der Anfragen\n" 
			+"void serverHomepage() { \n"
		    +" if (server.hasArg(\"message\")) {// Wenn neuer Anzeigetext eingetroffen,\n"
			+"   matrixausgabe_text = server.arg(\"message\");     // dann Text vom Client einlesen\n"
			+"   matrixausgabe_index=0;\n"
			+"   Serial.print(\"Server Anzeigetext: \"); Serial.println(matrixausgabe_text);\n"
			+" }\n"       
			+" String html=String(INDEX_HTML_START);  // Hompage zusammenbauen\n" 
			+" html=html+messwertTabelle();           // Aktuelle Messwerte integrieren\n"
			+" html=html+String(INDEX_HTML_END);      // Http-Body abschliessen \n"
			+" server.send(200, \"text/html\", html); // Homepage ausgeben\n"
			+"}\n";
		} else {
			  HomepageCode ="//------------------------------ Server Hompage html-Code\n" 
						+"const char INDEX_HTML_START[] =\n" 
						+" \"<!DOCTYPE HTML>\"\n"
						+"  \"<html>\"\n"
						+"   \"<head>\"\n"
						+"   \"<meta name = \\\"viewport\\\" content = \\\"width = device-width, initial-scale = 1.0, maximum-scale = 1.0, user-scalable=0\\\">\"\n"
						+"	   \"<title>ESP8266 IoT.OCTOPUS</title>\"\n"
						+"	 \"</head>\"\n"
						+"	 \"<body>\"\n"
						+"\"  <CENTER>\"\n" + 
						"\"    <img src=' data:image/jpeg;base64,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' alt=''>\"\n" + 
						"\"  </CENTER> \"\n"  
						+"	   \"<h1>\"\n"
						+                title+"\n" 
						+                    "\"</h1>\"\n"
						+"	   \"<FORM action=\\\"/\\\" method=\\\"post\\\">\"\n"
						+"     \"<P>\"\n"
						+"        \"Deine Nachricht<br>\"\n"
						+"        \"<INPUT type=\\\"text\\\" name=\\\"message\\\"><br>\"\n"
						+"        \"<INPUT type=\\\"submit\\\" value=\\\"Send\\\">\"\n"
						+"	    \"</P>\"\n"
						+"	   \"</FORM>\";\n"			

						+"const char INDEX_HTML_END[] =\n"
						+"	 \"</body>\"\n"
						+"	\"</html>\";\n"

						
						+"//------------------------------ Server Unterprogramm zur Bearbeitung der Anfragen\n" 
						+"void serverHomepage() { \n"
					    +" if (server.hasArg(\"message\")) {// Wenn neuer Anzeigetext eingetroffen,\n"
						+"   matrixausgabe_text = server.arg(\"message\");     // dann Text vom Client einlesen\n"
						+"   matrixausgabe_index=0;\n"
						+"   Serial.print(\"Server Anzeigetext: \"); Serial.println(matrixausgabe_text);\n"
						+" }\n"       
						+" String html=String(INDEX_HTML_START);  // Hompage zusammenbauen\n" 
						+" html=html+String(INDEX_HTML_END);      // Http-Body abschliessen \n"
						+" server.send(200, \"text/html\", html); // Homepage ausgeben\n"
						+"}\n";	
		}
		
		String DefWeb = "#if defined(ESP8266)\n" + 
		        "    ESP8266WebServer server(80);\n" +
	        	"#elif defined(ESP32)\n"+
		        "    WebServer server(80);\n" +
		        "#endif\n"; 
        translator.addDefinitionCommand(DefWeb);
		
		translator.addDefinitionCommand("String matrixausgabe_text  = \" \"; // Ausgabetext als globale Variable\n");
		translator.addDefinitionCommand("volatile int matrixausgabe_index = 0;// aktuelle Position in Matrix\n");
		if (!Sensorik.equals("false")) {
		 translator.addDefinitionCommand("BME280 boschBME280; // Objekt Bosch Umweltsensor");
 		 translator.addDefinitionCommand("int boschBME280_ready = 0; // Objekt Bosch Umweltsensor");
    	 translator.addDefinitionCommand("// BME680 Lib written by Limor Fried & Kevin Townsend for Adafruit Industries, http://www.adafruit.com/products/3660");
 		 translator.addDefinitionCommand("Adafruit_BME680 boschBME680; // Objekt Bosch Umweltsensor");
 	     translator.addDefinitionCommand("int boschBME680_ready = 0;\n");
   	    }
   	    translator.addDefinitionCommand(HomepageCode);

		
	    String ret ="//Block------------------------------ HTML-Server\n"
	    		  + "server.begin();// Server starten \n" 
	    	  	  + "server.handleClient(); //Homepageanfragen versorgen\ndelay(1);\n";
        return codePrefix + ret + codeSuffix;
	 	}
}

