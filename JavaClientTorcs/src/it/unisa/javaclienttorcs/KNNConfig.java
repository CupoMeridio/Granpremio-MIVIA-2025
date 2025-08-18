package it.unisa.javaclienttorcs;

/**
 * Configurazione per l'algoritmo KNN.
 * Contiene i parametri necessari per il funzionamento del driver KNN.
 * Supporta sia modalità regressiva che classificatoria.
 */
public class KNNConfig {
    
    // Numero di vicini da considerare nell'algoritmo KNN
    private int k = 8;
    
    // Percorso del file dataset
    private String datasetPath = "dataset.csv";
    
    // Flag per abilitare la normalizzazione dei dati
    private boolean normalizeData = true;
    
    // Modalità di funzionamento: true per classificatore, false per regressore
    private boolean classifierMode = false;
    
    /**
     * Costruttore di default.
     */
    public KNNConfig() {
        // Configurazione di default
    }
    
    /**
     * Costruttore con solo il percorso del dataset (per compatibilità).
     * 
     * @param datasetPath Percorso del dataset
     */
    public KNNConfig(String datasetPath) {
        this.datasetPath = datasetPath;
    }
    
    /**
     * Costruttore con parametri personalizzati.
     * 
     * @param k Numero di vicini
     * @param datasetPath Percorso del dataset
     * @param normalizeData Flag per la normalizzazione
     */
    public KNNConfig(int k, String datasetPath, boolean normalizeData) {
        this.k = k;
        this.datasetPath = datasetPath;
        this.normalizeData = normalizeData;
    }
    
    /**
     * Costruttore completo con modalità classificatore.
     * 
     * @param k Numero di vicini
     * @param datasetPath Percorso del dataset
     * @param normalizeData Flag per la normalizzazione
     * @param classifierMode Flag per modalità classificatore
     */
    public KNNConfig(int k, String datasetPath, boolean normalizeData, boolean classifierMode) {
        this.k = k;
        this.datasetPath = datasetPath;
        this.normalizeData = normalizeData;
        this.classifierMode = classifierMode;
    }
    
    /**
     * Restituisce il numero di vicini K.
     * 
     * @return Numero di vicini
     */
    public int getK() {
        return k;
    }
    
    /**
     * Imposta il numero di vicini K.
     * 
     * @param k Numero di vicini (deve essere > 0)
     */
    public void setK(int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("K deve essere maggiore di 0");
        }
        this.k = k;
    }
    
    /**
     * Restituisce il percorso del dataset.
     * 
     * @return Percorso del file dataset
     */
    public String getDatasetPath() {
        return datasetPath;
    }
    
    /**
     * Imposta il percorso del dataset.
     * 
     * @param datasetPath Percorso del file dataset
     */
    public void setDatasetPath(String datasetPath) {
        if (datasetPath == null || datasetPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso del dataset non può essere vuoto");
        }
        this.datasetPath = datasetPath;
    }
    
    /**
     * Verifica se la normalizzazione dei dati è abilitata.
     * 
     * @return true se la normalizzazione è abilitata
     */
    public boolean isNormalizeData() {
        return normalizeData;
    }
    
    /**
     * Abilita o disabilita la normalizzazione dei dati.
     * 
     * @param normalizeData Flag per la normalizzazione
     */
    public void setNormalizeData(boolean normalizeData) {
        this.normalizeData = normalizeData;
    }
    
    /**
     * Verifica se è abilitata la modalità classificatore.
     * 
     * @return true se è in modalità classificatore, false per regressore
     */
    public boolean isClassifierMode() {
        return classifierMode;
    }
    
    /**
     * Imposta la modalità di funzionamento.
     * 
     * @param classifierMode true per classificatore, false per regressore
     */
    public void setClassifierMode(boolean classifierMode) {
        this.classifierMode = classifierMode;
    }
    
    /**
     * Verifica se il logging è abilitato (per compatibilità).
     * 
     * @return false (logging disabilitato di default)
     */
    public boolean isEnableLogging() {
        return false; // Logging disabilitato per performance
    }
    
    /**
     * Verifica se il voto pesato è abilitato (per compatibilità).
     * 
     * @return true (voto pesato sempre abilitato)
     */
    public boolean isUseWeightedVoting() {
        return true; // Voto pesato sempre abilitato
    }
    

    
    /**
     * Crea una configurazione KNN per il dataset umano.
     * Utilizza il file "human_dataset.csv" come dataset di training.
     * 
     * @return Configurazione KNN per dataset umano
     */
    public static KNNConfig forHumanDataset() {
        return new KNNConfig("human_dataset.csv");
    }
    
    /**
     * Valida la configurazione corrente.
     * Verifica che il percorso del dataset sia valido e non vuoto.
     * 
     * @throws IllegalStateException Se il percorso del dataset è null o vuoto
     */
    public void validate() {
        if (datasetPath == null || datasetPath.trim().isEmpty()) {
            throw new IllegalStateException("Il percorso del dataset non può essere vuoto");
        }
    }
    
    /**
     * Restituisce una rappresentazione testuale della configurazione.
     * 
     * @return Stringa con i parametri di configurazione
     */
    @Override
    public String toString() {
        return String.format("KNNConfig{k=%d, datasetPath='%s', normalizeData=%s, classifierMode=%s}", 
                           k, datasetPath, normalizeData, classifierMode);
    }
}