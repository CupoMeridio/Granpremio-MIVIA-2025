package it.unisa.javaclienttorcs;

import java.io.IOException;

/**
 * EnhancedDataCollectionManager - Gestore principale per la raccolta dati avanzata
 */
public class EnhancedDataCollectionManager {
    
    private final EnhancedDataCollector enhancedCollector;
    private final String enhancedFilePath;
    private final String humanFilePath;
    
    public EnhancedDataCollectionManager() {
        this.enhancedCollector = new EnhancedDataCollector();
        this.enhancedFilePath = "enhanced_dataset.csv";
        this.humanFilePath = "human_dataset.csv";
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
            System.out.println("[INFO] Conversione in formato human_dataset.csv...");
            DatasetConverter.convertToHumanDataset(enhancedFilePath, humanFilePath);
            System.out.println("[SUCCESS] Processo completato!");
            System.out.println("[INFO] File creati:");
            System.out.println("  - " + enhancedFilePath + " (tutti i sensori e azioni)");
            System.out.println("  - " + humanFilePath + " (formato standard per behavioral cloning)");
        } catch (IOException e) {
            System.err.println("[ERROR] Errore durante conversione: " + e.getMessage());
        }
    }
    
    /**
     * Metodo per integrazione con HumanController
     * @param sensors
     * @param targetSpeed
     * @param action
     */
    public void recordData(SensorModel sensors, double targetSpeed, Action action) {
        enhancedCollector.recordData(sensors, action, targetSpeed);
    }
    
    /**
     * Ferma la raccolta dati
     */
    public void stopCollection() {
        enhancedCollector.stopCollection();
    }

    /**
     * Controlla se la raccolta è attiva
     * @return 
     */
    public boolean isCollecting() {
        return enhancedCollector.isCollecting();
    }
    

}