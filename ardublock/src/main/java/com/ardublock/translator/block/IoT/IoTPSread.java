package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTPSread  extends TranslatorBlock {

	public IoTPSread (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
	
		
  	    translator.addHeaderFile("Wire.h");
      	translator.addHeaderFile("person_sensor.h");

		//translator.addSetupCommand("Serial.begin(115200);");
	    translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
		translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
			
		String confidence,print,mode;

		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    mode = translatorBlock.toCode();
		
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    confidence = translatorBlock.toCode();
	    
	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    print = translatorBlock.toCode();

	    String Dis="/* Personal Sensor \n"
				 + "Copyright (c) Useful Sensors\r\n"
				 + "Disclaimer see https://github.com/usefulsensors/person_sensor_arduino \n"
				 + "*/\n";
	   	translator.addDefinitionCommand(Dis);
	
	    
	    
	    translator.addDefinitionCommand("int PS_next_unused_id = 1;\n" + 
	    		"#define PS_MAXWAITCAL 10 \n" + 
	    		"\n");
	    
	    String Code = "// Person Sensor, useful sensors https://github.com/usefulsensors/person_sensor_docs\n" + 
	    		"// Author pete@usefulsensors.com \n" + 
	    		"//\n" + 
	    		"// ID 0: internal counting, n: calibrate person n\n" + 
	    		"int PS_Calibrate(int ID){\n" + 
	    		"  int OK = 1;\n" + 
	    		"  if (ID < 1) ID = PS_next_unused_id;\n" + 
	    		"  if (ID >= PERSON_SENSOR_MAX_IDS_COUNT){\n" + 
	    		"    printf(\"sorry, max IDS Count\\n\");\n" + 
	    		"    return 0;\n" + 
	    		"  }\n" + 
	    		"  person_sensor_results_t results = {\n" + 
	    		"  };\n" + 
	    		"\n" + 
	    		"  int wait = PS_MAXWAITCAL; // n frames stable \n" + 
	    		"  if (person_sensor_read(&results)) { // valid results\n" + 
	    		"    //Serial.print(results.faces[0].id_confidence);\n" + 
	    		"    while (wait > 0 && OK) {\n" + 
	    		"      delay(1);\n" + 
	    		"      if (!((results.num_faces == 1) &&\n" + 
	    		"        (results.faces[0].box_confidence >= 95) &&\n" + 
	    		"        (results.faces[0].id_confidence <= 90) &&\n" + 
	    		"        (results.faces[0].is_facing == 1))) {\n" + 
	    		"        OK = 0;\n" + 
	    		"        yield();\n" + 
	    		"      } \n" + 
	    		"      else {\n" + 
	    		"        if (wait==PS_MAXWAITCAL) Serial.print(\" start ...\");\n" + 
	    		"        delay (250);\n" + 
	    		"        Serial.println(\"New\");\n" + 
	    		"        person_sensor_read(&results);\n" + 
	    		"        yield();\n" + 
	    		"        Serial.print('.');\n" + 
	    		"      }\n" + 
	    		"      wait--;\n" + 
	    		"      delay(1);\n" + 
	    		"    }    \n" + 
	    		"    if (OK) {\n" + 
	    		"      person_sensor_write_reg(\n" + 
	    		"      PERSON_SENSOR_REG_CALIBRATE_ID, ID);\n" + 
	    		"      Serial.println(\" done\");\n" + 
	    		"      OK = ID;\n" + 
	    		"      PS_next_unused_id += 1;\n" + 
	    		"    }\n" + 
	    		"  } \n" + 
	    		"  else OK = 0;\n" + 
	    		"  return OK;\n" + 
	    		"}\n" + 
	    		"\n" + 
	    		"void PS_IDStore(int type){\n" + 
	    		"  if (type == 1) {\n" + 
	    		"    person_sensor_write_reg(\n" + 
	    		"    PERSON_SENSOR_REG_PERSIST_IDS, 0x01);\n" + 
	    		"    Serial.println(\"PS: Store any recognized IDs even when unpowered\");\n" + 
	    		"  } \n" + 
	    		"  else {\n" + 
	    		"    person_sensor_write_reg(\n" + 
	    		"    PERSON_SENSOR_REG_ERASE_IDS  , 0x00);\n" + 
	    		"    Serial.println(\"PS: Wipe any recognized IDs from storage\");\n" + 
	    		"  }\n" + 
	    		"}\n" + 
	    		"\n" + 
	    		"// SearchMode:-4: Position X, -3: isFacing, -2: all visible Persons, -1: count Person, 0: identify any person, n detect Person n\n" + 
	    		"int PS_Read(int my_confidence, int SearchMode, int mit_print){\n" + 
	    		"  int ActPerson = -1; // no person detected\n" + 
	    		"  int Facing = 0;\n" + 
	    		"  int power=1;\n" + 
	    		"  if (SearchMode < 0) ActPerson = 0; // count mode\n" + 
	    		"\n" + 
	    		"  person_sensor_results_t results = {\n" + 
	    		"  };\n" + 
	    		"  if (!person_sensor_read(&results)) {\n" + 
	    		"    if (mit_print)\n" + 
	    		"      printf(\"No person sensor results found on the i2c bus\\n\");\n" + 
	    		"  } \n" + 
	    		"  else {\n" + 
	    		"    if (mit_print) {\n" + 
	    		"      printf(\"********\\n\");\n" + 
	    		"      printf(\"%d faces found \\n\", results.num_faces);\n" + 
	    		"    }\n" + 
	    		"    for (int i = 0; i < results.num_faces; ++i) {\n" + 
	    		"      const person_sensor_face_t* face = &results.faces[i];\n" + 
	    		"      if (mit_print) {\n" + 
	    		"        if (face->id_confidence > 0) {\n" + 
	    		"          printf(\"Recognized face %d as person %d with confidence %d\\n\", \n" + 
	    		"          i, face->id, face->id_confidence);\n" + 
	    		"        } \n" + 
	    		"        else {\n" + 
	    		"          printf(\"Unrecognized face %d with confidence %d\\n\", i,face->box_confidence);\n" + 
	    		"        }\n" + 
	    		"      }\n" + 
	    		"      if (face->is_facing) Facing = 1;\n" + 
	    		"      if (face->id_confidence >= my_confidence) {\n" + 
	    		"        if (SearchMode == -1) ActPerson++; // just count the known onces\n" + 
	    		"        if ((SearchMode == 0) && (ActPerson == -1)) ActPerson = face->id; // known person detected\n" + 
	    		"        if (SearchMode  > 0)  ActPerson = (SearchMode == face->id); // certain known person \n" + 
	    		"        if (SearchMode == -2) {\n" + 
	    		"          ActPerson = ActPerson*power+(face->id);\n" + 
	    		"          power=power*10;\n" + 
	    		"        }\n" + 
	    		"      } \n" + 
	    		"      else {\n" + 
	    		"        if (face->box_confidence >= my_confidence) { // but it is sure a face\n" + 
	    		"          if (SearchMode == -1) ActPerson++;  // just count person\n" + 
	    		"          if ((SearchMode == 0) && (ActPerson == -1)) ActPerson = 0; // unknown person  \n" + 
	    		"        }\n" + 
	    		"      }\n" + 
	    		"      if ((SearchMode == -4)&& (i==0)) ActPerson=(face->box_left + face->box_right)/2 - 128;\n" + 
	    		" \n" + 
	    		"    } // all faces checked\n" + 
	    		"  }\n" + 
	    		"  if (SearchMode == -3) ActPerson = Facing;\n" + 
	    		"  return ActPerson;\n" + 
	    		"}\n" + 
	    		"";
	    
	    translator.addDefinitionCommand(Code);

	    
	    
	    
	    String ret = "PS_Read("+confidence+","+mode+","+print+")";
        return codePrefix + ret + codeSuffix;
	 	}
}

