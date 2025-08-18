package it.unisa.javaclienttorcs;

/**
 * Rappresenta un punto dati per il classificatore KNN.
 * Contiene le features di input e la classe di azione discreta come output.
 */
public class DataPointClassifier {
    
    /** Features di input (sensori normalizzati) */
    public double[] features;
    
    /** Classe di azione discreta */
    public DrivingAction actionClass;
    
    /** Distanza calcolata durante la ricerca KNN */
    public double distance;
    
    /**
     * Costruttore per un punto dati del classificatore.
     * 
     * @param features Array delle features di input
     * @param actionClass Classe di azione discreta
     */
    public DataPointClassifier(double[] features, DrivingAction actionClass) {
        this.features = features.clone();
        this.actionClass = actionClass;
        this.distance = 0.0;
    }
    
    /**
     * Costruttore copia per creare una copia del punto dati.
     * 
     * @param other Punto dati da copiare
     */
    public DataPointClassifier(DataPointClassifier other) {
        this.features = other.features.clone();
        this.actionClass = other.actionClass;
        this.distance = other.distance;
    }
    
    /**
     * Imposta la distanza calcolata.
     * 
     * @param distance Distanza dal punto di query
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    /**
     * Restituisce la distanza calcolata.
     * 
     * @return Distanza dal punto di query
     */
    public double getDistance() {
        return distance;
    }
    
    /**
     * Restituisce le features di input.
     * 
     * @return Array delle features
     */
    public double[] getFeatures() {
        return features.clone();
    }
    
    /**
     * Restituisce la classe di azione.
     * 
     * @return Classe di azione discreta
     */
    public DrivingAction getActionClass() {
        return actionClass;
    }
    
    /**
     * Rappresentazione stringa del punto dati.
     * 
     * @return Stringa rappresentativa
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DataPointClassifier{")
          .append("actionClass=").append(actionClass)
          .append(", distance=").append(String.format("%.3f", distance))
          .append(", features=[")
          .append(String.format("%.2f", features[0]));
        
        for (int i = 1; i < Math.min(features.length, 5); i++) {
            sb.append(", ").append(String.format("%.2f", features[i]));
        }
        
        if (features.length > 5) {
            sb.append(", ...");
        }
        
        sb.append("]}");
        return sb.toString();
    }
}