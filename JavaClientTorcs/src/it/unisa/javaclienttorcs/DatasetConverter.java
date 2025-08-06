package it.unisa.javaclienttorcs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * DatasetConverter - Utility per convertire dataset enhanced in formato standard
 */
public class DatasetConverter {
    
    /**
     * Converte un file CSV enhanced nel formato dataset standard
     * @param enhancedFile
     * @param standardFile
     * @throws java.io.IOException
     */
    public static void convertToStandardDataset(String enhancedFile, String standardFile) throws IOException {
        // Ottimizzazione I/O: Streaming invece di caricare tutto in memoria
        int bufferSize = calculateOptimalBufferSize(new File(enhancedFile));
        int processedLines = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(enhancedFile), bufferSize);
             BufferedWriter writer = new BufferedWriter(new FileWriter(standardFile), bufferSize)) {
            
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    if (firstLine && line.contains("track0")) {
                        // Scrivi header ottimizzato
                        writer.write(getOptimizedHeader());
                        writer.newLine();
                        firstLine = false;
                        continue;
                    }
                    
                    // Elabora e scrivi immediatamente (streaming)
                    String convertedLine = convertLineOptimized(line);
                    if (convertedLine != null) {
                        writer.write(convertedLine);
                        writer.newLine();
                        processedLines++;
                    }
                    firstLine = false;
                }
            }
        }
        
        System.out.println("[INFO] Convertito " + processedLines + " punti in formato dataset standard (streaming I/O)");
    }
    
    /**
     * Calcola buffer size ottimale basato sulla dimensione del file
     * Utilizza configurazione centralizzata IOConfig
     */
    private static int calculateOptimalBufferSize(File file) {
        return IOConfig.getOptimalBufferSize(file.length());
    }
    
    /**
     * Header ottimizzato pre-costruito
     * Sensori selezionati per modello di guida autonoma:
     * - Track sensors alternati (0,2,4,6,8,10,12,14,16,18) per copertura ottimale
     * - Sensori di posizione e velocità essenziali
     * - Azioni di controllo principali
     */
    private static String getOptimizedHeader() {
        // Pre-costruito per evitare StringBuilder ripetuti
        return "track0,track2,track4,track6,track8,track10,track12,track14,track16,track18,speedX,angleToTrackAxis,trackPosition,distanceFromStartLine,steering,acceleration,brake";
    }
    
    /**
     * Conversione ottimizzata di singola riga
     * Estrae solo i sensori selezionati per il modello di guida autonoma
     */
    private static String convertLineOptimized(String enhancedLine) {
        String[] parts = enhancedLine.split(",");
        if (parts.length < 45) return null; // Riga malformata
        
        // Usa StringBuilder con capacità pre-allocata (ridotta per meno campi)
        StringBuilder result = new StringBuilder(200);
        
        // Track sensors alternati (0,2,4,6,8,10,12,14,16,18) per copertura ottimale
        result.append(parts[0]).append(",")   // track0
              .append(parts[2]).append(",")   // track2
              .append(parts[4]).append(",")   // track4
              .append(parts[6]).append(",")   // track6
              .append(parts[8]).append(",")   // track8
              .append(parts[10]).append(",")  // track10
              .append(parts[12]).append(",")  // track12
              .append(parts[14]).append(",")  // track14
              .append(parts[16]).append(",")  // track16
              .append(parts[18]).append(","); // track18
        
        // Sensori di posizione e velocità essenziali
        result.append(parts[19]).append(",")  // speedX
              .append(parts[20]).append(",")  // angleToTrackAxis
              .append(parts[21]).append(",")  // trackPosition
              .append(parts[25]).append(","); // distanceFromStartLine
        
        // Azioni di controllo principali
        result.append(parts[42]).append(",")  // steering
              .append(parts[43]).append(",")  // acceleration
              .append(parts[44]);             // brake
        
        return result.toString();
    }
    

    
    /**
     * Metodo main per testing standalone
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java DatasetConverter <enhanced_file.csv> <standard_file.csv>");
            return;
        }
        
        try {
            convertToStandardDataset(args[0], args[1]);
            System.out.println("[SUCCESS] Conversione completata!");
        } catch (IOException e) {
            System.err.println("[ERROR] Errore durante la conversione: " + e.getMessage());
        }
    }
}