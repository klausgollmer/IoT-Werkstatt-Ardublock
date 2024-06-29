package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTMessageServer_CO2_V2  extends TranslatorBlock {

	public IoTMessageServer_CO2_V2 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
		
		String s,s0,u0,s1,u1,s2,u2,v0,v1,v2,pma0,pma1,pma2,pmi0,pmi1,pmi2,pd0,pd1,pd2;
		u0="\"\"";
		u1="\"\"";
		u2="\"\"";
		
		s0="\"\"";
		s1="\"\"";
		s2="\"\"";
		
		v0="0";
		v1="0";
		v2="0";

		pma0="1000";
		pma1="1000";
		pma2="1000";
		
		pmi0="0";
		pmi1="0";
    	pmi2="0";

		pd0="100";
		pd1="100";
    	pd2="100";

			
        String c0,c1,c2;
        c0 = "60";
        c1 = "60";
        c2 = "60";
		
		String pass = "your password";
		String Titel = "IoT-Werkstatt CO2";
		
		int pos;
		
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    Titel = translatorBlock.toCode();
		
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    s = translatorBlock.toCode();
 	    pos = s.indexOf(",");
        if (pos>=0) { 
          s0 = s.substring( 0, pos)+"\"";
   	      u0= "\""+s.substring(pos+1,s.length()-1)+"\"";
        } else {u0="\"\"";s0=s;}

		translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    v0 = translatorBlock.toCode();
        
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    String l1 = translatorBlock.toCode();
        
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(4);
	    String l2 = translatorBlock.toCode();

		translatorBlock = this.getRequiredTranslatorBlockAtSocket(5);
	    c0 = translatorBlock.toCode();
        // plot-Angaben auswerten
	    
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(6);
	    s = translatorBlock.toCode();
 	    pos = s.indexOf(",");
        if (pos>=0) { 
          pmi0 = s.substring(1, pos);
          s    = s.substring(pos+1,s.length()-1);
          pos  = s.indexOf(","); 
          pma0 = s.substring(0, pos);
          pd0  =  s.substring(pos+1,s.length());
        }
	    
        
	    
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(8);
	    if (translatorBlock!=null) { // value v1 vorhanden
	    	v1 =translatorBlock.toCode();
			translatorBlock = this.getTranslatorBlockAtSocket(7);
		    s = translatorBlock.toCode();
	 	    pos = s.indexOf(",");
	        if (pos>=0) { 
	          s1 = s.substring( 0, pos)+"\"";
	   	      u1= "\""+s.substring(pos+1,s.length()-1)+"\"";
	        } else {u1="\"\"";s1=s;}
	        translatorBlock = this.getRequiredTranslatorBlockAtSocket(9);
		    c1 = translatorBlock.toCode();
	        // plot auswerten
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(10);
		    s = translatorBlock.toCode();
		    pos = s.indexOf(",");
		    if (pos>=0) { 
		          pmi1 = s.substring(1, pos);
		          s    = s.substring(pos+1,s.length()-1);
		          pos  = s.indexOf(","); 
		          pma1 = s.substring(0, pos);
		          pd1  = s.substring(pos+1,s.length());
		    }
	    }
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(12);
	    if (translatorBlock!=null) { // V2 vorhanden
	    	v2 =translatorBlock.toCode();
			translatorBlock = this.getTranslatorBlockAtSocket(11);
		    s = translatorBlock.toCode();
	 	    pos = s.indexOf(",");
	        if (pos>=0) { 
	          s2 = s.substring( 0, pos)+"\"";
	   	      u2= "\""+s.substring(pos+1,s.length()-1)+"\"";
	        } else {u2="\"\"";s2=s;}
	        translatorBlock = this.getRequiredTranslatorBlockAtSocket(13);
		    c2 = translatorBlock.toCode();
	        // plot auswerten
			translatorBlock = this.getRequiredTranslatorBlockAtSocket(14);
		    s = translatorBlock.toCode();
		    pos = s.indexOf(",");
		    if (pos>=0) { 
		          pmi2 = s.substring(1, pos);
		          s    = s.substring(pos+1,s.length()-1);
		          pos  = s.indexOf(","); 
		          pma2 = s.substring(0, pos);
		          pd2  = s.substring(pos+1,s.length());
		    }
	    }

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(15);
	    if (translatorBlock!=null) {
	    	pass=translatorBlock.toCode();
	    }

	    translator.addDefinitionCommand("int doCal=0;  // Kalibriermerker");
		String calfun = "void CO2_Kalibrierfunktion(){ // Kalibrierfunktion\n";
		translatorBlock = getTranslatorBlockAtSocket(16);
	    
		while (translatorBlock != null)
		{
			calfun = calfun + translatorBlock.toCode();
			translatorBlock = translatorBlock.nextTranslatorBlock();
		}
		calfun = calfun + "}\n\n";
		translator.addDefinitionCommand(calfun);
	    
	    

		translator.setHTTPServerProgram(true);;
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WebServer.h> \n#elif defined(ESP32) \n #include <WebServer.h>;\n#endif\n");		
		translator.addHeaderFile("sensorplot_webinterface.h");
		
		translator.addDefinitionCommand("String matrixausgabe_text  = \" \"; // Ausgabetext als globale Variable\n");
		translator.addDefinitionCommand("volatile int matrixausgabe_index = 0;// aktuelle Position in Matrix\n");
		String webserverDef = "typedef void (*func_ptr)(void);\n" + 
				"func_ptr WebServerHousekeeping = yield;\n";
		translator.addDefinitionCommand(webserverDef);
		String WebServerDefinition = "//---- CO2WebServer, Open-Source Library contributed by Jason Rietzke, UCB -------------\n"+
		                             "//     https://github.com/jason-rietzke/SensorPlot_WebInterface\n"+
				                     "//---------------------- SensorPlot WebInterface Module 1/3 START ----------------------\n" + 
				"#if defined(ESP8266)\n" + 
				"    ESP8266WebServer server(80);\n" +
				"#elif defined(ESP32)\n"+
				"    WebServer server(80);\n" +
				"#endif\n"+
				"String WebInterfaceTitel = "+Titel+";\n"+
				"SensorPlot_WebInterface webInterface = SensorPlot_WebInterface();\n" + 
				"\n" + 
				"String calibrationPassword = "+pass+"; // Kalibrierpasswort\n"+

				
				"int calibrateCallback(String password) {\n" + 
				"  if (password == calibrationPassword) {\n" + 
				"    CO2_Kalibrierfunktion();\n" + 
				"    return 1;\n" + 
				"  }\n" + 
				"  return 0;\n" + 
				"}\n" + 
				"\n" + 
				"void sensorReading(float *measurements, int *measurementsCount, int maxMeasurements, int *measurementsTimestamp, float sensorInput) {\n" + 
				"  if (*measurementsCount < maxMeasurements) {\n" + 
				"    *measurementsCount += 1;\n" + 
				"    measurements[(*measurementsCount - 1)] = sensorInput;\n" + 
				"  } else {\n" + 
				"    *measurementsCount = maxMeasurements;\n" + 
				"    for(int i = 0; i < (*measurementsCount - 1); i++) {\n" + 
				"      measurements[i] = measurements[i + 1];\n" + 
				"    }\n" + 
				"    measurements[(*measurementsCount - 1)] = sensorInput;\n" + 
				"  }\n" + 
				"  *measurementsTimestamp = millis();\n" + 
				"}\n" + 
				"\n" + 
				"int measurementsCount1 = 0;\n" + 
				"float measurements1[128] = {};\n" + 
				"int measurementsTimestamp1 = millis();\n" + 
				"int cycleDuration1 = "+c0+";  // cycle time\n" + 
				"void sensorReading1() {\n" + 
				"  float sensorInput = (float) "+v0+";    // <- sensor reading for first input\n" + 
				"  sensorReading(measurements1, &measurementsCount1, 128, &measurementsTimestamp1, sensorInput);\n" + 
				"}\n" + 
				"\n" + 
				"int measurementsCount2 = 0;\n" + 
				"float measurements2[128] = {};\n" + 
				"int measurementsTimestamp2 = millis();\n" + 
				"int cycleDuration2 = "+c1+"; // cycle time\n" + 
				"void sensorReading2() {\n" + 
				"  float sensorInput = (float) "+v1+";    // <- sensor reading for second input\n" + 
				"  sensorReading(measurements2, &measurementsCount2, 128, &measurementsTimestamp2, sensorInput);\n" + 
				"}\n" + 
				"\n" + 
				"int measurementsCount3 = 0;\n" + 
				"float measurements3[128] = {};\n" + 
				"int measurementsTimestamp3 = millis();\n" + 
				"int cycleDuration3 = "+c2+";  // cycle time\n" + 
				"void sensorReading3() {\n" + 
				"  float sensorInput = (float) "+v2+";    // <- sensor reading for third input \n" + 
				"  sensorReading(measurements3, &measurementsCount3, 128, &measurementsTimestamp3, sensorInput);\n" + 
				"}\n" + 
				"\n" + 
				"void configWebInterface() {\n" + 
				"  // Graph 1\n" + 
				"  String name1 = "+s0+";\n" + 
				"  String unit1 = "+u0+";\n" + 
				"  int good1 = "+l1+";\n" +
				"  int bad1 = "+l2+";\n" + 
				"  int min1 = "+pmi0+"; // lower limit plot\n" + 
				"  int max1 = "+pma0+"; // upper limit plot\n" + 
				"  int stepsize1 = "+pd0+"; // delta grid \n" + 
				"  int cycleStepsize1 = 10*"+c0+"; // cycle time \n" + 
				"  if (name1 != \"\") {\n" + 
				"    webInterface.addPlot(name1, unit1, cycleDuration1, good1, bad1, min1, max1, stepsize1, cycleDuration1, cycleStepsize1, &measurementsCount1, measurements1, &measurementsTimestamp1);\n" + 
				"  }\n" + 
				"  \n" + 
				"  // Graph 2\n" + 
				"  String name2 = "+s1+";\n" + 
				"  String unit2 = "+u1+";\n" + 
				"  int good2 = 2147483647L;// Veraenderbar, bei Smilie-Farbe genutzt\n" + 
				"  int bad2  = 2147483647L;// Veraenderbar, bei Smilie-Farbe genutzt\n" + 
				"  int min2 = "+pmi1+"; // lower limit plot\n" + 
				"  int max2 = "+pma1+"; // upper limit plot\n" + 
				"  int stepsize2 = "+pd1+"; // delta grid \n" + 
				"  int cycleStepsize2 = 10*"+c1+"; // cycle time \n" + 
				"  if (name2 != \"\") {\n" + 
				"    webInterface.addPlot(name2, unit2, cycleDuration2, good2, bad2, min2, max2, stepsize2, cycleDuration2, cycleStepsize2, &measurementsCount2, measurements2, &measurementsTimestamp2);\n" + 
				"  }\n" + 
				"  \n" + 
				"  // Graph 3\n" + 
				"  String name3 = "+s2+";\n" + 
				"  String unit3 = "+u2+";\n" + 
				"  int good3= 2147483647L;    // Veraenderbar, bei Smilie-Farbe genutzt\n" + 
				"  int bad3 = 2147483647L;    // Veraenderbar, bei Smilie-Farbe genutzt\n" + 
				"  int min3 = "+pmi2+"; // lower limit plot\n" + 
				"  int max3 = "+pma2+"; // upper limit plot\n" + 
				"  int stepsize3 = "+pd2+"; // delta grid \n" + 
				"  int cycleStepsize3 = 10*"+c2+"; // cycle time \n" + 
				"  if (name3 != \"\") {\n" + 
				"    webInterface.addPlot(name3, unit3, cycleDuration3, good3, bad3, min3, max3, stepsize3, cycleDuration3, cycleStepsize3, &measurementsCount3, measurements3, &measurementsTimestamp3);\n" + 
				"  }\n" + 
				"}\n" + 
				"void ServerHousekeeping(void) {\n" + 
				"  server.handleClient(); // server.handleClient\n" + 
				"}\n" + 
				"//----------------------- SensorPlot WebInterface Module 1/3 END -----------------------//\n" + 
				"";
				
		translator.addSetupCommand("Serial.begin(115200);");
		String WebServerSetup = "//---------------------- SensorPlot WebInterface Module 2/3 START ----------------------//\n" + 
				"  configWebInterface();\n" + 
                "  webInterface.interfaceConfig(WebInterfaceTitel, \"Passwort\", \"Kalibrieren\");\n" + 
				"  webInterface.serverResponseSetup(&server, &calibrateCallback);\n" + 
				"  sensorReading1();\n" + 
				"  sensorReading2();\n" + 
				"  sensorReading3();\n" + 
				"  WebServerHousekeeping = ServerHousekeeping; // Matrix blockiert sonst den Webzugriff\n"+
				"//----------------------- SensorPlot WebInterface Module 2/3 END -----------------------//\n";
				
		
		translator.addSetupCommand(WebServerSetup);
		translator.addDefinitionCommand(WebServerDefinition);
   	    

		
	    String ret ="//Block------------------------------ HTML-Server\n"+
                 "  if ((millis() - measurementsTimestamp1) > (cycleDuration1 * 1000)) {\n" + 
                 "    sensorReading1();\n" + 
                 "  }\n" + 
                 "  if ((millis() - measurementsTimestamp2) > (cycleDuration2 * 1000)) {\n" + 
                 "    sensorReading2();\n" + 
                 "  }\n" + 
                 "  if ((millis() - measurementsTimestamp3) > (cycleDuration3 * 1000)) {\n" + 
                 "    sensorReading3();\n" + 
                 "  }\n" + 
                 "  server.handleClient();\n" + 
                 "  //----------------------- SensorPlot WebInterface Module 3/3 END -----------------------//\n" + 
                 "  \n" + 
                 "  server.begin();// Server starten \n" +
                 "  delay(20);";	    	  	 
        return codePrefix + ret + codeSuffix;
	 	}
}

