package com.ardublock.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ardublock.core.Context;
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


public class OpenblocksFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2841155965906223806L;

	private Context context;
	private JFileChooser fileChooser;
	private FileFilter ffilter;
	
	private ResourceBundle uiMessageBundle;
	
	public JComboBox boardComboBox;
	
	
	
	
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
	
	public OpenblocksFrame()
	{
		context = Context.getContext();
		this.setTitle(makeFrameTitle());
		this.setSize(new Dimension(1024, 760));
		this.setLayout(new BorderLayout());
		//put the frame to the center of screen
		this.setLocationRelativeTo(null);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		uiMessageBundle = ResourceBundle.getBundle("com/ardublock/block/ardublock");
		
		fileChooser = new JFileChooser();
		ffilter = new FileNameExtensionFilter(uiMessageBundle.getString("ardublock.file.suffix"), "abp");
		fileChooser.setFileFilter(ffilter);
		fileChooser.addChoosableFileFilter(ffilter);
		
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
	
		int optionValue = JOptionPane.showOptionDialog(this, uiMessageBundle.getString("message.question.newboard"), uiMessageBundle.getString("message.title.question"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION);
		if (optionValue == JOptionPane.NO_OPTION)
		{
			//- change combobox selection back to the current version.
			boardComboBox.setSelectedItem((String)context.ArdublockVersion);
			System.out.println("Setting combo box back to original: " + context.ArdublockVersion);
			return;
		}
		else {
			context.ArdublockVersion = (String) boardComboBox.getSelectedItem();
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			context.resetWorksapce();
			context.setWorkspaceChanged(false);
			this.setTitle(this.makeFrameTitle());
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		}
	}	
	
	private void initOpenBlocks()
	{
		final Context context = Context.getContext();
		
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
		newButton.addActionListener(new NewButtonListener(this));
		JButton saveButton = new JButton(uiMessageBundle.getString("ardublock.ui.save"));
		saveButton.addActionListener(new SaveButtonListener(this));
		JButton saveAsButton = new JButton(uiMessageBundle.getString("ardublock.ui.saveAs"));
		saveAsButton.addActionListener(new SaveAsButtonListener(this));
		JButton openButton = new JButton(uiMessageBundle.getString("ardublock.ui.load"));
		openButton.addActionListener(new OpenButtonListener(this));
		String mess;
		
		if (context.getArduinoCodeFileString()=="") {
			System.out.println("in");
			mess = uiMessageBundle.getString("ardublock.ui.upload");
		} else {
			mess = uiMessageBundle.getString("ardublock.ui.generate");
		}
			
		JButton generateButton = new JButton(mess);
		generateButton.addActionListener(new GenerateCodeButtonListener(this, context));
		JButton serialMonitorButton = new JButton(uiMessageBundle.getString("ardublock.ui.serialMonitor"));
		serialMonitorButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				context.getEditor().handleSerial();
			}
		});
		JButton saveImageButton = new JButton(uiMessageBundle.getString("ardublock.ui.saveImage"));
		saveImageButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				Dimension size = workspace.getCanvasSize();
				System.out.println("size: " + size);
				BufferedImage bi = new BufferedImage(7000, 7000, BufferedImage.TYPE_INT_RGB); //#kgp 2560 2560
				Graphics2D g = (Graphics2D)bi.createGraphics();
				double theScaleFactor = (600d/72d);  
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
		});

		String[] programList = {"Starter","Makey","Octopus"};
		boardComboBox = new JComboBox<String>(programList);
		boardComboBox.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("item state changed to: " + boardComboBox.getSelectedItem() + ".  Current Ardublock Version = " + context.ArdublockVersion);
				//- ignore if selected program is same as the current one. 
				if (context.ArdublockVersion == boardComboBox.getSelectedItem()) {
					System.out.println("skipped");
					return;
				}
				else {
					changeBoardVersion();
				}
			}
			
		});
		
		
		
		JLabel zoomLabel = new JLabel("Zoom: ");
		JButton zoomIn = new JButton("+");
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
		
		JButton zoomOut = new JButton("-");
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
		JLabel abpLabel = new JLabel("Block:");
		jp1.add(abpLabel);
		jp1.add(newButton);
		jp1.add(saveButton);
		jp1.add(saveAsButton);
		jp1.add(openButton);
		//jp1.setBorder(new CompoundBorder(border, margin));

		jp0.add(boardComboBox);
		if (context.getArduinoCodeFileString()=="")
   		     jp3.add(serialMonitorButton);



		JPanel bottomPanel = new JPanel();
		
		
		
		JButton websiteButton = new JButton(uiMessageBundle.getString("ardublock.ui.website"));
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
		JLabel versionLabel = new JLabel(uiMessageBundle.getString("ardublock.ui.version"));
		JLabel inoLabel = new JLabel("Arduino: ");
		JLabel inoFileLabel = new JLabel(context.getArduinoCodeFileString());
	//	JLabel zoomLabel = new JLabel("Zoom:");

		jp5.add(inoLabel);
        jp5.add(jp3);
		jp5.add(generateButton);
		//jp5.setBorder(new CompoundBorder(border, margin));

		jp7.add(zoomLabel);
		jp7.add(zoomIn);
		jp7.add(zoomOut);
		//jp7.setBorder(new CompoundBorder(border, margin));

		
		buttons.add(jp0);
		buttons.add(jp7);
		buttons.add(jp8);
		buttons.add(jp9);
		buttons.add(jp1);
		buttons.add(jp2);
		buttons.add(jp3);
		buttons.add(jp4);
		buttons.add(jp5);

			
		
		bottomPanel.add(versionLabel);
	    bottomPanel.add(saveImageButton);
		bottomPanel.add(websiteButton);
		bottomPanel.add(inoFileLabel);
		
		
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
	
	public void doSaveArduBlockFile()
	{
		if (!context.isWorkspaceChanged())
		{
			return ;
		}
		
		String saveString = getArduBlockString();
		
		if (context.getSaveFilePath() == null)
		{
			chooseFileAndSave(saveString);
		}
		else
		{
			File saveFile = new File(context.getSaveFilePath());
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
			if (optionValue != JOptionPane.YES_OPTION)
			{
				return ;
			}
		}
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		context.resetWorksapce();
		context.setWorkspaceChanged(false);
		this.setTitle(this.makeFrameTitle());
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	
	
	private File checkFileSuffix(File saveFile)
	{
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
