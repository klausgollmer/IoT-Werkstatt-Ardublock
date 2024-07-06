package com.ardublock.translator.block;

import com.ardublock.translator.Translator;

public class IoTPolyBlock extends TranslatorBlock
{
	public IoTPolyBlock(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode()
	{
		
		
		//TODO take out special character
				String ret;
				ret = label.replaceAll("\\\\", "\\\\\\\\");
				ret = ret.replaceAll("\"", "\\\\\"");
				//A way to have 'space' at start or end of message
				ret = ret.replaceAll("<&space>", " ");
				//A way to have other block settings applied but no message sent
				ret = ret.replaceAll("<&nothing>", "");
				// A way to add \t to messages
				ret = ret.replaceAll("<&tab>", "\\\\t");
				ret = "\""+ ret + "\"";
		
	    return codePrefix + ret + codeSuffix;
	}

}
