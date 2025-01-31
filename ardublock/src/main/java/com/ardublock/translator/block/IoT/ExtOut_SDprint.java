package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtOut_SDprint extends TranslatorBlock
{

  public ExtOut_SDprint (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }


	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
	    translator.addHeaderFile("SPI.h");
	    translator.addHeaderFile("SD.h");
	   	translator.addDefinitionCommand("// SD-Card lib https://github.com/arduino-libraries/SD/blob/master/examples/Datalogger/Datalogger.ino\r\n");
	   	translator.addDefinitionCommand("const int chipSelect = 15;");

	   	
	   	String SDprint = "void SDprint(String filename, String data, int CR) {"
	   			+ "// open the file. note that only one file can be open at a time,\r\n" + 
	   			"  // so you have to close this one before opening another.\r\n" + 
	   			"  File dataFile = SD.open(filename, FILE_WRITE);\r\n" + 
	   			"  // if the file is available, write to it:\r\n" + 
	   			"  if (dataFile) {\r\n" +
	   			"    if (CR) { \r\n"+
	   			"        dataFile.println(data);\r\n"+
	   	        "    } else { \r\n" + 
	   			"        dataFile.print(data);\r\n"+
	   			"	 }"+
	   			"    dataFile.close();\r\n" + 
	   			"    // print to the serial port too:\r\n" + 
	   			"  }\r\n" + 
	   			"  // if the file isn't open, pop up an error:\r\n" + 
	   			"  else {\r\n" + 
	   			"    IOTW_PRINTLN(\"error opening SD file\");\r\n" + 
	   			"  }"
	   			+ "}";
	   		   	translator.addDefinitionCommand(SDprint);
   	
	    
	    // Setupdeklaration
	    // I2C-initialisieren
	    //translator.addSetupCommand("Serial.begin(115200);");
	    
	    String initial = " if (!SD.begin(chipSelect)) {\r\n" + 
	    		"    IOTW_PRINTLN(\"initialization failed. Things to check:\");\r\n" + 
	    		"    IOTW_PRINTLN(\"1. is a card inserted?\");\r\n" + 
	    		"    IOTW_PRINTLN(\"2. is your wiring correct?\");\r\n" + 
	    		"    IOTW_PRINTLN(\"3. did you change the chipSelect pin to match your shield or module?\");\r\n" + 
	    		"    IOTW_PRINTLN(\"Note: press reset button on the board and reopen this Serial Monitor after fixing your issue!\");\r\n" + 
	    		"    while (true);\r\n" + 
	    		"  }\r\n" + 
	    		"\r\n" + 
	    		"  IOTW_PRINTLN(\"initialization done.\");";
	    translator.addSetupCommand(initial);
	  
	    String ret = "";
	    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		String filename =  translatorBlock.toCode();
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
		String message =translatorBlock.toCode();
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
        String test = translatorBlock.toCode();
		if(test.equals("true")){
		    ret="SDprint("+filename+","+message+",1);\n";
		} else {
		    ret="SDprint("+filename+","+message+",0);\n";
		}
		return codePrefix + ret + codeSuffix;
	}
}