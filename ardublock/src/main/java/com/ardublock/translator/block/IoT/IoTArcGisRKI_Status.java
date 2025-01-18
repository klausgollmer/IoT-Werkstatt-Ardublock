package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTArcGisRKI_Status  extends TranslatorBlock {

	public IoTArcGisRKI_Status (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		translator.addHeaderFile("WiFiClientSecureBearSSL.h");
		//translator.addSetupCommand("Serial.begin(115200);");
		
	
		String httpGET ="//--------------------------------------- https-GET\n" + 
				 "int httpsGET(String host, String cmd, String &antwort, const uint8_t fingerprint[] ) {\n" + 
				 "  int ok = 0;\n" + 
				 "  std::unique_ptr<BearSSL::WiFiClientSecure>client(new BearSSL::WiFiClientSecure);\n" + 
				 "  String message = host+cmd;\n" + 
				 "  if (sizeof(fingerprint) > 4) { // Validiere SHA-1 Fingerprint\n" + 
				 "     client->setFingerprint(fingerprint);\n" + 
				 "  } else { // keine Validierung, Achtung Sicherheitslücke\n" + 
				 "     client->setInsecure();\n" + 
				 "  }\n" + 
				 "  HTTPClient https;\n" + 
				 "  // Serial.println(message);\n" + 
				 "  if (https.begin(*client, message)){  // HTTPS\n" + 
				 "    // start connection and send HTTP header\n" + 
				 "    int httpCode = https.GET();\n" + 
				 "    // httpCode will be negative on error\n" + 
				 "    if (httpCode > 0) {\n" + 
				 "      // HTTP header has been send and Server response header has been handled\n" + 
				 "      String payload = https.getString();\n" + 
				 "      antwort = payload;\n" + 
				 "      // Serial.println(payload);\n" + 
				 "      // file found at server\n" + 
				 "      if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {\n" + 
				 "        ok = 1;\n" + 
				 "      }\n" + 
				 "    } \n" + 
				 "    else {\n" + 
				 "      Serial.printf(\"[HTTPS] GET... failed, error: %s\\n\", https.errorToString(httpCode).c_str());\n" + 
				 "    }\n" + 
				 "    https.end();\n" + 
				 "  } \n" + 
				 "  else {\n" + 
				 "    Serial.printf(\"[HTTPS] Unable to connect\\n\");\n" + 
				 "  }\n" + 
				 "  return ok;\n" + 
				 "}";
		translator.addDefinitionCommand(httpGET);
		
		String key,feld,ret;
		TranslatorBlock translatorBlock;
		String host = "\"https://services7.arcgis.com\"";
		
	    String finger = "{0x00}"; String len = "1";
	    translatorBlock = this.getTranslatorBlockAtSocket(0);
	    
	    if (translatorBlock!=null) {
  	      finger = translatorBlock.toCode();
	      finger = finger.substring(1,finger.length()-1);
          finger = finger.replaceAll(":",",0x");
          finger = "{0x"+finger+"}";        
          len = "20";
	    }
	
	    String RKIStatus = "int getArcGisRKI_UpdateStatus() {\n" + 
	    		"  // --------------------------------------RKI REST API\n" + 
	    		"  // Quellenvermerk: Robert Koch-Institut (RKI), dl-de/by-2-0\n"+
	    		"  // https://arcgis.esri.de/nutzung-der-api-des-rki-covid-19-dashboard/"+ 
	    		"  const uint8_t fingerprint["+len+"] = " + finger + ";\n"+
	    	    "  String cmd = \"/mOBPykOjAyBO2ZKk/ArcGIS/rest/services/rki_service_status_v/FeatureServer/0/query?where=1%3D1&objectIds=3&time=&resultType=none&outFields=Timestamp_txt&f=pgeojson&token=\";\n" + 
	    		"  String host = " + host + ";\n" + 
	    		"  const uint8_t fingerprint["+len+"] = " + finger + ";\n"+
	    		"  String json= \" \";\n" + 
	    		"  String txt = \"No RKI-Data\";\n" + 
	    		"  int    change = 0; \n" + 
			    "  if (millis()> RKI_NextUpdate) {\n" + 
			    "    int ok = httpsGET(host,cmd,json,fingerprint);// und absenden \n" + 
			    "    if (ok) {\n" + 
			    "      RKI_NextUpdate = millis()+(300000L);\n" + 
			    "      int iend = json.lastIndexOf('\"');\n" + 
			    "      int istart = json.indexOf('\"',iend-23);\n" + 
			    "      txt = json.substring(istart+1,iend);\n" + 
			    "      if (txt != RKI_LastTimestamp)\n" + 
			    "        change = 1;\n" + 
			    "      RKI_LastTimestamp = String(\" Update:\")+txt;\n" + 
			    "    } \n" + 
			    "  }\n" + 
			    "  return change;\n" + 
			    "}\n" + 
			    "";
			    
		translator.addDefinitionCommand("String   RKI_LastTimestamp = \"No Data\"; // Last update   RKI COVID 19 Dashboard");
		translator.addDefinitionCommand("uint32_t RKI_NextUpdate    = millis();    // Next status update RKI");

		translator.addDefinitionCommand(RKIStatus);
			    
	    ret = "getArcGisRKI_UpdateStatus()";

        return codePrefix + ret + codeSuffix;
	 	}
}

