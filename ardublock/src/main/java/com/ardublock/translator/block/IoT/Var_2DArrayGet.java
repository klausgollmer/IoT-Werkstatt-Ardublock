package com.ardublock.translator.block.IoT;

import java.util.ResourceBundle;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.VariableDigitalBlock;
import com.ardublock.translator.block.VariablePolyBlock;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class Var_2DArrayGet  extends TranslatorBlock {
	private static ResourceBundle uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	public Var_2DArrayGet (Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}
	
	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		String name=label.replace(" ","");
//		String name="TEST";
		String dum ="#ifndef IOTW_A2D_DEFAULT_SIZE\n"
		 		  + "  #define IOTW_A2D_DEFAULT_SIZE 10\n"
				  + "#endif\n";
		translator.addDefinitionCommand(dum);
		String ArrayStruct ="//--------------------------------  2D Array \n"
				+ "#ifndef SIZE1_"+name+"\n"
				+ "  #define SIZE1_"+name+" IOTW_A2D_DEFAULT_SIZE\n"
				+ "#endif\n"
				+ "#ifndef SIZE2_"+name+"\n"
				+ " #define SIZE2_"+name+" IOTW_A2D_DEFAULT_SIZE\n"
				+ "#endif\n"
				+ "float   A2D_"+name+"[SIZE1_"+name+"][SIZE2_"+name+"];\n";
		translator.addDefinitionCommand(ArrayStruct);


		String Vali = "// 2D Array - check at runtime\n"
				+ "int A2DCheck(float input,int bnd) {\n"
				+ "  int index = 0;\n"
				+ "  if ((round(input) < 0) || (round(input) > (bnd-1))) {\n"
				+ "    IOTW_PRINTLN(F(\"\\n2D_Array index error: \"));\n"
				+ "    IOTW_PRINT(round(input));\n"
				+ "    IOTW_PRINT(F(\" array dim: \"));\n"
				+ "    IOTW_PRINT(bnd);\n"
				+ "  } \n"
				+ "  else index = round(input);\n"
				+ "  return(index);  \n"
				+ "}"; 
		translator.addDefinitionCommand(Vali);

		String Setup = "for (uint8_t z=0; z<SIZE1_"+name+"; z++) {\n"
				+ "  for (uint8_t s=0; s<SIZE2_"+name+"; s++) {\n"
				+ "    A2D_"+name+"[z][s] = NAN;\n"
				+ "  }\n"
				+ "}\n";
        translator.addSetupCommand(Setup);
		
        String s,z;
    	TranslatorBlock tb = this.getRequiredTranslatorBlockAtSocket(0);
		z = tb.toCode();
		tb = this.getRequiredTranslatorBlockAtSocket(1);
		s = tb.toCode();
		String Code = "A2D_"+name+"[A2DCheck("+z+",SIZE1_"+name+")] [A2DCheck("+s+",SIZE2_"+name+")]";
	    return codePrefix + Code + codeSuffix;
        }
}

