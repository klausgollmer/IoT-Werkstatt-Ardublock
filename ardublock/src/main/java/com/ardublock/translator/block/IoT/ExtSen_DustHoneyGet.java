package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_DustHoneyGet extends TranslatorBlock
{

  public ExtSen_DustHoneyGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
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
    String Setup = "swSer.begin(9600);         // Honeywell Feinstaubsensor 9600 Baud\n"
    		     + "swSer.setTimeout(1500);    // Timeout länger als eine Messperiode\n";
    translator.addSetupCommand(Setup);

    
    // Deklarationen hinzuf�gen
    translator.addDefinitionCommand("SoftwareSerial swSer(14, 12, false); // Library: https://github.com/plerup/espsoftwareserial/, LGPL-2.1 license, Peter Lerup,  14 -> TX, 12 -> RX\n");
   	
    
    String read = "// Feinstaubsensor Honeywell HPMA115S0, www.dfrobot.com/wiki/index.php/PM2.5_laser_dust_sensor_SKU:SEN0177\n"
        +"int readFeinstaubHoneywell(int chan) {\n"  
    	+" if (chan > 2) return (-1);  // no PM 1.0 \n" 	
    	+"  #define LENG 31            // Antwortlänge\n"
    	+"  unsigned char RXbuf[LENG]; // Puffer für Antwort\n"
    	+"  int checkSum = 0x42;       // Prüfsumme\n"
    	+"  int val = -1;              // Ergebnis\n"
    	+"  if(swSer.find(0x42)){      // start when detect 0x42\n"
    	+"    swSer.readBytes(RXbuf,LENG); // Einlesen\n"
    	+"    if(RXbuf[0] == 0x4d){    // erwartete Antwort da\n"
    	+"       for(int i=0; i<(LENG-2); i++) checkSum+=RXbuf[i];   // Berechne Prüfsumme\n"
    	+"       if(checkSum == ((RXbuf[LENG-2]<<8)+RXbuf[LENG-1])){ // Pruefsumme ok\n"
    	+"         if (chan == 2)\n"
    	+"            val = ((RXbuf[5]<<8) + RXbuf[6]);//count PM2.5 value of the air detector module\n"
    	+"          else \n"
    	+"            val = ((RXbuf[7]<<8) + RXbuf[8]); //count PM10 value of the air detector module\n"  
    	+"      }\n"           
    	+"    } \n"
    	+"  }\n"
    	+"  return val;\n"
    	+"}\n";
    	  
    translator.addDefinitionCommand(read);		
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String code = translatorBlock.toCode();
          
    // Code von der Mainfunktion
	ret = "readFeinstaubHoneywell("+code+")";
	
   
    return codePrefix + ret + codeSuffix;
  }
}