package com.ardublock.translator.block.IoT;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SigProc_absHum extends TranslatorBlock
{

  public SigProc_absHum (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
  {
    super(blockId, translator, codePrefix, codeSuffix, label);
  }

  public String toCode() throws SocketNullException, SubroutineNotDeclaredException
  {
       
    String sub= "/* Formeln zur Umrechnung der Feuchte aus:\r\n" + 
    		"  \"Analyse zu einer einfachen Möglichkeit der Prognose von\r\n" + 
    		"  Feuchteproduktion im Wohnraum auf Basis von Messdaten\"\r\n" + 
    		"  Hanife Turan, Masterarbeit TU München\r\n" + 
    		"  https://mediatum.ub.tum.de/doc/1485555/1485555.pdf\r\n" + 
    		"*/\r\n" + 
    		"float DruckAllg(float TinC) {\r\n" + 
    		"  // Sättigungsdampfdruck nach Glück\r\n" + 
    		"  // Fallunterscheidung: Druck abhängig von Temperatur\r\n" + 
    		"  float y;\r\n" + 
    		"  if (TinC > 0) {\r\n" + 
    		"    y = 611.0 * exp((-1.913e-04) + 0.07258 * (TinC) -\r\n" + 
    		"                    (2.939e-04) * pow((TinC), 2) + (9.841e-07) * pow((TinC), 3) - (1.92e-9) * pow((TinC), 4));\r\n" + 
    		"  } else if (TinC <= 0) {\r\n" + 
    		"    y = 611.0 * exp((-4.909965e-04) + 8.183197e-02 * (TinC) -\r\n" + 
    		"                    (5.552967e-04) * pow((TinC), 2) - (2.228376e-05) * pow((TinC), 3) -\r\n" + 
    		"                    (6.2118082e-07) * pow((TinC), 4));\r\n" + 
    		"  }\r\n" + 
    		"  return (y);\r\n" + 
    		"}\r\n" + 
    		"\r\n" + 
    		"float Luftdichte(float TinC, float absFinKg, float p0) {\r\n" + 
    		"  // Formel zur Berechnung der Luftdichte\r\n" + 
    		"  // in Abhängigkeit der Temperatur absoluten Feuchte und dem Luftdruck\r\n" + 
    		"  float y = ((1 + absFinKg) / (0.6222 + absFinKg)) * (p0 / (461.5 * (TinC + 273.15)));\r\n" + 
    		"  return (y);\r\n" + 
    		"}\r\n" + 
    		"\r\n" + 
    		"\r\n" + 
    		"float absFeuchte(float TinC, float relF, float p0) {\r\n" + 
    		"  // Absoluter Wassergehalt [g/m3]\r\n" + 
    		"  // relF in [-] Temperatur in Celsius pSat in Pascal\r\n" + 
    		"  float psat = DruckAllg(TinC);\r\n" + 
    		"  float p   = relF/100.0 * psat;\r\n" + 
    		"  float absFinKg = 0.6222 * (p / (p0 - p)); // kg/kg\r\n" + 
    		"  float ro  = Luftdichte(TinC, absFinKg, p0);\r\n" + 
    		"  float y = absFinKg * ro * 1000;\r\n" + 
    		"  return (y);\r\n" + 
    		"}\r\n" + 
    		"\r\n" + 
    		"" ; 
    
    
	TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
    String temp = translatorBlock.toCode();
    
    translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
    String hum = translatorBlock.toCode();
   	
    
   	translator.addDefinitionCommand(sub);
 
    // Code von der Mainfunktion
    String ret = "absFeuchte("+temp+","+hum+",101325.0)";
    return codePrefix + ret + codeSuffix;
  }
}