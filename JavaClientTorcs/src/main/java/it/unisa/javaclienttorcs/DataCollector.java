package it.unisa.javaclienttorcs;

import java.io.*;
import java.util.*;

/**
 * Classe per raccogliere dati per behavioral cloning
 * Salva coppie osservazione-azione durante la guida manuale
 */
public class DataCollector {
    private List<DataPoint> dataset;
    private PrintWriter writer;
    private boolean collecting;
    private String filename;
    
    public DataCollector() {
        this("dataset.csv");
    }
    
    public DataCollector(String filename) {
        this.dataset = new ArrayList<>();
        this.collecting = false;
        this.filename = filename;
    }
    
    /**
     * Inizia la raccolta dati su file
     */
    public void startCollection(String filename) throws IOException {
        writer = new PrintWriter(new FileWriter(filename, true)); // append mode
        collecting = true;
        System.out.println("[INFO] Raccolta dati avviata su: " + filename);
    }
    
    /**
     * Registra una coppia osservazione-azione
     * Input: vettore track (19 valori) + velocità e posizione
     * Output: velocità target e sterzata
     */
    public void recordData(SensorModel sensors, double targetSpeed, double steer) {
        if (!collecting) return;
        
        // Estrai vettore track (19 sensori di distanza)
        double[] track = new double[19];
        for (int i = 0; i < 19; i++) {
            track[i] = sensors.getTrackEdgeSensors()[i];
        }
        
        // Estrai feature aggiuntive
        double speedX = sensors.getSpeed();
        double angleToTrackAxis = sensors.getAngleToTrackAxis();
        double trackPosition = sensors.getTrackPosition();
        
        // Crea punto dati
        DataPoint point = new DataPoint(track, speedX, angleToTrackAxis, trackPosition, targetSpeed, steer);
        dataset.add(point);
        
        // Salva su file CSV
        writer.println(point.toCSV());
        writer.flush();
    }
    
    /**
     * Ferma la raccolta dati
     */
    public void stopCollection() {
        if (writer != null) {
            writer.close();
        }
        collecting = false;
        System.out.println("[INFO] Raccolta dati terminata. Punti raccolti: " + dataset.size());
    }
    
    /**
     * Carica dataset esistente
     */
    public List<DataPoint> loadDataset(String filename) throws IOException {
        List<DataPoint> loaded = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                loaded.add(DataPoint.fromCSV(line));
            }
        }
        
        reader.close();
        System.out.println("[INFO] Dataset caricato: " + loaded.size() + " punti");
        return loaded;
    }
    
    public boolean isCollecting() {
        return collecting;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void close() {
        stopCollection();
    }
    
    /**
     * Stampa statistiche sul dataset raccolto
     */
    public void printDatasetStats() {
        System.out.println("=== STATISTICHE DATASET ===");
        System.out.println("File: " + filename);
        System.out.println("Punti raccolti: " + dataset.size());
        
        if (!dataset.isEmpty()) {
            double avgTargetSpeed = 0;
            double avgSteer = 0;
            
            for (DataPoint point : dataset) {
                avgTargetSpeed += point.targetSpeed;
                avgSteer += point.steer;
            }
            
            avgTargetSpeed /= dataset.size();
            avgSteer /= dataset.size();
            
            System.out.println("Velocità target media: " + String.format("%.2f", avgTargetSpeed));
            System.out.println("Sterzata media: " + String.format("%.3f", avgSteer));
        }
        System.out.println("============================");
    }
}

/**
 * Classe per rappresentare un punto dati
 */
class DataPoint {
    double[] trackSensors;      // 19 valori track
    double speedX;              // velocità attuale
    double angleToTrackAxis;    // angolo rispetto alla pista
    double trackPosition;       // posizione sulla pista (-1 a 1)
    double targetSpeed;         // velocità target (azione)
    double steer;               // sterzata (azione)
    
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
     * Converte in formato CSV
     */
    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        for (double sensor : trackSensors) {
            sb.append(sensor).append(",");
        }
        sb.append(speedX).append(",");
        sb.append(angleToTrackAxis).append(",");
        sb.append(trackPosition).append(",");
        sb.append(targetSpeed).append(",");
        sb.append(steer);
        return sb.toString();
    }
    
    /**
     * Carica da formato CSV
     */
    public static DataPoint fromCSV(String csv) {
        String[] parts = csv.split(",");
        double[] track = new double[19];
        
        for (int i = 0; i < 19; i++) {
            track[i] = Double.parseDouble(parts[i]);
        }
        
        double speedX = Double.parseDouble(parts[19]);
        double angle = Double.parseDouble(parts[20]);
        double position = Double.parseDouble(parts[21]);
        double targetSpeed = Double.parseDouble(parts[22]);
        double steer = Double.parseDouble(parts[23]);
        
        return new DataPoint(track, speedX, angle, position, targetSpeed, steer);
    }
    
    /**
     * Calcola distanza euclidea tra due punti
     */
    public double distanceTo(DataPoint other) {
        double sum = 0.0;
        
        // Distanza sui sensori track
        for (int i = 0; i < 19; i++) {
            double diff = this.trackSensors[i] - other.trackSensors[i];
            sum += diff * diff;
        }
        
        // Aggiungi differenza di velocità (pesata)
        double speedDiff = (this.speedX - other.speedX) * 0.1;
        sum += speedDiff * speedDiff;
        
        // Aggiungi differenza angolo (pesata)
        double angleDiff = (this.angleToTrackAxis - other.angleToTrackAxis) * 0.5;
        sum += angleDiff * angleDiff;
        
        return Math.sqrt(sum);
    }
}