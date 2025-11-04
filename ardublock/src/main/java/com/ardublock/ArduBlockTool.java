package com.ardublock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import processing.app.Editor;
import processing.app.EditorTab;
import processing.app.SketchFile;
import processing.app.tools.Tool;

import com.ardublock.core.Context;
import com.ardublock.ui.ArduBlockToolFrame;
import com.ardublock.ui.listener.OpenblocksFrameListener;
import com.formdev.flatlaf.FlatLightLaf;

public class ArduBlockTool implements Tool, OpenblocksFrameListener
{
	
	static Editor editor;
	static ArduBlockToolFrame openblocksFrame;
	
	public void init(Editor editor) {
		
			
		if (ArduBlockTool.editor == null )
		{
			//System.out.println("Start aus Arduino");
			Context context = Context.getContext();
			String arduinoVersion = this.getArduinoVersion();
			context.setInArduino(true);
			//System.out.println("Setze setInArduino in .ini");
			context.setArduinoVersionString(arduinoVersion);
			context.setEditor(editor);
			//System.out.println("Arduino Version: " + arduinoVersion);
			
			ArduBlockTool.editor = editor;
			ArduBlockTool.openblocksFrame = new ArduBlockToolFrame();
			ArduBlockTool.openblocksFrame.addListener(this);
		
		
			
		}
	}

	public void run() {
		try {
			ArduBlockTool.editor.toFront();
			ArduBlockTool.openblocksFrame.setVisible(true);
			ArduBlockTool.openblocksFrame.toFront();
		} catch (Exception e) {
			
		}
	}

	public String getMenuTitle() {
		return Context.APP_NAME;
	}

	public void didSave() {
		
	}
	
	public void didLoad() {
		
	}
	
	public void didSaveAs()
	{
		
	}
	
	public void didNew()
	{
		
	}
	
	/*
	public void didGenerate(String source) {
		java.lang.reflect.Method method;
		try {
			// pre Arduino 1.6.12
			Class ed = ArduBlockTool.editor.getClass();
			Class[] cArg = new Class[1];
			cArg[0] = String.class;
			method = ed.getMethod("setText", cArg);
			method.invoke(ArduBlockTool.editor, source);
		}
		catch (NoSuchMethodException e) {
			ArduBlockTool.editor.getCurrentTab().setText(source);
		} catch (IllegalAccessException e) {
			ArduBlockTool.editor.getCurrentTab().setText(source);
		} catch (SecurityException e) {
			ArduBlockTool.editor.getCurrentTab().setText(source);
		} catch (InvocationTargetException e) {
			ArduBlockTool.editor.getCurrentTab().setText(source);
		}
		ArduBlockTool.editor.handleExport(false);
	}
	
*/
	/*
	public void didGenerate(String source) {
	    final String desiredName = "IoTW_Sketch"; // ohne ".ino"

	    // --- Code in den Editor schreiben (wie gehabt) ---
	    try {
	        Class<?> ed = ArduBlockTool.editor.getClass();
	        java.lang.reflect.Method method = ed.getMethod("setText", String.class);
	        method.invoke(ArduBlockTool.editor, source);
	    } catch (NoSuchMethodException | IllegalAccessException | SecurityException | java.lang.reflect.InvocationTargetException e) {
	        ArduBlockTool.editor.getCurrentTab().setText(source);
	    }

	    // --- Aktuellen Sketch-Ordner auf IoTW_Sketch umstellen (Ordner ggf. überschreiben) ---
	    try {
	        processing.app.Sketch sketch = ArduBlockTool.editor.getSketch();
	        File currentFolder = sketch.getFolder();
	        File parent       = currentFolder.getParentFile();
	        File targetFolder = new File(parent, desiredName);

	        String curPath = currentFolder.getCanonicalPath();
	        String tgtPath = targetFolder.getCanonicalPath();

	        if (!curPath.equals(tgtPath)) {
	            // vorhandenen Zielordner (mit der einen .ino) hart löschen
	            if (targetFolder.exists()) {
	                try (var walk = Files.walk(targetFolder.toPath())) {
	                    walk.sorted(Comparator.reverseOrder()).forEach(p -> {
	                        try { Files.delete(p); } catch (IOException ex) { throw new RuntimeException(ex); }
	                    });
	                }
	            }
	            // und in exakt diesen Ordner speichern (setzt damit den aktuellen Sketch darauf)
	            sketch.saveAs(targetFolder);
	        }

	        // zur Sicherheit speichern
	        sketch.save();

	    } catch (Exception e) {
	        e.printStackTrace(); // wenn das Umstellen schiefgeht, exportieren wir den aktuellen Sketch
	    }

	    // --- Export ---
	    ArduBlockTool.editor.handleExport(false);
	}
*/

	// Context context = Context.getContext();
	//    boolean check = checkBoardPackage(context.ArdublockVersion);
	
	/*
	public void didGenerate(String source) {
	    final String desiredName = "IoTW_Sketch"; // no ".ino"

	    
	    
	    // Write source into the editor
	    try {
	        Class<?> ed = ArduBlockTool.editor.getClass();
	        java.lang.reflect.Method method = ed.getMethod("setText", String.class);
	        method.invoke(ArduBlockTool.editor, source);
	    } catch (NoSuchMethodException | IllegalAccessException | SecurityException | java.lang.reflect.InvocationTargetException e) {
	        ArduBlockTool.editor.getCurrentTab().setText(source);
	    }

	    try {
	        processing.app.Sketch sketch = ArduBlockTool.editor.getSketch();

	        File sketchFolder = sketch.getFolder();              
	        File sketchbookDir = sketchFolder.getParentFile();   
	        File portableDir   = sketchbookDir.getParentFile();  
	        File arduinoDir    = portableDir.getParentFile();    
	        File rootDir       = arduinoDir.getParentFile();     

	        File userSketchbookDir = new File(new File(rootDir, "user"), "Sketchbook");
	        if (!userSketchbookDir.exists() && !userSketchbookDir.mkdirs()) {
	            throw new IOException("Cannot create target base dir: " + userSketchbookDir);
	        }

	        File targetFolder = new File(userSketchbookDir, desiredName); 

	        String curPath = sketch.getFolder().getCanonicalPath();
	        String tgtPath = targetFolder.getCanonicalPath();

	        if (!curPath.equals(tgtPath)) {
	            // Hard delete target (you said it contains only one .ino)
	            if (targetFolder.exists()) {
	                try (var walk = Files.walk(targetFolder.toPath())) {
	                    walk.sorted(Comparator.reverseOrder()).forEach(p -> {
	                        try { Files.delete(p); } catch (IOException ex) { throw new RuntimeException(ex); }
	                    });
	                }
	            }
	            // Save into the exact target folder (this switches the current sketch)
	            sketch.saveAs(targetFolder);
	        }

	        // Ensure saved
	        sketch.save();

	    } catch (Exception e) {
	        e.printStackTrace(); // If switching fails, we export the current sketch
	    }

	    // Export
	    ArduBlockTool.editor.handleExport(false);
	}
	*/
	/*
	public void didGenerate(String source) {
	    java.lang.reflect.Method method;
	    try {
	        Class ed = ArduBlockTool.editor.getClass();
	        Class[] cArg = new Class[1];
	        cArg[0] = String.class;
	        method = ed.getMethod("setText", cArg);
	        method.invoke(ArduBlockTool.editor, source);
	    } catch (NoSuchMethodException e) {
	        ArduBlockTool.editor.getCurrentTab().setText(source);
	    } catch (IllegalAccessException e) {
	        ArduBlockTool.editor.getCurrentTab().setText(source);
	    } catch (SecurityException e) {
	        ArduBlockTool.editor.getCurrentTab().setText(source);
	    } catch (java.lang.reflect.InvocationTargetException e) {
	        ArduBlockTool.editor.getCurrentTab().setText(source);
	    }

	    // bring Arduino window to front
	    try {
	        Object base;
	        try {
	            base = ArduBlockTool.editor.getClass().getMethod("getBase").invoke(ArduBlockTool.editor);
	        } catch (NoSuchMethodException nsme) {
	            java.lang.reflect.Field f = ArduBlockTool.editor.getClass().getDeclaredField("base");
	            f.setAccessible(true);
	            base = f.get(ArduBlockTool.editor);
	        }
	        final javax.swing.JFrame frame = (javax.swing.JFrame) base;
	        javax.swing.SwingUtilities.invokeLater(() -> {
	            frame.setExtendedState(frame.getExtendedState() & ~java.awt.Frame.ICONIFIED);
	            frame.setAlwaysOnTop(true);
	            frame.toFront();
	            frame.requestFocus();
	            frame.setAlwaysOnTop(false);
	        });
	    } catch (Throwable ignore) {}
	    Context context = Context.getContext();
		if (checkBoardPackage(context.ArdublockVersion)) { // Board test
	        ArduBlockTool.editor.handleExport(false);
		}
	}
*/
	public void didGenerate(String source) {
	    java.lang.reflect.Method method;
	    try {
	        Class ed = ArduBlockTool.editor.getClass();
	        Class[] cArg = new Class[1];
	        cArg[0] = String.class;
	        method = ed.getMethod("setText", cArg);
	        method.invoke(ArduBlockTool.editor, source);
	    } catch (NoSuchMethodException e) {
	        ArduBlockTool.editor.getCurrentTab().setText(source);
	    } catch (IllegalAccessException e) {
	        ArduBlockTool.editor.getCurrentTab().setText(source);
	    } catch (SecurityException e) {
	        ArduBlockTool.editor.getCurrentTab().setText(source);
	    } catch (java.lang.reflect.InvocationTargetException e) {
	        ArduBlockTool.editor.getCurrentTab().setText(source);
	    }

	    try {
	        Object base;
	        try {
	            base = ArduBlockTool.editor.getClass().getMethod("getBase").invoke(ArduBlockTool.editor);
	        } catch (NoSuchMethodException nsme) {
	            java.lang.reflect.Field f = ArduBlockTool.editor.getClass().getDeclaredField("base");
	            f.setAccessible(true);
	            base = f.get(ArduBlockTool.editor);
	        }
	        final javax.swing.JFrame frame = (javax.swing.JFrame) base;
	        javax.swing.SwingUtilities.invokeLater(() -> {
	            frame.setExtendedState(frame.getExtendedState() & ~java.awt.Frame.ICONIFIED);
	            frame.setAlwaysOnTop(true);
	            frame.toFront();
	            frame.requestFocus();
	            frame.setAlwaysOnTop(false);
	        });
	    } catch (Throwable ignore) {}

	    Context context = Context.getContext();
	    if (checkBoardPackage(context.ArdublockVersion)) {
	        ArduBlockTool.editor.handleExport(false);
	    } else {
	        try {
	            try {
	                ArduBlockTool.editor.getClass()
	                    .getMethod("handleSave", boolean.class)
	                    .invoke(ArduBlockTool.editor, false);
	            } catch (NoSuchMethodException e) {
	                ArduBlockTool.editor.getSketch().save();
	            }
	        } catch (Throwable t) {
	            try {
					ArduBlockTool.editor.getSketch().save();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }
	}

	
	
	/*
	
	public static boolean checkBoardPackage(String boardTyp) {
	    String pkg = processing.app.PreferencesData.get("target_package");
	    String cur = (pkg == null ? "" : pkg.trim().toLowerCase());

	    String bt = (boardTyp == null ? "" : boardTyp.trim());
	    String expected = null;
	    if ("Makey".equalsIgnoreCase(bt)) {
	        expected = "esp32";
	    } else if ("Octopus".equalsIgnoreCase(bt)) {
	        expected = "esp8266";
	    }

	    if (!expected.equalsIgnoreCase(cur)) {
	        javax.swing.JOptionPane.showMessageDialog(
	        	ArduBlockTool.editor,
	            "Bitte in der IDE das Board einstellen.\nErwartet: " + expected + " ("+ boardTyp + ")" + "\nAktuell: " + (cur.isEmpty() ? "(unbekannt)" : cur),
	            "Hinweis",
	            javax.swing.JOptionPane.WARNING_MESSAGE
	        );
	        return false;
	    }

	    return true; // 
	}

	*/
	public static boolean checkBoardPackage(String boardTyp) {
	    String pkg = processing.app.PreferencesData.get("target_package");
	    String cur = (pkg == null ? "" : pkg.trim().toLowerCase());

	    String bt = (boardTyp == null ? "" : boardTyp.trim());
	    String expected = null;
	    if ("Makey".equalsIgnoreCase(bt)) {
	        expected = "esp32";
	    } else if ("Octopus".equalsIgnoreCase(bt)) {
	        expected = "esp8266";
	    }

	    // Unbekannter Boardname: immer true
	    if (expected == null) return true;

	    if (!expected.equalsIgnoreCase(cur)) {
	        javax.swing.JOptionPane.showMessageDialog(
	            ArduBlockTool.editor,
	            "Bitte unter Werkzeuge das Board einstellen.\nErwartet: " + expected + " (" + bt + ")\nAktuell: " + (cur.isEmpty() ? "(unbekannt)" : cur),
	            "Hinweis",
	            javax.swing.JOptionPane.WARNING_MESSAGE
	        );
	        return false;
	    }
	    return true;
	}

	
	
	private String getArduinoVersion()
	{
		Context context = Context.getContext();
		File versionFile = context.getArduinoFile("lib/version.txt");
		//System.out.println("Pfad: " + versionFile.getAbsolutePath());
		if (versionFile.exists())
		{
			try
			{
				InputStream is = new FileInputStream(versionFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String line = reader.readLine();
				reader.close();
				if (line == null)
				{
					return Context.ARDUINO_VERSION_UNKNOWN;
				}
				line = line.trim();
				if (line.length() == 0)
				{
					return Context.ARDUINO_VERSION_UNKNOWN;
				}
				return line;
				
			}
			catch (FileNotFoundException e)
			{
				return Context.ARDUINO_VERSION_UNKNOWN;
			}
			catch (UnsupportedEncodingException e)
			{
				return Context.ARDUINO_VERSION_UNKNOWN;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return Context.ARDUINO_VERSION_UNKNOWN;
			}
		}
		else
		{
			return Context.ARDUINO_VERSION_UNKNOWN;
		}
		
	}
	
}
	
	
