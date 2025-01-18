package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class ExtSen_MaxbotixGet extends TranslatorBlock
{

  public ExtSen_MaxbotixGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
    String ret;
        
    TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String rxpin = translatorBlock.toCode();

    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String max = translatorBlock.toCode();
          
    translatorBlock = this.getTranslatorBlockAtSocket(2);
    String sleep = "-1";
    if (translatorBlock!=null)
       sleep = translatorBlock.toCode();
    
    
    // Header hinzuf�gen
    translator.addHeaderFile("SoftwareSerial.h");
    translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
    
    // Setupdeklaration
    // I2C-initialisieren
    String Setup = " swSerMaxBot.begin(9600,SWSERIAL_8N1); // Maxbotix ultrasonic \n";
    translator.addSetupCommand(Setup);
    if (sleep != "-1")
    		Setup = "pinMode("+sleep+",OUTPUT); digitalWrite("+sleep+",HIGH); \n"+
    				"Serial.println(\"Side effect: set sleep GPIO"+sleep+" to OUTPUT HIGH (Maxbotix sensor)\");";
    translator.addSetupCommand(Setup);
    
    String wakeup = "#if defined(ESP8266)\r\n"
    		+ "/* ------------------------------------- light sleep, do not change a running system\r\n"
    		+ " *  https://kevinstadler.github.io/notes/esp8266-deep-sleep-light-sleep-arduino/\r\n"
    		+ " */\r\n"
    		+ "void fpm_wakup_cb_func(void) { // used for light sleep\r\n"
    		+ "  Serial.println(\"wakeup lightsleep\"); \r\n"
    		+ "  Serial.flush();\r\n"
    		+ "}\r\n"
    		+ "void MaxBotixLightsleep(long ms) {\r\n"
    		+ "  // Wifi off\r\n"
    		+ "  wifi_station_disconnect();\r\n"
    		+ "  wifi_set_opmode(NULL_MODE); \r\n"
    		+ "\r\n"
    		+ "  Serial.flush(); // no serial output in pipe\r\n"
    		+ "  // for timer-based light sleep to work, the os timers need to be disconnected\r\n"
    		+ "  extern os_timer_t *timer_list; \r\n"
    		+ "  timer_list = nullptr;\r\n"
    		+ "  // enable light sleep\r\n"
    		+ "  wifi_fpm_set_sleep_type(LIGHT_SLEEP_T);\r\n"
    		+ "  wifi_fpm_open();\r\n"
    		+ "  wifi_fpm_set_wakeup_cb(fpm_wakup_cb_func);\r\n"
    		+ "  // sleep for ms\r\n"
    		+ "  long sleepTimeMilliSeconds = ms;\r\n"
    		+ "  wifi_fpm_do_sleep(sleepTimeMilliSeconds * 1000);\r\n"
    		+ "  // timed light sleep is only entered when the sleep command is\r\n"
    		+ "  // followed by a delay() that is at least 1ms longer than the sleep\r\n"
    		+ "  delay(sleepTimeMilliSeconds + 1);\r\n"
    		+ "  // code will continue here after the time-out \r\n"
    		+ "}\r\n"
    		+ "#elif defined(ESP32) \r\n"
    		+ "  //------- Light SLEEP ESP 32 ---------------------------- \\n\"\r\n"
    		+ "void MaxBotixLightsleep(long ms) {\r\n"
    		+ "  Serial.flush();\r\n"
    		+ "  esp_sleep_enable_timer_wakeup(ms*1000ULL);\r\n"
    		+ "  esp_light_sleep_start(); \r\n"
    		+ "}\r\n"
    		+ "#endif";
	translator.addDefinitionCommand(wakeup);
    
    
  
    // Deklarationen hinzuf�gen
	translator.addDefinitionCommand("SoftwareSerial swSerMaxBot("+rxpin+", 100,true); // RXPin, TX not used, Library: https://github.com/plerup/espsoftwareserial/, Peter Lerup");   		   	
    
    String read = "int readMaxBotRaw(){ // ----------------------- Maxbotix serial protocol\r\n" + 
    		"  int reading = -1;\r\n" + 
    		"  int tout = 250;\r\n" + 
    		"  while (swSerMaxBot.available() > 0) swSerMaxBot.read(); // skip old data\r\n" + 
    		"  while ((tout > 0) & (reading == -1)) {\r\n" + 
    		"    if (swSerMaxBot.available()) {\r\n" + 
    		"      if (swSerMaxBot.read() == 'R') { // Start Message\r\n" + 
    		"        reading =swSerMaxBot.parseInt();\r\n" + 
    		"      }\r\n" + 
    		"    } \r\n" + 
    		"    else {\r\n" + 
    		"      tout--;\r\n" + 
    		"      delay(1);\r\n" + 
    		"    }\r\n" + 
    		"  }\r\n" + 
    		"  return reading;\r\n" + 
    		"}\r\n" + 
    		"int readMaxBotDistance(int maxLevel, int sleep_gpio){ // ----------------------- Maxbotix Median\r\n" + 
    		"  int tryout   = 10; // max. Versuche\r\n" + 
    		"  int minLevel = 0;\r\n" + 
    		"  if (maxLevel == 5000) minLevel = 300;\r\n" + 
    		"  if (maxLevel == 9999) minLevel = 500;\r\n" + 
    		"   \r\n" + 
    		"  if (sleep_gpio >= 0) {\r\n" + 
    		"    digitalWrite(sleep_gpio, LOW);\r\n" + 
    		"    MaxBotixLightsleep(10050); // power up the sensor\r\n" + 
    		"  }\r\n" + 
    		"\r\n" + 
    		"  int v1     = maxLevel;\r\n" + 
    		"  while (((v1 >= maxLevel) || (v1 <= minLevel)) && (tryout > 0)) {  // out of range \r\n" + 
    		"    v1 = readMaxBotRaw();\r\n" + 
    		"    tryout--;\r\n" + 
    		"    if (sleep_gpio >= 0) MaxBotixLightsleep(435);  \r\n" + 
    		"  }\r\n" + 
    		"\r\n" + 
    		"  int v2     = maxLevel;\r\n" + 
    		"  while (((v2 >= maxLevel) || (v2 <= minLevel)) && (tryout > 0)) {  // out of range \r\n" + 
    		"    v2 = readMaxBotRaw();\r\n" + 
    		"    tryout--;\r\n" + 
    		"    if (sleep_gpio >= 0) MaxBotixLightsleep(435);  \r\n" + 
    		"  }\r\n" + 
    		"\r\n" + 
    		"  int v3     = maxLevel;\r\n" + 
    		"  while (((v3 >= maxLevel) || (v3 <= minLevel)) && (tryout > 0)) {  // out of range \r\n" + 
    		"    v3 = readMaxBotRaw();\r\n" + 
    		"    tryout--;\r\n" + 
    		"  }\r\n" + 
    		"\r\n" + 
    		"  if (sleep_gpio >= 0) {\r\n" + 
    		"    digitalWrite(sleep_gpio, HIGH); // power down the sensor\r\n" + 
    		"  }\r\n" + 
    		"\r\n" + 
    		"  int val = v1; \r\n" + 
    		"  // Median Filter\r\n" + 
    		"  if (((v1<=v2) && (v2<=v3)) || ((v3<=v2) && (v2<=v1))) val = v2;\r\n" + 
    		"  if (((v2<=v3) && (v3<=v1)) || ((v1<=v3) && (v3<=v2))) val = v3;\r\n" + 
    		"  if (((v3<=v1) && (v1<=v2)) || ((v2<=v1) && (v1<=v3))) val = v1;\r\n" + 
    		"\r\n" + 
    		"  // Test out of range\r\n" + 
    		"  if (val >= maxLevel) { \r\n" + 
    		"    if (v1 < val) val = v1;\r\n" + 
    		"    if (v2 < val) val = v2;\r\n" + 
    		"    if (v3 < val) val = v3;\r\n" + 
    		"  }\r\n" + 
    		"  if (val <= minLevel) { \r\n" + 
    		"    if (v1 < maxLevel) val = v1;\r\n" + 
    		"    if (v2 < maxLevel) val = v2;\r\n" + 
    		"    if (v3 < maxLevel) val = v3;\r\n" + 
    		"  }\r\n" +
    		"  return val;\r\n" + 
    		"}\r\n" + 
    		"";
    translator.addDefinitionCommand(read);		
	
	
    // Code von der Mainfunktion
	ret = "readMaxBotDistance("+max+","+sleep+")";
    return codePrefix + ret + codeSuffix;
  }
}