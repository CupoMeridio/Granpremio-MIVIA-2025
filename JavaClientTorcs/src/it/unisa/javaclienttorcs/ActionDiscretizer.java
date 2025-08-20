package it.unisa.javaclienttorcs;

import java.io.*;
import java.util.*;

/**
 * Utility per discretizzare azioni continue in classi discrete.
 * Converte un dataset con azioni continue (steering, acceleration, brake)
 * in un dataset con classi di azioni discrete per il training del classificatore.
 */
public class ActionDiscretizer {
    
    /**
     * Converte un dataset con azioni continue in un dataset con classi discrete.
     * 
     * @param inputPath Percorso del dataset originale con azioni continue
     * @param outputPath Percorso del dataset di output con classi discrete
     * @throws IOException Se si verifica un errore durante la lettura/scrittura
     */
    public static void discretizeDataset(String inputPath, String outputPath) throws IOException {
        Map<DrivingAction, Integer> actionCounts = new HashMap<>();
        for (DrivingAction action : DrivingAction.values()) {
            actionCounts.put(action, 0);
        }
        
        int totalSamples = 0;
        int validSamples = 0;
        
        System.out.println("[DISCRETIZER] Inizio discretizzazione dataset: " + inputPath);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath));
             PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            
            // Leggi e scrivi header
            String headerLine = reader.readLine();
            if (headerLine != null) {
                // Modifica header per includere la classe di azione
                String[] headers = headerLine.split(",");
                StringBuilder newHeader = new StringBuilder();
                
                // Mantieni le features (sensori)
                for (int i = 0; i < 14; i++) {
                    if (i > 0) newHeader.append(",");
                    newHeader.append(headers[i]);
                }
                
                // Aggiungi colonna per la classe di azione
                newHeader.append(",actionClass");
                
                // Mantieni le azioni originali per riferimento
                newHeader.append(",steering,acceleration,brake");
                
                writer.println(newHeader.toString());
            }
            
            String line;
            while ((line = reader.readLine()) != null) {
                totalSamples++;
                String[] values = line.split(",");
                
                try {
                    // Estrai azioni continue
                    double steering = Double.parseDouble(values[14]);
                    double acceleration = Double.parseDouble(values[15]);
                    double brake = Double.parseDouble(values[16]);
                    
                    // Converti in classe discreta
                    DrivingAction actionClass = DrivingAction.fromContinuous(steering, acceleration, brake);
                    actionCounts.put(actionClass, actionCounts.get(actionClass) + 1);
                    
                    // Scrivi riga con classe discreta
                    StringBuilder newLine = new StringBuilder();
                    
                    // Features (sensori)
                    for (int i = 0; i < 14; i++) {
                        if (i > 0) newLine.append(",");
                        newLine.append(values[i]);
                    }
                    
                    // Classe di azione
                    newLine.append(",").append(actionClass.name());
                    
                    // Azioni originali per riferimento
                    newLine.append(",").append(steering);
                    newLine.append(",").append(acceleration);
                    newLine.append(",").append(brake);
                    
                    writer.println(newLine.toString());
                    validSamples++;
                    
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("[DISCRETIZER] Errore parsing riga " + totalSamples + ": " + e.getMessage());
                }
            }
        }
        
        System.out.printf("[DISCRETIZER] Discretizzazione completata: %d/%d campioni validi%n", validSamples, totalSamples);
        System.out.println("[DISCRETIZER] Dataset discretizzato salvato in: " + outputPath);
        
        // Stampa distribuzione delle classi
        printClassDistribution(actionCounts, validSamples);
    }
    
    /**
     * Stampa la distribuzione delle classi nel dataset discretizzato.
     * 
     * @param actionCounts Mappa con il conteggio per ogni classe
     * @param totalSamples Numero totale di campioni validi
     */
    private static void printClassDistribution(Map<DrivingAction, Integer> actionCounts, int totalSamples) {
        System.out.println("\n[DISCRETIZER] Distribuzione classi nel dataset discretizzato:");
        
        // Ordina per frequenza decrescente
        List<Map.Entry<DrivingAction, Integer>> sortedEntries = new ArrayList<>(actionCounts.entrySet());
        sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        for (Map.Entry<DrivingAction, Integer> entry : sortedEntries) {
            if (entry.getValue() > 0) {
                double percentage = (entry.getValue() * 100.0) / totalSamples;
                System.out.printf("[DISCRETIZER] - %-25s: %6d (%.1f%%)%n", 
                    entry.getKey().getDescription(), entry.getValue(), percentage);
            }
        }
        
        // Verifica bilanciamento
        analyzeClassBalance(actionCounts);
    }
    
    /**
     * Analizza il bilanciamento delle classi e fornisce suggerimenti.
     * 
     * @param actionCounts Mappa con il conteggio per ogni classe
     */
    private static void analyzeClassBalance(Map<DrivingAction, Integer> actionCounts) {
        System.out.println("\n[DISCRETIZER] Analisi bilanciamento classi:");
        
        int maxCount = actionCounts.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        int minCount = actionCounts.values().stream().filter(c -> c > 0).mapToInt(Integer::intValue).min().orElse(0);
        
        if (minCount > 0) {
            double imbalanceRatio = (double) maxCount / minCount;
            System.out.printf("[DISCRETIZER] - Rapporto sbilanciamento: %.2f (max/min)%n", imbalanceRatio);
            
            if (imbalanceRatio > 10) {
                System.out.println("[DISCRETIZER] - ATTENZIONE: Dataset molto sbilanciato!");
                System.out.println("[DISCRETIZER] - Suggerimento: Considera tecniche di bilanciamento o raccolta dati aggiuntivi");
            } else if (imbalanceRatio > 5) {
                System.out.println("[DISCRETIZER] - AVVISO: Dataset moderatamente sbilanciato");
            } else {
                System.out.println("[DISCRETIZER] - Dataset ragionevolmente bilanciato");
            }
        }
        
        // Conta classi vuote
        long emptyClasses = actionCounts.values().stream().filter(c -> c == 0).count();
        if (emptyClasses > 0) {
            System.out.printf("[DISCRETIZER] - ATTENZIONE: %d classi senza campioni%n", emptyClasses);
        }
    }
    
    /**
     * Analizza un dataset esistente e mostra statistiche sulle azioni continue.
     * 
     * @param datasetPath Percorso del dataset da analizzare
     * @throws IOException Se si verifica un errore durante la lettura
     */
    public static void analyzeDataset(String datasetPath) throws IOException {
        System.out.println("[DISCRETIZER] Analisi dataset: " + datasetPath);
        
        double minSteering = Double.MAX_VALUE, maxSteering = Double.MIN_VALUE;
        double minAcceleration = Double.MAX_VALUE, maxAcceleration = Double.MIN_VALUE;
        double minBrake = Double.MAX_VALUE, maxBrake = Double.MIN_VALUE;
        
        int totalSamples = 0;
        int validSamples = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(datasetPath))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                totalSamples++;
                String[] values = line.split(",");
                
                try {
                    double steering = Double.parseDouble(values[14]);
                    double acceleration = Double.parseDouble(values[15]);
                    double brake = Double.parseDouble(values[16]);
                    
                    minSteering = Math.min(minSteering, steering);
                    maxSteering = Math.max(maxSteering, steering);
                    minAcceleration = Math.min(minAcceleration, acceleration);
                    maxAcceleration = Math.max(maxAcceleration, acceleration);
                    minBrake = Math.min(minBrake, brake);
                    maxBrake = Math.max(maxBrake, brake);
                    
                    validSamples++;
                    
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    // Ignora righe malformate
                }
            }
        }
        
        System.out.printf("[DISCRETIZER] Campioni analizzati: %d/%d%n", validSamples, totalSamples);
        System.out.println("[DISCRETIZER] Range valori azioni:");
        System.out.printf("[DISCRETIZER] - Steering: [%.3f, %.3f]%n", minSteering, maxSteering);
        System.out.printf("[DISCRETIZER] - Acceleration: [%.3f, %.3f]%n", minAcceleration, maxAcceleration);
        System.out.printf("[DISCRETIZER] - Brake: [%.3f, %.3f]%n", minBrake, maxBrake);
    }
    
    /**
     * Metodo main per utilizzare il discretizzatore da riga di comando.
     * 
     * @param args Argomenti: [input_dataset] [output_dataset] [analyze_only]
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java ActionDiscretizer <input_dataset> [output_dataset] [analyze_only]");
            System.out.println("  input_dataset: Percorso del dataset originale");
            System.out.println("  output_dataset: Percorso del dataset discretizzato (opzionale)");
            System.out.println("  analyze_only: 'true' per solo analisi, 'false' per discretizzazione (default: false)");
            return;
        }
        
        String inputPath = args[0];
        String outputPath = args.length > 1 ? args[1] : inputPath.replace(".csv", "_discretized.csv");
        boolean analyzeOnly = args.length > 2 && "true".equalsIgnoreCase(args[2]);
        
        try {
            if (analyzeOnly) {
                analyzeDataset(inputPath);
            } else {
                analyzeDataset(inputPath);
                System.out.println();
                discretizeDataset(inputPath, outputPath);
            }
        } catch (IOException e) {
            System.err.println("[DISCRETIZER] Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }
}