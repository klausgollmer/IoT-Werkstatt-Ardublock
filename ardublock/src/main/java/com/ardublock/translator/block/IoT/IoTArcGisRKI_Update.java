package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTArcGisRKI_Update  extends TranslatorBlock {

	public IoTArcGisRKI_Update (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		translator.addHeaderFile("WiFiClientSecureBearSSL.h");
		translator.addSetupCommand("Serial.begin(115200);");
		
	
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
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    key = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    feld = translatorBlock.toCode();

	    String finger = "{0x00}"; String len = "1";
	    translatorBlock = this.getTranslatorBlockAtSocket(2);
	    
	    if (translatorBlock!=null) {
  	      finger = translatorBlock.toCode();
	      finger = finger.substring(1,finger.length()-1);
          finger = finger.replaceAll(":",",0x");
          finger = "{0x"+finger+"}";        
          len = "20";
	    }
	    
	    
	    String RKIUpdate = "float getArcGisRKI_UpdateData(int AdmUnitId, String Field) {\n" + 
	    		"  // --------------------------------------RKI REST API\n" + 
	    		"  // Quellenvermerk: Robert Koch-Institut (RKI), dl-de/by-2-0\n" + 
	    		"  // https://arcgis.esri.de/nutzung-der-api-des-rki-covid-19-dashboard/"+ 
	    		"  // AdmUnitID der Landkreise https://www.arcgis.com/home/item.html?id=c093fe0ef8fd4707beeb3dc0c02c3381#data\n" + 
	    		"  // Fields https://npgeo-corona-npgeo-de.hub.arcgis.com/datasets/917fc37a709542548cc3be077a786c17_0/geoservice?geometry=-19.357%2C46.211%2C41.375%2C55.839\n" + 
	    		"  const uint8_t fingerprint["+len+"] = " + finger + ";\n"+
	    		"  String cmd = \"/mOBPykOjAyBO2ZKk/ArcGIS/rest/services/rki_key_data_v/FeatureServer/0/query?where=AdmUnitId+%3D\"+String(AdmUnitId)+\"%20AND%20AdmUnitId%20%3C%3D%20\"+String(AdmUnitId)+\"&outFields=\"+Field+\"&f=pgeojson&token=\";\n" + 
	    		"  String host = " + host +";\n" + 
	    		"  String json= \" \";\n" + 
	    		"  float  val = -1; // Default, Kommunikationsproblem\n" + 
	    		"  int ok = httpsGET(host,cmd,json,fingerprint);// und absenden \n" + 
	    		"  if (ok) {\n" + 
	    		"    int istart = json.lastIndexOf(':')+1;\n" + 
	    		"    int iend   = json.indexOf('}',istart);\n" + 
	    		"    String txt = json.substring(istart,iend);\n" + 
	    		"    val = txt.toFloat();\n" + 
	    		"  }else { \n" +
	    		"    RKI_LastTimestamp = \"outdated\"; // Last update   RKI COVID 19 Dashboard\n" + 
	    		"  }"+
	    		"  return val;\n" + 
	    		"}";
		translator.addDefinitionCommand("String   RKI_LastTimestamp = \"No Data\"; // Last update   RKI COVID 19 Dashboard");
		translator.addDefinitionCommand("uint32_t RKI_NextUpdate    = millis();    // Next status update RKI");
	    translator.addDefinitionCommand(RKIUpdate);
			    
	    ret = "getArcGisRKI_UpdateData("+key+","+feld+")";

        return codePrefix + ret + codeSuffix;
	 	}
}

