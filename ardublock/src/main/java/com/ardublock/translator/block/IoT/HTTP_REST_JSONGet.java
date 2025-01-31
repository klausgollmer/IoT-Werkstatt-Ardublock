package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class HTTP_REST_JSONGet  extends TranslatorBlock {

	public HTTP_REST_JSONGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		//translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
			
		//translator.addSetupCommand("Serial.begin(115200);");
				
		String JSON="//--------------------------------------- tiny JSON Parser\n" + 
				"String parseJSON(String xml,String suchtext) {\n" + 
				"  String valStr = \"\";                // Hilfsstring\n" + 
				"\n" + 
				"  \n" + 
				"  //IOTW_PRINTLN(\"string:\"+xml);\n" + 
				"  int start,ende,doppel,ende1,ende2; // Index im Text\n" + 
				"  String antwort =\"\";\n" + 
				"  start = xml.indexOf(suchtext);     // Suche Text\n" + 
				" \n" + 
				"  if (start >= 0) {                   // Item gefunden\n" + 
				"    antwort = xml.substring(start+suchtext.length(),xml.length());\n" + 
				"   // IOTW_PRINTLN(\"antw:\"+antwort);\n" + 
				"    doppel = antwort.indexOf(':');\n" + 
				"   // IOTW_PRINTLN(\"doppel:\"+String(doppel));\n" + 
				"    \n" + 
				"    antwort = antwort.substring(doppel+1,antwort.length());\n" + 
				"   // IOTW_PRINTLN(\"antw:\"+antwort);\n" + 
				"    \n" + 
				"    ende1 =  antwort.indexOf(',');  // Ende der Zahl\n" + 
				"   // IOTW_PRINTLN(\", =\"+String(ende1));\n" + 
				"    ende2 =  antwort.indexOf('\"',1);  // Ende der Zahl\n" + 
				"   // IOTW_PRINTLN(\"# =\"+String(ende2));\n" + 
				"    if (ende1 >= 0) {\n" + 
				"      ende = ende1;\n" + 
				"    } else {\n" + 
				"      ende = ende2;\n" + 
				"    }\n" + 
				"    valStr= antwort.substring(0,ende);// itemtext\n" + 
				"    valStr.replace(\"\\\"\",\" \");          // delete ggf. vorhandene \"\n" + 
				"    valStr.trim();\n" + 
				"  } \n" + 
				"  else                             // Item nicht gefunden\n" + 
				"  IOTW_PRINT(\"error - no such item: \"+suchtext);\n" + 
				"  return valStr;\n" + 
				"}\n" + 
				"";
  	    translator.addDefinitionCommand(JSON);

		String json,key;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    json = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    key = translatorBlock.toCode();
	    
	    String ret = "parseJSON("+json+","+key+")";
        return codePrefix + ret + codeSuffix;
	 	}
}

