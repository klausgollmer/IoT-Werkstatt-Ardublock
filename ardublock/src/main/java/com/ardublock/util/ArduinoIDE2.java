package com.ardublock.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ardublock.core.Context;

public class ArduinoIDE2 {

	public static void writeFile(String fullPath, String content) {
        Path path = Paths.get(fullPath);
        try {
            // Erstellen Sie das Verzeichnis, falls es nicht existiert
            if (Files.notExists(path.getParent())) {
                Files.createDirectories(path.getParent());
                System.out.println("Directory created: " + path.getParent().toString());
            }
            
            // Erstellen Sie die Datei, falls sie nicht existiert
            if (Files.notExists(path)) {
                Files.createFile(path);
                System.out.println("File created: " + path.toString());
            }
            
            // Schreiben Sie den Inhalt in die Datei
            Files.write(path, content.getBytes());
            System.out.println("File written successfully!");
        } catch (IOException e) {
            System.err.println("An error occurred while writing the file: " + e.getMessage());
        }
    }
}