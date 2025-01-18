package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_GPSGet extends TranslatorBlock
{

  public ExtSen_GPSGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Header hinzuf�gen
    translator.addHeaderFile("TinyGPS++.h");
    translator.addHeaderFile("SoftwareSerial.h");
    
    // Setupdeklaration
    // I2C-initialisieren

	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String RxPin = translatorBlock.toCode();

    
    String Def = "const int GPS_RxPin = "+RxPin+";\n"
    		   + "SoftwareSerial ssGPS(GPS_RxPin, 100); // RXPin, TX not used, Library: https://github.com/plerup/espsoftwareserial/, Peter Lerup \n";
    translator.addDefinitionCommand(Def);
    
    //translator.addSetupCommand("Serial.begin(115200);");    
    translator.addSetupCommand("if (GPS_RxPin == 3) pinMode(3, FUNCTION_3); // swap UART-RX-Pin to normal GPIO \n"
                            +  "delay(100);\n"  		
    	                    +  "ssGPS.begin(9600);// software serial GPS\n");

    
    
   	translator.addDefinitionCommand("//Reading GPS Datastream, TiniGPSPlus by: Mikal Hart\n");
   	translator.addDefinitionCommand("//https://github.com/mikalhart/TinyGPSPlus\n");
   	translator.addDefinitionCommand("TinyGPSPlus gps;                 // The TinyGPS++ object\n");
	
    String Util = "// -----------------  GPS \r\n" + 
    		"// Update the GPS-Datastrem first, after successful update read the actual coordinates\r\n" + 
    		"float updateGPS(uint8_t sel) {\r\n" + 
    		"  int      tout = 5000; // 5 sec timeout\r\n" + 
    		"  float ret = 0;      // return value\r\n" + 
    		"  uint8_t  ok = 0;       // valid value\r\n" + 
    		"  if (sel == 1) {        // Update GPS values\r\n" + 
    		"    Serial.print(\"\\nGPS: listen to sat ...\");\r\n" + 
    		"    while (ssGPS.available() > 0) ssGPS.read(); // flush pipe (delete old data)\r\n" + 
    		"    while ((ssGPS.available() == 0) && tout > 0)  { \r\n" + 
    		"      tout--; \r\n" + 
    		"      delay(1);\r\n" + 
    		"    } // wait for new input\r\n" + 
    		"    if (tout == 0) Serial.println(\"GPS-timeout\");\r\n" + 
    		"    tout = 5000; \r\n" + 
    		"    while ((tout>0) && (ok == 0)) {\r\n" + 
    		"      if (ssGPS.available() > 0) {\r\n" + 
    		"        if (gps.encode(ssGPS.read())) { // valid sentence\r\n" + 
    		"          ok = gps.location.isValid() && (gps.location.age() < 100);   \r\n" + 
    		"        }\r\n" + 
    		"      } \r\n" + 
    		"      else {\r\n" + 
    		"        delay(1);\r\n" + 
    		"        tout--;\r\n" + 
    		"      }\r\n" + 
    		"    }\r\n" + 
    		"  }\r\n" + 
    		"  switch (sel) {\r\n" + 
    		"  case 1: \r\n" + 
    		"    ret = ok;\r\n" + 
    		"    if (!ok) Serial.println(\"no fix\");\r\n" + 
    		"    break;\r\n" + 
    		"  case 2: \r\n" + 
    		"    ret = gps.location.lat() ;\r\n" + 
    		"    Serial.println(\"GPS: lat = \"+String(ret));\r\n" + 
    		"    break;\r\n" + 
    		"  case 3: \r\n" + 
    		"    ret = gps.location.lng() ;\r\n" + 
    		"    Serial.println(\"GPS: lng = \"+String(ret));\r\n" + 
    		"    break;\r\n" + 
    		"  case 4: \r\n" + 
    		"    ret = gps.altitude.meters();\r\n" + 
    		"    Serial.println(\"GPS: alt = \"+String(ret));\r\n" + 
    		"    break;\r\n" + 
    		"  case 5: \r\n" + 
    		"    ret = gps.hdop.value();\r\n" + 
    		"    Serial.println(\"GPS: hdop = \"+String(ret));\r\n" + 
    		"    break;\r\n" + 
    		"  }\r\n" + 
    		"  return ret;   \r\n" + 
    		"}";
    
    translator.addDefinitionCommand(Util);
	
   	
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String code = translatorBlock.toCode();
    
    
    // Code von der Mainfunktion
	ret = "updateGPS("+code+")";
    return codePrefix + ret + codeSuffix;
  }
}