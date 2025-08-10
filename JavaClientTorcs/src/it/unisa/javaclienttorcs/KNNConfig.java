package it.unisa.javaclienttorcs;

/**
 * Classe di configurazione semplificata per il sistema KNN.
 * Mantiene solo il percorso del dataset come parametro configurabile,
 * con tutti gli altri parametri ottimali fissi.
 */
public class KNNConfig {
    

    private static final int K = 5;
    private static final boolean NORMALIZE_DATA = true;
    private static final boolean USE_WEIGHTED_VOTING = true;
    private static final boolean ENABLE_LOGGING = false;
    
    private String datasetPath;
    
    /**
     * Costruttore che accetta solo il percorso del dataset
     * @param datasetPath Percorso del file dataset CSV
     */
    public KNNConfig(String datasetPath) {
        if (datasetPath == null || datasetPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso del dataset non può essere vuoto");
        }
        this.datasetPath = datasetPath;
    }
    
    /**
     * Restituisce il valore K per l'algoritmo KNN.
     * 
     * @return Numero di vicini più prossimi da considerare (fisso a 5)
     */
    public int getK() {
        return K;
    }
    
    /**
     * Verifica se la normalizzazione dei dati è abilitata.
     * 
     * @return true se la normalizzazione è abilitata (sempre true)
     */
    public boolean isNormalizeData() {
        return NORMALIZE_DATA;
    }
    
    /**
     * Verifica se il voto pesato è abilitato per le predizioni.
     * 
     * @return true se il voto pesato è abilitato (sempre true)
     */
    public boolean isUseWeightedVoting() {
        return USE_WEIGHTED_VOTING;
    }
    
    /**
     * Verifica se il logging dettagliato è abilitato.
     * 
     * @return true se il logging è abilitato (sempre false)
     */
    public boolean isEnableLogging() {
        return ENABLE_LOGGING;
    }
    
    /**
     * Restituisce il percorso del file dataset configurato.
     * 
     * @return Percorso del file dataset CSV
     */
    public String getDatasetPath() {
        return datasetPath;
    }
    
    /**
     * Imposta il percorso del file dataset.
     * 
     * @param datasetPath Nuovo percorso del file dataset CSV
     * @throws IllegalArgumentException Se il percorso è null o vuoto
     */
    public void setDatasetPath(String datasetPath) {
        if (datasetPath == null || datasetPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso del dataset non può essere vuoto");
        }
        this.datasetPath = datasetPath;
    }
    
    /**
     * Crea una configurazione KNN per il dataset automatico.
     * Utilizza il file "auto_dataset.csv" come dataset di training.
     * 
     * @return Configurazione KNN per dataset automatico
     */
    public static KNNConfig forAutoDataset() {
        return new KNNConfig("auto_dataset.csv");
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
     * Restituisce una rappresentazione testuale della configurazione KNN.
     * Include il percorso del dataset e tutti i parametri di configurazione.
     * 
     * @return Stringa formattata con i dettagli della configurazione
     */
    @Override
    public String toString() {
        return String.format("KNNConfig{datasetPath='%s', k=%d, normalizeData=%s, useWeightedVoting=%s}",
                datasetPath, K, NORMALIZE_DATA, USE_WEIGHTED_VOTING);
    }
}