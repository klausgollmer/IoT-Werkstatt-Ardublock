package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Sen_BME680VectorIAQ extends TranslatorBlock
{

  public Sen_BME680VectorIAQ (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }


	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
	    translator.addDefinitionCommand("double IAQ_filter = 25.0;\n");

	    String IAQ = "float boschBME680_IAQ() {\r\n" + 
	    		"  float alpha=0.5; // 0.1...1 (strong fiter .... no filter)\r\n" + 
	    		"  float gas_lower_limit = 5000. ;\r\n" + 
	    		"  float gas_upper_limit = 50000. ;\r\n" + 
	    		"  float gas = boschBME680.readGas();\r\n" + 
	    		"  String text;\r\n" + 
	    		"  //IOTW_PRINT(\"gas:\"+String(gas));\r\n" + 
	    		"  if (gas > gas_upper_limit) gas = gas_upper_limit;\r\n" + 
	    		"  if (gas < gas_lower_limit) gas = gas_lower_limit;\r\n" + 
	    		"  float IAQ_score = 500*(1.-(gas-gas_lower_limit)/(gas_upper_limit-gas_lower_limit));\r\n" + 
	    		"  IAQ_filter = IAQ_score*alpha+IAQ_filter*(1-alpha);\r\n" + 
	    		"\r\n" + 
	    		"  //IOTW_PRINT(\" score:\"+String(IAQ_score));\r\n" + 
	    		"  //IOTW_PRINT(\" filter:\"+String(IAQ_filter));\r\n" + 
	    		"  //IOTW_PRINTLN();\r\n" + 
	    		"    switch ((int) IAQ_filter) {\r\n" + 
	    		"    case 301 ... 500:\r\n" + 
	    		"       text = \"Hazardous\" ;\r\n" + 
	    		"       break;\r\n" + 
	    		"    case 201 ... 300:\r\n" + 
	    		"       text = \"Very Unhealthy\" ;\r\n" + 
	    		"       break;\r\n" + 
	    		"    case 176 ... 200:\r\n" + 
	    		"       text = \"Unhealthy\" ;\r\n" + 
	    		"       break;\r\n" + 
	    		"    case 151 ... 175:\r\n" + 
	    		"       text = \"Unhealthy for Sensitive Groups\" ;\r\n" + 
	    		"       break;\r\n" + 
	    		"    case 51 ... 150:\r\n" + 
	    		"       text = \"Moderate\" ;\r\n" + 
	    		"       break;\r\n" + 
	    		"    case 0 ... 50:\r\n" + 
	    		"       text = \"Good\" ;\r\n" + 
	    		"       break;\r\n" + 
	    		"   }\r\n" + 
	    		"   IOTW_PRINTLN(text); \r\n" + 
	    		"   return IAQ_filter;\r\n" + 
	    		"}";
	    
	    translator.addDefinitionCommand(IAQ);
	    
		String ret = "boschBME680_IAQ()";
		return codePrefix + ret + codeSuffix;
	}
}