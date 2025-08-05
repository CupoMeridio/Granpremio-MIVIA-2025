package it.unisa.javaclienttorcs;

/**
 * Classe di configurazione semplificata per il sistema KNN.
 * Mantiene solo il percorso del dataset come parametro configurabile,
 * con tutti gli altri parametri ottimali fissi.
 */
public class KNNConfig {
    
    // Parametri ottimali fissi (non configurabili)
    private static final int K = 5;
    private static final KNNDriver.DistanceMetric DISTANCE_METRIC = KNNDriver.DistanceMetric.EUCLIDEAN;
    private static final boolean NORMALIZE_DATA = true;
    private static final boolean USE_WEIGHTED_VOTING = true;
    private static final double MIN_WEIGHT = 1e-10;
    // Rimosso il limite sui punti di training - ora carica tutti i dati disponibili
    private static final boolean ENABLE_LOGGING = true;
    
    // Unico parametro configurabile
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
    
    // Getters per i parametri fissi
    public int getK() {
        return K;
    }
    
    public KNNDriver.DistanceMetric getDistanceMetric() {
        return DISTANCE_METRIC;
    }
    
    public boolean isNormalizeData() {
        return NORMALIZE_DATA;
    }
    
    public boolean isUseWeightedVoting() {
        return USE_WEIGHTED_VOTING;
    }
    
    public double getMinWeight() {
        return MIN_WEIGHT;
    }
    

    
    public boolean isEnableLogging() {
        return ENABLE_LOGGING;
    }
    
    // Getter per il parametro configurabile
    public String getDatasetPath() {
        return datasetPath;
    }
    
    // Setter per il parametro configurabile
    public void setDatasetPath(String datasetPath) {
        if (datasetPath == null || datasetPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Il percorso del dataset non può essere vuoto");
        }
        this.datasetPath = datasetPath;
    }
    
    /**
     * Metodi factory per configurazioni predefinite
     */
    public static KNNConfig forAutoDataset() {
        return new KNNConfig("auto_dataset.csv");
    }
    
    public static KNNConfig forHumanDataset() {
        return new KNNConfig("human_dataset.csv");
    }
    
    /**
     * Valida la configurazione
     */
    public void validate() {
        if (datasetPath == null || datasetPath.trim().isEmpty()) {
            throw new IllegalStateException("Il percorso del dataset non può essere vuoto");
        }
    }
    
    @Override
    public String toString() {
        return String.format("KNNConfig{datasetPath='%s', k=%d, distanceMetric=%s, normalizeData=%s, useWeightedVoting=%s}",
                datasetPath, K, DISTANCE_METRIC, NORMALIZE_DATA, USE_WEIGHTED_VOTING);
    }
}