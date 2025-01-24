package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class AI_EDGE_ClassifyDeterm  extends TranslatorBlock {

	public AI_EDGE_ClassifyDeterm (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
     //   translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
	//	translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		translator.addHeaderFile("IoTW_EI.h");
		
		/*
		String EI_Def ="// ---- EDGE AI data \n" + 
				"int     EI_NumSens=0,EI_Index=0;\n" + 
				"float   EI_Datenfeld[EI_MAXPOINTS][EI_MAXSENSOR]; \n" +
				"int     AI_Datentyp[EI_MAXPOINTS];\n"+
				"String  EI_nameOfSensor[EI_MAXSENSOR];\n" + 
				"String  EI_unitOfSensor[EI_MAXSENSOR];\n" + 
				"";
			translator.addDefinitionCommand(EI_Def);
		
		*/
	
		
		String fun = "// -------- EDGE AI Nearest Neighbour Classification\n" + 
				"int AI_ClassCalculate (float s1,float s2,float s3, int EI_NumSens,int norm,float rej) {\n" + 
				"  // -------- Nearest Neighbour Classification\n" + 
				"  float pattern[EI_MAXSENSOR];\n" + 
				"  float dist = 0., Betrag = 0.;\n" + 
				"  float sse[EI_MAXPOINTS];\n" + 
				"  float ref[EI_MAXSENSOR];\n" + 
				"  int i_min=0,final_class;\n" + 
				"\n" + 
				"  pattern[1]=s1; // zu klassifizierendes Muster\n" + 
				"  pattern[2]=s2;\n" + 
				"  pattern[3]=s3;\n" + 
				"\n" + 
				"  Serial.print(\"\\nPattern:\\n\"); \n" + 
				"  if (norm==1) {   // Normiere Pattern auf Einheitsvektor (Länge 1)\n" + 
				"    for (int j = 1; j < EI_NumSens; j++) {\n" + 
				"     dist += pattern[j]*pattern[j];\n" + 
				"    }\n" + 
				"    Betrag = sqrt(dist);\n" + 
				"  } else Betrag = 1.;\n" + 
				"  \n" + 
				"  for (int j = 1; j < EI_NumSens; j++) {\n" + 
				"    pattern[j] = pattern[j]/Betrag;\n" + 
				"    Serial.printf(\" %6.2f \",(pattern[j]));\n" + 
				"  }\n" + 
				"  \n" + 
				"  Serial.println(\"\\nClassify:\"); \n" + 
				"  for (int i = 0; i < EI_Index; i++) {\n" + 
				"    dist   = 0.; Betrag = 0.;\n" + 
				"    for (int j = 1; j < EI_NumSens; j++) {// Referenzmuster erzeugen\n" + 
				"      ref[j]=  EI_Datenfeld[i][j];\n" + 
				"      dist += ref[j]*ref[j];\n" + 
				"    }\n" + 
				"    if (norm == 1) { // normieren\n" + 
				"      for (int j = 1; j < EI_NumSens; j++) {\n" + 
				"        ref[j]= ref[j]/sqrt(dist);\n" + 
				"      }\n" + 
				"    }\n" + 
				"    dist = 0;     // Abstand zum Referenzmuster ermitteln\n" + 
				"    for (int j = 1; j < EI_NumSens; j++) {\n" + 
				"      dist += (ref[j] - pattern[j]) * (ref[j] - pattern[j]);\n" + 
				"      Serial.printf(\" %6.2f \",(ref[j]));\n" + 
				"    }\n" + 
				"    sse[i] = dist;\n" + 
				"  \n" + 
				"    Serial.print(\" i=\"+String(i)+ \" class=\"+String(EI_Datenfeld[i][0])+String(\" sse=\") + String(sse[i])+ String(\"\\n\")); \n" + 
				"    if (sse[i] < sse[i_min]) {   // Minimum suchen\n" + 
				"      i_min = i;\n" + 
				"    }\n" + 
				"  }\n" + 
 			    "  final_class = EI_Datenfeld[i_min][0];\n" + 
				"  if (sse[i_min] > rej) final_class = 0; // Rejection \n"+
				"  Serial.println(\"Minimum: index = \"+String(i_min)+String(\"-> Class= \")+String(final_class));\n" + 
				"  return final_class;\n" + 
				"}\n" + 
				""; 

		translator.addDefinitionCommand(fun);

		//translator.addSetupCommand("Serial.begin(115200);");

		String v1,v2,v3,norm,rej;

	    String Code="AI_ClassCalculate("; 

		
	   	String no="2";
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    v1 = translatorBlock.toCode();
        Code+=v1;
	    translatorBlock = this.getTranslatorBlockAtSocket(1);
	    if (translatorBlock!=null) {
	       v2 = translatorBlock.toCode();
	       Code+=","+v2;
	       no="3";
	    } else Code+=",0";
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(2);
	    if (translatorBlock!=null) {
	       v3 = translatorBlock.toCode();
	       Code+=","+v3;
	       no="4";
	    } else Code+=",0";
		
	    Code+=","+no;
	
        translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    if (translatorBlock!=null) {
	       norm = translatorBlock.toCode();
		   Code+=","+norm;
	    }
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(4);
	    if (translatorBlock!=null) {
	       rej = translatorBlock.toCode();
		   Code+=","+rej+")";
	    }
	
	    return codePrefix + Code + codeSuffix;
	 	}
}

