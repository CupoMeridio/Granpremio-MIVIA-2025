package it.unisa.javaclienttorcs;

/**
 * Classe per rappresentare un punto dati per behavioral cloning
 * Contiene sensori track + feature aggiuntive + azioni target
 */
public class DataPoint {
    public double[] trackSensors;      // 19 valori track
    public double speedX;              // velocità attuale
    public double angleToTrackAxis;    // angolo rispetto alla pista
    public double trackPosition;       // posizione sulla pista (-1 a 1)
    public double targetSpeed;         // velocità target (azione)
    public double steer;               // sterzata (azione)

    public DataPoint(double[] trackSensors, double speedX, double angleToTrackAxis, 
                    double trackPosition, double targetSpeed, double steer) {
        this.trackSensors = trackSensors.clone();
        this.speedX = speedX;
        this.angleToTrackAxis = angleToTrackAxis;
        this.trackPosition = trackPosition;
        this.targetSpeed = targetSpeed;
        this.steer = steer;
    }

    /**
     * Converte il punto dati in formato CSV
     * @return 
     */
    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        for (double sensor : trackSensors) {
            sb.append(sensor).append(",");
        }
        sb.append(speedX).append(",")
          .append(angleToTrackAxis).append(",")
          .append(trackPosition).append(",")
          .append(targetSpeed).append(",")
          .append(steer);
        return sb.toString();
    }

    /**
     * Carica punto dati da riga CSV
     * @param csv
     * @return 
     */
    public static DataPoint fromCSV(String csv) {
        String[] parts = csv.split(",");
        if (parts.length < 24) {
            throw new IllegalArgumentException("Formato CSV non valido per DataPoint");
        }

        double[] track = new double[19];
        for (int i = 0; i < 19; i++) {
            track[i] = Double.parseDouble(parts[i]);
        }

        double speedX = Double.parseDouble(parts[19]);
        double angleToTrackAxis = Double.parseDouble(parts[20]);
        double trackPosition = Double.parseDouble(parts[21]);
        double targetSpeed = Double.parseDouble(parts[22]);
        double steer = Double.parseDouble(parts[23]);

        return new DataPoint(track, speedX, angleToTrackAxis, trackPosition, targetSpeed, steer);
    }

    /**
     * Calcola distanza euclidea da un altro punto
     * @param other
     * @return 
     */
    public double distanceTo(DataPoint other) {
        double distance = 0;
        
        // Distanza sui sensori track
        for (int i = 0; i < trackSensors.length; i++) {
            double diff = trackSensors[i] - other.trackSensors[i];
            distance += diff * diff;
        }
        
        // Distanza su altre feature
        distance += Math.pow(speedX - other.speedX, 2);
        distance += Math.pow(angleToTrackAxis - other.angleToTrackAxis, 2);
        distance += Math.pow(trackPosition - other.trackPosition, 2);
        
        return Math.sqrt(distance);
    }
}