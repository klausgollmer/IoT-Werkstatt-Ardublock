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
		/* old code		
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
				*/
		
		String JSON = "// ----------------------  tiny JSON Parser \n"
				+ "String parseJSON(String json, String key) {\n"
				+ "  String valStr = \"\";\n"
				+ "  int start, endPos, colonPos, commaPos, quotePos;\n"
				+ "  String remaining = \"\";\n"
				+ "\n"
				+ "  start = json.indexOf(key);  // Locate the key\n"
				+ "\n"
				+ "#if (IOTW_DEBUG_LEVEL > 1)\n"
				+ "    IOTW_PRINTF(\"\\nparseJSON: string: %s, key: %s \", json.c_str(), key.c_str());\n"
				+ "#endif\n"
				+ "  if (start >= 0) {  // Key found\n"
				+ "    remaining = json.substring(start + key.length());  // Extract substring after key\n"
				+ "\n"
				+ "    colonPos = remaining.indexOf(':');  // Locate colon\n"
				+ "    if (colonPos == -1) {\n"
				+ "     IOTW_PRINTLN(F(\"JSON ERROR: no colon found\"));\n"
				+ "     return valStr;\n"
				+ "    }\n"
				+ "      \n"
				+ "    remaining = remaining.substring(colonPos + 1);  // Extract value part\n"
				+ "      \n"
				+ "	commaPos = remaining.indexOf(',');  // Find comma (possible end)\n"
				+ "	quotePos = remaining.indexOf('\"', 1);  // Find closing quote (if exists)\n"
				+ "\n"
				+ "	// Determine the correct end position\n"
				+ "	if (commaPos != -1 && quotePos != -1) {\n"
				+ "	 endPos = min(commaPos, quotePos);  // Take the first one\n"
				+ "	} else if (commaPos != -1) {\n"
				+ "	 endPos = commaPos;\n"
				+ "	} else if (quotePos != -1) {\n"
				+ "	 endPos = quotePos;\n"
				+ "	} else {\n"
				+ "	 endPos = remaining.length();  // If neither, take the rest\n"
				+ "	}\n"
				+ "\n"
				+ "	valStr = remaining.substring(0, endPos);\n"
				+ "	 \n"
				+ "	// Trim whitespace and remove surrounding quotes if present\n"
				+ "	valStr.trim();\n"
				+ "	if (valStr.startsWith(\"\\\"\") && valStr.endsWith(\"\\\"\")) {\n"
				+ "	 valStr = valStr.substring(1, valStr.length() - 1);\n"
				+ "	}\n"
				+ "  } else {\n"
				+ "	IOTW_PRINTLN(F(\"JSON ERROR: Key not found\"));\n"
				+ "	return valStr;\n"
				+ "  }\n"
				+ "  valStr.trim();\n"
				+ "  if (valStr.endsWith(\"}\")) {\n"
				+ "	 valStr = valStr.substring(0, valStr.length() - 1);\n"
				+ "  }\n"
				+ "  #if (IOTW_DEBUG_LEVEL > 1)\n"
				+ "	  IOTW_PRINTF(\"return: %s\\n\", valStr.c_str());\n"
				+ "  #endif\n"
				+ "\n"
				+ "  return valStr;\n"
				+ "}\n"
				+ "" ;
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

