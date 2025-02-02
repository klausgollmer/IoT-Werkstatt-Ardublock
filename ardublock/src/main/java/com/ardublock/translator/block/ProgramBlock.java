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
			//ret += "mqttclient.loop();// MQTT-Client bedienen\n";
			ret += "mqttreconnect(1);// MQTT-Client bedienen\n";
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
		
		String Dis = "\n\n\n/* Disclaimer MIT License\n"
				+ "Some of the libraries used are under MIT Licence. The following applies to these:"
				+ "Permission is hereby granted, free of charge, to any person obtaining a copy\n"
				+ "of this software and associated documentation files (the \"Software\"), to deal\n"
				+ "in the Software without restriction, including without limitation the rights\n"
				+ "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n"
				+ "copies of the Software, and to permit persons to whom the Software is\n"
				+ "furnished to do so, subject to the following conditions:\n"
				+ "The above copyright notice and this permission notice shall be included in all\n"
				+ "copies or substantial portions of the Software.\n"
				+ "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n"
				+ "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n"
				+ "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n"
				+ "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n"
				+ "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n"
				+ "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n"
				+ "SOFTWARE.*/\n";
		
		
		return ret+Dis;
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
