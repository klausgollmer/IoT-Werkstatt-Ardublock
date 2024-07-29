package com.ardublock.translator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import javax.swing.JOptionPane;

import com.ardublock.translator.adaptor.BlockAdaptor;
import com.ardublock.translator.adaptor.OpenBlocksAdaptor;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.TranslatorBlockFactory;
import com.ardublock.translator.block.exception.BlockException;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNameDuplicatedException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Workspace;


public class Translator
{
	//private static final String variablePrefix = "_ABVAR_";
	
	private Set<String> headerFileSet;
	private Set<String> definitionSet;
	private List<String> setupCommand;
	private Set<String> functionNameSet;
	private Set<TranslatorBlock> bodyTranslatreFinishCallbackSet;
	private BlockAdaptor blockAdaptor;
	
	private Set<String> inputPinSet;
	private Set<String> outputPinSet;
	
	private Map<String, String> numberVariableSet;
	private Map<String, String> numberLocalVariableSet;
	private Map<String, String> booleanVariableSet;
	private Map<String, String> stringVariableSet;
	private Map<String, Object> internalData;
	
	private Workspace workspace;
	
	private String rootBlockName;
	
	private int variableCnt;
	private boolean isScoopProgram;
	private boolean isMQTTProgram;
	private boolean isWiFiProgram;
	private boolean isRTCVarProgram;
	private boolean isRTCNTPProgram;
	private boolean isBlynkProgram;
	private boolean isSCD30Program;
	private boolean isBME680Program;
	private boolean isHTTPServerProgram;
	private boolean isDIYSpectroProgram;
	private boolean isGPRSProgram;
	private boolean isAPProgram;
	private boolean ismDNSProgram;
	private boolean isLORAProgram;
	private boolean isDeepSleepProgram;
	private boolean isIRQProgram;
	private boolean isNTPServerProgram;
	

	public Translator(Workspace ws)
	{
		workspace = ws;
		reset();
	}
	
	public String genreateHeaderCommand()
	{
		
		String Disclaimer = "/* Disclaimer IoT-Werkstatt CC 4.0 BY NC SA \n"+
				"       This program is distributed in the hope that it will be useful,\n" + 
				"       but WITHOUT ANY WARRANTY; without even the implied warranty of\n" + 
				"       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. For Ardublock see the\n" + 
				"       GNU General Public License for more details. */\n\n";
		
		StringBuilder headerCommand = new StringBuilder();
		headerCommand.append(Disclaimer);
		
		if (!isWiFiProgram()) {
		//  addHeaderFile("ESP8266WiFi.h");
			addDefinitionCommand ("#if defined(ESP8266)\n  #include <ESP8266WiFi.h> \n #endif\r\n");
		}
		addHeaderFile("IoTBoards_Generic.h");
		if (!headerFileSet.isEmpty())
		{
			for (String file:headerFileSet)
			{
				if (file.contains("#")==false) 
				  headerCommand.append("#include <" + file + ">\n");
				else
				  headerCommand.append(file+"\n");
								
			}
			headerCommand.append("\n");
		}
		
		if (!definitionSet.isEmpty())
		{
			for (String command:definitionSet)
			{
				headerCommand.append(command + "\n");
			}
			headerCommand.append("\n");
		}
		
		if (!functionNameSet.isEmpty())
		{
			for (String functionName:functionNameSet)
			{
				if(this.getRootBlockName().equals("sub_return")){				//Check if subroutine with return
			//#kgo EcoMaker		headerCommand.append("int " + functionName + "();\n");
				}
				else{
// #kgo predef cocktail					headerCommand.append("void " + functionName + "();\n");
				}
			}
			headerCommand.append("\n");
		}
		
		return headerCommand.toString() + generateSetupFunction();
	}
	
	public String generateSetupFunction()
	{
		StringBuilder setupFunction = new StringBuilder();
//		setupFunction.append("void setup()\n{\n");
		setupFunction.append("void setup(){ // Einmalige Initialisierung\n");
		
 	    if (false) { //isRTCVarProgram()) {
 	    	
            String util = "// Lese gespeicherte Daten aus RTC-Memory\n"
         		     + " ESP.rtcUserMemoryRead(0, (uint32_t*) &RTCData, sizeof(RTCData)); \n";

//            if (isRTCNTPProgram()) {
            if (false) { // wäre nötig für Zeitnachführung im deepsleep RTC-Clock, beisst sich mit LORA-MAC
            	
     	    	util +=   " if (RTCLoadCheck()) {            // Gültiger Eintrag, setze Uhrzeit\r\n" + 
                 	      "  struct timeval now;\r\n" + 
     	    	          "  now.tv_sec=RTCData.time_t;\r\n" + 
     	    	          "  now.tv_usec=micros(); \r\n" + 
     	    	          "  settimeofday(&now, NULL);"+
	    				  " }";
            }
            
            
           
           
        /* 
 	    	String util = " if (!RTCVar.loadFromRTC()) {            // we load the values from rtc memory back into the registered variables\r\n" + 
 	    				  "    Serial.println(\"This seems to be a cold boot. We don't have a valid state on RTC memory\");\r\n" + 
 	    			      " }";
        */
 			setupFunction.append(util);
 	    }
 		
 		if (!isWiFiProgram()) {
		   setupFunction.append("#if defined(ESP8266) \n WiFi.forceSleepBegin(); \n #endif \n");
		}
		
		if (!inputPinSet.isEmpty())
		{
			for (String pinNumber:inputPinSet)
			{
				setupFunction.append("pinMode( " + pinNumber + " , INPUT);\n");
			}
		}
		if (!outputPinSet.isEmpty())
		{
			for (String pinNumber:outputPinSet)
			{
				setupFunction.append("pinMode( " + pinNumber + " , OUTPUT);\n");
			}
		}
		
		if (!setupCommand.isEmpty())
		{
			for (String command:setupCommand)
			{
				setupFunction.append(command + "\n");
			}
		}
		
		 if (isSCD30Program()) {
			 String esp8266 = "#if defined(ESP8266) \n Wire.setClock(100000L); Wire.setClockStretchLimit(200000L);\n #endif\n";
			 setupFunction.append(esp8266);
		 }
		
		 
		
		
		setupFunction.append("}\n\n");
		
		return setupFunction.toString();
	}
	
	public String translate(Long blockId) throws SocketNullException, SubroutineNotDeclaredException, BlockException
	{
		TranslatorBlockFactory translatorBlockFactory = new TranslatorBlockFactory();
		Block block = workspace.getEnv().getBlock(blockId);
		TranslatorBlock rootTranslatorBlock = translatorBlockFactory.buildTranslatorBlock(this, blockId, block.getGenusName(), "", "", block.getBlockLabel());
		return rootTranslatorBlock.toCode();
	}
	
	public BlockAdaptor getBlockAdaptor()
	{
		return blockAdaptor;
	}
	
	public void reset()
	{
		headerFileSet = new LinkedHashSet<String>();
		definitionSet = new LinkedHashSet<String>();
		setupCommand = new LinkedList<String>();
		functionNameSet = new HashSet<String>();
		inputPinSet = new HashSet<String>();
		outputPinSet = new HashSet<String>();
		bodyTranslatreFinishCallbackSet = new HashSet<TranslatorBlock>();
		
		numberVariableSet = new HashMap<String, String>();
		numberLocalVariableSet = new HashMap<String, String>();
		booleanVariableSet = new HashMap<String, String>();
		stringVariableSet = new HashMap<String, String>();
		
		internalData =  new HashMap<String, Object>();
		blockAdaptor = buildOpenBlocksAdaptor();
		
		variableCnt = 0;
		
		rootBlockName = null;
		isScoopProgram = false;
		isMQTTProgram = false;
		isWiFiProgram = false;
		isRTCVarProgram = false;
		isRTCNTPProgram = false;
		isBlynkProgram = false;
		isSCD30Program=false;
		isBME680Program=false;
		
		isHTTPServerProgram=false;
		isDIYSpectroProgram=false;
		isGPRSProgram=false;
		isIRQProgram=false;

		//System.out.printf("\nkgo reset %b", this.ismDNSProgram);

		isAPProgram=false;
		
		ismDNSProgram = false;
		isDeepSleepProgram = false;
		isLORAProgram = false;
		isNTPServerProgram = false;
	}
	
	private BlockAdaptor buildOpenBlocksAdaptor()
	{
		return new OpenBlocksAdaptor();
	}
	
	public void addHeaderFile(String headerFile)
	{
		if (!headerFileSet.contains(headerFile))
		{
			headerFileSet.add(headerFile);
		}
	}
	
	public void addSetupCommand(String command)
	{
		if (!setupCommand.contains(command))
		{
			setupCommand.add(command);
		}
	}
	
	public void addSetupCommandForced(String command)
	{
		setupCommand.add(command);
	}
	
	public void addDefinitionCommand(String command)
	{
		definitionSet.add(command);
	}
	
	public void addInputPin(String pinNumber)
	{
		inputPinSet.add(pinNumber);
	}
	
	public void addOutputPin(String pinNumber)
	{
		outputPinSet.add(pinNumber);
	}
	
	public String getNumberVariable(String userVarName)
	{
		return numberVariableSet.get(userVarName);
	}

	public String getLocalNumberVariable(String userVarName)
	{
		return numberLocalVariableSet.get(userVarName);
	}

	public void clearLocalNumberVariable()
	{
		numberLocalVariableSet = new HashMap<String, String>();
		return ;
	}
	
	
	public String getBooleanVariable(String userVarName)
	{
		return booleanVariableSet.get(userVarName);
	}
	
	public String getStringVariable(String userVarName)
	{
		return stringVariableSet.get(userVarName);
	}
	
	public void addNumberVariable(String userVarName, String internalName)
	{
		numberVariableSet.put(userVarName, internalName);
	}

	public void addLocalNumberVariable(String userVarName, String internalName)
	{
		numberLocalVariableSet.put(userVarName, internalName);
	}

	
	public void addBooleanVariable(String userVarName, String internalName)
	{
		booleanVariableSet.put(userVarName, internalName);
	}
	
	public void addStringVariable(String userVarName, String internalName)
	{
		stringVariableSet.put(userVarName, internalName);
	}
	
	public void addFunctionName(Long blockId, String functionName) throws SubroutineNameDuplicatedException
	{
		if (functionName.equals("loop") ||functionName.equals("setup") || functionNameSet.contains(functionName))
		{
			throw new SubroutineNameDuplicatedException(blockId);
		}
		
		functionNameSet.add(functionName);
	}
	
	public boolean containFunctionName(String name)
	{
		return functionNameSet.contains(name.trim());
	}
	
	
	public String buildVariableName()
	{
		return buildVariableName("");
	}
	
	public String buildVariableName(String reference)
	{
		variableCnt = variableCnt + 1;
		String varName = "";
		//if (reference.length()==0) 
		//  varName = "autoIndex" + variableCnt;
		int i;
		for (i=0; i<reference.length(); ++i)
		{
			char c = reference.charAt(i);
			if (Character.isLetter(c) || Character.isDigit(c) || (c == '_'))
			{
				//varName = varName + c;
				varName += c;
			}
		}
		return varName;
	}
	
	public Workspace getWorkspace() {
		return workspace;
	}
	
	public Block getBlock(Long blockId) {
		return workspace.getEnv().getBlock(blockId);
	}
	
	public void registerBodyTranslateFinishCallback(TranslatorBlock translatorBlock)
	{
		bodyTranslatreFinishCallbackSet.add(translatorBlock);
	}

	public void beforeGenerateHeader()  throws SocketNullException, SubroutineNotDeclaredException
	{
		for (TranslatorBlock translatorBlock : bodyTranslatreFinishCallbackSet)
		{
			translatorBlock.onTranslateBodyFinished();
		}
	}

	public String getRootBlockName() {
		return rootBlockName;
	}

	public void setRootBlockName(String rootBlockName) {
		this.rootBlockName = rootBlockName;
	}

	public boolean isScoopProgram() {
		return isScoopProgram;
	}

	public void setScoopProgram(boolean isScoopProgram) {
		this.isScoopProgram = isScoopProgram;
	}

	public boolean isMQTTProgram() {
		return isMQTTProgram;
	}
	public boolean isWiFiProgram() {
		return isWiFiProgram;
	}

	public boolean isRTCVarProgram() {
		return isRTCVarProgram;
	}

	public boolean isRTCNTPProgram() {
		return isRTCNTPProgram;
	}

	
	public boolean isBlynkProgram() {
		return isBlynkProgram;
	}

	public void setBlynkProgram(boolean isBlynkProgram) {
		this.isBlynkProgram = isBlynkProgram;
	}

	public void setMQTTProgram(boolean isMQTTProgram) {
		this.isMQTTProgram = isMQTTProgram;
	}

	public void setWiFiProgram(boolean isWiFiProgram) {
		this.isWiFiProgram = isWiFiProgram;
	}
	
	
	public void setRTCVarProgram(boolean isRTCVarProgram) {
		this.isRTCVarProgram = isRTCVarProgram;
	}

	public void setRTCNTPProgram(boolean isRTCNTPProgram) {
		this.isRTCNTPProgram = isRTCNTPProgram;
	}
	
	public boolean isHTTPServerProgram() {
		return isHTTPServerProgram;
	}
	
	public boolean isBME680Program() {
		return isBME680Program;
	}
	public boolean isSCD30Program() {
		return isSCD30Program;
	}
	
	public boolean isDIYSpectroProgram() {
		return isDIYSpectroProgram;
	}

	public void setHTTPServerProgram(boolean isHTTPServerProgram) {
		this.isHTTPServerProgram = isHTTPServerProgram;
	}
	
	public void setNTPServerProgram(boolean isNTPServerProgram) {
		this.isNTPServerProgram = isNTPServerProgram;
	}
	
	public boolean isNTPServerProgram() {
		return isNTPServerProgram;
	}
	
	public void setSCD30Program(boolean isSCD30Program) {
		this.isSCD30Program = isSCD30Program;
	}
	
	public void setBME680Program(boolean isBME680Program) {
		this.isBME680Program = isBME680Program;
	}

	public void setDIYSpectroProgram(boolean isDIYSpectroProgram) {
		this.isDIYSpectroProgram = isDIYSpectroProgram;
	}

	public boolean isGPRSProgram() {
		return isGPRSProgram;
	}

	public boolean isIRQProgram() {
		return isIRQProgram;
	}

	public void setIRQProgram(boolean isIRQProgram) {
		this.isIRQProgram = isIRQProgram;
	}
	
	
	public void setGPRSProgram(boolean isGPRSProgram) {
		this.isGPRSProgram = isGPRSProgram;
	}
	
	public boolean isAPProgram() {
		return isAPProgram;
	}

	public boolean ismDNSProgram() {
	//	System.out.printf("\nkgo abfrage %b", this.ismDNSProgram);

    	return ismDNSProgram;
	}

	public boolean isLORAProgram() {
		return isLORAProgram;
	}
	
	public void setLORAProgram(boolean isLORAProgram) {
		this.isLORAProgram = isLORAProgram;
	}

	public boolean isDeepSleepProgram() {
		//System.out.printf("\nkgo test %b", isDeepSleepProgram);
		return isDeepSleepProgram;
	}
	
	public void setDeepSleepProgram(boolean isDeepSleepProgram) {
		this.isDeepSleepProgram = isDeepSleepProgram;
		//System.out.printf("\nkgo set %b", isDeepSleepProgram);
	}

	
	public void setAPProgram(boolean isAPProgram) {
		this.isAPProgram = isAPProgram;
	}
	
	public void setmDNSProgram(boolean ismDNSProgram) {
//		System.out.printf("\nkgo set %b", ismDNSProgram);
		this.ismDNSProgram = ismDNSProgram;
	}
	
	
	public Set<RenderableBlock> findEntryBlocks()
	{
		Set<RenderableBlock> loopBlockSet = new HashSet<RenderableBlock>();
		Iterable<RenderableBlock> renderableBlocks = workspace.getRenderableBlocks();
		
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
			}
		}
		
		return loopBlockSet;
	}
	
	public Set<RenderableBlock> findSubroutineBlocks() throws SubroutineNameDuplicatedException
	{
		Set<RenderableBlock> subroutineBlockSet = new HashSet<RenderableBlock>();
		Iterable<RenderableBlock> renderableBlocks = workspace.getRenderableBlocks();
		
		for (RenderableBlock renderableBlock:renderableBlocks)
		{
			Block block = renderableBlock.getBlock();
			
			if (!block.hasPlug() && (Block.NULL.equals(block.getBeforeBlockID())))
			{
				if (block.getGenusName().equals("subroutine"))
				{
					String functionName = block.getBlockLabel().trim()+"_gen";
				System.out.println(block.getBlockLabel().trim());
					this.addFunctionName(block.getBlockID(), functionName);
					subroutineBlockSet.add(renderableBlock);
				}			
				
				if (block.getGenusName().equals("TTN_RxCallback"))
				{
					String functionName = block.getBlockLabel().trim()+"_gen";
					this.addFunctionName(block.getBlockID(), functionName);
					subroutineBlockSet.add(renderableBlock);
				}			
				
			}
		}
		
		return subroutineBlockSet;
	}
	
	public String translate(Set<RenderableBlock> loopBlocks, Set<RenderableBlock> subroutineBlocks) throws SocketNullException, SubroutineNotDeclaredException
	{
		StringBuilder code = new StringBuilder();
		
		for (RenderableBlock renderableBlock : loopBlocks)
		{
			Block loopBlock = renderableBlock.getBlock();
			code.append(translate(loopBlock.getBlockID()));
		}
		
		for (RenderableBlock renderableBlock : subroutineBlocks)
		{
			Block subroutineBlock = renderableBlock.getBlock();
			code.append(translate(subroutineBlock.getBlockID()));
		}
		beforeGenerateHeader();
		code.insert(0, genreateHeaderCommand());
		
		return code.toString();
	}
	
	public Object getInternalData(String name)
	{
		return internalData.get(name);
	}
	
	public void addInternalData(String name, Object value)
	{
		internalData.put(name, value);
	}
}
