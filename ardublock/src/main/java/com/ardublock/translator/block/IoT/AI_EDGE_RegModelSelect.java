package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class AI_EDGE_RegModelSelect  extends TranslatorBlock {

	public AI_EDGE_RegModelSelect (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		translator.addHeaderFile("IoTW_EI.h");
		translator.addHeaderFile("curveFitting.h");
	/*	
		String EI_Def ="// ---- EDGE AI data \n" + 
				"int     EI_NumSens=0,EI_Index=0;\n" + 
				"float   EI_Datenfeld[IOTW_EI_MAXPOINTS][EI_MAXSENSOR]; \n" +
				"int     AI_Datentyp[IOTW_EI_MAXPOINTS];\n"+
				"String  EI_nameOfSensor[EI_MAXSENSOR];\n" + 
				"String  EI_unitOfSensor[EI_MAXSENSOR];\n" + 
				"";
			translator.addDefinitionCommand(EI_Def);
	*/
		
		String EI_Def ="// ---- EDGE AI regression model data \n" + 
				"double  AI_RegPara [AI_MAXMODEL][AI_MAXMODELPARA];\n" + 
				"int     AI_RegPara_valid[AI_MAXMODEL];"+
				"int     AI_RegParaBestModeltyp;\n" + 
				"";
				translator.addDefinitionCommand(EI_Def);
		//translator.addSetupCommand("Serial.begin(115200);");

	       String fun = "// --------- AI Regession Model \n" + 
	        		"float AI_RegModelCalculate(float x1,float x2, float x3, int no, int modeltyp) {\n" + 
	        		"  float y = 0;\n" + 
	        		"  if (no > 1) IOTW_PRINT(\"\\nonly one input dimension in ModelCalculate jet\");\n" + 
	        		"  if (modeltyp == AI_MODEL_AUTO){\n" + 
	        		"    modeltyp = AI_RegParaBestModeltyp;\n" + 
	        		"    IOTW_PRINTLN(\"Model:\"+String(modeltyp));\n" + 
	        		"  }\n" + 
	        		"  if (AI_RegPara_valid[modeltyp] == 0) {\n" + 
	        		"    IOTW_PRINTF(\"\\nNo calculation for type %i, start parameter calibration first\",modeltyp);  \n" + 
	        		"  } \n" + 
	        		"  else {  // use parameter for calculation    \n" + 
	        		"    switch (modeltyp) {\n" + 
	        		"    case AI_MODEL_LIN:\n" + 
	        		"      y = AI_RegPara[modeltyp][0]*x1+AI_RegPara[modeltyp][1];\n" + 
	        		"      break;\n" + 
	        		"    case AI_MODEL_QUAD: \n" + 
	        		"      y = (AI_RegPara[modeltyp][0]*x1+AI_RegPara[modeltyp][1])*x1+AI_RegPara[modeltyp][2];\n" + 
	        		"      break;\n" + 
	        		"    case AI_MODEL_EXP: \n" + 
	        		"      y = pow(CONST_e,AI_RegPara[modeltyp][1])* exp(AI_RegPara[modeltyp][0]*x1);\n" + 
	        		"      break;\n" + 
	        		"    case AI_MODEL_CUB: \n" + 
	        		"      y = ((AI_RegPara[modeltyp][0]*x1+AI_RegPara[modeltyp][1])*x1+AI_RegPara[modeltyp][2])*x1+AI_RegPara[modeltyp][3];\n" + 
	        		"      break;\n" + 
	        		"\n" + 
	        		"    default:  \n" + 
	        		"      IOTW_PRINTF(\"\\nwrong modeltype %i,  0 <= type < %i\", modeltyp,AI_MAXMODEL );\n" + 
	        		"    }\n" + 
	        		"  }\n" + 
	        		"  return y;\n" + 
	        		"}  \n" + 
	        		"\n" + 
	        		"\n" + 
	        		"\n" + 
	        		"// --------- AI Regession Model Calibration\n" + 
	        		"void AI_RegModelCalibrateOne(int modeltyp) {\n" + 
	        		"  double  x[EI_Index];\n" + 
	        		"  double  y[EI_Index];\n" + 
	        		"  int i_cal = 0, order = 0;\n" + 
	        		"  double para [AI_MAXMODELPARA];\n" + 
	        		"  i_cal = 0;\n" + 
	        		"  for (int i = 0; i<EI_Index;i++) {\n" + 
	        		"    if (AI_Datentyp[i] == AI_CALIBRATION) { // Calibration\n" + 
	        		"      switch (modeltyp) {\n" + 
	        		"      case AI_MODEL_LIN:\n" + 
	        		"        x[i_cal] =  EI_Datenfeld[i][1];\n" + 
	        		"        y[i_cal] =  EI_Datenfeld[i][0];\n" + 
	        		"        order = 1;\n" + 
	        		"        break;\n" + 
	        		"      case AI_MODEL_QUAD: \n" + 
	        		"        x[i_cal] =  EI_Datenfeld[i][1];\n" + 
	        		"        y[i_cal] =  EI_Datenfeld[i][0];\n" + 
	        		"        order = 2;\n" + 
	        		"        break;\n" + 
	        		"      case AI_MODEL_EXP: \n" + 
	        		"        x[i_cal] =  EI_Datenfeld[i][1];\n" + 
	        		"        y[i_cal] =  log(EI_Datenfeld[i][0]);\n" + 
	        		"        order = 1;\n" + 
	        		"        break;\n" + 
	        		"      case AI_MODEL_CUB: \n" + 
	        		"        x[i_cal] =  EI_Datenfeld[i][1];\n" + 
	        		"        y[i_cal] =  EI_Datenfeld[i][0];\n" + 
	        		"        order = 3;\n" + 
	        		"        break;\n" + 
	        		"\n" + 
	        		"      default:  \n" + 
	        		"        IOTW_PRINTF(\"\\nwrong modeltype %i,  0 <= type < %i\", modeltyp,AI_MAXMODEL );\n" + 
	        		"      }\n" + 
	        		"\n" + 
	        		"      i_cal++;\n" + 
	        		"    }\n" + 
	        		"  }\n" + 
	        		"\n" + 
	        		"  //IOTW_PRINTF(\"\\nDo model calibration fitCurve using %i calibration Data available ...\",i_cal+1);\n" + 
	        		"  int ret = fitCurve(order, i_cal, x, y, order+1, para);\n" + 
	        		"\n" + 
	        		"\n" + 
	        		"  if (ret == 0){ //Returned value is 0 if no error\n" + 
	        		"    AI_RegPara_valid[modeltyp]=1;     \n" + 
	        		"    for (int p = 0; p<AI_MAXMODELPARA;p++) {\n" + 
	        		"      AI_RegPara[modeltyp][p] = para[p];\n" + 
	        		"    } \n" + 
	        		"    switch (modeltyp) {\n" + 
	        		"    case AI_MODEL_LIN:\n" + 
	        		"      IOTW_PRINTF(\"\\n y = %8.3f*x+%8.3f        \",para[0],para[1]);\n" + 
	        		"      break;\n" + 
	        		"    case AI_MODEL_QUAD: \n" + 
	        		"      IOTW_PRINTF(\"\\n y = %8.3f*x^2+%8.3f*x+%8.3f\",para[0],para[1],para[2]);\n" + 
	        		"      break;\n" + 
	        		"    case AI_MODEL_EXP: \n" + 
	        		"      IOTW_PRINTF(\"\\n y = %8.3f*exp(%8.3f*x)   \",pow(CONST_e,para[1]),para[0]);\n" + 
	        		"      break;\n" + 
	        		"\n" + 
	        		"    case AI_MODEL_CUB: \n" + 
	        		"      IOTW_PRINTF(\"\\n y = %8.3f*x^3+%8.3f*x^2+%8.3f*x+%8.3f\",para[0],para[1],para[2],para[3]);\n" + 
	        		"      break;\n" + 
	        		"    default:  \n" + 
	        		"      IOTW_PRINTF(\"\\nwrong modeltype %i,  0 <= type < %i\", modeltyp,AI_MAXMODEL );\n" + 
	        		"    }\n" + 
	        		"\n" + 
	        		"  } \n" + 
	        		"  else IOTW_PRINTF(\"\\nError fitCurve ret = %i\\n\",ret);\n" + 
	        		"}  \n" + 
	        		"\n" + 
	        		"// ------- AI Calibrate regression model\n" + 
	        		"void AI_RegModelCalibrate(int modeltyp){\n" + 
	        		"  IOTW_PRINTF(\"\\nDo model calibration using calibration data available ...\");"+
	        		"  if (modeltyp == AI_MODEL_AUTO) { // all models\n" + 
	        		"    for (int m = 0; m<AI_MAXMODEL;m++) {\n" + 
	        		"      AI_RegModelCalibrateOne(m);\n" + 
	        		"    }\n" + 
	        		"  } \n" + 
	        		"  else { // only one model\n" + 
	        		"    AI_RegModelCalibrateOne(modeltyp);\n" + 
	        		"  }\n" + 
	        		"\n" + 
	                 "IOTW_PRINTLN();"+
	        		"}\n" + 
	        		"// --------- AI Regession Model Selection\n" + 
	        		"int AI_RegModelSelect(int what){\n" + 
	        		"  float   sse[AI_MAXMODEL];\n" + 
	        		"  double  x[EI_Index];\n" + 
	        		"  double  y[EI_Index];\n" + 
	        		"  double  y_pred;\n" + 
	        		"  int m_min  = 0;\n" + 
	        		"  int i_val  = 0;\n" + 
	        		"\n" + 
	        		"  for (int i = 0; i<EI_Index;i++) {\n" + 
	        		"    if (AI_Datentyp[i] == what) { // Validation or calibration\n" + 
	        		"      x[i_val] =  EI_Datenfeld[i][1];\n" + 
	        		"      y[i_val] =  EI_Datenfeld[i][0]; \n" + 
	        		"      i_val++;\n" + 
	        		"    }\n" + 
	        		"  }\n" + 
	        		"  IOTW_PRINTF(\"\\nDo model validation using %i validation data available\",i_val);\n" + 
	        		"\n" + 
	        		"  for (int m = 0;m < AI_MAXMODEL;m++){\n" + 
	        		"    sse[m]=0;\n" + 
	        		"    for (int i = 0; i<i_val;i++) {\n" + 
	        		"      y_pred=AI_RegModelCalculate(x[i],0,0,1,m);\n" + 
	        		"      sse[m]+=((y_pred-y[i])*(y_pred-y[i]));\n" + 
	        		"    }\n" + 
	        		"    if (sse[m]<sse[m_min]) m_min = m;\n" + 
	        		"    IOTW_PRINTF(\"\\n model %i ->sse = %f\",m,sse[m]);\n" + 
	        		"  }\n" + 
	        		"  IOTW_PRINTF(\"\\nSelect the best fit model %i\\n\",m_min);\n" + 
	        		"  AI_RegParaBestModeltyp = m_min;\n" + 
	        		"  return m_min;\n" + 
	        		"}\n" + 
	        		"";
			
		
		translator.addDefinitionCommand(fun);
		
		
		
		String datatyp;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    datatyp = translatorBlock.toCode();
			    
	    String Code = "AI_RegModelSelect("+datatyp+"); // Select a regression model";
	    return codePrefix + Code + codeSuffix;
	 	}
}

