package it.unisa.javaclienttorcs;

/**
 * Wrapper per SimpleDriver che aggiunge la raccolta dati avanzata.
 * Questa classe estende la funzionalità di SimpleDriver per generare
 * sia enhanced_dataset.csv che human_dataset.csv durante la guida automatica.
 */
public class SimpleDriverWithCollection extends Controller {
    
    private final SimpleDriver simpleDriver;
    private final EnhancedDataCollectionManager dataManager;
    
    public SimpleDriverWithCollection() {
        this("dataset.csv");
    }
    
    public SimpleDriverWithCollection(String datasetFilename) {
        this.simpleDriver = new SimpleDriver();
        this.dataManager = new EnhancedDataCollectionManager(datasetFilename);
        
        // Avvia automaticamente la raccolta dati
        dataManager.startEnhancedCollection();
        System.out.println("[INFO] SimpleDriverWithCollection: raccolta dati avviata con output: " + datasetFilename);
    }
    
    /**
     * Metodo di controllo principale che delega a SimpleDriver
     * e registra i dati per la raccolta.
     * 
     * @param sensors Modello sensoriale con tutti i dati attuali
     * @return Azione da inviare alla macchina
     */
    @Override
    public Action control(SensorModel sensors) {
        // Ottieni l'azione da SimpleDriver
        Action action = simpleDriver.control(sensors);
        
        // Registra i dati per la raccolta
        dataManager.recordData(sensors, action.accelerate, action);
        
        return action;
    }
    
    /**
     * Resetta lo stato del driver e continua la raccolta dati
     */
    @Override
    public void reset() {
        simpleDriver.reset();
        System.out.println("[INFO] SimpleDriverWithCollection: reset completato");
    }
    
    /**
     * Cleanup finale: ferma la raccolta dati e genera human_dataset.csv
     */
    @Override
    public void shutdown() {
        System.out.println("[INFO] SimpleDriverWithCollection: chiusura raccolta dati");
        
        // Ferma la raccolta e genera automaticamente human_dataset.csv
        dataManager.stopCollection();
        
        // Chiudi anche il SimpleDriver interno
        simpleDriver.shutdown();
        
        System.out.println("[INFO] SimpleDriverWithCollection: shutdown completato");
    }
    
    /**
     * Inizializza gli angoli dei sensori (delega a SimpleDriver)
     * @return 
     */
    @Override
    public float[] initAngles() {
        return simpleDriver.initAngles();
    }
}