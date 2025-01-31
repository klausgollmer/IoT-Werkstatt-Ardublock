package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_TSL2561Get2 extends TranslatorBlock
{

  public ExtSen_TSL2561Get2 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Header hinzuf�gen
    translator.addHeaderFile("Adafruit_Sensor.h");
    translator.addHeaderFile("Adafruit_TSL2561_U.h");

    // Setupdeklaration
    //translator.addSetupCommand("Serial.begin(115200);");
    // I2C-initialisieren

    
    String adress;
	TranslatorBlock translatorBlock = this.getTranslatorBlockAtSocket(1);
	if (translatorBlock==null)
	       adress ="0x39";
	else   adress = translatorBlock.toCode();

    
    
    String Setup;
    // now in init : translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
    // now in init : translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) IOTW_PRINTLN(F(\"Something wrong with I2C\")); \n  #endif \n");
    // Deklarationen hinzuf�gen
    String dec = " // Adafruit TSL2561, https://github.com/adafruit/Adafruit_TSL2561, Written by Kevin (KTOWN) Townsend for Adafruit Industries \n"
              + "Adafruit_TSL2561_Unified tsl = Adafruit_TSL2561_Unified("+adress+", 12345);\n";

    
    translator.addDefinitionCommand(dec);
    
    
    
    
	Setup = "  if(!tsl.begin()) {\r\n" + 
			"    IOTW_PRINT(\"no TSL2561 detected ... Check your wiring or I2C ADDR!\");\r\n" + 
			"    while(1) {yield();};\r\n" + 
			"  }\r\n" + 
			"  /* Setup the sensor gain and integration time */\r\n" + 
			"  tsl.enableAutoRange(true);            /* Auto-gain ... switches automatically between 1x and 16x */\r\n" + 
			"  tsl.setIntegrationTime(TSL2561_INTEGRATIONTIME_101MS);  /* medium resolution and speed   */";
    translator.addSetupCommand(Setup);

    String read = "//******* Lese Light Sensor \r\n" + 
    		"int32_t readLightTSL2561(int spec)\r\n" + 
    		"{\r\n" + 
    		"  uint16_t broadband = 0;     // Sensor-Daten vom Sensorteil der im VIS und IR Bereich misst\r\n" + 
    		"  uint16_t infrared  = 0;     // Sensor-Daten vom Sensorteil der nur im IR Bereich misst\r\n" + 
    		"  int32_t val = -1;\r\n" + 
    		"  int32_t lux;\r\n" + 
    		"  \r\n" + 
    		"  sensors_event_t event;      // Ein neues TSL2561 Lichtsensor Ereigniss einrichten\r\n" + 
    		"  tsl.getEvent(&event);       // Eine Messung durchführen\r\n" + 
    		"  if (event.light) {          // Wird nur dann \"Wahr\" wenn erfolgreich gemesen wurde\r\n" + 
    		"    tsl.getLuminosity (&broadband, &infrared);       // VIS-IR- und IR-Werte auslesen\r\n" + 
    		"    switch (spec) {\r\n" + 
    		"      case 0: \r\n" + 
    		"         val = event.light; // Lux\r\n" + 
    		"         break;\r\n" + 
    		"      case 1: \r\n" + 
    		"         val = broadband;\r\n" + 
    		"         break;\r\n" + 
    		"      case 2: \r\n" + 
    		"         val = infrared;\r\n" + 
    		"         break;\r\n" + 
    		"    }\r\n" + 
    		"            \r\n" + 
    		"  }\r\n" + 
    		" return val;\r\n"+    
    		"}\r\n";	  
    translator.addDefinitionCommand(read);		
	
	translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
    
    
    
    // Code von der Mainfunktion
	ret = "readLightTSL2561("+code+")";
	
   
    return codePrefix + ret + codeSuffix;
  }
}