package com.ardublock.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;

import com.ardublock.core.Context;
import com.ardublock.translator.Translator;
import com.ardublock.ui.listener.ArdublockWorkspaceListener;
import com.ardublock.ui.listener.GenerateCodeButtonListener;
import com.ardublock.ui.listener.NewButtonListener;
import com.ardublock.ui.listener.OpenButtonListener;
import com.ardublock.ui.listener.OpenblocksFrameListener;
import com.ardublock.ui.listener.SaveAsButtonListener;
import com.ardublock.ui.listener.SaveButtonListener;

import edu.mit.blocks.controller.WorkspaceController;
import edu.mit.blocks.workspace.Page;
import edu.mit.blocks.workspace.PageChangeEventManager;
import edu.mit.blocks.workspace.Workspace;
import javax.swing.BorderFactory;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class OpenblocksFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2841155965906223806L;

	private Context context;
	private JFileChooser fileChooser;
	private FileFilter ffilter;
	
	private static ResourceBundle uiMessageBundle;
	
	public JComboBox boardComboBox;
	public JComboBox debugComboBox;
	public JCheckBox greenCheckBox;
	

	public JComboBox<String> tutorComboBox = new JComboBox<String>();
	 
	public void addListener(OpenblocksFrameListener ofl)
	{
		context.registerOpenblocksFrameListener(ofl);
	}
	
	public String makeFrameTitle()
	{
		String title = Context.APP_NAME + " " + context.getSaveFileName();
		if (context.isWorkspaceChanged())
		{
			title = title + " *";
		}
		return title;
		
	}
	
	public void bringToFront() {
  		    setAlwaysOnTop(true);
	        toFront();
	        requestFocus();
	        setAlwaysOnTop(false); 
	}
	
	public OpenblocksFrame()
	{
		context = Context.getContext();
		this.setTitle(makeFrameTitle());
		//
		 Image img = null;
		  // Lade das Bild von einem relativen Pfad
	        String relativePath = "/com/ardublock/block/IoTkit/IconMakeyLab.png";  // Der Pfad innerhalb des Ressourcenverzeichnisses
	      
	        try {
	            URL imgURL = getClass().getResource(relativePath);
	            if (imgURL != null) {
	                BufferedImage originalImage = ImageIO.read(imgURL);
	                // Skaliere das Bild in hoher Qualität
	                img = originalImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
	            } else {
	                System.out.println("Bild nicht gefunden: " + relativePath);
	            }
	        } catch (IOException e) {
	            System.out.println("Das Bild konnte nicht geladen werden: " + e.getMessage());
	        }
	
		  
		  if (img != null) {
	           // Image img = imgIcon.getImage();

	            // Setze das Bild als Fenstersymbol
	            setIconImage(img);
	        } else {
	            System.out.println("Das Bild konnte nicht geladen werden.");
	        }
		
		//
		
		this.setSize(new Dimension(1024, 760));
		
		this.setLayout(new BorderLayout());
		//put the frame to the center of screen
		this.setLocationRelativeTo(null);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
		
		// fileChooser = new JFileChooser();
		
		// Aufwachen im aktuellen Arbeitsverzeichnis /IoTW/Arduino
		// Userdaten in /IoTW/user
		Path wd    = Paths.get(System.getProperty("user.dir")).toAbsolutePath(); // IoTW/arduino
		Path base  = wd.getParent();                                             // IoTW
		Path target= (base != null ? base.resolve("user") : wd.resolve("..\\user")).normalize();

		File dir = target.toFile();
		if (!dir.isDirectory()) dir.mkdirs(); // muss existieren

		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(dir);
		
		ffilter = new FileNameExtensionFilter(uiMessageBundle.getString("ardublock.file.suffix"), "abp");
		fileChooser.setFileFilter(ffilter);
		fileChooser.addChoosableFileFilter(ffilter);
		tutorComboBox.setMaximumRowCount(10);
		 
		
		initOpenBlocks();
		

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	        // WindowListener to add a close confirmation dialog
		this.addWindowListener(new WindowAdapter() {
	            @Override
	            public void windowClosing(WindowEvent e) {
	            	Context context = Context.getContext();
	            	if (context.isWorkspaceChanged()) {
	  	                int confirm = JOptionPane.showOptionDialog(
		                        null,
		                        uiMessageBundle.getString("message.question.exit"),
		                        uiMessageBundle.getString("message.title.question"), //"Exit Confirmation",
		                        JOptionPane.YES_NO_OPTION,
		                        JOptionPane.QUESTION_MESSAGE,
		                        null,
		                        null, 
		                        null //"No"
		                );
		                if (confirm == JOptionPane.YES_OPTION) {
		                	dispose(); // Close the window
		                }
	            	} else dispose(); // Close the window
	            }
	        });

	}
	
	 
	
	

	public void changeBoardVersion()
	{
		int optionValue;
		if 	(  context.isWorkspaceChanged()) {
			optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.question.newboard"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
		} else {
			optionValue = JOptionPane.YES_OPTION;
		}
		
		if (optionValue == JOptionPane.NO_OPTION)
		{
			//- change combobox selection back to the current version.
			boardComboBox.setSelectedItem((String)context.ArdublockVersion);
	//		System.out.println("Setting combo box back to original: " + context.ArdublockVersion);
			return;
		}
		else {
	//		System.out.println("changeBoard in der Proc- "+boardComboBox.getSelectedItem());
			context.ArdublockVersion = (String) boardComboBox.getSelectedItem();
			
			String[] tutorList      = new String[0];
			String[] tutorList_trim = new String[0];

//			if (tutorComboBox != null) {
//			    tutorComboBox.setModel(new DefaultComboBoxModel<>(tutorList));
//			} else {
//			    System.err.println("vorher tutorComboBox ist null!");
//			}
			
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		    context.writeVersion();
			context.resetWorksapce();
			context.setWorkspaceChanged(false);
			this.setTitle(this.makeFrameTitle());
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			// Tutor aktualisieren
			Object selectedBoard = boardComboBox.getSelectedItem();
			if (selectedBoard == null) {
			    System.err.println("Kein Board ausgewählt.");
			    return; // oder setze einen Default-Wert
			}

			String board = selectedBoard.toString();
			//System.out.println("board"+board);
			
			String Dir, MyDir=System.getProperty("user.dir");
	        File currentDirFile = new File(MyDir);
	        // Den übergeordneten Ordner (eine Ebene zurück) abrufen
	        String UserDir = currentDirFile.getParent();
			
			Locale currentLocale = Locale.getDefault();
			String language = currentLocale.getLanguage();
			// In ein File-Objekt umwandeln
		//	System.out.println("change dir: " + context.ArdublockVersion);
			if (context.isInArduino())
				Dir = UserDir+"/tutor/" + language+ "/"+boardComboBox.getSelectedItem() + "/";

			else
			    Dir = "E:/IoTW/tutor/"+ language+ "/"+boardComboBox.getSelectedItem()+"/";
			
			tutorList = getSubDirectoryNames(Dir);
			 // Neues Array mit gefilterten Strings erstellen
			tutorList_trim = Arrays.stream(tutorList)
	                .map(s -> s.replaceFirst("^\\d+_", ""))  // Entfernt NUR die Zahl + "_"
	                .toArray(String[]::new);
			
			
			// Falls getSubDirectoryNames null zurückgibt, lieber ein leeres Array verwenden
			if (tutorList == null) {
			    tutorList = new String[0];
			    tutorList_trim = new String[0];
			}

			if (tutorComboBox != null) {
			    tutorComboBox.setModel(new DefaultComboBoxModel<>(tutorList_trim));
			} else {
			    System.err.println("tutorComboBox ist null!");
			}
	 
			if (tutorList.length <= 1) {
	            tutorComboBox.setEnabled(false);
	        } else {
	            tutorComboBox.setEnabled(true);
	        }
	        
			
		}
	}	
	
	 /**
     * Sucht im angegebenen Verzeichnis nach einer PDF-Datei und öffnet die erste gefundene
     * im Standard-Webbrowser.
     *
     * @param directoryPath Der Pfad zum Verzeichnis, in dem nach einer PDF-Datei gesucht wird.
     */
    public static void openPdfInBrowser(String directoryPath) {
        // Erstelle ein File-Objekt für das Verzeichnis
        File directory = new File(directoryPath);
        
        // Prüfen, ob das Verzeichnis existiert und gültig ist
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Ungültiges Verzeichnis: " + directoryPath);
            return;
        }

        // Suche nach Dateien, deren Name mit ".pdf" endet (unabhängig von Groß-/Kleinschreibung)
        File[] pdfFiles = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".pdf");
            }
        });

        // Überprüfen, ob mindestens eine PDF-Datei gefunden wurde
        if (pdfFiles == null || pdfFiles.length == 0) {
      //      System.out.println("Keine PDF-Datei im Verzeichnis gefunden: " + directoryPath);
            return;
        }

        // Wähle die erste gefundene PDF-Datei aus
        File pdfFile = pdfFiles[0];
        //System.out.println("Öffne PDF-Datei im Browser: " + pdfFile.getAbsolutePath());

        // Prüfe, ob das Desktop-Feature unterstützt wird
        if (!Desktop.isDesktopSupported()) {
            System.err.println("Desktop wird auf diesem System nicht unterstützt.");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.BROWSE)) {
            System.err.println("Die BROWSE-Aktion wird auf diesem System nicht unterstützt.");
            return;
        }
        
        int optionValue = JOptionPane.showOptionDialog(null, pdfFile, "Open pdf Resources", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
   		if (optionValue == JOptionPane.NO_OPTION)
   		{
   			return;
   	    }

        try {
            // Konvertiere die Datei in eine URI und öffne sie im Browser
            URI pdfUri = pdfFile.toURI();
            desktop.browse(pdfUri);
        } catch (Exception e) {
            System.err.println("Fehler beim Öffnen der PDF-Datei im Browser:");
            e.printStackTrace();
        }
    }

	

	 /**
     * Öffnet die Datei Link.txt im angegebenen Verzeichnis, liest den darin enthaltenen Link aus
     * und öffnet diesen im Standardbrowser.
     *
     * @param directoryPath Der Pfad zum Verzeichnis, in dem sich die Datei Link.txt befindet.
     */
    public static void openLinkFromDirectory(String directoryPath) {
        // Prüfen, ob das übergebene Verzeichnis existiert und ein Verzeichnis ist
     //   System.out.println("openLink"+directoryPath);
        
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
         //   System.err.println("Das Verzeichnis existiert nicht oder ist kein Verzeichnis: " + directoryPath);
            return;
        }

        // Erstelle ein File-Objekt für Link.txt im übergebenen Verzeichnis
        File linkFile = new File(directory, "Link.txt");
        if (!linkFile.exists()) {
         //   System.err.println("Die Datei Link.txt wurde im Verzeichnis " + directoryPath + " nicht gefunden.");
            return;
        }

        // Lese den Link aus der Datei
        String link = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(linkFile))) {
            link = reader.readLine();
            if (link != null) {
                link = link.trim();
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Datei: " + e.getMessage());
            return;
        }

        if (link == null || link.isEmpty()) {
            System.err.println("Der Link in der Datei ist leer.");
            return;
        }

        // Prüfen, ob die Desktop-Funktionalität unterstützt wird
        if (!Desktop.isDesktopSupported()) {
            System.err.println("Desktop wird auf diesem System nicht unterstützt.");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.BROWSE)) {
            System.err.println("Die BROWSE-Aktion wird auf diesem System nicht unterstützt.");
            return;
        }
        int optionValue = JOptionPane.showOptionDialog(null, link, "Open Internet Resources", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
		if (optionValue == JOptionPane.NO_OPTION)
		{
			return;
	    }
	    
        
        // Versuche, den Link im Standardbrowser zu öffnen
        try {
            URI uri = new URI(link);
            desktop.browse(uri);
         //   System.out.println("Link wird geöffnet: " + link);
        } catch (Exception e) {
            System.err.println("Fehler beim Öffnen des Links: " + e.getMessage());
        }
    }

    
    /**
     * Gibt alle Unterverzeichnisse des angegebenen Verzeichnispfads als String-Array zurück. Die erste Rückgabe ist immer "AI Tutor"
     *
     * @param directoryPath Der Pfad zum Verzeichnis, in dem nach Unterverzeichnissen gesucht werden soll.
     * @return Ein String-Array, das die vollständigen Pfade der gefundenen Unterverzeichnisse enthält.
     *         Falls der Pfad ungültig ist oder keine Unterverzeichnisse vorhanden sind, wird ein leeres Array zurückgegeben.
     */
    public static String[] getSubDirectoryNames(String directoryPath) {
        File directory = new File(directoryPath);

        // Liste für die Ergebnisnamen erstellen (Typ explizit angeben)
        List<String> namesList = new ArrayList<String>();

        // Füge den festen Eintrag hinzu
        
        namesList.add(uiMessageBundle.getString("ardublock.ui.aiHelp"));

        
        // Prüfen, ob der Pfad existiert und ein Verzeichnis ist
        if (!directory.exists() || !directory.isDirectory()) {
            //System.err.println("Der angegebene Pfad ist kein gültiges Verzeichnis: " + directoryPath);
            return namesList.toArray(new String[0]);
        }

        // Alle Unterverzeichnisse auflisten
        File[] subDirs = directory.listFiles(File::isDirectory);
        if (subDirs != null) {
            for (File subDir : subDirs) {
                namesList.add(subDir.getName());
            }
        }
        return namesList.toArray(new String[0]);
    }

   
   
    /**
     * Sucht im übergebenen Verzeichnis nach einer WAV-Datei und öffnet diese im Standardbrowser.
     * Dadurch übernimmt der Browser die Wiedergabe und bietet auch eigene Steuerelemente.
     *
     * @param directoryPath Der Pfad zum Verzeichnis, in dem nach einer WAV-Datei gesucht wird.
     */
    public static void playWavInBrowser(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Ungültiges Verzeichnis: " + directoryPath);
            return;
        }

        // Suche nach Dateien, die auf ".wav" enden (unabhängig von Groß-/Kleinschreibung)
        File[] wavFiles = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".wav");
            }
        });

        if (wavFiles == null || wavFiles.length == 0) {
//            System.out.println("Keine WAV-Datei im Verzeichnis gefunden.");
            return;
        }

        // Wähle die erste gefundene WAV-Datei aus
        File wavFile = wavFiles[0];
//        System.out.println("Öffne WAV-Datei im Browser: " + wavFile.getName());

        // Prüfe, ob Desktop und BROWSE unterstützt werden
        if (!Desktop.isDesktopSupported()) {
            System.err.println("Desktop wird auf diesem System nicht unterstützt.");
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.BROWSE)) {
            System.err.println("Die BROWSE-Aktion wird auf diesem System nicht unterstützt.");
            return;
        }

        int optionValue = JOptionPane.showOptionDialog(null, wavFile, "Open wav Resources", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
   		if (optionValue == JOptionPane.NO_OPTION)
   		{
   			return;
   	    }
   	    
        
        
        
        try {
            // Konvertiere den Dateipfad in eine URI und öffne diese im Browser
            URI uri = wavFile.toURI();
            desktop.browse(uri);
        } catch (Exception e) {
            System.err.println("Fehler beim Öffnen der WAV-Datei im Browser: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    public static String findAbpFile(String directoryPath) {
        // Erstelle ein File-Objekt für das Verzeichnis
        File directory = new File(directoryPath);

        // Prüfen, ob das Verzeichnis existiert und gültig ist
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Kein gültiges Verzeichnis: " + directoryPath);
            return null;
        }

        // Suche nach Dateien, deren Name mit ".abp" endet (unabhängig von Groß-/Kleinschreibung)
        File[] abpFiles = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".abp");
            }
        });

        // Falls mindestens eine Datei gefunden wurde, wird die erste zurückgegeben
        if (abpFiles != null && abpFiles.length > 0) {
            return abpFiles[0].getAbsolutePath();
        }

        // Falls keine .abp-Datei gefunden wurde, gebe null zurück
        //System.out.println("Keine .abp-Datei gefunden in " + directoryPath);
        return "";
    }
    
	
	private void initOpenBlocks()
	{
		final Context context = Context.getContext();
		
		int mymargin_lr=10;
		int mymargin_ul=3;
		
		/*
		WorkspaceController workspaceController = context.getWorkspaceController();
		JComponent workspaceComponent = workspaceController.getWorkspacePanel();
		*/
		
		final Workspace workspace = context.getWorkspace();
		
		// WTF I can't add worksapcelistener by workspace contrller
		workspace.addWorkspaceListener(new ArdublockWorkspaceListener(this));

		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		JButton newButton = new JButton(uiMessageBundle.getString("ardublock.ui.new"));
		newButton.setMargin(new Insets(mymargin_ul, mymargin_lr, mymargin_ul, mymargin_lr)); // Innenabstände: Oben, Links, Unten, Rechts
		newButton.addActionListener(new NewButtonListener(this));

		JButton saveButton = new JButton(uiMessageBundle.getString("ardublock.ui.save"));
		saveButton.setMargin(new Insets(mymargin_ul, mymargin_lr, mymargin_ul, mymargin_lr)); // Innenabstände: Oben, Links, Unten, Rechts
		saveButton.addActionListener(new SaveButtonListener(this));

		JButton saveAsButton = new JButton(uiMessageBundle.getString("ardublock.ui.saveAs"));
		saveAsButton.setMargin(new Insets(mymargin_ul, mymargin_lr, mymargin_ul, mymargin_lr)); // Innenabstände: Oben, Links, Unten, Rechts
		saveAsButton.addActionListener(new SaveAsButtonListener(this));

		
		JButton openButton = new JButton(uiMessageBundle.getString("ardublock.ui.load"));
		openButton.setMargin(new Insets(mymargin_ul, mymargin_lr, mymargin_ul, mymargin_lr)); // Innenabstände: Oben, Links, Unten, Rechts
		openButton.addActionListener(new OpenButtonListener(this));
		String mess;
		
		if (context.getArduinoCodeFileString()=="") {
			//System.out.println("in");
			mess = uiMessageBundle.getString("ardublock.ui.upload");
		} else {
			// mess = uiMessageBundle.getString("ardublock.ui.generate");
			Path path = Paths.get(context.getArduinoCodeFileString());
	        // Nur den Dateinamen extrahieren
	        String fileName = path.getFileName().toString();
			mess = fileName;
		}
			
		JButton generateButton = new JButton(mess);
		generateButton.setMargin(new Insets(mymargin_ul, mymargin_lr, mymargin_ul, mymargin_lr)); // Innenabstände: Oben, Links, Unten, Rechts
		generateButton.addActionListener(new GenerateCodeButtonListener(this, context));

		JButton serialMonitorButton = new JButton(uiMessageBundle.getString("ardublock.ui.serialMonitor"));
		serialMonitorButton.setMargin(new Insets(mymargin_ul, mymargin_lr, mymargin_ul, mymargin_lr)); // Innenabstände: Oben, Links, Unten, Rechts
		serialMonitorButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				context.getEditor().handleSerial();
			}
		});
		
		
		JButton saveImageButton = new JButton(uiMessageBundle.getString("ardublock.ui.saveImage"));
		saveImageButton.setMargin(new Insets(mymargin_ul, mymargin_lr, mymargin_ul, mymargin_lr)); // Innenabstände: Oben, Links, Unten, Rechts
		saveImageButton.addActionListener(new ActionListener () {

			
			public void actionPerformed(ActionEvent e) {
			    Dimension size = workspace.getCanvasSize();
			   // System.out.println("Canvas Size: " + size);

			    int width = Math.min(size.width*3,  10000); // Limit width to avoid excessive memory usage
			    int height = Math.min(size.height*2, 10000); // Limit height to avoid excessive memory usage

			    // Ensure we have a valid image size
			    if (width <= 0 || height <= 0) {
			        System.err.println("Invalid canvas size. Aborting image creation.");
			        return;
			    }

		//	    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_565_RGB);
			    Graphics2D g = bi.createGraphics();

			    try {
			        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			        // Adjust scale factor based on actual canvas size
			        double scaleFactor = 300.0 / 72.0;
			        g.scale(scaleFactor, scaleFactor);

			        // Paint the workspace content onto the BufferedImage
			        workspace.getBlockCanvas().getPageAt(0).getJComponent().paint(g);

			        // Show file chooser dialog
			        JFileChooser fc = new JFileChooser();
			        fc.setSelectedFile(new File("ardublock.png"));
			        int returnVal = fc.showSaveDialog(workspace.getBlockCanvas().getJComponent());

			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
			            ImageIO.write(bi, "png", file);
			        //    System.out.println("Image saved successfully: " + file.getAbsolutePath());
			        }
			    } catch (Exception ex) {
			        ex.printStackTrace(); // Print exception details instead of ignoring them
			    } finally {
			        g.dispose();
			    }
			}
			
			
			/*
			public void actionPerformed(ActionEvent e) {
				Dimension size = workspace.getCanvasSize();
				System.out.println("size: " + size);
				BufferedImage bi = new BufferedImage(7000,7000, BufferedImage.TYPE_INT_RGB); //#kgo 2560 2560
				Graphics2D g = (Graphics2D)bi.createGraphics();
		
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				double theScaleFactor = (300d/72d);  //#kgo memory problem in ide
				g.scale(theScaleFactor,theScaleFactor);
				workspace.getBlockCanvas().getPageAt(0).getJComponent().paint(g);
		
				try{
					final JFileChooser fc = new JFileChooser();
					fc.setSelectedFile(new File("ardublock.png"));
					int returnVal = fc.showSaveDialog(workspace.getBlockCanvas().getJComponent());
			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
						ImageIO.write(bi,"png",file);
			        }
				} catch (Exception e1) {
					
				} finally {
					g.dispose();
				}
			}
			*/
			
		});

		   // Green Checkbox erstellen
        JCheckBox greenCheckBox = new JCheckBox("Green Code");
        greenCheckBox.setBounds(50, 50, 200, 30);
        // Event Listener für die Checkbox
        greenCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	Translator.setGreenProgram(1);
            } else {
              	Translator.setGreenProgram(0);
            }
        });
		
		
		String[] programList = {"Starter","Makey","Octopus"};
		boardComboBox = new JComboBox<String>(programList);
		//boardComboBox.setFont(new Font("Arial", Font.BOLD, 18)); // Schriftgröße 18
		
		boardComboBox.setSelectedItem((String)context.ArdublockVersion);
		
		boardComboBox.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
		//		System.out.println("item state changed to: " + boardComboBox.getSelectedItem() + ".  Current Ardublock Version = " + context.ArdublockVersion);
				//- ignore if selected program is same as the current one. 
				if (context.ArdublockVersion == boardComboBox.getSelectedItem()) {
					System.out.println("skipped");
					return;
				}
				else {
			//		System.out.println("changeBoard - "+boardComboBox.getSelectedItem());
					changeBoardVersion();
				}
				if (context.ArdublockVersion.contains("Makey")) {
			  	   greenCheckBox.setSelected(Translator.isGreenProgram() != 0);
			 	   //greenCheckBox.setVisible(true);
				} else {
				   Translator.setGreenProgram(0);	
			  	   greenCheckBox.setSelected(Translator.isGreenProgram() != 0);
			 	   greenCheckBox.setVisible(false);
					
				}
			}
			
		});
		
	//	JLabel debugLabel = new JLabel("        "+uiMessageBundle.getString("ardublock.ui.debug"));
		String[] debugList = {uiMessageBundle.getString("ardublock.ui.debug0"),uiMessageBundle.getString("ardublock.ui.debug1"),uiMessageBundle.getString("ardublock.ui.debug2")};
        JComboBox<String> debugComboBox = new JComboBox<>(debugList);

        // Set initial selection
        int currentDebugLevel = Translator.isDebugProgram();
        if (currentDebugLevel >= 0 && currentDebugLevel < debugList.length) {
            debugComboBox.setSelectedIndex(currentDebugLevel);
        } else {
            debugComboBox.setSelectedIndex(0); // Default to "None"
        }

        // Add action listener for selection changes
        debugComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int actual = Translator.isDebugProgram();
                int selectedIndex = debugComboBox.getSelectedIndex();

                if (actual == selectedIndex) {
//                    System.out.println("Selection unchanged. Skipping update.");
                    return;
                }

                // Update debug program
                if (selectedIndex >= 0) {
                    Translator.setDebugProgram(selectedIndex);
 //                   System.out.println("Debug program changed to: " + debugList[selectedIndex]);
                }
            }
        });

        
 	
    	String Dir, MyDir=System.getProperty("user.dir");
        File currentDirFile = new File(MyDir);
        // Den übergeordneten Ordner (eine Ebene zurück) abrufen
        String UserDir = currentDirFile.getParent();
		
		Locale currentLocale = Locale.getDefault();
		String language = currentLocale.getLanguage();
		// In ein File-Objekt umwandeln
       
		if (context.isInArduino())
			Dir = UserDir+"/tutor/" + language+ "/"+boardComboBox.getSelectedItem() + "/";

		else
		    Dir = "E:/IoTW/tutor/"+ language+ "/"+boardComboBox.getSelectedItem()+"/";
	
		//System.out.println("ggg"+Dir);
		//System.out.println(boardComboBox.getSelectedItem());
		//System.out.println(context.ArdublockVersion);
		
		String[] tutorList = getSubDirectoryNames(Dir);
		 // Neues Array mit gefilterten Strings erstellen
        String[] tutorList_trim = Arrays.stream(tutorList)
                .map(s -> s.replaceFirst("^\\d+_", ""))  // Entfernt NUR die Zahl + "_"
                .toArray(String[]::new);
		
		
		
		
		
		// Erzeuge separate Arrays für jeden Bestandteil
		
       // JComboBox<String> tutorComboBox = new JComboBox<>(tutorList);
		tutorComboBox.setModel(new DefaultComboBoxModel<String>(tutorList_trim));
		
        // Set initial selection
        tutorComboBox.setSelectedIndex(0); // Default to "None"
        if (tutorList.length <= 1) {
            tutorComboBox.setEnabled(false);
        } else {
            tutorComboBox.setEnabled(true);
        }
        
        
        // Add action listener for selection changes
        tutorComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
                //int actual = Translator.isDebugProgram();
                int selectedIndex = tutorComboBox.getSelectedIndex();
                String Dir;
                if (context.isInArduino())
        			Dir = UserDir+"/tutor/" + language+ "/"+boardComboBox.getSelectedItem() + "/";

        		else
        		    Dir = "E:/IoTW/tutor/"+ language+ "/"+boardComboBox.getSelectedItem()+"/";
                // Update debug program
                if (selectedIndex > 0) {
                    String[] tutorList = getSubDirectoryNames(Dir);
           		 // Neues Array mit gefilterten Strings erstellen
                    
                    String[] tutorList_trim = Arrays.stream(tutorList)
                           .map(s -> s.replaceFirst("^\\d+_", ""))  // Entfernt NUR die Zahl + "_"
                           .toArray(String[]::new);
           			String DemoFile = findAbpFile(Dir+tutorList[tutorComboBox.getSelectedIndex()]);
              if (!DemoFile.isEmpty()) {
              		doOpenTutorFile(DemoFile);
  		      }
              	playWavInBrowser(Dir+tutorList[selectedIndex]);                	
              	openLinkFromDirectory(Dir+tutorList[selectedIndex]);
              	openPdfInBrowser(Dir+tutorList[selectedIndex]);
              	
                	 //SwingUtilities.invokeLater(() -> bringToFront());
                	 Timer timer = new Timer(800, evt -> bringToFront());
                	    timer.setRepeats(false); 
                	    timer.start();
                	   // System.out.println("wsc");
               	     SwingUtilities.invokeLater(() -> context.setWorkspaceChanged(false));
                            	 
                	 
                	 
              
                }
              
            }
            
        });
		JLabel zoomLabel = new JLabel("Zoom: ");
		JButton zoomIn = new JButton("+");
		zoomIn.setMargin(new Insets(mymargin_ul, mymargin_lr, mymargin_ul, mymargin_lr)); // Innenabstände: Oben, Links, Unten, Rechts
		zoomIn.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				double zoom = Page.getZoomLevel();
				zoom += 0.05;
				Page.setZoomLevel(zoom);
				if (zoom > 2) zoom = 2;
				workspace.setWorkspaceZoom(zoom);
				PageChangeEventManager.notifyListeners();
	//	        System.out.println("Zoom level: " + Page.getZoomLevel());
			}
		});
		
		JButton zoomOut = new JButton("- ");
		zoomOut.setMargin(new Insets(mymargin_ul, mymargin_lr, mymargin_ul, mymargin_lr)); // Innenabstände: Oben, Links, Unten, Rechts
		zoomOut.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				double zoom = Page.getZoomLevel();
				zoom -= 0.05;
				if (zoom < 0.02) zoom = 0.02;
				Page.setZoomLevel(zoom);
				workspace.setWorkspaceZoom(zoom);
				PageChangeEventManager.notifyListeners();
//		        System.out.println("Zoom level: " + Page.getZoomLevel());
			}
		});
		
		
		JPanel jp0 = new JPanel();
		JPanel jp1 = new JPanel();
		JPanel jp2 = new JPanel();
		JPanel jp3 = new JPanel();
		JPanel jp4 = new JPanel();
		JPanel jp5 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel jp7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel jp8 = new JPanel();
		JPanel jp9 = new JPanel();

		Border border = BorderFactory.createLineBorder(Color.black);
		Border margin = new EmptyBorder(1,1,1,1);
//		JLabel abpLabel = new JLabel("Block:");
//		jp1.add(abpLabel);
		jp1.add(newButton);
		jp1.add(saveButton);
		jp1.add(saveAsButton);
		jp1.add(openButton);
		//jp1.setBorder(new CompoundBorder(border, margin));

		jp0.add(boardComboBox);
		jp0.add(tutorComboBox);
//		if (context.getArduinoCodeFileString()=="")
//   		     jp3.add(serialMonitorButton);



		JPanel bottomPanel = new JPanel();

		// Angenommen, die URL steht in Deinem uiMessageBundle unter dem Schlüssel "ardublock.ui.website"
		//String websiteURL = uiMessageBundle.getString("ardublock.ui.website");
/*
		String websiteURL = "www.umwelt-campus.de";
		JLabel websiteLabel = new JLabel(
		    "<html>         " +
		    "<a href=\"" + websiteURL + "\" style=\"text-decoration: underline; color: blue;\">" + websiteURL + "</a>" +
		    ", Hochschule Trier" +
		    "</html>         "
		);
	*/	
		
		String websiteURL = "www.iot-werkstatt.de";
		JLabel websiteLabel = new JLabel(
		    "<html>" +
	          "<div style=\"padding-left:40px; padding-right:30px;\">" +
	           "Umwelt-Campus Birkenfeld, Hochschule Trier, " +
			  
	            "<a href=\"" + websiteURL + "\" style=\"text-decoration: underline; color: blue;\">" 
		          + websiteURL + 
		        "</a>" +
		      "</div>" +
		    "</html>"
		);

		// Setze den Cursor auf den Hand-Cursor, damit der Benutzer erkennt, dass es klickbar ist
		websiteLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		// Füge einen MouseListener hinzu, der beim Klick die URL im Standardbrowser öffnet
		websiteLabel.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		    	try {
				    String url = "http://www.iot-werkstatt.de";  // Ersetze durch Deine URL
				    System.out.println("Versuche, URL zu öffnen: " + url);
				    // Option 1: Direkte Erzeugung der URI
				    URI uri = new URI(url);
				    // Option 2: Mit URL-Konvertierung (falls Probleme auftreten)
				    // URI uri = new URL(url).toURI();
				    Desktop.getDesktop().browse(uri);
				} catch (Exception ex) {
				    ex.printStackTrace();
				    System.err.println("Failed to open Webseite. Error message: " + ex.getMessage());
				}
		    }
		});
		
		
		/*
		JButton websiteButton = new JButton(uiMessageBundle.getString("ardublock.ui.website"));
		websiteButton.setMargin(new Insets(mymargin_ul, mymargin_lr, mymargin_ul, mymargin_lr)); // Innenabstände: Oben, Links, Unten, Rechts
		websiteButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
			    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    URL url;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
						url = new URL("https://www.umwelt-campus.de/iot-werkstatt/tutorials/");
			            desktop.browse(url.toURI());
			        } catch (Exception e1) {
			            e1.printStackTrace();
			        }
			    }
			}
		});
		*/
		JLabel homeLabel = new JLabel("        Umwelt-Campus Birkenfeld, Hochschule Trier       ");
		JLabel versionLabel = new JLabel(uiMessageBundle.getString("ardublock.ui.version"));
		//JLabel inoLabel = new JLabel("Code: ");

		
		JLabel inoFileLabel = new JLabel(context.getArduinoCodeFileString());
		inoFileLabel.setFont(new Font("Arial", Font.PLAIN, 8)); // Schriftgröße 8
	
		//jp5.add(inoLabel);
        jp5.add(jp3);
		jp5.add(generateButton);
	
	//	jp7.add(zoomLabel);
	//	jp7.add(zoomIn);
	//	jp7.add(zoomOut);
	
		
		buttons.add(jp0);
		buttons.add(jp7);
		buttons.add(jp8);
		buttons.add(jp9);
		buttons.add(jp1);
		buttons.add(jp2);
		buttons.add(jp3);
		buttons.add(jp4);
		buttons.add(jp5);
	
	
	//	bottomPanel.setLayout(new GridLayout(1, 1, 10, 1));
		//bottomPanel.add(debugLabel);
		
//		jp7.add(zoomLabel);
		bottomPanel.add(zoomIn);
		bottomPanel.add(zoomOut);
		bottomPanel.add(debugComboBox);

		//System.out.println(" los"+context.ArdublockVersion);

		bottomPanel.add(greenCheckBox);
	    //greenCheckBox.setVisible(context.ArdublockVersion.contains("Makey"));
	    greenCheckBox.setVisible(false);
		//bottomPanel.add(homeLabel);
		bottomPanel.add(websiteLabel);
	    bottomPanel.add(saveImageButton);
		bottomPanel.add(versionLabel);
		//bottomPanel.add(inoFileLabel);
		
		//bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(buttons, BorderLayout.NORTH);
		this.add(bottomPanel, BorderLayout.SOUTH);
		this.add(workspace, BorderLayout.CENTER);
	}
	
	public void doOpenArduBlockFile()
	{
		
		if (context.isWorkspaceChanged())
		{
			int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.content.open_unsaved"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			if (optionValue == JOptionPane.YES_OPTION)
			{
				doSaveArduBlockFile();
				this.loadFile();
			}
			else
			{
				if (optionValue == JOptionPane.NO_OPTION)
				{
					this.loadFile();
				}
			}
		}
		else
		{
			this.loadFile();
		}
		this.setTitle(makeFrameTitle());
	}
	
	private void loadFile()
	{
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File savedFile = fileChooser.getSelectedFile();
			if (!savedFile.exists())
			{
				JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.file_not_found"), uiMessageBundle.getString("message.title.error"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, null, JOptionPane.OK_OPTION);
				return ;
			}
			
			try
			{
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				context.loadArduBlockFile(savedFile);
				context.setWorkspaceChanged(false);
			}
			catch (IOException e)
			{
				JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.file_not_found"), uiMessageBundle.getString("message.title.error"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, null, JOptionPane.OK_OPTION);
				e.printStackTrace();
			}
			finally
			{
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}
	
	private void loadTutorFile(File Example)
	{
			try
			{
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				context.loadArduBlockFile(Example);
				context.setWorkspaceChanged(false);
			}
			catch (IOException e)
			{
				JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.file_not_found"), uiMessageBundle.getString("message.title.error"), JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, null, JOptionPane.OK_OPTION);
				e.printStackTrace();
			}
			finally
			{
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
	}
	
	
	
	public void doOpenTutorFile(String filePath)
	{
		File demoFile= new File(filePath);

		if (context.isWorkspaceChanged())
		{
			int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.content.open_unsaved"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			if (optionValue == JOptionPane.YES_OPTION)
			{
				doSaveArduBlockFile();
				this.loadTutorFile(demoFile);
			}
			else
			{
				if (optionValue == JOptionPane.NO_OPTION)
				{
					this.loadTutorFile(demoFile);
				}
			}
		}
		else
		{
			this.loadTutorFile(demoFile);
		}
		this.setTitle(makeFrameTitle());
		context.setWorkspaceChanged(false);
	}
	
	
	
	public void doSaveArduBlockFile()
	{
//		if (!context.isWorkspaceChanged())
//		{
//		System.out.println("no change");
//			return ;
//		}
		
		String saveString = getArduBlockString();
		//System.out.println("doSave"+saveString);
		if (context.getSaveFilePath() == null)
		{
			chooseFileAndSave(saveString);
		}
		else
		{
			File saveFile = new File(context.getSaveFilePath());
		//	System.out.println("doSave"+saveFile);
			writeFileAndUpdateFrame(saveString, saveFile);
		}
	}

	
	public void doSaveAsArduBlockFile()
	{
		if (context.isWorkspaceEmpty())
		{
			return ;
		}
		
		String saveString = getArduBlockString();
		
		chooseFileAndSave(saveString);
		
	}
	
	private void chooseFileAndSave(String ardublockString)
	{
		File saveFile = letUserChooseSaveFile();
		saveFile = checkFileSuffix(saveFile);
		if (saveFile == null)
		{			
			return ;
		}
		
		if (saveFile.exists() && !askUserOverwriteExistedFile())
		{
			return ;
		}
		
		writeFileAndUpdateFrame(ardublockString, saveFile);
	}
	
	private String getArduBlockString()
	{
		WorkspaceController workspaceController = context.getWorkspaceController();
		return workspaceController.getSaveString();
	}
	
	private void writeFileAndUpdateFrame(String ardublockString, File saveFile) 
	{
		try
		{
			saveArduBlockToFile(ardublockString, saveFile);
			context.setWorkspaceChanged(false);
			this.setTitle(this.makeFrameTitle());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private File letUserChooseSaveFile()
	{
		int chooseResult;
		chooseResult = fileChooser.showSaveDialog(this);
		if (chooseResult == JFileChooser.APPROVE_OPTION)
		{
			return fileChooser.getSelectedFile();
		}
		return null;
	}
	
	private boolean askUserOverwriteExistedFile()
	{
		int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.content.overwrite"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
		return (optionValue == JOptionPane.YES_OPTION);
	}
	
	private void saveArduBlockToFile(String ardublockString, File saveFile) throws IOException
	{
		context.saveArduBlockFile(saveFile, ardublockString);
		context.setSaveFileName(saveFile.getName());
		context.setSaveFilePath(saveFile.getAbsolutePath());
	}
	
	public void doNewArduBlockFile()
	{
		if (context.isWorkspaceChanged())
		{
			int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.question.newfile_on_workspace_changed"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
			if (optionValue == JOptionPane.YES_OPTION)
			{
				System.out.println("save");
				doSaveArduBlockFile();
				if (context.isWorkspaceChanged()) return;
			}
			if (optionValue == JOptionPane.CANCEL_OPTION)
			{
				System.out.println("exit, do nothing");

				return;
			}

		}
		System.out.println("new");
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		context.resetWorksapce();
		context.setWorkspaceChanged(false);
		this.setTitle(this.makeFrameTitle());
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	
	
	private File checkFileSuffix(File saveFile)
	{
		if (saveFile == null) {
				return saveFile;
		}
		String filePath = saveFile.getAbsolutePath();
		if (filePath.endsWith(".abp"))
		{
			return saveFile;
		}
		else
		{
			return new File(filePath + ".abp");
		}
	}
}
