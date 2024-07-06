package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTArcGisRKI  extends TranslatorBlock {

	public IoTArcGisRKI (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
				"    std::unique_ptr<BearSSL::WiFiClientSecure>client(new BearSSL::WiFiClientSecure);\n" + 
				"    String message = \"https://\"+host+cmd;\n" + 
				"    client->setFingerprint(fingerprint);\n" + 
				"    HTTPClient https;\n" + 
				"\n" + 
				"    //Serial.print(\"[HTTPS] begin...\\n\");\n" + 
				"    Serial.println(message);\n" + 
				"    if (https.begin(*client, message)){  // HTTPS\n" + 
				"      //Serial.print(\"[HTTPS] GET...\\n\");\n" + 
				"      // start connection and send HTTP header\n" + 
				"      int httpCode = https.GET();\n" + 
				"      // httpCode will be negative on error\n" + 
				"      if (httpCode > 0) {\n" + 
				"        // HTTP header has been send and Server response header has been handled\n" + 
				"        //Serial.printf(\"[HTTPS] GET... code: %d\\n\", httpCode);\n" + 
				"\n" + 
				"        // file found at server\n" + 
				"        if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {\n" + 
				"          String payload = https.getString();\n" + 
				"          antwort = payload;\n" + 
				"          Serial.println(payload);\n" + 
				"        }\n" + 
				"      } else {\n" + 
				"        Serial.printf(\"[HTTPS] GET... failed, error: %s\\n\", https.errorToString(httpCode).c_str());\n" + 
				"      }\n" + 
				"\n" + 
				"      https.end();\n" + 
				"    } else {\n" + 
				"      Serial.printf(\"[HTTPS] Unable to connect\\n\");\n" + 
				"    }\n" + 
				"  }";
				translator.addDefinitionCommand(httpGET);
		

				
	    String AskRKI = "float getArcGisRKI(int AdmUnitId, String Field) {\n" + 
	    		"  // --------------------------------------RKI REST API\n" + 
	    		"  // AdmUnitID der Landkreise https://www.arcgis.com/home/item.html?id=c093fe0ef8fd4707beeb3dc0c02c3381#data\n" + 
	    		"  // Fields https://npgeo-corona-npgeo-de.hub.arcgis.com/datasets/917fc37a709542548cc3be077a786c17_0/geoservice?geometry=-19.357%2C46.211%2C41.375%2C55.839\n" + 
	    		"  \n" + 
	    		"    const uint8_t fingerprint[20] ={\n" + 
	    		"      0xE2,0x60,0xC5,0xC1,0x2F,0xE0,0xB8,0xAB,0x21,0x05,0x2C,0xC6,0x7A,0xB6,0xA8,0x85,0xC2,0x76,0x0F,0x85    };\n" + 
	    		"    String cmd = \"/mOBPykOjAyBO2ZKk/arcgis/rest/services/RKI_Landkreisdaten/FeatureServer/0/query?where=AdmUnitId%20%3E%3D%20\"+String(AdmUnitId-1)+\"%20AND%20AdmUnitId%20%3C%3D%20\"+String(AdmUnitId)+\"&outFields=\"+Field+\"&returnGeometry=false&f=json\";\n" + 
	    		"    String host = \"services7.arcgis.com\";\n" + 
	    		"    String json= \" \";\n" + 
	    		"    httpsGET(host,cmd,json,fingerprint);// und absenden \n" + 
	    		"    int istart = json.lastIndexOf(':')+1;\n" + 
	    		"    int iend   = json.indexOf('}',istart);;\n" + 
	    		"    String txt = json.substring(istart,iend);\n" + 
	    		"    float val = txt.toFloat();\n" + 
	    		"    Serial.println(txt);\n" + 
	    		"    return val;\n" + 
	    		"}\n" + 
	    		"";
	    translator.addDefinitionCommand(AskRKI);
		
		String key,host,feld,ret,wert;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    host = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    String finger = translatorBlock.toCode();
	    finger = finger.substring(1,finger.length()-1);
        finger = finger.replaceAll(":",",0x");
        finger = "{0x"+finger+"}";        

        
        
        
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    key = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    feld = translatorBlock.toCode();
	    
	    ret = "getArcGisRKI("+key+","+feld+")";

        return codePrefix + ret + codeSuffix;
	 	}
}

