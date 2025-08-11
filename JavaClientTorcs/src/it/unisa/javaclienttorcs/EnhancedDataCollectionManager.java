package it.unisa.javaclienttorcs;

import java.io.File;
import java.io.IOException;

/**
 * EnhancedDataCollectionManager - Gestore principale per la raccolta dati avanzata
 */
public class EnhancedDataCollectionManager {
    
    private final EnhancedDataCollector enhancedCollector;
    private final String enhancedFilePath;
    private final String outputDatasetFile;
    
    /**
     * Costruttore di default che utilizza "human_dataset.csv" come file di output.
     */
    public EnhancedDataCollectionManager() {
        this("human_dataset.csv");
    }
    
    /**
     * Costruttore che permette di specificare il nome del file di output.
     * 
     * @param outputFilename Nome del file CSV di output per il dataset standard
     */
    public EnhancedDataCollectionManager(String outputFilename) {
        this.enhancedCollector = new EnhancedDataCollector();
        this.enhancedFilePath = "enhanced_dataset.csv";
        this.outputDatasetFile = outputFilename;
    }
    
    /**
     * Avvia la raccolta dati enhanced
     */
    public void startEnhancedCollection() {
        try {
            System.out.println("[INFO] Avvio raccolta dati enhanced...");
            enhancedCollector.startCollection(enhancedFilePath);
            System.out.println("[INFO] Raccolta avviata. Premi 'C' per fermare e convertire.");
        } catch (IOException ex) {
            System.getLogger(EnhancedDataCollectionManager.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
    
    /**
     * Ferma la raccolta e converte i dati
     */
    public void stopAndConvert() {
        System.out.println("[INFO] Fermatura raccolta dati...");
        enhancedCollector.stopCollection();
        
        try {
            System.out.println("[INFO] Conversione in formato " + outputDatasetFile + "...");
            DatasetConverter.convertToStandardDataset(enhancedFilePath, outputDatasetFile);
            System.out.println("[SUCCESS] Processo completato!");
            System.out.println("[INFO] File creati:");
            System.out.println("  - " + enhancedFilePath + " (tutti i sensori e azioni)");
            System.out.println("  - " + outputDatasetFile + " (formato CSV standard per analisi dati)");
        } catch (IOException e) {
            System.err.println("[ERROR] Errore durante conversione: " + e.getMessage());
        }
    }
    
    /**
     * Metodo per integrazione con HumanController per registrare i dati di guida.
     * 
     * @param sensors Modello sensoriale contenente lo stato attuale del veicolo
     * @param targetSpeed Velocità target desiderata
     * @param action Azione di controllo eseguita
     */
    public void recordData(SensorModel sensors, double targetSpeed, Action action) {
        enhancedCollector.recordData(sensors, action, targetSpeed);
    }
    
    /**
     * Ferma la raccolta dati e genera automaticamente human_dataset.csv
     */
    public void stopCollection() {
        enhancedCollector.stopCollection();
        
        // Genera automaticamente il file dataset
        try {
            System.out.println("[INFO] Generazione automatica di " + outputDatasetFile + "...");
            DatasetConverter.convertToStandardDataset(enhancedFilePath, outputDatasetFile);
            System.out.println("[SUCCESS] " + outputDatasetFile + " generato automaticamente!");
            
            // Verifica che il file sia stato creato
            File file = new File(outputDatasetFile);
            if (file.exists()) {
                System.out.println("[INFO] File creato: " + file.getAbsolutePath() + " (" + file.length() + " bytes)");
            } else {
                System.err.println("[ERROR] File non trovato: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Errore durante generazione automatica di " + outputDatasetFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Controlla se la raccolta dati è attualmente attiva.
     * 
     * @return true se la raccolta è in corso, false altrimenti
     */
    public boolean isCollecting() {
        return enhancedCollector.isCollecting();
    }
    

}