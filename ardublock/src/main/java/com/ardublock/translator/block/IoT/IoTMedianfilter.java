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
  
    String MedianCode = "// ---------------------- simple median filter with 3 values \n"
    		+ "float findMedian(float v1, float v2, float v3) { \r\n"
    		+ "    if ((v1 <= v2 && v2 <= v3) || (v3 <= v2 && v2 <= v1)) return v2;\r\n"
    		+ "    if ((v2 <= v3 && v3 <= v1) || (v1 <= v3 && v3 <= v2)) return v3;\r\n"
    		+ "    return v1;\r\n"
    		+ "}\r\n"
    		+ "";
    translator.addDefinitionCommand(MedianCode);		
	
    String FilterCode = "// ---------------------- medianfilter with sample and retry, eliminate outlayer \n"
    		+ "float readMedian_"+myfun+"(float minValue, float maxValue, int maxCount) { \n"
    		+ "  float data[3] = {maxValue,maxValue,maxValue};\r\n"
    		+ "  int tryout;\r\n"
    		+ "  for (int i=0;i<=2;i++) { // calculate median of 3 measurements, each maxCount retry\r\n"
    		+ "    tryout = maxCount;\r\n"
    		+"     if (i>0) { \n"
    		+ "    // pause between each measurement \r\n"
    		+             Pause +"\n"
    		+ "    }\n"
      		+ "    // now take a sample (maxCount retry) \r\n"
    		+ "    while ((isnan(data[i]) || (data[i] >= maxValue) || (data[i] <= minValue)) && (tryout > 0)) {  // test out of range \r\n"
    		+ "      data[i]="+code+";\r\n"
    		+ "      tryout--;\r\n"
    		+ "    }\r\n"
    		+ "  }\r\n"
    		+ "  return findMedian(data[0],data[1],data[2]); // return the median\r\n"
    		+ "}";
    translator.addDefinitionCommand(FilterCode);				
    
	
    // Code von der Mainfunktion
	ret = "readMedian_"+myfun+"("+min+","+max+",3)";
    return codePrefix + ret + codeSuffix;
  }
}