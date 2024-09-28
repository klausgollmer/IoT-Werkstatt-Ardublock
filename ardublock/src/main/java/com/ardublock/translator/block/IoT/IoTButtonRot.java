package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTButtonRot extends TranslatorBlock
{

  public IoTButtonRot (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
	  String ret;
      
	  String Dis ="/* Encoder Library, for measuring quadrature encoded signals\r\n"
	  		+ " * http://www.pjrc.com/teensy/td_libs_Encoder.html\r\n"
	  		+ " * Copyright (c) 2011,2013 PJRC.COM, LLC - Paul Stoffregen <paul@pjrc.com>\r\n"
	  		+ " * Version 1.2 - fix -2 bug in C-only code\r\n"
	  		+ " * Permission is hereby granted, free of charge, to any person obtaining a copy\r\n"
	  		+ " * of this software and associated documentation files (the \"Software\"), to deal\r\n"
	  		+ " * in the Software without restriction, including without limitation the rights\r\n"
	  		+ " * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\r\n"
	  		+ " * copies of the Software, and to permit persons to whom the Software is\r\n"
	  		+ " * furnished to do so, subject to the following conditions:\r\n"
	  		+ " * The above copyright notice and this permission notice shall be included in\r\n"
	  		+ " * all copies or substantial portions of the Software.\r\n"
	  		+ " * THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\r\n"
	  		+ " * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\r\n"
	  		+ " * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\r\n"
	  		+ " * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\r\n"
	  		+ " * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\r\n"
	  		+ " * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN\r\n"
	  		+ " * THE SOFTWARE.\r\n"
	  		+ " */\r\n"
	  		+ ""
	  		+ "/*ESP32Encoder: Autor: Kevin Harrington, https://www.arduino.cc/reference/en/libraries/esp32encoder\\n"
	  		+ "redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:\r\n"
	  		+ "1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.\r\n"
	  		+ "2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.\r\n"
	  		+ "3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.\r\n"
	  		+ "4. Redistributions of any form whatsoever must retain the following acknowledgment: 'This product includes software developed by the \"Universidad de Palermo, Argentina\" (http://www.palermo.edu/).'\r\n"
	  		+ "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\r\n"
	  		+ "*/\n";
      translator.addDefinitionCommand(Dis);
	  
	  String EncDef =   "#if defined(ESP8266) \n"
		      + "// Autor Paul Stoffregen, http://www.pjrc.com/teensy/td_libs_Encoder.html\n"
		      + " #include <Encoder.h> \n"
		      + "#elif defined(ESP32) \n"
		      + "// Autor: Kevin Harrington, https://www.arduino.cc/reference/en/libraries/esp32encoder\n"
		      + " #include <ESP32Encoder.h>;\n"
		      + "#endif\n"
		      + "// encoder max range\n"
		      + "int encoder_counter_rot_min = -100;\n"
		      + "int encoder_counter_rot_max = 100;\n"
		      + "";
        translator.addDefinitionCommand(EncDef);


		EncDef = "// Helper rotary encoder\n"
		        + "#if defined(ESP8266) \n"
				+ "Encoder button_encoder(GPIO_ROTARY_B,GPIO_ROTARY_A);\n"
			    + "#elif defined(ESP32) \n"
			    +  "ESP32Encoder button_encoder; \n"
			    + "#endif\n"
				+ " \n"
				+ "int encoderRead() {\r\n"
				+ "  int wert = 0;\r\n"
				+ "#if defined(ESP8266) \r\n"
				+ "  wert = button_encoder.read();\r\n"
				+ "  if (wert > encoder_counter_rot_max) {\r\n"
				+ "     button_encoder.write(encoder_counter_rot_max);\r\n"
				+ "     wert = encoder_counter_rot_max;\r\n"
				+ "  }\r\n"
				+ "  if (wert < encoder_counter_rot_min) {\r\n"
				+ "     button_encoder.write(encoder_counter_rot_min);\r\n"
				+ "     wert = encoder_counter_rot_min;\r\n"
				+ "  }\r\n"
				+ "#elif defined(ESP32)\r\n"
				+ "  wert = button_encoder.getCount()/2;\r\n"
				+ "  if (wert > encoder_counter_rot_max) {\r\n"
				+ "     button_encoder.setCount(encoder_counter_rot_max * 2);\r\n"
				+ "     wert = encoder_counter_rot_max;\r\n"
				+ "  }\r\n"
				+ "  if (wert < encoder_counter_rot_min) {\r\n"
				+ "     button_encoder.setCount(encoder_counter_rot_min * 2);\r\n"
				+ "     wert = encoder_counter_rot_min;\r\n"
				+ "  }\r\n"
				+ "#endif\r\n"
				+ "  return wert;\r\n"
				+ "}";
		
		translator.addDefinitionCommand(EncDef);
	    EncDef ="#if defined(ESP32) \n "
			   		+  "    ESP32Encoder::useInternalWeakPullResistors = puType::up;\n"
			   		+  "    button_encoder.attachHalfQuad(GPIO_ROTARY_B, GPIO_ROTARY_A); \n "
			   		+  "    pinMode(GPIO_ROTARY_B,INPUT_PULLUP); \n "
			   		+  "    pinMode(GPIO_ROTARY_A,INPUT_PULLUP); \n "
			   		+  "#endif \n";
	    translator.addSetupCommand(EncDef);

		ret = "encoderRead()";
		

		return codePrefix + ret + codeSuffix;
  }
}