package com.ardublock.ui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.ardublock.core.Context;
import com.ardublock.translator.AutoFormat;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNameDuplicatedException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;
import com.ardublock.util.ArduinoIDE2;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Workspace;

public class GenerateCodeButtonListener implements ActionListener
{
	private JFrame parentFrame;
	private Context context;
	private Workspace workspace; 
	private ResourceBundle uiMessageBundle;
	
	public GenerateCodeButtonListener(JFrame frame, Context context)
	{
		this.parentFrame = frame;
		this.context = context;
		workspace = context.getWorkspaceController().getWorkspace();
		uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
	}
	
	public void actionPerformed(ActionEvent e)
	{
		boolean success;
		success = true;
		Translator translator = new Translator(workspace);
		translator.reset();
		
		Iterable<RenderableBlock> renderableBlocks = workspace.getRenderableBlocks();
		
		Set<RenderableBlock> loopBlockSet = new HashSet<RenderableBlock>();
		Set<RenderableBlock> subroutineBlockSet = new HashSet<RenderableBlock>();
		Set<RenderableBlock> scoopBlockSet = new HashSet<RenderableBlock>();
		StringBuilder code = new StringBuilder();
		
		
		for (RenderableBlock renderableBlock:renderableBlocks)
		{
			Block block = renderableBlock.getBlock();
	
		
			if (!block.hasPlug() && (Block.NULL.equals(block.getBeforeBlockID())))
			{
				
				if(block.getGenusName().equals("loop"))
				{
					loopBlockSet.add(renderableBlock);
				}
				if(block.getGenusName().equals("loop1"))
				{
					loopBlockSet.add(renderableBlock);
				}
				if(block.getGenusName().equals("loop2"))
				{
					loopBlockSet.add(renderableBlock);
				}
				if(block.getGenusName().equals("loop3"))
				{
					loopBlockSet.add(renderableBlock);
				}
				if(block.getGenusName().equals("program"))
				{
					loopBlockSet.add(renderableBlock);
				}
				if(block.getGenusName().equals("setup"))
				{
					loopBlockSet.add(renderableBlock);
				}
				// orginal: if (block.getGenusName().equals("subroutine"))
				if (block.getGenusName().equals("subroutine") || block.getGenusName().equals("sub_return") || block.getGenusName().equals("TTN_RxCallback") )	//Check if subroutine with return				
				{
					String functionName = block.getBlockLabel().trim()+"_gen";
					try
					{
						translator.addFunctionName(block.getBlockID(), functionName);
					}
					catch (SubroutineNameDuplicatedException e1)
					{
						context.highlightBlock(renderableBlock);
						//find the second subroutine whose name is defined, and make it highlight. though it cannot happen due to constraint of OpenBlocks -_-
						JOptionPane.showMessageDialog(parentFrame, uiMessageBundle.getString("ardublock.translator.exception.subroutineNameDuplicated"), "Error", JOptionPane.ERROR_MESSAGE);
						return ;
					}
					subroutineBlockSet.add(renderableBlock);
				}
				
				if (block.getGenusName().equals("scoop_task"))
				{
					translator.setScoopProgram(true);
					scoopBlockSet.add(renderableBlock);
				}
				if (block.getGenusName().equals("scoop_loop"))
				{
					translator.setScoopProgram(true);
					scoopBlockSet.add(renderableBlock);
				}
				if (block.getGenusName().equals("scoop_pin_event"))
				{
					translator.setScoopProgram(true);
					scoopBlockSet.add(renderableBlock);
				}
				
			}
		}
		if (loopBlockSet.size() == 0) {
			JOptionPane.showMessageDialog(parentFrame, uiMessageBundle.getString("ardublock.translator.exception.noLoopFound"), "Error", JOptionPane.ERROR_MESSAGE);
			return ;
		}
		if (loopBlockSet.size() > 1) {
			for (RenderableBlock rb : loopBlockSet)
			{
				context.highlightBlock(rb);
			}
			JOptionPane.showMessageDialog(parentFrame, uiMessageBundle.getString("ardublock.translator.exception.multipleLoopFound"), "Error", JOptionPane.ERROR_MESSAGE);
			return ;
		}

		try
		{
			
			for (RenderableBlock renderableBlock : loopBlockSet)
			{
				translator.setRootBlockName("loop");
				Block loopBlock = renderableBlock.getBlock();
				code.append(translator.translate(loopBlock.getBlockID()));
			}
			
			for (RenderableBlock renderableBlock : scoopBlockSet)
			{
				translator.setRootBlockName("scoop");
				
				Block scoopBlock = renderableBlock.getBlock();
				code.append(translator.translate(scoopBlock.getBlockID()));
			}
			
			for (RenderableBlock renderableBlock : subroutineBlockSet)
			{
                boolean add_ok = false;
				Block subroutineBlock = renderableBlock.getBlock();
				
				if (subroutineBlock.getGenusName().equals("subroutine")){
					translator.setRootBlockName("subroutine");
				}
				else if (subroutineBlock.getGenusName().equals("sub_return")){
					translator.setRootBlockName("sub_return");
				} else if (subroutineBlock.getGenusName().equals("TTN_RxCallback")){
					translator.setRootBlockName("TTN_RxCallback");
				}
				
                //	each subroutine definition must have at least one call	
				for (RenderableBlock testBlock : renderableBlocks)
				{
					Block block = testBlock.getBlock();
					if (block.getGenusName().equals(subroutineBlock.getGenusName()+"-ref")) {
					//	System.out.println("Genius ok");
						if (block.getBlockLabel().equals(subroutineBlock.getBlockLabel())) {
					//		System.out.println("label ok");
							System.out.println("Genius " + block.getGenusName());
							System.out.println("Label " + block.getBlockLabel());
					
							System.out.println("before "+block.getBeforeBlockID());
							System.out.println("before NULL"+Block.NULL.equals(block.getBeforeBlockID()));
		                     if ((block.hasPlug() || (!Block.NULL.equals(block.getBeforeBlockID())))) 			
							   add_ok = true;		
						}
					}
					//System.out.println("Genius " + block.getGenusName());
					//System.out.println("Label " + block.getBlockLabel());
				}
				
				if (add_ok == true) code.append(translator.translate(subroutineBlock.getBlockID()));
			}
			
			translator.beforeGenerateHeader();
			code.insert(0, translator.genreateHeaderCommand());
		}
		catch (SocketNullException e1)
		{
			e1.printStackTrace();
			success = false;
			Long blockId = e1.getBlockId();
			Iterable<RenderableBlock> blocks = workspace.getRenderableBlocks();
			for (RenderableBlock renderableBlock2 : blocks)
			{
				Block block2 = renderableBlock2.getBlock();
				if (block2.getBlockID().equals(blockId))
				{
					context.highlightBlock(renderableBlock2);
					break;
				}
			}
			JOptionPane.showMessageDialog(parentFrame, uiMessageBundle.getString("ardublock.translator.exception.socketNull"), "Error", JOptionPane.ERROR_MESSAGE);
		}
		catch (BlockException e2)
		{
			e2.printStackTrace();
			success = false;
			Long blockId = e2.getBlockId();
			Iterable<RenderableBlock> blocks = workspace.getRenderableBlocks();
			for (RenderableBlock renderableBlock2 : blocks)
			{
				Block block2 = renderableBlock2.getBlock();
				if (block2.getBlockID().equals(blockId))
				{
					context.highlightBlock(renderableBlock2);
					break;
				}
			}
			JOptionPane.showMessageDialog(parentFrame, e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		catch (SubroutineNotDeclaredException e3)
		{
			e3.printStackTrace();
			success = false;
			Long blockId = e3.getBlockId();
			Iterable<RenderableBlock> blocks = workspace.getRenderableBlocks();
			for (RenderableBlock renderableBlock3 : blocks)
			{
				Block block2 = renderableBlock3.getBlock();
				if (block2.getBlockID().equals(blockId))
				{
					context.highlightBlock(renderableBlock3);
					break;
				}
			}
			JOptionPane.showMessageDialog(parentFrame, uiMessageBundle.getString("ardublock.translator.exception.subroutineNotDeclared"), "Error", JOptionPane.ERROR_MESSAGE);
			
		}
		
		if (success)
		{
			AutoFormat formatter = new AutoFormat();
			String codeOut = code.toString();
			
			if (context.isNeedAutoFormat)
			{
				codeOut = formatter.format(codeOut);
			}
			
			System.out.println(codeOut);
			
			if (!context.isInArduino())
			{
				System.out.println(codeOut);
				  //String filePath = "E:\\IoTW2\\Sketchbook\\IoT-Werkstatt";
				  //String fileName = "IoT-Werkstatt.ino";
				  //String content = "This is the content of the file.";
				  
				  Context context = Context.getContext();
				  String codeFile = context.getArduinoCodeFileString();
			      ArduinoIDE2.writeFile(codeFile, codeOut);
			    
			}		
			context.didGenerate(codeOut);
		}
	}
}