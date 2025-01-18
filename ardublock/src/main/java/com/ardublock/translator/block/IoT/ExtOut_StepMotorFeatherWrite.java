package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtOut_StepMotorFeatherWrite extends TranslatorBlock
{

  public ExtOut_StepMotorFeatherWrite (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }


	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    String pos = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    String steps = translatorBlock.toCode();

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
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
	   	translator.addDefinitionCommand("Adafruit_StepperMotor *myStepMotor1 = AFMS.getStepper(100,1); // Motor 1-Objekt, 100 Steps/Umdrehung");
	   	translator.addDefinitionCommand("Adafruit_StepperMotor *myStepMotor2 = AFMS.getStepper(100,2); // Motor 2-Objekt");
	   
	   	String set = "// Control Feather Motor Shield\n"+
	    "void setMotorSteps(int mot, int steps, int speed) {\n"+
	    "   int dir= RELEASE; 	\n"+
	    "	if (steps > 0)\n"+
	    "		dir = FORWARD;\n"+
	    "	else {\n"+
	    "		dir = BACKWARD;\n"+
	    "		steps = - steps;\n"+
	    "	}\n"+
	    "	switch (mot) {\n"+
	    "	  case 1:\n"+
	    "		myStepMotor1->setSpeed(speed);\n"+	
	    "		myStepMotor1->step(steps, dir, DOUBLE);\n"+
	    "		if (steps==0) myStepMotor1->release();\n"+
	    "		break;\n"+
	    "	  case 2:\n"+
	    "		myStepMotor2->setSpeed(speed);\n"+	
	    "		myStepMotor2->step(steps, dir, DOUBLE);\n"+
	    "		if (steps==0) myStepMotor2->release();\n"+
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
	    Setup = "Wire.setClock(400000L); // speed up i2c ";
	    translator.addSetupCommand(Setup);
	    
		String ret = "setMotorSteps("+pos+","+steps+","+speed+");    // Ausgabe an Motor \n";
		

		return codePrefix + ret + codeSuffix;
	}
}