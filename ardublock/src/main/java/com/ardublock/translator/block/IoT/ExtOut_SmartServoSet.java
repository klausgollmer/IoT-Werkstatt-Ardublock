package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtOut_SmartServoSet extends TranslatorBlock
{

  public ExtOut_SmartServoSet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
    translator.addHeaderFile("IoTW_SmartServo.h");
    translator.addDefinitionCommand("int IOTW_debug_level = IOTW_DEBUG_LEVEL; // Debug print auch in den IOTW_ Libs nutzen\n");     
   
    // Header hinzuf�gen
    translator.addHeaderFile("IoTW_SmartServo.h");
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String id = translatorBlock.toCode();

    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String mode = translatorBlock.toCode();
    
	translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
    String value = translatorBlock.toCode();

    int type = -1;
    
    String Setup ="#ifdef ESP32\r\n"
    		+ "#ifndef SmartServo_START\"   \r\n"
    		+ "  SmartServo_start(1,2,16,17,1000000);// default: feetech serial 2\r\n"
    		+ "#endif\r\n"
    		+ "#else\r\n"
    		+ "  IOTW_PRINTLN(F(\"smart Servo ESP32 only\"));\r\n"
    		+ "#endif";
    translator.addSetupCommand(Setup);

    
    // Code von der Mainfunktion
	ret = "// SmartServo_set (type, id, mode, value)\r\n"
	    + "SmartServo_set("+type+","+id+","+mode+","+value+");\n";
	
   
    return codePrefix + ret + codeSuffix;
  }
}