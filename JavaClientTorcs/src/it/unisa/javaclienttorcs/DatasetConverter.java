package it.unisa.javaclienttorcs;

import java.io.*;
import java.util.*;

/**
 * DatasetConverter - Utility per convertire dataset enhanced in formato standard
 */
public class DatasetConverter {
    
    /**
     * Converte un file CSV enhanced nel formato human_dataset.csv standard
     * @param enhancedFile
     * @param humanFile
     * @throws java.io.IOException
     */
    public static void convertToHumanDataset(String enhancedFile, String humanFile) throws IOException {
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(enhancedFile))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    if (firstLine && line.contains("track0")) {
                        firstLine = false;
                        continue;
                    }
                    lines.add(line);
                    firstLine = false;
                }
            }
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(humanFile))) {
            // Header standard
            StringBuilder header = new StringBuilder();
            for (int i = 0; i < 19; i++) {
                header.append("track").append(i).append(",");
            }
            header.append("speedX,angleToTrackAxis,trackPosition,targetSpeed,steer");
            writer.println(header.toString());
            
            // Converti ogni riga
            for (String enhancedLine : lines) {
                String[] parts = enhancedLine.split(",");
                StringBuilder humanLine = new StringBuilder();
                
                // Prendi solo i campi necessari per human_dataset.csv
                // track0-track18 (posizioni 0-18)
                for (int i = 0; i < 19; i++) {
                    humanLine.append(parts[i]).append(",");
                }
                
                // speedX (posizione 19)
                humanLine.append(parts[19]).append(",");
                
                // angleToTrackAxis (posizione 20)
                humanLine.append(parts[20]).append(",");
                
                // trackPosition (posizione 21)
                humanLine.append(parts[21]).append(",");
                
                // targetSpeed (posizione 37 - ultima azione prima delle azioni)
                humanLine.append(parts[37]).append(",");
                
                // steer (posizione 38 - prima azione)
                humanLine.append(parts[38]);
                
                writer.println(humanLine.toString());
            }
        }
        
        System.out.println("[INFO] Convertito " + lines.size() + " punti in formato human_dataset.csv");
    }
    
    /**
     * Metodo main per testing standalone
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java DatasetConverter <enhanced_file.csv> <human_file.csv>");
            return;
        }
        
        try {
            convertToHumanDataset(args[0], args[1]);
            System.out.println("[SUCCESS] Conversione completata!");
        } catch (IOException e) {
            System.err.println("[ERROR] Errore durante la conversione: " + e.getMessage());
        }
    }
}