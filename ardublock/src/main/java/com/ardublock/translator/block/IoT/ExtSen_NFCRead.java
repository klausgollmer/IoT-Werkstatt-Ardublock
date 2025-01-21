package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_NFCRead extends TranslatorBlock
{

  public ExtSen_NFCRead (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    
    // Header hinzuf�gen
    //translator.addHeaderFile("NfcAdapter.h");
    //translator.addHeaderFile("PN532/PN532_I2C/PN532_I2C.h");
    //translator.addHeaderFile("PN532/PN532/PN532.h");
    //// now in init translator.addHeaderFile("Wire.h");
    
    translator.addHeaderFile("NfcAdapter.h");
    translator.addHeaderFile("PN532_I2C.h");
    translator.addHeaderFile("PN532.h");
    // now in init translator.addHeaderFile("Wire.h");
    
    
    // Deklarationen hinzuf�gen
    translator.addDefinitionCommand("// (C) PN532 Don Coleman, Seedstudio \n"
                                  + "// https://wiki.seeedstudio.com/Grove_NFC/ \n");
    translator.addDefinitionCommand("// https://github.com/Seeed-Studio/PN532\n");

	
	
	translator.addDefinitionCommand("PN532_I2C pn532_i2c(Wire); // NFC-Reader");
 	translator.addDefinitionCommand("NfcAdapter nfcSeed = NfcAdapter(pn532_i2c);");
 	    
    // Setupdeklaration
	//translator.addSetupCommand("Serial.begin(115200);");
    // I2C-initialisieren
	// now in init : translator.addSetupCommand("Wire.begin(SDA, SCL); // ---- Initialisiere den I2C-Bus \n");
	// now in init : translator.addSetupCommand("#if defined(ESP8266) \n   if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n  #endif \n");
	 
    String Setup = "Serial.println(\"Initialize NFC Reader\");\r\n" + 
    		       "nfcSeed.begin();  // ---- PN532 NFC-Reader\n";
    translator.addSetupCommand(Setup);

  
    String Read;
    Read= "// -----------  Einlesefunktion NFC-Tag, wartet auf Tag\n"
  		+ "String NFCRead(int typ, int index) { \n"
    	+ "  uint8_t  OK=0;\n"
   	    + "  String ret, payloadAsString = \"\";\n"
        + "  Serial.print(\"\\nWarte auf NFC-Tag \");\n"
        + "  while (!OK)  { // wiederhole bis gueltiger Transponder gesehen\n"
        + "    long T = millis();\n"
        + "    OK = nfcSeed.tagPresent();\n"
        + "    if ((millis()-T) < 15) {                 // QnD Bugfix: zu schnell, wiederhole\n"
        + "      OK = 0;     \n"
        + "      Wire.begin(SDA, SCL);                          // I2C-Bus restart\n" 
        + "      #if defined(ESP8266)\n if (Wire.status() != I2C_OK) Serial.println(F(\"Something wrong with I2C\")); \n #endif \n"
        + "      nfcSeed.begin();\n"
        + "    }\n"
        + "  }\n"
        + "  NfcTag tag   = nfcSeed.read();\n"
        + "  String tagID = tag.getUidString();\n"
        + "  int hash = 0;\n"
        + "  for (int i = 0;i<tagID.length();i++) hash+=tagID.charAt(i);"
        +"   switch (typ) {\n"
        +"    case 2: "
        +"            if (tag.hasNdefMessage()) { // every tag won't have a message\n"
        +"             NdefMessage message = tag.getNdefMessage();\n"
        +"             NdefRecord  record  = message.getRecord(index);\n"
        +"             int payloadLength = record.getPayloadLength();\n"
        +"             byte payload[payloadLength];\n"
        +"             record.getPayload(payload);\n"
        +"             for (int c = 0; c < payloadLength; c++) {\n"
        +"               payloadAsString = payloadAsString+String((char)payload[c]);\n"
        +"             }\n"
        +"            }\n"
        + "           ret = payloadAsString;\n"
        +"            break;\n"
        +"    case 1: ret = tagID;\n"
        +"            break;\n" 
        +"    case 0: ret = String(hash);\n"
        +"            break;\n"
        +"    default:ret = \"Fehler nfcSeed\" ;\n"
        +"            break;\n" 
        +"  }\n"
        +"  delay(500);\n"
        +"  Serial.println(\"NFC-Read returns: \"+ret);\n"
        +"  return ret;\n"
        +" }\n";
        

    translator.addDefinitionCommand(Read); 		
    	
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String typ = translatorBlock.toCode();

    //translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    //String index = translatorBlock.toCode();
          
    // Code von der Mainfunktion
	ret = "NFCRead("+ typ +"," + 0 +")";
	
   
    return codePrefix + ret + codeSuffix;
  }
}