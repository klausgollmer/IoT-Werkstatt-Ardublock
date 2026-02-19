package com.ardublock.translator.block.IoT;

import java.util.ResourceBundle;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.VariableDigitalBlock;
import com.ardublock.translator.block.VariablePolyBlock;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Var_2DArrayDef  extends TranslatorBlock {
	private static ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	public Var_2DArrayDef (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{  String name=label.replace(" ","");
//		String name="TEST";
		String s,z;
		TranslatorBlock tb = this.getRequiredTranslatorBlockAtSocket(0);
		z = tb.toCode();
		tb = this.getRequiredTranslatorBlockAtSocket(1);
		s = tb.toCode();
		
		String Def = "#define SIZE1_"+name+" "+z;
		translator.addHeaderFile(Def);
		Def = "#define SIZE2_"+name+" "+s;
		translator.addHeaderFile(Def);
		
		String Code = ""; 
	    return codePrefix + Code + codeSuffix;
	 	}
}

