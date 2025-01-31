package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class AI_EDGE_IMPULSE_classify  extends TranslatorBlock {

	public AI_EDGE_IMPULSE_classify (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
     //   translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
	//	translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266HTTPClient.h> \n#elif defined(ESP32) \n #include <HTTPClient.h>\n#endif\n");
		translator.addHeaderFile("esp32_test_inferencing.h");
		
		String EI_Def ="// ---- EDGE IMPULSE definitions  \n"
				+ "// static float features[EI_CLASSIFIER_DSP_INPUT_FRAME_SIZE];\n"
				+ "// const int sampling_interval_ms = 20; // Anpassen entsprechend des Edge-Impulse-Projekts (z. B. 50 Hz = 20 ms)\n"
				+ "\n"
				+ "// Zugriff auf das gleiche Speicherfeld\n"
				+ "float* features = reinterpret_cast<float*>(EI_Datenfeld);\n"
				+ "\n"
				+ "int raw_feature_get_data(size_t offset, size_t length, float *out_ptr) {\n"
				+ "    memcpy(out_ptr, features + offset, length * sizeof(float));\n"
				+ "    return 0;\n"
				+ "}\n";			
		translator.addDefinitionCommand(EI_Def);
		
		String fun = "// -------- EDGE IMPULSE Classification\n"
				+ "int EI_classification(float reject) {\n"
				+ "\n"
				+ "  int max_index = 0;\n"
				+ "  // Überprüfe die Größe des Feature-Puffers\n"
				+ "  if (EI_Index*EI_MAXSENSOR != EI_CLASSIFIER_DSP_INPUT_FRAME_SIZE) {\n"
				+ "    IOTW_PRINT(F(\"Die Größe des 'features'-Arrays ist nicht korrekt. Erwartet:\"));\n"
				+ "    IOTW_PRINTF(\"%lu Elemente, aber hatte: %lu\\n\",\n"
				+ "    EI_CLASSIFIER_DSP_INPUT_FRAME_SIZE, EI_Index*EI_MAXSENSOR);\n"
				+ "    return (-3);\n"
				+ "  }"
				+ "  // Führe Inferenz aus\n"
				+ "  ei_impulse_result_t result = {0};\n"
				+ "  signal_t features_signal;\n"
				+ "  features_signal.total_length = EI_Index*EI_MAXSENSOR;\n"
				+ "  features_signal.get_data = &raw_feature_get_data;\n"
				+ "\n"
				+ "  EI_IMPULSE_ERROR res = run_classifier(&features_signal, &result, false /* Debug-Ausgabe */);\n"
				+ "  if (res != EI_IMPULSE_OK) {\n"
				+ "    IOTW_PRINT(F(\"FEHLER: Klassifizierer konnte nicht ausgeführt werden\"));\n"
				+ "    IOTW_PRINTLN(res);\n"
				+ "    max_index = -2;\n"
				+ "  }\n"
				+ "\n"
				+ "  IOTW_PRINTF(\"Zeiten: DSP %d ms, Inferenz %d ms, Anomalie %d ms\\n\",\n"
				+ "  result.timing.dsp, result.timing.classification, result.timing.anomaly);\n"
				+ "\n"
				+ "  IOTW_PRINTLN(F(\"Vorhersagen:\"));\n"
				+ "  for (uint8_t i = 0; i < EI_CLASSIFIER_LABEL_COUNT; i++) {\n"
				+ "    if (result.classification[i].value > result.classification[max_index].value){\n"
				+ "      max_index = i;\n"
				+ "    }\n"
				+ "  }\n"
				+ "  IOTW_PRINTF(\"  %s: %.5f\\n\", ei_classifier_inferencing_categories[max_index], result.classification[max_index].value);\n"
				+ "\n"
				+ "  //Auf Anomalien überprüfen\n"
				+ "#if EI_CLASSIFIER_HAS_ANOMALY\n"
				+ "  IOTW_PRINT(F(\"Anomalie-Vorhersage:\"));\n"
				+ "  IOTW_PRINTLN(result.anomaly);\n"
				+ "#endif\n"
				+ "\n"
				+ "  //Abweisung testen\n"
				+ "  if (result.classification[max_index].value < reject) {\n"
				+ "     IOTW_PRINT(F(\"Rejection\"));\n"
				+ "     return (-1);\n"
				+ "  }  \n"
				+ "  return max_index+1;\n"
				+ "}";
		translator.addDefinitionCommand(fun);

		//translator.addSetupCommand("Serial.begin(115200);");

		String rej = " INFINITY";
	   	
		TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(0);
		if (translatorBlock!=null)
	    	    rej = translatorBlock.toCode();
	    return codePrefix + "EI_classification("+rej+")" + codeSuffix;
	    
	    
	 	}
}

