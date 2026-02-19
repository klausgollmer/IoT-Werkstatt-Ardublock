package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtOut_SmartServoStart extends TranslatorBlock
{

  public ExtOut_SmartServoStart (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
   
    // Header hinzuf�gen
    translator.addHeaderFile("IoTW_SmartServo.h");
    translator.addHeaderFile("#define SmartServo_START");
    String Dis= "// ----------------------------------------------------SmartServo-------------\n"
    		+   "// IoTW_SmartServo Lib uses the following libraries: \n"
    		+   "// dynamixel2arduino from ROBOTIS, Apache-2.0 license, https://github.com/ROBOTIS-GIT/Dynamixel2Arduino\n"
    		+   "// FTservo from Feetech, MIT license, https://github.com/ftservo/FTServo_Arduino\n"
    		+   "// SmartServo_set(type,mode,id,value)\n"
    		+   "//   mode 0=GoalPositionDegree, 1=TorqueEnable, 2=Max. Torque, 3=Max Speed, 4=GoalPositionUnit, 5=Wheel, 7=new ID\n"
            +   "// SmartServo_get(type,mode,id)\n"
    		+   "//   mode 0=CurrentPositionDegree,1=TorqueEnable, 2=actual Load, 3=isMoving, 4=Temperature, 5=CurrentpositionUnit, 6=Ping\n ";
       		
	translator.addDefinitionCommand("int IOTW_debug_level = IOTW_DEBUG_LEVEL; // Debug print auch in den IOTW_ Libs nutzen\n");
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String type = translatorBlock.toCode();

	translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String serial = translatorBlock.toCode();
    
	translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
    String rx = translatorBlock.toCode();
    
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
    String tx = translatorBlock.toCode();

    translatorBlock = this.getRequiredTranslatorBlockAtSocket(4);
    String baud = translatorBlock.toCode();

    
    translator.addSetupCommand(Dis);
    String Setup ="#ifdef ESP32\r\n"
    		+ "#ifndef SmartServo_START\"   \r\n"
    		+ "  SmartServo_start(1,2,16,17,1000000);// default: feetech serial 2\r\n"
    		+ "#endif\r\n"
    		+ "#else\r\n"
    		+ "  IOTW_PRINTLN(F(\"smart Servo ESP32 only\"));\r\n"
    		+ "#endif";
    translator.addSetupCommand(Setup);

    // Code von der Mainfunktion
	ret = "SmartServo_start("+type+","+serial+","+rx+","+tx+","+baud+");";
	
   
    return codePrefix + ret + codeSuffix;
  }
}