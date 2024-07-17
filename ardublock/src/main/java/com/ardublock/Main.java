package com.ardublock;

import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.IOException;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.ardublock.core.Context;
import com.ardublock.ui.ConsoleFrame;
import com.ardublock.ui.OpenblocksFrame;

public class Main
{	
	private OpenblocksFrame openblocksFrame;

	public static void main(String args[]) throws SAXException, IOException, ParserConfigurationException
	{

	
	  // Set system properties for high DPI scaling
	  System.setProperty("sun.java2d.uiScale", "1.5");
	  System.setProperty("swing.aatext", "true");
	  System.setProperty("awt.useSystemAAFontSettings", "lcd");
  	  
	  FlatLightLaf.setup();
	
	     UIManager.put("Button.arc", 10);
	        UIManager.put("Button.margin", new Insets(1, 3, 3, 3));
	        UIManager.put("Component.focusWidth", 1);
	        UIManager.put("Component.innerFocusWidth", 1);

	        // Set larger font size
	        UIManager.put("defaultFont", new Font("Arial", Font.PLAIN, 10));
		
	        UIManager.put("Button.font", new Font("Arial", Font.PLAIN, 17));
	        UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 18));
	        UIManager.put("ComboBox.font", new Font("Arial", Font.PLAIN, 16));
	        
	        
	        
	        
	        
	 Context context = Context.getContext();
	 context.setArduinoCodeFileString("");
	    
		
	   if (args.length > 0) {
	     String parameter = args[0];
	     System.out.println("Der übergebene Parameter ist: " + parameter);
	    
	     //Context context = Context.getContext();
	     context.setArduinoCodeFileString(parameter);
	     context.setInArduino(false);
	     
	   } else {
	     System.out.println("Kein Parameter übergeben.");
	  //   context.setArduinoCodeFileString("E:\\IoTW_IDE2\\Sketchbook\\IoT-Werkstatt\\IoT-Werkstatt.ino");
	     context.setArduinoCodeFileString("E:\\IoTW_IDE2\\Sketchbook\\IoT-Werkstatt\\IoT-Werkstatt.ino");
	     context.setInArduino(true);
	   }
		
		
		Main me = new Main();
		me.startArdublock();
	}
	
	public void startArdublock() throws SAXException, IOException, ParserConfigurationException
	{
		startOpenblocksFrame();
		//startConsoleFrame();
	}
	
	private void startOpenblocksFrame() throws SAXException, IOException, ParserConfigurationException
	{
		openblocksFrame = new OpenblocksFrame();
		//openblocksFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Context context = Context.getContext();
		//context.setInArduino(false);
		openblocksFrame.setVisible(true);
	}

	public void shutdown()
	{
		openblocksFrame.dispatchEvent(new WindowEvent(openblocksFrame, WindowEvent.WINDOW_CLOSING));
	}
	
	@SuppressWarnings("unused")
	private void startConsoleFrame()
	{
		ConsoleFrame consoleFrame = new ConsoleFrame();
		consoleFrame.setVisible(true);
	}
}
