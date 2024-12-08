package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTNOWPayloadStr extends TranslatorBlock
{

  public IoTNOWPayloadStr (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }


	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{

	  String CallbackCode = "// ESP-Now, Einführung unter https://wolles-elektronikkiste.de/esp-now\r\n"
	    		+"// ------------ ESP-NOW Payloads \n"
	    		+ "String ESPNOW_Payload_String =\"\";\r\n";
	  translator.addDefinitionCommand(CallbackCode);
		
		String ret = "ESPNOW_Payload_String";
		return codePrefix + ret + codeSuffix;
	}
}