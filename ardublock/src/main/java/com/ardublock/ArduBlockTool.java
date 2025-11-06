package com.ardublock;

import java.awt.AWTEvent;

import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;


import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


import java.lang.reflect.Method;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Comparator;


import java.util.stream.Stream;

import javax.swing.JOptionPane;

import javax.swing.WindowConstants;

import processing.app.Editor;

import processing.app.Sketch;

import processing.app.tools.Tool;

import com.ardublock.core.Context;
import com.ardublock.ui.ArduBlockToolFrame;
import com.ardublock.ui.listener.OpenblocksFrameListener;


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
			//System.out.println("Setze setInArduino in .ini"+arduinoVersion);
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


	
	public static final class IoTWGlobalCrossGuard {
	    private static volatile boolean installed = false;
	    private static Path targetIno;

	    // NEU: Referenz oder Titel-Matcher für das zweite Fenster
	    private static java.lang.ref.WeakReference<Window> companionRef;
	    private static volatile String companionTitleSubstr;

	    private static final ThreadLocal<Boolean> reenter = ThreadLocal.withInitial(() -> Boolean.FALSE);

	    public static synchronized void setCompanionWindow(Window w) {
	        companionRef = (w != null) ? new java.lang.ref.WeakReference<>(w) : null;
	    }
	    public static synchronized void setCompanionTitleContains(String titlePart) {
	        companionTitleSubstr = titlePart;
	    }

	    public static synchronized void install(Path target) {
	        if (installed || target == null) return;
	        targetIno = target.toAbsolutePath().normalize();
	      	//System.out.println("install Guard");
	        IoTWGlobalCrossGuard.setCompanionTitleContains("IoT-Werkstatt Makey:Lab");
	        
	        EventQueue proxy = new EventQueue() {
	            @Override protected void dispatchEvent(AWTEvent ev) {
	                if (Boolean.TRUE.equals(reenter.get())) { super.dispatchEvent(ev); return; }

	                try {
	                    if (ev instanceof WindowEvent) {
	                        WindowEvent we = (WindowEvent) ev;
	                        int id = we.getID();
	                        Window w = we.getWindow();

	                        if (id == WindowEvent.WINDOW_CLOSING && w instanceof processing.app.Editor) {
	                            processing.app.Editor win = (processing.app.Editor) w;

	                            if (!editorIsOn(win, targetIno)) { super.dispatchEvent(ev); return; }

	                            try { win.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); } catch (Throwable ignore) {}
	                            try {
	                                win.setAutoRequestFocus(true);
	                                win.setVisible(true);
	                                win.setState(Frame.NORMAL);
	                                win.toFront();
	                                win.requestFocus();
	                            } catch (Throwable ignore) {}

	                            int choice = JOptionPane.showConfirmDialog(
	                                    win.isDisplayable() ? win : null,
	                                    "Werkstatt noch aktiv – wirklich schließen?",
	                                    "IoTW_Sketch schließen",
	                                    JOptionPane.YES_NO_OPTION,
	                                    JOptionPane.WARNING_MESSAGE
	                            );
	                         // ... im WINDOW_CLOSING-Block, nachdem choice ermittelt wurde:
	                         // ... choice wurde ermittelt
	                            if (choice == JOptionPane.YES_OPTION) {
	                                // 1) Companion schließen lassen
	                                Window companion = findCompanionWindow();
	                                if (companion != null && companion != win && companion.isDisplayable()) {
	                                    reenter.set(Boolean.TRUE);
	                                    try {
	                                        companion.dispatchEvent(new WindowEvent(companion, WindowEvent.WINDOW_CLOSING));
	                                    } finally {
	                                        reenter.set(Boolean.FALSE);
	                                    }
	                                    // Companion hat „abgebrochen“ → Editor NICHT schließen
	                                    if (companion.isDisplayable()) {
	                                        try { win.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); } catch (Throwable ignore) {}
	                                        return; // <<< WICHTIG: nicht weiterleiten!
	                                    } else {
	                                        // Companion ist zu → Editor regulär schließen (Original-Event durchlassen)
	                                        try { win.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); } catch (Throwable ignore) {}
	                                        reenter.set(Boolean.TRUE);
	                                        try { super.dispatchEvent(ev); } finally { reenter.set(Boolean.FALSE); }
	                                        return; // sicherheitshalber
	                                    }
	                                } else {
	                                    // Kein Companion → Editor regulär schließen
	                                    try { win.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); } catch (Throwable ignore) {}
	                                    reenter.set(Boolean.TRUE);
	                                    try { super.dispatchEvent(ev); } finally { reenter.set(Boolean.FALSE); }
	                                    return;
	                                }
	                            } else {
	                                // NO_OPTION → gar nicht schließen
	                                try { win.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); } catch (Throwable ignore) {}
	                                return; // <<< WICHTIG: Event NICHT weiterreichen
	                            }

	                        }
	                    }
	                } catch (Throwable t) {
	                    // Bei Fehlern nicht blockieren
	                }

	                super.dispatchEvent(ev);
	            }
	        };

	        Toolkit.getDefaultToolkit().getSystemEventQueue().push(proxy);
	        installed = true;
	    }

	    public static synchronized void uninstall() {
	    	//System.out.println("uninstall Guard");
	        targetIno = null;
	        companionRef = null;
	        companionTitleSubstr = null;
	        installed = false;
	    }

	    // --- NEU: Companion-Finder ---
	    private static Window findCompanionWindow() {
	        // 1) direkte Referenz
	        if (companionRef != null) {
	            Window w = companionRef.get();
	            if (w != null && w.isDisplayable()) return w;
	        }
	        // 2) Suche per Titel-Substring
	        String sub = companionTitleSubstr;
	        if (sub != null && !sub.isEmpty()) {
	            for (Window w : Window.getWindows()) {
	                if (!w.isDisplayable()) continue;
	                if (w instanceof Frame) {
	                    String t = ((Frame) w).getTitle();
	                    if (t != null && t.contains(sub)) return w;
	                } else if (w instanceof Dialog) {
	                    String t = ((Dialog) w).getTitle();
	                    if (t != null && t.contains(sub)) return w;
	                }
	            }
	        }
	        return null;
	    }

	    private static boolean editorIsOn(processing.app.Editor ed, Path target) {
	        try {
	            Object sketch = ed.getSketch();
	            if (sketch == null) return false;
	            String main = (String) sketch.getClass().getMethod("getMainFilePath").invoke(sketch);
	            if (main == null) return false;
	            String curr = java.nio.file.Paths.get(main).toAbsolutePath().normalize().toString();
	            String targ = target.toAbsolutePath().normalize().toString();
	            return curr.equalsIgnoreCase(targ);
	        } catch (Throwable e) {
	            return false;
	        }
	    }
	}


	
	
	
		

	    


	    /* -------- Helpers -------- */

	private static boolean isEditorWindowAlive(Editor ed) {
	    try {
	        // Editor ist i.d.R. ein JFrame; diese Methoden kommen von java.awt.Window
	        Method isDisplayable = ed.getClass().getMethod("isDisplayable");
	        Method isShowing     = ed.getClass().getMethod("isShowing");
	        Boolean d = (Boolean) isDisplayable.invoke(ed);
	        Boolean s = (Boolean) isShowing.invoke(ed);
	        return Boolean.TRUE.equals(d) && Boolean.TRUE.equals(s);
	    } catch (Throwable t) {
	        // Falls Build kein JFrame ist, nehmen wir konservativ an: ok
	        return true;
	    }
	}

	
	private static boolean editorIsOn(Editor ed, Path targetIno) {
	    try {
	        Object sketch = ed.getSketch();
	        if (sketch == null) return false;
	        String main = (String) sketch.getClass().getMethod("getMainFilePath").invoke(sketch);
	        if (main == null) return false;
	        String curr = Paths.get(main).toAbsolutePath().normalize().toString();
	        String targ = targetIno.toAbsolutePath().normalize().toString();
	        return curr.equalsIgnoreCase(targ);
	    } catch (Throwable e) {
	        return false;
	    }
	}

	                
	public static boolean switchToTargetAndWrite(Path desiredPath, String source) {
	    boolean ok = true;

	    Editor ed = ArduBlockTool.editor;
	    ArduBlockTool.IoTWGlobalCrossGuard.install(desiredPath);
   	    //System.out.println("ison="+editorIsOn(ed, desiredPath));
        //System.out.println("alive="+isEditorWindowAlive(ed));
   	    if (!isEditorWindowAlive(ed)) {
   	   	    //System.out.println("not alive, alles aus");
        } 	    
	    
	    if (ed == null) { System.err.println("editor == null"); ok = false; }

	    Sketch sk = (ok ? ed.getSketch() : null);
	    if (sk == null) { System.err.println("sketch == null"); ok = false; }

	    // Zielordner aus .ino oder Ordnerpfad ermitteln
	    File targetFolder = null;
	    if (ok) {
	        Path targetFolderPath = desiredPath;
	        String name = desiredPath.getFileName() != null ? desiredPath.getFileName().toString() : "";
	        if (name.endsWith(".ino")) {
	            Path p = desiredPath.getParent();
	            if (p == null) { System.err.println("parent of .ino is null"); ok = false; }
	            else targetFolderPath = p;
	        }
	        if (ok) targetFolder = targetFolderPath.toFile();
	    }

	    // Umschalten, falls nicht schon am Ziel
	    if (ok) {
	        try { 
	        	sk.save(); // altes File, was übernommen wird
	        	//System.out.println("save="+sk.getFolder());
                } catch (Throwable t) { System.err.println("save() failed: " + t); ok = false; }
   	    
	        if (ok) {
	            String cur = safeCanonical(sk.getFolder());
	            String tgt = safeCanonical(targetFolder);
                //System.out.println("tgt ="+ tgt);
                //System.out.println("cur ="+ cur);

	            boolean same = (cur != null && tgt != null && cur.equalsIgnoreCase(tgt));

	            if (!same) {
	                // *** WICHTIG: Zielordner muss für saveAs NICHT existieren ***
	                if (targetFolder.exists()) {
	                    try (Stream<Path> walk = Files.walk(targetFolder.toPath())) {
	                        walk.sorted(Comparator.reverseOrder()).forEach(p -> {
	                            try { Files.delete(p); } catch (Exception ex) { throw new RuntimeException(ex); }
	                        });
	                    } catch (RuntimeException re) {
	                        System.err.println("delete target folder failed: " + re.getCause());
	                        ok = false;
	                    } catch (Exception e) {
	                        System.err.println("delete target folder failed: " + e);
	                        ok = false;
	                    }
	                }
	                if (ok) {
	                    try {
	            	        //System.out.println("save="+targetFolder);
	                        sk.saveAs(targetFolder);   // wechselt IDE auf Ziel
	                        
	                        sk = ed.getSketch();       // referenz aktualisieren
	                    } catch (Throwable t) {
	                        System.err.println("saveAs() failed: " + t);
	                        ok = false;
	                    }
	                }
	            }
	        }
	    }

	 // ... nach save/saveAs und nachdem du cur/tgt geprüft/umgeschaltet hast:
	    sk = ed.getSketch(); // Referenz aktualisieren, falls nötig

	    // Fokus (best effort)
	    try { if (ed != null) ed.toFront(); } catch (Throwable ignore) {}
	    try { if (ed != null) ed.requestFocus(); } catch (Throwable ignore) {}

	    // Am Ende: Source in den (jetzt) Ziel-Tab schreiben
	    if (ok && ed != null) {
	    	// in den gerade aktiven Tab schreiben
	    	ArduBlockTool.editor.getCurrentTab().setText(source);
	    	// Silent save: direkt über Sketch.save() (kein Dialog)
	    	try {
	    	    ArduBlockTool.editor.getSketch().save();
	    	} catch (Throwable ignore) {
	    	    // falls es aus irgendeinem Grund fehlschlägt, NICHT auf handleSave(true/false) wechseln,
	    	    // das könnte wieder UI öffnen. Dann lieber vorerst ohne speichern weiterlaufen.
	    	}
	    }
	    Context context = Context.getContext();
	    if (checkBoardPackage(context.ArdublockVersion)) { // Board test
	        ArduBlockTool.editor.handleExport(false);
		}
	    
	    return ok;
	}

	private static String safeCanonical(File f) {
	    try { return (f != null) ? f.getCanonicalPath() : null; }
	    catch (Exception e) { return (f != null) ? f.getAbsolutePath() : null; }
	}

	
	
		public void didGenerate(String source) {
		    Path cwd  = Paths.get("").toAbsolutePath().normalize();
		    Path root = cwd.getParent();
		    
		 	//System.out.println("generate cwd"+cwd);
		 	//System.out.println("generate root"+root);

		    // Ziel: /user/Sketchbook/IoTW_Sketch/IoTW_Sketch.ino
		    Path targetFolder = root.resolve("user").resolve("Sketchbook").resolve("IoTW_Sketch");
		    //System.out.println("targetfolder"+targetFolder);;
		 	Path targetIno    = targetFolder.resolve("IoTW_Sketch.ino");
		    //System.out.println("targetIno"+targetIno);;

           switchToTargetAndWrite(targetIno, source);
           
		}

	
	
		
	
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
	
	
