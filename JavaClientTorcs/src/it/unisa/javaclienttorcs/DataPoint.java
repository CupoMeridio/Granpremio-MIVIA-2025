package it.unisa.javaclienttorcs;

/**
 * Rappresenta un punto dati per l'algoritmo KNN.
 * Contiene le features (sensori) e le azioni target (steering, acceleration, brake).
 */
public class DataPoint {
    /** Array delle features (sensori) del punto dati */
    public double[] features;
    
    /** Valore di steering (-1.0 a 1.0) */
    public double steering;
    
    /** Valore di accelerazione (0.0 a 1.0) */
    public double acceleration;
    
    /** Valore di frenata (0.0 a 1.0) */
    public double brake;
    
    /** Distanza calcolata durante la ricerca KNN */
    public double distance;
    
    /**
     * Costruttore per un punto dati
     * @param features Array delle features (sensori)
     * @param steering Valore di steering (-1.0 a 1.0)
     * @param acceleration Valore di accelerazione (0.0 a 1.0)
     * @param brake Valore di frenata (0.0 a 1.0)
     */
    public DataPoint(double[] features, double steering, double acceleration, double brake) {
        this.features = features.clone(); // Copia difensiva
        this.steering = steering;
        this.acceleration = acceleration;
        this.brake = brake;
        this.distance = 0.0;
    }
    
    /**
     * Costruttore di copia
     * @param other Punto dati da copiare
     */
    public DataPoint(DataPoint other) {
        this.features = other.features.clone();
        this.steering = other.steering;
        this.acceleration = other.acceleration;
        this.brake = other.brake;
        this.distance = other.distance;
    }
    
    /**
     * Calcola la distanza Euclidea tra questo punto e un altro set di features
     * @param otherFeatures Le features dell'altro punto
     * @return La distanza Euclidea
     * @throws IllegalArgumentException se le dimensioni delle features non sono uguali
     */
    public double euclideanDistance(double[] otherFeatures) {
        if (features.length != otherFeatures.length) {
            throw new IllegalArgumentException("Le dimensioni delle features devono essere uguali");
        }
        
        double sum = 0.0;
        for (int i = 0; i < features.length; i++) {
            double diff = features[i] - otherFeatures[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
    

    
    /**
     * Calcola la distanza euclidea tra questo punto e un altro set di features
     * @param otherFeatures Le features dell'altro punto
     * @return La distanza euclidea calcolata
     */
    public double calculateDistance(double[] otherFeatures) {
        return euclideanDistance(otherFeatures);
    }
    
    /**
     * Imposta la distanza calcolata
     * @param distance La distanza
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    /**
     * Restituisce una rappresentazione stringa del punto dati
     * @return Una stringa che rappresenta il punto dati con features, steering, acceleration, brake e distance
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DataPoint{features=[");
        for (int i = 0; i < features.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(String.format("%.3f", features[i]));
        }
        sb.append(String.format("], steering=%.3f, acceleration=%.3f, brake=%.3f, distance=%.3f}", 
                steering, acceleration, brake, distance));
        return sb.toString();
    }
    
    /**
     * Verifica l'uguaglianza tra due punti dati
     * @param obj L'oggetto da confrontare
     * @return true se i punti dati sono uguali, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        DataPoint dataPoint = (DataPoint) obj;
        
        if (Double.compare(dataPoint.steering, steering) != 0) return false;
        if (Double.compare(dataPoint.acceleration, acceleration) != 0) return false;
        if (Double.compare(dataPoint.brake, brake) != 0) return false;
        
        return java.util.Arrays.equals(features, dataPoint.features);
    }
    
    /**
     * Calcola l'hash code del punto dati
     * @return Il valore hash code del punto dati
     */
    @Override
    public int hashCode() {
        int result = java.util.Arrays.hashCode(features);
        long temp;
        temp = Double.doubleToLongBits(steering);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(acceleration);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(brake);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}