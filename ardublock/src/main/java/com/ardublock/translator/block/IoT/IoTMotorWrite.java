package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTMotorWrite extends TranslatorBlock
{

  public IoTMotorWrite (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }


	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    String pos = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    String speed = translatorBlock.toCode();
		
		 // Header hinzuf�gen
	    translator.addHeaderFile("Wire.h");
	    translator.addHeaderFile("Adafruit_MotorShield.h");
	    
		String Dis="/* Adafruit Motor Shield \n"
				 + "Copyright (c) 2012 Adafruit Industries\r\n"
				 + "BSD License, Disclaimer see github \n"
				 + "https://github.com/adafruit/Adafruit_Motor_Shield_V2_Library?tab=License-1-ov-file#readme */\n";
	    translator.addDefinitionCommand(Dis);
	    
	    translator.addDefinitionCommand("// Feather Adafruit Motor Shield v2 http:\\www.adafruit.com/products/1438");
	   	translator.addDefinitionCommand("Adafruit_MotorShield AFMS = Adafruit_MotorShield(); // Treiber-Objekt");
	   	translator.addDefinitionCommand("Adafruit_DCMotor *myMotor1 = AFMS.getMotor(1); // Motor 1-Objekt");
	   	translator.addDefinitionCommand("Adafruit_DCMotor *myMotor2 = AFMS.getMotor(2); // Motor 2-Objekt");
	   	translator.addDefinitionCommand("Adafruit_DCMotor *myMotor3 = AFMS.getMotor(3); // Motor 3-Objekt");
	   	translator.addDefinitionCommand("Adafruit_DCMotor *myMotor4 = AFMS.getMotor(4); // Motor 4-Objekt");
	   
	   	String set = "// Control Feather Motor Shield\n"+
	    "void setMotorSpeed(int mot, int speed) {\n"+
	    "    int dir= RELEASE; 	\n"+
	    "	if (speed > 0)\n"+
	    "		dir = FORWARD;\n"+
	    "	else {\n"+
	    "		dir = BACKWARD;\n"+
	    "		speed = - speed;\n"+
	    "	}\n"+
	    "   if (speed > 255) speed = 255; // max Speed\n"+
	    "	switch (mot) {\n"+
	    "	 case 1:\n"+
	    "		myMotor1->setSpeed(speed);\n"+	
	    "		myMotor1->run(dir);\n"+
	    "		break;\n"+
	    "	 case 2:\n"+
	    "		myMotor2->setSpeed(speed);\n"+	
	    "		myMotor2->run(dir);\n"+
	    "		break;\n"+
	    "	 case 3:\n"+
	    "		myMotor3->setSpeed(speed);\n"+	
	    "		myMotor3->run(dir);\n"+
	    "		break;\n"+
	    "	 case 4:\n"+
	    "		myMotor4->setSpeed(speed);\n"+	
	    "		myMotor4->run(dir);\n"+
	    "		break;\n"+
	    "	}\n"+
	    "}\n";
	   	
	   	translator.addDefinitionCommand(set);
	   	
	    // Setupdeklaration
	    // I2C-initialisieren
	    //translator.addSetupCommand("Serial.begin(115200);");
	    
	    translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
	    translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
	 
	   	   
	    String Setup = "AFMS.begin(); // Setup Feather-Shield 1.6KHz PWM\n";
	    translator.addSetupCommand(Setup);
	    
		String ret = "setMotorSpeed("+pos+","+speed+");    // Ausgabe an Motor \n";
		

		return codePrefix + ret + codeSuffix;
	}
}