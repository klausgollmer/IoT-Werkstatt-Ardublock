package com.ardublock.translator.block;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SleepBlockLight extends TranslatorBlock
{

	public SleepBlockLight(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
	String extern="extern \"C\" {  // zur Nutzung der speziellen ESP-Befehle wie Deep Sleep\n"
			     + "   #include \"user_interface.h\"\n"
			     +"}\n";
	translator.addDefinitionCommand(extern);
	translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
	
	String wakeup = "/* ------------------------------------- light sleep, do not change a running system\n" + 
			" *  https://kevinstadler.github.io/notes/esp8266-deep-sleep-light-sleep-arduino/\n" + 
			"*/\n" + 
			"void fpm_wakup_cb_func(void) { // used for light sleep\n" + 
			"  Serial.println(\"wakeup lightsleep\"); Serial.flush();\n" + 
			"}\n" + 
			"void lightsleep(long ms) {\n" + 
			"    // Wifi off\n" + 
			"  wifi_station_disconnect();\n" + 
			"  wifi_set_opmode(NULL_MODE); \n" + 
			"  \n" + 
			"  Serial.flush(); // no serial output in pipe\n" + 
			"  // for timer-based light sleep to work, the os timers need to be disconnected\n" + 
			"  extern os_timer_t *timer_list; timer_list = nullptr;\n" + 
			"  // enable light sleep\n" + 
			"  wifi_fpm_set_sleep_type(LIGHT_SLEEP_T);\n" + 
			"  wifi_fpm_open();\n" + 
			"  wifi_fpm_set_wakeup_cb(fpm_wakup_cb_func);\n" + 
			"  // sleep for ms\n" + 
			"  long sleepTimeMilliSeconds = ms;\n" + 
			"  wifi_fpm_do_sleep(sleepTimeMilliSeconds * 1000);\n" + 
			"  // timed light sleep is only entered when the sleep command is\n" + 
			"  // followed by a delay() that is at least 1ms longer than the sleep\n" + 
			"  delay(sleepTimeMilliSeconds + 1);\n" + 
			"  // code will continue here after the time-out \n" + 
			"}\n" + 
			"";
	translator.addDefinitionCommand(wakeup);
	
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	String Delay_ms = translatorBlock.toCode(); 
    
	String ret ="lightsleep("+Delay_ms+");";
    return ret;
	}

}

