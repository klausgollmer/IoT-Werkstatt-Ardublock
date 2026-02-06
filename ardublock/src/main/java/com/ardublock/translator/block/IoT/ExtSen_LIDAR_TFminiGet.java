package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_LIDAR_TFminiGet extends TranslatorBlock
{

  public ExtSen_LIDAR_TFminiGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    // Header hinzuf�gen
    translator.addHeaderFile("SoftwareSerial.h");

    // Setupdeklaration
    // I2C-initialisieren
    String Setup = "swSerLIDAR.begin(19200);         // LIDAR ToF, TFmini Abstandssensor\n";
    translator.addSetupCommand(Setup);

    
    // Deklarationen hinzuf�ge
    translator.addDefinitionCommand("SoftwareSerial swSerLIDAR(14, 12, false); //Library: https://github.com/plerup/espsoftwareserial/, LGPL-2.1 license, Peter Lerup, 14 -> TX, 12 -> RX\n");
   	
    
    String read = "// LIDAR TFmini http://www.benewake.com/en/tfmini.html \n"
    +"int getDistanceToFSensor(int option){ \n"
    +"  int dist = -1, strength = -1;\n"
    +"  int Tout = 1000;\n"
    +"  char crc,crc_soll;\n"
    +"  unsigned int t1, t2;\n"
    +"  char message[20];\n"

    +"   swSerLIDAR.flush(); // alte Zeichen löschen, auf zwei Nachrichten warte\n"
    +"    while ((swSerLIDAR.available() < 18) && (Tout > 0)) { // warte bis Message da\n"
    +"    delay(1);\n"
    +"    Tout--;\n"
    +"  }\n"
    +"  if (Tout <= 0) return -1;     // Timeout, kein Sensor\n"

   +"     for (int i=0;i<18;i++){ //         // Nachricht einlesen\n"
   +"         message[i] = swSerLIDAR.read();\n"
   +"     }\n"

   +"      int start = -1; //         // Header suchen\n"
   +"      for (int i=0;i<9;i++){\n"
   +"        if ((message[i] == 0x59) && (message[i+1]==0x59)) {\n"
   +"        start = i;\n"
   +"       }\n"
   +"      }\n"
   +"   if (start < 0) return -2;      // Kein Header gefunden\n"

   +"    dist     = (message[start+3] << 8) + message[start+2]; // Auswerten\n"
   +"    strength = (message[start+5] << 8) + message[start+4];\n"

   +"     crc_soll = message[start+8];    // Byte 8\n"
   +"     crc = 0;\n"

   +"     for (int i = start;i<(start+8);i++)\n"
   +"       crc = crc+message[i]; \n"

   +"     if (crc != crc_soll)  return -3; // check error\n"
   +"     if (dist > 1200)      return -4; // unreliablen\n"
   +"      if (option ) return dist;\n"
   +"      return strength;\n"
   +"  }\n";

    translator.addDefinitionCommand(read);		
	
//	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
 //   String code = translatorBlock.toCode();
          
    // Code von der Mainfunktion
	ret = "getDistanceToFSensor(1)";
	
   
    return codePrefix + ret + codeSuffix;
  }
}