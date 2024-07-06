package com.ardublock.translator.block;

import java.util.LinkedList;
import java.util.List;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ProgramBlock extends TranslatorBlock
{
	private List<String> setupCommand;
	
	public ProgramBlock(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator);
		setupCommand = new LinkedList<String>();
	}
	
	

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{   translator.clearLocalNumberVariable(); // clear all local Variables

	    String ret="";
		TranslatorBlock translatorBlock = getTranslatorBlockAtSocket(0);
		while (translatorBlock != null)
		{
			ret = translatorBlock.toCode();
			translatorBlock = translatorBlock.nextTranslatorBlock();
			this.setupCommand.add(ret);
		}
    	 translator.clearLocalNumberVariable(); // clear all local Variables

		translator.registerBodyTranslateFinishCallback(this);
//		return "";
		ret="";
		ret = "void loop() { // Kontinuierliche Wiederholung \n";
		
		if (translator.isBlynkProgram())
		{
			ret += "Blynk.run();// Blynk Housekeeping\n";
		}

		if (translator.isDIYSpectroProgram())
		{
			ret += "DIY_SpectroOctiLoop();// Web-Server Spectrometer bedienen\n";
		}

		
		//if (translator.isAPProgram())
		//{
		//	ret += "//dnsServer.processNextRequest(); // DNS-Server bedienen\n";
		//}

		
		
		
		TranslatorBlock translatorBlock2 = getTranslatorBlockAtSocket(1);
		while (translatorBlock2 != null)
		{
			ret = ret + translatorBlock2.toCode();
			translatorBlock2 = translatorBlock2.nextTranslatorBlock();
		}
		
		if (translator.isScoopProgram())
		{
			ret += "yield();// wifi housekeeping\n";
		}
		
	//	if (translator.ismDNSProgram())
	//	{
	//		ret += " MDNS.update(); // Domain Name Service bedienen\n";
	//	}

		if (translator.isLORAProgram())
		{
			ret += " os_runloop_once(); // LORA LMIC Housekeeping\n";
		}

		if (translator.isMQTTProgram())
		{
			ret += "mqttclient.loop();// MQTT-Client bedienen\n";
		}
		if (translator.isHTTPServerProgram())
		{
			ret += " server.handleClient();// Web-Server bedienen\n";
		}
		if (translator.isNTPServerProgram())
		{
			ret += " ShellyNTPServer(); // NTP-Server bedienen\n";
		}
		
		
		ret = ret + "} //end loop\n\n";
		return ret;
	}
	
	@Override
	public void onTranslateBodyFinished()
	{
		for (String command : setupCommand)
		{
			translator.addSetupCommandForced(command);
		}
	}
	
}
