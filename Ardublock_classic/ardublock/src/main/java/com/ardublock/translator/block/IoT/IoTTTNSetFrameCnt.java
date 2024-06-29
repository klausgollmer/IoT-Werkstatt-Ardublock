package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTTTNSetFrameCnt extends TranslatorBlock
{

  public IoTTTNSetFrameCnt (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    String conv = "// Conversion long to float array in RTC - Memory\r\n" + 
			"float cnt2float(uint32_t cnt) {\r\n" + 
			"   float merk;\r\n" + 
			"   memcpy(&merk, &cnt, sizeof(uint32_t));\r\n" + 
			"   return merk;\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"uint32_t float2cnt(float merk) {\r\n" + 
			"   uint32_t cnt;\r\n" + 
			"   memcpy(&cnt, &merk, sizeof(float));\r\n" + 
			"   return cnt;\r\n" + 
			"}\r\n" + 
			"";
			
	translator.addDefinitionCommand(conv);
	
   
    // Header hinzuf�gen
    translator.addHeaderFile("lmic.h");
	translator.addHeaderFile("hal/hal.h");
	
    // Setupdeklaration
    // I2C-initialisieren
    translator.addSetupCommand("Serial.begin(115200);");
    
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
          
    // Code von der Mainfunktion
	ret = "LMIC.seqnoUp = ("+code+");// set LMIC Frame Counter\n";
    return codePrefix + ret + codeSuffix;
  }
}