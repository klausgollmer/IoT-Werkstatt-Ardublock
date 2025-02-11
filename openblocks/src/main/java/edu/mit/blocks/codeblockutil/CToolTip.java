package edu.mit.blocks.codeblockutil;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JToolTip;
import javax.swing.plaf.basic.BasicToolTipUI;

public class CToolTip extends JToolTip {

    private static final long serialVersionUID = 328149080249L;
    private Color background = new Color(255, 255, 150);

    // Privater Standardkonstruktor
    private CToolTip() {
        this(new Color(55, 255, 225));
    }

    public CToolTip(Color background) {
        this.background = background;
        updateUI();
    }

    @Override
    public void updateUI() {
        setUI(new CToolTipUI(background));
    }
}

class CToolTipUI extends BasicToolTipUI {

    private static final int WIDTH = 150;
    private CellRendererPane renderer;
    private JEditorPane editorPane;
    private Color background;
    // Speichert den ursprünglich gesetzten Tooltip-Text,
    // z. B. "Alles was den Programmablauf steuert. ... Beispiele hier: https://www.umwelt-campus.de/..."
    private String originalTipText;
   
    public CToolTipUI(Color background) {
        super();
        // Hintergrundfarbe (weiß)
        this.background = Color.WHITE;
        renderer = new CellRendererPane();
        editorPane = new JEditorPane();
        editorPane.setMargin(new Insets(5, 5, 5, 5));
        editorPane.setFont(new Font("Arial", Font.PLAIN, 16));
        // Wir wollen HTML-Inhalt rendern
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        renderer.removeAll();
        renderer.add(editorPane);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Dimension size = c.getSize();
        editorPane.setBackground(background);
        renderer.paintComponent(g, editorPane, c, 1, 1, size.width - 1, size.height - 1, true);
    }
/*
    @Override
    public Dimension getPreferredSize(JComponent c) {
        String tipText = ((JToolTip) c).getTipText();
        if (tipText == null) {
            return new Dimension(0, 0);
        }
        // Speichern des Originaltexts, wie er vom Anwender gesetzt wurde.
        originalTipText = tipText;
        // Falls der Text noch nicht als HTML vorliegt, betten wir ihn in eine HTML-Struktur ein.
        // Dabei wird über makeLinksClickable() jede URL durch einen Link ersetzt, der nur den Server anzeigt.
        if (!tipText.trim().toLowerCase().startsWith("<html>")) {
            tipText = "<html><head>"
                    + "<style type=\"text/css\">"
                    + "body { word-break: break-all; overflow-wrap: break-word; white-space: normal; }"
                    + "</style>"
                    + "</head><body>" 
                    + makeLinksClickable(tipText) 
                    + "</body></html>";
        } else {
            if (!tipText.toLowerCase().contains("<body>")) {
                tipText = tipText.replaceFirst("(?i)<html>", 
                        "<html><head><style type=\"text/css\">body { word-break: break-all; overflow-wrap: break-word; white-space: normal; }</style></head><body>") 
                        + "</body></html>";
            }
        }
        editorPane.setText(tipText);
        Dimension d = editorPane.getPreferredSize();
        // Optional: Passe die Breite an (hier: maximal 2*min(WIDTH, d.width))
        d.width = 2 * Math.min(d.width, WIDTH)+30;
        d.height++;
        editorPane.setSize(d);
        return d;
    }
*/
    @Override
    public Dimension getPreferredSize(JComponent c) {
        String tipText = ((JToolTip) c).getTipText();
        if (tipText == null) {
            return new Dimension(0, 0);
        }
        // Speichern des Originaltexts, wie er vom Anwender gesetzt wurde.
        originalTipText = tipText;
        
        // Falls der Text noch nicht als HTML vorliegt, betten wir ihn in eine HTML-Struktur ein.
        // Dabei fügen wir einen CSS-Style ein, der eine feste Breite (WIDTH in Pixel) vorgibt.
        if (!tipText.trim().toLowerCase().startsWith("<html>")) {
            tipText = "<html><head>"
                    + "<style type=\"text/css\">"
                    + "body { font-family: Arial, sans-serif; font-size: 12px; "
                    + "word-break: normal; overflow-wrap: break-word; white-space: normal; "
                    + "width: " + WIDTH + "px; }"  // Hier wird die feste Breite definiert.
                    + "</style>"
                    + "</head><body>" 
                    + makeLinksClickable(tipText) 
                    + "</body></html>";
        } else {
            if (!tipText.toLowerCase().contains("<body>")) {
                tipText = tipText.replaceFirst("(?i)<html>", 
                        "<html><head><style type=\"text/css\">"
                        + "body { font-family: Arial, sans-serif; font-size: 12px; "
                        + "word-break: normal; overflow-wrap: break-word; white-space: normal; "
                        + "width: " + WIDTH + "px; }"
                        + "</style></head><body>") 
                        + "</body></html>";
            }
        }
        
        editorPane.setText(tipText);
        // Hole die vom EditorPane berechnete PreferredSize.
        Dimension d = editorPane.getPreferredSize();
        // Entferne die manuelle Breitenanpassung – die Breite wird nun durch CSS bestimmt.
        editorPane.setSize(d);
        return d;
    }


    
    
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispatchEventToEditor(e);
            }
        });
    }

    /**
     * Diese Methode sucht im übergebenen Text (z. B. "Alles was ... Beispiele hier: https://www.umwelt-campus.de/...")
     * alle Vorkommen von URLs (http:// oder https://) und ersetzt diese durch einen HTML-Link,
     * bei dem als Linktext nur der Host (z. B. "www.umwelt-campus.de") angezeigt wird.
     */
    private String makeLinksClickable(String text) {
        if (text == null) {
            return "";
        }
        Pattern pattern = Pattern.compile("(?i)(https?://\\S+)");
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String url = matcher.group(1);
            // Entferne nachgestellte Satzzeichen, falls vorhanden.
            url = url.replaceAll("[.,;:!?)]*$", "");
            String display = url;
            try {
                java.net.URI uri = new java.net.URI(url);
                if (uri.getHost() != null) {
                    display = uri.getHost();
                }
            } catch (Exception ex) {
                // Fallback: Nutze die volle URL
            }
            // Ersetze die URL durch einen Link, bei dem nur der Host angezeigt wird.
            String replacement = "<a href=\"" + url + "\" style=\"color: blue; text-decoration: underline;\">" + display + "</a>";
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Extrahiert die erste URL aus dem gegebenen Text.
     */
    private String extractFirstUrl(String text) {
        if (text == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("(?i)(https?://\\S+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String url = matcher.group(1);
            url = url.replaceAll("[.,;:!?)]*$", "");
            return url;
        }
        return null;
    }

    /**
     * Diese Methode wird beim Klick im Tooltip aufgerufen.
     * Es wird die erste URL aus dem ursprünglichen Tooltip-Text extrahiert und im Standardbrowser geöffnet.
     */
    private void dispatchEventToEditor(MouseEvent e) {
        String url = extractFirstUrl(originalTipText);
        if (url != null) {
            openWebpage(url);
        }
    }

    /**
     * Öffnet die übergebene URL im Standardbrowser.
     */
    private void openWebpage(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.err.println("Desktop wird nicht unterstützt!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

/*
package edu.mit.blocks.codeblockutil;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.URI;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicToolTipUI;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class CToolTip extends JToolTip {

    private static final long serialVersionUID = 328149080249L;
    private Color background = new Color(255, 255, 150);

    private CToolTip() {
        this(new Color(55, 255, 225));
    }

    public CToolTip(Color background) {
        this.background = background;
        updateUI();
    }

    @Override
    public void updateUI() {
        setUI(new CToolTipUI(background));
    }
}

class CToolTipUI extends BasicToolTipUI {

    private static final int WIDTH = 5000;
    private CellRendererPane renderer;
    private JEditorPane editorPane;
    private Color background;

    public CToolTipUI(Color background) {
        super();
        // falls du eine andere Farbe haben möchtest, weise sie zu:
        this.background = background;
        renderer = new CellRendererPane();
        editorPane = new JEditorPane();
        editorPane.setMargin(new Insets(5, 5, 5, 5));
        editorPane.setFont(new Font("Arial", Font.PLAIN, 16));

        // HTML einstellen, damit Links klickbar sind
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);

        // HyperlinkListener einfügen, der auf Klick in Link reagiert
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
  System.out.println("act:");
                    try {
                        openWebpage(e.getURL().toString());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                            "Fehler beim Öffnen der URL: " + ex.getMessage());
                    }
                }
            }
        });

        renderer.removeAll();
        renderer.add(editorPane);
    }

    
    private void openWebpage(String url) {
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    desktop.browse(new java.net.URI(url));
                }
            } else {
                System.err.println("Desktop wird nicht unterstützt!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    
    
    
    @Override
    public void paint(Graphics g, JComponent c) {
        Dimension size = c.getSize();
        editorPane.setBackground(background);
        renderer.paintComponent(g, editorPane, c, 1, 1,
                size.width - 1, size.height - 1, true);
    }

   
    @Override
    public Dimension getPreferredSize(JComponent c) {
        String tipText = ((JToolTip) c).getTipText();
        if (tipText == null) {
            return new Dimension(0, 0);
        }
        // Falls der Text nicht bereits vollständig strukturiert ist,
        // wickle ihn in <html><head></head><body> ... </body></html> ein.
        if (!tipText.trim().toLowerCase().startsWith("<html>")) {
            tipText = "<html><head></head><body>" + makeLinksClickable(tipText) + "</body></html>";
        } else {
            // Falls schon <html> vorhanden ist, prüfen wir auf <body>:
            if (!tipText.toLowerCase().contains("<body>")) {
                // Füge <body> ein, falls nicht vorhanden
                tipText = tipText.replaceFirst("(?i)<html>", "<html><head></head><body>") + "</body></html>";
            }
        }
        editorPane.setText(tipText);
        System.out.println("tiptext: " + tipText);
        
        Dimension d = editorPane.getPreferredSize();
        d.width = Math.min(d.width, WIDTH * 2);
        d.height++;
        editorPane.setSize(d);
        return d;
    }


    // Diese Methode sucht nach Mustern wie http:// oder https:// und verwandelt sie in klickbare Links
    private String makeLinksClickable(String text) {
        // Ersetze z.B. <http://irgendwas> durch <a href="http://irgendwas">http://irgendwas</a>
        // Falls du Backslashes verwendest, passe den Regex an.
        text = text.replaceAll("(?i)(<https?\\\\?:\\\\/\\\\/[^>]+>)", "<a href=\"$1\">$1</a>");

        // Oder wenn du text wie http://... ohne <> hast, kannst du:
        text = text.replaceAll("(https?://[\\w./#?=&-]+)", "<a href=\"$1\">$1</a>");
    
        return text;
        
        
    }
    
    private void checkForHyperlinkActivation(Point pt) {
        int pos = editorPane.viewToModel(pt);
        System.out.println("Document position: " + pos);
        if (pos >= 0) {
            javax.swing.text.Document doc = editorPane.getDocument();
            if (doc instanceof javax.swing.text.html.HTMLDocument) {
                javax.swing.text.html.HTMLDocument hdoc = (javax.swing.text.html.HTMLDocument) doc;
                javax.swing.text.Element elem = hdoc.getCharacterElement(pos);
                System.out.println("Element attributes: " + elem.getAttributes());
                // Prüfe, ob das Element ein Anker ist:
                Object anchor = elem.getAttributes().getAttribute(javax.swing.text.html.HTML.Tag.A);
                String href = (String) elem.getAttributes().getAttribute(javax.swing.text.html.HTML.Attribute.HREF);
                if (anchor != null || href != null) {
                    System.out.println("act: hyperlink found, href = " + href);
                    if (href != null) {
                        openWebpage(href);
                    }
                } else {
                    System.out.println("Kein Hyperlink an dieser Position gefunden.");
                }
            }
        }
    }

    
    
    private void dispatchEventToEditor(MouseEvent e) {
        // Berechne den Gesamtversatz: offset aus paint (1,1) plus Margin (hier 5,5)
        int offsetX = 1 + editorPane.getMargin().left; // z.B. 1 + 5 = 6
        int offsetY = 1 + editorPane.getMargin().top;  // z.B. 1 + 5 = 6

        // Ursprüngliche Koordinaten aus dem Tooltip
        Point pt = new Point(e.getX(), e.getY());
        // Passe die Koordinaten an, damit sie im editorPane stimmen
        pt.translate(-offsetX, -offsetY);

        // Debug-Ausgabe nur für Klick-Ereignisse
        if (e.getID() == MouseEvent.MOUSE_CLICKED) {
            System.out.println("Dispatching click event at: " + pt);
        } else {
            // Falls du nur Klicks protokollieren möchtest, kannst du hier den Debug-Ausdruck weglassen
            // System.out.println("Dispatching event at: " + pt);
        }

        // Erzeuge ein neues MouseEvent mit den korrigierten Koordinaten
        MouseEvent newEvent = new MouseEvent(editorPane, e.getID(), e.getWhen(),
                                               e.getModifiersEx(), pt.x, pt.y,
                                               e.getClickCount(), e.isPopupTrigger(), e.getButton());
        editorPane.dispatchEvent(newEvent);
        
        // Für Klick-Ereignisse kannst du zusätzlich manuell prüfen:
        if (e.getID() == MouseEvent.MOUSE_CLICKED) {
            checkForHyperlinkActivation(pt);
        }
    }


    
    
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);

        // Temporären Container erstellen, um das Layout des editorPane aufzubauen
        // Dieser Container wird nicht dem sichtbaren UI hinzugefügt
        javax.swing.JPanel tempPanel = new javax.swing.JPanel();
        tempPanel.add(editorPane);
        // Hierdurch wird das Layout des editorPane initialisiert.
        tempPanel.doLayout();
        editorPane.validate();

        // Weiterleitung aller relevanten Mausereignisse:
        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dispatchEventToEditor(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                dispatchEventToEditor(e);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                dispatchEventToEditor(e);
            }
        });

        c.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                dispatchEventToEditor(e);
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                dispatchEventToEditor(e);
            }
        });
    }

}
    

*/






/* old
package edu.mit.blocks.codeblockutil;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JToolTip;
import javax.swing.plaf.basic.BasicToolTipUI;

public class CToolTip extends JToolTip {

    private static final long serialVersionUID = 328149080249L;
    private Color background = new Color(255, 255, 150);

    private CToolTip() {
        this(new Color(55, 255, 225));
    }

    public CToolTip(Color background) {
        this.background = background;
        updateUI();
    }

    public void updateUI() {
        setUI(new CToolTipUI(background));
    }
}

class CToolTipUI extends BasicToolTipUI {

    private static final int WIDTH = 150;
    private CellRendererPane renderer;
    private JTextArea textArea;
    private Color background;

    public CToolTipUI(Color background) {
        super();
        this.background = Color.WHITE;
        renderer = new CellRendererPane();
        textArea = new JTextArea();
//        textArea.setMargin(new Insets(0, 3, 0, 0));
        textArea.setMargin(new Insets(5, 5, 5, 5));
        renderer.removeAll();
        renderer.add(textArea);
//        textArea.setFont(new Font("Ariel", Font.PLAIN, 11));
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }

    public void paint(Graphics g, JComponent c) {
        Dimension size = c.getSize();
        textArea.setBackground(background);
        renderer.paintComponent(g, textArea, c, 1, 1,
                size.width - 1, size.height - 1, true);
    }

    public Dimension getPreferredSize(JComponent c) {
        String tipText = ((JToolTip) c).getTipText();
        if (tipText == null) {
            return new Dimension(0, 0);
        }
        textArea.setText(tipText);
        Dimension d = textArea.getPreferredSize();
        //d.width = Math.min(d.width, WIDTH);
        d.width = 2*Math.min(d.width, WIDTH);
        d.height++;
        textArea.setSize(d);
        return d;
        //return textArea.getPreferredSize();
    }
}
*/
