package com.ardublock.translator.block.IoT;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class IoTMessageServer_CO2  extends TranslatorBlock {

	public IoTMessageServer_CO2 (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
		String title, Sensorik;
		
		String s,s0,u0,s1,u1,s2,u2,s3,u3,v0,v1,v2,v3;
		u0="\"\"";
		u1="\"\"";
		u2="\"\"";
		u3="\"\"";
		s0="\"\"";
		s1="\"\"";
		s2="\"\"";
		s3="\"\"";
		v0="0";
		v1="0";
		v2="0";
		v3="0";

		String pass = "your password";
		
		int pos;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
	    s = translatorBlock.toCode();
 	    pos = s.indexOf(",");
        if (pos>=0) { 
          s0 = s.substring( 0, pos)+"\"";
   	      u0= "\""+s.substring(pos+1,s.length()-1)+"\"";
        } else {u0="\"\"";s0=s;}

		translatorBlock = this.getRequiredTranslatorBlockAtSocket(1);
	    v0 = translatorBlock.toCode();
        
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(2);
	    String l1 = translatorBlock.toCode();
        
		translatorBlock = this.getRequiredTranslatorBlockAtSocket(3);
	    String l2 = translatorBlock.toCode();

	    translatorBlock = this.getTranslatorBlockAtSocket(5);
	    if (translatorBlock!=null) {
	    	v1 =translatorBlock.toCode();
			translatorBlock = this.getTranslatorBlockAtSocket(4);
		    s = translatorBlock.toCode();
	 	    pos = s.indexOf(",");
	        if (pos>=0) { 
	          s1 = s.substring( 0, pos)+"\"";
	   	      u1= "\""+s.substring(pos+1,s.length()-1)+"\"";
	        } else {u1="\"\"";s1=s;}
	    }
	    
	    translatorBlock = this.getTranslatorBlockAtSocket(7);
	    if (translatorBlock!=null) {
	    	v2 =translatorBlock.toCode();
			translatorBlock = this.getTranslatorBlockAtSocket(6);
		    s = translatorBlock.toCode();
	 	    pos = s.indexOf(",");
	        if (pos>=0) { 
	          s2 = s.substring( 0, pos)+"\"";
	   	      u2= "\""+s.substring(pos+1,s.length()-1)+"\"";
	        } else {u2="\"\"";s2=s;}
	    }

	    translatorBlock = this.getTranslatorBlockAtSocket(9);
	    if (translatorBlock!=null) {
	    	v3 =translatorBlock.toCode();
			translatorBlock = this.getTranslatorBlockAtSocket(8);
		    s = translatorBlock.toCode();
	 	    pos = s.indexOf(",");
	        if (pos>=0) { 
	          s3 = s.substring( 0, pos)+"\"";
	   	      u3= "\""+s.substring(pos+1,s.length()-1)+"\"";
	        } else {u3="\"\"";s3=s;}
	    }

	    translatorBlock = this.getRequiredTranslatorBlockAtSocket(10);
	    if (translatorBlock!=null) {
	    	pass=translatorBlock.toCode();
	    }

	    translator.addDefinitionCommand("int doCal=0;  // Kalibriermerker");
		String calfun = "void CO2_Kalibrierfunktion(){ // Kalibrierfunktion\n";
		translatorBlock = getTranslatorBlockAtSocket(11);
	    
		while (translatorBlock != null)
		{
			calfun = calfun + translatorBlock.toCode();
			translatorBlock = translatorBlock.nextTranslatorBlock();
		}
		calfun = calfun + "}\n\n";
		translator.addDefinitionCommand(calfun);
	    
	    

		translator.setHTTPServerProgram(true);;
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WiFi.h> \n#elif defined(ESP32) \n #include <WiFi.h>\n#endif\n");		
		translator.addHeaderFile("#if defined(ESP8266)\n #include <ESP8266WebServer.h> \n#elif defined(ESP32) \n #include <WebServer.h>;\n#endif\n");		
		
		translator.addSetupCommand("Serial.begin(115200);");
		translator.addSetupCommand("//------------ HTML-Server initialisieren");
		translator.addSetupCommand("server.on(\"/\", serverHomepage);");
		//translator.addSetupCommand(" server.begin();// Server starten");
		
			    		
		String HomepageCode="//------------------------------ Server Hompage html-Code\n" + 
				"const char INDEX_HTML_START[] =\n" + 
				" \"<!DOCTYPE HTML>\"\n" + 
				"  \"<html>\"\n" + 
				"  \"<META HTTP-EQUIV=\\\"refresh\\\" CONTENT=\\\"20\\\">\"\n" + 
				"   \"<head>\"\n" + 
				"   \"<meta name = \\\"viewport\\\" content = \\\"width = device-width, initial-scale = 1.0, maximum-scale = 1.0, user-scalable=0\\\">\"\n" + 
				"     \"<title>IoT-Werkstatt Umwelt-Campus Birkenfeld</title>\"\n" + 
				"   \"</head>\"\n" + 
				"    \"<body>\"\n" + 
				"    \"<CENTER>\";\n" + 
				"\n" + 
				
				"const char INDEX_HTML_SUBMIT[] =\n" + 
				"     \"<FORM action=\\\"/\\\" method=\\\"post\\\">\"\n" + 
				"     \"<P>\"\n" + 
				"        \"Password: \"\n" + 
				"        \"<INPUT type=\\\"text\\\" name=\\\"message\\\">\"\n" + 
				"        \"<INPUT type=\\\"submit\\\" value=\\\"Calibrate\\\">\"\n" + 
				"      \"</P>\"\n" + 
				"     \"</FORM>\";\n"+
                "\n" +
				"const char INDEX_HTML_END[] =\n" + 
				"    \"</CENTER>\"\n" + 
				"   \"</body>\"\n" + 
				"  \"</html>\";\n" + 
				" \n"+
				" String cal_passwort = "+pass+"; // Kalibrierpasswort\n"+
				" String cal_message  = \"\";      // Nachricht\n"+
				" \n"+
				"//------------------------------ Server Ausgabe Messwerte in Form einer html-Tabelle\n" + 
				"\n" + 
				"String messwertTabelle(){ \n" + 
				"  String unit1="+u1+";\n" + 
				"  String unit2="+u2+";\n" + 
				"  String unit3="+u3+";\n" + 
				"\n" + 
				"  String nam1="+s1+";\n" + 
				"  String nam2="+s2+";\n" + 
				"  String nam3="+s3+";\n" + 
				"\n" + 
				"  float mess1="+v1+";\n" + 
				"  float mess2="+v2+";\n" + 
				"  float mess3="+v3+";\n" + 
				"  //String html =  \"<h2>Aktuelle Informationen:</h2>\";\n" + 
				"  String html = \"<h2><table>\";\n" + 
				"  if (nam1.length()>0)\n" + 
				"     html = html +  \"<tr><td>\" + nam1 + \":</td> <td>\" + String(mess1) + \"</td><td>\"+ unit1 +\" </td></tr>\";\n" + 
				"  if (nam2.length()>0)\n" + 
				"     html = html +  \"<tr><td>\" + nam2 + \":</td> <td>\" + String(mess2) + \"</td><td>\"+ unit2 +\" </td></tr>\";\n" + 
				"  if (nam3.length()>0)\n" + 
				"     html = html +  \"<tr><td>\" + nam3 + \":</td> <td>\" + String(mess3) + \"</td><td>\"+ unit3 +\" </td></tr>\";\n" + 
				"  html=html+\"</table></h2>\";\n" + 
				"  return html;\n" + 
				"}\n" + 
				"//------------------------------ Server Ausgabe Bild in Abhängigkeit von Messert und Grenzen\n" + 
				"void serverSendFigure(){ \n" + 
				"  String unit ="+u0+";\n" + 
				"  String nam  ="+s0+";\n" + 
				"  float mess="+v0+";\n" + 
				"  float limit1 = "+l1+";\n" + 
				"  float limit2 = "+l2+";\n" + 
				"  if (mess < limit1) // grün\n" + 
				"     server.sendContent(F(\"<img src= 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEBLAEsAAD/2wBDAFk9Q05DOFlOSE5kXllphd6QhXp6hf/CzaHe////////////////////////////////////////////////////2wBDAV5kZIV1hf+QkP//////////////////////////////////////////////////////////////////////////wAARCAExASwDASIAAhEBAxEB/8QAGQABAAMBAQAAAAAAAAAAAAAAAAIDBAEF/8QAKhABAAIBAgYBBAMBAQEAAAAAAAECAxExBBIhQVFhcRMiMoFCkaFSsSP/xAAXAQEBAQEAAAAAAAAAAAAAAAAAAQID/8QAGxEBAQEBAAMBAAAAAAAAAAAAAAERAhIxUUH/2gAMAwEAAhEDEQA/ANYAAAAAAAAAAjfJTHGtrRDLk4yZ6Y409yDZMxEaz0U34rFX+Ws+mG97XnW1plEGq3Gz/GkftXbiss/y0+IUgJTlyTve39ozMzvMgAADsXtG1pj9pxnyxtef31VgNFeMyRvEStpxtJ/Ksx/rEA9SmWl/xtEpPJW4+JyU780eJB6Iox8XS/S32z72X7gAAAAAAAAAAAAAAAAAqzZ64o69bdoBZa0VjW06QyZuMmemLp7lRky2y21tP6QAmZtOszMz7BKuO1vUBuIuxWZ2hdXHWPfymmsXv4ojFM7zonGKveZlYDPlUYx1js7yx4h0E1zSPEGkeIdBEZpXxDk46z20TBdqqcPiUJx2jtr8NAavlWUaZrE7wrti/wCZNbnUVLMWe+L8Z6eJVzExOkwK09HDxFMvT8beJWvJacHFzX7cnWPPcG0ImLRExOsSAAAAAAAAAAAAy8TxHLrSk9e8+AS4jiYp9tOtu8+GGZmZ1mdZkAHa1m09EqY+brPSF0RERpCM3rEa44r7lMBzt0ARAAAAAAAAAAAAHJiJjSYVXxTHWvX0uFWWxlF96Rb1Ki0TWdJV1l1Zhz2xT0617w9DHeuSvNWdYeWniy2xW1rt3jyK9MRx5K5KRaqQAAAAAAAK8+WMVNe87QCvis/045Kz90/4wkzNpmZnWZAFmPH3t/Rjp/Kf0uRjrr8gAjmAAAAAAAAAAAAAAAAAAI2rFo0lIUZrVms6S40WrFo0lRas1nSVdeetTw5ZxX1jbvD0a2i9YtWdYl5S/hc307ctp+2f8Gm8AAAAACZiImZ6RDzM2WcuSbdu0NPG5dIjHHzLGAnjpzTrO0I1rzTpDREREaQjPVx0BHIAAAAAAAAAAAAAAAAAAAAAARvWLR7SFVlmNJ0kW5aaxzR+1Susut3B5uevJaesbe4aHlUvNLxaN4epS0XpFo2kV0ABy9opSbTtDrLx2TSIxx36yDJe03tNp3lwSpXmtp2BZirpXXvKwEcbdAEQAAAAAAAAAAAAAAAAAAAAAAAAZ715badmhDLXWvuFa5uVQ1cFk0mcc9+sMrtLTS8WjeJV1eqOVtFqxaNpjV0B5ma/1Mtre+jfxF+TDae+mkPNAXYq6V18qYjWdGmOkaJWO66AjmAAAAAAAAAAAAAAAAAAAAAAAAAAAAzXry2mHFuaOkSqadpdjdwV+bHNZ3rLQ8/hL8ueI7W6PQFZeOt9ta+Z1Y1/G21z6eIUAnijW/wvVYY6TK1HLr2AIyryXmsxojGae8I5Z1vKKus5mLoy19wlF6z3hnDE8I1DLEzGzRj15I1kZvOJAIyAAAAAAAAAAAACN9eWdN2eZmd5lWpzrTNojeYRnLWO+qgMa8ItnNHaHaZJtbSdFKWOdLwLeZjQAjkjeNaTDO1Mto0tMLHTh2s8totHadXqxOsaw8l6XD25sFJ9aK2w8ROue8+1bt51vafbgL8UaUhNGn4R8JI432AIiM0rO8IzijtMrBV2qZwz2mEZx2js0BrXlWWYmN4aYjSIgdEvWgCMgAAAAAAAAAAAOM/JbxLSK1LiiMVp9Oxh8yuDTyquMVY9pRWsbRCQJtAEQZ8saXloUZvy/Sxvj2g3cHaPo6T2lhXYb8tZj2ropncJ3Aaa/jHw65X8Y+HUcQBEAAAAAAAAAAAAAAAAAAAAAAAAAAAAFObeFynNvCtc+1ZE6Borq7eNL2j24nnjTPePaANFPwj4SQxTrSE0cb7AEQAAAAAAAAAAAAAAAAAAAAAAAAAAAAUZvyj4Xs+Wdbysb59orcVOasz7VN3B1/8AjrPeVdFHGV0z6+Y1UNnHV6Ut+mMFuGekwtUYZ0vp5Xo5dewBGQAAAAAAAAAAAAAAAAAAAAAAAAAAABmtOtplfedKzLOsdOB6XDV5cFI9avNrHNaIjvOj1ojSIiOytq+Jpz4LR3jq816zy8tPp5bV8T0BGJ0mJaY6wzLsVta6eErHcWAI5gAAAAAAAAAAAAAAAAAAAAAAAAAAAKs09IhU7eea0y407SZF3CU5s8T2r1egzcFTTHNp/lLSKMnHY9skfEtaOSkXpNZ7g8tLHblt6ctWa2ms7w4DUIYrc1dO8Jo42YAIgAAACucsROkxJ9aviTJTm6xupV0klXfVr4lOJiY1hmdi012kwvHxpFVc3/Uf0nF6ztIxZYkAiAIzesbyok5a0VjWVds3/Mf2qmZmdZkxuc/V31q+JPrV8SpSpXmt6GvGL4nWNXQRyAAAAEMluWvuU2fJbmt6VrmbUXa1m1orG8uNXBY9bTknt0hXVrpWKVisbRGjoAAAycbi2yR8SyPWtEWrMT1iXmZcc4sk1n9AjW3LbVoidY1hmTxX0nSdkZ6mrwEcgAAABC+OLepTFWXGa1ZrvDjShbFWduhrc7+qRKcdo9/CMxMbq1prMbS7zW8z/bgKazPcCImdoAEox2n0srirG/UZvUiulJt8L6xFY0h0Ri9aAIyAAAje0VjVRHLfSOWN5UkzrOsiu0mO0rN7RWN5enjpGOkVjso4PDy1+pbedvhpFAAAAFXEYfq06flGy0B5MxMTpO8DbxWDmjnpH3RvHliBbiv/ABn9LWVbjya9Lf2jn1z+xaAjAAAAAAA46Ajy18Qclf8AmEhV1Hlr4hIBABAAAAABy1orGsqFpisayz2tNp1kvabS4rrzzgu4bD9W+s/jG/tDFinLflj9z4elSkUrFax0gadAAAAAAAAZOK4fe+OPmGsB5I2cTw2v344694YwWUyadLbLY67MztLzWfXhGLz8aRGt4tskMACIAAAAAAAAAAAADiu+XtX+1WTU73ivz4UWtNp1lzcV0nOCWLHbLblrH78O4sVsttKx8z4ejix1xV5a/ufI05ix1xU5a/ufKYAAAAAAAAAAAKM/DVyfdXpb/wBXgPKvS1Lcto0lx6mTHXJXS0aseXhLU60+6P8AQZ065ZjfqgCWa0VvW20pMqUXtHdMZvHxoFUZvMf0lGSs99BnxqY5FonaYdGQBAHNYjdyb1jvCqkK5y17ayhOW07dBfGrpmI3QtliNuqmZmd51DGpx9dtabby4JY8d8k6VjVW0V+DhrZOtvtr/wCr8PC1p1v91v8AIaAcpStK8tY0h0AAAAAAAAAAAAAAAAAV5MGPL+UaT5hlycJevWv3R/rcA8mYms6TExPseralbxpasT8qL8Hjn8Zmv+gwjRbg8kfjMWVWw5a70t/QIBMTG8aAGs+ZNZ8gADsUtbasz8QDgtrw2W38dPldTgv+7/qAZE8eHJk/Gs6eW6nD4qbV1nzPVaDNj4Osdck6z4jZprWKxpWIiPQAAAAAAAAAAAAAAAAAAAAAAAAAAAjk2YswAoSpuANmFoAAAAAAAAAAAAAAAAAH/9k='alt=''>\"));\n" + 
				"    else\n" + 
				"     if (mess>limit2) // rot \n" + 
				"        server.sendContent(F(\"<img src='data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEBLAEsAAD/2wBDAFk9Q05DOFlOSE5kXllphd6QhXp6hf/CzaHe////////////////////////////////////////////////////2wBDAV5kZIV1hf+QkP//////////////////////////////////////////////////////////////////////////wAARCAExASwDASIAAhEBAxEB/8QAGQABAAMBAQAAAAAAAAAAAAAAAAECAwQF/8QAKhABAQABAwMDBAMAAwEAAAAAAAECAxExBCFREkFhIjJxgRNCoSORsVL/xAAYAQEBAQEBAAAAAAAAAAAAAAAAAgEDBP/EAB8RAQEBAQADAQEAAwAAAAAAAAABEQIDITFBURITMv/aAAwDAQACEQMRAD8A6wAAAAAAAAABXPUw05vnlI5dTrLxp47fNB2WyTe9mWfVaWP9t78ODPUzzu+WVqoOrLrb/XD/ALZ5dVq3+234jEBa6upec8v+1d7ebQAABMzynGVn7XmvqzjO/vuzAdGPWak5krXDrcL92Nx/1xAPUw1cM/tylWeS10+p1MPf1TxQeiMNPq8M+2X0354bgAAAAAAAAAAAAAAAAAy1tfHSnfvl4BpllMZvldo5NbrLe2l2+aw1NXLVy3yv6UAtuV3ttvyCZhaNkt+IJLeI0mEnysnXSeP+s5p33WmnFhm1c45iPTj4TtPEAVkNobTwDBHpniFwx8JGmRS6fiq3DKezUNRfHKxG1kvMUun4VqL47PijTS189L7b28VnZZyNc3o6PUYavbjLxWryXTodXcfp1O88g7QlmUll3lAAAAAAAAAAAAcvU9R6d8ML3974BbqOpmH04d8ve+HDbbd7d7QAJLeFscN+94aSbcMtdOeN91GOEnzUgl2kk+ADGgAAAAAAAAAAAFkvKmWHhcbqbzKxGuWMv5ZWWXuqXXDrm8tNHXy0r274+8ehp546mPqxu8eWvpauWllvj+55al6Yrp6mOphMsVgAAAAAAAZ6+rNLDf3vEBn1Wv6J6Mb9V/xwltyttu9oAvhh70wx96um1144/aAJdgAAAAAAAAAAAAAAAAAAslncBjGyy9xtZLNqxssu1XK4d8400dW6We84949HHKZ4zLG7yvKb9Lrfx5enK/Tf8ah3gAAAAAWyS29pHma2rdXU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' alt=''>\"));\n" + 
				"       else // gelb\n" + 
				"        server.sendContent(F(\"<img src='data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEBLAEsAAD/2wBDAP//////////////////////////////////////////////////////////////////////////////////////2wBDAf//////////////////////////////////////////////////////////////////////////////////////wAARCAExASwDASIAAhEBAxEB/8QAGAABAQEBAQAAAAAAAAAAAAAAAAMCAQT/xAAlEAEBAAICAQQDAQEBAQAAAAAAAQIRMVEhEkFxkWGBsaEy0eH/xAAXAQEBAQEAAAAAAAAAAAAAAAAAAQID/8QAHREBAQEAAgMBAQAAAAAAAAAAAAERMVECEmEhQf/aAAwDAQACEQMRAD8AoAAAAAAAAAAOWycsXPr/AEFGbljPffwlbbzXAUufU+2fXl2yA7u937cAAADd7d9WXdcAb9d/Famc95pIBeWXiuvO1MrPz8gsMTOXnw2AAAAAAAAAAAAAAAAADNyk+ega4TufX2xbby4ADUxt/AMuyW8RSYyflpn2E5hffw76I2JtHPTOjU6joihqdADmp1HPTGg2jHo6rNxs/wDiou1EBayXlm4dNbBN2ZWf+FlnLii0yl/F6aedvHP2v2CoAAAAAAAAAAAAJ5Ze0/YO5Za8Tn+JAA7Jbw1jjvzeFOEtGZjJ+a0DCgAAAAAAAAAAAAAGt8p3Dr6UFlwQFbjL8p2WctS6juOVnx0rLL5iDstlUXHJZZuOgAAAAAAAzldT+A5llrxOf4kADeOPvfoxx97+lGbf5AAZUAAAAAAAAAAAAAAAAAAcsldARs04tZuaSs1dNy6hjdX+ry78vO3jlq69qoqAAAAAAhld3f03nfb7TAaxx359nJN3S0mvCWgAwoAAAAAAAAAAAAAAAAAAAAAA5ZuOgICmc9/tNuXUVwu/HvP42hLq7Wl3NqOgAOW6m3U877fsE7d3YO4zdBTGam+2gc1AAAAAAAAAAAAAAAAAAAAAAAAAAEcpq/xZnKbnwsuVElML7fSZLq7bHoCefIAhbu2rZXUqACuE8b7S5XZ8gAZUAAAAAAAAAAAAAAAAAAAAAAAAAAABGzVrimc4qbc4RXC+NdNo4XV+VlE87xE28+fiMA1hPPwqxhxa2xeQARQAAAAAAAAAAAAAAAAAAAAAAAAAAAHLNyoro3xa14o5w9DzrY/8xoSy5vy47eb8uArjxGnJxPh1zvKgAAAAAAAAAAAAAAAAAAAAAAAAAAAAACWXNVTz5/S+PKMK4Xx+0mpdRsZABacT4dcnE+HXNQAGMsrLpn138GfLLckxGvXfweu/hkMnQ167+FJ5kqK2PE+EsHQGVAAAAAAAAAAAAL4lqfrvUbvF+Ki1IjfrvUPXeowLk6G/Xeo7MrbJ4Tax/wCoWQVAYUTz5iiefMWcowA2O3m/LjuXN+XAWnE+HWceI053lQAHLjLy56cev9rQbRn049f7T049f7WhdvYz6cev9rQIAAAAAAAAAAAAAADmp1HQHNTqGp1HQHNTqGp1HQAABPPn9KJZc1fHlGWpNxlXCeP22M58sKZzipgphxW0sL5+VWLyACKAAAAAAAAAAAAAAAAAAAAAAAAAAAAI3zarbqVFrxQWx/5iL0NDOU3Ki9CFmrYDk8Xa6CuF3NdM+Q0AyoAAAAAAAAAAAAAAAAAAAAAAAAAAADGd4ibuV3XG5wjWE3fjysxhPG+21BPOcX9KOWbmgQaxuqzwAuM43c/Mac1AAAAAAHNzuO8s3CfC/g7udw3O4x6L256b0ZO0U3O4bncS1eq5qrn0W3O4bncRD1+i253Dc7iJq9Hr9FtzuG53EvTeq76KZOxTc7ju4xMO79NSScJ+K6AgAAAAM5XU+WksrurJtRknnwKYT3+mxSTU0AAACec9/tN6OULNXQEurtblBvHLXi8JYKAMKAAAAAAAAAAAAAAAAAAAAAAA5bqAzldeEzkbkxCTd0vJqaZwnv8ATagAAAAzlNz8+zQDziueO/M590gUxy9r+m0FMcva/bNn9g2AyoAAAAAAAAAAAAAAAAAADlsnIFuvNSt3S3bjcmINY47v4jkm7paTU1FHQAAAAAAAE8sfefuKAPOKZY+8+kwbxy9qog7LYzZ0LDksrrKgAAAAAAAAAAAAAAMXPr7XNGrlJ89JW28uDUmIOyW3UJLeFpJJqKEkk06AAAAAAAAAAADOWO/xWgELLOXF7JeU7hZx5/oMNzOznywAtMpXUHZlZ7s+vQsMTPufTvqnaZVaHNzuOoAAAOeqdg6MeufLlzvwuVFGLnPbynu3kX1HbbeXB2S3hocaxxt58RuYSc+a2DkkniOgAAAAAAAAAAAAAAAADNxl/wDWLhZx5/qoDzi9kvLNwnt4BIbuF/FZ9N6oOAAboAAO6vVBwa9OXTUw7v0CbslvEVmMnt9tAxMJ7+W+AAAAAAAAAAAAAAAAAAAAAAAAAAAByp0AYdgApi2AAAAAAAAAAAAAAAAAP//Z' alt=''>\"));\n" + 
				"     String val = String(\"<h1>\")+String(nam)+String(\": \")+String(mess)+String(\" \")+unit+ String(\"</h1>\");\n" + 
				"     server.sendContent(val);\n" + 
				" }\n" + 
				"//------------------------------ Server Unterprogramm zur Bearbeitung der Anfragen\n" + 
				"void serverHomepage() { \n" + 
			    " if (server.hasArg(\"message\")) {// Wenn Kalibrierpasswort eingetroffen,\n" + 
			    "   String input = server.arg(\"message\");     // dann Text vom Client einlesen\n" + 
			    "   if (input == cal_passwort){\n" + 
			    "      cal_message = \"do calibration (20 s wait)\";\n" + 
			    "      doCal = 1;\n" + 
			    "   } else\n" + 
			    "      cal_message = \"wrong password\";\n" + 
			    " } else cal_message = \"\";\n	"+
				" server.setContentLength(CONTENT_LENGTH_UNKNOWN);\n" + 
				" server.send ( 200, \"text/html\", INDEX_HTML_START);\n" + 
				" serverSendFigure();             // Ampel integrieren\n" + 
				" server.sendContent(F(\"<img src=' data:image/jpeg;base64,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' alt=''>\"));\n" + 
				" server.sendContent(messwertTabelle());\n" + 
				" if (cal_passwort != \"your password\") {\n" + 
				"  server.sendContent(INDEX_HTML_SUBMIT);\n" + 
				"  server.sendContent(cal_message);\n"+
				" }\n"+
				" server.sendContent(INDEX_HTML_END);\n" + 
				"}\n" + 
				"";
		
		String DefWeb = "#if defined(ESP8266)\n" + 
				        "    ESP8266WebServer server(80);\n" +
			        	"#elif defined(ESP32)\n"+
				        "    WebServer server(80);\n" +
				        "#endif\n"; 
		translator.addDefinitionCommand(DefWeb);
		
		
		translator.addDefinitionCommand("String matrixausgabe_text  = \" \"; // Ausgabetext als globale Variable\n");
		translator.addDefinitionCommand("volatile int matrixausgabe_index = 0;// aktuelle Position in Matrix\n");
   	    translator.addDefinitionCommand(HomepageCode);

		
	    String ret ="//Block------------------------------ HTML-Server\n"
	    		  +" server.begin();// Server starten\n"
	              + "server.handleClient(); //Homepageanfragen versorgen\ndelay(1);\n"
	    		  + "if (doCal) {\n" + 
	    		  "   CO2_Kalibrierfunktion(); // Kalibrierfunktion aufrufen \n" + 
	    		  "   doCal=0; \n" + 
	    		  "}";
        return codePrefix + ret + codeSuffix;
	 	}
}

