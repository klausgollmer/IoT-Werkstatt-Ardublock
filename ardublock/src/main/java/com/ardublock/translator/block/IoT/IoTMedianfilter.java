package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTMedianfilter extends TranslatorBlock
{

  public IoTMedianfilter (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
    
    // Name der Funktion zufällig, da Messdatenerfassung im code der Medianfilterfunktion
    int zufall = (int) (Math.random() * 999999) + 1; 
    String myfun=String.valueOf(zufall);
    
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String min = translatorBlock.toCode();

    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
    String max = translatorBlock.toCode();
    
    translatorBlock = this.getTranslatorBlockAtSocket(3);
    String Pause="";
    while (translatorBlock != null)
	{
		Pause = Pause + translatorBlock.toCode();
		translatorBlock = translatorBlock.nextTranslatorBlock();
	}
    
    // Header hinzuf�gen
    translator.addHeaderFile("SoftwareSerial.h");
    translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
  
    String MedianCode = "float findMedian(float v1, float v2, float v3) { // return median\r\n"
    		+ "    if ((v1 <= v2 && v2 <= v3) || (v3 <= v2 && v2 <= v1)) return v2;\r\n"
    		+ "    if ((v2 <= v3 && v3 <= v1) || (v1 <= v3 && v3 <= v2)) return v3;\r\n"
    		+ "    return v1;\r\n"
    		+ "}\r\n"
    		+ "";
    translator.addDefinitionCommand(MedianCode);		
	
    String FilterCode = "float readMedian_"+myfun+"(float minValue, float maxValue, int maxCount) { \n"
    		+ "  float data[3] = {maxValue,maxValue,maxValue};\r\n"
    		+ "  float median;\r\n"
    		+ "  int tryout;\r\n"
    		+ "  for (int i=0;i<=2;i++) {\r\n"
    		+ "    tryout = maxCount;\r\n"
    		+ "    while ((isnan(data[i]) || (data[i] >= maxValue) || (data[i] <= minValue)) && (tryout > 0)) {  // test out of range \r\n"
      		+ "    // pause between measurements \r\n"
    		+ Pause +"\n" 
    		+ "      data[i]="+code+";\r\n"
    		+ "      tryout--;\r\n"
    		+ "    }\r\n"
    		+ "  }\r\n"
    		+ "  return findMedian(data[0],data[1],data[2]);\r\n"
    		+ "}";
    translator.addDefinitionCommand(FilterCode);				
    
	
    // Code von der Mainfunktion
	ret = "readMedian_"+myfun+"("+min+","+max+",3)";
    return codePrefix + ret + codeSuffix;
  }
}