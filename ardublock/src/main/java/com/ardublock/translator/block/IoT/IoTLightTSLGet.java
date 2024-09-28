package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTLightTSLGet extends TranslatorBlock
{

  public IoTLightTSLGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
    translator.addSetupCommand("Serial.begin(115200);");
    // I2C-initialisieren
    
    String Setup;
    translator.addSetupCommand("Wire.begin(GPIO_I2C_SDA, GPIO_I2C_SCL); // ---- Initialisiere den I2C-Bus \n");
    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
    // Deklarationen hinzuf�gen
    
    String Dis="/* Adafruit Lib for TSL2561 Light Sensor"
			 + "Copyright Adafruit \r\n"
			 + "Adafruit invests time and resources providing this open source code.\n "
			 + "Please support Adafruit and open-source hardware by purchasing products from Adafruit!*/\n";
    translator.addDefinitionCommand(Dis);
    
    String dec = " // Adafruit TSL2561, https://github.com/adafruit/Adafruit_TSL2561, Written by Kevin (KTOWN) Townsend for Adafruit Industries \n"
              + "Adafruit_TSL2561_Unified tsl = Adafruit_TSL2561_Unified(0x39, 12345);\n";

    translator.addDefinitionCommand(dec);

    
    
	Setup = "  if(!tsl.begin()) {\r\n" + 
			"    Serial.print(\"no TSL2561 detected ... Check your wiring or I2C ADDR!\");\r\n" + 
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
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
          
    // Code von der Mainfunktion
	ret = "readLightTSL2561("+code+")";
	
   
    return codePrefix + ret + codeSuffix;
  }
}